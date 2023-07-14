package app.com.arresto.arresto_connect.ui.modules.warehouse;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

public class Assign_user_fragment extends Base_Fragment {
    View view;
    RecyclerView data_list;
    SiteList_Adapter search_listadapter;
    ArrayList component_models;
    TextView assign_btn;
    String type = "", job_no;

    public static Assign_user_fragment newInstance(List data, String job_no) {
        Assign_user_fragment fragmentFirst = new Assign_user_fragment();
        Bundle args = new Bundle();
        args.putString("data", AppUtils.getGson().toJson(data));
        args.putString("job_no", job_no);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.scan_factory_asset, parent, false);
            component_models = new ArrayList<>();
            if (getArguments() != null) {
                component_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(getArguments().getString("data"), Site_Model[].class)));
                job_no = getArguments().getString("job_no");
            }
            find_id();

        }
        return view;
    }

    TextView user_name, job_name, ttl_tv;

    private void find_id() {
        data_list = view.findViewById(R.id.product_list);
        assign_btn = view.findViewById(R.id.add_jobcard_btn);
        job_name = view.findViewById(R.id.job_name);
        user_name = view.findViewById(R.id.user_name);
        ttl_tv = view.findViewById(R.id.ttl_tv);
        view.findViewById(R.id.ttl_layer).setVisibility(View.VISIBLE);
        user_name.setVisibility(View.GONE);
        assign_btn.setText(getResString("lbl_assign_users"));
        setdata();
    }

    private void setdata() {
        job_name.setText("Job No.: " + job_no);
        ttl_tv.setText("Total Assets: " + component_models.size());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        data_list.setLayoutManager(layoutManager);
        data_list.setPadding(3, 6, 3, 6);
        data_list.getLayoutManager().setMeasurementCacheEnabled(false);
        search_listadapter = new SiteList_Adapter(this, component_models, "warehouse_assign");
        data_list.setAdapter(search_listadapter);
        fetch_GroupUsers();
        assign_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (search_listadapter.getSelected_Data().size() > 0)
                    selectUser();
                else
                    show_snak(getActivity(), "Please select minimum one asset to assign.");
            }
        });
    }

    ArrayList<GroupUsers> groupUsers;

    public void fetch_GroupUsers() {
        groupUsers = new ArrayList<>();
        String url;
//         url = All_Api.getGroup_Users + role_id + "&client_id=" + client_id + "&user_id=" + user_id+ "&filter=client";
        url = All_Api.getAll_Users + client_id + "&user_id=" + user_id+ "&user_type=client";
        new NetworkRequest(baseActivity).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (object.get("data") instanceof JSONArray) {
                            groupUsers = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), GroupUsers[].class)));
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

    public void selectUser() {
        if (groupUsers.size() < 1) {
            show_snak(getActivity(), "No group user found to assign asset");
            return;
        }
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        dialog.setCancelable(true);
        MaterialButton save_btn = dialog.findViewById(R.id.save_btn);
        TextView header = dialog.findViewById(R.id.header);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        header.setText(getResString("lbl_select_user"));
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        RecyclerView lvLangs = dialog.findViewById(R.id._list);
        lvLangs.setLayoutManager(new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false));
        CustomRecyclerAdapter ad;
        save_btn.setText("Assign");
        if (groupUsers.size() > 0) {
            ad = new CustomRecyclerAdapter(baseActivity, groupUsers);
            lvLangs.setAdapter(ad);
            dialog.show();
        } else {
            ad = null;
        }
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad != null && ad.lastSelected > -1) {
                   Object slected_item= ad.getItem(ad.lastSelected);
                   if(slected_item!=null){
                       AssetRegisterToUser((GroupUsers) slected_item);
                   }
                    dialog.dismiss();
                } else
                    Toast.makeText(baseActivity, "Please select an user!", Toast.LENGTH_SHORT).show();
            }
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

    void filter(String filter_txt, ArrayList<GroupUsers> users, CustomRecyclerAdapter ad) {
        List<GroupUsers> temp = new ArrayList();
        for (GroupUsers d : users) {
            if (d.getUacc_username().toLowerCase().contains(filter_txt)) {
                temp.add(d);
            }
        }
        ad.UpdateData(temp);
    }

    Site_Model singleAssignSite;

    public void assignSingle(int position) {
        singleAssignSite = (Site_Model) component_models.get(position);
        selectUser();
    }

    void AssetRegisterToUser(GroupUsers groupUsers) {
        NetworkRequest networkRequest = new NetworkRequest(baseActivity);
        JSONObject params = new JSONObject();
        try {
            params.put("client_id", client_id);
            params.put("assign_to", groupUsers.getUacc_id());
            params.put("assign_by", user_id);
            params.put("user_id", user_id);
            params.put("jobno", job_no);
            JSONArray assets = new JSONArray();
            params.put("assets", assets);
            if (singleAssignSite != null) {
                JSONObject object = new JSONObject();
                object.put("mdata_uin", singleAssignSite.getMdata_uin());
                object.put("master_id", singleAssignSite.getMaster_id());
                assets.put(object);
            } else {
                for (Site_Model siteModel : search_listadapter.getSelected_Data()) {
                    JSONObject object = new JSONObject();
                    object.put("mdata_uin", siteModel.getMdata_uin());
                    object.put("master_id", siteModel.getMaster_id());
                    assets.put(object);
                }
            }
        } catch (Exception e) {
            printLog(e.getMessage());
        }

        networkRequest.make_contentpost_request(All_Api.register_rental_assets, params, new NetworkRequest.VolleyResponseListener() {
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
                            AppUtils.show_snak(baseActivity, jsonObject.getString("message"));
                            if (singleAssignSite != null) {
                                component_models.remove(singleAssignSite);
                                singleAssignSite = null;
                            } else {
                                for (Site_Model siteModel : search_listadapter.getSelected_Data()) {
                                    component_models.remove(siteModel);
                                }
                            }
                            search_listadapter.getSelected_Data().clear();
//                            search_listadapter.isSelectEnable = false;
                            if (component_models.size() > 0)
                                search_listadapter.notifyDataSetChanged();
                            else HomeActivity.homeActivity.submit_action("dashboard");
                        } else {
                            AppUtils.show_snak(baseActivity, jsonObject.getString("message"));
//                            update_list();
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

}

