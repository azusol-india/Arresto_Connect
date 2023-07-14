package app.com.arresto.arresto_connect.ui.modules.sensor.ble_lib;

import android.Manifest;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
class BluetoothLeScannerImplJB extends BluetoothLeScannerCompat {

	/**
	 * A map that stores {@link ScanCallbackWrapper}s for user's {@link ScanCallback}.
	 * Each wrapper keeps track of found and lost devices and allows to emulate batching.
	 */
	@NonNull private final Map<ScanCallback, ScanCallbackWrapper> wrappers = new HashMap<>();

	@Nullable private HandlerThread handlerThread;
	@Nullable private Handler powerSaveHandler;

	private long powerSaveRestInterval;
	private long powerSaveScanInterval;

	private final Runnable powerSaveSleepTask = new Runnable() {
		@Override
		@RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
		public void run() {
			final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter != null && powerSaveRestInterval > 0 && powerSaveScanInterval > 0) {
				adapter.stopLeScan(scanCallback);
				powerSaveHandler.postDelayed(powerSaveScanTask, powerSaveRestInterval);
			}
		}
	};

	private final Runnable powerSaveScanTask = new Runnable() {
		@Override
		@RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
		public void run() {
			final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
			if (adapter != null && powerSaveRestInterval > 0 && powerSaveScanInterval > 0) {
				adapter.startLeScan(scanCallback);
				powerSaveHandler.postDelayed(powerSaveSleepTask, powerSaveScanInterval);
			}
		}
	};

	/* package */ BluetoothLeScannerImplJB() {}

	@Override
	@RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
	/* package */ void startScanInternal(@NonNull final List<ScanFilter> filters,
										 @NonNull final ScanSettings settings,
										 @NonNull final ScanCallback callback,
										 @NonNull final Handler handler) {
		final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		boolean shouldStart;

		synchronized (wrappers) {
			if (wrappers.containsKey(callback)) {
				throw new IllegalArgumentException("scanner already started with given scanCallback");
			}
			final ScanCallbackWrapper wrapper = new ScanCallbackWrapper(
					false, false,
					filters, settings, callback, handler);
			shouldStart = wrappers.isEmpty();
			wrappers.put(callback, wrapper);
		}

		if (handlerThread == null) {
			handlerThread = new HandlerThread(BluetoothLeScannerImplJB.class.getName());
			handlerThread.start();
			powerSaveHandler = new Handler(handlerThread.getLooper());
		}

		setPowerSaveSettings();

		if (shouldStart) {
			adapter.startLeScan(scanCallback);
		}
	}

	@Override
	@RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
	/* package */ void stopScanInternal(@NonNull final ScanCallback callback) {
		final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

		boolean shouldStop;
		ScanCallbackWrapper wrapper;
		synchronized (wrappers) {
			wrapper = wrappers.remove(callback);
			shouldStop = wrappers.isEmpty();
		}
		if (wrapper == null)
			return;

		wrapper.close();

		setPowerSaveSettings();

		if (shouldStop) {
			adapter.stopLeScan(scanCallback);

			if (powerSaveHandler != null) {
				powerSaveHandler.removeCallbacksAndMessages(null);
			}

			if (handlerThread != null) {
				handlerThread.quitSafely();
				handlerThread = null;
			}
		}
	}

	@Override
	@RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
	/* package */ void startScanInternal(@NonNull final List<ScanFilter> filters,
										 @NonNull final ScanSettings settings,
										 @NonNull final Context context,
										 @NonNull final PendingIntent callbackIntent) {
		final Intent service = new Intent(context, ScannerService.class);
		service.putParcelableArrayListExtra(ScannerService.EXTRA_FILTERS, new ArrayList<>(filters));
		service.putExtra(ScannerService.EXTRA_SETTINGS, settings);
		service.putExtra(ScannerService.EXTRA_PENDING_INTENT, callbackIntent);
		service.putExtra(ScannerService.EXTRA_START, true);
		context.startService(service);
	}

	@Override
	@RequiresPermission(allOf = {Manifest.permission.BLUETOOTH_ADMIN, Manifest.permission.BLUETOOTH})
	/* package */ void stopScanInternal(@NonNull final Context context,
										@NonNull final PendingIntent callbackIntent) {
		final Intent service = new Intent(context, ScannerService.class);
		service.putExtra(ScannerService.EXTRA_PENDING_INTENT, callbackIntent);
		service.putExtra(ScannerService.EXTRA_START, false);
		context.startService(service);
	}

	@Override
	@RequiresPermission(Manifest.permission.BLUETOOTH)
	public void flushPendingScanResults(@NonNull final ScanCallback callback) {
		//noinspection ConstantConditions
		if (callback == null) {
			throw new IllegalArgumentException("callback cannot be null!");
		}

		ScanCallbackWrapper wrapper;
		synchronized (wrappers) {
			wrapper = wrappers.get(callback);
		}

		if (wrapper == null) {
			throw new IllegalArgumentException("callback not registered!");
		}

		wrapper.flushPendingScanResults();
	}

	/**
	 * This method goes through registered callbacks and sets the power rest and scan intervals
	 * to next lowest value.
	 */
	private void setPowerSaveSettings() {
		long minRest = Long.MAX_VALUE, minScan = Long.MAX_VALUE;
		synchronized (wrappers) {
			for (final ScanCallbackWrapper wrapper : wrappers.values()) {
				final ScanSettings settings = wrapper.scanSettings;
				if (settings.hasPowerSaveMode()) {
					if (minRest > settings.getPowerSaveRest()) {
						minRest = settings.getPowerSaveRest();
					}
					if (minScan > settings.getPowerSaveScan()) {
						minScan = settings.getPowerSaveScan();
					}
				}
			}
		}
		if (minRest < Long.MAX_VALUE && minScan < Long.MAX_VALUE) {
			powerSaveRestInterval = minRest;
			powerSaveScanInterval = minScan;
			if (powerSaveHandler != null) {
				powerSaveHandler.removeCallbacks(powerSaveScanTask);
				powerSaveHandler.removeCallbacks(powerSaveSleepTask);
				powerSaveHandler.postDelayed(powerSaveSleepTask, powerSaveScanInterval);
			}
		} else {
			powerSaveRestInterval = powerSaveScanInterval = 0;
			if (powerSaveHandler != null) {
				powerSaveHandler.removeCallbacks(powerSaveScanTask);
				powerSaveHandler.removeCallbacks(powerSaveSleepTask);
			}
		}
	}

	private final BluetoothAdapter.LeScanCallback scanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
			final ScanResult scanResult = new ScanResult(device, ScanRecord.parseFromBytes(scanRecord),
					rssi, SystemClock.elapsedRealtimeNanos());

			synchronized (wrappers) {
				final Collection<ScanCallbackWrapper> scanCallbackWrappers = wrappers.values();
				for (final ScanCallbackWrapper wrapper : scanCallbackWrappers) {
					wrapper.handler.post(new Runnable() {
						@Override
						public void run() {
							wrapper.handleScanResult(ScanSettings.CALLBACK_TYPE_ALL_MATCHES, scanResult);
						}
					});
				}
			}
		}
	};
}