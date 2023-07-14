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

import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

public class Component_model {
    private String component_id;
    private String component_code;
    private String component_description;
    private String component_uom;
    private String component_expectedresult;
    private String component_observation;
    private String component_repair;
    private String component_inspection;
    private String component_geo_fancing;
    private String component_work_permit;
    private String component_excerpt;
    private String component_inspectiontype;
    private String component_imagepath;
    private String component_lifespan_month;
    private String component_lifespan_hours;
    private String component_frequency_asset;
    private String component_frequency_hours;
    private String component_pdm_frequency;
    private String component_sub_assets;
    private String asm_user_id;
    private String asm_project_id;
    private String freq_hours;
    private String asm_log_hours;
    private String mdata_id;
    private String mdata_uin;
    private String mdata_rfid;
    private String asm_log_seconds;
    private String asm_product_status;
    private String inspection_due_date;
    boolean selected;
    String selectedStatus;


    public String getComponent_id() {
        return component_id;
    }

    public void setComponent_id(String component_id) {
        this.component_id = component_id;
    }


    public void setComponent_code(String component_code) {
        this.component_code = component_code;
    }

    public void setComponent_description(String component_description) {
        this.component_description = component_description;
    }
    public void setComponent_uom(String component_uom) {
        this.component_uom = component_uom;
    }

    public String getComponent_expectedresult() {
        return component_expectedresult;
    }

    public void setComponent_expectedresult(String component_expectedresult) {
        this.component_expectedresult = component_expectedresult;
    }

    public String getComponent_observation() {
        return component_observation;
    }

    public void setComponent_observation(String component_observation) {
        this.component_observation = component_observation;
    }

    public void setComponent_repair(String component_repair) {
        this.component_repair = component_repair;
    }

    public String getComponent_inspection() {
        return component_inspection;
    }

    public void setComponent_inspection(String component_inspection) {
        this.component_inspection = component_inspection;
    }

    public void setComponent_excerpt(String component_excerpt) {
        this.component_excerpt = component_excerpt;
    }

    public void setComponent_inspectiontype(String component_inspectiontype) {
        this.component_inspectiontype = component_inspectiontype;
    }

    public void setComponent_imagepath(String component_imagepath) {
        this.component_imagepath = component_imagepath;
    }

    public String getComponent_lifespan_hours() {
        return component_lifespan_hours;
    }

    public void setComponent_lifespan_hours(String component_lifespan_hours) {
        this.component_lifespan_hours = component_lifespan_hours;
    }


    public String getComponent_lifespan_month() {
        return component_lifespan_month;
    }

    public void setComponent_lifespan_month(String component_lifespan_month) {
        this.component_lifespan_month = component_lifespan_month;
    }

    public String getComponent_frequency_asset() {
        return component_frequency_asset;
    }

    public void setComponent_frequency_asset(String component_frequency_asset) {
        this.component_frequency_asset = component_frequency_asset;
    }

    public String getComponent_frequency_hours() {
        return component_frequency_hours;
    }

    public void setComponent_frequency_hours(String component_frequency_hours) {
        this.component_frequency_hours = component_frequency_hours;
    }

    public String getComponent_pdm_frequency() {
        return component_pdm_frequency;
    }

    public void setComponent_pdm_frequency(String component_pdm_frequency) {
        this.component_pdm_frequency = component_pdm_frequency;
    }

    public String getComponent_sub_assets() {
        return component_sub_assets;
    }

    public void setComponent_sub_assets(String component_sub_assets) {
        this.component_sub_assets = component_sub_assets;
    }

    public String getAsm_user_id() {
        return asm_user_id;
    }

    public void setAsm_user_id(String asm_user_id) {
        this.asm_user_id = asm_user_id;
    }

    public String getAsm_project_id() {
        return asm_project_id;
    }

    public void setAsm_project_id(String asm_project_id) {
        this.asm_project_id = asm_project_id;
    }

    public String getMdata_id() {
        return mdata_id;
    }

    public void setMdata_id(String mdata_id) {
        this.mdata_id = mdata_id;
    }


    public String getMdata_uin() {
        return mdata_uin;
    }

    public void setMdata_uin(String mdata_uin) {
        this.mdata_uin = mdata_uin;
    }


    public String getMdata_rfid() {
        return mdata_rfid;
    }

    public void setMdata_rfid(String mdata_rfid) {
        this.mdata_rfid = mdata_rfid;
    }

    public String getFreq_hours() {
        return freq_hours;
    }

    public void setFreq_hours(String freq_hours) {
        this.freq_hours = freq_hours;
    }

    public String getAsm_log_hours() {
        return asm_log_hours;
    }

    public void setAsm_log_hours(String asm_log_hours) {
        this.asm_log_hours = asm_log_hours;
    }


    public String getAsm_log_seconds() {
        return asm_log_seconds;
    }

    public void setAsm_log_seconds(String asm_log_seconds) {
        this.asm_log_seconds = asm_log_seconds;
    }


    public String getAsm_product_status() {
        return asm_product_status;
    }

    public void setAsm_product_status(String asm_product_status) {
        this.asm_product_status = asm_product_status;
    }
    public String getInspection_due_date() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(inspection_due_date));
        } catch (ParseException e) {
            e.printStackTrace();
            return inspection_due_date;
        }
    }

    public void setInspection_due_date(String inspection_due_date) {
        this.inspection_due_date = inspection_due_date;
    }

    public String getComponent_code() {
        return component_code;
    }

    public String getComponent_description() {
        return component_description;
    }

    public String getComponent_uom() {
        return component_uom;
    }

    public String getComponent_repair() {
        return component_repair;
    }

    public String getComponent_geo_fancing() {
        return component_geo_fancing;
    }

    public void setComponent_geo_fancing(String component_geo_fancing) {
        this.component_geo_fancing = component_geo_fancing;
    }

    public String getComponent_work_permit() {
        return component_work_permit;
    }

    public void setComponent_work_permit(String component_work_permit) {
        this.component_work_permit = component_work_permit;
    }

    public String getComponent_excerpt() {
        return component_excerpt;
    }

    public String getComponent_inspectiontype() {
        return component_inspectiontype;
    }

    public String getComponent_imagepath() {
        return component_imagepath;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getSelectedStatus() {
        return selectedStatus;
    }

    public void setSelectedStatus(String selectedStatus) {
        this.selectedStatus = selectedStatus;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
