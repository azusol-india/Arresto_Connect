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

import android.bluetooth.BluetoothDevice;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.ui.modules.sensor.ble_lib.ScanResult;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.FallCountModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.OnDeviceConnect;

public class DiscoveredBluetoothDevice implements Parcelable {
    private final BluetoothDevice device;
    private ScanResult lastScanResult;
    private String name;
    private int listPosition;
    private int rssi;
    private int previousRssi;
    String deviceMode="";
    private int highestRssi = -130;
    FallCountModel.Firmware firmwareInfo;
//    public static final String TAG = AppController.class.getSimpleName();
    public static final String TAG = "DiscoveredBluetoothDevice";

    public DiscoveredBluetoothDevice(@NonNull final ScanResult scanResult) {
        Log.d(TAG, "DiscoveredBluetoothDevice:");
        device = scanResult.getDevice();
        update(scanResult);
    }

    public DiscoveredBluetoothDevice(@NonNull final BluetoothDevice scanResult) {
        device = scanResult;
    }

    int free_fall_count = 0;
    int fall_count = 0;
    int lock_count = 0;
    int th1_count = 0;
    int th2_count = 0;
    int th3_count = 0;
    boolean isConnected;
    int batteryPercentage = -1;
    int totalRotationCount = 0;
    String VersionName = "";
    String thresholds_data = "";
    private String assetName;
    private String firmwareType;
    private String assetImage;

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }

    public int getFall_count() {
        return fall_count;
    }

    public void setFall_count(int fall_count) {
        this.fall_count = fall_count;
    }

    public int getLock_count() {
        return lock_count;
    }

    public void setLock_count(int lock_count) {
        this.lock_count = lock_count;
    }

    public int getFree_fall_count() {
        return free_fall_count;
    }

    public void setFree_fall_count(int free_fall_count) {
        this.free_fall_count = free_fall_count;
    }

    public int getTh1_count() {
        return th1_count;
    }

    public void setTh1_count(int th1_count) {
        this.th1_count = th1_count;
    }

    public int getTh2_count() {
        return th2_count;
    }

    public void setTh2_count(int th2_count) {
        this.th2_count = th2_count;
    }

    public int getTh3_count() {
        return th3_count;
    }

    public void setTh3_count(int th3_count) {
        this.th3_count = th3_count;
    }

    public int getBatteryPercentage() {
        return batteryPercentage;
    }

    public void setBatteryPercentage(int batteryPercentage) {
        this.batteryPercentage = batteryPercentage;
    }

    public int getTotalRotationCount() {
        return totalRotationCount;
    }

    public void setTotalRotationCount(int totalRotationCount) {
        this.totalRotationCount = totalRotationCount;
    }

    public String getVersionName() {
        return VersionName;
    }

    public void setVersionName(String versionName) {
        VersionName = versionName;
    }

    public String getThresholds_data() {
        return thresholds_data;
    }

    public void setThresholds_data(String thresholds_data) {
        this.thresholds_data = thresholds_data;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getFirmwareType() {
        return firmwareType;
    }

    public void setFirmwareType(String firmwareType) {
        this.firmwareType = firmwareType;
    }

    public String getAssetImage() {
        return assetImage;
    }

    public void setAssetImage(String assetImage) {
        this.assetImage = assetImage;
    }

    public int getListPosition() {
        return listPosition;
    }

    public void setListPosition(int listPosition) {
        this.listPosition = listPosition;
    }

    public FallCountModel.Firmware getFirmwareInfo() {
        return firmwareInfo;
    }

    public void setFirmwareInfo(FallCountModel.Firmware firmwareInfo) {
        this.firmwareInfo = firmwareInfo;
    }

    public String getDeviceMode() {
        return deviceMode;
    }

    public void setDeviceMode(String deviceMode) {
        this.deviceMode = deviceMode;
    }

    @NonNull
    public BluetoothDevice getDevice() {
        return device;
    }

    @NonNull
    public String getAddress() {
        return device.getAddress();
    }

    @Nullable
    public String getName() {
        return name;
    }

    @SuppressWarnings("WeakerAccess")
    public int getRssi() {
        return rssi;
    }

    @NonNull
    public ScanResult getScanResult() {
        return lastScanResult;
    }

    public int getHighestRssi() {
        return highestRssi;
    }

    public double getDistance(int measuredPower, double rssi) {
        if (rssi >= 0) {
            return -1.0;
        }
        if (measuredPower == 0) {
            return -1.0;
        }
        double ratio = rssi * 1.0 / measuredPower;
        if (ratio < 1.0) {
            return Math.pow(ratio, 10);
        } else {
            double distance= (0.42093) * Math.pow(ratio, 6.9476) + 0.54992;
            return distance;
        }
    }

    boolean hasRssiLevelChanged() {
        final int newLevel =
                rssi <= 10 ?
                        0 :
                        rssi <= 28 ?
                                1 :
                                rssi <= 45 ?
                                        2 :
                                        rssi <= 65 ?
                                                3 :
                                                4;
        final int oldLevel =
                previousRssi <= 10 ?
                        0 :
                        previousRssi <= 28 ?
                                1 :
                                previousRssi <= 45 ?
                                        2 :
                                        previousRssi <= 65 ?
                                                3 :
                                                4;
        return newLevel != oldLevel;
    }

/**
     * Updates the device values based on the scan result.
     *
     * @param scanResult the new received scan result.
*/

    public void update(@NonNull final ScanResult scanResult) {
//        Log.d(TAG, "update:");
        lastScanResult = scanResult;
        name = scanResult.getScanRecord() != null ?
                scanResult.getScanRecord().getDeviceName() : null;
        previousRssi = rssi;
        rssi = scanResult.getRssi();
        if (highestRssi < rssi)
            highestRssi = rssi;
    }

    public boolean matches(@NonNull final ScanResult scanResult) {
//        Log.d(TAG, "matches:");
        return device.getAddress().equals(scanResult.getDevice().getAddress());
    }

    @Override
    public int hashCode() {
        return device.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (o instanceof DiscoveredBluetoothDevice) {
            final DiscoveredBluetoothDevice that = (DiscoveredBluetoothDevice) o;
            return device.getAddress().equals(that.device.getAddress());
        }
        return super.equals(o);
    }


    // Parcelable implementation

    private DiscoveredBluetoothDevice(final Parcel in) {
        device = in.readParcelable(BluetoothDevice.class.getClassLoader());
        lastScanResult = in.readParcelable(ScanResult.class.getClassLoader());
        name = in.readString();
        rssi = in.readInt();
        previousRssi = in.readInt();
        highestRssi = in.readInt();
    }


    @Override
    public void writeToParcel(final Parcel parcel, final int flags) {
        parcel.writeParcelable(device, flags);
        parcel.writeParcelable(lastScanResult, flags);
        parcel.writeString(name);
        parcel.writeInt(rssi);
        parcel.writeInt(previousRssi);
        parcel.writeInt(highestRssi);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DiscoveredBluetoothDevice> CREATOR = new Creator<DiscoveredBluetoothDevice>() {
        @Override
        public DiscoveredBluetoothDevice createFromParcel(final Parcel source) {
            return new DiscoveredBluetoothDevice(source);
        }

        @Override
        public DiscoveredBluetoothDevice[] newArray(final int size) {
            return new DiscoveredBluetoothDevice[size];
        }
    };
}
