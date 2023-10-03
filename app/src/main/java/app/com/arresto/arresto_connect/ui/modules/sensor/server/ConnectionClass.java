package app.com.arresto.arresto_connect.ui.modules.sensor.server;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static app.com.arresto.arresto_connect.network.All_Api.post_SensorNotification;
import static app.com.arresto.arresto_connect.network.All_Api.post_SensorVibrations;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver.RESULT_BATTERY;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver.RESULT_NOTIFYDATA;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver.RESULT_STATE;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver.RESULT_STRING;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver.RESULT_THRESHOLDS;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver.RESULT_VERSION;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.BATTERY_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.BATTERY_SERVICE_UUID;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.DATA_HISTORY_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.DATA_LIVE_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.DATA_SERVICE;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.HOOK_MODE_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.RAW_DATA_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.REBOOT_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.SETTING_SERVICE;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.THRESHOLD_DATA_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.THRESHOLD_MODE_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.VERSION_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.VERSION_SERVICE;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

import com.flir.thermalsdk.androidsdk.BuildConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;

//@SuppressLint("MissingPermission")
public class ConnectionClass {
    Context context;
    DiscoveredBluetoothDevice device;
    ResultReceiver resultReceiver;
    ArrayList<SensorVibrationModel> fallModels;
    FallCountModel sensorInfo;
    String dv_threshold_data;
    boolean needReconnect, isGyro;
    public static final String TAG = AppController.class.getSimpleName();

    BluetoothManager btm = (BluetoothManager) AppController.getInstance().getSystemService(Context.BLUETOOTH_SERVICE);

    public ConnectionClass(DiscoveredBluetoothDevice device, boolean needReconnect, boolean isGyro, ResultReceiver resultReceiver) {
        sensor_date_time.setTimeZone(TimeZone.getTimeZone("GMT"));
        this.device = device;
        this.resultReceiver = resultReceiver;
        this.needReconnect = needReconnect;
        this.isGyro = isGyro;
        fallModels = new ArrayList<>();
        connect(device.getDevice());
    }

    public void updateReceiver(ResultReceiver resultReceiver) {
        this.resultReceiver = resultReceiver;
//        setBootingTimer();
    }

    public void updateDevice(DiscoveredBluetoothDevice device) {
        dv_threshold_data = "";
        this.device = device;
    }

    public void setSensorInfo(FallCountModel sensorInfo) {
        this.sensorInfo = sensorInfo;
        if (!needReconnect)
            matchServerThresholds();
//        else if (sensorInfo.getAsset_type().equalsIgnoreCase("hook")) {
//            hookMode("1");
//        }
    }

    public void hookMode(String is_enable) {
        Log.d(TAG, "addDevice: "+is_enable);
        BluetoothGattService callSer = mBluetoothGatt.getService(UUID.fromString(SETTING_SERVICE));
        if (callSer == null) {
            printMessage("HOOK_MODE_CHAR Threshold service not found!");
            return;
        }
//        printServicesCharacteristics(callSer);
        BluetoothGattCharacteristic TxChar = callSer.getCharacteristic(UUID.fromString(HOOK_MODE_CHAR));
        if (TxChar == null) {
            printMessage("Threshold Characteristic HOOK_MODE_CHAR not found! ");
            return;
        }
        printMessage("Characteristic writing text= HOOK_MODE_CHAR");
        byte[] strBytes = ("SET_HM:" + is_enable).getBytes();
        TxChar.setValue(strBytes);
        mBluetoothGatt.writeCharacteristic(TxChar);
        postHookData(hookEnabled);
        hookEnabled = is_enable;
        printMessage("enableMode write Threshold Service run");
    }

    public FallCountModel getSensorInfo() {
        return sensorInfo;
    }

    boolean notificationEnabled, disableUART;
    String dv_status;
    String hookEnabled = "0";
    public BluetoothGatt mBluetoothGatt;
    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                dv_status = "Connected";
                printMessage("Connected to GATT server.");
                sendReceiverMessage(RESULT_STATE, RESULT_STRING, "Connected");
                postNotificationData(device.getName(), "Connected");//TODO CHANGE THIS IS COMMENTED
                postSensorData(device.getName());
                // Attempts to discover services after successful connection.
                try {
                    Thread.sleep(200);
                    printMessage("Start service discovery:" + mBluetoothGatt.discoverServices());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    printMessage("Start service discovery error:" + e.getMessage());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                printMessage("Disconnected from GATT server.");
                dv_status = "Disconnected";
                sendReceiverMessage(RESULT_STATE, RESULT_STRING, "Disconnected");
                if (!closeCalled && (notificationEnabled || disableUART || device.getBatteryPercentage() == -1)) {
                    mBluetoothGatt.connect();
                } else {
                    postNotificationData(device.getName(), "Disconnected");
                    postSensorData(device.getName());
                    if (!closeCalled)
                        closeConnection();
                    else closeCalled = false;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            printMessage("onServicesDiscovered received: " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (!notificationEnabled) {
                    printMessage("mBluetoothGatt = " + mBluetoothGatt);
                    getBattery();
                } else {
                    setNotification(DATA_HISTORY_CHAR, true);//get History
                }
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            printMessage("onCharacteristicRead Battery level fetch ");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getUuid().toString().equals(BATTERY_CHAR)) {
                    int battery = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    device.setBatteryPercentage(battery);
                    printMessage("Battery level fetch " + battery);
                    sendReceiverMessage(RESULT_BATTERY, "batPer", device.getBatteryPercentage());
                    if (device.getVersionName().equals("")) {
                        getVersion();
                    } else {
                        if (!notificationEnabled) {
                            sendReceiverMessage(RESULT_VERSION, "version", device.getVersionName());
                            setNotification(DATA_HISTORY_CHAR, true);
                        }
                    }
                } else if (characteristic.getUuid().toString().equals(VERSION_CHAR)) {
                    String version = new String(characteristic.getValue(), StandardCharsets.UTF_8);
                    device.setVersionName(version);
                    sendReceiverMessage(RESULT_VERSION, "version", version);
                    if (!notificationEnabled) {
                        setNotification(DATA_HISTORY_CHAR, true); //get History
                    }
                } else if (characteristic.getUuid().toString().equals(THRESHOLD_DATA_CHAR)) {
                    dv_threshold_data = new String(characteristic.getValue(), StandardCharsets.UTF_8);
                    printMessage("threshold_data fetched " + dv_threshold_data);
                    is_rebooting = false;
                    sendReceiverMessage(RESULT_STATE, RESULT_STRING, "");
                    if (!dv_threshold_data.contains("WT") && !needReconnect) {
                        rebootDevice();
                    } else {
                        dv_threshold_data = dv_threshold_data.replaceAll("[*#]", "");
                        if (dv_threshold_data.startsWith(","))
                            dv_threshold_data = dv_threshold_data.replaceFirst(",", "");
                        device.setThresholds_data(dv_threshold_data);
                        sendReceiverMessage(RESULT_THRESHOLDS, "thresholds", dv_threshold_data);
                        if (!needReconnect) {
                            matchServerThresholds();
                        }
                    }
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            printMessage(" onCharacteristicWrite status " + characteristic.getUuid() + "status " + status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getUuid().toString().equals(THRESHOLD_MODE_CHAR))
                    disableUART = true;
                else if (characteristic.getUuid().toString().equals(THRESHOLD_DATA_CHAR)) {
                    if (dvthNames.size() > 0) dvthNames.remove(dvthNames.size() - 1);
                    else if (unmatchedThresholds.size() > 0)
                        unmatchedThresholds.remove(unmatchedThresholds.size() - 1);
                    try {
                        Thread.sleep(200);
                        checkWritingThreshold();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        printMessage("onCharacteristicWrite : " + e.getMessage());
                    }
                } else if (characteristic.getUuid().toString().equals(REBOOT_CHAR)) {
                    is_rebooting = true;
                    sendReceiverMessage(RESULT_STATE, RESULT_STRING, "booting");
                }
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            try {
                Bundle bdl = new Bundle();
                String data = new String(characteristic.getValue(), "UTF-8");
                if (data != null && !data.equals("")) {
                    data = data.replaceAll("[*#]", "");
                    if (isGyro) {
                        bdl.putSerializable(RESULT_STRING, data);
                        resultReceiver.send(RESULT_NOTIFYDATA, bdl);
                    } else {
                        printMessage(data);
                        try {
                            JSONObject notiData = new JSONObject(data);
                            SensorVibrationModel fallModel = new SensorVibrationModel();
                            if (notiData.has("time")) {
                                Date date_time = new Date(Long.parseLong(notiData.getString("time")) * 1000L);
                                String date = sensor_date_time.format(date_time);
                                fallModel.setDate(date);
                            } else if (notiData.has("ct")) {
                                Date date_time = new Date(Long.parseLong(notiData.getString("ct")) * 1000L);
                                String date = sensor_date_time.format(date_time);
                                fallModel.setDate(date);
                            }
                            if (notiData.has("dc")) {
                                fallModel.setHook_mode(notiData.getInt("dc"));
                                fallModels.add(fallModel);
                                if (fallModel.getHook_mode() == 0) {
                                    postNotificationData(device.getName(), "HD");
                                } else if (fallModel.getHook_mode() == 1) {
                                    postNotificationData(device.getName(), "HC");
                                }
                            } else if (notiData.has("alert")) {
                                fallModel.setVibrationType(notiData.getString("alert"));
                                fallModels.add(fallModel);
                            } else if (sensorInfo != null && sensorInfo.getFirmware_info().getWinch().equals("1") && notiData.has("acl")) {
                                fallModel.setClockWiseCount(notiData.getString("acl"));
                                fallModel.setAntiClockCount(notiData.getString("acl"));
                                if (notiData.getString("acl").equals("1")) {
                                    fallModel.setVibrationType("acl");
                                    fallModels.add(fallModel);
                                } else if (notiData.getString("acl").equals("1")) {
                                    fallModel.setVibrationType("acl");
                                    fallModels.add(fallModel);
                                }
                            } else {
                                return;
                            }
                            bdl.putSerializable(RESULT_STRING, fallModel);
                            resultReceiver.send(RESULT_NOTIFYDATA, bdl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            printMessage("JSONException==== " + e.getMessage());
                        }
                    }
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }


        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (descriptor.getCharacteristic().getUuid().toString().equals(DATA_HISTORY_CHAR))
                    setNotification(DATA_LIVE_CHAR, true);
                else if (!notificationEnabled)
                    readThresholdService();
                notificationEnabled = true;
            } else {
                printMessage(" onDescriptorWrite " + status + " -D- " + descriptor.getUuid());
            }
        }
    };

    public SimpleDateFormat sensor_date_time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    public void sendReceiverMessage(int result_code, String key, Object value) {
        Bundle bdl = new Bundle();
        if (value instanceof String)
            bdl.putString(key, value.toString());
        else if (value instanceof Integer)
            bdl.putInt(key, (int) value);
        else if (value instanceof Boolean)
            bdl.putBoolean(key, (Boolean) value);
        resultReceiver.send(result_code, bdl);
    }


    public boolean connect(final BluetoothDevice device) {
        if (device == null) {
            Log.e("connect", "Device not found.  Unable to connect.");
            return false;
        }//todo change autoConnect true from false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(context, true, mGattCallback, TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(context, true, mGattCallback);
        }
        return true;
    }


    boolean closeCalled;

    public void closeConnection() {
        closeCalled = true;
        if (hookEnabled.equals("1")) {
            hookMode("0");
        }
        if (notificationEnabled) {
            setNotification(DATA_LIVE_CHAR, false);
        }
        if (mBluetoothGatt != null) {
            printMessage("Disconnect call.");
            mBluetoothGatt.disconnect();
        }
        notificationEnabled = false;
    }

    public void getBattery() {
        if (mBluetoothGatt == null)
            return;
        BluetoothGattService batteryService = mBluetoothGatt.getService(UUID.fromString(BATTERY_SERVICE_UUID));
        if (batteryService == null) {
            printMessage("Battery service not found!");
            getVersion();
            return;
        }
        BluetoothGattCharacteristic batteryLevel = batteryService.getCharacteristic(UUID.fromString(BATTERY_CHAR));
        if (batteryLevel == null) {
            printMessage("Battery level not found!");
            getVersion();
            return;
        }
        mBluetoothGatt.readCharacteristic(batteryLevel);
    }

    public void printServices() {
        for (BluetoothGattService service : mBluetoothGatt.getServices()) {
            printMessage("service=== " + service.getUuid());
        }

    }

    public void printServicesCharacteristics(BluetoothGattService service) {
        for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
            printMessage("characteristic=== " + characteristic.getUuid());
        }

    }

    public void getVersion() {
        BluetoothGattService versionService = mBluetoothGatt.getService(UUID.fromString(VERSION_SERVICE));
        if (versionService == null) {
            printMessage("Version service not found!");

            setNotification(DATA_HISTORY_CHAR, true);//get History
            return;
        }
        BluetoothGattCharacteristic versionLevel = versionService.getCharacteristic(UUID.fromString(VERSION_CHAR));
        if (versionLevel == null) {
            printMessage("Version level not found!");
            setNotification(DATA_HISTORY_CHAR, true); //get History
            return;
        }
        mBluetoothGatt.readCharacteristic(versionLevel);
    }

    public void readThresholdService() {
        BluetoothGattService dataService = mBluetoothGatt.getService(UUID.fromString(SETTING_SERVICE));
        if (dataService == null) {
            printMessage("Threshold service not found!");
            return;
        }

        BluetoothGattCharacteristic TxChar = dataService.getCharacteristic(UUID.fromString(THRESHOLD_DATA_CHAR));
        if (TxChar == null) {
            printMessage("Threshold Characteristic not found!");
            return;
        }
        mBluetoothGatt.readCharacteristic(TxChar);
    }

    public boolean setNotification(String CharUuid, boolean enable) {
        if (!needReconnect) {
            readThresholdService();
            return false;
        }
        if (isGyro)
            CharUuid = RAW_DATA_CHAR;
        printMessage("setNotification for " + CharUuid);
        BluetoothGattService dataService = mBluetoothGatt.getService(UUID.fromString(DATA_SERVICE));
        if (dataService == null) {
            printMessage("service not found for uuid: " + DATA_SERVICE);
            return false;
        }
        BluetoothGattCharacteristic TxChar = dataService.getCharacteristic(UUID.fromString(CharUuid));
        if (TxChar == null) {
            printMessage("Characteristic not found for uuid: " + CharUuid);
            return false;
        }
        mBluetoothGatt.setCharacteristicNotification(TxChar, enable);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(UUID.fromString(SensorConstants.CCCD));
        int properties = TxChar.getProperties();
        byte[] notyfy_value;
        if ((properties & PROPERTY_INDICATE) > 0) {
            notyfy_value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
        } else if ((properties & PROPERTY_NOTIFY) > 0) {
            notyfy_value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
        } else {
            printMessage(String.format("ERROR: Characteristic %s does not have notify or indicate property", TxChar.getUuid()));
            return false;
        }
        final byte[] finalValue = enable ? notyfy_value : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        descriptor.setValue(finalValue);
        return mBluetoothGatt.writeDescriptor(descriptor);
    }





    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        printMessage("mBluetoothGatt closed");
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
    }

    /*    writing Threshold Code Here  */

    ArrayList<Constant_model> unmatchedThresholds;
    ArrayList<String> dvthNames, serverthNames;

    private void matchServerThresholds() {
        printMessage("sensorInfo :  " + sensorInfo);
        if (sensorInfo != null && dv_threshold_data != null && dv_threshold_data.contains("T")) {
            unmatchedThresholds = new ArrayList<>();
            serverthNames = new ArrayList<>();
            dvthNames = new ArrayList<>();
            List<String> dv_thresholds = Arrays.asList(dv_threshold_data.split(","));
            if (sensorInfo.getFirmware_info().getThresholds() != null) {
                String dv_threshold = dv_threshold_data.replace("T", "");
                if (sensorInfo.getFirmware_info().getThresholds().size() > 0) {
                    for (Constant_model serverThreshold : sensorInfo.getFirmware_info().getThresholds()) {
                        String thName = getThresholdName(serverThreshold.getId(), false);
                        serverthNames.add(thName);
                        for (String thresholdItem : dv_thresholds) {
                            String[] device_threshold_value = thresholdItem.split(":");
                            if (device_threshold_value != null && device_threshold_value.length > 1) {
                                if (device_threshold_value[0].equalsIgnoreCase(thName)) {
                                    if (!device_threshold_value[0].equals(serverThreshold.getName())) {
                                        unmatchedThresholds.add(serverThreshold);
                                    }
                                    if (serverthNames.contains(device_threshold_value[0]) || dvthNames.contains(device_threshold_value[0])) {
                                        dvthNames.remove(device_threshold_value[0]);
                                    }
                                } else if (!dvthNames.contains(device_threshold_value[0]) && !serverthNames.contains(device_threshold_value[0])) {
                                    dvthNames.add(device_threshold_value[0]);
                                }
                            }
                        }
                        if (!dv_threshold.contains(thName + ":")) {
                            unmatchedThresholds.add(serverThreshold);
                        }
                    }
                } else {
                    for (String thresholdItem : dv_thresholds) {
                        String[] device_threshold_value = thresholdItem.split(":");
                        dvthNames.add(device_threshold_value[1]);
                    }
                }
                dvthNames.remove("T");
                printMessage("unmatchedThresholds===  " + unmatchedThresholds);
                printMessage("dvthNames===  " + dvthNames);
            }
            if (dvthNames.size() > 0) {
                writeCleanTH(dvthNames.get(dvthNames.size() - 1));
            } else if (unmatchedThresholds.size() > 0) {
                Constant_model model = unmatchedThresholds.get(unmatchedThresholds.size() - 1);
                writeThreshold(getThresholdName(model.getId(), true) + model.getName());
            } else {
                closeConnection();
            }
        }
    }

    public void checkWritingThreshold() {
        if (unmatchedThresholds != null && dvthNames != null) {
            if (dvthNames.size() > 0) {
                String txt = dvthNames.get(dvthNames.size() - 1);
                writeCleanTH(txt);
            } else if (unmatchedThresholds.size() > 0) {
                Constant_model model = unmatchedThresholds.get(unmatchedThresholds.size() - 1);
                writeThreshold(getThresholdName(model.getId(), true) + model.getName());
            } else {
                rebootDevice();
            }
        }
    }

    void writeCleanTH(String txt) {
        String writing_th;
        if (txt.contains("_1")) {
            if (txt.contains("T"))
                txt = txt.replace("T", "TS");
            writing_th = txt.replace("_1", "_0:0");
        } else {
            if (txt.equals("TA"))
                txt = txt.replace("TA", "TS");
            writing_th = "SET_" + txt + "_0:0";
        }
        if (!writing_th.equals("")) {
            printMessage("writing_th === " + writing_th);
            writeThreshold(writing_th);
        }
    }

    public void writeThreshold(String txt) {
        BluetoothGattService callSer = mBluetoothGatt.getService(UUID.fromString(SETTING_SERVICE));
        if (callSer == null) {
            printMessage("Threshold service not found! ");
            return;
        }
        BluetoothGattCharacteristic TxChar = callSer.getCharacteristic(UUID.fromString(THRESHOLD_DATA_CHAR));
        if (TxChar == null) {
            printMessage("Threshold Characteristic service not found! ");
            return;
        }
        printMessage("Characteristic writing text= " + txt);
        byte[] strBytes = txt.getBytes();
        TxChar.setValue(strBytes);
        mBluetoothGatt.writeCharacteristic(TxChar);
//        isWriteDone = true;
        printMessage(" write Threshold Service run");
    }

    boolean is_rebooting;

    public void rebootDevice() {
        BluetoothGattService callSer = mBluetoothGatt.getService(UUID.fromString(SETTING_SERVICE));
        if (callSer == null) {
            printMessage("Reboot service not found! ");
            return;
        }
        BluetoothGattCharacteristic TxChar = callSer.getCharacteristic(UUID.fromString(REBOOT_CHAR));
        if (TxChar == null) {
            printMessage("Reboot Characteristic service not found! ");
            return;
        }
        byte[] value = new byte[1];
        value[0] = (byte) (01 & 0xFF);
        TxChar.setValue(value);
        mBluetoothGatt.writeCharacteristic(TxChar);
        printMessage(" writeRebootService run");
    }

    private String getThresholdName(String threshold_name, boolean isWriting) {
        if (isWriting) {
            switch (threshold_name) {
                case "Threshold 1":
                    return "SET_FA_1:";
                case "Threshold 2":
                    return "SET_TS_1:";
                case "Threshold 3":
                    return "SET_FF_1:";
                case "Threshold 4":
                    return "SET_TH1_1:";
                case "Threshold 5":
                    return "SET_TH2_1:";
                case "Threshold 6":
                    return "SET_TH3_1:";
                default:
                    return "SET_FA_1:";
            }
        } else {
            switch (threshold_name) {
                case "Threshold 1":
                    return "FA";
                case "Threshold 2":
                    return "T";
                case "Threshold 3":
                    return "FF";
                case "Threshold 4":
                    return "TH1";
                case "Threshold 5":
                    return "TH2";
                case "Threshold 6":
                    return "TH3";
                default:
                    return "";
            }
        }
    }


    private void printMessage(String msg) {
        if (BuildConfig.DEBUG)
            Log.e("Connection Class: ", msg);
    }


    // Network call here

    private void postHookData(String hookEnabled) {
        SensorVibrationModel fallModel = new SensorVibrationModel();
        fallModel.setDate(BaseActivity.server_date_time.format(new Date()));
        if (hookEnabled.equals("0")) {
            fallModel.setVibrationType("stop");
            postNotificationData(device.getName(), "start");
        } else {
            fallModel.setVibrationType("start");
            postNotificationData(device.getName(),"stop" );
        }
        fallModels.add(fallModel);
//        postSensorData(device.getName());
    }

    String lastStatus = "";

    private void postNotificationData(String sensor_id, String status) {
        JSONObject object = new JSONObject();
        try {
            object.put("client_id", Static_values.client_id);
            object.put("user_id", Static_values.user_id);
            object.put("status", status);
            object.put("sensor_id", sensor_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkRequest().make_contentpost_request(post_SensorNotification, object, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);
                } catch (JSONException e) {
                    printMessage("JSONException is  " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
            }
        });
    }

    private void postSensorData(String sensor_id) {
        if (fallModels.size() < 1 && lastStatus.equals(dv_status))
            return;
        if (fallModels.size() < 1 && lastStatus.equals(""))
            return;
        lastStatus = dv_status;
        JSONObject object = new JSONObject();
        try {
            object.put("client_id", Static_values.client_id);
            object.put("user_id", Static_values.user_id);
            object.put("status", dv_status);
            object.put("sensor_id", sensor_id);
            JSONArray jsonArray = new JSONArray();
            object.put("data", jsonArray);
            for (SensorVibrationModel fallModel : fallModels) {
                JSONObject item_obj = new JSONObject();
//                item_obj.put("timestamp", fallModel.getDate() + " " + fallModel.getTime());
                item_obj.put("timestamp", fallModel.getDate());
                if (fallModel.getVibrationType().equals("") && fallModel.getHook_mode() > -1) {
                    if (fallModel.getHook_mode() == 1)
                        item_obj.put("vibration_type", "HC");
                    else
                        item_obj.put("vibration_type", "HD");
                } else
                    item_obj.put("vibration_type", fallModel.getVibrationType());
                jsonArray.put(item_obj);
            }
            fallModels.clear();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkRequest().make_contentpost_request(post_SensorVibrations, object, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);
                } catch (JSONException e) {
                    printMessage("JSONException is  " + e.getMessage());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
            }
        });
    }
}