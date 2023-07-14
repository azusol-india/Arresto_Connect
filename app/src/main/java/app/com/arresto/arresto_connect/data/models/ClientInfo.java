package app.com.arresto.arresto_connect.data.models;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientInfo {
    @SerializedName("customer_id")
    @Expose
    private String customerId;
    @SerializedName("customer_uacc_fk")
    @Expose
    private String customerUaccFk;
    @SerializedName("customer_company_name")
    @Expose
    private String customerCompanyName;
    @SerializedName("customer_company_slug")
    @Expose
    private String customerCompanySlug;
    @SerializedName("customer_logo")
    @Expose
    private String customerLogo;
    @SerializedName("customer_logo_path")
    @Expose
    private String customerLogoPath;
    @SerializedName("customer_theme_color")
    @Expose
    private String customerThemeColor;
    @SerializedName("customer_txt_color")
    @Expose
    private String customerTxtColor;
    @SerializedName("customer_address")
    @Expose
    private String customerAddress;
    @SerializedName("customer_address_new")
    @Expose
    private Object customerAddressNew;
    @SerializedName("customer_district")
    @Expose
    private String customerDistrict;
    @SerializedName("customer_city")
    @Expose
    private String customerCity;
    @SerializedName("customer_state")
    @Expose
    private String customerState;
    @SerializedName("customer_country")
    @Expose
    private String customerCountry;
    @SerializedName("customer_site")
    @Expose
    private String customerSite;
    @SerializedName("customer_reply_email")
    @Expose
    private String customerReplyEmail;
    @SerializedName("customer_contact_person")
    @Expose
    private String customerContactPerson;
    @SerializedName("customer_designation")
    @Expose
    private String customerDesignation;
    @SerializedName("customer_email")
    @Expose
    private String customerEmail;
    @SerializedName("customer_phone")
    @Expose
    private String customerPhone;
    @SerializedName("customer_gst")
    @Expose
    private String customerGst;
    @SerializedName("customer_plan")
    @Expose
    private String customerPlan;
    @SerializedName("activation_flag")
    @Expose
    private String activationFlag;
    @SerializedName("payment_flag")
    @Expose
    private String paymentFlag;
    @SerializedName("auth_key")
    @Expose
    private String authKey;
    @SerializedName("auth_by")
    @Expose
    private String authBy;
    @SerializedName("timestamp")
    @Expose
    private String timestamp;
    @SerializedName("customer_ip")
    @Expose
    private String customerIp;
    @SerializedName("invoice_no")
    @Expose
    private String invoiceNo;
    @SerializedName("customer_licence_no")
    @Expose
    private String customerLicenceNo;
    @SerializedName("licence_status")
    @Expose
    private String licenceStatus;
    @SerializedName("cusomer_licence_count")
    @Expose
    private String cusomerLicenceCount;
    @SerializedName("invite_id")
    @Expose
    private String inviteId;
    @SerializedName("advt_image")
    @Expose
    private String advtImage;
    @SerializedName("advt_url")
    @Expose
    private String advtUrl;
    @SerializedName("kare_geofencing_radious")
    @Expose
    private String kareGeofencingRadious;
    @SerializedName("ram_geofencing_radious")
    @Expose
    private String ramGeofencingRadious;
    @SerializedName("ram_geofencing_flag")
    @Expose
    private String ramGeofencingFlag;
    @SerializedName("customer_languages")
    @Expose
    private String customerLanguages;
    @SerializedName("customer_qr_code")
    @Expose
    private String customerQrCode;
    @SerializedName("default_languages")
    @Expose
    private String defaultLanguages;
    @SerializedName("bucket_name")
    @Expose
    private String bucketName;
    @SerializedName("android_app")
    @Expose
    private String androidApp;
    @SerializedName("ios_app")
    @Expose
    private String iosApp;
    @SerializedName("authority_title")
    @Expose
    private String authorityTitle;
    @SerializedName("authority_sign")
    @Expose
    private String authoritySign;
    @SerializedName("authority_designation")
    @Expose
    private String authorityDesignation;

    @SerializedName("web_link")
    @Expose
    private String web_link;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getCustomerUaccFk() {
        return customerUaccFk;
    }

    public void setCustomerUaccFk(String customerUaccFk) {
        this.customerUaccFk = customerUaccFk;
    }

    public String getCustomerCompanyName() {
        return customerCompanyName;
    }

    public void setCustomerCompanyName(String customerCompanyName) {
        this.customerCompanyName = customerCompanyName;
    }

    public String getCustomerCompanySlug() {
        return customerCompanySlug;
    }

    public void setCustomerCompanySlug(String customerCompanySlug) {
        this.customerCompanySlug = customerCompanySlug;
    }

    public String getCustomerLogo() {
        return customerLogo;
    }

    public void setCustomerLogo(String customerLogo) {
        this.customerLogo = customerLogo;
    }

    public String getCustomerLogoPath() {
        return customerLogoPath;
    }

    public void setCustomerLogoPath(String customerLogoPath) {
        this.customerLogoPath = customerLogoPath;
    }

    public String getCustomerThemeColor() {
        return customerThemeColor;
    }

    public void setCustomerThemeColor(String customerThemeColor) {
        this.customerThemeColor = customerThemeColor;
    }

    public String getCustomerTxtColor() {
        return customerTxtColor;
    }

    public void setCustomerTxtColor(String customerTxtColor) {
        this.customerTxtColor = customerTxtColor;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public CustomerAddressNew getCustomerAddressNew() {
        if (!customerAddressNew.equals("")) {
            try {
                JSONObject obj;
                Gson gson = new Gson();
                if (customerAddressNew instanceof String) {
                    obj = new JSONObject(customerAddressNew.toString());
                    return gson.fromJson(obj.toString(), ClientInfo.CustomerAddressNew.class);
                } else if (customerAddressNew instanceof LinkedTreeMap) {
                    JsonObject object = gson.toJsonTree(customerAddressNew).getAsJsonObject();
                    return gson.fromJson(object.toString(), ClientInfo.CustomerAddressNew.class);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public void setCustomerAddressNew(Object customerAddressNew) {
        this.customerAddressNew = customerAddressNew;
    }

    public String getCustomerDistrict() {
        return customerDistrict;
    }

    public void setCustomerDistrict(String customerDistrict) {
        this.customerDistrict = customerDistrict;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public String getCustomerCountry() {
        return customerCountry;
    }

    public void setCustomerCountry(String customerCountry) {
        this.customerCountry = customerCountry;
    }

    public String getCustomerSite() {
        return customerSite;
    }

    public void setCustomerSite(String customerSite) {
        this.customerSite = customerSite;
    }

    public String getCustomerReplyEmail() {
        return customerReplyEmail;
    }

    public void setCustomerReplyEmail(String customerReplyEmail) {
        this.customerReplyEmail = customerReplyEmail;
    }

    public String getCustomerContactPerson() {
        return customerContactPerson;
    }

    public void setCustomerContactPerson(String customerContactPerson) {
        this.customerContactPerson = customerContactPerson;
    }

    public String getCustomerDesignation() {
        return customerDesignation;
    }

    public void setCustomerDesignation(String customerDesignation) {
        this.customerDesignation = customerDesignation;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getCustomerGst() {
        return customerGst;
    }

    public void setCustomerGst(String customerGst) {
        this.customerGst = customerGst;
    }

    public String getCustomerPlan() {
        return customerPlan;
    }

    public void setCustomerPlan(String customerPlan) {
        this.customerPlan = customerPlan;
    }

    public String getActivationFlag() {
        return activationFlag;
    }

    public void setActivationFlag(String activationFlag) {
        this.activationFlag = activationFlag;
    }

    public String getPaymentFlag() {
        return paymentFlag;
    }

    public void setPaymentFlag(String paymentFlag) {
        this.paymentFlag = paymentFlag;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthBy() {
        return authBy;
    }

    public void setAuthBy(String authBy) {
        this.authBy = authBy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getCustomerIp() {
        return customerIp;
    }

    public void setCustomerIp(String customerIp) {
        this.customerIp = customerIp;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getCustomerLicenceNo() {
        return customerLicenceNo;
    }

    public void setCustomerLicenceNo(String customerLicenceNo) {
        this.customerLicenceNo = customerLicenceNo;
    }

    public String getLicenceStatus() {
        return licenceStatus;
    }

    public void setLicenceStatus(String licenceStatus) {
        this.licenceStatus = licenceStatus;
    }

    public String getCusomerLicenceCount() {
        return cusomerLicenceCount;
    }

    public void setCusomerLicenceCount(String cusomerLicenceCount) {
        this.cusomerLicenceCount = cusomerLicenceCount;
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }

    public String getAdvtImage() {
        return advtImage;
    }

    public void setAdvtImage(String advtImage) {
        this.advtImage = advtImage;
    }

    public String getAdvtUrl() {
        return advtUrl;
    }

    public void setAdvtUrl(String advtUrl) {
        this.advtUrl = advtUrl;
    }

    public String getKareGeofencingRadious() {
        return kareGeofencingRadious;
    }

    public void setKareGeofencingRadious(String kareGeofencingRadious) {
        this.kareGeofencingRadious = kareGeofencingRadious;
    }

    public String getRamGeofencingRadious() {
        return ramGeofencingRadious;
    }

    public void setRamGeofencingRadious(String ramGeofencingRadious) {
        this.ramGeofencingRadious = ramGeofencingRadious;
    }

    public String getRamGeofencingFlag() {
        return ramGeofencingFlag;
    }

    public void setRamGeofencingFlag(String ramGeofencingFlag) {
        this.ramGeofencingFlag = ramGeofencingFlag;
    }

    public String getCustomerLanguages() {
        return customerLanguages;
    }

    public void setCustomerLanguages(String customerLanguages) {
        this.customerLanguages = customerLanguages;
    }

    public String getCustomerQrCode() {
        return customerQrCode;
    }

    public void setCustomerQrCode(String customerQrCode) {
        this.customerQrCode = customerQrCode;
    }

    public String getDefaultLanguages() {
        return defaultLanguages;
    }

    public void setDefaultLanguages(String defaultLanguages) {
        this.defaultLanguages = defaultLanguages;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getAndroidApp() {
        return androidApp;
    }

    public void setAndroidApp(String androidApp) {
        this.androidApp = androidApp;
    }

    public String getIosApp() {
        return iosApp;
    }

    public void setIosApp(String iosApp) {
        this.iosApp = iosApp;
    }

    public String getAuthorityTitle() {
        return authorityTitle;
    }

    public void setAuthorityTitle(String authorityTitle) {
        this.authorityTitle = authorityTitle;
    }

    public String getAuthoritySign() {
        return authoritySign;
    }

    public void setAuthoritySign(String authoritySign) {
        this.authoritySign = authoritySign;
    }

    public String getAuthorityDesignation() {
        return authorityDesignation;
    }

    public void setAuthorityDesignation(String authorityDesignation) {
        this.authorityDesignation = authorityDesignation;
    }

    public String getWeb_link() {
        return web_link;
    }

    public void setWeb_link(String web_link) {
        this.web_link = web_link;
    }

    public class CustomerAddressNew {

        private String line1;
        private String line2;
        private String state;
        private String pin;
        private String contact_email;

        public String getLine1() {
            return line1;
        }

        public void setLine1(String line1) {
            this.line1 = line1;
        }

        public String getLine2() {
            return line2;
        }

        public void setLine2(String line2) {
            this.line2 = line2;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getPin() {
            return pin;
        }

        public void setPin(String pin) {
            this.pin = pin;
        }

        public String getContact_email() {
            return contact_email;
        }

        public void setContact_email(String contact_email) {
            this.contact_email = contact_email;
        }

    }
}