package app.com.arresto.arresto_connect.ui.modules.sensor.server;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import app.com.arresto.arresto_connect.BuildConfig;

import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_INDICATE;
import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.BATTERY_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.BATTERY_SERVICE_UUID;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.CCCD;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.REBOOT_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.SETTING_SERVICE;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.THRESHOLD_DATA_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.THRESHOLD_MODE_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.UART_RX_SERVICE;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.UART_SERVICE;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.UART_TX_CHAR;

@SuppressLint("MissingPermission")
public class CalibrationService extends Service {
    private final static String TAG = CalibrationService.class.getSimpleName();
    public final static String ACTION_GATT_CONNECTED = "GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED = "GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED = "GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DESCRIPTOR_WRITE = "DESCRIPTOR_WRITE";
    public final static String ACTION_CHARACTERISTIC_WRITE = "CHARACTERISTIC_WRITE";
    public final static String ACTION_DATA_AVAILABLE = "DATA_AVAILABLE";
    public final static String NOTIFICATION_DATA = "NOTIFICATION_DATA";
    public final static String DEVICE_DOES_NOT_SUPPORT_UART = "DEVICE_DOES_NOT_SUPPORT_UART";
    public final static String EXTRA_DATA = "EXTRA_DATA";

    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_CONNECTED = 2;

    public BluetoothGatt mBluetoothGatt;
    public int mConnectionState = STATE_DISCONNECTED;


    public String mean_str = "";
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss:SSS");
    public double minValue = 0, maxValue = 0;
    public ArrayList<MeanModel> meanArray = new ArrayList<>();
    boolean isWrite;

    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                printMessage("Connected to GATT server.");
                // Attempts to discover services after successful connection.
                try {
                    Thread.sleep(600);
                    printMessage("Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    printMessage("Thread.sleep error :" + e.getMessage());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                printMessage("Disconnected from GATT server.");
                mConnectionState = STATE_DISCONNECTED;
                if (stopCall) {
                    close();
                } else if (isWrite) {
                    intentAction = ACTION_GATT_DISCONNECTED;
                    broadcastUpdate(intentAction);
                    isWrite = false;
                }
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                printMessage("mBluetoothGatt = " + mBluetoothGatt);
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                printMessage("onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                if (characteristic.getUuid().toString().equals(BATTERY_CHAR)) {
                    printMessage("Battery level fetch");
//                    broadcastBattery(BATTERY_DATA, bt);
                    getUART();
//                    writeThreshold();
                }
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            printMessage(" onCharacteristicWrite " + status + "\n" + characteristic.getUuid().toString());
            broadcastUpdate(ACTION_CHARACTERISTIC_WRITE, characteristic);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                isWrite = true;
            } else if (status == BluetoothGatt.GATT_INVALID_ATTRIBUTE_LENGTH) {
                printMessage("onCharacteristicWrite() A write operation failed due to invalid attribute length");
            } else {
                printMessage("onCharacteristicWrite() A write operation failed. Status = " + status);
                gatt.close();
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                printMessage(" onDescriptorWrite " + descriptor.getUuid());
                broadcastUpdate(ACTION_DESCRIPTOR_WRITE);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            try {
                String dt = new String(characteristic.getValue(), "UTF-8");
                printMessage(dt);
                if (dt != null && !dt.equals("")) {
                    dt = dt.replace("Mean=", "");
                    double actualValue = Double.parseDouble(dt);
                    String timeStamp = sdf.format(new Date());
                    MeanModel meanModel = new MeanModel();
                    meanModel.setTime_stamp(timeStamp);
                    meanModel.setMean_value(actualValue);
                    dt = timeStamp + "   " + dt;
                    mean_str = dt + "\n" + mean_str;
                    printMessage(" onCharacteristicChanged value: " + actualValue);
                    meanArray.add(meanModel);
                    if (actualValue > maxValue)
                        maxValue = actualValue;
                    if (actualValue < minValue || minValue == 0)
                        minValue = actualValue;
                }
                broadcastUpdate(NOTIFICATION_DATA);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }



    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        ArrayList<Parcelable> parcelableArr = new ArrayList<>();
        parcelableArr.add(characteristic);
        intent.putParcelableArrayListExtra(EXTRA_DATA, parcelableArr);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */

    public boolean connect(final BluetoothDevice device) {
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        device.createBond();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback, TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        }
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    public void getbattery() {
        BluetoothGattService batteryService = mBluetoothGatt.getService(UUID.fromString(BATTERY_SERVICE_UUID));
        if (batteryService == null) {
            getUART();
            printMessage("Battery service not found!");
            return;
        }
        BluetoothGattCharacteristic batteryLevel = batteryService.getCharacteristic(UUID.fromString(BATTERY_CHAR));
        if (batteryLevel == null) {
            printMessage("Battery level not found!");
            return;
        }
        mBluetoothGatt.readCharacteristic(batteryLevel);
    }

    BluetoothGattCharacteristic TxChar;

    public boolean isUARTEnabled() {
        BluetoothGattService RxService = mBluetoothGatt.getService(UUID.fromString(UART_SERVICE));
        if (RxService == null)
            return false;
        else
            return true;
    }

    public void getUART() {
        if (!isUARTEnabled()) {
            printMessage("RxService  not found! ");
            writeClibrationService();
            return;
        }
        BluetoothGattService RxService = mBluetoothGatt.getService(UUID.fromString(UART_SERVICE));
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(UUID.fromString(UART_RX_SERVICE));
        if (RxChar == null) {
            printMessage("RxChar  not found!");
            return;
        }
        printMessage("UART service found is " + RxChar);
        setTXNotification(true);
    }

    public void setTXNotification(boolean enable) {
        BluetoothGattService RxService = mBluetoothGatt.getService(UUID.fromString(UART_SERVICE));
        if (RxService == null) {
            Log.e("Rx service 00", " not found! ");
            return;
        }
        TxChar = RxService.getCharacteristic(UUID.fromString(UART_TX_CHAR));
        if (TxChar == null) {
            printMessage("Rx service 00 not found! 22222");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(TxChar, enable);
        BluetoothGattDescriptor descriptor = TxChar.getDescriptor(UUID.fromString(CCCD));

        int properties = TxChar.getProperties();
        byte[] notyfy_value;
        if ((properties & PROPERTY_NOTIFY) > 0) {
            notyfy_value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE;
        } else if ((properties & PROPERTY_INDICATE) > 0) {
            notyfy_value = BluetoothGattDescriptor.ENABLE_INDICATION_VALUE;
        } else {
            printMessage(String.format("ERROR: Characteristic %s does not have notify or indicate property", TxChar.getUuid()));
            return;
        }
        final byte[] finalValue = enable ? notyfy_value : BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE;
        descriptor.setValue(finalValue);
        mBluetoothGatt.writeDescriptor(descriptor);
    }

    public void writeClibrationService() {
        BluetoothGattService callSer = mBluetoothGatt.getService(UUID.fromString(SETTING_SERVICE));
        if (callSer == null) {
            printMessage("Calibration service not found! ");
            return;
        }
        BluetoothGattCharacteristic TxChar = callSer.getCharacteristic(UUID.fromString(THRESHOLD_MODE_CHAR));
        if (TxChar == null) {
            printMessage("Calibration Characteristic service not found! ");
            return;
        }
        byte[] value = new byte[1];
        value[0] = (byte) (01 & 0xFF);
        TxChar.setValue(value);
        mBluetoothGatt.writeCharacteristic(TxChar);
//        isWriteDone = true;
        printMessage(" writeClibrationService run");
    }

    public void writeRebootService() {
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
        byte[] strBytes = txt.getBytes();
        TxChar.setValue(strBytes);
        mBluetoothGatt.writeCharacteristic(TxChar);
//        isWriteDone = true;
        printMessage(" write Threshold Service run");
    }

    boolean stopCall;

    public void stopCalibration() {
        stopCall = true;
        if (mBluetoothGatt != null) {
            printMessage(" stopCalibration");
            if (mConnectionState == STATE_CONNECTED && isUARTEnabled()) {
                writeClibrationService();
            } else {
                close();
            }
        }
    }

    public void setNotifyEnable(boolean enable) {
        if (mBluetoothGatt != null) {
            if (mConnectionState == STATE_DISCONNECTED) {
                mBluetoothGatt.connect();
            } else if (mConnectionState == STATE_CONNECTED) {
                setTXNotification(enable);
            }
        }
        printMessage(" setNotifyEnable  " + enable);
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.disconnect();
        // mBluetoothGatt.close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        printMessage("mBluetoothGatt closed");
        mBluetoothGatt.disconnect();
        mBluetoothGatt.close();
        stopCall = false;
    }


    private void printMessage(String msg) {
        if (BuildConfig.DEBUG)
            Log.e(TAG, msg);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //TODO do something useful
        printMessage("onStartCommand run");
        Toast.makeText(this, "service starting", Toast.LENGTH_SHORT).show();
        return Service.START_NOT_STICKY;
    }

    public class LocalBinder extends Binder {
        public CalibrationService getService() {
            return CalibrationService.this;
        }
    }
}