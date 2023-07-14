package app.com.arresto.arresto_connect.data.models;


import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Client_model {
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("client_name")
    @Expose
    private String clientName;
    @SerializedName("client_district")
    @Expose
    private String clientDistrict;
    @SerializedName("client_circle")
    @Expose
    private String clientCircle;
    @SerializedName("client_address")
    @Expose
    private String clientAddress;
    @SerializedName("client_contactPerson")
    @Expose
    private String clientContactPerson;
    @SerializedName("client_contactNo")
    @Expose
    private String clientContactNo;
    @SerializedName("client_contactPerson_email")
    @Expose
    private String clientContactPersonEmail;
    @SerializedName("client_type")
    @Expose
    private String clientType;
    @SerializedName("client_status")
    @Expose
    private String clientStatus;

    @SerializedName("client_latlong")
    @Expose
    private Object client_latlong;

    @SerializedName("client_range")
    @Expose
    private String client_range;

    @SerializedName("client_website")
    @Expose
    private String client_website;

    @SerializedName("client_support")
    @Expose
    private String client_support;

    double distance;

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientDistrict() {
        return clientDistrict;
    }

    public void setClientDistrict(String clientDistrict) {
        this.clientDistrict = clientDistrict;
    }

    public String getClientCircle() {
        return clientCircle;
    }

    public void setClientCircle(String clientCircle) {
        this.clientCircle = clientCircle;
    }

    public String getClientAddress() {
        return clientAddress;
    }

    public void setClientAddress(String clientAddress) {
        this.clientAddress = clientAddress;
    }

    public String getClientContactPerson() {
        return clientContactPerson;
    }

    public void setClientContactPerson(String clientContactPerson) {
        this.clientContactPerson = clientContactPerson;
    }

    public String getClientContactNo() {
        return clientContactNo;
    }

    public void setClientContactNo(String clientContactNo) {
        this.clientContactNo = clientContactNo;
    }

    public String getClientContactPersonEmail() {
        return clientContactPersonEmail;
    }

    public void setClientContactPersonEmail(String clientContactPersonEmail) {
        this.clientContactPersonEmail = clientContactPersonEmail;
    }

    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public String getClientStatus() {
        return clientStatus;
    }

    public void setClientStatus(String clientStatus) {
        this.clientStatus = clientStatus;
    }

    public Latlong getClient_latlong() {
        if (!client_latlong.equals(""))
            return new Gson().fromJson(client_latlong.toString(),Latlong.class);
        else return null;
    }

    public void setClient_latlong(String client_latlong) {
        this.client_latlong = client_latlong;
    }

    public String getClient_range() {
        return client_range;
    }

    public void setClient_range(String client_range) {
        this.client_range = client_range;
    }

    public String getClient_website() {
        return client_website;
    }

    public void setClient_website(String client_website) {
        this.client_website = client_website;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getClient_support() {
        return client_support;
    }

    public void setClient_support(String client_support) {
        this.client_support = client_support;
    }

    @NonNull
    @Override
    public String toString() {
        return clientName;
    }

    public class Latlong {
        @SerializedName("lat")
        @Expose
        String lat;
        @SerializedName("long")
        @Expose
        private String lng;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLng() {
            return lng;
        }

        public void setLng(String lng) {
            this.lng = lng;
        }
    }
}
