package app.com.arresto.arresto_connect.data.models;

import android.os.Parcel;
import android.os.Parcelable;

public class ThermalSubassetModel implements Cloneable {
    //    String thermal_image;
//    String marked_image;
//    String actual_image;
    String subAssetName, subAsset2Name;
    String subAssetId;
    Constant_model temperature, temperature2;
    String delta_t;
    String remark;
    Observation_Model observation;


    public ThermalSubassetModel() {

    }

    public String getSubAssetName() {
        return subAssetName;
    }

    public void setSubAssetName(String subAssetName) {
        this.subAssetName = subAssetName;
    }

    public String getSubAssetId() {
        return subAssetId;
    }

    public void setSubAssetId(String subAssetId) {
        this.subAssetId = subAssetId;
    }

    public Constant_model getTemperature() {
        return temperature;
    }

    public void setTemperature(Constant_model temperature) {
        this.temperature = temperature;
    }

    public String getSubAsset2Name() {
        return subAsset2Name;
    }

    public void setSubAsset2Name(String subAsset2Name) {
        this.subAsset2Name = subAsset2Name;
    }

    public Constant_model getTemperature2() {
        return temperature2;
    }

    public void setTemperature2(Constant_model temperature2) {
        this.temperature2 = temperature2;
    }

    public String getDelta_t() {
        return delta_t;
    }

    public void setDelta_t(String delta_t) {
        this.delta_t = delta_t;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Observation_Model getObservation() {
        return observation;
    }

    public void setObservation(Observation_Model observation) {
        this.observation = observation;
    }

    public ThermalSubassetModel clone() {
        try {
            return (ThermalSubassetModel) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }
}
