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

package app.com.arresto.arresto_connect.ui.fragments;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.create_addAstDialog;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.AppUtils.showUIN_Dialog;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PaginationListener.PAGE_START;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.today_Pm_data;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;
import static app.com.arresto.arresto_connect.ui.adapters.Filter_GroupAdapter.slected_group;
import static app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter.slctd_project;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.PaginationListener;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Promotion_Model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NFC_Listner;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;
import app.com.arresto.arresto_connect.ui.adapters.Asset_Adapter;
import app.com.arresto.arresto_connect.ui.adapters.Inspector_search_listadapter;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionListItems;

public class Main_Fragment extends Base_Fragment implements View.OnClickListener {
    public EditText searchView;
    public Inspector_search_listadapter inspector_search_listadapter;
    public String page_type = "";
    public TextView add_btn, ad_stor_btn, finish_project_btn, tp1, tp2, tp3, tp4, tp5;
    public TextView ttl_tv;
    public TextView dwn_excl_btn;
    String inputtype;
    View view;
    RecyclerView listView;
    boolean flag = true;
    ArrayList<String> searched_array;
    String filter;
    public String project_id;
    AppBarLayout appbar;
    public static Main_Fragment mainFragment;
    String uin, master_id;
    public static boolean is_refresh = false;
    String totalSize = "";
    String user_id, group_id, role_id;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.inspection_search, parent, false);
            mainFragment = this;
            setupUI(view, getActivity());
            project_id = "";
            searched_array = new ArrayList<>();
            site_models = new ArrayList<>();
            all_sites = new ArrayList<>();
            findAllIds(view);
            if (getArguments() != null) {
                filter = getArguments().getString("filter", "");
                page_type = getArguments().getString("page_type", "");
                user_id = getArguments().getString("user_id", Static_values.user_id);
                group_id = getArguments().getString("group_id", Static_values.group_id);
                role_id = getArguments().getString("role_id", Static_values.role_id);
                uin = getArguments().getString("uin", "");
                master_id = getArguments().getString("mdata_id", "");
                inputtype = "uin";

                if (getArguments().containsKey("total")) {
                    totalSize = getArguments().getString("total", "");
                    ttl_tv.setText(totalSize);
                    ttl_tv.setVisibility(VISIBLE);
                } else {
                    ttl_tv.setVisibility(GONE);
                }
//                uin = getArguments().getString("mdata_id", "");
            }
            InspectionListItems.selectedPosition.clear();
            setdata();
            if (uin == null || uin.equals(""))
                init_data();
            else
                get_scaned_data(uin, "");

            rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (flag) {
                        flag = false;
                        rg2.clearCheck();
                        if (group.findViewById(checkedId) != null) {
                            inputtype = "" + (group.findViewById(checkedId)).getTag();
                            if (inputtype.equals("RFID")) {
                                add_btn.setText(getResString("lbl_scan_rfid"));
                                check_scan_typ();
                            } else if (inputtype.equals("Barcode"))
                                add_btn.setText(getResString("lbl_scan_qr_code"));
                        }
                        flag = !flag;
                    }
                }
            });
            rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    if (flag) {
                        flag = false;
                        rg1.clearCheck();
                        if (group.findViewById(checkedId) != null) {
                            inputtype = "" + (group.findViewById(checkedId)).getTag();
                            if (inputtype.equals("UIN"))
                                add_btn.setText(getResString("lbl_entr_no"));
                            else if (inputtype.equals("Barcode"))
                                add_btn.setText(getResString("lbl_scan_barcode"));
                            else if (inputtype.equals("ocr"))
                                add_btn.setText(getResString("lbl_scan_label"));
                        }
                        flag = !flag;
                    }
                }
            });

            ((ViewGroup) tv1.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Static_values.group_id.equals("13")) {
                        if (tv1.getText().toString().equals("0")) {
                            show_snak(getActivity(), getResString("lbl_nostr_dta_msg"));
                        } else {
                            load_fragment("free", getResString("lbl_mystr_txt"), "myStore", tv1.getText().toString());
                        }
                    } else if (Static_values.group_id.equals("20")) {
                        if (tv1.getText().toString().equals("0")) {
                            show_snak(getActivity(), getResString("lbl_no_data_warehouse"));
                        } else {
                            groupFragment("total", getResString("lbl_total_assets"), "warehouse");
                        }
                    } else {
                        if (tv1.getText().toString().equals("0")) {
                            show_snak(getActivity(), getResString("lbl_noreg_dta_msg"));
                        } else {
                            if (Static_values.group_id.equals("9"))
                                load_fragment("register", getResString("lbl_scheduler_txt"), "scheduler", tv1.getText().toString());
                            else if (Static_values.group_id.equals("18")) {
                                load_fragment("register", getResString("lbl_my_sites"), "myassets", tv1.getText().toString());
                            } else {
                                if (page_type.equalsIgnoreCase("inspection"))
                                    groupFragment("register", getResString("lbl_my_asst_txt"), "myassets");
                                else
                                    groupFragment("register", getResString("lbl_my_asst_txt"), "page_type");
                            }
                        }
                    }
                }
            });

            ((ViewGroup) tv2.getParent()).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Static_values.group_id.equals("13")) {
                        if (tv2.getText().toString().equals("0")) {
                            show_snak(getActivity(), getResString("lbl_nouse_dta_msg"));
                        } else {
                            load_fragment("used", getResString("lbl_ast_inuse_txt"), "myStore", tv2.getText().toString());
                        }
                    } else if (Static_values.group_id.equals("20")) {
                        if (tv2.getText().toString().equals("0")) {
                            show_snak(getActivity(), getResString("lbl_no_remaining_data"));
                        } else {
                            groupFragment("remaining", getResString("lbl_remaining"), "warehouse");
                        }
                    } else {
                        if (tv2.getText().toString().equals("0")) {
                            show_snak(getActivity(), getResString("lbl_inspcted_dta_msg"));
                        } else {
                            if (group_id.equals("9") || page_type.equals("inspection"))
                                groupFragment("inspected", getResString("lbl_inspctd_st"), "myassets");
//                                load_fragment("inspected", getResString("lbl_inspctd_st"), "myassets", tv2.getText().toString());
                            else {
                                load_fragment("due", getResString("lbl_main_due_st"), "maintenanceDue", tv2.getText().toString());
                            }
                        }
                    }
                }
            });

            ((ViewGroup) tv3.getParent()).
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Static_values.group_id.equals("13")) {
                                if (tv3.getText().toString().equals("0")) {
                                    show_snak(getActivity(), getResString("lbl_nouse_dta_msg"));
                                } else {
                                    load_fragment("used", getResString("lbl_ast_inuse_txt"), "myStore", tv3.getText().toString());
                                }
                            } else if (Static_values.group_id.equals("20")) {
                                if (tv3.getText().toString().equals("0")) {
                                    show_snak(getActivity(), getResString("lbl_no_issued_data"));
                                } else {
                                    groupFragment("issued", getResString("lbl_issued_assets"), "warehouse");
                                }
                            } else {
                                if (tv3.getText().toString().equals("0")) {
                                    show_snak(getActivity(), getResString("lbl_nodue_dta_msg"));
                                } else {
                                    if (group_id.equals("9") || page_type.equals("inspection"))
                                        groupFragment("due", getResString("lbl_inspctndue_txt"), "myassets");
                                    else {
                                        load_fragment("over", getResString("lbl_main_ovdue_st"), "maintenanceDue", tv3.getText().toString());
                                    }
                                }
                            }
                        }
                    });

            ((ViewGroup) tv4.getParent()).
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            if (Static_values.group_id.equals("13")) {
//                                if (tv3.getText().toString().equals("0")) {
//                                    show_snak(getActivity(), getResString("lbl_nouse_dta_msg"));
//                                } else {
//                                    load_fragment("rejected", getResString("lbl_ast_regected_txt"), "myStore");
//                                }
//                            } else {
                            if (Static_values.group_id.equals("20")) {
                                if (tv4.getText().toString().equals("0")) {
                                    show_snak(getActivity(), getResString("lbl_no_retired_data"));
                                } else {
                                    groupFragment("retired", getResString("lbl_retired_assets"), "warehouse");
                                }
                            } else {
                                if (tv4.getText().toString().equals("0")) {
                                    show_snak(getActivity(), getResString("lbl_noOvrdue_dta_msg"));
                                } else {
                                    if (group_id.equals("9") || page_type.equals("inspection"))
                                        groupFragment("over", getResString("lbl_inspctndue_txt"), "myassets");
//                                    load_fragment("over", getResString("lbl_ins_ovrdue_st"), "myassets", tv4.getText().toString());
//                                    else
//                                        load_fragment("over", getResString("lbl_main_ovdue_st"), "maintenanceDue");
                                }
                            }
                        }
                    });
            listView.addOnScrollListener(new PaginationListener(layoutManager) {
                @Override
                protected void loadMoreItems() {
                    isLoading = true;
                    currentPage++;
                    init_data();
                }

                @Override
                public boolean isLastPage() {
                    return isLastPage;
                }

                @Override
                public boolean isLoading() {
                    return isLoading;
                }

                @Override
                public void isFirstPage(boolean b) {
                    swipe.setEnabled(b);
                }
            });

            if (!isNetworkAvailable(getActivity())) {
                add_layer.setVisibility(GONE);
            }

            if (page_type.contains("periodic") && !page_type.equals("periodic today")) {
                select_seqlite();
            }
//            AppBarLayout appBarLayout = view.findViewById(R.id.hder);
//            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) appBarLayout.getLayoutParams();
//            layoutParams.setBehavior(new AppBarLayoutNoEmptyScrollBehavior(appBarLayout, listView));
        } else {
            if (page_type.equals("addStore")) {
                searched_array = new ArrayList<>();
                if (getActivity().getSupportFragmentManager().getBackStackEntryCount() < 2)
                    DataHolder_Model.getInstance().setComponent_models(new ArrayList<>());
            }
            if (is_refresh) {
                init_data();
                is_refresh = false;
            }
        }

        show_showCase();

        return view;
    }

    // load group Fragment
    public void groupFragment(String filter, String page_name, String page_type) {
        Group_Filter_fragment group_filter_fragment = new Group_Filter_fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("page_type", page_type);
        bundle1.putString("group_userId", user_id);
        bundle1.putString("filter", filter);
        if (tv1.getVisibility() == VISIBLE && !tv1.getText().toString().equals(""))
            bundle1.putString("total", tv1.getText().toString());
        group_filter_fragment.setArguments(bundle1);
        LoadFragment.replace(group_filter_fragment, baseActivity, page_name);
    }

    /**
     * do api call here to fetch data from server
     * In example i'm adding data manually
     */

    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;
    int itemCount = 0;

    @Override
    public void onResume() {
        super.onResume();


        switch (page_type) {
            case "scheduler":
                ConstantMethods.find_pageVideo(getActivity(), "inspection scheduler");
                break;
            case "inspection":
                ConstantMethods.find_pageVideo(getActivity(), "inspection dashboard");
                break;
            case "client_periodic":
            case "maintenanceDue":
            case "periodic today":
                ConstantMethods.find_pageVideo(getActivity(), "pm dashboard");
                break;
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    ArrayList<String> messages;
    ArrayList<View> views;

    public void init_data() {
        messages = new ArrayList<>();
        views = new ArrayList<>();
        switch (page_type) {
            case "client_periodic":
                tp1.setText(getResString("lbl_my_asst_txt"));
//                tp2.setText(getResString("lbl_inspctd_st"));
                tp2.setText(getResString("lbl_main_due_st"));
                tp3.setText(getResString("lbl_main_ovdue_st"));
//                ((ViewGroup) tp4.getParent()).setVisibility(VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Static_values.group_id.equals("9"))
                            tp1.setText(getResString("lbl_scdule_prdct_st"));
                        get_sites_status(All_Api.get_maintenance_sites + user_id + "&group_id=" + Static_values.group_id + "&cgrp_id=" + role_id);
                    }
                }, 300);
                messages.addAll(new ArrayList<>(Arrays.asList(getResString("lbl_total_number_of_assets_registered"), getResString("lbl_asset_inspection_due"), getResString("lbl_asset_inspection_overdue"))));
                views.addAll(new ArrayList<>(Arrays.asList((View) tp1.getParent(), (View) tp2.getParent(), (View) tp3.getParent())));
                break;
            case "inspection":
                tp2.setText(getResString("lbl_inspctd_st"));
                tp3.setText(getResString("lbl_inspctndue_txt"));
                tp4.setText(getResString("lbl_ins_ovrdue_st"));
                if (group_id.equals("18")) {
                    tp1.setText(getResString("lbl_my_sites"));
                    tp4.setText(getResString("lbl_balance"));
                }
                ((ViewGroup) tp4.getParent()).setVisibility(VISIBLE);
                if (client_id.equals("419"))
                    ((ViewGroup) tp5.getParent()).setVisibility(VISIBLE);
//                ((LinearLayout) ttl_tv.getParent()).setVisibility(GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Static_values.group_id.equals("9"))
                            tp1.setText(getResString("lbl_scdule_prdct_st"));
                        get_sites_status(All_Api.get_due_sites + user_id + "&group_id=" + Static_values.group_id + "&cgrp_id=" + role_id);
                    }
                }, 300);

                if (group_id.equals("9")) {
                    messages.addAll(new ArrayList<>(Arrays.asList(getResString("lbl_your_scheduled_products"), getResString("lbl_asset_inspected"), getResString("lbl_scheduled_due_products"), getResString("lbl_scheduled_overdue_products"))));
                } else {
                    messages.addAll(new ArrayList<>(Arrays.asList(getResString("lbl_total_number_of_assets_registered"), getResString("lbl_asset_inspected"), getResString("lbl_asset_inspection_due"), getResString("lbl_asset_inspection_overdue"))));
                }
                views.addAll(new ArrayList<>(Arrays.asList((View) tp1.getParent(), (View) tp2.getParent(), (View) tp3.getParent(), (View) tp4.getParent())));
                break;
            case "manage_site":
            case "scheduler":
                ((LinearLayout) dwn_excl_btn.getParent()).setVisibility(GONE);
            case "myassets":
            case "maintenanceDue":
            case "inspected":
                product_hstry_lay.setVisibility(GONE);
                add_layer.setVisibility(GONE);
                map_btn.setVisibility(VISIBLE);
                serviceCall_2(user_id, "");
                break;
            case "group asset":
                product_hstry_lay.setVisibility(GONE);
                add_layer.setVisibility(GONE);
                map_btn.setVisibility(VISIBLE);
                serviceCall_2(slected_group, "");
                break;
            case "periodic today":
                product_hstry_lay.setVisibility(GONE);
                add_layer.setVisibility(GONE);
                map_btn.setVisibility(VISIBLE);
                inspector_search_listadapter = new Inspector_search_listadapter(baseActivity, today_Pm_data, page_type);
                listView.setAdapter(inspector_search_listadapter);
                ttl_tv.setText(getResString("lbl_total") + ": " + today_Pm_data.size());
                break;
            case "add_to_project":
                project_id = slctd_project.getUp_id();
            case "warehouse":
                tp1.setText(getResString("lbl_total_assets"));
                tp2.setText(getResString("lbl_remaining"));
                tp3.setText(getResString("lbl_issued_assets"));
                tp4.setText(getResString("lbl_retired_assets"));
                ((ViewGroup) tp4.getParent()).setVisibility(VISIBLE);
                ((ViewGroup) searchView.getParent()).setVisibility(GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        get_sites_status(All_Api.warehouse_count_api + user_id);
                    }
                }, 300);
                break;
            case "addStore":
                tp1.setText(getResString("lbl_mystr_txt"));
                tp2.setText(getResString("lbl_ast_inuse_txt"));
                tp3.setText(getResString("lbl_ast_regected_txt"));
                DataHolder_Model.getInstance().setComponent_models(new ArrayList<>());
                if (Static_values.group_id.equals("13")) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            get_sites_status(All_Api.get_store_count + user_id);
                        }
                    }, 300);
                    messages.addAll(new ArrayList<>(Arrays.asList(getResString("lbl_product_in_store"), getResString("lbl_product_in_use"), getResString("lbl_Rejected_products"))));
                    views.addAll(new ArrayList<>(Arrays.asList((View) tp1.getParent(), (View) tp2.getParent(), (View) tp3.getParent())));
                } else if (Static_values.group_id.equals("15")) {
                    ad_stor_btn.setText(getResString("lbl_add_to_project"));
                }
                break;
            case "myStore":
                add_layer.setVisibility(GONE);
                product_hstry_lay.setVisibility(GONE);
                get_component(user_id);
                break;
            default:
                product_hstry_lay.setVisibility(GONE);
                break;
        }
        if (add_layer.getVisibility() == VISIBLE) {
            messages.addAll(new ArrayList<>(Arrays.asList(
                    getResString("lbl_search_by_qr_code"),
                    getResString("lbl_search_by_rfid_tag"),
                    getResString("lbl_search_by_scan_label"),
                    getResString("lbl_search_by_manual_entry"),
                    getResString("lbl_scan_and_search_product"))));
            views.addAll(new ArrayList<>(Arrays.asList(rg1.getChildAt(0), rg1.getChildAt(1), rg2.getChildAt(0), rg2.getChildAt(1), add_btn)));
        }
        if (swipe.isRefreshing())
            swipe.setRefreshing(false);

    }


    public void load_fragment(String filter, String page_name, String page_type, String ttl) {
        Main_Fragment main_fragment = new Main_Fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("filter", filter);
        bundle1.putString("page_type", page_type);
        if (!ttl.equals(""))
            bundle1.putString("total", ttl);
        main_fragment.setArguments(bundle1);
        LoadFragment.replace(main_fragment, getActivity(), page_name);
    }

    TextView tv1, tv2, tv3, tv4;
    GridLayout product_hstry_lay;
    LinearLayout add_layer;
    RadioGroup rg1, rg2;
    SwipeRefreshLayout swipe;
    ImageView map_btn;

    public void findAllIds(View view) {
        ttl_tv = view.findViewById(R.id.ttl_tv);
        dwn_excl_btn = view.findViewById(R.id.dwn_excl_btn);
        appbar = view.findViewById(R.id.hder);
        add_layer = view.findViewById(R.id.add_layer);
        map_btn = view.findViewById(R.id.map_btn);
        add_layer.setVisibility(VISIBLE);
        product_hstry_lay = view.findViewById(R.id.product_hstry_lay);
        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        tv3 = view.findViewById(R.id.tv3);
        tv4 = view.findViewById(R.id.tv4);
        searchView = view.findViewById(R.id.search_view);
        listView = view.findViewById(R.id.suggestion_list);
        swipe = view.findViewById(R.id.swipe);

        rg1 = view.findViewById(R.id.rg1);
        rg2 = view.findViewById(R.id.rg2);

        add_btn = view.findViewById(R.id.add_butn);
        ad_stor_btn = view.findViewById(R.id.ad_stor_btn);
        finish_project_btn = view.findViewById(R.id.finish_project_btn);
        tp1 = view.findViewById(R.id.tp1);
        tp2 = view.findViewById(R.id.tp2);
        tp3 = view.findViewById(R.id.tp3);
        tp4 = view.findViewById(R.id.tp4);
        tp5 = view.findViewById(R.id.tp5);

        map_btn.setOnClickListener(this);
        add_btn.setOnClickListener(this);
        ad_stor_btn.setOnClickListener(this);
        finish_project_btn.setOnClickListener(this);
        dwn_excl_btn.setOnClickListener(this);
        swipe.setColorSchemeResources(R.color.app_text, R.color.app_btn_bg, R.color.app_green);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                itemCount = 0;
                currentPage = PAGE_START;
                isLastPage = false;
                inspector_search_listadapter.clear();
                init_data();
            }
        });
        ttl_tv.setText(getResString("lbl_total") + ": 0");
        if (AppUtils.isTablet(getActivity())) {
            product_hstry_lay.setColumnCount(3);
        }
        swipe.setDistanceToTriggerSync(450);
        setupList(null);
    }

    LinearLayoutManager layoutManager;

    private void setdata() {
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        layoutManager.setAutoMeasureEnabled(false);
        listView.setLayoutManager(layoutManager);
        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                if (TextUtils.isEmpty(newText)) {
                    if (page_type.equals("addStore") || page_type.equals("myStore") || page_type.equals("project_data")) {
                        asset_adapter = new Asset_Adapter(baseActivity, page_type, null);
                        listView.setAdapter(asset_adapter);
                    } else if (page_type.equals("periodic today")) {
                        ttl_tv.setText(getResString("lbl_total") + ": " + today_Pm_data.size());
                        inspector_search_listadapter = new Inspector_search_listadapter(baseActivity, today_Pm_data, page_type);
                        listView.setAdapter(inspector_search_listadapter);
                    } else {
                        ttl_tv.setText(getResString("lbl_total") + ": " + all_sites.size() + "/" + totalSize);
                        inspector_search_listadapter = new Inspector_search_listadapter(baseActivity, all_sites, page_type);
                        listView.setAdapter(inspector_search_listadapter);
                    }
                } else if (newText != null) {
                    setList_inspectorData(newText.toLowerCase());
                }
            }
        });
    }


    private void setList_inspectorData(String newText) {
        if (page_type.equals("addStore") || page_type.equals("myStore") || page_type.equals("project_data")) {
            ArrayList<Component_model> slctcomp_mdels = new ArrayList<>();
            ArrayList<Component_model> allcomp_mdels = DataHolder_Model.getInstance().getComponent_models();
            for (int i = 0; i < allcomp_mdels.size(); i++) {
                Component_model component_model = allcomp_mdels.get(i);
                if (component_model.getComponent_code().toLowerCase().contains(newText.toLowerCase())) {
                    slctcomp_mdels.add(component_model);
                }
            }
            if (asset_adapter == null) {
                asset_adapter = new Asset_Adapter(baseActivity, page_type, null);
                listView.setAdapter(asset_adapter);
                asset_adapter.addData(slctcomp_mdels);
            } else {
                asset_adapter.addData(slctcomp_mdels);
            }
        } else if (page_type.equals("periodic today")) {
            ArrayList<Site_Model> slctsite_models = new ArrayList<>();
            for (int i = 0; i < today_Pm_data.size(); i++) {
                if (today_Pm_data.get(i).getMdata_item_series().toLowerCase().contains(newText) || today_Pm_data.get(i).getMdata_uin().toLowerCase().contains(newText) || today_Pm_data.get(i).getClient_name().toLowerCase().contains(newText) ||
                        today_Pm_data.get(i).getSite_location().toLowerCase().contains(newText) || today_Pm_data.get(i).getSite_address().toLowerCase().contains(newText) || today_Pm_data.get(i).getSite_city().toLowerCase().contains(newText)) {
                    slctsite_models.add(today_Pm_data.get(i));
                }
            }
            ttl_tv.setText(getResString("lbl_total") + ": " + slctsite_models.size() + "/" + today_Pm_data);
            updateAdapter(slctsite_models);
        } else {
            ArrayList<Site_Model> slctsite_models = new ArrayList<>();
            for (int i = 0; i < all_sites.size(); i++) {
                if (all_sites.get(i).getMdata_item_series().toLowerCase().contains(newText) || all_sites.get(i).getMdata_uin().toLowerCase().contains(newText) || all_sites.get(i).getClient_name().toLowerCase().contains(newText) ||
                        all_sites.get(i).getSite_location().toLowerCase().contains(newText) || all_sites.get(i).getSite_address().toLowerCase().contains(newText) || all_sites.get(i).getSite_city().toLowerCase().contains(newText)) {
                    slctsite_models.add(all_sites.get(i));
                }
            }
            ttl_tv.setText(getResString("lbl_total") + ": " + slctsite_models.size() + "/" + totalSize);
            updateAdapter(slctsite_models);
        }
    }

    public void updateAdapter(ArrayList<Site_Model> site_models) {
        if (inspector_search_listadapter == null) {
            inspector_search_listadapter = new Inspector_search_listadapter(baseActivity, site_models, page_type);
            listView.setAdapter(inspector_search_listadapter);
        } else {
            inspector_search_listadapter.updateData(site_models);
        }
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_butn:
                check_scan_typ();
                break;
            case R.id.map_btn:
                if (site_models != null && site_models.size() > 0)
                    LoadFragment.replace(SitesMapFragment.newInstance(site_models), baseActivity, getResString("lbl_sites_on_map"));
                break;
            case R.id.dwn_excl_btn:
                if (inspector_search_listadapter != null && inspector_search_listadapter.getData().size() > 0)
                    AppUtils.createExcelSheet(getActivity(), page_type, inspector_search_listadapter.getData());
                else
                    AppUtils.show_snak(getActivity(), "No data to import.");
                break;
            case R.id.ad_stor_btn:
                if (page_type.equals("project_data") && Static_values.group_id.equals("15")) {
                    load_fragment("", getResString("lbl_add_to_project"), "add_to_project", "");
                } else if (Static_values.group_id.equals("15")) {
                    if (page_type.equals("add_to_project") && project_id != null)
                        add_to_Store(project_id);
                    else
                        create_addAstDialog(this);
                } else {
                    add_to_Store("");
                }
                break;
        }
    }

    ArrayList<Site_Model> site_models, all_sites;

    public void serviceCall_2(final String key, String optional) {
        String url;
        switch (page_type) {
            case "myassets":
                url = All_Api.myasset_api + key + "&filter=" + filter;
                break;
            case "maintenanceDue":
                url = All_Api.get_maintenance_sites + key + "&filter=" + filter;
                break;
            case "scheduler":
                url = All_Api.myasset_api + key + "&filter=" + filter;
//                url = All_Api.get_schedule + key + "&filter=" + filter;
                break;
            case "manage_site":
                url = All_Api.assigned_sites + "userID=" + key + "&userGroupID=" + Static_values.group_id;
                break;
            case "group asset": {
                if (key.equals(""))
                    url = All_Api.get_group_assets + Static_values.client_id + "&user_id=" + key;
                else
                    url = All_Api.get_group_assets + Static_values.client_id + "&user_id=" + Static_values.user_id;
            }
            break;
            default:
                if (key == null || key.equals("")) {
                    show_snak(getActivity(), getResString("lbl_srch_pram_msg"));
                    return;
                } else if (searched_array.contains(key.toLowerCase()) && (optional.equals("") || searched_array.contains(optional.toLowerCase()))) {
                    show_snak(getActivity(), getResString("lbl_alrdy_lst_msg"));
                    return;
                } else {
                    if (optional.equals("")) {
                        url = All_Api.search_Data + inputtype.toLowerCase() + "=" + key;
                        if (master_id != null && !master_id.equals(""))
                            url = url + "&master_id=" + master_id;
                    } else
                        url = All_Api.search_Data + "serial_no" + "=" + key + "&batch_no" + "=" + optional;
                }
                break;
        }

        if (!page_type.equals("group asset"))
            url = url + "&client_id=" + client_id + "&group_id=" + group_id + "&cgrp_id=" + role_id;
        if (!url.equals("")) {
            if (isNetworkAvailable(getActivity())) {
                if (!url.contains("searchSiteidBarcode"))
                    url = url + "&page=" + currentPage + "&limit=" + 30;
                url = url.replace(" ", "%20");
                clearInputType();
                new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        master_id = null;
                        Object json;
                        try {
                            json = new JSONTokener(response).nextValue();
                            if (json instanceof JSONArray) {
                                JSONArray jsonArray = new JSONArray(response);
                                JSONObject jsonObject = jsonArray.getJSONObject(0);
                                if (jsonObject.has("error")) {
                                    show_snak(getActivity(), jsonObject.getString("error"));
                                } else {
                                    searched_array.add(key.toLowerCase());
                                    GsonBuilder b = new GsonBuilder();
                                    b.registerTypeAdapter(Site_Model.class, new Site_Model.Site_Model_Deserial(inputtype));
                                    site_models = new ArrayList<>(Arrays.asList(b.create().fromJson(response, Site_Model[].class)));
//                                    scrollDown();
                                }
                            } else if (json instanceof JSONObject) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("msg_code")) {
                                    if (jsonObject.getString("msg_code").equals("200")) {
                                        if (page_type.equals("scheduler")) {
                                            site_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("scheduler_data"), Site_Model[].class)));
                                        }
                                        searched_array.add(key.toLowerCase());
                                    } else {
                                        show_snak(getActivity(), jsonObject.getString("msg"));
                                    }
                                } else if (jsonObject.has("status_code")) {
                                    if (jsonObject.getString("status_code").equals("200")) {
                                        site_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getJSONObject("data").getString("data"), Site_Model[].class)));
                                        searched_array.add(key.toLowerCase());
                                    } else if (jsonObject.getString("status_code").equals("301") && client_id.equals("1040")) {
                                        Promotion_Model promotion_model = AppUtils.getGson().fromJson(jsonObject.getString("data"), Promotion_Model.class);
                                        baseActivity.promotionApp_Dialog(promotion_model);
                                        return;
                                    } else {
                                        show_snak(getActivity(), jsonObject.getString("message"));
                                    }
                                }
                            }
                            if (!optional.equals("")) {
                                searched_array.add(optional);
                            }
                            setupList(site_models);
                            set_auto_functions();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("JSONException ", "run now " + e.getMessage());
                        }
                    }

                    @Override
                    public void onError(String error) {
                        master_id = null;
                        Log.e("error", "" + error);
                    }
                });
            } else {
                show_snak(getActivity(), AppUtils.getResString("lbl_network_alert"));
//                select_seqlite();
            }
        }
    }

    public void setupList(ArrayList<Site_Model> site_models) {
        if (site_models != null) {
            if (site_models.size() == 0)
                isLastPage = true;
            all_sites.addAll(site_models);
            if (inspector_search_listadapter != null && add_layer.getVisibility() != VISIBLE) {
                if (currentPage != PAGE_START)
                    inspector_search_listadapter.removeLoading();
                isLoading = false;
                swipe.setRefreshing(false);
                inspector_search_listadapter.addData(site_models);
            } else {
                listView.setVisibility(VISIBLE);
                inspector_search_listadapter = new Inspector_search_listadapter(baseActivity, site_models, page_type);
                listView.setAdapter(inspector_search_listadapter);
            }
            if (!isLastPage && site_models.size() > 15) {
                inspector_search_listadapter.addLoading();
            }
            ttl_tv.setText(getResString("lbl_total") + ": " + inspector_search_listadapter.getItemCount() + "/" + totalSize);
        }
    }


    public void clearInputType() {
        flag = false;
        rg1.clearCheck();
        rg2.clearCheck();
        flag = true;
        inputtype = "";
    }

    public void set_auto_functions() {
        Log.e("scroll ", "run now");
        searchView.clearFocus();
        if (site_models.size() > 0) {
            if (messages != null) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        View view = listView.getLayoutManager().findViewByPosition(0);
                        if (view == null)
                            return;
                        if (page_type.equalsIgnoreCase("Scheduler")) {
                            view.setId(R.id.scheduler_item);
                            messages.add(getResString("lbl_swipe_right_reschedule"));
                        } else {
                            view.setId(R.id.inspection_item);
                            messages.add(getResString("lbl_click_anywhere"));
                        }
                        views.add(view);
                        show_showCase();
                    }
                }, 300);
            }
//                                if (site_models.size() == 1 && !uin.equals("")) {
            if (site_models.size() > 0 && add_layer.getVisibility() == VISIBLE) {
                listView.scrollToPosition(site_models.size() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (listView.findViewHolderForAdapterPosition(site_models.size() - 1) != null)
                            listView.findViewHolderForAdapterPosition(site_models.size() - 1).itemView.performClick();
                    }
                }, 400);
            }
        }
    }

    public void get_sites_status(String url) {
        product_hstry_lay.setVisibility(VISIBLE);
        url = url + "&client_id=" + client_id;
        if (isNetworkAvailable(getActivity())) {
            url = url.replace(" ", "%20");
            NetworkRequest networkRequest = new NetworkRequest(getActivity());
            networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("status_code")) {
                                if (jsonObject.getString("status_code").equals("200")) {
                                    product_hstry_lay.setVisibility(VISIBLE);
                                    if (page_type.equals("addStore")) {
                                        tv1.setText(jsonObject.getJSONObject("data").getString("free_product"));
                                        tv2.setText(jsonObject.getJSONObject("data").getString("used_product"));
                                        tv3.setText(jsonObject.getJSONObject("data").getString("rejected_product"));
                                    } else if (page_type.equals("warehouse")) {
                                        tv1.setText(jsonObject.getJSONObject("data").getString("total"));
                                        tv2.setText(jsonObject.getJSONObject("data").getString("remaining"));
                                        tv3.setText(jsonObject.getJSONObject("data").getString("assign"));
                                        tv4.setText(jsonObject.getJSONObject("data").getString("retired"));
                                    } else if (page_type.equals("client_periodic")) {
                                        tv1.setText(jsonObject.getJSONObject("data").getString("totalCount"));
                                        tv2.setText(jsonObject.getJSONObject("data").getString("dueCount"));
                                        tv3.setText(jsonObject.getJSONObject("data").getString("overdueCount"));
                                        count_today_pm(jsonObject.getJSONObject("data").getJSONArray("data"));
                                    }
                                }
//                                else {
//                                    show_snak(getActivity(), jsonObject.getString("message"));
//                                }
                            } else if (jsonObject.has("msg_code")) {
                                if (jsonObject.getString("msg_code").equals("200")) {
                                    product_hstry_lay.setVisibility(VISIBLE);
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    tv1.setText(data.getString("product_data_count"));
                                    tv2.setText(data.getString("inspection_inspected"));
                                    tv3.setText(data.getString("inspection_due"));
                                    tv4.setText(data.getString("inspection_over_due"));
                                }
//                                else {
//                                    show_snak(getActivity(), jsonObject.getString("msg"));
//                                }
                            }
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
        } else {
            show_snak(getActivity(), getResString("lbl_network_alert"));
        }
    }


    private void count_today_pm(JSONArray jsonArray) {
        today_Pm_data = new ArrayList<>();
        String today_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object;
            try {
                object = jsonArray.getJSONObject(i);
                if (object.getString("pdm_due_date").equalsIgnoreCase(today_date) && !object.getString("pdm_inspection_date").equals(today_date)) {
                    today_Pm_data.add(new Gson().fromJson(object.toString(), Site_Model.class));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        tv4.setText("" + today_Pm_data.size());
        ViewGroup viewGroup = (ViewGroup) tv4.getParent();
        viewGroup.setVisibility(VISIBLE);
        viewGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (today_Pm_data.size() > 0) {
                    load_fragment("", "Today's PM", "periodic today", "" + today_Pm_data.size());
                } else {
                    show_snak(getActivity(), getResString("lbl_no_data_found"));
                }
            }
        });
    }

    Asset_Adapter asset_adapter;

    public void get_component(final String key) {
        if (isNetworkAvailable(getActivity())) {
            listView.setPadding(3, 5, 5, 155);
            listView.setClipToPadding(false);
            String url;
            if (searched_array.contains(key.toLowerCase())) {
                show_snak(getActivity(), getResString("lbl_alrdy_lst_msg"));
                return;
            }
            if (page_type.equals("myStore")) {
                url = All_Api.filtrd_storeData + key + "&filter=" + filter;
                DataHolder_Model.getInstance().setComponent_models(new ArrayList<>());
            } else
                url = All_Api.get_assetInputType + inputtype.toLowerCase() + "=" + key;

            url = url + "&client_id=" + client_id;

            url = url.replace(" ", "%20");
            rg1.clearCheck();
            rg2.clearCheck();
            inputtype = "";
            NetworkRequest networkRequest = new NetworkRequest(getActivity());
            networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status_code").equals("200")) {
                                DataHolder_Model.getInstance().getComponent_models().addAll(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Component_model[].class))));
                                if (filter != null && !filter.equals(""))
                                    asset_adapter = new Asset_Adapter(baseActivity, page_type, filter);
                                else
                                    asset_adapter = new Asset_Adapter(baseActivity, page_type, null);
                                if (asset_adapter.getItemCount() > 0 && !page_type.equals("myStore") && !page_type.equals("project_data")) {
                                    ad_stor_btn.setVisibility(VISIBLE);
                                    searched_array.add(key.toLowerCase());
                                }
                                listView.setVisibility(VISIBLE);
                                listView.setAdapter(asset_adapter);
                                searchView.clearFocus();
                            } else {
                                show_snak(getActivity(), jsonObject.getString("message"));
                            }
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
        } else {
            show_snak(getActivity(), getResString("lbl_network_alert"));
        }
    }


    public void add_to_Store(String project_id) {
        if (DataHolder_Model.getInstance().getComponent_models().size() > 0) {
            if (isNetworkAvailable(getActivity())) {
                String url;
                HashMap<String, String> params = new HashMap<>();
                params.put("user_id", user_id);
                params.put("client_id", client_id);
                params.put("group_id", Static_values.group_id);

                if (!project_id.equals("")) {
                    StringBuilder all_components_code = new StringBuilder();
                    StringBuilder all_mdata_id = new StringBuilder();
                    for (Component_model component_model : DataHolder_Model.getInstance().getComponent_models()) {
                        if (all_components_code.toString().equals("")) {
                            all_components_code = new StringBuilder(component_model.getComponent_code());
                            all_mdata_id = new StringBuilder(component_model.getMdata_id());
                        } else {
                            all_components_code.append(",").append(component_model.getComponent_code());
                            all_mdata_id.append(",").append(component_model.getMdata_id());
                        }
                    }
                    params.put("product_id", all_components_code.toString());
                    params.put("mdata_id", all_mdata_id.toString());
                    params.put("project_id", project_id);
                    url = All_Api.add_to_project;
                } else {
                    try {
                        JSONArray jsonArray = new JSONArray();
                        for (Component_model component_model : DataHolder_Model.getInstance().getComponent_models()) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("mdata_id", component_model.getMdata_id());
                            jsonObject.put("product_id", component_model.getComponent_code());
                            jsonArray.put(jsonObject);
                        }
                        params.put("products", jsonArray.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    url = All_Api.add_store_api;
                }

                Log.e("params", "" + params);
                NetworkRequest networkRequest = new NetworkRequest(getActivity());
                networkRequest.make_post_request(url, params, new NetworkRequest.VolleyResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        Object json;
                        try {
                            json = new JSONTokener(response).nextValue();
                            if (json instanceof JSONObject) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("status_code")) {
                                    if (jsonObject.getString("status_code").equals("200")) {
                                        DataHolder_Model.getInstance().getComponent_models().clear();
                                        if (asset_adapter != null) {
                                            asset_adapter.notifyDataSetChanged();
                                            ad_stor_btn.setVisibility(GONE);
                                            searched_array.clear();
                                            if (Static_values.group_id.equals("13"))
                                                get_sites_status(All_Api.get_store_count + user_id);
                                        }
                                        show_snak(getActivity(), jsonObject.getString("message"));
                                    } else {
                                        show_snak(getActivity(), jsonObject.getString("message"));
                                    }
                                }
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

            } else {
                show_snak(getActivity(), getResString("lbl_network_alert"));
            }
        } else {
            show_snak(getActivity(), getResString("lbl_add_ast_msg"));
        }
    }


    private void select_seqlite() {
        site_models = new ArrayList<>(Arrays.asList(new Gson().fromJson(AppController.getInstance().getDatabase().getSites_data_Dao().getSites_data(client_id).toString(), Site_Model[].class)));
        inspector_search_listadapter = new Inspector_search_listadapter(baseActivity, site_models, page_type);
        listView.setAdapter(inspector_search_listadapter);
    }


    public void check_scan_typ() {
        if (inputtype == null)
            show_snak(getActivity(), getResString("lbl_slct_inpt_msg"));
        else if (inputtype.equalsIgnoreCase("RFID")) {
            homeActivity.create_EPCInstance();
            if (baseActivity.mUhfrManager == null) {
                if (!baseActivity.scan_rfid.isNFCSupport) {
                    AppUtils.show_snak(getActivity(), "Your Device Does Not support NFC.");
                    return;
                } else if (!baseActivity.scan_rfid.checkNfcEnabled()) {
                    return;
                }
            }
            final Dialog alertDialog = show_Rfid_Dialog();
            scan_rfid(new NFC_Listner.Read_interface() {
                @Override
                public void read_complete(String read_text) {
                    alertDialog.dismiss();
                    printLog("read_text " + read_text);
                    if (read_text.contains("arresto.in/")) {
                        read_text = Uri.parse(read_text).getQueryParameter("u");
                        inputtype = "uin";
                    }
                    if (read_text != null && !read_text.equals("")) {
                        get_scaned_data(read_text, "");
                        AppUtils.show_snak(getActivity(), "Tag " + read_text + " scanned");
                    }
                }
            });
        } else if (inputtype.equalsIgnoreCase("Barcode")) {
            scan_barcode(new BarcodeListener() {
                @Override
                public void onScanned(String scaned_text) {
                    if (scaned_text.contains("https://arresto.in/")) {
                        Uri uri = Uri.parse(scaned_text);
                        master_id = uri.getQueryParameter("m");
                        scaned_text = uri.getQueryParameter("u");
                        inputtype = "uin";
                    }
                    get_scaned_data(scaned_text, "");
                }
            });
        } else if (inputtype.equalsIgnoreCase("UIN")) {
            showUIN_Dialog(baseActivity, new Handler() {
                @Override
                public void handleMessage(@NonNull Message msg) {
                    super.handleMessage(msg);
                    if (msg.obj != null && !(msg.obj.toString()).equals("")) {
                        get_scaned_data(msg.obj.toString(), "");
                    }
                }
            });
        } else if (inputtype.equalsIgnoreCase("ocr")) {
            scan_Ocr(new OCRListener() {
                @Override
                public void onScanned(String batch, String serial) {
                    get_scaned_data(serial, batch);
                }

                @Override
                public void onUIDScanned(String uid) {
                    inputtype.equalsIgnoreCase("UIN");
                    get_scaned_data(uid, "");
                }
            });
        } else {
            show_snak(getActivity(), getResString("lbl_slct_inpt_msg"));
        }
    }

    public void get_scaned_data(String scand_str, String optinal) {
        if (!page_type.equals("addStore") && !page_type.equals("add_to_project")) {
            serviceCall_2(scand_str, optinal);
        } else {
            if ((group_id.equals("15") && DataHolder_Model.getInstance().getComponent_models().size() < 1) || group_id.equals("13"))
                get_component(scand_str);
            else
                show_snak(getActivity(), getResString("lbl_add_msg"));
        }
    }


    public void show_showCase() {
        if (messages != null && messages.size() > 0)
            Add_Showcase.getInstance(getActivity()).setData(messages, views);
    }
}