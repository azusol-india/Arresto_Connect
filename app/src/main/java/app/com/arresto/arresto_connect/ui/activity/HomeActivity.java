/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.activity;

import static app.com.arresto.Flavor_Id.FlavourInfo.Fl_CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.AppUtils.getDisplayWidth;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isLogin;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.isTablet;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.Check_permissions.LOCATION_PERMISSIONS;
import static app.com.arresto.arresto_connect.constants.Check_permissions.OTHER_PERMISSIONS;
import static app.com.arresto.arresto_connect.constants.Check_permissions.addAutoStartup;
import static app.com.arresto.arresto_connect.constants.Check_permissions.request_LocationPermission;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.EMAIL;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.GROUP_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.IS_LOGIN;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.IS_WELCOME_DONE;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.ROLE_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.SENSOR_PERMISSION;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USERTYPE;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_FNAME;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_LNAME;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.group_id;
import static app.com.arresto.arresto_connect.constants.Static_values.isProfile;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_email;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_typ;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Check_permissions;
import app.com.arresto.arresto_connect.constants.GetVersionCode;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Scan_RFID;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.ExpandedMenuModel;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.interfaces.AlertClickListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.services.Service_Restarter;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;
import app.com.arresto.arresto_connect.ui.adapters.ExpandableListAdapter;
import app.com.arresto.arresto_connect.ui.fragments.BlankFragment;
import app.com.arresto.arresto_connect.ui.fragments.Close_projectFragment;
import app.com.arresto.arresto_connect.ui.fragments.Dealer_detailFragment;
import app.com.arresto.arresto_connect.ui.fragments.Group_Filter_fragment;
import app.com.arresto.arresto_connect.ui.fragments.Home_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.Main_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.My_profile_frg;
import app.com.arresto.arresto_connect.ui.fragments.Notification_frag;
import app.com.arresto.arresto_connect.ui.fragments.Setting_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.UsersList;
import app.com.arresto.arresto_connect.ui.fragments.VisitedDates_fragment;
import app.com.arresto.arresto_connect.ui.modules.add_data.AddAssetFragment;
import app.com.arresto.arresto_connect.ui.modules.add_data.AddSubAsset_Fragment;
import app.com.arresto.arresto_connect.ui.modules.add_data.Add_Asset_Series_Fragment;
import app.com.arresto.arresto_connect.ui.modules.add_data.Add_SMS_Component_Fragment;
import app.com.arresto.arresto_connect.ui.modules.add_data.Add_Site;
import app.com.arresto.arresto_connect.ui.modules.add_data.Add_masterData;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Projects_Fragment;
import app.com.arresto.arresto_connect.ui.modules.factory_data.AttachSensor;
import app.com.arresto.arresto_connect.ui.modules.factory_data.UpdateRFID;
import app.com.arresto.arresto_connect.ui.modules.factory_data.Update_FactoryMaster;
import app.com.arresto.arresto_connect.ui.modules.inspection.Recently_Inspected_page;
import app.com.arresto.arresto_connect.ui.modules.kowledge_tree.Karam_infonet_frg;
import app.com.arresto.arresto_connect.ui.modules.rams.Projects_fragment;
import app.com.arresto.arresto_connect.ui.modules.safety_management.Safety_Select_Forms;

import app.com.arresto.arresto_connect.ui.modules.sensor.ui.Add_SensorFragment;
import app.com.arresto.arresto_connect.ui.modules.sensor.ui.DeviceListFragment;
import app.com.arresto.arresto_connect.ui.modules.training.Training_Home;
import app.com.arresto.arresto_connect.ui.modules.warehouse.Scan_warehouse_asset;

public class HomeActivity extends BaseActivity implements View.OnClickListener {
    public static HomeActivity homeActivity;
    LinearLayout cmove_layout;
    private long mLastClickTime = 0;
    FrameLayout frgmnt_lay;
    NavigationView nav_view;

    ExpandableListAdapter mMenuAdapter;
    ExpandableListView expandableList;
    List<ExpandedMenuModel> listDataHeader;

    String uData;
    boolean isNotification = false;

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("TAG", "onStart:HomeActivity ");
        isStoragePermissionGranted();
        if (!Check_permissions.hasPermissions(this, OTHER_PERMISSIONS)) {
            Check_permissions.request_permissions(this, OTHER_PERMISSIONS);
        }
        if (Profile_Model.getInstance().getUpro_first_name() != null) {
            check_intent();
        }
        if (getIntent().getExtras() != null) {
            String notific_click = getIntent().getExtras().getString("key");
            if (notific_click != null && notific_click.equals("notification")) {
                isNotification = true;
            }
        }
        if (scan_rfid == null) {
            scan_rfid = new Scan_RFID(this);
        }
        create_EPCInstance();
    }

    @Override
    protected int getLayoutResourceId() {
        setAppTheme();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("Profile update"));
        return R.layout.home_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeActivity = this;
        Log.d("TAG", "onStart:HomeActivity ");
        getHelpVideoData(null);
        Static_values.downloaded_sites = AppController.getInstance().getDatabase().getSites_data_Dao().getAllSitesId(user_id, client_id);
        addAutoStartup(this);
        all_Ids();
        show_showCase();
        if (isTablet(this)) {
            DrawerLayout.LayoutParams params = (DrawerLayout.LayoutParams) nav_view.getLayoutParams();
            params.width = (int) (getDisplayWidth(this) / 1.8);
            nav_view.setLayoutParams(params);
        }

//        AppUtils.setupUI(frgmnt_lay, this);
        fragContainer = R.id._lay2;
        getLocalData();

        String user_name = getResString("lbl_welcm_txt") + Profile_Model.getInstance().getUpro_first_name() + " " + Profile_Model.getInstance().getUpro_last_name();
        user_txt.setText(user_name);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerSlide(View drawerView, float offset) {
                cmove_layout.setTranslationX(offset * drawerView.getWidth());
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        if (isNetworkAvailable(this))
            get_profileData(true);
        else {
            load_home_fragment(true);
        }

        File imageDirectory = new File(directory);
        if (!imageDirectory.exists())
            imageDirectory.mkdirs();

        initLocation();
        if (!group_id.equals("19") && !client_id.equals("2069") && !client_id.equals("931") && !client_id.equals("952") && !client_id.equals("947") && !Fl_CLIENT_ID.equals("")) {
            String sensor_permission = mPrefrence.getData(SENSOR_PERMISSION);
            if (sensor_permission.equals("")) {
                show_OkAlert("Sensor Permission", "Please allow background services to enable the app to update " + getResString("app_name") + "  sensor data to cloud", "Yes", "No", new AlertClickListener() {
                    @Override
                    public void onYesClick() {
                        mPrefrence.saveData(SENSOR_PERMISSION, "Yes");
                    }

                    @Override
                    public void onNoClick() {
                        mPrefrence.saveData(SENSOR_PERMISSION, "No");
                    }
                });
            }
        }
        if (!client_id.equals("2069") && !client_id.equals("952") && !client_id.equals("931")) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startLocationBackgroundService();
            } else {
                request_LocationPermission(this);
            }
        } else if (!Check_permissions.hasPermissions(this, LOCATION_PERMISSIONS)) {
            Check_permissions.request_permissions(this, LOCATION_PERMISSIONS);
        }
//        To fetch usb connection event uncomment this code nothing else to do
//        IntentFilter filter = new IntentFilter();
//        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
//        registerReceiver(mUsbReceiver, filter);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean updated = intent.getBooleanExtra("isUpdate", false);
            printLog("Homeactivity BroadcastReceiver run");
            if (updated) {
                set_profiledata(true);
                load_home_fragment(true);
            }
        }
    };


    public boolean check_intent() {
        if (!AppController.get_Deeplink().equals("")) {
            uData = AppController.get_Deeplink();
        } else {
            Uri data = getIntent().getData();
            if (data != null) {
                uData = data.toString();
                printLog(uData);
            }
        }
        if (uData != null && !uData.equals("")) {
            if (uData.contains("knowledgetree")) {
                load_fragment(Karam_infonet_frg.newInstance(uData), getResString("lbl_knowledge_tree"));
            } else {
                Uri uri = Uri.parse(uData);
                Log.e("data is ", "master_id=" + uri.getQueryParameter("m"));
                Log.e("data is ", "uin = " + uri.getQueryParameter("u"));
                Main_Fragment mainFragment = new Main_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("page_type", "inspection");
                bundle.putString("uin", uri.getQueryParameter("u"));
                bundle.putString("mdata_id", uri.getQueryParameter("m"));
                mainFragment.setArguments(bundle);
                load_fragment(mainFragment, getResString("lbl_dashbrd_txt"));
            }
            AppController.set_Deeplink("");
            return true;
        }
        return false;
    }

    private void show_showCase() {
        ArrayList<String> mesages = new ArrayList<>(Arrays.asList(getResString("lbl_click_to_open_menu")));
        ArrayList<View> views = new ArrayList<>(Arrays.asList(menuButton));
        Add_Showcase.newInstance(HomeActivity.this).setData(mesages, views);
    }

    public void setup_menu() {
        setupDrawerContent(nav_view);
        setListData();
        mMenuAdapter = new ExpandableListAdapter(this, listDataHeader, expandableList);
        // setting list adapter
        expandableList.setAdapter(mMenuAdapter);
        expandableList.setBackgroundColor(Dynamic_Var.getInstance().getApp_text());
        expandableList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                String tag = (listDataHeader.get(i).getChilds().get(i1).getTag());
                if (denied_permissions.contains(tag) || denied_permissions.contains(listDataHeader.get(i).getTag())) {
                    Toast.makeText(HomeActivity.this, getResString("lbl_contact_to_admin"), Toast.LENGTH_SHORT).show();
                } else
                    submit_action(listDataHeader.get(i).getChilds().get(i1).getTag());
                return false;
            }
        });
        expandableList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView expandableListView, View view, int i, long l) {
                if (listDataHeader.get(i).getChilds() == null || listDataHeader.get(i).getChilds().size() < 1) {
                    if (denied_permissions.contains(listDataHeader.get(i).getTag())) {
                        Toast.makeText(HomeActivity.this, getResString("lbl_contact_to_admin"), Toast.LENGTH_SHORT).show();
                    } else
                        submit_action(listDataHeader.get(i).getTag());
                }
                return false;
            }
        });
        expandableList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    expandableList.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }
        });
    }

    private void getLocalData() {
        Profile_Model.getInstance().setUpro_first_name(mPrefrence.getData(USER_FNAME));
        Profile_Model.getInstance().setUpro_last_name(mPrefrence.getData(USER_LNAME));
        client_id = mPrefrence.getData(CLIENT_ID);
        user_id = mPrefrence.getData(USER_ID);
        role_id = mPrefrence.getData(ROLE_ID);
        user_email = mPrefrence.getData(EMAIL);
        user_typ = mPrefrence.getData(USERTYPE);
        group_id = mPrefrence.getData(GROUP_ID);
    }


    public TextView user_txt, email_tv, user_type_tv;
    ImageView profil_pic;

    public void all_Ids() {
        drawer = findViewById(R.id.drawer_layout);
        expandableList = findViewById(R.id.navigationmenu);
        menuButton = findViewById(R.id.drawer_img_main);
        upload_btn = findViewById(R.id.upload_btn);
        cmove_layout = findViewById(R.id.move_layout);
        frgmnt_lay = findViewById(R.id._lay2);
        nav_view = findViewById(R.id.nav_view);
        headerTv = findViewById(R.id.top_text);
        user_txt = findViewById(R.id.usr_name);
        email_tv = findViewById(R.id.email_tv);
        user_type_tv = findViewById(R.id.user_type_tv);

        profil_pic = findViewById(R.id.profil_pic);
        back_button = findViewById(R.id.back_icn);

        buttonsClickable();

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            new GetVersionCode(this, pInfo.versionName, getPackageName(), false).execute();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        ImageViewCompat.setImageTintList(menuButton, ColorStateList.valueOf(Dynamic_Var.getInstance().getApp_background()));
        ImageViewCompat.setImageTintList(upload_btn, ColorStateList.valueOf(Dynamic_Var.getInstance().getApp_background()));
    }

    public void buttonsClickable() {
        menuButton.setOnClickListener(this);
        back_button.setOnClickListener(this);
//     Code to remove auto click when usb device scanned barcode
//        menuButton.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View view, int i, KeyEvent keyEvent) {
//                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                    printLog("-Listener-" + "the onKeyDown in Listenr");
//                }
//                return false;
//            }
//        });
    }

    public void move_drawer() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    void close_drawer() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    public void dueFragment(String filter, String page_name, String page_type) {
        Group_Filter_fragment group_filter_fragment = new Group_Filter_fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("page_type", page_type);
        bundle1.putString("group_userId", user_id);
        bundle1.putString("filter", filter);
        group_filter_fragment.setArguments(bundle1);
        LoadFragment.replace(group_filter_fragment, this, page_name);
    }


    public void submit_action(String tag) {
        //todo drawer_actions
        Main_Fragment main_fragment = new Main_Fragment();
        Bundle bundle = new Bundle();
        switch (tag) {
            case "dashboard":
                close_drawer();
                back_button.setVisibility(View.GONE);
                menuButton.setVisibility(View.VISIBLE);
                load_home_fragment(false);
                break;
            case "welcome":
                close_drawer();
                load_fragment(new Home_Fragment(), getResString("lbl_welcm_txt"));
                break;

            case "register":
                close_drawer();
                bundle.putString("page_type", "inspection");
                main_fragment.setArguments(bundle);
                load_fragment(main_fragment, getResString("lbl_rgistr_nw_txt"));
                break;

            case "myAsset":
                close_drawer();
                if (group_id.equals("18")) {
                    bundle.putString("page_type", "myassets");
                    main_fragment.setArguments(bundle);
                    load_fragment(main_fragment, getResString("lbl_my_sites"));
                } else {
                    dueFragment("register", getResString("lbl_my_asst_txt"), "myassets");
                }
                break;

            case "due":
                close_drawer();
                dueFragment("due", getResString("lbl_inspctndue_txt"), "myassets");
//                bundle.putString("page_type", "myassets");
//                bundle.putString("filter", "due");
//                main_fragment.setArguments(bundle);
//                load_fragment(main_fragment, getResString("lbl_inspctndue_txt"));
                break;

            case "overdue":
                close_drawer();
                dueFragment("over", getResString("lbl_inspctndue_txt"), "myassets");
//                bundle.putString("page_type", "myassets");
//                bundle.putString("filter", "over");
//                main_fragment.setArguments(bundle);
//                load_fragment(main_fragment, getResString("lbl_ins_ovrdue_st"));
                break;

            case "strt_pdm":
                close_drawer();
                bundle.putString("page_type", "periodic");
                main_fragment.setArguments(bundle);
                load_fragment(main_fragment, getResString("lbl_preiodic_txt"));
//                    show_Dialog();
                break;

            case "ins_scan":
                close_drawer();
                bundle.putString("page_type", "inspection");
                main_fragment.setArguments(bundle);
                load_fragment(main_fragment, getResString("lbl_scan_txt"));
                break;

            case "users":
                close_drawer();
                load_fragment(new UsersList(), getResString("lbl_registered_users"));
                break;

            case "mng_site":
                close_drawer();
                bundle.putString("page_type", "manage_site");
                main_fragment.setArguments(bundle);
                load_fragment(main_fragment, getResString("lbl_mnag_sit_txt"));
                break;

            case "schedule":
                close_drawer();
                bundle.putString("page_type", "scheduler");
                bundle.putString("filter", "scheduled");
                main_fragment.setArguments(bundle);
                load_fragment(main_fragment, getResString("lbl_scheduler_txt"));
                break;

            case "recently_inspected":
                close_drawer();
                LoadFragment.replace(new Recently_Inspected_page(), this, getResString("lbl_recently_inspected"));
                break;
            case "recently_maintained":
                close_drawer();
                LoadFragment.replace(new Recently_Inspected_page(), this, getResString("lbl_recently_maintained"));
                break;

            case "my_store":
                close_drawer();
                if (group_id.equals("15")) {
                    LoadFragment.replace(new Projects_fragment(), this, getResString("lbl_projects_txt"));
                } else if (group_id.equals("13")) {
                    bundle.putString("page_type", "addStore");
                    bundle.putString("filter", "total");
                    main_fragment.setArguments(bundle);
                    load_fragment(main_fragment, getResString("lbl_mystr_txt"));
                }
                break;

            case "pdm_report":
                close_drawer();
                bundle.putString("id", "pdm_report");
                bundle.putInt("index", 1);
                bundle.putString("pdm_url", All_Api.post_pdm_ins_list + client_id + "&inspector_id=" + user_id + "&cgrp_id=" + role_id);
                Close_projectFragment close_projectFragment = new Close_projectFragment();
                close_projectFragment.setArguments(bundle);
                LoadFragment.replace(close_projectFragment, this, getResString("lbl_maintenance_report"));
                break;
            case "pdm_dshboard":
                close_drawer();
                if (group_id.equals("8") || group_id.equals("3")) {
                    bundle.putString("page_type", "client_periodic");
                    main_fragment.setArguments(bundle);
                    load_fragment(main_fragment, getResString("lbl_preiodic_txt"));
                }
                break;
            case "pdm":
                close_drawer();
                if (group_id.equals("7") || group_id.equals("1")) {
                    bundle.putString("page_type", "client_periodic");
                    main_fragment.setArguments(bundle);
                    load_fragment(main_fragment, getResString("lbl_preiodic_txt"));
                }
                break;
            case "competent":
                close_drawer();
                if (group_id.equals("1")) {
                    inspection_alert();
                }
                break;

            case "rams_report":
                close_drawer();
                bundle.putString("id", "asm_report");
                bundle.putInt("index", 0);
                bundle.putString("asm_url", All_Api.asm_ins_list + client_id + "&user_id=" + user_id);
                Close_projectFragment close_projectFragment0 = new Close_projectFragment();
                close_projectFragment0.setArguments(bundle);
                LoadFragment.replace(close_projectFragment0, this, getResString("lbl_rams_report"));
                break;

            case "safty_add":
                close_drawer();
                load_fragment(new Safety_Select_Forms(), "Safety Management");
                break;
            case "safety_report":
                bundle.putString("id", "safety");
                bundle.putInt("index", 0);
                bundle.putString("safety_url", All_Api.safety_report_list + client_id + "&user_id=" + user_id);
                Close_projectFragment safety_report = new Close_projectFragment();
                safety_report.setArguments(bundle);
                load_fragment(safety_report, getResString("lbl_safety_management_report"));
                close_drawer();
                break;
            case "kdt":
                load_fragment(new Karam_infonet_frg(), getResString("lbl_knowledge_tree"));
                close_drawer();
                break;
            case "ec_quote":
                SelectLifeLineDialog();
                close_drawer();
                break;
            case "ec_project":
                load_fragment(new EC_Projects_Fragment(), getResString("lbl_ec_project"));
                close_drawer();
                break;
            case "ec_report":
                load_fragment(new EC_Projects_Fragment(), getResString("lbl_ec_reports"));
                close_drawer();
                break;
            case "ins_addAsset":
                close_drawer();
//                load_fragment(new AddAssetPager(), getResString("lbl_add_asset"));
                load_fragment(new AddAssetFragment(), getResString("lbl_add_asset_details"));
                break;
            case "ins_addAssetSeries":
                close_drawer();
//                load_fragment(new AddAssetPager(), getResString("lbl_add_asset"));
                load_fragment(new Add_Asset_Series_Fragment(), getResString("lbl_add_asset_kit"));
                break;

            case "ins_addSite":
                close_drawer();
//                load_fragment(new AddAssetPager(), getResString("lbl_add_asset"));
                load_fragment(new Add_Site(), getResString("lbl_add_site_id_data"));
                break;
            case "ins_addsubAsset":
                close_drawer();
                load_fragment(AddSubAsset_Fragment.newInstance(), getResString("ins_addsubAsset"));
                break;
            case "ins_addMaster":
                close_drawer();
//                Report_webview report_webview1 = new Report_webview();
//                bundle.putString("url", All_Api.add_master + client_id + "&user_id=" + user_id);
//                report_webview1.setArguments(bundle);
                LoadFragment.replace(Add_masterData.newInstance("add_master", null), this, getResString("lbl_add_master_data"));
                break;
            case "ins_addSms":
                close_drawer();
                LoadFragment.replace(Add_SMS_Component_Fragment.newInstance(null), this, getResString("lbl_add_sms_component"));
                break;

            case "assign_users":
                close_drawer();
                load_fragment(new Scan_warehouse_asset(), getResString("lbl_assign_users"));
                break;
            case "return_factory":
                close_drawer();
                load_fragment(Scan_warehouse_asset.newInstance("returnAsset"), getResString("lbl_return_warehouse"));
                break;

            case "by_qr":
                close_drawer();
                load_fragment(new Update_FactoryMaster(), getResString("lbl_add_asset"));
                break;
            case "by_serial":
                close_drawer();
                load_fragment(new UpdateRFID(), getResString("lbl_add_asset"));
                break;
            case "attach_Sensor":
                close_drawer();
                load_fragment(new AttachSensor(), getResString("lbl_attach_sensor"));
                break;
//            case "updateRFID":
//                close_drawer();
//                load_fragment(new Factory_data_frg(), "Update RFID");
//                break;
            case "warehouse_report":
                bundle.putString("id", "warehouse_report");
                bundle.putInt("index", 2);
                Close_projectFragment close_project = new Close_projectFragment();
                close_project.setArguments(bundle);
                load_fragment(close_project, getResString("lbl_warehouse_report"));
                close_drawer();
                break;
            case "ins_report":
                bundle.putString("id", "report");
                bundle.putInt("index", 2);
                Close_projectFragment close_projectFragment1 = new Close_projectFragment();
                close_projectFragment1.setArguments(bundle);
                load_fragment(close_projectFragment1, getResString("lbl_inspection_report"));
                close_drawer();
                break;

            case "drone":
            case "sensor":
                close_drawer();
//                load_fragment(new BlankFragment(), getResString("lbl_drone"));
                load_fragment(new BlankFragment(), tag.substring(0, 1).toUpperCase() + tag.substring(1));
                break;

            case "add_sensor":
                close_drawer();
                load_fragment(new Add_SensorFragment(), getResString("lbl_add_sensor"));
                break;
            case "sensor_list":
                close_drawer();
                load_fragment(new DeviceListFragment(), getResString("lbl_sensor_discovery"));
                break;

            case "sensor_gyro":
                close_drawer();
                load_fragment(new DeviceListFragment(), getResString("lbl_gyroscope_accelerometer"));
                break;

            case "write_nfc":
                close_drawer();
                load_fragment(Update_FactoryMaster.newInstance("scan_write"), getResString("lbl_scan_write_rfid"));
                break;

            case "visits":
                close_drawer();
                load_fragment(new VisitedDates_fragment(), getResString("lbl_visits"));
                break;

            case "training":
                close_drawer();
                load_fragment(new Training_Home(), getResString("lbl_training_st"));
                break;

            case "message":
                load_fragment(new Notification_frag(), getResString("lbl_message_txt"));
                close_drawer();
                break;

            case "profile":
                load_fragment(new My_profile_frg(), getResString("lbl_profile"));
                close_drawer();
                break;

            case "setting":
                load_fragment(new Setting_Fragment(), getResString("lbl_action_settings"));
                close_drawer();
                break;
            case "dealer_info":
                fetch_dealer_data();
                close_drawer();
                break;
            case "contact":
//                Client_model client_model = new Client_model();
//                client_model.setClientName("KStrong");
//                client_model.setClientAddress("150-N. Radnor Chester Road, Suite F2001500 N.Radnor Chester Road, Radnor, PA 19087)");
//                client_model.setClientContactNo("1-833-KSTRONG (578-7664)");
//                client_model.setClientContactPerson("XYZ");
//                client_model.setClientContactPersonEmail("contact@kstrong.com");
//                client_model.setClient_website("https://kstrong.com/");
//                client_model.setClient_support("support@arresto.in");
                load_fragment(Dealer_detailFragment.newInstance(null, "contact"), getResString("lbl_contact_us"));
                close_drawer();
                break;
            case "logout":
                new NetworkRequest().save_logs((All_Api.logs_api + Static_values.user_id + "&eventType=" + user_email + " logout from Device " +
                        Build.BRAND + " " + Build.MODEL));
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mPrefrence.saveBoolean(IS_LOGIN, false);
                close_drawer();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (SystemClock.elapsedRealtime() - mLastClickTime > 500) {
            switch (v.getId()) {
                case R.id.back_icn:
                    onBackPressed();
                    break;
                case R.id.profil_pic:
                case R.id.usr_name:
                    load_fragment(new My_profile_frg(), getResString("lbl_profile"));
                    close_drawer();
                    break;
                case R.id.drawer_img_main:
                    set_profiledata(false);
                    move_drawer();
                    break;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
        }
    }

    public void load_fragment(final Fragment fragment, String tag) {
        clear_all_fragment();
        LoadFragment.replace(fragment, this, tag);
//        LoadFragment.add(fragment, this, tag);
    }

    public void clear_all_fragment() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count > 0) {
            getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    public void set_profiledata(boolean is_show) {
        Profile_Model profile = Profile_Model.getInstance();
        String usr_nam = profile.getUpro_first_name() + " " + profile.getUpro_last_name();
        user_txt.setText(usr_nam);
        email_tv.setText(user_email);
        user_type_tv.setText("(" + user_typ + ")");
        AppUtils.load_profile(profile.getUpro_image(), profil_pic);

        if (!logo_url.equals("")) {
            ImageView logo_img = findViewById(R.id.logo_img);
            load_image_file(logo_url, logo_img);
        }
//        if (is_show && (profile.getUpro_address().equals("") || profile.getUpro_country_id().equals("") || profile.getUpro_company().equals(""))) {
        if (is_show && (profile.getUpro_company().equals("") || profile.getUpro_first_name().equals("") || profile.getUpro_last_name().equals(""))) {
            isProfile = true;
            complete_profile();
        }
    }

    public void complete_profile() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog;
        builder.setTitle(getResString("lbl_incomplete_profile"));
        builder.setMessage(getResString("lbl_please_complete_your_profile"));
        // add a button
        builder.setNegativeButton(getResString("lbl_cncl_st"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                isProfile = false;
                dialog.dismiss();
            }
        });
        builder.setPositiveButton(getResString("lbl_ok_st"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if (Add_Showcase.getInstance(HomeActivity.this).guideView != null)
                    Add_Showcase.getInstance(HomeActivity.this).guideView.dismiss();
                submit_action("profile");
            }
        });
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        printLog("onNewIntent " + intent.getAction());
        if (scan_rfid != null)
            scan_rfid.set_intent(intent);
    }

    public void create_EPCInstance() {
        if (mUhfrManager != null)
            return;
//        try {
//            SerialPort sSerialPort = new SerialPort(13, 115200, 0);
//            sSerialPort.close(13);
//            if (Build.VERSION.SDK_INT >= 29) {
//                ScanUtil instance = ScanUtil.getInstance(this);
//                instance.disableScanKey("134");
//            }
//            mUhfrManager = UHFRManager.getInstance();
//        } catch (IOException | SecurityException var2) {
//            var2.printStackTrace();
//            mUhfrManager = null;
//        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (scan_rfid.getIntent() != null && write_interface != null) {
            String msg = scan_rfid.write_text(text_toWrite);
            if (msg != null) {
                if (msg.contains("Success")) {
                    write_interface.write_complete();
                    write_interface = null;
                } else {
                    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                }
            }
        } else if (read_listner != null) {
            read_listner.read_complete(scan_rfid.get_RfidTAG());
            read_listner = null;
            StopReadUhfrRFID();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
//        scan_rfid.dissable_adapter();
        scan_rfid = null;
    }


    ArrayList<String> denied_permissions;

    public void load_home_fragment(boolean check) {
        String tag = "";
        String page_type = "";
        denied_permissions = new ArrayList<>();
        clear_all_fragment();
        switch (group_id) {
            case "1": // public
                denied_permissions.add("schedule");
                denied_permissions.add("pdm");
                denied_permissions.add("inspection");
//                denied_permissions.add("safety");
                denied_permissions.add("rams");
                denied_permissions.add("factory");
                denied_permissions.add("ec");
                page_type = "inspection";
                tag = getResString("lbl_dashbrd_txt");
                break;
            case "7": //dealer
                denied_permissions.add("inspection");
            case "8"://Client
                denied_permissions.add("schedule");
                denied_permissions.add("safety");
                denied_permissions.add("rams");
                denied_permissions.add("factory");
                denied_permissions.add("ec");
                tag = getResString("lbl_dashbrd_txt");
                page_type = "inspection";
                break;
            case "9"://inspector
                denied_permissions.add("register");
                denied_permissions.add("myAsset");
                denied_permissions.add("rams");
                denied_permissions.add("safety");
                denied_permissions.add("factory");
                page_type = "inspection";
                tag = getResString("lbl_dashbrd_txt");
                break;

            case "12":
                denied_permissions.add("schedule");
                denied_permissions.add("register");
                denied_permissions.add("inspection");
                denied_permissions.add("safety");
                denied_permissions.add("rams");
                denied_permissions.add("pdm");
                denied_permissions.add("myAsset");
//                denied_permissions.add("by_serial");
                denied_permissions.add("ec");
                page_type = "factory_addStore";
                tag = getResString("lbl_dashbrd_txt");
                break;

            case "13":
                denied_permissions.add("schedule");
                denied_permissions.add("register");
                denied_permissions.add("inspection");
                denied_permissions.add("safety");
                denied_permissions.add("pdm");
                denied_permissions.add("factory");
                denied_permissions.add("myAsset");
                denied_permissions.add("rams_report");
                denied_permissions.add("ec");
                page_type = "addStore";
                tag = getResString("lbl_dashbrd_txt");
                break;
            case "15":
                NetworkRequest.get_projects_data(this, All_Api.project_list + user_id + "&client_id=" + client_id, false, new Handler());
                denied_permissions.add("schedule");
                denied_permissions.add("register");
                denied_permissions.add("inspection");
                denied_permissions.add("pdm");
                denied_permissions.add("factory");
                denied_permissions.add("myAsset");
                page_type = "addStore";
                tag = getResString("lbl_dashbrd_txt");
                DataHolder_Model.getInstance().setCustomViews_models(new ArrayList<CustomForm_Model>());
                NetworkRequest.getCustom_Data(this, All_Api.custom_forms + client_id, "forms", new Handler());
                break;
            case "20": // Warehouse
                denied_permissions.add("schedule");
                denied_permissions.add("register");
                denied_permissions.add("inspection");
                denied_permissions.add("myAsset");
                denied_permissions.add("safety");
                denied_permissions.add("dealer_info");
                denied_permissions.add("pdm");
                denied_permissions.add("rams");
                denied_permissions.add("factory");
                denied_permissions.add("ec");
                denied_permissions.add("drone");
                denied_permissions.add("sensor");
                denied_permissions.add("write_nfc");
                denied_permissions.add("visits");
                page_type = "warehouse";
                tag = getResString("lbl_dashbrd_txt");
                break;
            case "18": //supervisor
//                denied_permissions.add("schedule");
                denied_permissions.add("register");
//                denied_permissions.add("inspection");
//                denied_permissions.add("myAsset");
                denied_permissions.add("kdt");
                denied_permissions.add("safety");
                denied_permissions.add("dealer_info");
                denied_permissions.add("pdm");
                denied_permissions.add("rams");
                denied_permissions.add("factory");
                denied_permissions.add("ec");
                denied_permissions.add("drone");
                denied_permissions.add("sensor");
                page_type = "inspection";
                tag = getResString("lbl_dashbrd_txt");
                DataHolder_Model.getInstance().setCustomViews_models(new ArrayList<CustomForm_Model>());
                NetworkRequest.getCustom_Data(this, All_Api.custom_forms + client_id, "forms", new Handler());
                break;

            case "19": //supervisor
                denied_permissions.add("dashboard");
                denied_permissions.add("pdm");
                denied_permissions.add("inspection");
                denied_permissions.add("competent");
                denied_permissions.add("myAsset");
                denied_permissions.add("register");
                denied_permissions.add("dealer_info");
                denied_permissions.add("schedule");
//              denied_permissions.add("kdt");
//              denied_permissions.add("ec");
                denied_permissions.add("safety");
                denied_permissions.add("rams");
                denied_permissions.add("factory");
                denied_permissions.add("drone");
                denied_permissions.add("sensor");
                page_type = "Engineering Calculation";
                tag = getResString("lbl_ec_project");
                break;
        }
        if (client_id.equals("2072") || Fl_CLIENT_ID.equals("2072")) { // SNN for KT only
            denied_permissions.add("pdm");
            denied_permissions.add("inspection");
            denied_permissions.add("competent");
            denied_permissions.add("myAsset");
            denied_permissions.add("register");
            denied_permissions.add("dealer_info");
            denied_permissions.add("ec");
            denied_permissions.add("safety");
            denied_permissions.add("rams");
            denied_permissions.add("factory");
            denied_permissions.add("drone");
            denied_permissions.add("sensor");
        }
        else if (client_id.equals("4096") || Fl_CLIENT_ID.equals("4096")) { // Kratos (Ks calculator) only
            denied_permissions.add("pdm");
            denied_permissions.add("myAsset");
            denied_permissions.add("register");
            denied_permissions.add("schedule");
            denied_permissions.add("dealer_info");
            denied_permissions.add("safety");
            denied_permissions.add("rams");
            denied_permissions.add("factory");
            denied_permissions.add("drone");
            denied_permissions.add("sensor");
            denied_permissions.add("visits");
            denied_permissions.add("write_nfc");
            page_type = "inspection";
            tag = getResString("lbl_dashbrd_txt");
        }
        else if (client_id.equals("947") || Fl_CLIENT_ID.equals("947")) { // Dewalt only
            denied_permissions.add("safety");
            denied_permissions.add("drone");
            denied_permissions.add("sensor");
//            denied_permissions.add("write_nfc");
            denied_permissions.add("competent");
            denied_permissions.add("dealer_info");
            denied_permissions.add("visits");
            if (group_id.equals("9"))
                denied_permissions.add("ec");
            page_type = "inspection";
            tag = getResString("lbl_dashbrd_txt");
        }
        else if (client_id.equals("931") || Fl_CLIENT_ID.equals("931")) { // Steelpro only
            denied_permissions.add("safety");
            denied_permissions.add("drone");
            denied_permissions.add("sensor");
            denied_permissions.add("write_nfc");
            denied_permissions.add("visits");
            page_type = "inspection";
            tag = getResString("lbl_dashbrd_txt");
        }

        if (group_id.equals("1") && (client_id.equals("376") || Fl_CLIENT_ID.equals("376"))) {
            denied_permissions.add("dashboard");
            denied_permissions.add("register");
            denied_permissions.add("safety");
            denied_permissions.add("competent");
            denied_permissions.add("dealer_info");
            denied_permissions.add("drone");
            denied_permissions.add("sensor");
            denied_permissions.add("write_nfc");
            denied_permissions.add("visits");
        }


        if (isNotification) {
            load_fragment(new Karam_infonet_frg(), getResString("lbl_knowledge_tree"));
            isNotification = false;
        } else if (client_id.equals("2072") || Fl_CLIENT_ID.equals("2072")) {//snn
            denied_permissions.add("pdm");
            denied_permissions.add("inspection");
            denied_permissions.add("ec");
            denied_permissions.add("safety");
            denied_permissions.add("rams");
            denied_permissions.add("factory");
            load_fragment(new Karam_infonet_frg(), tag);
        } else if (check_intent() || (uData != null && !uData.equals(""))) {
            uData = "";
            Log.e("deep linking ", " uin loading");
        } else if (!mPrefrence.getBoolean(IS_WELCOME_DONE) && check) {
            load_fragment(new Home_Fragment(), getResString("lbl_welcm_txt"));
        } else if (group_id.equals("12")) {
            load_fragment(new Update_FactoryMaster(), tag);
        } else if (group_id.equals("19")) {
            load_fragment(new EC_Projects_Fragment(), tag);
        } else {
            Main_Fragment mainFragment = new Main_Fragment();
            Bundle bundle = new Bundle();
            bundle.putString("page_type", page_type);
            mainFragment.setArguments(bundle);
            load_fragment(mainFragment, tag);
//            load_fragment(new DeviceListFragment(), getResString("lbl_sensor"));
        }
        if (check)
            setup_menu();

    }

    private void setListData() {
        //todo drawer_menuList
        listDataHeader = new ArrayList<>();

        ExpandedMenuModel wlcm = new ExpandedMenuModel();
        wlcm.setIconName(getResString("lbl_welcm_txt"));
        wlcm.setTag("welcome");
        listDataHeader.add(wlcm);
        if (!denied_permissions.contains("dashboard")) {
            ExpandedMenuModel dashbrd = new ExpandedMenuModel();
            dashbrd.setIconName(getResString("lbl_dashbrd_txt"));
            dashbrd.setTag("dashboard");
            listDataHeader.add(dashbrd);
        }
        if (!denied_permissions.contains("register")) {
            ExpandedMenuModel rgstr = new ExpandedMenuModel();
            rgstr.setIconName(getResString("lbl_rgistr_nw_txt"));
            rgstr.setTag("register");
            listDataHeader.add(rgstr);
        }
        if (!denied_permissions.contains("myAsset")) {
            ExpandedMenuModel my_ast = new ExpandedMenuModel();
            if (group_id.equals("18"))
                my_ast.setIconName(getResString("lbl_my_sites"));
            else
                my_ast.setIconName(getResString("lbl_my_asst_txt"));
            my_ast.setTag("myAsset");
            listDataHeader.add(my_ast);
        }


        if (!denied_permissions.contains("competent") && group_id.equals("1")) {
            ExpandedMenuModel competent = new ExpandedMenuModel();
            if (client_id.equals("931") || Fl_CLIENT_ID.equals("931"))
                competent.setIconName(getResString("lbl_inspection"));
            else
                competent.setIconName(getResString("lbl_competent"));
            competent.setTag("competent");
            listDataHeader.add(competent);
        }


        if (!denied_permissions.contains("inspection")) {
            if (group_id.equals("9") || group_id.equals("8")) {
                ExpandedMenuModel inspction = new ExpandedMenuModel();
                if (client_id.equals("931") || Fl_CLIENT_ID.equals("931"))
                    inspction.setIconName(getResString("lbl_inspection"));
                else
                    inspction.setIconName(getResString("lbl_competent"));
                inspction.setTag("inspection");
                listDataHeader.add(inspction);
                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_scan_txt"), "ins_scan"));
                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_mnag_sit_txt"), "mng_site"));
                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_inspctndue_txt"), "due"));
                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_ins_ovrdue_st"), "overdue"));

                if (group_id.equals("8"))
                    inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_registered_users"), "users"));
                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_inspection_report"), "ins_report"));
                if (!client_id.equals("931")) {
                    inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_add_sub_assets"), "ins_addsubAsset"));
                    inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_add_assets"), "ins_addAsset"));
                    inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_add_asset_kit"), "ins_addAssetSeries"));
                    inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_add_master_data"), "ins_addMaster"));
                    inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_add_site"), "ins_addSite"));
                    inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_add_sms_component"), "ins_addSms"));
                    inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_recently_inspected"), "recently_inspected"));
                }
                //            } else if (group_id.equals("8")) {
//                ExpandedMenuModel inspction = new ExpandedMenuModel();
//                inspction.setIconName(getResString("lbl_inspection"));
//                inspction.setTag("inspection");
//                listDataHeader.add(inspction);
//                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_scan_txt"), "ins_scan"));
//                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_inspection_report"), "ins_report"));
            } else if (group_id.equals("18")) {
                ExpandedMenuModel sp2 = new ExpandedMenuModel();
                sp2.setIconName(getResString("lbl_inspctndue_txt"));
                sp2.setTag("due");
                listDataHeader.add(sp2);
                ExpandedMenuModel sp3 = new ExpandedMenuModel();
                sp3.setIconName(getResString("lbl_ins_ovrdue_st"));
                sp3.setTag("overdue");
                listDataHeader.add(sp3);
                ExpandedMenuModel spu = new ExpandedMenuModel();
                spu.setIconName(getResString("lbl_team_users"));
                spu.setTag("users");
                listDataHeader.add(spu);
                ExpandedMenuModel sp = new ExpandedMenuModel();
                sp.setIconName(getResString("lbl_inspection_report"));
                sp.setTag("ins_report");
                listDataHeader.add(sp);

//                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_mnag_sit_txt"), "mng_site"));
//                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_inspctndue_txt"), "due"));
//                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_ins_ovrdue_st"), "overdue"));
//                inspction.addChild(new ExpandedMenuModel.Child(getResString("lbl_inspection_report"), "ins_report"));
            }
        }
        if (!denied_permissions.contains("pdm")) {
            ExpandedMenuModel pdm = new ExpandedMenuModel();
            pdm.setIconName(getResString("lbl_preiodic_txt")); // menu
            pdm.setTag("pdm");
            listDataHeader.add(pdm);
            if (group_id.equals("9")) {
                pdm.addChild(new ExpandedMenuModel.Child(getResString("lbl_strt_periodic_st"), "strt_pdm"));
                pdm.addChild(new ExpandedMenuModel.Child(getResString("lbl_maintenance_report"), "pdm_report"));
                pdm.addChild(new ExpandedMenuModel.Child(getResString("lbl_recently_maintained"), "recently_maintained"));
            } else if (group_id.equals("8") || group_id.equals("3")) {
                pdm.addChild(new ExpandedMenuModel.Child(getResString("lbl_pm_dashboard"), "pdm_dshboard"));
                pdm.addChild(new ExpandedMenuModel.Child(getResString("lbl_maintenance_report"), "pdm_report"));

            }
        }

        if (!denied_permissions.contains("schedule")) {
            ExpandedMenuModel schdul = new ExpandedMenuModel();
            if (group_id.equals("18"))
                schdul.setIconName(getResString("lbl_schd_forIns_st"));
            else
                schdul.setIconName(getResString("lbl_scheduler_txt"));
            schdul.setTag("schedule");
            listDataHeader.add(schdul);
        }

        if (group_id.equals("20")) {
            ExpandedMenuModel fctry = new ExpandedMenuModel();
            fctry.setIconName(getResString("lbl_warehouse"));
            fctry.setTag("lbl_warehouse");
            listDataHeader.add(fctry);
            fctry.addChild(new ExpandedMenuModel.Child(getResString("lbl_assign_users"), "assign_users"));
            fctry.addChild(new ExpandedMenuModel.Child(getResString("lbl_return_warehouse"), "return_factory"));
            fctry.addChild(new ExpandedMenuModel.Child(getResString("lbl_warehouse_report"), "warehouse_report"));
//            fctry.addChild(new ExpandedMenuModel.Child("Update RFID", "updateRFID"));
        }

        if (!denied_permissions.contains("kdt")) {
            ExpandedMenuModel kdt = new ExpandedMenuModel();
            kdt.setIconName(getResString("lbl_knowledge_tree"));
            kdt.setTag("kdt");
            listDataHeader.add(kdt);
        }

        if (!denied_permissions.contains("safety")) {
            ExpandedMenuModel sfty = new ExpandedMenuModel();
            sfty.setIconName(getResString("lbl_safety_txt"));
            sfty.setTag("safety");
            listDataHeader.add(sfty);
            sfty.addChild(new ExpandedMenuModel.Child(getResString("lbl_create_safety"), "safty_add"));
            sfty.addChild(new ExpandedMenuModel.Child(getResString("lbl_safety_reports"), "safety_report"));
        }

        if (!denied_permissions.contains("rams")) {
            ExpandedMenuModel rams = new ExpandedMenuModel();
            rams.setIconName(getResString("lbl_rams"));
            rams.setTag("rams");
            listDataHeader.add(rams);
            if (group_id.equalsIgnoreCase("15"))
                rams.addChild(new ExpandedMenuModel.Child(getResString("lbl_projects_txt"), "my_store"));
            else
                rams.addChild(new ExpandedMenuModel.Child(getResString("lbl_mystr_txt"), "my_store"));
            if (!denied_permissions.contains("rams_report"))
                rams.addChild(new ExpandedMenuModel.Child(getResString("lbl_rams_report"), "rams_report"));
        }

        if (!denied_permissions.contains("factory")) {
            ExpandedMenuModel fctry = new ExpandedMenuModel();
            fctry.setIconName(getResString("lbl_add_assets"));
            fctry.setTag("factory");
            listDataHeader.add(fctry);
            fctry.addChild(new ExpandedMenuModel.Child("By Qr code", "by_qr"));
            fctry.addChild(new ExpandedMenuModel.Child("By Serial No.", "by_serial"));
            fctry.addChild(new ExpandedMenuModel.Child("Attach Sensors", "attach_Sensor"));
//            fctry.addChild(new ExpandedMenuModel.Child("Update RFID", "updateRFID"));
        }
        if (!denied_permissions.contains("ec")) {
            ExpandedMenuModel ec = new ExpandedMenuModel();
            ec.setIconName(getResString("lbl_engineering_calculations"));
            ec.setTag("ec");
            listDataHeader.add(ec);
            ec.addChild(new ExpandedMenuModel.Child(getResString("lbl_ec_quote"), "ec_quote"));
            ec.addChild(new ExpandedMenuModel.Child(getResString("lbl_ec_project"), "ec_project"));
            ec.addChild(new ExpandedMenuModel.Child(getResString("lbl_ec_reports"), "ec_report"));
        }
        if (!denied_permissions.contains("dealer_info") && !client_id.equalsIgnoreCase("376")) {
            ExpandedMenuModel dealer_info = new ExpandedMenuModel();
            dealer_info.setIconName(getResString("lbl_dealer_info"));
            dealer_info.setTag("dealer_info");
            listDataHeader.add(dealer_info);
        }

        if (!denied_permissions.contains("drone")) {
            ExpandedMenuModel drone_item = new ExpandedMenuModel();
            drone_item.setIconName(getResString("lbl_drone"));
            drone_item.setTag("drone");
            listDataHeader.add(drone_item);
        }

        if (!denied_permissions.contains("sensor")) {
            ExpandedMenuModel sensors_item = new ExpandedMenuModel();
            sensors_item.setIconName(getResString("lbl_sensor"));
            sensors_item.setTag("sensor");
            if (!Fl_CLIENT_ID.equals("2069") && !client_id.equals("2069")) {
                sensors_item.addChild(new ExpandedMenuModel.Child(getResString("lbl_add_sensor"), "add_sensor"));
                sensors_item.addChild(new ExpandedMenuModel.Child(getResString("lbl_sensor_discovery"), "sensor_list"));
                if (user_id.equals("426") && client_id.equals("376"))
                    sensors_item.addChild(new ExpandedMenuModel.Child(getResString("lbl_gyroscope_accelerometer"), "sensor_gyro"));
            }
            listDataHeader.add(sensors_item);
        }

        if (!denied_permissions.contains("write_nfc")) {
            ExpandedMenuModel write_nfc = new ExpandedMenuModel();
            write_nfc.setIconName(getResString("lbl_scan_write_rfid"));
            write_nfc.setTag("write_nfc");
            listDataHeader.add(write_nfc);
        }
        if (!denied_permissions.contains("visits")) {
            ExpandedMenuModel visit_item = new ExpandedMenuModel();
            visit_item.setIconName(getResString("lbl_visits"));
            visit_item.setTag("visits");
//            sensors_item.addChild(new ExpandedMenuModel.Child(getResString("lbl_add_sensor"), "add_sensor"));
            listDataHeader.add(visit_item);
        }


        ExpandedMenuModel msg = new ExpandedMenuModel();
        msg.setIconName(getResString("lbl_message_txt"));
        msg.setTag("message");
        listDataHeader.add(msg);

//        ExpandedMenuModel training = new ExpandedMenuModel();
//        training.setIconName(getResString("lbl_training_st"));
//        training.setTag("training");
//        listDataHeader.add(training);

        ExpandedMenuModel profile = new ExpandedMenuModel();
        profile.setIconName(getResString("lbl_profile_txt"));
        profile.setTag("profile");
        listDataHeader.add(profile);

        ExpandedMenuModel setting = new ExpandedMenuModel();
        setting.setIconName(getResString("lbl_action_settings"));
        setting.setTag("setting");
        listDataHeader.add(setting);

        ExpandedMenuModel contact = new ExpandedMenuModel();
        contact.setIconName(getResString("lbl_contact_us"));
        contact.setTag("contact");
        listDataHeader.add(contact);

        ExpandedMenuModel logout = new ExpandedMenuModel();
        logout.setIconName(getResString("lbl_logout"));
        logout.setTag("logout");
        listDataHeader.add(logout);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        drawer.closeDrawers();
                        return true;
                    }
                });
    }

//    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
//        public void onReceive(Context context, Intent intent) {
//            printLog("onReceive" + "****" + intent);
//            String action = intent.getAction();
//            if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
//                printLog("USB device attached");
//                    synchronized (this) {
//                        UsbDevice   usbDevice =  intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
//                            if (usbDevice != null) {
//                                int deviceId = usbDevice.getDeviceId();
//                                int productId = usbDevice.getProductId();
//                                printLog("device id" + "****" + deviceId);
//                                printLog("product id" + "****" + productId);
//                        }
//                }
//            }
//        }
//    };

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        if (isLogin() && !client_id.equals("2069")) {
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Service_Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }
        super.onDestroy();
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1011:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startLocationBackgroundService();
                }
                break;
            case 1012:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    request_LocationPermission(this);
                }
                break;

        }
    }

}
