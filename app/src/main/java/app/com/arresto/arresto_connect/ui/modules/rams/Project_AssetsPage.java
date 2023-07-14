package app.com.arresto.arresto_connect.ui.modules.rams;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Asset_Adapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.Main_Fragment;
import app.com.arresto.arresto_connect.ui.modules.inspection.ClientSignatureFragment;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.adapters.Project_listAdapter.slctd_project;
import static app.com.arresto.arresto_connect.ui.fragments.Main_Fragment.is_refresh;

public class Project_AssetsPage extends Base_Fragment implements View.OnClickListener {
    View view;
    public String page_type = "";
    Project_Model project;

    LinearLayout btn_section;
    TextView checkin_btn, checkout_btn;
    Handler refreshHandler;

    @SuppressLint("HandlerLeak")
    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.project_assets_page, parent, false);
            project = slctd_project;
            if (getArguments() != null) {
                page_type = getArguments().getString("page_type", "");
            }
            findAllIds();
            init_data();
            refreshHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    if ((boolean) msg.obj) {
                        setVisblity(false);
                    }
                }
            };
            refreshHandler.obtainMessage();
        } else {
            if (is_refresh) {
                init_data();
                is_refresh = false;
            }
        }
        return view;
    }

    RecyclerView listView;
    public EditText searchView;
    public TextView ad_stor_btn, users_btn, submit_btn, finish_project_btn;
    LinearLayout custom_pages;
    SwipeRefreshLayout swipe;

    public void findAllIds() {
        custom_pages = view.findViewById(R.id.custom_pages);
        searchView = view.findViewById(R.id.search_view);
        listView = view.findViewById(R.id.suggestion_list);
        swipe = view.findViewById(R.id.swipe);

        ad_stor_btn = view.findViewById(R.id.ad_stor_btn);
        finish_project_btn = view.findViewById(R.id.finish_project_btn);
        submit_btn = view.findViewById(R.id.submit_btn);
        users_btn = view.findViewById(R.id.users_btn);

        btn_section = view.findViewById(R.id.btn_section);
        checkin_btn = view.findViewById(R.id.checkin_btn);
        checkout_btn = view.findViewById(R.id.checkout_btn);
        btn_section.setVisibility(VISIBLE);
        ad_stor_btn.setOnClickListener(this);
        finish_project_btn.setOnClickListener(this);
        users_btn.setOnClickListener(this);
        checkin_btn.setOnClickListener(this);
        checkout_btn.setOnClickListener(this);
        submit_btn.setOnClickListener(this);

        if (!project.getUp_enddate().equals("") && baseActivity.server_date_format.format(new Date()).equals(project.getUp_enddate())) {
            finish_project_btn.setVisibility(VISIBLE);
        }


        swipe.setColorSchemeResources(R.color.app_text, R.color.app_btn_bg, R.color.app_green);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                init_data();
            }
        });
        setdata();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.finish_project_btn:
                if (Static_values.group_id.equals("15") && page_type.equals("project_data")) {
//                    if (!iconSwitch.getChecked().equals(LEFT))
//                        show_snak(getActivity(), getResString("lbl_rope_dwn_msg"));
//                    else
                    if (continue_btn.contains(0))
                        show_snak(getActivity(), getResString("lbl_fill_form_msg"));
                    else if (asset_adapter != null && asset_adapter.getAssetStatus().contains("checkin"))
                        show_snak(getActivity(), getResString("lbl_chk_out_msg"));
                    else
                        returnToStoreDialog();
                }
                break;
            case R.id.ad_stor_btn:
                if (page_type.equals("project_data") && Static_values.group_id.equals("15")) {
                    load_fragment("", "Add to Project", "add_to_project");
                }
                break;

            case R.id.users_btn:
                if (project.getTeam() != null && project.getTeam().size() > 0)
                    LoadFragment.replace(UsersListFragment.newInstance("rope", asset_adapter.getAssetStatus().contains("checkin")), baseActivity, getResString("lbl_team_users"));
                else show_snak(baseActivity, "Please create a team first!");
                break;
            case R.id.checkin_btn:
                if (asset_adapter != null) {
                    asset_adapter.updateStatus("checkin");
                    if (component_models.size() > 0)
                        setVisblity(true);
                }
                break;
            case R.id.checkout_btn:
                if (asset_adapter != null) {
                    asset_adapter.updateStatus("checkout");
                    if (component_models.size() > 0)
                        setVisblity(true);
                }

                break;
            case R.id.submit_btn:
                if (asset_adapter != null) {
                    if (asset_adapter.statusAll.equals("checkin")) {
                        asset_adapter.submitStatus(null, "", "");
                    } else if (asset_adapter.statusAll.equals("checkout")) {
                        asset_adapter.Checkout_Dialog(null);
                    }
                }
                break;
        }
    }


    private void setdata() {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
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
                if (!TextUtils.isEmpty(newText)) {
                    setList_inspectorData(newText);
                }
            }
        });
    }

    public void setVisblity(boolean isSubmit) {
        if (isSubmit) {
            submit_btn.setVisibility(VISIBLE);
            users_btn.setVisibility(View.GONE);
            ad_stor_btn.setVisibility(View.GONE);
        } else {
            submit_btn.setVisibility(View.GONE);
            users_btn.setVisibility(VISIBLE);
            ad_stor_btn.setVisibility(VISIBLE);
        }

    }

    private void setList_inspectorData(String newText) {
        if (page_type.equals("project_data")) {
            ArrayList<Component_model> slctcomp_mdels = new ArrayList<>();
            for (int i = 0; i < component_models.size(); i++) {
                Component_model component_model = component_models.get(i);
                if (component_model.getComponent_code().toLowerCase().contains(newText.toLowerCase()) || component_model.getMdata_uin().toLowerCase().contains(newText.toLowerCase())) {
                    slctcomp_mdels.add(component_model);
                }
            }
            if (asset_adapter == null) {
                asset_adapter = new Asset_Adapter(baseActivity, project, component_models, page_type, refreshHandler);
                listView.setAdapter(asset_adapter);
                asset_adapter.addData(slctcomp_mdels);
            } else {
                asset_adapter.addData(slctcomp_mdels);
            }
        }
    }

    public void init_data() {
        ad_stor_btn.setText(getResString("lbl_add_assets"));
        ad_stor_btn.setVisibility(VISIBLE);
        DataHolder_Model.getInstance().setComponent_models(new ArrayList<>());
        get_forms_data();
        component_models = new ArrayList<>();
        get_component();
        initiate_tabs();
        if (swipe.isRefreshing())
            swipe.setRefreshing(false);
    }

    public void initiate_tabs() {
        users_btn.setVisibility(VISIBLE);
        update_custom_forms();
    }

    /*
        ASM USER tabs setting here
    */

    @SuppressLint("HandlerLeak")
    public void get_forms_data() {
        NetworkRequest.getCustom_Data(getActivity(), All_Api.get_custom_forms_data + user_id + "&client_id=" + client_id + "&project_id=" + project.getUp_id() + "&time=" + System.currentTimeMillis(), "data", new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    custom_pages.removeAllViews();
                    update_custom_forms();
                }
                //       process incoming messages here
                //       this will run in the thread, which instantiates it
            }
        });
    }


    ArrayList<Integer> continue_btn;

    public void update_custom_forms() {
        ArrayList<String> submited_forms_id = new ArrayList<>();
        ArrayList<String> last_submit_time = new ArrayList<>();
        if (DataHolder_Model.getInstance().getCustomViewsData_models() != null) {
            for (CustomForm_Model item : DataHolder_Model.getInstance().getCustomViewsData_models()) {
                submited_forms_id.add(item.getForm_id());
                try {
                    last_submit_time.add(baseActivity.Date_Format().format(baseActivity.server_date_time.parse(item.getCreated_date())));
                } catch (ParseException e) {
                    e.printStackTrace();
                    last_submit_time.add(item.getCreated_date());
                }
//                last_submit_time.add(sdf.format(sdf.format(item.getCreated_date())));
            }
        }

        //   Custom forms code here

        final ArrayList<CustomForm_Model> customViews_model = new ArrayList<>();

        final ArrayList<String> all_form_id = new ArrayList<>();
        for (CustomForm_Model customForm_model : DataHolder_Model.getInstance().getCustomViews_models()) {
            all_form_id.add(customForm_model.getCf_id());
            if (project.getUp_forms().contains(customForm_model.getCf_id())) {
                customViews_model.add(customForm_model);
            }
        }
        if (customViews_model.size() > 0)
            custom_pages.setVisibility(VISIBLE);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        String current_date = baseActivity.Date_Format().format(new Date());
        continue_btn = new ArrayList<>();
        for (int i = 0; i < customViews_model.size(); i++) {
            final CustomForm_Model form = customViews_model.get(i);
            TextView tab_label = new TextView(baseActivity);
            params1.setMargins(5, 3, 5, 5);
            tab_label.setLayoutParams(params1);
            tab_label.setGravity(Gravity.CENTER);
            tab_label.setSingleLine(false);
            tab_label.setLines(2);
            tab_label.setMaxLines(2);
            tab_label.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            tab_label.setBackgroundResource(R.drawable.edittext_bg1);
            GradientDrawable gradientDrawable = (GradientDrawable) tab_label.getBackground();
            gradientDrawable.setStroke(1, Dynamic_Var.getInstance().getBtn_bg_clr());
            tab_label.setPadding(10, 0, 10, 0);
            tab_label.setBackground(gradientDrawable);
            tab_label.setText(form.getForm_name());
            tab_label.setTextColor(Dynamic_Var.getInstance().getApp_background());

            final int finalI = i;
            continue_btn.add(8);
            final int form_Data_index = submited_forms_id.indexOf(form.getCf_id());
            if (form_Data_index > -1 && form.getForm_occurrence().equals("One Time"))
                gradientDrawable.setColor(AppUtils.getResColor(R.color.app_green));
            else if (form_Data_index > -1 && last_submit_time.get(form_Data_index).equals(current_date))
                gradientDrawable.setColor(AppUtils.getResColor(R.color.app_green));
            else {
                gradientDrawable.setColor(AppUtils.getResColor(R.color.app_error));
                continue_btn.set(i, 0);
            }

            tab_label.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AsmCustomPage asmCustomPage = AsmCustomPage.newInstance(all_form_id.indexOf(form.getCf_id()), form_Data_index, continue_btn.get(finalI));
                    asmCustomPage.setUpdate_Event(new AsmCustomPage.Update_formEvent() {
                        @Override
                        public void OnUpdate() {
                            get_forms_data();
                        }
                    });
                    LoadFragment.replace(asmCustomPage, getActivity(), "" + project.getUp_project_name());
                }
            });
            custom_pages.addView(tab_label);
        }
    }

    Asset_Adapter asset_adapter;
    private ArrayList<Component_model> component_models;

    public void get_component() {
        if (isNetworkAvailable(getActivity())) {
            listView.setPadding(3, 5, 5, 155);
            listView.setClipToPadding(false);
            String url;
            if (!project.getUp_id().equals(""))
                url = All_Api.project_data + user_id + "&project_id=" + project.getUp_id();
            else
                return;
            url = url + "&client_id=" + client_id;

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
                            if (jsonObject.getString("status_code").equals("200")) {
                                component_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Component_model[].class)));
                                DataHolder_Model.getInstance().setComponent_models(component_models);
                                asset_adapter = new Asset_Adapter(baseActivity, project, component_models, page_type, refreshHandler);
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

    public void load_fragment(String filter, String page_name, String page_type) {
        Main_Fragment main_fragment = new Main_Fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("filter", filter);
        bundle1.putString("page_type", page_type);
        main_fragment.setArguments(bundle1);
        LoadFragment.replace(main_fragment, getActivity(), page_name);
    }

    private void returnToStoreDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("Product return to store!")
                .setMessage("Are you wish to return all product to store?")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        LoadFragment.replace(ClientSignatureFragment.newInstance("Asm_Sign"), getActivity(), getResString("lbl_suprvisr_msg"));
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        return_toStoreAll();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    public void return_toStoreAll() {
        HashMap<String, String> params = new HashMap<>();
        params.put("store_id", "");
        params.put("user_id", user_id);
        params.put("product_id", "");
        params.put("project_id", project.getUp_id());
        params.put("client_id", client_id);
        String master_id = "";
        for (Component_model component_model : component_models) {
            if (master_id.equals(""))
                master_id = component_model.getMdata_id();
            else
                master_id = master_id + "," + component_model.getMdata_id();
        }

        params.put("mdata_id", master_id);
        NetworkRequest.post_data(baseActivity, All_Api.return_to_store, params, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
//                                create_Historydialog(activity);
                    LoadFragment.replace(ClientSignatureFragment.newInstance("Asm_Sign"), getActivity(), getResString("lbl_suprvisr_msg"));
                }
            }
        });
    }
}
