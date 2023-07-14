package app.com.arresto.arresto_connect.data.models;

import android.view.View;

import org.json.JSONObject;

import app.com.arresto.arresto_connect.constants.CaptureSignatureView;

public class Signature_objcet {
    JSONObject field_data;
    View  sign_view;
    CaptureSignatureView captureObject;

    public JSONObject getField_data() {
        return field_data;
    }

    public void setField_data(JSONObject field_data) {
        this.field_data = field_data;
    }

    public View getSign_view() {
        return sign_view;
    }

    public void setSign_view(View sign_view) {
        this.sign_view = sign_view;
    }

    public CaptureSignatureView getCaptureObject() {
        return captureObject;
    }

    public void setCaptureObject(CaptureSignatureView captureObject) {
        this.captureObject = captureObject;
    }
}
