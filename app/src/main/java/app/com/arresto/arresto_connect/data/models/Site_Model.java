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

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;

import app.com.arresto.arresto_connect.constants.AppUtils;

public class Site_Model {

    public String getMdata_rfid() {
        return mdata_rfid;
    }

    public void setMdata_rfid(String mdata_rfid) {
        this.mdata_rfid = mdata_rfid;
    }

    public String getMdata_barcode() {
        return mdata_barcode;
    }

    public void setMdata_barcode(String mdata_barcode) {
        this.mdata_barcode = mdata_barcode;
    }

    public String getMdata_uin() {
        return mdata_uin;
    }

    public void setMdata_uin(String mdata_uin) {
        this.mdata_uin = mdata_uin;
    }

    private String mdata_rfid;
    private String mdata_barcode;
    private String mdata_uin;
    private String mdata_item_series;
    private String siteID_id;
    private String site_jobcard;
    private String site_sms;
    private String site_id;
    private String site_location;
    private String site_city;
    private String site_address;
    private String site_lattitude;
    private String site_longitude;
    private String site_contact_name;
    private String site_contact_number;
    private String site_contact_email;
    private String master_id;
    private String master_data_id;
    private String totalAsset;
    private String totalSubAsset;
    private String totalAction_proposed;
    private String client_name;
    private String label_user;
    private String labeled_by;
    private String report_no;
    private String inspected_status;
    private String approved_status;
    private String workPermit_number;
    private String input_type;
    private String inspection_status;
    private String asset_status;
    private int mdata_pro;
    public Object userInfo;

    public ArrayList<String> getALL() {
        ArrayList<String> strings = new ArrayList<>();
        strings.add(getClient_name());
        strings.add(getSite_contact_name());
        strings.add(getSite_contact_number());
        strings.add(getMdata_item_series());
        strings.add(getMdata_uin());
        strings.add(getReport_no());
        strings.add(getSite_id());
        strings.add(getSite_jobcard());
        strings.add(getSite_sms());
        strings.add(getInspected_status());
        return strings;
    }


    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    private String imagepath;

    public String getRef_user_id() {
        return ref_user_id;
    }

    public void setRef_user_id(String ref_user_id) {
        this.ref_user_id = ref_user_id;
    }

    private String ref_user_id;

    public String getScheduled_date() {
        return scheduled_date;
    }

    public void setScheduled_date(String scheduled_date) {
        this.scheduled_date = scheduled_date;
    }

    private String scheduled_date;

    public String getMdata_item_series() {
        if (mdata_item_series != null)
            return mdata_item_series.replaceAll("\"|\\[|\\]", "");
        else
            return mdata_item_series;
    }

    public void setMdata_item_series(String mdata_item_series) {
        this.mdata_item_series = mdata_item_series;
    }

    public String getSiteID_id() {
        return siteID_id;
    }

    public void setSiteID_id(String siteID_id) {
        this.siteID_id = siteID_id;
    }

    public String getSite_jobcard() {
        return site_jobcard;
    }

    public void setSite_jobcard(String site_jobcard) {
        this.site_jobcard = site_jobcard;
    }

    public String getSite_sms() {
        return site_sms;
    }

    public void setSite_sms(String site_sms) {
        this.site_sms = site_sms;
    }

    public String getSite_id() {
        return site_id;
    }

    public void setSite_id(String site_id) {
        this.site_id = site_id;
    }

    public String getSite_location() {
        return site_location;
    }

    public void setSite_location(String site_location) {
        this.site_location = site_location;
    }

    public String getSite_city() {
        return site_city;
    }

    public void setSite_city(String site_city) {
        this.site_city = site_city;
    }

    public String getSite_address() {
        return site_address;
    }

    public void setSite_address(String site_address) {
        this.site_address = site_address;
    }

    public String getSite_lattitude() {
        return site_lattitude;
    }

    public void setSite_lattitude(String site_lattitude) {
        this.site_lattitude = site_lattitude;
    }

    public String getSite_longitude() {
        return site_longitude;
    }

    public void setSite_longitude(String site_longitude) {
        this.site_longitude = site_longitude;
    }

    public String getSite_contact_name() {
        return site_contact_name;
    }

    public void setSite_contact_name(String site_contact_name) {
        this.site_contact_name = site_contact_name;
    }

    public String getSite_contact_number() {
        return site_contact_number;
    }

    public void setSite_contact_number(String site_contact_number) {
        this.site_contact_number = site_contact_number;
    }

    public int getMdata_pro() {
        return mdata_pro;
    }

    public void setMdata_pro(int mdata_pro) {
        this.mdata_pro = mdata_pro;
    }

    public String getSite_contact_email() {
        return site_contact_email;
    }

    public void setSite_contact_email(String site_contact_email) {
        this.site_contact_email = site_contact_email;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getMaster_data_id() {
        return master_data_id;
    }

    public void setMaster_data_id(String master_data_id) {
        this.master_data_id = master_data_id;
    }

    public String getTotalAsset() {
        return totalAsset;
    }

    public void setTotalAsset(String totalAsset) {
        this.totalAsset = totalAsset;
    }

    public String getTotalSubAsset() {
        return totalSubAsset;
    }

    public void setTotalSubAsset(String totalSubAsset) {
        this.totalSubAsset = totalSubAsset;
    }

    public String getTotalAction_proposed() {
        return totalAction_proposed;
    }

    public void setTotalAction_proposed(String totalAction_proposed) {
        this.totalAction_proposed = totalAction_proposed;
    }

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getReport_no() {
        return report_no;
    }

    public void setReport_no(String report_no) {
        this.report_no = report_no;
    }

    public String getInspected_status() {
        return inspected_status;
    }

    public void setInspected_status(String inspected_status) {
        this.inspected_status = inspected_status;
    }

    public String getApproved_status() {
        return approved_status;
    }

    public void setApproved_status(String approved_status) {
        this.approved_status = approved_status;
    }

    public String getWorkPermit_number() {
        return workPermit_number;
    }

    public void setWorkPermit_number(String workPermit_number) {
        this.workPermit_number = workPermit_number;
    }

    public String getInput_type() {
        return input_type;
    }

    public void setInput_type(String input_type) {
        this.input_type = input_type;
    }

    public String getLabel_user() {
        return label_user;
    }

    public void setLabel_user(String label_user) {
        this.label_user = label_user;
    }

    public String getLabeled_by() {
        return labeled_by;
    }

    public void setLabeled_by(String labeled_by) {
        this.labeled_by = labeled_by;
    }

    public String getInspection_status() {
        return inspection_status;
    }

    public void setInspection_status(String inspection_status) {
        this.inspection_status = inspection_status;
    }

    public String getAsset_status() {
        return asset_status;
    }

    public void setAsset_status(String asset_status) {
        this.asset_status = asset_status;
    }

    public String getInput_value() {
        if (input_type != null) {
            if (input_type.equalsIgnoreCase("uin"))
                return mdata_uin;
            else if (input_type.equalsIgnoreCase("RFID"))
                return mdata_uin;
            else return mdata_barcode;
        } else {
            return "";
        }
    }

    public Profile_Model getUserInfo() {
        if (userInfo != null && !userInfo.equals("")) {
            String data = AppUtils.getGson().toJson(userInfo);
            return AppUtils.getGson().fromJson(data, Profile_Model.class);
        } else return null;
    }

    public void setUserInfo(Profile_Model userInfo) {
        this.userInfo = userInfo;
    }
    public static class Site_Model_Deserial implements JsonDeserializer<Site_Model> {
        String input_type;

        public Site_Model_Deserial(String input_type) {
            this.input_type = input_type;
        }

        @Override
        public Site_Model deserialize(JsonElement arg0, Type arg1, JsonDeserializationContext arg2) throws
                JsonParseException {
            Gson gson = new Gson();
            Site_Model decode = gson.fromJson(arg0, Site_Model.class);
            decode.setInput_type(input_type);
            return decode;
        }
    }

}
