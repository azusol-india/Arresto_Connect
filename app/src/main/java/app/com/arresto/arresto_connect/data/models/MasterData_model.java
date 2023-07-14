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

public class MasterData_model {
    private String mdata_id;
    private String mdata_batch;
    private String mdata_serial;
    private String mdata_rfid;
    private String mdata_barcode;
    private String mdata_uin;
    private String mdata_po;
    private String mdata_client;
    private String mdata_company_id;
    private String mdata_item_series;
    private String mdata_material_invoice;
    private String mdata_material_invoice_date;
    private String mdata_asset;
    private String date_of_first_use;
    private String date_of_inspection;
    private String inspection_due_date;
    private String assetCodes;
    private String product_geo_fancing;
    private String product_work_permit;
    private String mdata_item_series_image;
    private String ref_user_id;
    private String pdm_inspection_date;
    private String pdm_due_date;
    private String mdata_jobcard;
    private String mdata_sms;
    private String sensor_id;
    private String is_breakdown;
    private String mdata_breakdown_remark;
    private String mdata_location;
    private String product_repair;
    private String component_inspection;
    private Data ref_inspection;
    private String pdm_report_status;
    private String is_confirmed;
    private String mdata_client_id;
    private String preuse_time;
    private String inspection_due_period;
    private String mdata_make;
    private String mdata_swl;
    private String form11_remark;
    private String fm13_maker_address;
    private String fm13_process;
    private String fm13_thickness;
    private String fm13_safe_pressure;
    int not_make_inspected;

    public String getPdm_inspection_date() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(pdm_inspection_date));
        } catch (ParseException e) {
            e.printStackTrace();
            return pdm_inspection_date;
        }
    }

    public void setPdm_inspection_date(String pdm_inspection_date) {
        this.pdm_inspection_date = pdm_inspection_date;
    }

    public String getMdata_material_invoice_date() {
        return mdata_material_invoice_date;
    }

    public void setMdata_material_invoice_date(String mdata_material_invoice_date) {
        this.mdata_material_invoice_date = mdata_material_invoice_date;
    }

    public String getPdm_due_date() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(pdm_due_date));
        } catch (ParseException e) {
            e.printStackTrace();
            return pdm_due_date;
        }
//        return pdm_due_date;
    }

    public String getPdm_report_status() {
        return pdm_report_status;
    }

    public void setPdm_report_status(String pdm_report_status) {
        this.pdm_report_status = pdm_report_status;
    }

    public void setPdm_due_date(String pdm_due_date) {
        this.pdm_due_date = pdm_due_date;
    }

    public String getMdata_company_id() {
        return mdata_company_id;
    }

    public void setMdata_company_id(String mdata_company_id) {
        this.mdata_company_id = mdata_company_id;
    }

    public String getMdata_sms() {
        return mdata_sms;
    }

    public void setMdata_sms(String mdata_sms) {
        this.mdata_sms = mdata_sms;
    }


    public String getMdata_jobcard() {
        return mdata_jobcard;
    }

    public void setMdata_jobcard(String mdata_jobcard) {
        this.mdata_jobcard = mdata_jobcard;
    }

    public String getMdata_client() {
        return mdata_client;
    }

    public void setMdata_client(String mdata_client) {
        this.mdata_client = mdata_client;
    }

    public String getComponent_inspection() {
        return component_inspection;
    }

    public void setComponent_inspection(String component_inspection) {
        this.component_inspection = component_inspection;
    }

    public String getProduct_repair() {
        return product_repair;
    }

    public void setProduct_repair(String product_repair) {
        this.product_repair = product_repair;
    }

    public String getRef_user_id() {
        return ref_user_id;
    }

    public void setRef_user_id(String ref_user_id) {
        this.ref_user_id = ref_user_id;
    }

    public Data getRef_inspection() {
        return ref_inspection;
    }

    public void setRef_inspection(Data ref_inspection) {
        this.ref_inspection = ref_inspection;
    }

    public String getMdata_id() {
        return mdata_id;
    }

    public void setMdata_id(String mdata_id) {
        this.mdata_id = mdata_id;
    }

    public String getSensor_id() {
        return sensor_id;
    }

    public void setSensor_id(String sensor_id) {
        this.sensor_id = sensor_id;
    }

    public String getMdata_batch() {
        return mdata_batch;
    }

    public void setMdata_batch(String mdata_batch) {
        this.mdata_batch = mdata_batch;
    }

    public String getMdata_serial() {
        return mdata_serial;
    }

    public void setMdata_serial(String mdata_serial) {
        this.mdata_serial = mdata_serial;
    }

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

    public String getMdata_po() {
        return mdata_po;
    }

    public void setMdata_po(String mdata_po) {
        this.mdata_po = mdata_po;
    }

    public String getMdata_item_series() {
        return mdata_item_series.replaceAll("\"|\\[|\\]", "");
    }

    public void setMdata_item_series(String mdata_item_series) {
        this.mdata_item_series = mdata_item_series;
    }

    public String getMdata_material_invoice() {
        return mdata_material_invoice;
    }

    public void setMdata_material_invoice(String mdata_material_invoice) {
        this.mdata_material_invoice = mdata_material_invoice;
    }

    public String getIs_breakdown() {
        return is_breakdown;
    }

    public void setIs_breakdown(String is_breakdown) {
        this.is_breakdown = is_breakdown;
    }

    public String getMdata_breakdown_remark() {
        return mdata_breakdown_remark;
    }

    public void setMdata_breakdown_remark(String mdata_breakdown_remark) {
        this.mdata_breakdown_remark = mdata_breakdown_remark;
    }

    public String getMdata_location() {
        return mdata_location;
    }

    public void setMdata_location(String mdata_location) {
        this.mdata_location = mdata_location;
    }

    public String getIs_confirmed() {
        return is_confirmed;
    }

    public void setIs_confirmed(String is_confirmed) {
        this.is_confirmed = is_confirmed;
    }

    public String getPreuse_time() {
        return preuse_time;
    }

    public void setPreuse_time(String preuse_time) {
        this.preuse_time = preuse_time;
    }

    public String getMdata_asset() {
        return mdata_asset.replaceAll("\"|\\[|\\]", "");
    }

    public void setMdata_asset(String mdata_asset) {
        this.mdata_asset = mdata_asset;
    }

    public String getDate_of_first_use() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(date_of_first_use));
        } catch (ParseException e) {
            e.printStackTrace();
            return date_of_first_use;
        }
//        return date_of_first_use;
    }

    public void setDate_of_first_use(String date_of_first_use) {
        this.date_of_first_use = date_of_first_use;
    }

    public String getDate_of_inspection() {
        return date_of_inspection;
    }

    public void setDate_of_inspection(String date_of_inspection) {
        this.date_of_inspection = date_of_inspection;
    }

    public String getInspection_due_date() {
        try {
            return BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(inspection_due_date));
        } catch (ParseException e) {
            e.printStackTrace();
            return inspection_due_date;
        }
//        return inspection_due_date;
    }

    public void setInspection_due_date(String inspection_due_date) {
        this.inspection_due_date = inspection_due_date;
    }

    public String getAssetCodes() {
        return assetCodes;
    }

    public void setAssetCodes(String assetCodes) {
        this.assetCodes = assetCodes;
    }

    public String getProduct_geo_fancing() {
        return product_geo_fancing;
    }

    public void setProduct_geo_fancing(String product_geo_fancing) {
        this.product_geo_fancing = product_geo_fancing;
    }

    public String getProduct_work_permit() {
        return product_work_permit;
    }

    public void setProduct_work_permit(String product_work_permit) {
        this.product_work_permit = product_work_permit;
    }

    public String getMdata_client_id() {
        return mdata_client_id;
    }

    public void setMdata_client_id(String mdata_client_id) {
        this.mdata_client_id = mdata_client_id;
    }

    public String getMdata_item_series_image() {
        return mdata_item_series_image;
    }

    public void setMdata_item_series_image(String mdata_item_series_image) {
        this.mdata_item_series_image = mdata_item_series_image;
    }

    public String getInspection_due_period() {
        return inspection_due_period;
    }

    public void setInspection_due_period(String inspection_due_period) {
        this.inspection_due_period = inspection_due_period;
    }

    public int getNot_make_inspected() {
        return not_make_inspected;
    }

    public void setNot_make_inspected(int not_make_inspected) {
        this.not_make_inspected = not_make_inspected;
    }

    public String getMdata_make() {
        return mdata_make;
    }

    public void setMdata_make(String mdata_make) {
        this.mdata_make = mdata_make;
    }

    public String getMdata_swl() {
        return mdata_swl;
    }

    public void setMdata_swl(String mdata_swl) {
        this.mdata_swl = mdata_swl;
    }

    public String getForm11_remark() {
        return form11_remark;
    }

    public void setForm11_remark(String form11_remark) {
        this.form11_remark = form11_remark;
    }

    public String getFm13_maker_address() {
        return fm13_maker_address;
    }

    public void setFm13_maker_address(String fm13_maker_address) {
        this.fm13_maker_address = fm13_maker_address;
    }

    public String getFm13_process() {
        return fm13_process;
    }

    public void setFm13_process(String fm13_process) {
        this.fm13_process = fm13_process;
    }

    public String getFm13_thickness() {
        return fm13_thickness;
    }

    public void setFm13_thickness(String fm13_thickness) {
        this.fm13_thickness = fm13_thickness;
    }

    public String getFm13_safe_pressure() {
        return fm13_safe_pressure;
    }

    public void setFm13_safe_pressure(String fm13_safe_pressure) {
        this.fm13_safe_pressure = fm13_safe_pressure;
    }

    public class Data {
        String s_user_id;
        String s_site_id;

        public String getS_user_id() {
            return s_user_id;
        }

        public void setS_user_id(String s_user_id) {
            this.s_user_id = s_user_id;
        }

        public String getS_site_id() {
            return s_site_id;
        }

        public void setS_site_id(String s_site_id) {
            this.s_site_id = s_site_id;
        }

        public String getS_scheduler_date() {
            return s_scheduler_date;
        }

        public void setS_scheduler_date(String s_scheduler_date) {
            this.s_scheduler_date = s_scheduler_date;
        }

        String s_scheduler_date;
    }


}
