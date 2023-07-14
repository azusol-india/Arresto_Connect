/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.periodic_maintainance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.database.pdm_tables.Step_subitem_table;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.modules.inspection.ClientSignatureFragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.slctd_product_name;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.network.All_Api.post_asset_count;
import static app.com.arresto.arresto_connect.ui.modules.periodic_maintainance.Periadic_steps.inspection_id;

public class Periodic_steps_subitem extends Fragment {
    View view;
    MaterialButton finish_btn;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.periodic_steps_list, container, false);
            sub_cmpnt_layer = view.findViewById(R.id.sub_cmpnt_layer);
            finish_btn = view.findViewById(R.id.finish_btn);

            if (AppUtils.isNetworkAvailable(getActivity()))
                get_Component_data(All_Api.components_service + slctd_product_name + "?client_id=" + client_id + "&pdm=1");
            else {
                Component_model component_model = new Gson().fromJson(AppController.getInstance().getDatabase().getComponents_Dao().getComponents(slctd_product_name, client_id), Component_model.class);
                if (component_model != null)
                    set_Subasset_list(component_model);
            }

            finish_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    post_subasset_data();
                    save_data();

                }
            });
        }
        return view;
    }

    @SuppressLint("HandlerLeak")
    private void get_Component_data(String components_service) {
        NetworkRequest.getComponents(getActivity(), components_service, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    set_Subasset_list(DataHolder_Model.getInstance().getComponent_models().get(0));
                }
                // process incoming messages here
                // this will run in the thread, which instantiates it
            }
        });
    }

    LinearLayout sub_cmpnt_layer;
    JSONArray cmpnt_qty_lstary;
    String subAssets;

    public void set_Subasset_list(Component_model component_model) {
        cmpnt_qty_lstary = new JSONArray();
        sub_cmpnt_layer.removeAllViews();
        subAssets = component_model.getComponent_sub_assets();
        if (subAssets != null && !subAssets.equals("0") && !subAssets.equals("")) {
            final List<String> sub_ast_name = Arrays.asList(subAssets.split("##"));
            for (int j = 0; j < sub_ast_name.size(); j++) {
                String[] row_data = sub_ast_name.get(j).split("#");
                final String sub_asset_code = row_data[0];
                String sub_asset_image = row_data[1];
                View view1 = getActivity().getLayoutInflater().inflate(R.layout.periodic_steps_subitem, sub_cmpnt_layer, false);
                TextView ast_txtx = view1.findViewById(R.id.subast_tv);
                ImageView comp_img = view1.findViewById(R.id.item_img);
                final EditText qty_edt = view1.findViewById(R.id.qty_edt);
                ast_txtx.setText(sub_asset_code);
                AppUtils.load_image(sub_asset_image, comp_img);
                sub_cmpnt_layer.addView(view1);
                qty_edt.setText("0");
                qty_edt.setId(j);
                final JSONObject subassetObject = new JSONObject();
                cmpnt_qty_lstary.put(subassetObject);
                qty_edt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        try {
                            subassetObject.put("subasset_code", sub_asset_code);
                            subassetObject.put("subasset_count", qty_edt.getText().toString());
                            cmpnt_qty_lstary.put(qty_edt.getId(), subassetObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
    }

    public void post_subasset_data() {

        JSONObject params = new JSONObject();
        try {
            params.put("asset_code", slctd_product_name);
            params.put("asset_id", slctd_product_name);
            params.put("user_id", Static_values.user_id);
            params.put("client_id", client_id);
            params.put("master_id", Static_values.selectedMasterData_model.getMdata_id());
            params.put("subasset_list", cmpnt_qty_lstary);

            if (!inspection_id.equals(""))
                params.put("inspection_id", inspection_id);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
//        Log.e("params", "" + params);
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(post_asset_count, params, new NetworkRequest.VolleyResponseListener() {
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
                            AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                            LoadFragment.replace(ClientSignatureFragment.newInstance("Pdm_Sign"), getActivity(), getResString("lbl_client_remark"));
//                            LoadFragment.replace(Inspection3.newInstance("Pdm_Sign"),  getActivity(), "Add Signature");
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

    private void save_data() {
        JSONObject params = new JSONObject();
        try {
            params.put("asset_code", slctd_product_name);
            params.put("asset_id", slctd_product_name);
            params.put("user_id", Static_values.user_id);
            params.put("client_id", client_id);
            params.put("master_id", Static_values.selectedMasterData_model.getMdata_id());
            params.put("subasset_list", cmpnt_qty_lstary);

            Step_subitem_table step_subitemData_table = new Step_subitem_table();
            step_subitemData_table.setData(client_id, unique_id, params.toString());
            AppController.getInstance().getDatabase().getStep_subitem_Dao().insert(step_subitemData_table);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        LoadFragment.replace(ClientSignatureFragment.newInstance("Pdm_Sign"), getActivity(), getResString("lbl_client_remark"));
    }
}
