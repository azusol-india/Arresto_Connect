package app.com.arresto.arresto_connect.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ECSites {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("project_id")
    @Expose
    private String projectId;
    @SerializedName("client_id")
    @Expose
    private String clientId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("site_name")
    @Expose
    private String siteName;
    @SerializedName("site_image")
    @Expose
    private String siteImage;
    @SerializedName("jobcard_no")
    @Expose
    private String jobcardNo;
    @SerializedName("region")
    @Expose
    private String region;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("deleted")
    @Expose
    private String deleted;
    @SerializedName("geolocation")
    @Expose
    private Geolocation geolocation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteImage() {
        return siteImage;
    }

    public void setSiteImage(String siteImage) {
        this.siteImage = siteImage;
    }

    public String getJobcardNo() {
        return jobcardNo;
    }

    public void setJobcardNo(String jobcardNo) {
        this.jobcardNo = jobcardNo;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getDeleted() {
        return deleted;
    }

    public void setDeleted(String deleted) {
        this.deleted = deleted;
    }

    public Geolocation getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Geolocation geolocation) {
        this.geolocation = geolocation;
    }

    public class Geolocation {

        String latitude;

        String longitude;

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

    }
}
