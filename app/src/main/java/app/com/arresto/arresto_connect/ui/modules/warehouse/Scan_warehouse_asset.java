package app.com.arresto.arresto_connect.ui.modules.warehouse;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.showUIN_Dialog;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.interfaces.AlertClickListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.Group_Filter_fragment;
import app.com.arresto.arresto_connect.ui.fragments.SitesMapFragment;

public class Scan_warehouse_asset extends Base_Fragment {
    View view;
    MaterialButton scan_btn;
    RecyclerView data_list;
    SiteList_Adapter siteList_adapter;
    ArrayList<Site_Model> component_models;
    String input_type = "";
    RadioGroup rg1;
    TextView user_name, job_name, ttl_tv, add_jobcard_btn;
    String type = "";
    ArrayList<String> searched_array;

    public static Scan_warehouse_asset newInstance(String pageType) {
        Scan_warehouse_asset fragmentFirst = new Scan_warehouse_asset();
        Bundle args = new Bundle();
        args.putString("pageType", pageType);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.scan_factory_asset, parent, false);
            searched_array = new ArrayList<>();
            component_models = new ArrayList<>();
            if (getArguments() != null) {
                type = getArguments().getString("pageType", "");
            }
            find_id();
            scan_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (input_type.equalsIgnoreCase("Barcode")) {
                        scan_barcode(new BarcodeListener() {
                            @Override
                            public void onScanned(String scaned_text) {
                                if (scaned_text.contains("https://arresto.in/")) {
                                    scaned_text = Uri.parse(scaned_text).getQueryParameter("u");
                                    search_scaned_data(scaned_text, "uin");
                                } else
                                    search_scaned_data(scaned_text, input_type);

                            }
                        });
                    } else if (input_type.equalsIgnoreCase("UIN")) {
                        showUIN_Dialog(baseActivity, new Handler() {
                            @SuppressLint("HandlerLeak")
                            @Override
                            public void handleMessage(@NonNull Message msg) {
                                super.handleMessage(msg);
                                if (msg.obj != null && !(msg.obj.toString()).equals("")) {
                                    search_scaned_data(msg.obj.toString(), input_type);
                                } else
                                    AppUtils.show_snak(getActivity(), getResString("lbl_entr_uin_msg"));
                            }
                        });
                    } else
                        AppUtils.show_snak(getActivity(), getResString("lbl_slct_inpt_msg"));
                }
            });
        }
        return view;
    }

    private void find_id() {
        scan_btn = view.findViewById(R.id.scan_btn);
        ttl_tv = view.findViewById(R.id.ttl_tv);
        job_name = view.findViewById(R.id.job_name);
        user_name = view.findViewById(R.id.user_name);
        data_list = view.findViewById(R.id.product_list);
        add_jobcard_btn = view.findViewById(R.id.add_jobcard_btn);
        rg1 = view.findViewById(R.id.rg1);
        scan_btn.setVisibility(View.VISIBLE);
        ((ViewGroup) rg1.getParent()).setVisibility(View.VISIBLE);
        setdata();
    }

    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        data_list.setLayoutManager(layoutManager);
        data_list.setPadding(3, 6, 3, 6);
        data_list.getLayoutManager().setMeasurementCacheEnabled(false);
        fetch_JobCards();
        if (type.equals("returnAsset")) {
            add_jobcard_btn.setText("Return Asset");
            view.findViewById(R.id.ttl_layer).setVisibility(View.VISIBLE);
            siteList_adapter = new SiteList_Adapter(this, component_models, "returnAsset");
        } else {
            siteList_adapter = new SiteList_Adapter(this, component_models, "inspection");
        }

        data_list.setAdapter(siteList_adapter);
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                input_type = "" + (group.findViewById(checkedId)).getTag();
                if(input_type.equalsIgnoreCase("uin")){
                    scan_btn.setText(getResString("lbl_entr_no"));
                }else{
                    scan_btn.setText(getResString("lbl_scan_st"));
                }
            }
        });
        add_jobcard_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!type.equals("returnAsset")) {
                    if (component_models.size() > 0)
                        addJobDialog();
                    else
                        show_snak(getActivity(), "Please select minimum one asset to assign.");
                } else {
                    if (slctdJob == null) {
                        show_snak(baseActivity, "Please select a Job No.");
                    } else if (slctdUser == null) {
                        show_snak(baseActivity, "Please select a User");
                    } else if (siteList_adapter.getSelected_Data().size() > 0)
                        returnFromUser();
                    else
                        show_snak(getActivity(), "Please select minimum one asset to return.");
                }
            }
        });

        job_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("returnAsset") && jobcards != null && jobcards.size() > 0) {
                    showJobDialog();
                }
            }
        });

        user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (type.equals("returnAsset") && jobUsers != null && jobUsers.size() > 0) {
                    showJobDialog();
                }
            }
        });
    }

    public void search_scaned_data(final String key, String input_type) {
        if (isNetworkAvailable(getActivity())) {
            String url;
            if (searched_array.contains(key.toLowerCase())) {
                show_snak(getActivity(), getResString("lbl_alrdy_lst_msg"));
                return;
            }
            url = All_Api.search_Data + input_type.toLowerCase() + "=" + key + "&client_id=" + client_id + "&user_id=" + user_id + "&geo_location=" + baseActivity.curr_lat + "," + baseActivity.curr_lng;
            url = url.replace(" ", "%20");
            NetworkRequest networkRequest = new NetworkRequest(getActivity());
            networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONArray) {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("error")) {
                                show_snak(getActivity(), jsonObject.getString("error"));
                            } else {
                                component_models.addAll(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(response, Site_Model[].class))));
                                siteList_adapter.updateData(component_models);
                                data_list.setVisibility(View.VISIBLE);
                            }
                        } else {
                            JSONObject jsonObject = new JSONObject(response);
                            ;
                            show_snak(getActivity(), jsonObject.getString("message"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "" + e.getMessage());
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

    private void removeOverdueAssets() {
//        for (int j = component_models.size() - 1; j >= 0; j--) {
//            if (component_models.get(j).getInspection_status().equals("over")) {
//                component_models.remove(j);
//            }
//        }
//        siteList_adapter.notifyDataSetChanged();
        if (component_models.size() > 0)
            addJobDialog();
    }

    public void addJobDialog() {
        ArrayList<Site_Model> overDueAssets = new ArrayList<>();
        for (int j = 0; j < component_models.size(); j++) {
            Site_Model item = component_models.get(j);
            if (item.getInspection_status().equals("over")) {
                overDueAssets.add(item);
            }
        }
        if (overDueAssets.size() > 0) {
            baseActivity.show_OkAlert("Alert!", "Few Assets are overdue for inspection. Please go to Dashboard for " +
                            "inspecting the asset. Do you wish to skip those assets or inspect now?",
                    "Skip Inspection", "Inspect Assets", new AlertClickListener() {
                        @Override
                        public void onYesClick() {
                            removeOverdueAssets();
                        }

                        @Override
                        public void onNoClick() {
                            Group_Filter_fragment sitesMapFragment = new Group_Filter_fragment();
                            Bundle args = new Bundle();
                            args.putString("sites", AppUtils.getGson().toJson(overDueAssets));
                            args.putString("page_type", "warehouseOverdue");
                            sitesMapFragment.setArguments(args);
                            LoadFragment.replace(sitesMapFragment, baseActivity, "Warehouse Overdue");
                        }
                    });
            return;
        }

        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_add_jobcard);
        final EditText job_tv = dialog.findViewById(R.id.job_tv);
        MaterialButton submit_btn = dialog.findViewById(R.id.submit_btn);
        DialogSpinner jobcard_spnr = dialog.findViewById(R.id.jobcard_spnr);
        final ImageView close_btn = dialog.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        jobcard_spnr.setItems(jobcards, "");
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                LoadFragment.replace(Assign_user_fragment.newInstance(component_models, "Test job"), baseActivity, getResString("lbl_assign_users"));
//                dialog.dismiss();
                String job_no = job_tv.getText().toString();
                if (!job_no.isEmpty()) {
                    submit_data(job_no);
                    dialog.dismiss();
                } else if (!jobcard_spnr.getSelectedItem().toString().equals("") && !jobcard_spnr.getSelectedItem().toString().equals("Select a Job No.")) {
                    LoadFragment.replace(Assign_user_fragment.newInstance(component_models, jobcard_spnr.getSelectedItem().toString()), baseActivity, getResString("lbl_assign_users"));
                    dialog.dismiss();
                } else {
                    show_snak(baseActivity, "Please Enter Job No.");
                }

            }
        });
        dialog.show();
        hide_progress();
    }


    private void submit_data(String job_no) {
        HashMap params = new HashMap();
        params.put("client_id", client_id);
        params.put("created_by", Static_values.user_id);
        params.put("jobno", job_no);
        Log.e("field set ", " is " + params);

        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_post_request(All_Api.create_job_api, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
//                            update_formEvent.OnUpdate();
                            LoadFragment.replace(Assign_user_fragment.newInstance(component_models, job_no), baseActivity, getResString("lbl_assign_users"));
                        } else {
                            AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
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


    // return Code From Here

    ArrayList<Constant_model> jobcards;

    public void fetch_JobCards() {
        jobcards = new ArrayList<>();
        String url = All_Api.get_rental_jobcards + client_id + "&user_id=" + user_id;
        new NetworkRequest(baseActivity).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (object.get("data") instanceof JSONArray) {
                            jobcards = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            Constant_model msg_object = new Constant_model();
                            msg_object.setName("Select a Job No.");
                            jobcards.add(0, msg_object);
                            if (type.equals("returnAsset")) {
                                selectJob();
                                showJobDialog();
                            }
                        }
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

    ArrayList<GroupUsers> jobUsers;

    public void fetch_JobUsers(String jobNo, DialogSpinner user_spnr) {
        jobUsers = new ArrayList<>();
        String url = All_Api.get_rental_users_api + client_id + "&user_id=" + user_id + "&jobno=" + jobNo;
        printLog(url);
        new NetworkRequest(baseActivity).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (object.get("data") instanceof JSONArray) {
                            jobUsers = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), GroupUsers[].class)));
                            GroupUsers msg_object = new GroupUsers();
                            msg_object.setUacc_username(getResString("lbl_select_user"));
                            jobUsers.add(0, msg_object);

                            if (user_spnr != null)
                                user_spnr.setItems(jobUsers, "");
                        }
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

    void returnFromUser() {
        JSONArray assets = new JSONArray();
        try {
            for (Site_Model siteModel : siteList_adapter.getSelected_Data()) {
                JSONObject object = new JSONObject();
                if (siteModel.getAsset_status() != null && !siteModel.getAsset_status().equals(""))
                    object.put("status", siteModel.getAsset_status());
                else if (commonStatus.equals("")) {
                    missingStatusAlert();
                    return;
                } else {
                    object.put("status", commonStatus);
                }
                object.put("mdata_uin", siteModel.getMdata_uin());
                object.put("master_id", siteModel.getMaster_id());
                assets.put(object);
            }
            submitReturn(assets, false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    Constant_model slctdJob;
    GroupUsers slctdUser;

    void submitReturn(JSONArray assets, boolean isForce) {
        NetworkRequest networkRequest = new NetworkRequest(baseActivity);
        JSONObject params = new JSONObject();
        try {
            params.put("client_id", client_id);
            params.put("assign_to", slctdUser.getUacc_id());
            params.put("user_id", user_id);
            params.put("jobno", slctdJob.getName());
            params.put("force_update", isForce);
            params.put("assets", assets);

        } catch (Exception e) {
            printLog(e.getMessage());
        }

        networkRequest.make_contentpost_request(All_Api.return_asset_api, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (!msg_code.equals("200")) {
                            AppUtils.show_snak(baseActivity, jsonObject.getString("message"));
                        } else {
                            if (jsonObject.has("missmatched_assets") &&
                                    jsonObject.getString("missmatched_assets").length() > 2) {
                                JSONArray mismatchAssets = jsonObject.getJSONArray("missmatched_assets");
                                String msgAsstes = "";
                                for (int k = 0; k < mismatchAssets.length(); k++) {
                                    JSONObject obj = mismatchAssets.getJSONObject(k);
                                    msgAsstes = msgAsstes + obj.getString("mdata_uin") + ",";
                                }
                                baseActivity.show_OkAlert("", "Assets " + msgAsstes + " is issued to other user would " +
                                        "you like to return these assets?", "Yes", "No", new AlertClickListener() {
                                    @Override
                                    public void onYesClick() {
                                        submitReturn(mismatchAssets, true);
                                    }

                                    @Override
                                    public void onNoClick() {
                                        HomeActivity.homeActivity.submit_action("dashboard");
                                    }
                                });
                            } else {
                                AppUtils.show_snak(baseActivity, jsonObject.getString("message"));
                                baseActivity.show_OkAlert("", "All assets successfully returned to Present Job No. " + slctdJob.getName(), "Ok", null, new AlertClickListener() {
                                    @Override
                                    public void onYesClick() {
                                        HomeActivity.homeActivity.submit_action("dashboard");
                                    }

                                    @Override
                                    public void onNoClick() {

                                    }
                                });
                            }
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

    Dialog dialog;

    public void selectJob() {
        dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.select_job_dialoge);
        dialog.setCancelable(true);
        DialogSpinner jobcard_spnr, user_spnr;
        MaterialButton save_btn = dialog.findViewById(R.id.save_btn);
        MaterialButton cancel_btn = dialog.findViewById(R.id.cancel_btn);
        jobcard_spnr = dialog.findViewById(R.id.jobcard_spnr);
        user_spnr = dialog.findViewById(R.id.user_spnr);
        jobcard_spnr.setItems(jobcards, "");
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelJobDialog();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelJobDialog();
            }
        });


        jobcard_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int position, long l) {
                slctdJob = (Constant_model) jobcard_spnr.getSelectedItem();
                job_name.setText("Job No.: " + slctdJob.getName());
                fetch_JobUsers(jobcard_spnr.getSelectedItem().toString(), user_spnr);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        user_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View v, int position, long l) {
                slctdUser = (GroupUsers) user_spnr.getSelectedItem();
                user_name.setText("User: " + slctdUser.toString());
                if (slctdUser.getAsset_count() != null && !slctdUser.getAsset_count().equals("")) {
                    ttl_tv.setVisibility(View.VISIBLE);
                    ttl_tv.setText("Total Assets: " + slctdUser.getAsset_count());
                } else {
                    ttl_tv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void missingStatusAlert() {
        baseActivity.show_OkAlert("Status Missing!", "Some of your asset's status is missing.\nPlease enter status for all", "Yes", "No", new AlertClickListener() {
            @Override
            public void onYesClick() {
                status_Dialog(-2);
            }

            @Override
            public void onNoClick() {

            }
        });
    }

    String commonStatus = "";

    public void status_Dialog(int pos) { // for user
        Dialog dialoglayout = new Dialog(baseActivity, R.style.theme_dialog);
        dialoglayout.setContentView(R.layout.asset_status_lay);
        dialoglayout.setCancelable(true);

        TextView title_tv = dialoglayout.findViewById(R.id.title_tv);
        ImageView close_btn = dialoglayout.findViewById(R.id.close_btn);
        title_tv.setText("Select Status");
        final LinearLayout remark_lay = dialoglayout.findViewById(R.id.remark_lay);
        final EditText remark_edt = dialoglayout.findViewById(R.id.remark_edt);
        final DialogSpinner status_spnr = dialoglayout.findViewById(R.id.status_spnr);

        MaterialButton save_btn = dialoglayout.findViewById(R.id.save_btn);
        List<String> status_items = Arrays.asList(baseActivity.getResources().getStringArray(R.array.warehouse_value));
        status_spnr.setItems(status_items, "");
        status_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 5) {
                    remark_lay.setVisibility(View.VISIBLE);
                } else {
                    remark_lay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status_spnr.getSelectedItemPosition() > 0) {
                    String reason;
                    if (status_spnr.getSelectedItemPosition() == 5) {
                        reason = "" + remark_edt.getText();
                    } else {
                        reason = status_items.get(status_spnr.getSelectedItemPosition());
                    }
                    if (pos == -2) {
                        commonStatus = reason;
                        returnFromUser();
                    } else
                        component_models.get(pos).setAsset_status(reason);
                    dialoglayout.cancel();
                } else {
                    AppUtils.show_snak(baseActivity, "Please select asset's current status");
                }
            }
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialoglayout.cancel();
            }
        });
        dialoglayout.show();
    }

    public void showJobDialog() {
        dialog.show();
    }

    public void cancelJobDialog() {
        dialog.cancel();
    }


}
