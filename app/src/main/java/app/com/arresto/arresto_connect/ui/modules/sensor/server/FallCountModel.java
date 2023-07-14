package app.com.arresto.arresto_connect.ui.modules.sensor.server;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.Periodic_model;

public class FallCountModel implements Serializable {
    String sensor_id, asset_code, asset_image, type, asset_type, sensor_status, uacc_email, name;
    ArrayList<DataCount> count_data;
    Firmware firmware_info;

    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getAsset_code() {
        return asset_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getAsset_image() {
        return asset_image;
    }

    public void setAsset_image(String asset_image) {
        this.asset_image = asset_image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<DataCount> getCount_data() {
        return count_data;
    }

    public void setCount_data(ArrayList<DataCount> count_data) {
        this.count_data = count_data;
    }

    public String getAsset_type() {
        return asset_type;
    }

    public void setAsset_type(String asset_type) {
        this.asset_type = asset_type;
    }

    public Firmware getFirmware_info() {
        return firmware_info;
    }

    public void setFirmware_info(Firmware firmware_info) {
        this.firmware_info = firmware_info;
    }

    public String getSensor_status() {
        return sensor_status;
    }

    public void setSensor_status(String sensor_status) {
        this.sensor_status = sensor_status;
    }

    public String getUacc_email() {
        return uacc_email;
    }

    public void setUacc_email(String uacc_email) {
        this.uacc_email = uacc_email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public class DataCount {
        String count, vibration_type;

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getVibration_type() {
            return vibration_type;
        }

        public void setVibration_type(String vibration_type) {
            this.vibration_type = vibration_type;
        }
    }

    public class Firmware {
        String id, firmware_version, firmware_path, winch = "";
        ArrayList<Constant_model> thresholds;
        ArrayList<String> containingThresholds;
        ArrayList<Constant_model> winch_data;
        String gear_ratio;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getFirmware_version() {
            return firmware_version;
        }

        public void setFirmware_version(String firmware_version) {
            this.firmware_version = firmware_version;
        }

        public String getFirmware_path() {
            return firmware_path;
        }

        public void setFirmware_path(String firmware_path) {
            this.firmware_path = firmware_path;
        }

        public ArrayList<Constant_model> getThresholds() {
            return thresholds;
        }

        public void setThresholds(ArrayList<Constant_model> thresholds) {
            this.thresholds = thresholds;
        }

        public String getWinch() {
            return winch;
        }

        public void setWinch(String winch) {
            this.winch = winch;
        }

        public ArrayList<Constant_model> getWinch_data() {
            return winch_data;
        }

        public void setWinch_data(ArrayList<Constant_model> winch_data) {
            this.winch_data = winch_data;
        }

        public String getGear_ratio() {
            return gear_ratio;
        }

        public void setGear_ratio(String gear_ratio) {
            this.gear_ratio = gear_ratio;
        }

        public ArrayList<String> getContainingThresholds() {
            return containingThresholds;
        }

        public void setContainingThresholds(ArrayList<String> containingThresholds) {
            this.containingThresholds = containingThresholds;
        }
    }

}
