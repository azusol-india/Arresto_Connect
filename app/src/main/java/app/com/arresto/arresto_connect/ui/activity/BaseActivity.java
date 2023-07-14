package app.com.arresto.arresto_connect.ui.activity;

import static android.os.Build.VERSION.SDK_INT;
import static app.com.arresto.Flavor_Id.FlavourInfo.Fl_CLIENT_ID;
import static app.com.arresto.Flavor_Id.FlavourInfo.date_format;
import static app.com.arresto.arresto_connect.constants.AppUtils.extractYTId;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Check_permissions.CAMERA_STORAGE_PERMISSIONS;
import static app.com.arresto.arresto_connect.constants.Check_permissions.CAMERA_STORAGE_PERMISSIONS_10;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.ALL_LANGUAGES;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.APP_COLOR3;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.APP_COLOR4;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.APP_LOGO;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.GROUP_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.IS_LOGIN;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.PROFILE;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.ROLE_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_FNAME;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_LNAME;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.current_language;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.docsName;
import static app.com.arresto.arresto_connect.constants.Static_values.docsStatus;
import static app.com.arresto.arresto_connect.constants.Static_values.horizontal;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.user_email;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_alurail;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_wire;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.handheld.uhfr.UHFRManager;
import com.uhf.api.cls.Reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.com.arresto.arresto_connect.BuildConfig;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppLocationService;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Check_permissions;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.PrefernceConstants;
import app.com.arresto.arresto_connect.constants.Scan_RFID;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Advt_Model;
import app.com.arresto.arresto_connect.data.models.Client_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.data.models.Promotion_Model;
import app.com.arresto.arresto_connect.interfaces.AlertClickListener;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NFC_Listner;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.services.Background_LocationService;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressConstant;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressFlower;
import app.com.arresto.arresto_connect.ui.fragments.Dealer_info_fragment;
import app.com.arresto.arresto_connect.ui.fragments.Video_Fragment;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Project_details;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionFragment;
import cn.pda.serialport.Tools;

public abstract class BaseActivity extends AppCompatActivity {
    public List<String> help_videos;
    public int fragContainer;
    public String page_link;
    public ImageView video_btn;
    public DrawerLayout drawer;
    public TextView headerTv;
    public ImageView menuButton, back_button, upload_btn;
    public static SimpleDateFormat server_date_format = new SimpleDateFormat("yyyy-MM-dd");
    public static SimpleDateFormat server_date_time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    public ACProgressFlower progressDialog;

    public NFC_Listner.Write_interface write_interface;
    public NFC_Listner.Read_interface read_listner;
    public Scan_RFID scan_rfid;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getLayoutResourceId() != 0) {
            setContentView(getLayoutResourceId());
            setUpDirectory();
            progressDialog = new ACProgressFlower.Builder(this)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.YELLOW)
//                .bgColor(Color.BLACK)
                    .text(getResString("lbl_wait_st")).textSize(16).textMarginTop(5)
                    .petalThickness(2)
                    .sizeRatio((float) 0.22)
                    .fadeColor(Color.WHITE).build();
            progressDialog.setCancelable(false);
            video_btn = findViewById(R.id.video_btn);

            if (video_btn != null) {
                video_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String video_id = extractYTId(page_link);
                        if (!video_id.equalsIgnoreCase("")) {
                            Intent intent = new Intent(BaseActivity.this, Video_Fragment.class);
                            intent.putExtra("video_id", video_id);
                            startActivity(intent);
                        } else {
                            show_snak(BaseActivity.this, getResString("lbl_no_video_available"));
                        }
                    }
                });
            }
        }
    }

    public boolean isSupportThermalSensor() {
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        return (sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null);
    }

    protected abstract int getLayoutResourceId();

    public void setAppTheme() {
        if (client_id.equals("")) {
            AppUtils.update_language_data();
            logo_url = mPrefrence.getData(APP_LOGO);
            if (!Fl_CLIENT_ID.equals("")) {
                client_id = Fl_CLIENT_ID;
            } else {
                client_id = mPrefrence.getData(CLIENT_ID);
            }

//        AppController.getInstance().getDatabase().getLanguageDao().;
//        Dynamic_Var.getInstance().setApp_background("#ffffff");
//        Dynamic_Var.getInstance().setApp_text("#464445");
            Dynamic_Var.getInstance().setBtn_txt_clr("#" + mPrefrence.getData(APP_COLOR3));
            Dynamic_Var.getInstance().setBtn_bg_clr("#" + mPrefrence.getData(APP_COLOR4));
        }
    }

    public String barcode = "";

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if (e.getAction() == KeyEvent.ACTION_DOWN && e.getScanCode() != 0 && e.getKeyCode() != KeyEvent.KEYCODE_BACK) {
            if (e.getUnicodeChar() != 0) {
                char pressedKey = (char) e.getUnicodeChar();
                barcode += pressedKey;
            }
            if (e.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                Intent broadcastIntent = new Intent("deviceBarcode");
                broadcastIntent.putExtra("barcode", barcode);
                LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(broadcastIntent);
                barcode = "";
            }
            return false;
        } else {
//            printLog("event " + e);
            return super.dispatchKeyEvent(e);
        }
    }

    public void setUpDirectory() {
        if (getExternalMediaDirs().length > 0)
            directory = getExternalMediaDirs()[0] + "/Arresto/";
        else {
            directory = getExternalFilesDir(null) + "/Arresto/";
            directory = directory.replace("data", "media");
            directory = directory.replace("files/", "");
        }
    }

    public void startLocationBackgroundService() {
        Background_LocationService mService = new Background_LocationService();
        Intent mServiceIntent = new Intent(this, mService.getClass());
        if (!AppUtils.isMyServiceRunning(this, mService.getClass())) {
            startService(mServiceIntent);
        }
    }

    public void checkDir(String path) {
        File f1 = new File(path);
        if (!f1.exists()) {
            f1.mkdirs();
        }
    }

    public boolean isStoragePermissionGranted() {
        if (SDK_INT > Build.VERSION_CODES.P && !Check_permissions.hasPermissions(this, CAMERA_STORAGE_PERMISSIONS_10)) {
            Check_permissions.request_permissions(this, CAMERA_STORAGE_PERMISSIONS_10);
            return false;
        } else if (SDK_INT < Build.VERSION_CODES.Q && !Check_permissions.hasPermissions(this, CAMERA_STORAGE_PERMISSIONS)) {
            Check_permissions.request_permissions(this, CAMERA_STORAGE_PERMISSIONS);
            return false;
        }
        return true;
    }

    public void showDialog() {
        if (progressDialog != null && !progressDialog.isShowing())
            progressDialog.show();
    }

    public void hideDialog() {
        if (progressDialog != null && progressDialog.isShowing())
            progressDialog.dismiss();
    }

    public static String format = "";

    public static SimpleDateFormat DateTime_Format() {
        if (!mPrefrence.getData("dateFormat").equals(""))
            format = mPrefrence.getData(PrefernceConstants.DATE_FORMAT);
        else format = date_format;
        return new SimpleDateFormat(format);
    }

    public static SimpleDateFormat Time_Format() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm: a");
        return timeFormat;
    }

    public static SimpleDateFormat Date_Format() {
        if (!mPrefrence.getData("dateFormat").equals("")) {
            format = mPrefrence.getData(PrefernceConstants.DATE_FORMAT);
        } else format = date_format;
        String temp = format.split("hh")[0];
        return new SimpleDateFormat(temp.trim());
    }


    public void initLocation() {
        new AppLocationService(this, new AppLocationService.OnLocationChange() {
            @Override
            public void locationChange(Location location, double latitude, double longitude) {
                curr_location = location;
                curr_lat = latitude;
                curr_lng = longitude;
            }
        });
    }


    public void getHelpVideoData(final Handler handler) {
        final Message message = new Message();
        if (DataHolder_Model.getInstance().getAdvt_model() != null && DataHolder_Model.getInstance().getAdvt_model().getVideos().size() > 0) {
            setUpData();
            message.what = 1;
            if (handler != null)
                handler.sendMessage(message);
        } else {
            ConstantMethods.get_adv_data(new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 1) {
                        setUpData();
                        message.what = 1;
                        if (handler != null)
                            handler.sendMessage(message);
                    }
                }
            });
        }
    }

    public void setUpData() {
        help_videos = new ArrayList<>();
        for (Advt_Model.VData vData : DataHolder_Model.getInstance().getAdvt_model().getVideos()) {
            help_videos.add(vData.toString());
        }
    }

    public Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        return gsonBuilder.create();
    }

    public void show_Date_piker(final TextView textView) {
        int mYear, mMonth, mDay;
        final Calendar c = Calendar.getInstance();
        if (!textView.getText().toString().equals("")) {
            try {
                Date d = Date_Format().parse(textView.getText().toString());
                c.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                textView.setText(Date_Format().format(c.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(this, pDateSetListener, mYear, mMonth, mDay);
        dialog.show();
    }

    public Dialog show_OkAlert(final String title, final String msg, String txtPositive, String txtNegative, final AlertClickListener clickListener) {
        final Dialog dialog = new Dialog(this, R.style.theme_dialog);
        dialog.setContentView(R.layout.alert_dialog_layout);
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        final TextView title_tv = dialog.findViewById(R.id.title_tv);
        final TextView message_tv = dialog.findViewById(R.id.message_tv);
        Button but_yes = dialog.findViewById(R.id.but_yes);
        Button but_no = dialog.findViewById(R.id.but_no);
        title_tv.setText(title);
        message_tv.setText(msg);
        if (txtPositive != null && !txtPositive.equals("")) {
            but_yes.setText(txtPositive);
            but_yes.setVisibility(View.VISIBLE);
            but_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    if (clickListener != null)
                        clickListener.onYesClick();
                }
            });
        }
        if (txtNegative != null && !txtNegative.equals("")) {
            but_no.setText(txtNegative);
            but_no.setVisibility(View.VISIBLE);
            but_no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                    if (clickListener != null)
                        clickListener.onNoClick();
                }
            });
        }
        dialog.show();
        return dialog;
    }

    public void get_profileData(final boolean save_log) {
        docsStatus = "";
        docsName = "";
        String url = All_Api.getProfile + Static_values.user_id + "&client_id=" + client_id + "&language=" + current_language;
        if (!isNetworkAvailable(this)) {
            String oldResponse = mPrefrence.getData(PROFILE);
            if (oldResponse.equals(""))
                parseProfile(oldResponse);
            return;
        }
        new NetworkRequest(this).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response profile ", " is " + response);
                parseProfile(response);
                mPrefrence.saveData(PROFILE, response);
                if (save_log)
                    new NetworkRequest().save_logs((All_Api.logs_api + Static_values.user_id + "&eventType=" + user_email + " reopen app in Device " +
                            Build.BRAND + " " + Build.MODEL));
            }

            @Override
            public void onError(String message) {
                Log.e("onError logout ", " make_get_request " + message);
            }
        });
    }

    public String formatServerDate(String dateString) {
        Date date = null;
        try {
            date = server_date_format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateTime_Format().format(date);
    }

    public String formatServerDateTime(String dateString) {
        Date date = null;
        try {
            date = server_date_time.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateTime_Format().format(date);
    }

    private void parseProfile(String response) {
        Object json;
        try {
            json = new JSONTokener(response).nextValue();
            if (json instanceof JSONObject) {
                JSONObject jsonObject1 = new JSONObject(response);
                String usr_id = jsonObject1.getString("user_id");
                if (usr_id.equals("")) {
                    Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
                    startActivity(intent);
                    mPrefrence.saveBoolean(IS_LOGIN, false);
                } else {
                    Profile_Model profileModel = Profile_Model.setInstance(AppUtils.getGson().fromJson(jsonObject1.getString("profile"), Profile_Model.class));
                    if (jsonObject1.has("group_email"))
                        profileModel.setGroup_email(jsonObject1.getString("group_email"));
                    if (jsonObject1.has("user_group_code"))
                        profileModel.setUser_group_code(jsonObject1.getString("user_group_code"));
                    if (jsonObject1.has("user_group_name"))
                        profileModel.setUser_group_name(jsonObject1.getString("user_group_name"));
                    if (jsonObject1.has("can_approve_reports"))
                        profileModel.setCan_approve_reports(jsonObject1.getString("can_approve_reports"));
                    if (jsonObject1.has("cgrp_id")) {
                        String role_id = jsonObject1.getString("cgrp_id");
                        mPrefrence.saveData(ROLE_ID, role_id);
                        Static_values.role_id = role_id;
                    }
                    if (jsonObject1.has("client_group_id")) {
                        String client_group_id = jsonObject1.getString("client_group_id");
                        Static_values.client_group_id = client_group_id;
                    }
                    if (jsonObject1.has("group_id")) {
                        String group_id = jsonObject1.getString("group_id");
                        mPrefrence.saveData(GROUP_ID, group_id);
                        Static_values.group_id = group_id;
                    }
                    if (jsonObject1.has("inspector_docs_status")) {
                        JSONObject doc_obj = jsonObject1.getJSONObject("inspector_docs_status");
                        if (doc_obj.has("files")) {
                            JSONArray docsArr = doc_obj.getJSONArray("files");
                            for (int i = 0; i < docsArr.length(); i++) {
                                JSONObject obj = docsArr.getJSONObject(i);
                                if (obj.getString("status").equals("overdue")) {
                                    docsStatus = obj.getString("status");
                                    docsName = docsName + obj.getString("title") + ",";
                                } else if (obj.getString("status").equals("due")) {
                                    docsName = docsName + obj.getString("title") + ",";
                                    if (!docsStatus.equals("overdue"))
                                        docsStatus = obj.getString("status");
                                }
                            }
                            if (!docsStatus.equals("")) {
                                show_OkAlert("Document Expiring...", "Your document, " + docsName + " is Expiring. \nPlease contact the administrator and get your updated documents uploaded", null, "Close", null);
                            }
                        }
                    }


                    mPrefrence.saveData(USER_FNAME, profileModel.getUpro_first_name());
                    mPrefrence.saveData(USER_LNAME, profileModel.getUpro_last_name());
                    String languages = jsonObject1.getJSONObject("profile").getString("lang_key");
                    if (languages != null && !languages.equals("")) {
                        List<String> all_languages = new Gson().fromJson(languages, ArrayList.class);
                        mPrefrence.saveArray_Data(ALL_LANGUAGES, all_languages);
                    }
                    mPrefrence.saveData(USER_LNAME, profileModel.getUpro_last_name());
                    Intent intent = new Intent("Profile update");
                    intent.putExtra("isUpdate", true);
                    LocalBroadcastManager.getInstance(BaseActivity.this).sendBroadcast(intent);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("JSONException ", " profile " + e.getMessage());
        }
    }

    public double curr_lat = 0.0;
    public double curr_lng = 0.0;
    public Location curr_location = new Location("test loc");

    public void inspection_alert() {
        new AlertDialog.Builder(this)
                .setIcon(R.drawable.error)
                .setTitle(getResString("lbl_auth_issu_txt"))
                .setMessage(getResString("lbl_auth_issu_msg"))
                .setPositiveButton(getResString("lbl_ok_st"), null)
//                .setNegativeButton(getResString("lbl_no"), null)
                .show();
    }

    public void promotionApp_Dialog(final Promotion_Model promotionModel) { // for user
        final Dialog builder = new Dialog(this, R.style.theme_dialog);
        final View dialoglayout = getLayoutInflater().inflate(R.layout.promotion_app_dialog, null);
        TextView dwnlod_btn = dialoglayout.findViewById(R.id.dwnlod_btn);
        TextView msg_tv = dialoglayout.findViewById(R.id.msg_tv);
        TextView asset_tv = dialoglayout.findViewById(R.id.asset_tv);
        ImageView logo_img = dialoglayout.findViewById(R.id.logo_img);
        ImageView asset_img = dialoglayout.findViewById(R.id.asset_img);
        ImageView close_btn = dialoglayout.findViewById(R.id.close_btn);

        msg_tv.setText("The above asset is not a part of " + getResString("app_name") +
                ".\nThe asset is the product of " + promotionModel.getCustomer_company_name());
        asset_tv.setText("Asset: " + promotionModel.getAsset_code());
        AppUtils.load_image(promotionModel.getAsset_image(), asset_img);
        AppUtils.load_image(promotionModel.getCustomer_logo_path(), logo_img);

        dwnlod_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(promotionModel.getAndroid_app())));
                builder.dismiss();
            }
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.setContentView(dialoglayout);
        builder.show();
    }

    public void fetch_dealer_data() {
        new NetworkRequest(this).make_get_request(All_Api.dealerinfo + client_id, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.has("status_code")) {
                        if (jsonObject.getString("status_code").equals("200")) {
                            ArrayList<Client_model> clientModel = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Client_model[].class)));
                            showBottomSheetDialog(clientModel);
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

    public boolean isEOD(Project_Model project_model) {     // is end of day is done
        if (project_model.getTraining_status() == null || project_model.getTraining_status().equalsIgnoreCase("open"))
            return false;
        else {
            show_snak(this, "You already done end of day!");
            return true;
        }
    }

    public String text_toWrite;

    public void write_intent(String txt_toWrite, NFC_Listner.Write_interface wrt_interface) {
        if (scan_rfid != null) {
            scan_rfid.checkNfcEnabled();
        }
        text_toWrite = txt_toWrite;
        write_interface = wrt_interface;
    }

    public void Read_intent(NFC_Listner.Read_interface listner) {
        read_listner = listner;
        if (mUhfrManager != null) {
            StartReadUhfrRFID();
        }
    }


    public UHFRManager mUhfrManager;

    public void StopReadUhfrRFID() {
        if (mUhfrManager != null) {
            mUhfrManager.asyncStopReading();
            handler1.removeCallbacks(runnable_MainActivity);
        }
    }

    public void StartReadUhfrRFID() {
        if (mUhfrManager == null) {
//            show_snak(this, "connection_failed");
            return;
        }
        mUhfrManager.asyncStartReading();
        handler1.postDelayed(runnable_MainActivity, 0);
    }

    public Handler handler1 = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String epc = msg.getData().getString("data");
//                    String rssi = msg.getData().getString("rssi");
                    AppUtils.show_snak(homeActivity, epc);
                    if (epc == null || epc.length() == 0) {
                        return;
                    }
                    if (read_listner != null) {
                        read_listner.read_complete(epc);
                        read_listner = null;
                        StopReadUhfrRFID();
                    }
//                    AppUtils.show_snak(BaseActivity.this, epc + "\n" + rssi);
                    break;
                case 1980:
//                    String countString = tvRunCount.getText().toString();
//                    if (countString.equals("") || countString == null) {
//                        tvRunCount.setText(String.valueOf(1));
//                    } else {
//                        int previousCount = Integer.valueOf(countString);
//                        int nowCount = previousCount + 1;
//                        tvRunCount.setText(String.valueOf(nowCount));
//                    }
                    break;
            }
        }
    };

    public Runnable runnable_MainActivity = new Runnable() {
        @Override
        public void run() {
            List<Reader.TAGINFO> list1;
            if (mUhfrManager == null)
                return;
            list1 = mUhfrManager.tagInventoryRealTime();
            if (list1 == null || list1.size() < 1)
                list1 = mUhfrManager.tagEpcTidInventoryByTimer((short) 50);
            String data = "";
            handler1.sendEmptyMessage(1980);
            if (list1 != null && list1.size() > 0) {
                Log.e("TGA", list1.size() + "");
                for (Reader.TAGINFO tfs : list1) {
                    data = Tools.Bytes2HexString(tfs.EmbededData, tfs.EmbededDatalen);
//                    try {
//                        data = new String(tfs.EpcId, "UTF-8");
//                        data = new String(tfs.EmbededData, "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
                    int rssi = tfs.RSSI;
                    Message msg = new Message();
                    msg.what = 1;
                    Bundle b = new Bundle();
                    b.putString("data", data);
                    b.putString("rssi", rssi + "");
                    msg.setData(b);
                    handler1.sendMessage(msg);
                }
            }
            handler1.postDelayed(runnable_MainActivity, 0);
        }
    };

    public void showBottomSheetDialog(final ArrayList<Client_model> clientModels) {
        Dealer_info_fragment bottomSheetFragment = new Dealer_info_fragment(clientModels);
        bottomSheetFragment.show(getSupportFragmentManager(), bottomSheetFragment.getTag());
    }


    public void SelectLifeLineDialog() { // for user
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_line_dialog);
        if (!logo_url.equals("")) {
            ImageView logo_img = dialog.findViewById(R.id.logo_img);
            load_image_file(logo_url, logo_img);
        }

        final Spinner line_spinr = dialog.findViewById(R.id.line_spinr);
        final Spinner cat_spinr = dialog.findViewById(R.id.cat_spinr);
        final EditText customer_edt = dialog.findViewById(R.id.edt_dialog);
        final EditText appplication_edt = dialog.findViewById(R.id.appplication_edt);
        dialog.findViewById(R.id.loc_title).setVisibility(View.GONE);
        dialog.findViewById(R.id.latlng_tv).setVisibility(View.GONE);
        appplication_edt.setVisibility(View.VISIBLE);
        customer_edt.setHint("Customer Name");
        line_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    cat_spinr.setVisibility(View.GONE);
                } else {
                    cat_spinr.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        dialog.findViewById(R.id.ok_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String customer_name = customer_edt.getText().toString();
                String appplication = appplication_edt.getText().toString();
                if (line_spinr.getSelectedItemPosition() == 0)
                    loadEC_Fragment(horizontal, customer_name, appplication);
                else if (line_spinr.getSelectedItemPosition() == 1) {
                    if (cat_spinr.getSelectedItemPosition() == 0)
                        loadEC_Fragment(vertical_wire, customer_name, appplication);
                    else
                        loadEC_Fragment(vertical_alurail, customer_name, appplication);
                }
                dialog.dismiss();
            }
        });
        dialog.findViewById(R.id.cncl_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void loadEC_Fragment(String cat_id, String customer_name, String applicationName) {
        Bundle bundle = new Bundle();
        bundle.putString("boq_id", "");
        bundle.putString("line_type", cat_id);
        bundle.putString("customer_name", customer_name);
        bundle.putString("applicationName", applicationName);
        bundle.putBoolean("isQuote", true);
        EC_Project_details ec_project = new EC_Project_details();
        ec_project.setArguments(bundle);
        LoadFragment.replace(ec_project, this, getResString("lbl_life_line_details"));
    }


    public void save_Image(Bitmap image, String path) {
        try {
            File imageFile = new File(path);
            OutputStream out = new FileOutputStream(imageFile);
            if (out != null) {
                image.compress(Bitmap.CompressFormat.JPEG, 80, out);
                out.flush();
                out.close();
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public boolean validateEmail(TextView inputEmail) {
        String email = inputEmail.getText().toString().trim();
        if (!isValidEmail(email)) {
            setError(inputEmail, getResString("lbl_valid_email"));
            requestFocus(inputEmail);
            return false;
        } else {
            ((TextInputLayout) inputEmail.getParent().getParent()).setErrorEnabled(false);
        }
        return true;
    }

    public boolean validatePassword(TextView inputView) {
        if (isEmpty(inputView)) {
            return false;
        } else if (inputView.getText().toString().trim().length() > 5)
            return true;
        else {
            setError(inputView, getResString("lbl_valid_pass"));
            return false;
        }
    }

    public boolean validatePhone(TextView inputView) {
        String phone = inputView.getText().toString().trim();
        if (!isValidPhone(phone)) {
            setError(inputView, getResString("lbl_please_enter_valid_mobile_number"));
            requestFocus(inputView);
            return false;
        } else
            ((TextInputLayout) inputView.getParent().getParent()).setErrorEnabled(false);
        return true;
    }

    public boolean isEmpty(TextView etText) {
        if (etText.getText().toString().trim().length() > 0 && etText.getParent().getParent() instanceof TextInputLayout) {
            ((TextInputLayout) etText.getParent().getParent()).setErrorEnabled(false);
            return false;
        } else {
            requestFocus(etText);
            setError(etText, getResString("lbl_mandatory_field"));
            return true;
        }
    }

    public boolean isEmpty1(TextView etText) {
        if (etText.getText().toString().trim().length() > 0) {
            etText.setEnabled(false);
            return false;
        } else {
            requestFocus(etText);
            etText.setError(getResString("lbl_mandatory_field"));
            return true;
        }
    }

    public void setError(TextView etText, String error) {
        if (etText.getParent().getParent() instanceof TextInputLayout) {
            ((TextInputLayout) etText.getParent().getParent()).setError(error);
        } else
            etText.setError(error);
    }

    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private static boolean isValidPhone(String phone) {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public void printLog(String msg) {
        if (BuildConfig.DEBUG)
            Log.e("baseActivity", msg);
    }


    @Override
    public void onBackPressed() {
        printLog("BaseActivity onBackPressed run");
        if (this instanceof HomeActivity) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
                return;
            }
            video_btn.setVisibility(View.GONE);
            FragmentManager fM = getSupportFragmentManager();
            int count = fM.getBackStackEntryCount();

            if (count <= 2) {
                back_button.setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
            } else {
                back_button.setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.GONE);
            }
            if (count < 2) {
                AppUtils.app_close_alert(this);
            } else {
                Fragment f = fM.findFragmentById(R.id._lay2);
                if (f instanceof InspectionFragment) {
                    InspectionFragment inspectionFragment = (InspectionFragment) f;
                    if (inspectionFragment.fullmap) {
                        inspectionFragment.on_mapClick();
                    } else {
                        fM.popBackStack();
                        headerTv.setText(fM.getBackStackEntryAt(count - 2).getName());
                    }
                } else {
                    fM.popBackStack();
                    headerTv.setText(fM.getBackStackEntryAt(count - 2).getName());
                }
            }
        } else if (this instanceof LoginActivity) {
            AppUtils.app_close_alert(this);
        } else {
            super.onBackPressed();
        }
    }


    public void getCurrentAddress(ObjectListener listner) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(curr_lat, curr_lng, 1);
            if (addresses != null && addresses.size() > 0) {
                String current_state = addresses.get(0).getAdminArea();
                String current_city = addresses.get(0).getSubAdminArea();
                String current_address = addresses.get(0).getAddressLine(0);
                Log.e("current_address", "address " + addresses.get(0));
//                Log.e("state local", "state " + current_state);
//                Log.e("city local", "city " + current_city);

                if (listner != null) {
                    listner.onResponse(current_address + " " + current_city + " " + current_state);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
