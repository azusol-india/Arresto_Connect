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

import static app.com.arresto.Flavor_Id.FlavourInfo.Fl_CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.services.AlarmSetter.scheduleServiceUpdates;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.AppDatabase;
import app.com.arresto.arresto_connect.database.inspection_tables.InspectionSignature_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Detail_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Inspection_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.Sites_data_table;
import app.com.arresto.arresto_connect.database.inspection_tables.ThermalAsset_Table;
import app.com.arresto.arresto_connect.database.inspection_tables.WorkPermitTable;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Close_projectFragment;

public class Upload_site_data {
    public static ProgressDialog progressDialog;
    private Activity mContext;
    private ArrayList<String> allfile_path, array_for_logs;
    private String worpermit_id, returnid, thermal_id, unique_id, siteName;

    //    ObjectMapper oMapper;
    public Upload_site_data(Activity context) {
        this.mContext = context;
//         oMapper = new ObjectMapper();
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("uploading data...");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
        allfile_path = new ArrayList<>();
        array_for_logs = new ArrayList<>();
        Db = AppController.getInstance().getDatabase();
    }

    WorkPermitTable permitData;
    Inspection_Detail_Table detail_table;
    List<Inspection_Table> allInspected_asset;
    List<ThermalAsset_Table> allThermal_asset;
    InspectionSignature_Table signatureTable, client_signatureTable;
    int totalApiCount, progress_chunk = 5, progress = 0;
    AppDatabase Db;

    public void startUpload(String uniqu_id) {
        progressDialog.show();
        this.unique_id = uniqu_id;
        permitData = Db.getWorkPermit_Dao().getWorkPermit_data(user_id, client_id, unique_id);
        detail_table = Db.getInspection_Detail_Dao().getInspection_Detail(user_id, unique_id);
        allInspected_asset = Db.getInspection_Asset_dao().getAllInspected_Asset(user_id, unique_id);
        allThermal_asset = Db.getThermalAsset_dao().getAllThermal_Asset(user_id, unique_id);
        signatureTable = Db.getInspectionSignature_Dao().getInspectionSignature(user_id, unique_id, 1);
        client_signatureTable = Db.getInspectionSignature_Dao().getInspectionSignature(user_id, unique_id, 0);

        if (permitData == null) {
            totalApiCount = 3 + allInspected_asset.size();
            progress_chunk = 100 / totalApiCount;
            worpermit_id = "";
            send_frg_data();
        } else {
            totalApiCount = 4 + allInspected_asset.size();
            progress_chunk = 100 / totalApiCount;
            makeJsonRequest();
        }
        if (allThermal_asset != null) {
            totalApiCount = totalApiCount + allThermal_asset.size();
        }
    }

    private void makeJsonRequest() {
        Map<String, String> params = toParam(permitData);
        Log.e("prams weekly ", " is " + params);

        HashMap<String, File> files = new HashMap<>();
        if (permitData.getImage_path() != null && !permitData.getImage_path().equals("")) {
            File file = new File(permitData.getImage_path());
            if (file.exists())
                files.put("inspector_image", file);
        }
        AndroidNetworking.upload(All_Api.worrkprmit_service)
                .addMultipartParameter(params)
                .addMultipartFile(files)
                .setPriority(Priority.HIGH)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response workPermit", " is  " + response);
                        Object json;
                        try {
                            json = new JSONTokener(response).nextValue();
                            if (json instanceof JSONObject) {
                                JSONObject jsonObject = new JSONObject(response);
                                worpermit_id = jsonObject.getString("workPermitID");
                                progress = progress + progress_chunk;
                                progressDialog.setProgress(progress);
                                send_frg_data();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error0", "" + anError);
                        Log.e("error1", "" + anError.getMessage());
                        Log.e("error2", "" + anError.getErrorCode());
                        Log.e("error3", "" + anError.getErrorDetail());
                        Log.e("error4", "" + anError.getErrorBody());
                        progressDialog.cancel();
                        ACRA.getErrorReporter().handleSilentException(anError);
                    }
                });

    }

    private void send_frg_data() {
        Map<String, String> params = toParam(detail_table);
        params.put("workPermitID", worpermit_id);
        Log.e("prams frag ", " is " + params);
        AndroidNetworking.post(All_Api.inspection_frag_send)
                .addBodyParameter(params)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", "" + response);
                        try {
                            returnid = String.valueOf(response.getInt("success"));
                            if (response.has("unique_id"))
                                thermal_id = response.getString("unique_id");
                            Log.e("returnid string", " returnid " + returnid);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        progress = progress + progress_chunk;
                        progressDialog.setProgress(progress);
                        upload_Assets();
//                      send_asset_data(allInspected_asset.get(submit_count), data_handler);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error5", "" + anError);
                        Log.e("error6", "" + anError.getMessage());
                        Log.e("error7", "" + anError.getErrorCode());
                        Log.e("error8", "" + anError.getErrorDetail());
                        Log.e("error9", "" + anError.getErrorBody());
                        progressDialog.cancel();
                        ACRA.getErrorReporter().handleSilentException(anError);
                    }
                });
    }

    int submit_count;
    Handler data_handler;

    @SuppressLint("HandlerLeak")
    public void upload_Assets() {
        submit_count = 0;
        data_handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj.toString().equals("true")) {
                    progress = progress + progress_chunk;
                    progressDialog.setProgress(progress);
                    if (submit_count < allInspected_asset.size() - 1) {
                        submit_count = submit_count + 1;
                        send_asset_data(allInspected_asset.get(submit_count), data_handler);
                    } else {
                        if (allThermal_asset != null && allThermal_asset.size() > 0) {
                            send_thermal_data();
                        } else
                            send_inspectr_data();
                    }
                }
            }
        };
        if (allInspected_asset.size() > 0) {
            send_asset_data(allInspected_asset.get(0), data_handler);
        }
    }


    private void send_asset_data(Inspection_Table inspected_asset, Handler handler) {
        Message mesg = new Message();
        try {
            JSONObject params = new JSONObject(new Gson().toJson(inspected_asset));
            JSONArray before_imge_data = new JSONArray();
            JSONArray after_imge_data = new JSONArray();
            TypeToken<List<String>> token = new TypeToken<List<String>>() {
            };
            ArrayList<String> befor_img_arr = new Gson().fromJson(inspected_asset.getBefore_images(), token.getType());
            ArrayList<String> after_img_arr = new Gson().fromJson(inspected_asset.getAfter_images(), token.getType());
            if (befor_img_arr != null)
                for (String fileName : befor_img_arr) {
                    before_imge_data.put("data:image/jpg;base64," + AppUtils.Image_toBase64(fileName));
                }
            if (after_img_arr != null)
                for (String fileName : after_img_arr) {
                    after_imge_data.put("data:image/jpg;base64," + AppUtils.Image_toBase64(fileName));
                }
            params.put("before_images", before_imge_data);
            params.put("after_images", after_imge_data);
            if (!returnid.equals(""))
                params.put("returnID", returnid);

            Log.e("prams asset ", " is " + params);
//                array_for_logs.add("" + (k + 1) + "- Assets:- " + a_asset.get(k) + " ,Subasset:- " + a_sub_asset.get(k));

            String url;
            if (client_id.equals("419") || Fl_CLIENT_ID.equals("419"))
                url = All_Api.abinspectionForm_service_v2;
            else url = All_Api.abinspectionForm_service1;
            AndroidNetworking.post(url)
                    .addJSONObjectBody(params)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("response ", "  " + response);
                            Log.e("asset_count string", " asset_count " + submit_count);

                            try {
                                if (response.has("success") && response.getBoolean("success"))
                                    mesg.obj = "true";
                                else mesg.obj = "false";
                                handler.sendMessage(mesg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("error10", "" + anError);
                            Log.e("error11", "" + anError.getMessage());
                            Log.e("error12", "" + anError.getErrorCode());
                            Log.e("error13", "" + anError.getErrorDetail());
                            Log.e("error14", "" + anError.getErrorBody());
                            mesg.obj = "false";
                            handler.sendMessage(mesg);
                            progressDialog.cancel();
                            ACRA.getErrorReporter().handleSilentException(anError);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            ACRA.getErrorReporter().handleSilentException(e);
        }
    }


    private void send_thermal_data() {
        submit_count = 0;
        data_handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj.toString().equals("true")) {
                    progress = progress + progress_chunk;
                    progressDialog.setProgress(progress);
                    if (submit_count < allThermal_asset.size() - 1) {
                        submit_count = submit_count + 1;
                        post_thermal_data(allThermal_asset.get(submit_count), data_handler);
                    } else {
                        send_inspectr_data();
                    }
                }
            }
        };
        if (allThermal_asset.size() > 0) {
            post_thermal_data(allThermal_asset.get(0), data_handler);
        }

    }

    private void post_thermal_data(ThermalAsset_Table thermalAsset_table, Handler handler) {
        Message mesg = new Message();
        try {
            JSONObject params = new JSONObject(thermalAsset_table.getJsonData());
            params.put("actual_image", "data:image/jpg;base64," + AppUtils.Image_toBase64(params.getString("actual_image")));
            params.put("thermal_image", "data:image/jpg;base64," + AppUtils.Image_toBase64(params.getString("thermal_image")));
            params.put("marked_image", "data:image/jpg;base64," + AppUtils.Image_toBase64(params.getString("marked_image")));
            params.put("scale_image", "data:image/jpg;base64," + AppUtils.Image_toBase64(params.getString("scale_image")));
            if (!returnid.equals(""))
                params.put("return_id", returnid);
            if (!thermal_id.equals(""))
                params.put("unique_id", thermal_id);

            Log.e("thermal asset ", " is " + params);
            AndroidNetworking.post(All_Api.postThermal_service)
                    .addJSONObjectBody(params)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.e("response ", "  " + response);
                            Log.e("asset_count string", " asset_count " + submit_count);
                            try {
                                String msg_code = response.getString("status_code");
                                if (msg_code.equals("200"))
                                    mesg.obj = "true";
                                else mesg.obj = "false";
                                handler.sendMessage(mesg);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                mesg.obj = "false";
                                handler.sendMessage(mesg);
                                progressDialog.cancel();
                                ACRA.getErrorReporter().handleSilentException(e);
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("error10", "" + anError);
                            Log.e("error11", "" + anError.getMessage());
                            Log.e("error12", "" + anError.getErrorCode());
                            Log.e("error13", "" + anError.getErrorDetail());
                            Log.e("error14", "" + anError.getErrorBody());
                            mesg.obj = "false";
                            handler.sendMessage(mesg);
                            progressDialog.cancel();
                            ACRA.getErrorReporter().handleSilentException(anError);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            ACRA.getErrorReporter().handleSilentException(e);
        }
    }

    private void send_inspectr_data() {
        Map<String, String> params = toParam(signatureTable);
        params.put("returnID", returnid);
        HashMap<String, File> files = new HashMap<>();
        if (signatureTable.getImagePath() != null && !signatureTable.getImagePath().equals("")) {
            File imgFile = new File(signatureTable.getImagePath());
            if (imgFile.exists())
                files.put("signImage", imgFile);
        }
        AndroidNetworking.upload(All_Api.inspectorInformation_send)
                .addMultipartFile(files)
                .addMultipartParameter(params)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", " karam " + response);
                        progress = progress + progress_chunk;
                        progressDialog.setProgress(progress);
                        send_client_data();
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("error", "test aaaa=== " + error.getMessage());
                        Log.e("error123  ", "test === " + error.getErrorBody());
                        progressDialog.cancel();
                        ACRA.getErrorReporter().handleSilentException(error);
                    }
                });
    }

    private void send_client_data() {
        Map<String, String> params = toParam(client_signatureTable);
        params.put("returnID", returnid);
        HashMap<String, File> files = new HashMap<>();
        if (client_signatureTable.getImagePath() != null && !client_signatureTable.getImagePath().equals("")) {
            File imgFile = new File(client_signatureTable.getImagePath());
            if (imgFile.exists())
                files.put("signImage", imgFile);
        }
        Log.e("prams client ", " is " + params);
        AndroidNetworking.upload(All_Api.clientInformation_send)
                .addMultipartFile(files)
                .addMultipartParameter(params)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("response", " client " + response);
                        Toast.makeText(mContext, "Site data Uploaded successfully.", Toast.LENGTH_LONG).show();
                        progressDialog.setProgress(100);
                        NetworkRequest networkRequest = new NetworkRequest(mContext);
                        networkRequest.save_logs((All_Api.logs_api + Static_values.user_id + "&eventType=Upload Site " + siteName + " With components " +
                                array_for_logs));

//                        delete_uploadedsite_data();
                        Sites_data_table.Sites_data_Dao sites_data_dao = Db.getSites_data_Dao();
                        Sites_data_table siteTbl = sites_data_dao.getSingleSites(user_id, unique_id);
                        siteTbl.setIsUploaded("yes");
                        sites_data_dao.updateSite(siteTbl);
                        scheduleServiceUpdates(mContext);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                                go_main_list();
                            }
                        }, 500);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error10", "" + anError);
                        Log.e("error11", "" + anError.getMessage());
                        Log.e("error12", "" + anError.getErrorCode());
                        Log.e("error13", "" + anError.getErrorDetail());
                        Log.e("error14", "" + anError.getErrorBody());
                        progressDialog.cancel();
                        ACRA.getErrorReporter().handleSilentException(anError);
                    }
                });
    }

    private void go_main_list() {
//        HomeActivity.homeActivity.submit_action("ins_report");
        Bundle bndl = new Bundle();
        bndl.putString("id", "report");
        bndl.putInt("index", 0);
        Static_values.currently_inspected = true;
        Close_projectFragment close_projectFragment1 = new Close_projectFragment();
        close_projectFragment1.setArguments(bndl);
        HomeActivity.homeActivity.clear_all_fragment();
        LoadFragment.replace(close_projectFragment1, HomeActivity.homeActivity, getResString("lbl_ins_histry_st"));
    }

    public static Map<String, String> toParam(Object obj) {
        HashMap<String, String> map = new HashMap<>();
        for (Field field : obj.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                map.put(field.getName(), field.get(obj).toString());
            } catch (Exception e) {
            }
        }
        return map;
    }
}
