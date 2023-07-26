package app.com.arresto.arresto_connect.ui.fragments;

import static app.com.arresto.arresto_connect.constants.AppUtils.calculate_inspection;
import static app.com.arresto.arresto_connect.constants.AppUtils.delete_uploadedsite_data;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.INSPECTED_SITES;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.SITES_TOUPLOAD;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.docsName;
import static app.com.arresto.arresto_connect.constants.Static_values.docsStatus;
import static app.com.arresto.arresto_connect.constants.Static_values.group_id;
import static app.com.arresto.arresto_connect.constants.Static_values.indicatorDrawable;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;
import static app.com.arresto.arresto_connect.constants.Static_values.slctd_product_image;
import static app.com.arresto.arresto_connect.constants.Static_values.slctd_product_name;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.fragments.Main_Fragment.is_refresh;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.webkit.MimeTypeMap;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.ImageViewCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CircleImageView;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.CustomAdapter;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.custom_views.ExpandableHeightGridView;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.data.models.Senitize_Model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.database.AppDatabase;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Detail_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.WorkPermitTable;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;
import app.com.arresto.arresto_connect.ui.adapters.Filter_GroupAdapter;
import app.com.arresto.arresto_connect.ui.adapters.Quality_image_adepter;
import app.com.arresto.arresto_connect.ui.modules.add_data.Add_masterData;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionFragment;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionListItems;
import app.com.arresto.arresto_connect.ui.modules.inspection.Work_permit_fragment;
import app.com.arresto.arresto_connect.ui.modules.periodic_maintainance.Periadic_steps;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.FallCountModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.ui.SensorInfoFragment;

public class Master_detail_fragment extends Base_Fragment implements View.OnClickListener,
        CustomAdapter.ListClick {
    View view;

    String page_type = "";

    ArrayList<String> image_url, product_name, section_type;
    public static String prdct_nam;
    Site_Model site_model;
    TextView prdct_name_tv, status_tv, manufacturing_dt_tv, fist_use_tv, due_date_tv,
            last_inspection_tv;

    TextView schdule_date, disc_txt, alertBar, inspector_tv0, inspector_tv1, inspector_tv2;
    CircleImageView profil_pic;

    TextView contn_btn, schdule_btn, register_btn;
    ImageView ast_img, edit_master_btn, breakdown_btn, indictr_img;
    RelativeLayout othr_lay, info_btn, prdct_lay, video_btn, sensor_btn, tecnicl_lay,
            tchncl_data_lay, crtfict_lay, prsntsn_lay, insHistry_lay, mainHistry_lay,
            inspctn_pram_lay;
    ImageView prdct_img, insHistry_img, mainHistry_img, inspctn_pram_img, tecnicl_img,
            tchncl_data_img, crtfict_img, prsntsn_img, othr_img, cl_ic;
    int current_pos;

    MasterData_model masterData_model;
    Bundle savedInstanceState;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        Log.d("TAG", "FragmentView: 1234567");
        if (view == null) {
            if (getArguments() != null) {
                page_type = getArguments().getString("page_type", "");
                image_url = getArguments().getStringArrayList("img_urls");
                product_name = getArguments().getStringArrayList("product_name");
                section_type = getArguments().getStringArrayList("section_type");
                Log.e("image_url ", " 6ty " + image_url);
            }
            view = inflater.inflate(R.layout.inspection_tree_ast, container, false);
            setdata_for_inspection();
            get_service_data(section_type.get(current_pos), product_name.get(current_pos));
            return view;
        } else {
            return view;
        }
    }

    String img_url;
    ImageView infected_img, status_ic, sensor_ic;
    TextView sensor_tv, s_user;

    GridLayout detail_grid;
    ExpandableHeightGridView new_btns;

    private void setdata_for_inspection() {
        detail_grid = view.findViewById(R.id.detail_grid);
        new_btns = view.findViewById(R.id.new_btns);
        new_btns.setExpanded(true);
        profil_pic = view.findViewById(R.id.profil_pic);
        disc_txt = view.findViewById(R.id.disc_txt);
        alertBar = view.findViewById(R.id.alertBar);
        inspector_tv0 = view.findViewById(R.id.inspector_tv0);
        inspector_tv1 = view.findViewById(R.id.inspector_tv1);
        inspector_tv2 = view.findViewById(R.id.inspector_tv2);
        schdule_date = view.findViewById(R.id.schdule_date);
        ast_img = view.findViewById(R.id.ast_img);
        indictr_img = view.findViewById(R.id.indictr_img);
        status_tv = view.findViewById(R.id.status_tv);
        prdct_name_tv = view.findViewById(R.id.prdct_name_tv);
        manufacturing_dt_tv = view.findViewById(R.id.manufacturing_dt_tv);
        fist_use_tv = view.findViewById(R.id.fist_use_tv);
        due_date_tv = view.findViewById(R.id.due_date_tv);
        last_inspection_tv = view.findViewById(R.id.last_inspection_tv);
        info_btn = view.findViewById(R.id.info_btn);
        video_btn = view.findViewById(R.id.video_btn);
        contn_btn = view.findViewById(R.id.contn_btn);
        schdule_btn = view.findViewById(R.id.schdule_btn);
        edit_master_btn = view.findViewById(R.id.edit_master_btn);
        breakdown_btn = view.findViewById(R.id.breakdown_btn);
        register_btn = view.findViewById(R.id.register_btn);
        insHistry_lay = view.findViewById(R.id.insHistry_lay);
        mainHistry_lay = view.findViewById(R.id.mainHistry_lay);
        inspctn_pram_lay = view.findViewById(R.id.inspctn_pram_lay);

        //sensor id
        sensor_btn = view.findViewById(R.id.sensor_btn);
        status_ic = view.findViewById(R.id.status_ic);
        sensor_ic = view.findViewById(R.id.sensor_ic);
        sensor_tv = view.findViewById(R.id.sensor_tv);
        s_user = view.findViewById(R.id.s_user);

        infected_img = view.findViewById(R.id.infected_img);
        prdct_nam = product_name.get(current_pos);
        prdct_name_tv.setText(prdct_nam);
        site_model = Static_values.selected_Site_model;
        masterData_model = Static_values.selectedMasterData_model;
        info_btn.setOnClickListener(this);

        ArrayList<String> names = new ArrayList<>();
        ArrayList<Integer> icons = new ArrayList<>();

        names.add(getString(R.string.lbl_ins_histry_st));
        icons.add(R.drawable.inspection_hist);
        names.add(getString(R.string.lbl_main_histry_st));
        icons.add(R.drawable.maintenance_hist);
        names.add(getString(R.string.lbl_prdct_img_st));
        icons.add(R.drawable.product_ic);
        names.add(getString(R.string.lbl_user_manul_st));
        icons.add(R.drawable.tcncl_spc_ic);
        names.add(getString(R.string.lbl_technical_data_sheet));
        icons.add(R.drawable.technicldata_ic);
        names.add(getString(R.string.lbl_certificate));
        icons.add(R.drawable.certifict_ic);
        names.add(getString(R.string.lbl_presentation));
        icons.add(R.drawable.prsentasn_ic);
        if (client_id.equals("931")) {
            detail_grid.removeView(sensor_btn);
            detail_grid.removeView(video_btn);
        } else {
            names.add(getString(R.string.lbl_video));
            icons.add(R.drawable.video_ic);
            if (masterData_model.getSensor_ids() != null)
                for (String sensor : masterData_model.getSensor_ids()) {
                    names.add(getString(R.string.lbl_sensor) + "ID\n" + sensor);
                    icons.add(R.drawable.sensor_ic);
                }
        }

        if (client_id.equalsIgnoreCase("376")) {
            detail_grid.removeView(info_btn);
        } else {
            names.add(getString(R.string.lbl_dealer_locator));
            icons.add(R.drawable.client_loc_ic);
        }

        CustomAdapter adapter = new CustomAdapter(getContext(), names, icons, this::performClick);
        new_btns.setAdapter(adapter);

        if (AppUtils.isTablet(getActivity())) {
            detail_grid.setColumnCount(4);
        }
        if (!site_model.getInspected_status().equals("Yes") && !group_id.equals("18"))
            edit_master_btn.setVisibility(View.VISIBLE);

        edit_master_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoadFragment.replace(Add_masterData.newInstance("edit_master", masterData_model),
                        baseActivity, getResString("lbl_edit_master_data"));
            }
        });

        breakdown_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakdownDialogView.show();
            }
        });

        if (!group_id.equals("9")) {
            ConstantMethods.find_pageVideo(getActivity(), "inspection register asset");
//            if (masterData_model.getRef_user_id() == null || !masterData_model.getRef_user_id()
//            .equals(Static_values.user_id))
            if ((masterData_model.getRef_user_id() == null || masterData_model.getRef_user_id().equals("") || masterData_model.getRef_user_id().equals("0")))
                register_btn.setVisibility(View.VISIBLE);
            else register_btn.setVisibility(View.GONE);
            schdule_btn.setVisibility(View.GONE);

            if (masterData_model.getRef_inspection() != null) {
                get_inspector_profile(masterData_model.getRef_inspection().getS_user_id());
            }
            if (page_type.equals("periodic today") || page_type.equals("client_periodic") || page_type.equals("maintenanceDue") || page_type.equals("maintenanceDue") || (Filter_GroupAdapter.page_type != null && Filter_GroupAdapter.page_type.equals("client_periodic"))) {
                register_btn.setVisibility(View.GONE);
                contn_btn.setVisibility(View.GONE);
                getGroupUsers();
                breakdown_btn.setVisibility(View.VISIBLE);
                if (masterData_model.getPdm_report_status() != null && masterData_model.getPdm_report_status().equalsIgnoreCase("Pending")) {
                    indictr_img.setImageResource(indicatorDrawable[3]);
                    status_tv.setText("Waiting for approval");
                    try {
                        manufacturing_dt_tv.setText(BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(masterData_model.getMdata_material_invoice_date())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        manufacturing_dt_tv.setText(masterData_model.getMdata_material_invoice_date());

                    }
                    fist_use_tv.setText(masterData_model.getDate_of_first_use());
                    due_date_tv.setText(Html.fromHtml("<u>NA</u>"));
                } else {
                    check_inpection_status("pdm", masterData_model.getPdm_due_date(), 5);
                    last_inspection_tv.setVisibility(View.VISIBLE);
                    last_inspection_tv.setText(getResString("lbl_last_pm_execution_date") + " : " + masterData_model.getPdm_inspection_date());
                }
            } else {
                if ((group_id.equals("8") || group_id.equals("20")) && client_id.equals("931")) {
                    checkInspectionAvailable();
                } else contn_btn.setVisibility(View.GONE);
                if (site_model.getInspected_status() != null && site_model.getApproved_status() != null && site_model.getInspected_status().equalsIgnoreCase("Yes") && site_model.getApproved_status().equalsIgnoreCase("Pending")) {
                    indictr_img.setImageResource(indicatorDrawable[3]);
                    status_tv.setText("Waiting for approval");
                    try {
                        manufacturing_dt_tv.setText(BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(masterData_model.getMdata_material_invoice_date())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        manufacturing_dt_tv.setText(masterData_model.getMdata_material_invoice_date());

                    }
                    fist_use_tv.setText(masterData_model.getDate_of_first_use());
                    due_date_tv.setText(Html.fromHtml("<u>NA</u>"));
                } else {
                    int period = 15;
                    if (masterData_model.getInspection_due_period() != null && !masterData_model.getInspection_due_period().equals(""))
                        period = Integer.parseInt(masterData_model.getInspection_due_period());
                    check_inpection_status("", masterData_model.getInspection_due_date(), period);
                }
            }
        } else {
            if (page_type.equals("periodic")) {
                getGroupUsers();
                breakdown_btn.setVisibility(View.VISIBLE);
                schdule_btn.setVisibility(View.GONE);
                contn_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (masterData_model.getProduct_repair().equals("yes")) {
                            slctd_product_name = product_name.get(current_pos);
                            slctd_product_image = img_url;
                            LoadFragment.replace(new Periadic_steps(), getActivity(),
                                    getResString("lbl_preiodic_txt"));
                        } else show_snak(getActivity(), getResString("lbl_asset_na"));
                    }
                });

                contn_btn.setText(getResString("lbl_strt_periodic_st"));
                last_inspection_tv.setVisibility(View.VISIBLE);
                last_inspection_tv.setText(getResString("lbl_last_pm_execution_date") + " : " + masterData_model.getPdm_inspection_date());
                contn_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (masterData_model.getProduct_repair().equals("yes")) {
                            slctd_product_name = product_name.get(current_pos);
                            slctd_product_image = img_url;
                            LoadFragment.replace(new Periadic_steps(), getActivity(),
                                    getResString("lbl_preiodic_txt"));
                        } else show_snak(getActivity(), getResString("lbl_asset_na"));
                    }
                });

                if (masterData_model.getPdm_report_status() != null && masterData_model.getPdm_report_status().equalsIgnoreCase("Pending")) {
//                    contn_btn.setVisibility(View.GONE);
                    indictr_img.setImageResource(indicatorDrawable[3]);
                    status_tv.setText("Waiting for approval");
                    try {
                        manufacturing_dt_tv.setText(BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(masterData_model.getMdata_material_invoice_date())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        manufacturing_dt_tv.setText(masterData_model.getMdata_material_invoice_date());

                    }
                    fist_use_tv.setText(masterData_model.getDate_of_first_use());
                    due_date_tv.setText(Html.fromHtml("<u>NA</u>"));
                } else {
                    check_inpection_status("pdm", masterData_model.getPdm_due_date(), 5);
                }
            } else {
                ConstantMethods.find_pageVideo(getActivity(), "Inspection Scheduler");
                schdule_btn.setVisibility(View.GONE);
                register_btn.setVisibility(View.GONE);
                List<String> inspected_sites = mPrefrence.getArray_Data(INSPECTED_SITES);
                if (site_model.getInspected_status() != null) {
                    if (site_model.getInspected_status().equals("Yes") && inspected_sites.contains(unique_id)) {
                        inspected_sites.remove(unique_id);
                        mPrefrence.saveArray_Data(SITES_TOUPLOAD, inspected_sites);
                    }
                }

                if (page_type.equals("inspection") && masterData_model.getRef_inspection().getS_user_id() == null)
                    schdule_btn.setVisibility(View.VISIBLE);
                else schdule_btn.setVisibility(View.GONE);
//                }
                String status = site_model.getApproved_status();
                if (status != null && (status.equalsIgnoreCase("approved") || status.equalsIgnoreCase("Rejected"))) {
                    List<String> inspected_pdm = mPrefrence.getArray_Data(INSPECTED_SITES);
                    inspected_pdm.remove(unique_id);
                    mPrefrence.saveArray_Data(INSPECTED_SITES, inspected_pdm);
                }
                checkInspectionAvailable();
                if (site_model.getInspected_status() != null && site_model.getApproved_status() != null && site_model.getInspected_status().equalsIgnoreCase("Yes") && site_model.getApproved_status().equalsIgnoreCase("Pending")) {
//                    contn_btn.setVisibility(View.GONE);
                    indictr_img.setImageResource(indicatorDrawable[3]);
                    status_tv.setText("Waiting for approval");
                    try {
                        manufacturing_dt_tv.setText(BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(masterData_model.getMdata_material_invoice_date())));
                    } catch (ParseException e) {
                        e.printStackTrace();
                        manufacturing_dt_tv.setText(masterData_model.getMdata_material_invoice_date());

                    }
                    fist_use_tv.setText(masterData_model.getDate_of_first_use());
                    due_date_tv.setText(Html.fromHtml("<u>NA</u>"));

                } else {
                    int period = 15;
                    if (masterData_model.getInspection_due_period() != null && !masterData_model.getInspection_due_period().equals(""))
                        period = Integer.parseInt(masterData_model.getInspection_due_period());
                    check_inpection_status("", masterData_model.getInspection_due_date(), period);
                }
            }
        }

        if (page_type.equals("scheduler")) schdule_btn.setVisibility(View.GONE);
        else if (page_type.equals("myassets")) register_btn.setVisibility(View.GONE);
        else if (page_type.equals("ASM_project")) {
            register_btn.setVisibility(View.GONE);
            checkInspectionAvailable();
        }

        findAllIds(view);
        img_url = image_url.get(0);
        profil_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (profile_model.getUpro_image() != null && !profile_model.getUpro_image().equals(""))
                    FullScreenDialog.newInstance((AppCompatActivity) getActivity(),
                            profile_model.getUpro_image());
            }
        });

        schdule_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                show_Date_piker(site_model.getSiteID_id(), masterData_model.getMdata_id());
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onClick(View v) {
                NetworkRequest networkRequest = new NetworkRequest(getActivity());
                HashMap<String, String> params = new HashMap<>();
                params.put("site_id", site_model.getSiteID_id());
                params.put("master_data_id", masterData_model.getMdata_id());
                params.put("user_id", Static_values.user_id);
                params.put("group_id", Static_values.group_id);
                params.put("client_id", client_id);
                params.put("cgrp_id", role_id);
                networkRequest.add_to_schedule(All_Api.register_site, params, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("return obj", "" + msg.obj);
                        if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                            register_btn.setVisibility(View.GONE);
                            is_refresh = true;
                        }
                        //       process incoming messages here
                        //       this will run in the thread, which instantiates it
                    }
                });
            }
        });
        inspctn_pram_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("product_name", product_name.get(current_pos));
                bundle.putString("product_type", section_type.get(current_pos));
                bundle.putString("master_id", masterData_model.getMdata_id());
                bundle.putString("page_type", page_type);
                bundle.putString("is_confirm", masterData_model.getIs_confirmed());
                bundle.putString("preuse_time", masterData_model.getPreuse_time());
                Inspection_parameter_fragment inspection_parameter_fragment =
                        new Inspection_parameter_fragment();
                inspection_parameter_fragment.setArguments(bundle);
                LoadFragment.replace(inspection_parameter_fragment, getActivity(), getResString(
                        "lbl_ins_params_st"));
            }
        });

        insHistry_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bndl = new Bundle();
                bndl.putString("id", "history_report");
                bndl.putInt("index", 2);
                bndl.putString("history_url",
                        All_Api.productInspection_history + product_name.get(current_pos) +
                                "&master_id=" + masterData_model.getMdata_id() + "&client_id=" + client_id);
                Close_projectFragment close_projectFragment1 = new Close_projectFragment();
                close_projectFragment1.setArguments(bndl);
                LoadFragment.replace(close_projectFragment1, getActivity(), getResString(
                        "lbl_ins_histry_st"));
            }
        });

        mainHistry_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


//        if (client_id.equals("376") ||  client_id.equals("419")) {
        infected_img.setVisibility(View.VISIBLE);
        fetch_infectionData();
        infected_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user_info_dialog(senitizeModel);
            }
        });
//        }

    }

    @Override
    public void performClick(String position) {
        Bundle bndl = new Bundle();
        if (position.equals(getString(R.string.lbl_ins_histry_st))) {
            bndl.putString("id", "history_report");
            bndl.putInt("index", 2);
            bndl.putString("history_url",
                    All_Api.productInspection_history + product_name.get(current_pos) +
                            "&master_id=" + masterData_model.getMdata_id() + "&client_id=" + client_id);
            Close_projectFragment close_projectFragment1 = new Close_projectFragment();
            close_projectFragment1.setArguments(bndl);
            LoadFragment.replace(close_projectFragment1, getActivity(), getResString(
                    "lbl_ins_histry_st"));
        } else if (position.equals(getString(R.string.lbl_main_histry_st))) {
            bndl.putString("id", "pdm_report");
            bndl.putInt("index", 1);
            bndl.putString("pdm_url",
                    All_Api.post_pdm_ins_list + client_id + "&master_id=" + masterData_model.getMdata_id());
            Close_projectFragment close_projectFragment = new Close_projectFragment();
            close_projectFragment.setArguments(bndl);
            LoadFragment.replace(close_projectFragment, getActivity(), getResString(
                    "lbl_maintenance_report"));
        } else if (position.equals(getString(R.string.lbl_prdct_img_st))) {
            LoadFragment.replace(Gallery_Fragment.newInstance(gallary_url), getActivity(),
                    getResString("lbl_prdct_img_st"));
        } else if (position.equals(getString(R.string.lbl_user_manul_st))) {
            load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(1))), 0,
                    getResString("lbl_user_manul_st"));
        } else if (position.equals(getString(R.string.lbl_technical_data_sheet))) {
            load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(2))), 0,
                    getResString("lbl_technical_data_sheet"));
        } else if (position.equals(getString(R.string.lbl_certificate))) {
            Report_webview report_webview = new Report_webview();
            Bundle bundle = new Bundle();
            bundle.putString("url", All_Api.certificateView + product_name.get(current_pos) +
                    "&type=" + section_type.get(current_pos) + "&serial_id=" + masterData_model.getMdata_serial() + "&batch_no=" + masterData_model.getMdata_batch() + "&manufacture_date=" + masterData_model.getMdata_material_invoice_date() + "&client_id=" + client_id);
            bundle.putString("type", "certificate");
            report_webview.setArguments(bundle);
            LoadFragment.replace(report_webview, getActivity(), getResString("lbl_certificate"));
        } else if (position.equals(getString(R.string.lbl_presentation))) {
            load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(4))), 0,
                    getResString("lbl_presentation"));
        } else if (position.equals(getString(R.string.lbl_video))) {
            Report_webview report_webview00 = new Report_webview();
            Bundle bundle00 = new Bundle();
            bundle00.putString("url", youtube_url.replace(" ", "%20"));
            report_webview00.setArguments(bundle00);
            LoadFragment.replace(report_webview00, getActivity(), "Video");

        } else if (position.equals(getString(R.string.lbl_sensor))) {
            if (masterData_model.getSensor_id() != null && !masterData_model.getSensor_id().equals("")) {
                SensorInfoFragment viewFragment = new SensorInfoFragment();
                Bundle bundle01 = new Bundle();
                bundle01.putString("sensor_id", masterData_model.getSensor_id());
                viewFragment.setArguments(bundle01);
                LoadFragment.replace(viewFragment, getActivity(), "Sensor Info");
            }
        } else if (position.equals(getString(R.string.lbl_dealer_locator))) {
            baseActivity.fetch_dealer_data();
        } else if (position.equals(getString(R.string.lbl_dec_of_conformity))) {
            load_full_screen_view(new ArrayList<>(Collections.singletonList(dec_of_conformity)),
                    0, getResString("lbl_dec_of_conformity"));
        }
    }


    ArrayList<String> title, file_url, gallary_url, file_name;
    String dec_of_conformity, youtube_url;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_btn:
                baseActivity.fetch_dealer_data();
                break;
            case R.id.prdct_lay:
                LoadFragment.replace(Gallery_Fragment.newInstance(gallary_url), getActivity(),
                        getResString("lbl_prdct_img_st"));
                break;
            case R.id.tecnicl_lay:
                load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(1)))
                        , 0, getResString("lbl_user_manul_st"));
                break;
            case R.id.tchncl_data_lay:
                load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(2)))
                        , 0, getResString("lbl_technical_data_sheet"));
                break;
            case R.id.crtfict_lay:
                Report_webview report_webview = new Report_webview();
                Bundle bundle = new Bundle();
                bundle.putString("url",
                        All_Api.certificateView + product_name.get(current_pos) + "&type=" + section_type.get(current_pos) + "&serial_id=" + masterData_model.getMdata_serial() + "&batch_no=" + masterData_model.getMdata_batch() + "&manufacture_date=" + masterData_model.getMdata_material_invoice_date() + "&client_id=" + client_id);
                bundle.putString("type", "certificate");
                report_webview.setArguments(bundle);
                LoadFragment.replace(report_webview, getActivity(), getResString("lbl_certificate"
                ));
                break;
            case R.id.video_btn:
                Report_webview report_webview00 = new Report_webview();
                Bundle bundle00 = new Bundle();
                bundle00.putString("url", youtube_url.replace(" ", "%20"));
                report_webview00.setArguments(bundle00);
                LoadFragment.replace(report_webview00, getActivity(), "Video");
                break;

            case R.id.prsntsn_lay:
                load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(4)))
                        , 0, getResString("lbl_presentation"));
                break;
            case R.id.othr_lay:
                load_full_screen_view(new ArrayList<>(Collections.singletonList(dec_of_conformity)), 0, getResString("lbl_dec_of_conformity"));
                break;

            case R.id.sensor_btn:
                if (masterData_model.getSensor_id() != null && !masterData_model.getSensor_id().equals("")) {
                    SensorInfoFragment viewFragment = new SensorInfoFragment();
                    Bundle bundle01 = new Bundle();
                    bundle01.putString("sensor_id", masterData_model.getSensor_id());
                    viewFragment.setArguments(bundle01);
                    LoadFragment.replace(viewFragment, getActivity(), "Sensor Info");
                }
                break;
        }
    }

    public void checkInspectionAvailable() {
        if (masterData_model.getComponent_inspection() != null && masterData_model.getComponent_inspection().equalsIgnoreCase("no")) {
            show_snak(getActivity(), getResString("lbl_asset_na_inspection"));
            contn_btn.setVisibility(View.GONE);
            alertBar.setVisibility(View.VISIBLE);
        } else {
            contn_btn.setVisibility(View.VISIBLE);
            alertBar.setVisibility(View.GONE);
            contn_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (docsStatus.equals("overdue")) {
                        baseActivity.show_OkAlert("Document Expiring...",
                                "Your document, " + docsName + " is Expiring. \nPlease contact " + "the administrator and get your updated documents " + "uploaded", null, "Close", null);
                        return;
                    }
                    if (mPrefrence.getArray_Data(INSPECTED_SITES).contains(unique_id))
                        show_snak(getActivity(), "You have already inspected this Product");
                    else check_workdone();
                }
            });

        }
    }


//    public  boolean isDataInDB(){
//        permitData = Db.getWorkPermit_Dao().getWorkPermit_data(user_id, client_id, unique_id);
//        detail_table = Db.getInspection_Detail_Dao().getInspection_Detail(user_id, unique_id);
//        allInspected_asset = Db.getInspection_Asset_dao().getAllInspected_Asset(user_id,
//        unique_id);
//        signatureTable = Db.getInspectionSignature_Dao().getInspectionSignature(user_id,
//        unique_id, 1);
//        client_signatureTable = Db.getInspectionSignature_Dao().getInspectionSignature(user_id,
//        unique_id, 0);
//    }

    public void load_full_screen_view(ArrayList<String> imgurls, int position, String tag) {
        Fullscreenview fullscreenview = new Fullscreenview();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imgurl", imgurls);
        bundle.putInt("pos", position);
        fullscreenview.setArguments(bundle);
        LoadFragment.replace(fullscreenview, getActivity(), tag);
    }

    private void show_Date_piker(final String site_id, final String master_id) {
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener pDateSetListener =
                new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("HandlerLeak")
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                NetworkRequest networkRequest = new NetworkRequest(getActivity());
                HashMap<String, String> params = new HashMap<>();
                params.put("site_id", site_id);
                params.put("user_id", Static_values.user_id);
                params.put("group_id", Static_values.group_id);
                params.put("scheduler_date",
                        new SimpleDateFormat("yyyy-MM-dd").format(c.getTime()));
                params.put("master_data_id", master_id);
                params.put("client_id", client_id);
                params.put("cgrp_id", role_id);
                networkRequest.add_to_schedule(All_Api.insert_schedule, params, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        Log.e("return obj", "" + msg.obj);
                        if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                            schdule_btn.setVisibility(View.GONE);
                            is_refresh = true;
                        }
                    }
                });
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), pDateSetListener, mYear,
                mMonth, mDay);
        dialog.getDatePicker().setMinDate(c.getTimeInMillis());
        dialog.show();
    }


    private void check_inpection_status(String type, String due_date, int days_forNotify) {
        MasterData_model masterData_model = Static_values.selectedMasterData_model;
        try {
            manufacturing_dt_tv.setText(BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(masterData_model.getMdata_material_invoice_date())));
        } catch (ParseException e) {
            e.printStackTrace();
            manufacturing_dt_tv.setText(masterData_model.getMdata_material_invoice_date());
        }
        fist_use_tv.setText(masterData_model.getDate_of_first_use());
        due_date_tv.setText(due_date);

        if (type.equals("pdm") && masterData_model.getIs_breakdown().equals("1")) {
            indictr_img.setImageResource(indicatorDrawable[4]);
            status_tv.setText("Breakdown");
            alertBar.setText("Product is under breakdown \nRemark: " + masterData_model.getMdata_breakdown_remark());
            alertBar.setVisibility(View.VISIBLE);
            contn_btn.setVisibility(View.GONE);
        } else {
            int inp_stats;
            if (masterData_model.getIs_confirmed().equals("0")) {
                inp_stats = 0;
            } else {
                inp_stats = calculate_inspection(due_date, days_forNotify);
            }
            indictr_img.setImageResource(indicatorDrawable[inp_stats]);
            if (inp_stats == 0) {
                if (type.equals("pdm")) status_tv.setText(getResString("lbl_main_ovdue_st"));
                else status_tv.setText(getResString("lbl_ins_ovrdue_st"));
            } else if (inp_stats == 1) {
                if (type.equals("pdm")) status_tv.setText(getResString("lbl_main_due_st"));
                else status_tv.setText(getResString("lbl_inspctndue_txt"));
            } else {
                if (type.equals("pdm")) status_tv.setText("Maintained");
                else status_tv.setText(getResString("lbl_inspctd_st"));
                schdule_btn.setVisibility(View.GONE);
//            contn_btn.setVisibility(View.GONE);
            }
        }
    }


    private void get_service_data(String product_type, String product_name) {
        title = new ArrayList<>();
        file_url = new ArrayList<>();
        gallary_url = new ArrayList<>();
        file_name = new ArrayList<>();

        String url =
                All_Api.assetValues + product_type + "&client_id=" + client_id + "&typeCode" +
                        "=" + product_name;
        url = url.replaceAll(" ", "%20");
        url = url.replaceAll("\\+", "%2B");
        Log.e("email id url", "" + url);

        new NetworkRequest(getActivity()).make_get_request(url,
                new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("error", "" + response);
                try {
                    JSONObject dataObject = new JSONObject(response);
                    if (img_url == null || img_url.equals("")) {
                        img_url = dataObject.getString("imagePath");
                    }
                    if (dataObject.has("description"))
                        disc_txt.setText("" + dataObject.getString("description"));
                    JSONArray jsonArray = dataObject.getJSONArray("file");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        title.add(jsonObject.getString("title"));
                        file_url.add(jsonObject.getString("url"));
                        file_name.add(jsonObject.getString("file_name"));
                    }

                    JSONArray gallary_arr = dataObject.getJSONArray("gallery_images");
                    for (int i = 0; i < gallary_arr.length(); i++) {
                        JSONObject jsonObject = gallary_arr.getJSONObject(i);
                        gallary_url.add(jsonObject.getString("url"));
                    }
                    youtube_url = dataObject.getString("youtube_url");
                    dec_of_conformity = dataObject.getString("dec_of_conformity");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "is " + e.getMessage());
                }
                check_data_for_menu();
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    public void findAllIds(View view) {
        prdct_img = view.findViewById(R.id.prdct_img);
        tecnicl_img = view.findViewById(R.id.tecnicl_img);
        tchncl_data_img = view.findViewById(R.id.tchncl_data_img);
        insHistry_img = view.findViewById(R.id.insHistry_img);
        mainHistry_img = view.findViewById(R.id.mainHistry_img);
        inspctn_pram_img = view.findViewById(R.id.inspctn_pram_img);
        crtfict_img = view.findViewById(R.id.crtfict_img);
        prsntsn_img = view.findViewById(R.id.prsntsn_img);
        othr_img = view.findViewById(R.id.othr_img);
        cl_ic = view.findViewById(R.id.cl_ic);

        prdct_lay = view.findViewById(R.id.prdct_lay);
        tecnicl_lay = view.findViewById(R.id.tecnicl_lay);
        tchncl_data_lay = view.findViewById(R.id.tchncl_data_lay);
        crtfict_lay = view.findViewById(R.id.crtfict_lay);
        prsntsn_lay = view.findViewById(R.id.prsntsn_lay);
        othr_lay = view.findViewById(R.id.othr_lay);

//        prdct_tv = view.findViewById(R.id.prdct_tv);
//        tecnicl_tv = view.findViewById(R.id.tecnicl_tv);
//        tchncl_data_tv = view.findViewById(R.id.tchncl_data_tv);
//        crtfict_tv = view.findViewById(R.id.crtfict_tv);
//        prsntsn_tv = view.findViewById(R.id.prsntsn_tv);
//        othr_tv = view.findViewById(R.id.othr_tv);

    }

    private void check_data_for_menu() {
        AppUtils.load_image(img_url, ast_img);

        set_botmmenu_gray_img();
        set_listnr();
        int color = Dynamic_Var.getInstance().getBtn_bg_clr();
        ImageViewCompat.setImageTintList(insHistry_img, ColorStateList.valueOf(color));
        ImageViewCompat.setImageTintList(mainHistry_img, ColorStateList.valueOf(color));
        ImageViewCompat.setImageTintList(inspctn_pram_img, ColorStateList.valueOf(color));
        ImageViewCompat.setImageTintList(crtfict_img, ColorStateList.valueOf(color));
        if (!client_id.equalsIgnoreCase("376")) {
            ImageViewCompat.setImageTintList(cl_ic, ColorStateList.valueOf(color));
        }
        crtfict_lay.setClickable(true);

        if (file_url.size() > 0) {

            if (!file_url.get(0).equals("")) {
                ImageViewCompat.setImageTintList(prdct_img, ColorStateList.valueOf(color));
//                prdct_tv.setTextColor(color);
                prdct_lay.setClickable(true);
                gallary_url.add(file_url.get(0));
            }
            if (!file_url.get(1).equals("")) {
                ImageViewCompat.setImageTintList(tecnicl_img, ColorStateList.valueOf(color));
                tecnicl_lay.setClickable(true);
            }
            if (!file_url.get(2).equals("")) {
                ImageViewCompat.setImageTintList(tchncl_data_img, ColorStateList.valueOf(color));
                tchncl_data_lay.setClickable(true);
            }
            if (!file_url.get(4).equals("")) {
                ImageViewCompat.setImageTintList(prsntsn_img, ColorStateList.valueOf(color));
                prsntsn_lay.setClickable(true);
            }
            if (dec_of_conformity != null && !dec_of_conformity.equals("")) {
                ImageViewCompat.setImageTintList(othr_img, ColorStateList.valueOf(color));
                othr_lay.setClickable(true);
            }
            if (youtube_url != null && !youtube_url.equals("")) {
                ImageViewCompat.setImageTintList(othr_img, ColorStateList.valueOf(color));
                video_btn.setClickable(true);
            }

        }
    }

    public void set_listnr() {
        prdct_lay.setOnClickListener(this);
        tecnicl_lay.setOnClickListener(this);
        tchncl_data_lay.setOnClickListener(this);
        crtfict_lay.setOnClickListener(this);
        prsntsn_lay.setOnClickListener(this);
        othr_lay.setOnClickListener(this);
        video_btn.setOnClickListener(this);
        sensor_btn.setOnClickListener(this);

        prdct_lay.setClickable(false);
        tecnicl_lay.setClickable(false);
        tchncl_data_lay.setClickable(false);
//            crtfict_lay.setClickable(false);
        prsntsn_lay.setClickable(false);
        othr_lay.setClickable(false);
        video_btn.setClickable(false);

    }

    public void set_botmmenu_gray_img() {
        prdct_img.setImageResource(R.drawable.product_ic);
        tecnicl_img.setImageResource(R.drawable.tcncl_spc_ic);
        tchncl_data_img.setImageResource(R.drawable.technicldata_ic);
        insHistry_img.setImageResource(R.drawable.inspection_hist);
        mainHistry_img.setImageResource(R.drawable.maintenance_hist);
        inspctn_pram_img.setImageResource(R.drawable.guide_ic);
        crtfict_img.setImageResource(R.drawable.certifict_ic);
        prsntsn_img.setImageResource(R.drawable.prsentasn_ic);
        othr_img.setImageResource(R.drawable.other_ic);
    }

    public void check_workdone() {
        if (masterData_model.getComponent_inspection() != null && masterData_model.getComponent_inspection().equalsIgnoreCase("no")) {
            show_snak(getActivity(), getResString("lbl_asset_na_inspection"));
            return;
        }
        AppDatabase database = AppController.getInstance().getDatabase();
        WorkPermitTable permit_data = database.getWorkPermit_Dao().getWorkPermit_data(user_id,
                client_id, unique_id);
        String today = baseActivity.Date_Format().format(new Date());
        if (masterData_model.getProduct_work_permit().equals("yes") && permit_data != null && !permit_data.getInspectionDate().equals(today)) {
            delete_uploadedsite_data(unique_id, masterData_model.getMdata_id());
            LoadFragment.replace(new Work_permit_fragment(), getActivity(), getResString(
                    "lbl_work_permit"));
        } else {
            Inspection_Detail_Table inspectionDetail =
                    AppController.getInstance().getDatabase().getInspection_Detail_Dao().getInspection_Detail(user_id, unique_id);
            if (inspectionDetail != null) {
                load_abimg_frag();
            } else if (permit_data != null || masterData_model.getProduct_work_permit().equals(
                    "no")) {
                load_inspfrag();
            } else {
                LoadFragment.replace(new Work_permit_fragment(), getActivity(), getResString(
                        "lbl_work_permit"));
            }
        }
    }

    // For continue Inspection
    public void load_inspfrag() {
        InspectionFragment inspectionFragment = new InspectionFragment();
        Bundle bundle = new Bundle();
        if (site_model != null && site_model.getClient_name() != null) {
            bundle.putString("client_name", site_model.getClient_name());
            bundle.putString("job_no", site_model.getSite_jobcard());
            bundle.putString("site",
                    site_model.getSite_address() + " " + site_model.getSite_city() + ", " + site_model.getSite_location());
            bundle.putString("site_id", site_model.getSiteID_id());
            bundle.putString("sms", site_model.getSite_sms());
            bundle.putString("sub_site_id", site_model.getSite_id());
            bundle.putString("asset", site_model.getMdata_item_series());
            bundle.putString("report_no", site_model.getReport_no());
            bundle.putString("latitude", site_model.getSite_lattitude());
            bundle.putString("longitude", site_model.getSite_longitude());
            bundle.putString("approved_status", site_model.getApproved_status());
            bundle.putString("inspected_status", site_model.getInspected_status());
            bundle.putString("workPermit_number", site_model.getWorkPermit_number());
        } else {
            bundle.putString("client_name", masterData_model.getMdata_client());
            bundle.putString("job_no", masterData_model.getMdata_jobcard());
            bundle.putString("asset", masterData_model.getMdata_item_series());
            bundle.putString("sms", masterData_model.getMdata_sms());
            bundle.putString("site", "");
            bundle.putString("site_id", "");
            bundle.putString("sub_site_id", "");
            bundle.putString("report_no", "");
            bundle.putString("latitude", "");
            bundle.putString("longitude", "");
            bundle.putString("approved_status", "");
            bundle.putString("inspected_status", "");
            bundle.putString("workPermit_number", "");
        }
        bundle.putString("rfid", masterData_model.getMdata_rfid());
        bundle.putString("uin", masterData_model.getMdata_uin());
        bundle.putString("barcode", masterData_model.getMdata_barcode());
        bundle.putString("asset_code", masterData_model.getAssetCodes());
        bundle.putString("mdata_id", masterData_model.getMdata_id());
        bundle.putString("po_no", masterData_model.getMdata_po());
        bundle.putString("batch_no", masterData_model.getMdata_batch());
        bundle.putString("serial_no", masterData_model.getMdata_serial());
        bundle.putString("image_url", img_url);
        inspectionFragment.setArguments(bundle);
        LoadFragment.replace(inspectionFragment, getActivity(), getResString("lbl_inspection"));
    }

    public void load_abimg_frag() {
        InspectionListItems inspectionListItems = new InspectionListItems();
        Bundle bundle = new Bundle();
        bundle.putStringArray("asset_code", masterData_model.getAssetCodes().split("####"));
        inspectionListItems.setArguments(bundle);
        LoadFragment.replace(inspectionListItems, getActivity(), getResString("lbl_inspection"));
    }

    Profile_Model profile_model;

    private void get_inspector_profile(String inspector_id) {
        String url = All_Api.getProfile + inspector_id + "&client_id=" + client_id;
        NetworkRequest networkRequest = new NetworkRequest(getActivity());
        networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response profile ", " is " + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject1 = new JSONObject(response);
                        profile_model = AppUtils.getGson().fromJson(jsonObject1.getString(
                                "profile"), Profile_Model.class);
                        ((ViewGroup) profil_pic.getParent()).setVisibility(View.VISIBLE);
                        inspector_tv0.setText(Html.fromHtml("<font><b>" + profile_model.getUpro_first_name() + " " + profile_model.getUpro_last_name() + "</b></font>"));
                        inspector_tv1.setText(jsonObject1.getString("email"));
                        inspector_tv2.setText("+91-" + profile_model.getUpro_phone());
//                        Linkify.addLinks(inspector_tv2, Linkify.PHONE_NUMBERS);
//                        inspector_tv2.setLinksClickable(true);
                        schdule_date.setText(Html.fromHtml("Scheduled Date: " + "<font><b>" + masterData_model.getRef_inspection().getS_scheduler_date() + "</b></font>"));
                        AppUtils.load_image(profile_model.getUpro_image(), profil_pic);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError logout ", " make_get_request " + message);
            }
        });
    }


    //    client_id/ user_id/ images /uin
    Senitize_Model senitizeModel;

    private void fetch_infectionData() {
        new NetworkRequest(getActivity()).make_get_request(All_Api.getInfection + client_id +
                "&uin=" + masterData_model.getMdata_uin(),
                new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        senitizeModel = new Gson().fromJson(object.getString("data"),
                                Senitize_Model.class);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }
        });
    }

    ArrayList<String> file_paths;

    public void user_info_dialog(Senitize_Model senitizeModel) {
        Quality_image_adepter adepter;
        final Dialog builder = new Dialog(baseActivity);
        builder.setCancelable(true);
        builder.setContentView(R.layout.senitize_dialog);
        RecyclerView grid_image = builder.findViewById(R.id.grid_image);
        TextView header = builder.findViewById(R.id.header);
        TextView email_tv = builder.findViewById(R.id.email_tv);
        TextView date_tv = builder.findViewById(R.id.date_tv);
        TextView cancel_btn = builder.findViewById(R.id.cancel_btn);
        TextView submit_btn = builder.findViewById(R.id.submit_btn);
        FloatingActionButton capture_img = builder.findViewById(R.id.capture_img);
        ImageView infection_img = builder.findViewById(R.id.infection_img);
        EditText remark_edt = builder.findViewById(R.id.remark_edt);
        if (senitizeModel != null) {
            if (senitizeModel.getImages() != null && senitizeModel.getImages().size() > 0) {
                load_image(senitizeModel.getImages().get(0), infection_img);
            }
            email_tv.setText("Email : " + senitizeModel.getEmail());
            try {
                date_tv.setText("Date : " + baseActivity.DateTime_Format().format(baseActivity.server_date_time.parse(senitizeModel.getCreated_date())));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            infection_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (senitizeModel.getImages() != null && senitizeModel.getImages().size() > 0)
                        LoadFragment.replace(Gallery_Fragment.newInstance(senitizeModel.getImages()), getActivity(), getResString("lbl_disinfection_images"));
                }
            });
        } else {
            header.setVisibility(View.GONE);
            ((RelativeLayout) infection_img.getParent()).setVisibility(View.GONE);
        }
        grid_image.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        grid_image.setPadding(3, 6, 3, 6);
        file_paths = new ArrayList<>();
        adepter = new Quality_image_adepter(baseActivity, file_paths);
        grid_image.setAdapter(adepter);
        capture_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(new OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        file_paths.add(path);
                        adepter.add_file(file_paths);
                        adepter.notifyDataSetChanged();
                    }
                });
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (file_paths != null && file_paths.size() > 0) {
                    post_data(remark_edt.getText().toString());
                    builder.dismiss();
                } else
                    Toast.makeText(baseActivity, getResString("lbl_please_add_a_picture"),
                            Toast.LENGTH_LONG).show();
            }
        });
        builder.show();
        builder.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void post_data(String remark) {
        final JSONObject params2 = new JSONObject();
        try {
            params2.put("user_id", Static_values.user_id);
            params2.put("client_id", Static_values.client_id);
            params2.put("uin", masterData_model.getMdata_uin());
            params2.put("remark", remark);
            params2.put("master_id", masterData_model.getMdata_id());
            params2.put("timestamp", "" + System.currentTimeMillis());
            JSONArray image_json = new JSONArray();
            for (int i = 0; i < file_paths.size(); i++) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(file_paths.get(i));
                String filetype = null;
                if (extension != null) {
                    filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                image_json.put("data:" + filetype + ";base64," + AppUtils.Image_toBase64(file_paths.get(i)));

//                Uri image_uri = (Uri) file_paths.get(i);
//                final MimeTypeMap mime = MimeTypeMap.getSingleton();
//                String extension = mime.getExtensionFromMimeType(baseActivity
//                .getContentResolver().getType(image_uri));
//                String filetype = null;
//                if (extension != null) {
//                    filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//                }
//                String image_string = uriToBase64(image_uri);
//                if (!image_string.equals(""))
//                    image_json.put("data:" + filetype + ";base64," + uriToBase64(image_uri));
            }
            params2.put("images", image_json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("data is", "" + params2.toString());
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(All_Api.postInfection, params2,
                new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject1 = new JSONObject(response);
                        show_snak(getActivity(), jsonObject1.getString("message"));
                        if (jsonObject1.getString("status_code").equals("200")) {
                            fetch_infectionData();
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
    }

    //    sensor code here
    ObjectListener objectListener = new ObjectListener() {
        @Override
        public void onResponse(Object obj) {
            if (obj instanceof FallCountModel) {
                FallCountModel sensorInfo = (FallCountModel) obj;
                if (sensorInfo.getSensor_status().equalsIgnoreCase("connected")) {
                    s_user.setText("Connected with: " + sensorInfo.getName());
                    ImageViewCompat.setImageTintList(status_ic,
                            ColorStateList.valueOf(getResColor(R.color.app_green)));
                } else {
                    ImageViewCompat.setImageTintList(status_ic,
                            ColorStateList.valueOf(getResColor(R.color.red)));
                    s_user.setText("Last connection : " + sensorInfo.getName());
                }
                if (anim == null) {
                    fade_anim(s_user);
                }
            }
        }

        @Override
        public void onError(String error) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (masterData_model.getSensor_id() != null && !masterData_model.getSensor_id().equals("") && !client_id.equals("931")) {
            startTimer();
            sensor_tv.setText(getResString("lbl_sensor") + "- " + masterData_model.getSensor_id());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    Timer sensorTimer;
    TimerTask sensorTimer_Tasks;
    AlphaAnimation anim;

    public void startTimer() {
        printLog(Master_detail_fragment.class.getName() + "  startTimer");
        sensorTimer_Tasks = new TimerTask() {
            @Override
            public void run() {
                getSensorData(masterData_model.getSensor_id(), objectListener);
            }
        };
        sensorTimer = new Timer();
        sensorTimer.schedule(sensorTimer_Tasks, 1000, 5 * 1000);
    }

    public void stopTimer() {
        printLog(Master_detail_fragment.class.getName() + "  stopTimer");
        if (sensorTimer_Tasks != null) {
            sensorTimer_Tasks.cancel();
        }
        if (sensorTimer != null) {
            sensorTimer.cancel();
        }
        if (anim != null) {
            anim.cancel();
            anim = null;
        }
    }

    private void fade_anim(View view) {
        anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(3000);
        anim.setStartOffset(3000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        view.startAnimation(anim);
    }

    ArrayList<GroupUsers> groupUsers;

    @SuppressLint("HandlerLeak")
    public void getGroupUsers() {
        String url;
        url = All_Api.getAll_Users + client_id + "&user_id=" + user_id;
//        url = All_Api.getGroup_Users + role_id + "&client_id=" + client_id + "&user_id=" +
//        user_id;
//        url = All_Api.get_child_users + Static_values.client_id + "&user_id=" + Static_values
//        .user_id;
        Log.e("url ", " is  " + url);
        new NetworkRequest(baseActivity).make_get_request(url,
                new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
                            groupUsers =
                                    new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data").toString(), GroupUsers[].class)));
                            Collections.sort(groupUsers, new Comparator<GroupUsers>() {
                                public int compare(GroupUsers obj1, GroupUsers obj2) {
                                    return obj1.getUacc_username().compareToIgnoreCase(obj2.getUacc_username());
                                }
                            });
                            setUpBreakdownDialog(groupUsers);
                        } else {
//                            checkBlankPage();
                            AppUtils.show_snak(baseActivity, "" + jsonObject.getString("message"));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Log.e("message 1", "   " + message);
            }
        });
    }

    Dialog breakdownDialogView;

    private void setUpBreakdownDialog(ArrayList<GroupUsers> groupUsers) {
        breakdownDialogView = new Dialog(baseActivity, R.style.theme_dialog);
        breakdownDialogView.setContentView(R.layout.breakdown_dialog_lay);
        final EditText rmrk_edt = breakdownDialogView.findViewById(R.id.rmrk_edt);
        MaterialButton submit_btn = breakdownDialogView.findViewById(R.id.sbmt_btn);
        DialogSpinner type_spnr = breakdownDialogView.findViewById(R.id.type_spnr);
        TextView user_spnr = breakdownDialogView.findViewById(R.id.user_spnr);
        List<String> breakdown_value =
                Arrays.asList(baseActivity.getResources().getStringArray(R.array.breakdown_value));
        type_spnr.setItems(breakdown_value, "");
        if (masterData_model.getMdata_breakdown_remark() != null && !masterData_model.getMdata_breakdown_remark().equals("")) {
            rmrk_edt.setText(masterData_model.getMdata_breakdown_remark());
        }
        final ImageView close_btn = breakdownDialogView.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                breakdownDialogView.dismiss();
            }
        });
        user_spnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupUsers != null && groupUsers.size() > 0) chooseUser(groupUsers, user_spnr);
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type_spnr.getSelectedItem().toString().equals("Select breakdown")) {
                    Toast.makeText(baseActivity, "Please select a breakdown type.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                if (rmrk_edt.getText().toString().isEmpty()) {
                    Toast.makeText(baseActivity, "Please enter remark.", Toast.LENGTH_LONG).show();
                    return;
                }
                if (slected_user == null) {
                    Toast.makeText(baseActivity, "Please select a user.", Toast.LENGTH_LONG).show();
                    return;
                }

                HashMap params = new HashMap();
                params.put("client_id", client_id);
                params.put("user_id", user_id);
                params.put("tag_user", slected_user.getUacc_id());
                params.put("mdata_id", masterData_model.getMdata_id());
                if (type_spnr.getSelectedItemPosition() == 1) params.put("is_breakdown", "1");
                else params.put("is_breakdown", "0");
                params.put("breakdown_remark", rmrk_edt.getText().toString());
                submit_data(params, rmrk_edt);

            }
        });
    }

    private void submit_data(HashMap params, EditText rmrk_edt) {
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_post_request(All_Api.breakdown_machine_api, params,
                new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                        if (msg_code.equals("200")) {
                            breakdownDialogView.dismiss();
                            rmrk_edt.setText("");
                            refreshMasterdata(masterData_model.getMdata_id());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
            }
        });
    }

    GroupUsers slected_user;

    private void chooseUser(ArrayList<GroupUsers> groupUsers, TextView user_spnr) {
        Dialog dialog = new Dialog(getContext(), R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        TextView header = dialog.findViewById(R.id.header);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        header.setText("Select a user to Notify");
        if (groupUsers != null && groupUsers.size() > 0) {
            Log.e("data length ", " is " + groupUsers.size());
            CustomRecyclerAdapter ad = new CustomRecyclerAdapter(getContext(), groupUsers);
            RecyclerView listView = dialog.findViewById(R.id._list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(baseActivity,
                    LinearLayoutManager.VERTICAL, false);
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(ad);

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad != null && ad.lastSelected != -1) {
                        slected_user = (GroupUsers) ad.getItem(ad.lastSelected);
                        user_spnr.setText(slected_user.toString());
                    }
                    dialog.cancel();
                }
            });
            srch_prdct.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!srch_prdct.getText().toString().equals("")) {
                        filter(editable.toString().toLowerCase(), groupUsers, ad);
                    }
                }
            });

        }
        dialog.show();
    }

    void filter(String filter_txt, ArrayList<GroupUsers> users, CustomRecyclerAdapter ad) {
        List<GroupUsers> temp = new ArrayList();
        for (GroupUsers d : users) {
            if (d.getUacc_username().toLowerCase().contains(filter_txt)) {
                temp.add(d);
            }
        }
        ad.UpdateData(temp);
    }

    private void refreshMasterdata(final String m_id) {
        new NetworkRequest(baseActivity).getMasterData(m_id, user_id, new ObjectListener() {
            @Override
            public void onResponse(Object obj) {
                Static_values.selectedMasterData_model = (MasterData_model) obj;
                setdata_for_inspection();
            }

            @Override
            public void onError(String error) {
                Log.e("error", "" + error);
            }
        });
    }
}
