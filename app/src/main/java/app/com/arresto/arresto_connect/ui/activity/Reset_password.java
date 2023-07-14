/*
 * <!--
 *   ~  Copyright (c)
 *   ~  @website: http://runpro.co.in/
 *   ~  @author: RunPro
 *   ~  @license:  http://runpro.co.in/
 *   ~
 *   ~  The below module/code/specifications belong to RunPro solely.
 *   -->
 *
 */

package app.com.arresto.arresto_connect.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import app.com.arresto.Flavor_Id.FlavourInfo;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;

public class Reset_password extends BaseActivity {
    String email;
    public String pass_str;
    public String cnfpass_str;
    EditText otp_edt, newpass_edt, cnf_pass_edt;
    ImageView back_btn;
    public MaterialButton reset_btn;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.reset_password_lay;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        email = getIntent().getStringExtra("email");

        find_all_id();
        back_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pass_str = newpass_edt.getText().toString();
                cnfpass_str = cnf_pass_edt.getText().toString();
                if (validatePassword(cnf_pass_edt) && !isEmpty(otp_edt)) {
                    if (!cnfpass_str.equals(pass_str)) {
                        show_snak(Reset_password.this, getResString("lbl_match_pass"));
                    } else {
                        if (AppUtils.isNetworkAvailable(Reset_password.this)) {
                            make_get_request();
                        } else {
                            show_snak(Reset_password.this, getResString("lbl_network_alert"));
                        }
                    }
                }
            }
        });
    }


    public void find_all_id() {
        back_btn = findViewById(R.id.back_icon);
        reset_btn = findViewById(R.id.reset_btn);
        otp_edt = findViewById(R.id.otp_edt);
        newpass_edt = findViewById(R.id.newpass_edt);
        cnf_pass_edt = findViewById(R.id.cnf_pass_edt);
        new CustomTextWatcher(otp_edt, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                if (text != null && text.length() > 3) {
                    ViewCompat.setBackgroundTintList(reset_btn, ColorStateList.valueOf(getResColor(R.color.button_bg)));
                } else {
                    ViewCompat.setBackgroundTintList(reset_btn, ColorStateList.valueOf(getResColor(R.color.grey)));
                }
            }
        });
    }

    public void make_get_request() {
        NetworkRequest network_request = new NetworkRequest(this);
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("client_id", FlavourInfo.Fl_CLIENT_ID);
            params.put("new_pass", pass_str);
            params.put("otp", otp_edt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        network_request.make_contentpost_request(All_Api.reset_pass, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", " is " + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg_code = jsonObject.getString("status_code");
                            AppUtils.show_snak(Reset_password.this, jsonObject.getString("message"));
                            if (msg_code.equals("200")) {
                                new NetworkRequest().save_logs(email + " reset their password on Device " + Build.BRAND + " " + Build.MODEL);
                                Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
                                startActivity(intent1);
                                finish();
                            }
                        }
                    } else if (json instanceof JSONArray) {

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
