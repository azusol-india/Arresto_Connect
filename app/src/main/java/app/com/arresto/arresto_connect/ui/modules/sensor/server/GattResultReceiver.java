package app.com.arresto.arresto_connect.ui.modules.sensor.server;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;

import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;

public class GattResultReceiver<T> extends ResultReceiver {
    public static final int RESULT_ASSET_DATA = 101;
    public static final int RESULT_BATTERY = 102;
    public static final int RESULT_VERSION = 103;
    public static final int RESULT_THRESHOLDS = 104;
    public static final int RESULT_NOTIFYDATA = 105;
    public static final int RESULT_STATE = 106;
    public static final String RESULT_STRING = "RESULT_STRING";
    private OnDeviceConnect listner;
    DiscoveredBluetoothDevice device;
    public GattResultReceiver(Handler handler, DiscoveredBluetoothDevice device,OnDeviceConnect listner) {
        super(handler);
        this.device=device;
        this.listner = listner;
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (listner != null) {
            if (resultCode == RESULT_ASSET_DATA) {
                listner.onAssetFetched((FallCountModel) resultData.getSerializable("fallCount"));
            } else  if (resultCode == RESULT_BATTERY) {
                listner.onBatteryFetched(resultData.getInt("batPer"));
            } else  if (resultCode == RESULT_VERSION) {
                listner.onVersionFetched(resultData.getString("version"));
            } else  if (resultCode == RESULT_THRESHOLDS) {
                device.setThresholds_data(resultData.getString("thresholds"));
            } else if (resultCode == RESULT_NOTIFYDATA) {
//                listner.notifyData(resultData.getString(RESULT_STRING));
                listner.notifyData(resultData.getSerializable(RESULT_STRING));
            }else if (resultCode == RESULT_STATE) {
                Log.e("RESULT_BOOT ", " is : " + resultData.getString(RESULT_STRING));
                listner.onStateChange(resultData.getString(RESULT_STRING));
            }
            }
        }
    }
