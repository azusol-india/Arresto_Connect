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

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResDrawable;
import static app.com.arresto.arresto_connect.constants.AppUtils.isLogin;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.server_alert;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;
import static app.com.arresto.arresto_connect.network.All_Api.checkServer_api;

import java.util.Scanner;

public class SplashActivity extends BaseActivity {

    public static final int ACCESS_FINE_LOCATION = 1, READ_EXTERNAL_STORAGE = 2;
    private static String[] PERMISSIONS_ACCESS_FINE_LOCATION = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE};
    String uData;
    Thread thread = null;
    boolean isHandle = false;
    int msg_what = 1;

    @Override
    protected int getLayoutResourceId() {
        setAppTheme();
        return 0;
    }

    //    int width, height;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Uri data = intent.getData();
        if (data != null) {
            uData = data.toString();
            go_for_next();
        } else {
            startSplash();
        }
    }

    void startSplash() {
        setContentView(R.layout.activity_splash);
        ImageView root = findViewById(R.id.root_layer);
        if (client_id.equals("952")) {
            root.setVisibility(View.VISIBLE);
            root.setImageDrawable(getResDrawable(R.drawable.ks_splash));
        } else if (client_id.equals("376") || client_id.equals("786")) {
            root.setVisibility(View.VISIBLE);
            root.setImageDrawable(getResDrawable(R.drawable.kare_splash));
        } else if (client_id.equals("395")) {
            root.setVisibility(View.VISIBLE);
            root.setImageDrawable(getResDrawable(R.drawable.maxsafe_splash));
//        } else if (client_id.equals("786")) {
//            root.setBackground(getResDrawable(R.drawable.bg_drawable));
//            findViewById(R.id.logo).setVisibility(View.VISIBLE);
//            findViewById(R.id.loadingText).setVisibility(View.VISIBLE);
        } else {
            if (!client_id.equals("947"))
                findViewById(R.id.logo).setVisibility(View.VISIBLE);
            TextView loadingText = findViewById(R.id.loadingText);
            if (loadingText != null)
                loadingText.setVisibility(View.VISIBLE);
            if (!logo_url.equals("")) {
                ImageView logo_img = findViewById(R.id.logo);
                load_image_file(logo_url, logo_img);
            }
        }
        if (client_id != null && !client_id.equals(""))
            ConstantMethods.get_adv_data(null);

        new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                isHandle = true;
                msg_what = msg.what;
                if (msg_what == 1) {
                    if (thread.isAlive()) {
                        thread.interrupt();
                    }
                    if (isNetworkAvailable(SplashActivity.this))
                        server_alert(SplashActivity.this);
                    else {
                        Toast.makeText(getApplicationContext(), "Please check internet connection", Toast.LENGTH_LONG).show();
//                        SplashActivity.this.finishAffinity();
//                        System.exit(0);
                    }
                } else if (msg_what == 0) {
                    if (!thread.isAlive()) {
//                        check_Permissions();
                        go_for_next();
                    }
                }
            }
        };

//        checkServer(handler);
        isHandle = true;
        msg_what = 0;

        thread = new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (Exception e) {
                    System.err.print(e.getMessage());
                }
                if (isHandle && msg_what == 0)
                    go_for_next();
//                    check_Permissions();
            }
        });
        thread.start();
    }

    public void checkServer(final Handler handler) {
        String url = checkServer_api;
        new NetworkRequest().make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                int is_shutdown = 1;
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
                            is_shutdown = jsonObject.getJSONObject("data").getInt("is_shutdown");
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
                if (handler != null) {
                    Message msg = new Message();
                    msg.what = is_shutdown;
                    handler.sendMessage(msg);
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
                if (handler != null) {
                    Message msg = new Message();
                    msg.what = 1;
                    handler.sendMessage(msg);
                }
            }
        });
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case ACCESS_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(PERMISSIONS_ACCESS_FINE_LOCATION, READ_EXTERNAL_STORAGE);
                    }
                } else {
                    finish();
                }
            }
            case READ_EXTERNAL_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    go_for_next();
                } else {
                    finish();
                }
            }
        }
    }


    public void check_Permissions() {
        int result = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(SplashActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

        if (result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED) {
            go_for_next();
        } else if (result != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS_ACCESS_FINE_LOCATION, ACCESS_FINE_LOCATION);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(PERMISSIONS_ACCESS_FINE_LOCATION, READ_EXTERNAL_STORAGE);
            }
        }
    }

    public void go_for_next() {
        Intent intent;
        if (client_id != null && !client_id.equals("") && uData == null) {
            intent = new Intent(SplashActivity.this, Advertisement_Activity.class);
        } else {
            if (isLogin())
                intent = new Intent(SplashActivity.this, HomeActivity.class);
            else
                intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        if (uData != null) {
            uData = uData.replaceAll("amp;", "");
            AppController.set_Deeplink(uData);
        }

//        intent = new Intent(SplashActivity.this, Tracking_first.class);
//        intent = new Intent(SplashActivity.this, EPC_Inventory.class);

        startActivity(intent);
        finish();

    }


}
