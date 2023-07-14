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


import app.com.arresto.arresto_connect.constants.AppUtils;

public class Profile_Model {

    String upro_company;

    public String getUpro_company() {
        return upro_company;
    }

    public void setUpro_company(String upro_company) {
        this.upro_company = upro_company;
    }

    public String getUpro_company_address() {
        return upro_company_address;
    }

    public void setUpro_company_address(String upro_company_address) {
        this.upro_company_address = upro_company_address;
    }

    String upro_company_address;

    public String getUacc_email() {
        return uacc_email;
    }

    public void setUacc_email(String uacc_email) {
        this.uacc_email = uacc_email;
    }

    public String getFullname() {
        if (upro_first_name != null && upro_last_name != null)
            return upro_first_name + " " + upro_last_name;
        else if (upro_first_name != null)
            return upro_first_name;
        else
            return "";
    }

    public String getUpro_first_name() {
        return upro_first_name;
    }

    public void setUpro_first_name(String upro_first_name) {
        this.upro_first_name = upro_first_name;
    }

    public String getUpro_last_name() {
        return upro_last_name;
    }

    public void setUpro_last_name(String upro_last_name) {
        this.upro_last_name = upro_last_name;
    }

    public String getUpro_country_id() {
        return upro_country_id;
    }

    public void setUpro_country_id(String upro_country_id) {
        this.upro_country_id = upro_country_id;
    }

    public String getUpro_zone_id() {
        return upro_zone_id;
    }

    public void setUpro_zone_id(String upro_zone_id) {
        this.upro_zone_id = upro_zone_id;
    }

    public String getUpro_city_id() {
        return upro_city_id;
    }

    public void setUpro_city_id(String upro_city_id) {
        this.upro_city_id = upro_city_id;
    }

    public String getUpro_phone() {
        return upro_phone;
    }

    public void setUpro_phone(String upro_phone) {
        this.upro_phone = upro_phone;
    }

    public String getUpro_image() {
        return upro_image;
    }

    public void setUpro_image(String upro_image) {
        this.upro_image = upro_image;
    }

    public String getUpro_address() {
        return upro_address;
    }

    public void setUpro_address(String upro_address) {
        this.upro_address = upro_address;
    }

    public String getUpro_dob() {
        return upro_dob;
    }

    public void setUpro_dob(String upro_dob) {
        this.upro_dob = upro_dob;
    }

    public String getGroup_email() {
        return group_email;
    }

    public void setGroup_email(String group_email) {
        this.group_email = group_email;
    }

    public String getEmp_id() {
        return emp_id;
    }

    public void setEmp_id(String emp_id) {
        this.emp_id = emp_id;
    }

    public String getCan_approve_reports() {
        return can_approve_reports;
    }

    public void setCan_approve_reports(String can_approve_reports) {
        this.can_approve_reports = can_approve_reports;
    }

    public String getUser_group_code() {
        return user_group_code;
    }

    public void setUser_group_code(String user_group_code) {
        this.user_group_code = user_group_code;
    }

    public String getUser_group_name() {
        return user_group_name;
    }

    public void setUser_group_name(String user_group_name) {
        this.user_group_name = user_group_name;
    }

    String uacc_id;
    String uacc_email;
    String upro_first_name;
    String upro_last_name;
    String upro_country_id;
    String upro_zone_id;
    String upro_city_id;
    String upro_phone;
    String upro_image;
    String upro_address;
    String upro_dob;
    String group_email;
    String emp_id;
    String can_approve_reports;
    String user_group_code;
    String user_group_name;
    public Data getConfig() {
        if (config != null && !config.equals("")) {
            String data = AppUtils.getGson().toJson(config);
            return AppUtils.getGson().fromJson(data, Data.class);
        } else return null;
    }

    public String getUacc_id() {
        return uacc_id;
    }

    public void setUacc_id(String uacc_id) {
        this.uacc_id = uacc_id;
    }

    public void setConfig(Data config) {
        this.config = config;
    }

    Object config;

    public class Data {
        public String getCustomer_theme_color() {
            return customer_theme_color;
        }

        public void setCustomer_theme_color(String customer_theme_color) {
            this.customer_theme_color = customer_theme_color;
        }

        public String getCustomer_txt_color() {
            return customer_txt_color;
        }

        public void setCustomer_txt_color(String customer_txt_color) {
            this.customer_txt_color = customer_txt_color;
        }

        public String getKare_geofencing_radious() {
            return kare_geofencing_radious;
        }

        public void setKare_geofencing_radious(String kare_geofencing_radious) {
            this.kare_geofencing_radious = kare_geofencing_radious;
        }

        public String getRam_geofencing_flag() {
            return ram_geofencing_flag;
        }

        public void setRam_geofencing_flag(String ram_geofencing_flag) {
            this.ram_geofencing_flag = ram_geofencing_flag;
        }

        public String getRam_geofencing_radious() {
            return ram_geofencing_radious;
        }

        public void setRam_geofencing_radious(String ram_geofencing_radious) {
            this.ram_geofencing_radious = ram_geofencing_radious;
        }

        String customer_theme_color, customer_txt_color, kare_geofencing_radious, ram_geofencing_flag, ram_geofencing_radious;
    }


    private static Profile_Model holder = new Profile_Model();

    public static Profile_Model getInstance() {
        return holder;
    }

    public static Profile_Model setInstance(Profile_Model profile_model) {
        holder = profile_model;
        return holder;
    }
}
