package app.com.arresto.arresto_connect.ui.modules.sensor.server;

public interface OnDeviceConnect {

    void onAssetFetched(FallCountModel sensorModels);

    void onBatteryFetched(int percentage);

    void onVersionFetched(String versionName);

    void notifyData(Object data);

    void onStateChange(String dvState);

}