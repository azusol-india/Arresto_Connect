

package app.com.arresto.arresto_connect.ui.modules.sensor.server;

import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver.RESULT_ASSET_DATA;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.EXTRA_DEVICE;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ResultReceiver;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.BuildConfig;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;

public class TempGattService extends Service {
    ArrayList<String> devices_name = new ArrayList<>();
    ArrayList<DiscoveredBluetoothDevice> allDevices = new ArrayList<>();
    ArrayList<ConnectionClass> connections = new ArrayList<>();
    boolean isGyro;

    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("service started ", "running");
        if (intent.getExtras() != null) {
                isGyro = intent.getExtras().getBoolean("isGyro", false);
                boolean needReconnect = intent.getExtras().getBoolean("needReconnect", false);
                if (intent.getExtras().containsKey("sendDevice")) {
                    boolean sendDevice = intent.getExtras().getBoolean("sendDevice");
                    if (sendDevice && allDevices.size() > 0)
                        broadCastCurrentDevices();
                }
                ResultReceiver resultReceiver = intent.getParcelableExtra("resultReceiver");
                if (intent.getExtras().containsKey(EXTRA_DEVICE)) {
                    DiscoveredBluetoothDevice device = intent.getExtras().getParcelable(EXTRA_DEVICE);
                    addDevice(device, resultReceiver, needReconnect);
                }
        } else {
            closeAll();
        }
        return START_NOT_STICKY;
    }

    private void broadCastCurrentDevices() {
        Intent intent = new Intent();
        intent.setAction("serviceDevices");
        Bundle args = new Bundle();
        args.putParcelableArrayList("devices", allDevices);
        intent.putExtras(args);
        sendBroadcast(intent);
    }

    public void addDevice(@Nullable DiscoveredBluetoothDevice device, ResultReceiver resultReceiver, boolean needReconnect) {
        if (!devices_name.contains(device.getName())) {
            devices_name.add(device.getName());
            allDevices.add(device);
            ConnectionClass connectionClass = new ConnectionClass(device, needReconnect, isGyro, resultReceiver);
            connections.add(connectionClass);
            if (device.getAssetName() == null && device.getAssetImage() == null)
                getServerData(device.getName(), resultReceiver, connectionClass);

            Log.e("new connection started ", "" + devices_name);
        } else {
            ConnectionClass connectionClass = connections.get(devices_name.indexOf(device.getName()));
            connectionClass.updateDevice(device);
            connectionClass.needReconnect = needReconnect;
            connectionClass.isGyro = isGyro;
            connectionClass.updateReceiver(resultReceiver);
            if (!needReconnect && connectionClass.getSensorInfo() != null) {
                Bundle bdl = new Bundle();
                bdl.putSerializable("fallCount", connectionClass.getSensorInfo());
                resultReceiver.send(RESULT_ASSET_DATA, bdl);
            }
            getServerData(device.getName(), resultReceiver, connectionClass);
            Log.e("old connection started ", "" + devices_name);
        }
    }

    public void closeAll() {
        Log.e("closeAll = ", "run now ");
        for (ConnectionClass connectionClass : connections) {
            connectionClass.is_rebooting = false;
            connectionClass.closeConnection();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        printMessage("service = destroyed ");
        closeAll();
    }


    private void getServerData(String name, ResultReceiver resultReceiver, ConnectionClass connectionClass) {
        if (isNetworkAvailable(this)) {
            String url = All_Api.getSensorVibrations + client_id + "&sensor_id=" + name;
//            String url = All_Api.getSensorVibrations + client_id + "&sensor_id=KARE-4430A2";
            url = url.replace(" ", "%20");
            new NetworkRequest().make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status_code").equals("200")) {
                                FallCountModel fallCountModel = AppUtils.getGson().fromJson(jsonObject.getString("data"), FallCountModel.class);
                                ArrayList<String> containingThresholds = new ArrayList<>();
                                if (fallCountModel != null && fallCountModel.getFirmware_info().getThresholds() != null) {
                                    for (Constant_model threshold : fallCountModel.getFirmware_info().getThresholds()) {
                                        containingThresholds.add(threshold.getId());
                                    }
                                }
                                fallCountModel.getFirmware_info().setContainingThresholds(containingThresholds);
                                Bundle bdl = new Bundle();
                                bdl.putSerializable("fallCount", fallCountModel);
                                resultReceiver.send(RESULT_ASSET_DATA, bdl);
                                connectionClass.setSensorInfo(fallCountModel);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("error", "" + error);
                }
            });
        } else {
//            show_snak(activity, getResString("lbl_network_alert"));
        }
    }

    private void printMessage(String msg) {
        if (BuildConfig.DEBUG)
            Log.e("TempGattService ", msg);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean stopService(Intent name) {
        Log.e("Service stop called", "run now====");
        closeAll();
        return super.stopService(name);
    }

}
