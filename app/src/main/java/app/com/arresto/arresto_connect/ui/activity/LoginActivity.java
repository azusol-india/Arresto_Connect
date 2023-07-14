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

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.LanguageHelper;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.custom_scan.DecoderActivity;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;

import static app.com.arresto.Flavor_Id.FlavourInfo.Fl_CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.AppUtils.bytes_toBase64;
import static app.com.arresto.arresto_connect.constants.AppUtils.clear_key;
import static app.com.arresto.arresto_connect.constants.AppUtils.getIMEI;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.save_logo;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Check_permissions.addAutoStartup;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.ALL_LANGUAGES;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.APP_COLOR3;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.APP_COLOR4;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.APP_LOGO;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.CURRENT_LANGUAGE;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.EMAIL;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.GROUP_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.IS_LOGIN;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.PASSWORD;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.RE_EMAIL;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.ROLE_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USERTYPE;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_NAME;
import static app.com.arresto.arresto_connect.constants.Static_values.device_id;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.network.All_Api.Host;

//
public class LoginActivity extends BaseActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public EditText emailEdt, passEdt;
    TextView facebook_btn, google_btn, new_tv, forgot_btn, chng_lang, chng_imi_tv, register_btn;
    View button;
    String url;
    String jsonMail, user_name;
    CheckBox rem_lay;

    private String localMail, localAuth, group_id, user_type, user_id, role_id;
    ImageView logo_img;
    LoginButton loginButton;
    private GoogleApiClient mGoogleApiClient;
    private ConnectionResult mConnectionResult;
    private CallbackManager callbackManager;
    private boolean mSignInClicked;
    private boolean mIntentInProgress;

    private ProgressDialog progressDialog;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LanguageHelper.onAttach(base));
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_login;
    }

    protected void onCreate(Bundle savedInstanceState) {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        clear_key(this);
        super.onCreate(savedInstanceState);
        find_allId();
        if (!logo_url.equals("")) {
            printLog("logo link == " + logo_url);
            logo_img = findViewById(R.id.logo_img);
            load_image_file(logo_url, logo_img);
        }
        if (!Fl_CLIENT_ID.equals("931")) {
            check_language();
        } else {
            register_btn.setText(Html.fromHtml(getResString("not_register_label")));
        }

        genrate_registration_token();
        chng_lang.setText(Static_values.current_language);
//        printHashKey(this);

        addAutoStartup(this);

        // To show login detail
        String email = mPrefrence.getData(RE_EMAIL);
        String pass = mPrefrence.getData(PASSWORD);
        emailEdt.setText(email);
        passEdt.setText(pass);
        passEdt.setTypeface(Typeface.DEFAULT);
        passEdt.setTransformationMethod(new PasswordTransformationMethod());
        forgot_btn.setPaintFlags(forgot_btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgot_btn.setText(AppUtils.getResString("lbl_forgot_password"));
//        HomeActivity.closeDrawer();

        SpannableString ss = new SpannableString(getResources().getString(R.string.lbl_chng_ime_strng));
        ClickableSpan span1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View textView) {
                // do some thing
                Log.e("string ", " is clicked now");
                send_imei_request();
            }
        };
        ss.setSpan(span1, 30, 42, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        chng_imi_tv.setText(ss);
        chng_imi_tv.setMovementMethod(LinkMovementMethod.getInstance());


        if (getIntent().getExtras() != null && getIntent().getExtras().getString("type").equalsIgnoreCase("signUp")) {
            emailEdt.setText(getIntent().getExtras().getString("email"));
            passEdt.setText(getIntent().getExtras().getString("password"));
            rem_lay.setChecked(true);
            button.performClick();
        }


        LoginManager.getInstance().logOut();

//        loginButton.(this);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.e("LoginActivity", " is " + object);
                        showpDialog();
                        try {
                            String email = "";
                            String profilePicUrl = "";
                            user_id = object.getString("id");
                            if (object.has("email")) {
                                email = object.getString("email");
                            }
                            if (object.has("name")) {
                                user_name = object.getString("name");
                            }
//                            if (object.has("first_name")) {
//                                String firstName = response.getJSONObject().getString("first_name");
//                                Log.e("firstName", " is " + firstName);
//                            }
//                            if (object.has("last_name")) {
//                                String lastName = response.getJSONObject().getString("last_name");
//                                Log.e("lastName", " is " + lastName);
//                            }
                            if (object.has("picture")) {
                                profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            }
                            hidepDialog();
                            make_data(user_name, user_id, email, profilePicUrl, "Facebook");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,name,email,gender,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.e("login cancel", toString());
            }

            @Override
            public void onError(FacebookException error) {
                Log.e("login error", error.toString());
            }
        });

        if (Fl_CLIENT_ID.equals("376") || client_id.equals("376")|| Fl_CLIENT_ID.equals("947") ||client_id.equals("947") || !isGooglePlayServicesAvailable(this)) {
            findViewById(R.id.social_section).setVisibility(View.GONE);
            google_btn.setVisibility(View.GONE);
            facebook_btn.setVisibility(View.GONE);
            google_btn.setVisibility(View.GONE);
        } else {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .enableAutoManage(this, this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

        }
        show_showCase();
        setUpHelpVideo();
    }

    private void check_language() {
        List<String> sLanguages = mPrefrence.getArray_Data(ALL_LANGUAGES);
        if (sLanguages.size() > 0) {
            ((ViewGroup) chng_lang.getParent()).setVisibility(View.VISIBLE);
            chng_lang.setVisibility(View.VISIBLE);
        }
//        else if (!Fl_CLIENT_ID.equals("")) {
////            get_all_languages_here();
//        }
        else {
            ((ViewGroup) chng_lang.getParent()).setVisibility(View.GONE);
            chng_lang.setVisibility(View.GONE);
        }


    }

    public void setUpHelpVideo() {
        getHelpVideoData(new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (help_videos != null) {
                        ConstantMethods.find_pageVideo(LoginActivity.this, "login");
                    }
                }
            }
        });
    }

    private void show_showCase() {
        ArrayList<String> mesages = new ArrayList<>(Arrays.asList(getResString("lbl_click_here_to_sign_up"),
                getResString("lbl_login_email_password"),
                getResString("lbl_login_with_facebook"),
                getResString("lbl_login_with_google"),
                getResString("lbl_click_forgot_password"),
                getResString("lbl_nexttime"),
                getResString("lbl_click_to_change_language")));
        ArrayList<View> views = new ArrayList<View>(Arrays.asList(register_btn, button, facebook_btn, google_btn, forgot_btn, rem_lay, chng_lang));
        Add_Showcase.newInstance(this).setData(mesages, views);
    }

    public void find_allId() {
        forgot_btn = findViewById(R.id.forgot);
        new_tv = findViewById(R.id.new_tv);
        chng_lang = findViewById(R.id.chng_lang);
        emailEdt = findViewById(R.id.usrname);
        rem_lay = findViewById(R.id.rem_lay);
        passEdt = findViewById(R.id.paas);
        button = findViewById(R.id.login_btn3);
        register_btn = findViewById(R.id.register_btn);
        chng_imi_tv = findViewById(R.id.chng_imi_tv);
        loginButton = findViewById(R.id.authButton1);
        facebook_btn = findViewById(R.id.facebook);
        google_btn = findViewById(R.id.google_btn1);

        facebook_btn.setOnClickListener(this);
        google_btn.setOnClickListener(this);
        chng_lang.setOnClickListener(this);
        forgot_btn.setOnClickListener(this);
        button.setOnClickListener(this);
        register_btn.setOnClickListener(this);
        new_tv.setText(getResString("lbl_new_on") + " " + getResString("app_name"));
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(true);
//        set_bg(Static_values.client_id,findViewById(R.id.root_lay));

        new CustomTextWatcher(emailEdt, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                if (text != null && text.length() > 4)
                    validateEmail(view);
            }
        });
        new CustomTextWatcher(passEdt, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                if (text != null && text.length() > 3)
                    validatePassword(view);
            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chng_lang:
                AppUtils.Change_language(this);
                break;
            case R.id.facebook:
                if (!isStoragePermissionGranted())
                    return;
                LoginManager.getInstance().logOut();
                loginButton.setReadPermissions(Arrays.asList("email"));
                loginButton.performClick();
                Log.e("loginButton", "run click  ");
                break;
            case R.id.google_btn1:
                if (!isStoragePermissionGranted())
                    return;
                mSignInClicked = true;
                showpDialog();
                signIn();
                break;
            case R.id.forgot:
                Intent intent = new Intent(LoginActivity.this, Forgot_paswrd_activity.class);
                intent.putExtra("email", emailEdt.getText().toString());
                startActivity(intent);
                break;
            case R.id.register_btn:
                Intent intent1 = new Intent(LoginActivity.this, Sign_upActivity.class);
                startActivity(intent1);
                break;
            case R.id.login_btn3:
                if (!isStoragePermissionGranted())
                    return;
                printLog("login_btn3 clicked");
                localMail = emailEdt.getText().toString();
                localAuth = passEdt.getText().toString();
                if (validateEmail(emailEdt) && validatePassword(passEdt)) {
                    makeJsonRequest();
                }
                break;
        }
    }

    private void makeJsonRequest() {
        Log.e("hitting login", "now======");
        url = All_Api.login_url + localMail + "&password=" + localAuth + "&token=" + device_id + "&imei_no=" + getIMEI(this) + "&device_type=Android";
        new NetworkRequest(this).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onResponse(String response) {
                Log.e("error", "" + response);
                parsh_response(response, false);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(getApplicationContext(), All_Api.connection_MSG, Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void remember_check() {
        if (rem_lay.isChecked()) {
            mPrefrence.saveData(RE_EMAIL, localMail);
            mPrefrence.saveData(PASSWORD, localAuth);
        } else {
            mPrefrence.remove_key(RE_EMAIL);
            mPrefrence.remove_key(PASSWORD);
            passEdt.setText("");
            emailEdt.setText("");
        }
    }

    public void go_Next() {
        new NetworkRequest().save_logs((All_Api.logs_api + Static_values.user_id + "&eventType=" + jsonMail + "login With Device " +
                Build.BRAND + " " + Build.MODEL));
        remember_check();
        mPrefrence.saveData(ROLE_ID, role_id);
        mPrefrence.saveData(EMAIL, jsonMail);
        mPrefrence.saveData(USERTYPE, user_type);
        mPrefrence.saveData(USER_ID, user_id);
        mPrefrence.saveData(GROUP_ID, group_id);
        mPrefrence.saveData(USER_NAME, user_name);
        mPrefrence.saveBoolean(IS_LOGIN, true);

        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
//        finish();
    }


    public void send_imei_request() {
        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", user_id);
        params.put("mob_imei", getIMEI(this));

        HashMap<String, File> files = new HashMap<>();

        NetworkRequest networkRequest = new NetworkRequest(this);
        networkRequest.upload_param_file(All_Api.update_profile, params, files, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject1 = new JSONObject(response);
                        AppUtils.show_snak(LoginActivity.this, jsonObject1.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("error", "" + error);

            }
        });

    }


    public void genrate_registration_token() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(LoginActivity.this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String newToken = instanceIdResult.getToken();
                device_id = newToken;
                Log.e("newToken", newToken);
            }
        });
        Log.e(LoginActivity.class.getSimpleName(), "device_id " + device_id);
    }

    HashMap<String, String> login_map;

    public void make_data(String name, String auth_id, final String email, String image, String provider) {
        login_map = new HashMap<>();
        login_map.put("name", name);
        login_map.put("oauth_id", auth_id);
        login_map.put("email", email);
        login_map.put("social_image", image);
        login_map.put("oauth_provider", provider);
        login_map.put("user_profile", "1");
        login_map.put("device_id", device_id);
        login_map.put("device_type", "Android");
        social_login();
    }

    public void social_login() {
        if (login_map == null)
            return;
        if (!Fl_CLIENT_ID.equals("")) {
            login_map.put("client_id", bytes_toBase64(Fl_CLIENT_ID.getBytes()).trim());
        } else if (!client_id.equals("")) {
            login_map.put("client_id", client_id);
        }

        Log.e("params ", " social login is  " + login_map);
        new NetworkRequest(this).make_post_request(All_Api.social_login, login_map, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", " is  " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        parsh_response(object.getString("data"), true);
                    } else {
                        if (Fl_CLIENT_ID.equals("") && client_id.equals("")) {
                            Intent intent = new Intent(LoginActivity.this, DecoderActivity.class);
                            startActivityForResult(intent, 10);
                            Toast.makeText(LoginActivity.this, "To register scan company's QR Code", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("social login ", " error is  " + message);
            }
        });
    }


    private void getProfileInformation(GoogleSignInResult result) {
        Log.e("activity data", " getProfileInformation  ");

        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.e("detail", "display name: " + acct.getDisplayName());
            user_name = acct.getDisplayName();
            String user_profilePic = "";
            if (acct.getPhotoUrl() != null) {
                user_profilePic = acct.getPhotoUrl().toString();
            }
            user_id = acct.getId();
            String email = acct.getEmail();

            Log.e("detail", "Name: " + user_name + ", user_Email: " + email + ", Image: " + user_profilePic);
            make_data(user_name, user_id, email, user_profilePic, "Google+");
        } else {
            Toast.makeText(this, "Person information is null", Toast.LENGTH_LONG).show();
        }
        hidepDialog();
    }

    String client_id = "";

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Add this line to your existing onActivityResult() method
        Log.e("activity data", "requestCode   " + requestCode + "resultCode   " + resultCode);
        if (requestCode == 10) {
            if (data != null) {
                String scaned_data = data.getDataString();
                if (scaned_data.contains(Host)) {
                    if (scaned_data != null) {
                        client_id = Uri.parse(scaned_data).getQueryParameter("client");
                        social_login();
//                        client_id = scaned_data;
                    } else {
                        show_snak(this, getResString("lbl_try_again_msg"));
                    }
                } else {
                    show_snak(this, "Invalid code! Please try again");
                }
            } else {
                show_snak(this, getResString("lbl_try_again_msg"));
            }
        } else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (resultCode != RESULT_OK) {
                mSignInClicked = false;
            }
            mIntentInProgress = false;
            getProfileInformation(result);

            if (mGoogleApiClient != null && !mGoogleApiClient.isConnecting()) {
                mGoogleApiClient.connect();
            }
        }
    }

    public void onResume() {
        super.onResume();
        Log.e("activity data", "on onResume   " + mGoogleApiClient);
        // Connect the client. Once connected, the camera is launched.
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

    }

    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    public void onStop() {
        super.onStop();
        Log.e("activity data", "on stop   ");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            revokeAccess();
            mGoogleApiClient.disconnect();
        }
    }

    public void parsh_response(String response, boolean is_social) {
        try {
            JSONObject res_data = new JSONObject(response);

            if (is_social) {
                JSONObject user_json = res_data.getJSONObject("user");
                jsonMail = user_json.getString("uacc_email");
                user_id = user_json.getString("uacc_id");
                group_id = user_json.getString("uacc_group_fk");
//                user_type  = user_json.getString("");
                role_id = user_json.getString("uacc_cgroup_fk");
            } else {
                jsonMail = res_data.getString("email");
                user_name = res_data.getString("user_fullName");
                user_id = res_data.getString("userID");
                group_id = res_data.getString("userGroupID");
                user_type = res_data.getString("userType");

                Log.e("jauth", "" + group_id + "#" + user_type);

                if (res_data.getInt("authenticaton") == 0) {
                    Toast.makeText(getApplicationContext(), res_data.getString("error"), Toast.LENGTH_LONG).show();
                    if (res_data.getString("error").equals("IMEI No. is Incorrect"))
                        chng_imi_tv.setVisibility(View.VISIBLE);
                    else
                        chng_imi_tv.setVisibility(View.GONE);
                    return;
                }
                role_id = res_data.getString("cgrp_id");
            }

            JSONObject config = res_data.getJSONObject("config");
            if (config.has("client_id") && (Fl_CLIENT_ID.equalsIgnoreCase("") || Fl_CLIENT_ID.equals(config.getString("client_id")))) {
                if (config.has("customer_logo_path")) {
                    if (logo_url.equals("") || !config.getString("client_id").equals(Static_values.client_id) || !(new File(logo_url).exists())) {
//                        if (Fl_CLIENT_ID.equals("")) {
                        logo_url = Static_values.directory + config.getString("client_id") + "_logo.png";
                        save_logo(this, config.getString("customer_logo_path"), logo_url);

                        mPrefrence.saveData(APP_LOGO, logo_url);
//                        }
                        mPrefrence.saveData(CLIENT_ID, config.getString("client_id"));
                        mPrefrence.saveData(APP_COLOR3, config.getString("customer_txt_color"));
                        Dynamic_Var.getInstance().setBtn_txt_clr("#" + config.getString("customer_txt_color"));
                        mPrefrence.saveData(APP_COLOR4, config.getString("customer_theme_color"));
                        Dynamic_Var.getInstance().setBtn_bg_clr("#" + config.getString("customer_theme_color"));


                        if (config.has("default_languages") && !config.getString("default_languages").equalsIgnoreCase("english")
                                && (mPrefrence.getData(CURRENT_LANGUAGE) == null || mPrefrence.getData(CURRENT_LANGUAGE).isEmpty())) {
                            AppUtils.getLanguage(LoginActivity.this, config.getString("default_languages"), new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    if (msg.obj.equals("true"))
                                        go_Next();
                                }
                            });
                        } else {
                            go_Next();
                            finish();
                        }
                    } else {
                        go_Next();
                        finish();
                    }
                } else {
                    mPrefrence.remove_key(APP_LOGO);
                    mPrefrence.remove_key(CLIENT_ID);
                    mPrefrence.remove_key(APP_COLOR3);
                    mPrefrence.remove_key(APP_COLOR4);
                    Dynamic_Var.getInstance().setBtn_bg_clr("");
                    go_Next();
                    finish();
                }
            } else {
                show_snak(LoginActivity.this, getResString("lbl_unauthorized_login_please_try_again"));
            }

        } catch (
                JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void showpDialog() {
        if (!progressDialog.isShowing())
            progressDialog.show();
    }

    private void hidepDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }

    /****  GOOGLE Code here***/


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e("activity ", " onConnected  ");
        mSignInClicked = false;
//        Toast.makeText(this, "User is connected!", Toast.LENGTH_LONG).show();

        // Get user's information
        revokeAccess();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public static final int RC_SIGN_IN = 100;

    private void resolveSignInError() {
        Log.e("activity data", " resolveSignInError  " + mConnectionResult.hasResolution());
        if (mConnectionResult.hasResolution()) {
            try {
                mIntentInProgress = true;
                mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
            } catch (IntentSender.SendIntentException e) {
                mIntentInProgress = false;
                if (mGoogleApiClient != null)
                    mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e("activity data", " onConnectionFailed  " + result);
        hidepDialog();
        if (!result.hasResolution()) {
            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }
        if (!mIntentInProgress) {
            mConnectionResult = result;
            if (mSignInClicked) {
                resolveSignInError();
            }
        }
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        Log.e("revokeAccess ", " onConnectionFailed  " + status.getStatusMessage());
                    }
                });
    }

    public boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(context);
        return resultCode == ConnectionResult.SUCCESS;
    }

}




