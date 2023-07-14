package app.com.arresto.arresto_connect.data.models;

import java.util.List;

public class AddAssetModel {
    private String client_id;
    private String user_id;
    private String asset_code;
    private String series_code;
    private String description;
    private String uom;
    private String inspection_type;
    private List<String> sub_assets;
    private List<String> assets;
    private List<String> results;
    private List<String> observations;
    private String frequency_asset;
    private String frequency_series;
    private String frequency_hours;
    private String pdm_frequency;
    private String pdm_due_date;
    private String repair;
    private String component_inspection;
    private String geo_fancing;
    private String work_permit;
    private String standard;
    private String body;
    private String certificate;
    private String certificate_text;
    private String jobcard;
    private String sms;
    private String batch;
    private String serial;
    private String uin;
    private String rfid;
    private String barcode;
    private String mdata_client;
    private String invoice_date;
    private String date_of_first_use;
    private String lifespan_hours;
    private String lifespan_month;
    private String asset_image;
    private String series_image;
    private String mdata_location;
    private String mdata_status;
    private String master_id;
    private String type;

    private String mdata_make;
    private String mdata_swl;
    private String form11_remark;

    private String fm13_maker_address;
    private String fm13_process;
    private String fm13_thickness;
    private String fm13_safe_pressure;

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getAsset_code() {
        return asset_code;
    }

    public void setAsset_code(String asset_code) {
        this.asset_code = asset_code;
    }

    public String getSeries_code() {
        return series_code;
    }

    public void setSeries_code(String series_code) {
        this.series_code = series_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSub_assets() {
        return sub_assets;
    }

    public void setSub_assets(List<String> sub_assets) {
        this.sub_assets = sub_assets;
    }

    public List<String> getAssets() {
        return assets;
    }

    public void setAssets(List<String> assets) {
        this.assets = assets;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getInspection_type() {
        return inspection_type;
    }

    public void setInspection_type(String inspection_type) {
        this.inspection_type = inspection_type;
    }

    public List<String> getResults() {
        return results;
    }

    public void setResults(List<String> results) {
        this.results = results;
    }

    public List<String> getObservations() {
        return observations;
    }

    public void setObservations(List<String> observations) {
        this.observations = observations;
    }

    public String getFrequency_hours() {
        return frequency_hours;
    }

    public void setFrequency_hours(String frequency_hours) {
        this.frequency_hours = frequency_hours;
    }

    public String getFrequency_asset() {
        return frequency_asset;
    }

    public void setFrequency_asset(String frequency_asset) {
        this.frequency_asset = frequency_asset;
    }

    public String getFrequency_series() {
        return frequency_series;
    }

    public void setFrequency_series(String frequency_series) {
        this.frequency_series = frequency_series;
    }

    public String getPdm_frequency() {
        return pdm_frequency;
    }

    public void setPdm_frequency(String pdm_frequency) {
        this.pdm_frequency = pdm_frequency;
    }

    public String getPdm_due_date() {
        return pdm_due_date;
    }

    public void setPdm_due_date(String pdm_due_date) {
        this.pdm_due_date = pdm_due_date;
    }

    public String getRepair() {
        return repair;
    }

    public void setRepair(String repair) {
        this.repair = repair;
    }

    public String getComponent_inspection() {
        return component_inspection;
    }

    public void setComponent_inspection(String component_inspection) {
        this.component_inspection = component_inspection;
    }

    public String getGeo_fancing() {
        return geo_fancing;
    }

    public void setGeo_fancing(String geo_fancing) {
        this.geo_fancing = geo_fancing;
    }

    public String getWork_permit() {
        return work_permit;
    }

    public void setWork_permit(String work_permit) {
        this.work_permit = work_permit;
    }

    public String getStandard() {
        return standard;
    }

    public void setStandard(String standard) {
        this.standard = standard;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getCertificate() {
        return certificate;
    }

    public void setCertificate(String certificate) {
        this.certificate = certificate;
    }

    public String getCertificate_text() {
        return certificate_text;
    }

    public void setCertificate_text(String certificate_text) {
        this.certificate_text = certificate_text;
    }

    public String getJobcard() {
        return jobcard;
    }

    public void setJobcard(String jobcard) {
        this.jobcard = jobcard;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getUin() {
        return uin;
    }

    public void setUin(String uin) {
        this.uin = uin;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getMdata_location() {
        return mdata_location;
    }

    public void setMdata_location(String mdata_location) {
        this.mdata_location = mdata_location;
    }

    public String getMdata_client() {
        return mdata_client;
    }

    public void setMdata_client(String mdata_client) {
        this.mdata_client = mdata_client;
    }

    public String getInvoice_date() {
        return invoice_date;
    }

    public void setInvoice_date(String invoice_date) {
        this.invoice_date = invoice_date;
    }

    public String getDate_of_first_use() {
        return date_of_first_use;
    }

    public void setDate_of_first_use(String date_of_first_use) {
        this.date_of_first_use = date_of_first_use;
    }

    public String getLifespan_hours() {
        return lifespan_hours;
    }

    public void setLifespan_hours(String lifespan_hours) {
        this.lifespan_hours = lifespan_hours;
    }

    public String getLifespan_month() {
        return lifespan_month;
    }

    public void setLifespan_month(String lifespan_month) {
        this.lifespan_month = lifespan_month;
    }

    public String getAsset_image() {
        return asset_image;
    }

    public void setAsset_image(String asset_image) {
        this.asset_image = asset_image;
    }

    public String getSeries_image() {
        return series_image;
    }

    public void setSeries_image(String series_image) {
        this.series_image = series_image;
    }

    public String getMdata_status() {
        return mdata_status;
    }

    public void setMdata_status(String mdata_status) {
        this.mdata_status = mdata_status;
    }

    public String getMaster_id() {
        return master_id;
    }

    public void setMaster_id(String master_id) {
        this.master_id = master_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    static AddAssetModel addAssetModel;

    public static AddAssetModel getInstance() {
        if (addAssetModel == null)
            addAssetModel = new AddAssetModel();
        return addAssetModel;
    }

    public static AddAssetModel getNewInstance() {
        addAssetModel = new AddAssetModel();
        return addAssetModel;
    }
}
