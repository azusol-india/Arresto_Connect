package app.com.arresto.arresto_connect.data.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Constant_model {
    @SerializedName(value = "id", alternate = {"key","product_id", "component_id", "sub_assets_id"})
    String id;
    @SerializedName(value = "name", alternate = {"value","type_name", "type", "product_code",
            "component_code", "sub_assets_code", "actionProposed", "jc_number","jobno", "sms_number"})
    String name;
    @SerializedName("date")
    String date;
    @SerializedName("temp")
    String temp;
//    @SerializedName("maxTemp")
//    String maxTemp;
//    @SerializedName("minTemp")
//    String minTemp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

//    public String getMaxTemp() {
//        return maxTemp;
//    }
//
//    public void setMaxTemp(String maxTemp) {
//        this.maxTemp = maxTemp;
//    }
//
//    public String getMinTemp() {
//        return minTemp;
//    }
//
//    public void setMinTemp(String minTemp) {
//        this.minTemp = minTemp;
//    }

    @NonNull
    @Override
    public String toString() {
        return name;
    }
}
