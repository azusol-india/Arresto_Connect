package app.com.arresto.arresto_connect.ui.modules.sensor.server;

public class SensorHistoryModel {
    String id,device_timestamp,device_timestamp1,vibration_type,status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDevice_timestamp() {
        return device_timestamp;
    }

    public void setDevice_timestamp(String device_timestamp) {
        this.device_timestamp = device_timestamp;
    }

    public String getDevice_timestamp1() {
        return device_timestamp1;
    }

    public void setDevice_timestamp1(String device_timestamp1) {
        this.device_timestamp1 = device_timestamp1;
    }

    public String getVibration_type() {
        return vibration_type;
    }

    public void setVibration_type(String vibration_type) {
        this.vibration_type = vibration_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
