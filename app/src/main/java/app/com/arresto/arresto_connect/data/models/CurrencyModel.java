package app.com.arresto.arresto_connect.data.models;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CurrencyModel {

    @SerializedName("home_ccy")
    @Expose
    private String homeCcy;
    @SerializedName("supported_ccy")
    @Expose
    private List<Currencies> supportedCcy = null;

    public String getHomeCcy() {
        return homeCcy;
    }

    public void setHomeCcy(String homeCcy) {
        this.homeCcy = homeCcy;
    }

    public List<Currencies> getSupportedCcy() {
        return supportedCcy;
    }

    public void setSupportedCcy(List<Currencies> supportedCcy) {
        this.supportedCcy = supportedCcy;
    }


    public static class Currencies {

        @SerializedName("id")
        @Expose
        private String id;
        @SerializedName("name")
        @Expose
        private String name;
        @SerializedName("value")
        @Expose
        private String value;

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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @NonNull
        @Override
        public String toString() {
            return name;
        }
    }
}