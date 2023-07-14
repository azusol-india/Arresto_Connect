/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by AZUSOL-PC-02 on 5/13/2019.
 */
public class Reports_model implements Serializable {

    public String getId() {
        return id;
    }

    public String getReport_no() {
        return report_no;
    }

    public String getSite_id() {
        return site_id;
    }


    public String getReport_url() {
        return report_url;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setReport_no(String report_no) {
        this.report_no = report_no;
    }

    public void setReport_url(String report_url) {
        this.report_url = report_url;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getProduct_code() {
        return product_code.replaceAll("\"|\\[|\\]", "");
    }

    public void setApproved_status(String approved_status) {
        this.approved_status = approved_status;
    }

    public String getApproved_status() {
        switch (approved_status) {
            case "0":
                return "rejected";
            case "1":
                return "approved";
            default:
                return approved_status;
        }
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getCreated_date() {
        return created_date;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public String getThermal_id() {
        return thermal_id;
    }

    public void setThermal_id(String thermal_id) {
        this.thermal_id = thermal_id;
    }

    public String getInspected_by() {
        return inspected_by;
    }

    public void setInspected_by(String inspected_by) {
        this.inspected_by = inspected_by;
    }

    public String getMdata_id() {
        return mdata_id;
    }

    public void setMdata_id(String mdata_id) {
        this.mdata_id = mdata_id;
    }

    @SerializedName(value = "id", alternate = {"cf_id", "inspection_id", "repo_project_id"})
    private String id;
    @SerializedName(value = "report_no", alternate = {"repo_report_no"})
    private String report_no;
    @SerializedName(value = "report_url", alternate = {"report_pdf","inspection_report_url", "report_path"})
    private String report_url;
    @SerializedName(value = "site_id", alternate = {"form_name", "repo_project_name", "mdata_uin"})
    private String site_id;
    @SerializedName(value = "asset_series", alternate = {"sm_name", "repo_user_name", "asset_id", "mdata_asset"})
    private String product_code;
    @SerializedName(value = "approved_status", alternate = {"inspection_status", "repo_status","checked_status", "is_confirmed"})
    private String approved_status = "pending";
    @SerializedName(value = "user_type")
    private String user_type;
    @SerializedName(value = "mdata_id")
    private String mdata_id;
    @SerializedName(value = "approver")
    private String approver;
    @SerializedName(value = "thermal_id")
    private String thermal_id;
    @SerializedName(value = "inspected_by")
    private String inspected_by;
    @SerializedName(value = "created_date")
    private String created_date;

}
