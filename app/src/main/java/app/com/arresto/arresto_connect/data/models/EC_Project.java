package app.com.arresto.arresto_connect.data.models;

import java.io.Serializable;
import java.text.ParseException;

import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

/**
 * Created by AZUSOL-PC-02 on 9/12/2019.
 */
public class EC_Project implements Serializable {
    //    String up_id;

    String ecp_id;
    String project_name;
    String customer_name;
    String customer_address;
    String customer_email;
    String customer_mobile;
    String site;
    String subsite;
    String project_image;
    String region;
    String application;

    String created_date;

    Project_Model.Data geolocation;

    public String getEcp_id() {
        return ecp_id;
    }

    public String getCreated_date() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_time.parse(created_date));
        } catch (ParseException e) {
            e.printStackTrace();
            return created_date;
        }

    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getProject_image() {
        return project_image;
    }

    public void setProject_image(String project_image) {
        this.project_image = project_image;
    }

    public void setCreated_date(String created_date) {
        this.created_date = created_date;
    }

    public void setEcp_id(String ecp_id) {
        this.ecp_id = ecp_id;
    }

    public String getCustomer_name() {
        return customer_name;
    }

    public void setCustomer_name(String customer_name) {
        this.customer_name = customer_name;
    }

    public String getCustomer_address() {
        return customer_address;
    }

    public void setCustomer_address(String customer_address) {
        this.customer_address = customer_address;
    }

    public String getCustomer_email() {
        return customer_email;
    }

    public void setCustomer_email(String customer_email) {
        this.customer_email = customer_email;
    }

    public String getCustomer_mobile() {
        return customer_mobile;
    }

    public void setCustomer_mobile(String customer_mobile) {
        this.customer_mobile = customer_mobile;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getSubsite() {
        return subsite;
    }

    public void setSubsite(String subsite) {
        this.subsite = subsite;
    }

    public Project_Model.Data getGeolocation() {
        return geolocation;
    }

    public void setGeolocation(Project_Model.Data geolocation) {
        this.geolocation = geolocation;
    }

    public EC_Project(String project_name) {
        this.project_name = project_name;
    }

    public String getProject_name() {
        return project_name;
    }

    public void setProject_name(String project_name) {
        this.project_name = project_name;
    }

}
