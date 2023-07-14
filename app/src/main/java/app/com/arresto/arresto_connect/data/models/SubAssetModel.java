package app.com.arresto.arresto_connect.data.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubAssetModel{
    @SerializedName("sub_assets_id")
    @Expose
    private String subAssetsId;
    @SerializedName("sub_assets_client_fk")
    @Expose
    private String subAssetsClientFk;
    @SerializedName("sub_assets_code")
    @Expose
    private String subAssetsCode;
    @SerializedName("sub_assets_description")
    @Expose
    private String subAssetsDescription;
    @SerializedName("sub_assets_image")
    @Expose
    private String subAssetsImage;
    @SerializedName("sub_assets_imagepath")
    @Expose
    private String subAssetsImagepath;
    @SerializedName("sub_assets_uom")
    @Expose
    private String subAssetsUom;
    @SerializedName("sub_assets_inspection")
    @Expose
    private String subAssetsInspection;
    @SerializedName("sub_assets_result")
    @Expose
    private String subAssetsResult;
    @SerializedName("sub_assets_observation")
    @Expose
    private String subAssetsObservation;
    @SerializedName("sub_assets_repair")
    @Expose
    private String subAssetsRepair;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("user_email")
    @Expose
    private String userEmail;
    @SerializedName("created_date")
    @Expose
    private String createdDate;
    @SerializedName("updated_date")
    @Expose
    private String updatedDate;
    @SerializedName("speci_file_id")
    @Expose
    private String speciFileId;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("error")
    @Expose
    private String error;
    @SerializedName("sub_assets_inspection_id")
    @Expose
    private String subAssetsInspectionId;
    @SerializedName("pass_imagepath")
    @Expose
    private String passImagepath;
    @SerializedName("fail_imagepath")
    @Expose
    private String failImagepath;
    @SerializedName("repair_imagepath")
    @Expose
    private String repairImagepath;

    public String getSubAssetsId() {
        return subAssetsId;
    }

    public void setSubAssetsId(String subAssetsId) {
        this.subAssetsId = subAssetsId;
    }

    public String getSubAssetsClientFk() {
        return subAssetsClientFk;
    }

    public void setSubAssetsClientFk(String subAssetsClientFk) {
        this.subAssetsClientFk = subAssetsClientFk;
    }

    public String getSubAssetsCode() {
        return subAssetsCode;
    }

    public void setSubAssetsCode(String subAssetsCode) {
        this.subAssetsCode = subAssetsCode;
    }

    public String getSubAssetsDescription() {
        return subAssetsDescription;
    }

    public void setSubAssetsDescription(String subAssetsDescription) {
        this.subAssetsDescription = subAssetsDescription;
    }

    public String getSubAssetsImage() {
        return subAssetsImage;
    }

    public void setSubAssetsImage(String subAssetsImage) {
        this.subAssetsImage = subAssetsImage;
    }

    public String getSubAssetsImagepath() {
        return subAssetsImagepath;
    }

    public void setSubAssetsImagepath(String subAssetsImagepath) {
        this.subAssetsImagepath = subAssetsImagepath;
    }

    public String getSubAssetsUom() {
        return subAssetsUom;
    }

    public void setSubAssetsUom(String subAssetsUom) {
        this.subAssetsUom = subAssetsUom;
    }

    public String getSubAssetsInspection() {
        return subAssetsInspection;
    }

    public void setSubAssetsInspection(String subAssetsInspection) {
        this.subAssetsInspection = subAssetsInspection;
    }

    public String getSubAssetsResult() {
        return subAssetsResult;
    }

    public void setSubAssetsResult(String subAssetsResult) {
        this.subAssetsResult = subAssetsResult;
    }

    public String getSubAssetsObservation() {
        return subAssetsObservation;
    }

    public void setSubAssetsObservation(String subAssetsObservation) {
        this.subAssetsObservation = subAssetsObservation;
    }

    public String getSubAssetsRepair() {
        return subAssetsRepair;
    }

    public void setSubAssetsRepair(String subAssetsRepair) {
        this.subAssetsRepair = subAssetsRepair;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }

    public String getSpeciFileId() {
        return speciFileId;
    }

    public void setSpeciFileId(String speciFileId) {
        this.speciFileId = speciFileId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getSubAssetsInspectionId() {
        return subAssetsInspectionId;
    }

    public void setSubAssetsInspectionId(String subAssetsInspectionId) {
        this.subAssetsInspectionId = subAssetsInspectionId;
    }

    public String getPassImagepath() {
        return passImagepath;
    }

    public void setPassImagepath(String passImagepath) {
        this.passImagepath = passImagepath;
    }

    public String getFailImagepath() {
        return failImagepath;
    }

    public void setFailImagepath(String failImagepath) {
        this.failImagepath = failImagepath;
    }

    public String getRepairImagepath() {
        return repairImagepath;
    }

    public void setRepairImagepath(String repairImagepath) {
        this.repairImagepath = repairImagepath;
    }

}
