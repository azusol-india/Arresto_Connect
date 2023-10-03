/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package app.com.arresto.arresto_connect.ui.modules.sensor;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.List;

import app.com.arresto.arresto_connect.ui.modules.sensor.ble_lib.BluetoothLeScannerCompat;
import app.com.arresto.arresto_connect.ui.modules.sensor.ble_lib.ScanCallback;
import app.com.arresto.arresto_connect.ui.modules.sensor.ble_lib.ScanResult;
import app.com.arresto.arresto_connect.ui.modules.sensor.ble_lib.ScanSettings;

public class ScannerViewModel extends AndroidViewModel {
	private static final String PREFS_FILTER_NAME_REQUIRED = "filter_name";
	private static final String PREFS_FILTER_NEARBY_ONLY = "filter_nearby";

	/**
	 * MutableLiveData containing the list of devices.
	 */
	private final DevicesLiveData devicesLiveData;
	/**
	 * MutableLiveData containing the scanner state.
	 */
	private final ScannerStateLiveData scannerStateLiveData;

	private final SharedPreferences preferences;

	public DevicesLiveData getDevices() {
		return devicesLiveData;
	}

	public ScannerStateLiveData getScannerState() {
		return scannerStateLiveData;
	}

	public ScannerViewModel(final Application application) {
		super(application);
		preferences = PreferenceManager.getDefaultSharedPreferences(application);

		final boolean filerName = isNameFilterEnabled();
		final boolean filerNearbyOnly = isNearbyFilterEnabled();

		scannerStateLiveData = new ScannerStateLiveData(Utils.isBleEnabled(), Utils.isLocationEnabled(application));
		devicesLiveData = new DevicesLiveData(filerNearbyOnly,filerName);
		registerBroadcastReceivers(application);
	}

	@Override
	protected void onCleared() {
		super.onCleared();
		getApplication().unregisterReceiver(bluetoothStateBroadcastReceiver);

		if (Utils.isMarshmallowOrAbove()) {
			getApplication().unregisterReceiver(locationProviderChangedReceiver);
		}
	}

	public boolean isNameFilterEnabled() {
		return preferences.getBoolean(PREFS_FILTER_NAME_REQUIRED, true);
	}

	public boolean isNearbyFilterEnabled() {
		return preferences.getBoolean(PREFS_FILTER_NEARBY_ONLY, false);
	}

	public void refresh() {
		scannerStateLiveData.refresh();
	}

	/**
	 * Updates the device filter. Devices that once passed the filter will still be shown
	 * even if they move away from the phone, or change the advertising packet. This is to
	 * avoid removing devices from the list.
	 *
	 * @param uuidRequired if true, the list will display only devices with Led-Button Service UUID
	 *                     in the advertising packet.
	 */
	public void filterByName(final boolean uuidRequired) {
		Log.d("ScannerViewModel", "filterByName: "+uuidRequired);
		preferences.edit().putBoolean(PREFS_FILTER_NAME_REQUIRED, uuidRequired).apply();
		if (devicesLiveData.filterByName(uuidRequired))
			scannerStateLiveData.recordFound();
		else
			scannerStateLiveData.clearRecords();
	}

	public void filterByDistance(final boolean nearbyOnly) {
		preferences.edit().putBoolean(PREFS_FILTER_NEARBY_ONLY, nearbyOnly).apply();
		if (devicesLiveData.filterByDistance(nearbyOnly))
			scannerStateLiveData.recordFound();
		else
			scannerStateLiveData.clearRecords();
	}

	/**
	 * Start scanning for Bluetooth devices.
	 */
	public void startScan() {
		try {
			if (scannerStateLiveData.isScanning()) {
				return;
			}
			final ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).setReportDelay(100).setUseHardwareBatchingIfSupported(false).build();
			final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
			scanner.startScan(null, settings, scanCallback);
			scannerStateLiveData.scanningStarted();
		}catch (Exception e){
			Log.d("ScannerViewModel", "startScan:excep "+e.getMessage());
			stopScan();
//			Toast.makeText(conte,"scanner not available",Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Stop scanning for bluetooth devices.
	 */
	public void stopScan() {
		if (scannerStateLiveData.isScanning() && scannerStateLiveData.isBluetoothEnabled()) {
			final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
			scanner.stopScan(scanCallback);
			scannerStateLiveData.scanningStopped();
		}
	}

	private final ScanCallback scanCallback = new ScanCallback() {
		@Override
		public void onScanResult(final int callbackType, @NonNull final ScanResult result) {
			// This callback will be called only if the scan report delay is not set or is set to 0.
			// If the packet has been obtained while Location was disabled, mark Location as not required
			if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(getApplication()))
				Utils.markLocationNotRequired(getApplication());
			if (devicesLiveData.deviceDiscovered(result)) {
				devicesLiveData.applyFilter();
				scannerStateLiveData.recordFound();
			}
		}

		@Override
		public void onBatchScanResults(@NonNull final List<ScanResult> results) {
			// This callback will be called only if the report delay set above is greater then 0.

			// If the packet has been obtained while Location was disabled, mark Location as not required
			if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(getApplication()))
				Utils.markLocationNotRequired(getApplication());

			boolean atLeastOneMatchedFilter = false;
			for (final ScanResult result : results)
				atLeastOneMatchedFilter = devicesLiveData.deviceDiscovered(result) || atLeastOneMatchedFilter;
			if (atLeastOneMatchedFilter) {
				devicesLiveData.applyFilter();
				scannerStateLiveData.recordFound();
			}
		}

		@Override
		public void onScanFailed(final int errorCode) {
			// TODO This should be handled
			scannerStateLiveData.scanningStopped();
		}
	};

	/**
	 * Register for required broadcast receivers.
	 */
	private void registerBroadcastReceivers(@NonNull final Application application) {
		application.registerReceiver(bluetoothStateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
		if (Utils.isMarshmallowOrAbove()) {
			application.registerReceiver(locationProviderChangedReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
		}
	}

	/**
	 * Broadcast receiver to monitor the changes in the location provider.
	 */
	private final BroadcastReceiver locationProviderChangedReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final boolean enabled = Utils.isLocationEnabled(context);
			scannerStateLiveData.setLocationEnabled(enabled);
		}
	};

	/**
	 * Broadcast receiver to monitor the changes in the bluetooth adapter.
	 */
	private final BroadcastReceiver bluetoothStateBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(final Context context, final Intent intent) {
			final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
			final int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);

			switch (state) {
				case BluetoothAdapter.STATE_ON:
					scannerStateLiveData.bluetoothEnabled();
					break;
				case BluetoothAdapter.STATE_TURNING_OFF:
				case BluetoothAdapter.STATE_OFF:
					if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
						stopScan();
						scannerStateLiveData.bluetoothDisabled();
					}
					break;
			}
		}
	};
}
