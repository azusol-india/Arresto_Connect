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

package app.com.arresto.arresto_connect.network;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.Periodic_model;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.database.pdm_tables.Asset_steps_table;
import app.com.arresto.arresto_connect.database.inspection_tables.Components_table;
import app.com.arresto.arresto_connect.database.Data_daowload;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressConstant;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressFlower;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.downloaded_sites;
import static app.com.arresto.arresto_connect.network.All_Api.get_pdm_steps;

public class Get_Save_componnent_subasset {
    private Activity mContext;
    private ACProgressFlower progressDialog;
    private Data_daowload data_daowload;
    private String unique_id;
    private String asset_name;

    private String dbasset, dbsubasset, dbactn_prpsd, json_asset, json_subasset, json_actn_prpsd;

    private String description, component_code, component_sub_assets, uom, inspection, result, observation, pass_imagepath, fail_imagepath, repair_imagepath;
    private String sub_assets_code, sub_description, sub_uom, sub_inspection, sub_result, sub_observation, sub_pass_imagepath, sub_fail_imagepath, sub_repair_imagepath;

    public Get_Save_componnent_subasset(Context context) {
        mContext = (Activity) context;
        if (mContext != null) {
            data_daowload = new Data_daowload(mContext);
            progressDialog = new ACProgressFlower.Builder(mContext)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.WHITE)
                    .text(getResString("lbl_wait_st")).textSize(16).textMarginTop(5)
                    .petalThickness(2)
                    .sizeRatio((float) 0.22)
                    .fadeColor(Color.YELLOW).build();
            progressDialog.setCancelable(false);
            data_daowload.open();
        }
    }


    public void checked_data(String dbasset, String dbsubasset, String dbacn_prpsd) {
        this.dbasset = dbasset;
        this.dbsubasset = dbsubasset;
        this.dbactn_prpsd = dbacn_prpsd;
    }

    public void getDataVolley(String unique_id, String totalasset, String totalsubasset, String totlacn_prpsd) {
        this.unique_id = unique_id;
        json_asset = totalasset;
        json_subasset = totalsubasset;
        json_actn_prpsd = totlacn_prpsd;
        progressDialog.show();
        get_all_assets();
    }

    public void download_pdm(String asset_name) {
        this.asset_name = asset_name;

        progressDialog.show();
        all_actionPropose_service("pdm");
    }

    private void get_all_assets() {
        if (!json_asset.equals(dbasset)) {
            data_daowload.deleteAll_component();
            Log.e("components_service_url", "  " + All_Api.components_service + "?client_id=" + client_id);
            new NetworkRequest(mContext).make_get_request(All_Api.components_service + "?client_id=" + client_id, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jresponse = jsonArray.getJSONObject(i);

                            component_code = jresponse.getString("component_code");
                            description = jresponse.getString("component_description");
                            component_sub_assets = jresponse.getString("component_sub_assets");
                            uom = jresponse.getString("component_uom");
                            inspection = jresponse.getString("component_inspectiontype");
                            result = jresponse.getString("component_expectedresult");
                            observation = jresponse.getString("component_observation");
                            pass_imagepath = jresponse.getString("component_pass_imagepath");
                            fail_imagepath = jresponse.getString("component_fail_imagepath");
                            repair_imagepath = jresponse.getString("component_repair_imagepath");

                            data_daowload.insert_component(component_code, description, component_sub_assets, uom, inspection, result, observation, pass_imagepath, fail_imagepath, repair_imagepath);
                        }

                        myService_sub_asset();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onError(String message) {

                }
            });

        } else {
            myService_sub_asset();
        }
    }

    private void myService_sub_asset() {

        if (!json_subasset.equals(dbsubasset)) {
            data_daowload.deleteAll_subaset();
            Log.e("subasset_service_url", "  " + All_Api.subasset_service + "?client_id=" + client_id);

            new NetworkRequest(mContext).make_get_request(All_Api.subasset_service + "?client_id=" + client_id, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jresponse = jsonArray.getJSONObject(i);
                            sub_assets_code = jresponse.getString("sub_assets_code");
                            sub_description = jresponse.getString("sub_assets_description");
                            sub_uom = jresponse.getString("sub_assets_u om");
                            sub_inspection = jresponse.getString("sub_assets_inspection");
                            sub_result = jresponse.getString("sub_assets_result");
                            sub_observation = jresponse.getString("sub_assets_observation");
                            sub_pass_imagepath = jresponse.getString("pass_imagepath");
                            sub_fail_imagepath = jresponse.getString("fail_imagepath");
                            sub_repair_imagepath = jresponse.getString("repair_imagepath");

                            data_daowload.insert_subasset(sub_assets_code, sub_description, sub_uom, sub_inspection, sub_result, sub_observation, sub_pass_imagepath, sub_fail_imagepath, sub_repair_imagepath);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    all_actionPropose_service("");
                }

                @Override
                public void onError(String message) {

                }
            });

        } else {
            all_actionPropose_service("");
        }
    }

    private void all_actionPropose_service(final String type) {
//        if (!json_actn_prpsd.equals(dbactn_prpsd)) {
        data_daowload.deleteAll_acprpos();
        String url = All_Api.all_actionPropose + "?client_id=" + client_id;
        new NetworkRequest(mContext).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    JSONArray data = object.getJSONArray("data");
                    for (int j = 0; j < data.length(); j++) {
                        JSONObject obsrv_id_obj = data.getJSONObject(j);
                        String observ_id = obsrv_id_obj.getString("id");
                        JSONArray jsonArray = obsrv_id_obj.getJSONArray("value");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(k);
                            String excerpt_id = jsonObject.getString("id");
                            String excerpt = jsonObject.getString("type_name");
                            data_daowload.insert_action_prpos(observ_id, excerpt_id, excerpt);
                        }
                    }
                    progressDialog.cancel();
                    if (type.equals("pdm")) {
                        download_periodic_steps(asset_name);
                    } else {

//                        List<String> downloaded__sites = mPrefrence.getArray_Data(DOWNLOADED_SITES);
//                        if (!downloaded__sites.contains(unique_id))
//                            downloaded__sites.add(unique_id);
//                        mPrefrence.saveArray_Data(DOWNLOADED_SITES, downloaded__sites);

                        if (!downloaded_sites.contains(unique_id))
                            downloaded_sites.add(unique_id);
                        data_daowload.close();
                    }
                    Log.e("data get and submitted", "   " + "completed");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {

            }
        });

    }

//    }

    private void download_periodic_steps(final String asset) {
        NetworkRequest network_request = new NetworkRequest(mContext);
        String url = get_pdm_steps + asset + "&client_id=" + client_id;
        network_request.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
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
                            AppUtils.show_snak(mContext, jsonObject.getString("message"));
                            return;
                        } else {
                            String data = jsonObject.getString("data");
                            ArrayList<Periodic_model> periodic_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data, Periodic_model[].class)));
                            Asset_steps_table asset_steps_table = new Asset_steps_table();
                            asset_steps_table.setAsset_id(asset);
                            asset_steps_table.setClient_id(client_id);
                            asset_steps_table.setSteps_data(new Gson().toJson(periodic_models));
                            AppController.getInstance().getDatabase().getAsset_stepsDao().insert(asset_steps_table);

                            download_component(asset);
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


    private void download_component(final String asset) {


        new NetworkRequest(mContext).make_get_request(All_Api.components_service + asset + "?client_id=" + client_id, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {

                            Component_model component_model = AppUtils.getGson().fromJson(jsonObject.getJSONArray("data").getString(0), Component_model.class);
                            Components_table components_table = new Components_table();
                            components_table.setData(client_id, asset, new Gson().toJson(component_model));
                            AppController.getInstance().getDatabase().getComponents_Dao().insert(components_table);

                        } else {
                            AppUtils.show_snak(mContext, "" + jsonObject.getString("message"));
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
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


}



