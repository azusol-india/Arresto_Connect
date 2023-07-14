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

package app.com.arresto.arresto_connect.ui.activity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.ViewCompat;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;

public class Forgot_paswrd_activity extends BaseActivity {
    MaterialButton reset_btn;
    String url, email;
    EditText email_edt;
    ImageView back_btn;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.forgot_pasword_activity;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        email = getIntent().getStringExtra("email");
        find_all_id();
        if (email.equals("")) {
            email_edt.setText(email);
            ViewCompat.setBackgroundTintList(reset_btn, ColorStateList.valueOf(getResColor(R.color.button_bg)));
        }
        back_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onBackPressed();
            }
        });
        reset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = email_edt.getText().toString();
                if (email.equals("")) {
                    Toast.makeText(getApplicationContext(), getResString("lbl_email_alert"), Toast.LENGTH_LONG).show();
                } else {
                    if (isNetworkAvailable(Forgot_paswrd_activity.this)) {
                        make_get_request();
                    } else {
                        Toast.makeText(getApplicationContext(), getResString("lbl_network_alert"), Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        if (!logo_url.equals("")) {
            ImageView logo_img = findViewById(R.id.logo_img);
            load_image_file(logo_url, logo_img);
        }
        show_showCase();
    }


    private void show_showCase() {
        ArrayList<String> mesages = new ArrayList<>(Arrays.asList("Reset your password"));
        ArrayList<View> views = new ArrayList<>(Arrays.asList(reset_btn));
        Add_Showcase.newInstance(this).setData(mesages, views);
    }


    public void find_all_id() {
        back_btn = findViewById(R.id.back1);
        reset_btn = findViewById(R.id.rest_btn);
        email_edt = findViewById(R.id.forgt_email);
        new CustomTextWatcher(email_edt, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                if (text != null && text.length() > 4) {
                    if (validateEmail(view)) {
//                    ViewCompat.setBackgroundTintList(view, ColorStateList.valueOf(Dynamic_Var.getInstance().getBtn_bg_clr()));
                        ViewCompat.setBackgroundTintList(reset_btn, ColorStateList.valueOf(getResColor(R.color.button_bg)));
                    } else {
                        ViewCompat.setBackgroundTintList(reset_btn, ColorStateList.valueOf(getResColor(R.color.grey)));
                    }
                }
            }
        });
    }


    public void make_get_request() {
        url = All_Api.forget_URL + email;
        new NetworkRequest(this).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", " is " + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        Log.e("error", "" + response);
                        JSONObject jsonObject1 = new JSONObject(response);
                        String msg = jsonObject1.getString("message");
                        String msg_code = "200";
                        if (jsonObject1.has("status_code"))
                            msg_code = jsonObject1.getString("status_code");
                        show_snak(Forgot_paswrd_activity.this, msg);
                        if (msg_code.equals("200")) {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent1 = new Intent(getApplicationContext(), Reset_password.class);
                                    intent1.putExtra("email", email);
                                    startActivity(intent1);
                                    finish();
                                }
                            }, 2000);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {

            }
        });
    }
}
