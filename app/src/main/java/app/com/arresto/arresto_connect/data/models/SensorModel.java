package app.com.arresto.arresto_connect.data.models;

import java.util.ArrayList;

public class SensorModel {

    Sensor_Details sensor;
    //thresolds
    ArrayList<ThresholdsData> thresholds;

    public Sensor_Details getSensor() {
        return sensor;
    }

    public void setSensor(Sensor_Details sensor) {
        this.sensor = sensor;
    }

    public ArrayList<ThresholdsData> getThresholds() {
        return thresholds;
    }

    public void setThresholds(ArrayList<ThresholdsData> thresholds) {
        this.thresholds = thresholds;
    }

    public class Sensor_Details {

        String id, client_id, sensor_id, uin, asset_code, asset_image, type, created_date;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getClient_id() {
            return client_id;
        }

        public void setClient_id(String client_id) {
            this.client_id = client_id;
        }

        public String getSensor_id() {
            return sensor_id;
        }

        public void setSensor_id(String sensor_id) {
            this.sensor_id = sensor_id;
        }

        public String getUin() {
            return uin;
        }

        public void setUin(String uin) {
            this.uin = uin;
        }

        public String getAsset_code() {
            return asset_code;
        }

        public void setAsset_code(String asset_code) {
            this.asset_code = asset_code;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getCreated_date() {
            return created_date;
        }

        public void setCreated_date(String created_date) {
            this.created_date = created_date;
        }

        public String getAsset_image() {
            return asset_image;
        }

        public void setAsset_image(String asset_image) {
            this.asset_image = asset_image;
        }


    }

    public class ThresholdsData {
        String id, model_alias,calibration_value, threshold_value;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getModel_alias() {
            return model_alias;
        }

        public void setModel_alias(String model_alias) {
            this.model_alias = model_alias;
        }
        public String getCalibration_value() {
            return calibration_value;
        }

        public void setCalibration_value(String calibration_value) {
            this.calibration_value = calibration_value;
        }

        public String getThreshold_value() {
            return threshold_value;
        }

        public void setThreshold_value(String threshold_value) {
            this.threshold_value = threshold_value;
        }
    }
}
