/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.data.models;

import java.text.ParseException;
import java.util.ArrayList;

import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

public class Project_Model {

    String up_id;
    String up_project_name;
    String up_rope_status;
    String product_count;
    String up_customer_address;
    String up_customer;
    String up_mobile;
    String up_email;
    String up_site;
    String up_site_image;
    String up_subsite;
    String up_subsite_image;
    String up_created_date;
    String training_status;
    String attendance_marked;
    String up_startdate;
    String up_enddate;
    String team_id;
    ArrayList<String> up_forms;
    ArrayList<GroupUsers> team;
    Data up_geolocation;

    public Data getUp_geolocation() {
        return up_geolocation;
    }

    public ArrayList<String> getUp_forms() {
        return up_forms;
    }

    public void setUp_forms(ArrayList<String> up_forms) {
        this.up_forms = up_forms;
    }

    public void setUp_geolocation(Data up_geolocation) {
        this.up_geolocation = up_geolocation;
    }

    public String getUp_created_date() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_time.parse(up_created_date));
        } catch (ParseException e) {
            e.printStackTrace();
            return up_created_date;
        }

    }

    public void setUp_created_date(String up_created_date) {
        this.up_created_date = up_created_date;
    }

    public String getUp_customer_address() {
        return up_customer_address;
    }

    public void setUp_customer_address(String up_customer_address) {
        this.up_customer_address = up_customer_address;
    }

    public String getTeam_id() {
        return team_id;
    }

    public void setTeam_id(String team_id) {
        this.team_id = team_id;
    }

    public String getUp_customer() {
        return up_customer;
    }

    public void setUp_customer(String up_customer) {
        this.up_customer = up_customer;
    }

    public String getUp_mobile() {
        return up_mobile;
    }

    public void setUp_mobile(String up_mobile) {
        this.up_mobile = up_mobile;
    }

    public String getUp_email() {
        return up_email;
    }

    public void setUp_email(String up_email) {
        this.up_email = up_email;
    }

    public String getUp_site() {
        return up_site;
    }

    public void setUp_site(String up_site) {
        this.up_site = up_site;
    }

    public String getUp_subsite() {
        return up_subsite;
    }

    public void setUp_subsite(String up_subsite) {
        this.up_subsite = up_subsite;
    }

    public String getSite_image() {
        return up_site_image;
    }

    public void setSite_image(String site_image) {
        this.up_site_image = site_image;
    }

    public String getSubsite_image() {
        return up_subsite_image;
    }

    public void setSubsite_image(String subsite_image) {
        this.up_subsite_image = subsite_image;
    }

    public String getUp_worktype() {
        return up_worktype;
    }

    public void setUp_worktype(String up_worktype) {
        this.up_worktype = up_worktype;
    }

    String up_worktype;

    public String getUp_id() {
        return up_id;
    }

    public void setUp_id(String up_id) {
        this.up_id = up_id;
    }

    public String getUp_project_name() {
        return up_project_name;
    }

    public void setUp_project_name(String up_project_name) {
        this.up_project_name = up_project_name;
    }


    public String getUp_rope_status() {
        return up_rope_status;
    }

    public void setUp_rope_status(String up_rope_status) {
        this.up_rope_status = up_rope_status;
    }


    public String getProduct_count() {
        return product_count;
    }

    public void setProduct_count(String product_count) {
        this.product_count = product_count;
    }

    public String getAttendance_marked() {
        return attendance_marked;
    }

    public void setAttendance_marked(String attendance_marked) {
        this.attendance_marked = attendance_marked;
    }

    public String getTraining_status() {
        return training_status;
    }

    public void setTraining_status(String training_status) {
        this.training_status = training_status;
    }

    public ArrayList<GroupUsers> getTeam() {
        return team;
    }

    public void setTeam(ArrayList<GroupUsers> team) {
        this.team = team;
    }

    public String getUp_startdate() {
        return up_startdate;
    }

    public void setUp_startdate(String up_startdate) {
        this.up_startdate = up_startdate;
    }

    public String getUp_enddate() {
        return up_enddate;
    }

    public void setUp_enddate(String up_enddate) {
        this.up_enddate = up_enddate;
    }

    public class Data {
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

        String latitude, longitude;
    }

    @Override
    public String toString() {
        return up_project_name;
    }
}
