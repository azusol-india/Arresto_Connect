package app.com.arresto.arresto_connect.ui.modules.sensor;

import android.os.ParcelUuid;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.ui.modules.sensor.ble_lib.ScanResult;


public class DevicesLiveData extends LiveData<List<DiscoveredBluetoothDevice>> {
    //    	private static final ParcelUuid FILTER_UUID = new ParcelUuid(BlinkyManager.LBS_UUID_SERVICE);
    private static final int FILTER_RSSI = -100; // [dBm]
    private static final String FILTER_NAME = "KARE"; // [dBm]
    ParcelUuid FILTER_UUID = null;
    @NonNull
    private final List<DiscoveredBluetoothDevice> devices = new ArrayList<>();
    private final List<String> devicesName = new ArrayList<>();
    @Nullable
    private List<DiscoveredBluetoothDevice> filteredDevices = null;
    private boolean filterUuidRequired;
    private boolean filterNearbyOnly;
    private boolean filterName;

    DevicesLiveData(final boolean filterUuidRequired, final boolean filterNearbyOnly) {
        this.filterUuidRequired = filterUuidRequired;
        this.filterNearbyOnly = filterNearbyOnly;
    }

    synchronized void bluetoothDisabled() {
        devices.clear();
        devicesName.clear();
        filteredDevices = null;
        postValue(null);
    }

    public boolean filterByName(boolean nameRequired) {
        filterName = nameRequired;
        return applyFilter();
    }

    boolean filterByDistance(final boolean nearbyOnly) {
        filterNearbyOnly = nearbyOnly;
        return applyFilter();
    }


    public List<DiscoveredBluetoothDevice> getDevices() {
        return devices;
    }
    public void addDevices(List<DiscoveredBluetoothDevice> devices) {
        for (DiscoveredBluetoothDevice device : devices) {
            addNewDevice(device);
        }
        Log.e("addDevices found ", "" + devices);
        applyFilter();
    }

    synchronized void addNewDevice(DiscoveredBluetoothDevice device) {
        if (!devicesName.contains(device.getName())) {
            devices.add(device);
            devicesName.add(device.getName());
            Log.e("addNewDevice found ", "gfgf===" + devicesName);
        }
    }


    synchronized boolean deviceDiscovered(@NonNull final ScanResult result) {
        DiscoveredBluetoothDevice device;
        // Check if it's a new device.
        final int index = indexOf(result);
        if (index == -1) {
            device = new DiscoveredBluetoothDevice(result);
            devices.add(device);
            devicesName.add(device.getName());
        } else {
            device = devices.get(index);
        }

        // Update RSSI and name.
        device.update(result);

        // Return true if the device was on the filtered list or is to be added.
        return (filteredDevices != null && filteredDevices.contains(device))
                || (matchesNameFilter(device.getName()) && matchesNearbyFilter(device.getHighestRssi()));
    }

    /**
     * Clears the list of devices.
     */
    public synchronized void clear() {
        devices.clear();
        filteredDevices = null;
        postValue(null);
    }

    /**
     * Refreshes the filtered device list based on the filter flags.
     */
    synchronized boolean applyFilter() {
        final List<DiscoveredBluetoothDevice> tmp = new ArrayList<>();
        for (final DiscoveredBluetoothDevice device : devices) {
            final ScanResult result = device.getScanResult();
            if (matchesNameFilter(device.getName()) && matchesNearbyFilter(device.getHighestRssi())) {
                tmp.add(device);
            }
        }
        filteredDevices = tmp;
        postValue(filteredDevices);
        return !filteredDevices.isEmpty();
    }

    private boolean matchesNameFilter(String deviceName) {
        if (!filterName)
            return true;
        if (deviceName == null)
            return false;
        return deviceName.contains(FILTER_NAME);
    }

    /**
     * Finds the index of existing devices on the device list.
     *
     * @param result scan result.
     * @return Index of -1 if not found.
     */
    private int indexOf(@NonNull final ScanResult result) {
        int i = 0;
        for (final DiscoveredBluetoothDevice device : devices) {
            if (device.matches(result))
                return i;
            i++;
        }
        return -1;
    }

    @SuppressWarnings("SimplifiableIfStatement")
    private boolean matchesNearbyFilter(final int rssi) {
        if (!filterNearbyOnly)
            return true;

        return rssi >= FILTER_RSSI;
    }

}
