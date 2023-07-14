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


import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

public class Product_model {

    @SerializedName("product_id")
    private String product_id;

    @SerializedName(value = "product_code", alternate = {"code", "name"})
    private String product_code;
    @SerializedName(value = "product_description", alternate = {"description"})
    private String product_description;
    @SerializedName(value = "product_inspectiontype", alternate = {"type"})
    private String type;
    @SerializedName("status")
    private String status;
    @SerializedName(value = "product_imagepath", alternate = {"url"})
    private String product_imagepath = "";
    @SerializedName("product_components")
    private String product_components;


    @SerializedName("product_geo_fancing")
    private String product_geo_fancing;

    @SerializedName("product_work_permit")
    private String product_work_permit;

    @SerializedName("product_frequency_asset")
    private String product_frequency_asset;

    @SerializedName("standard_certificate_id")
    private String standard_certificate_id;

    @SerializedName("notified_body_certificate_id")
    private String notified_body_certificate_id;

    @SerializedName("article_11b_certificate_id")
    private String article_11b_certificate_id;

    @SerializedName("ec_type_certificate_text")
    private String ec_type_certificate_text;

    @SerializedName("product_inspectiontype_id")
    private String product_inspectiontype_id;

    @SerializedName("infonet_status")
    private String infonet_status;

    @SerializedName("cat_id")
    private String cat_id;

    public String getCat_id() {
        return cat_id;
    }

    public void setCat_id(String cat_id) {
        this.cat_id = cat_id;
    }

    public String getProduct_code() {
        return product_code;
    }

    public String getProduct_description() {
        if (product_description != null) {
            return product_description;
        } else return "";
    }

    public void setProduct_description(String desc) {
        product_description = desc;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setProductName(String name) {
        this.product_code = name;
    }

    public void setProductImage(String url) {
        this.product_imagepath = url;
    }

    public String getStatus() {
        return status;
    }

    public String getProduct_imagepath() {
        return product_imagepath;
    }

    public String getProduct_components() {
        return product_components;
    }

    public String getProduct_id() {
        return product_id;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public void setProduct_components(String product_components) {
        this.product_components = product_components;
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

    public String getProduct_frequency_asset() {
        return product_frequency_asset;
    }

    public void setProduct_frequency_asset(String product_frequency_asset) {
        this.product_frequency_asset = product_frequency_asset;
    }

    public String getStandard_certificate_id() {
        return standard_certificate_id;
    }

    public void setStandard_certificate_id(String standard_certificate_id) {
        this.standard_certificate_id = standard_certificate_id;
    }

    public String getNotified_body_certificate_id() {
        return notified_body_certificate_id;
    }

    public void setNotified_body_certificate_id(String notified_body_certificate_id) {
        this.notified_body_certificate_id = notified_body_certificate_id;
    }

    public String getArticle_11b_certificate_id() {
        return article_11b_certificate_id;
    }

    public void setArticle_11b_certificate_id(String article_11b_certificate_id) {
        this.article_11b_certificate_id = article_11b_certificate_id;
    }

    public String getEc_type_certificate_text() {
        return ec_type_certificate_text;
    }

    public void setEc_type_certificate_text(String ec_type_certificate_text) {
        this.ec_type_certificate_text = ec_type_certificate_text;
    }

    public String getProduct_inspectiontype_id() {
        return product_inspectiontype_id;
    }

    public void setProduct_inspectiontype_id(String product_inspectiontype_id) {
        this.product_inspectiontype_id = product_inspectiontype_id;
    }

    public String getInfonet_status() {
        return infonet_status;
    }

    public void setInfonet_status(String infonet_status) {
        this.infonet_status = infonet_status;
    }

    @NonNull
    @Override
    public String toString() {
        return product_code;
    }
}
