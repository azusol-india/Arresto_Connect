package app.com.arresto.arresto_connect.ui.modules.sensor.ble_lib;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.os.Build;

import androidx.annotation.NonNull;

@TargetApi(Build.VERSION_CODES.M)
/* package */ class BluetoothLeScannerImplMarshmallow extends BluetoothLeScannerImplLollipop {
	@NonNull
	@Override
	/* package */ android.bluetooth.le.ScanSettings toNativeScanSettings(@NonNull final BluetoothAdapter adapter,
																		 @NonNull final ScanSettings settings,
																		 final boolean exactCopy) {
		final android.bluetooth.le.ScanSettings.Builder builder =
				new android.bluetooth.le.ScanSettings.Builder();

		if (exactCopy || adapter.isOffloadedScanBatchingSupported() && settings.getUseHardwareBatchingIfSupported())
			builder.setReportDelay(settings.getReportDelayMillis());

		if (exactCopy || settings.getUseHardwareCallbackTypesIfSupported())
			builder.setCallbackType(settings.getCallbackType())
					.setMatchMode(settings.getMatchMode())
					.setNumOfMatches(settings.getNumOfMatches());

		builder.setScanMode(settings.getScanMode());

		return builder.build();
	}
}