/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.network;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.pdm_signature_upload;
import static app.com.arresto.arresto_connect.network.All_Api.post_asset_count;
import static app.com.arresto.arresto_connect.network.All_Api.post_pdm_step;
import static app.com.arresto.arresto_connect.services.AlarmSetter.scheduleServiceUpdates;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.acra.ACRA;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.inspection_tables.Sites_data_table;
import app.com.arresto.arresto_connect.database.pdm_tables.Pdm_Inspected_steps_table;
import app.com.arresto.arresto_connect.database.pdm_tables.Signature_table;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Close_projectFragment;

/**
 * Created by AZUSOL-PC-02 on 4/29/2019.
 */
public class Upload_Pdm_Steps {

    private Activity mContext;
    private int submit_count = 1;
    private String inspection_id = "";

    public Upload_Pdm_Steps(Activity context) {
        this.mContext = context;
        progressDialog = new ProgressDialog(mContext);
        progressDialog.setTitle("uploading data...");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setMax(100);
    }

    private Handler main_handler, data_handler;
    private Message mesg;
    long start_time = 0, end_time = 0;
    int totalApiCount, progress_chunk = 5, progress = 0;
    public static ProgressDialog progressDialog;

    @SuppressLint("HandlerLeak")
    public void upload_steps(final String unique_id, final Handler handler) {
        final List<Pdm_Inspected_steps_table> data = AppController.getInstance().getDatabase().getPdm_stepsDao().getInspectedSteps(unique_id);
        this.main_handler = handler;
        totalApiCount = 3 + data.size();
        progress_chunk = 100 / totalApiCount;
        submit_count = 0;
        mesg = new Message();
        data_handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.obj.toString().equals("true")) {
                    progress = progress + progress_chunk;
                    progressDialog.setProgress(progress);
                    if (submit_count < data.size() - 1) {
                        submit_count = submit_count + 1;
                        send_data(data.get(submit_count), data_handler);
                    } else {
                        post_subasset_data(unique_id);
                    }
                } else {
                    progressDialog.cancel();
                    mesg.obj = "";
                    mesg.what = 0;
                    handler.sendMessage(mesg);
                }
            }
        };
        if (data.size() > 0) {
            progressDialog.show();
            send_data(data.get(0), data_handler);
        }
    }


    private void send_data(Pdm_Inspected_steps_table pdm_steps_table, final Handler handler) {
        if (pdm_steps_table.getStart_time() != 0) {
            start_time = pdm_steps_table.getStart_time();
        }
        final Message mesg = new Message();
        try {
            JSONObject params = new JSONObject(new Gson().toJson(pdm_steps_table));
            JSONArray before_imge_data = new JSONArray();
            JSONArray after_imge_data = new JSONArray();
            TypeToken<List<String>> token = new TypeToken<List<String>>() {
            };
            ArrayList<String> befor_img_arr = new Gson().fromJson(pdm_steps_table.getBefore_images(), token.getType());
            ArrayList<String> after_img_arr = new Gson().fromJson(pdm_steps_table.getAfter_images(), token.getType());
            for (String fileName : befor_img_arr) {
                before_imge_data.put("data:image/jpg;base64," + AppUtils.Image_toBase64(fileName));
            }
            for (String fileName : after_img_arr) {
                after_imge_data.put("data:image/jpg;base64," + AppUtils.Image_toBase64(fileName));
            }
            params.put("before_images", before_imge_data);
            params.put("after_images", after_imge_data);

            params.put("observation", new JSONArray(pdm_steps_table.getObservation()));
            params.put("action_taken", new JSONArray(pdm_steps_table.getAction_taken()));
            params.put("action_proposed", new JSONArray(pdm_steps_table.getAction_proposed()));

            if (!inspection_id.equals(""))
                params.put("inspection_id", inspection_id);

//                Log.e("params pdm", " is  "+params);
            new NetworkRequest().make_contentpost_request(post_pdm_step, params, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    try {
                        Object json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            String msg_code = jsonObject.getString("msg_code");
                            if (msg_code.equals("200")) {
                                inspection_id = jsonObject.getString("inspection_id");
//                                show_snak(mContext, jsonObject.getString("msg"));
//                                if (submit_count == data.size()) {
                                mesg.obj = "true";
                                handler.sendMessage(mesg);
//                                } else {
//                                    submit_count++;
//                                }
                            } else {
                                mesg.obj = "false";
                                handler.sendMessage(mesg);
                                show_snak(mContext, jsonObject.getString("msg"));
                            }
                        }
                    } catch (JSONException e) {
                        progressDialog.cancel();
                        e.printStackTrace();
                        Log.e("JSONException", "" + e.getMessage());
                        ACRA.getErrorReporter().handleSilentException(e);
                    }
                }

                @Override
                public void onError(String message) {
                    progressDialog.cancel();
                    Log.e("error", "" + message);
                }
            });
        } catch (JSONException e) {
            progressDialog.cancel();
            e.printStackTrace();
        }
    }

    private void post_subasset_data(final String unique_id) {
        JSONObject params = null;
        try {
            params = new JSONObject(AppController.getInstance().getDatabase().getStep_subitem_Dao().getStep_subitemData(unique_id, client_id));
            if (!inspection_id.equals(""))
                params.put("inspection_id", inspection_id);

        } catch (JSONException e1) {
            e1.printStackTrace();
        }
//        Log.e("params", "" + params);
        new NetworkRequest().make_contentpost_request(post_asset_count, params, new NetworkRequest.VolleyResponseListener() {
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
                            progress = progress + progress_chunk;
                            progressDialog.setProgress(progress);
//                            AppUtils.show_snak(mContext, jsonObject.getString("message"));
                            post_signature_data(unique_id, "client");
                        } else {
                            progressDialog.cancel();
                            AppUtils.show_snak(mContext, jsonObject.getString("message"));
                        }
                    }
                } catch (JSONException e) {
                    progressDialog.cancel();
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());

                    ACRA.getErrorReporter().handleSilentException(e);
                }
            }

            @Override
            public void onError(String message) {
                progressDialog.cancel();
                Log.e("error", "" + message);
            }
        });
    }


    private void post_signature_data(final String unique_id, final String type) {
        JSONObject params = new JSONObject();
        try {
            Signature_table signature_table = AppController.getInstance().getDatabase().getSignature_Dao().getSignature(client_id, unique_id, "pdm", type);
            params = new JSONObject(signature_table.getSignature_data());
            params.put("inspection_id", inspection_id);
//            params.put("signature_of", "technician");
            if (type.equals("technician")) {
                long total_duration;
                if (signature_table.getEnd_time() > 0) {
                    total_duration = (signature_table.getEnd_time() - start_time) / 1000;
                } else
                    total_duration = (System.currentTimeMillis() - start_time) / 1000;
                params.put("time_taken", total_duration);
            }
            String image_path = params.getString("signature_image");
            String extension = MimeTypeMap.getFileExtensionFromUrl(params.getString("signature_image"));
            String filetype = null;
            if (extension != null) {
                filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            }
            params.put("signature_image", "data:" + filetype + ";base64," + AppUtils.Image_toBase64(image_path));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkRequest().make_contentpost_request(pdm_signature_upload, params, new NetworkRequest.VolleyResponseListener() {
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
                            if (type.equals("technician")) {
                                progress = 100;
                                progressDialog.setProgress(progress);
                                show_snak(mContext, jsonObject.getString("message"));
                                Sites_data_table.Sites_data_Dao sites_data_dao = AppController.getInstance().getDatabase().getSites_data_Dao();
                                Sites_data_table siteTbl = sites_data_dao.getSingleSites(user_id, unique_id);
                                siteTbl.setIsUploaded("yes");
                                sites_data_dao.updateSite(siteTbl);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        scheduleServiceUpdates(mContext);
                                        progressDialog.cancel();
                                        go_report_list();
                                    }
                                }, 500);
                            } else {
                                progress = progress + progress_chunk;
                                progressDialog.setProgress(progress);// time in seconds
                                post_signature_data(unique_id, "technician");
                            }
                        } else
                            progressDialog.cancel();
                    }
                } catch (JSONException e) {
                    progressDialog.cancel();
                    ACRA.getErrorReporter().handleSilentException(e);
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                progressDialog.cancel();
                Log.e("error", "" + message);
            }
        });
    }

    private void go_report_list() {
        mesg.obj = inspection_id;
        mesg.what = 1;
        main_handler.sendMessage(mesg);
//        HomeActivity.homeActivity.submit_action("pdm_report");
        Bundle bndl = new Bundle();
        bndl.putString("id", "pdm_report");
        bndl.putInt("index", 0);
        Static_values.currently_inspected = true;
        bndl.putString("pdm_url", All_Api.post_pdm_ins_list + client_id + "&inspector_id=" + user_id + "&cgrp_id=" + role_id);
        Close_projectFragment close_projectFragment = new Close_projectFragment();
        close_projectFragment.setArguments(bndl);
        HomeActivity.homeActivity.clear_all_fragment();
        LoadFragment.replace(close_projectFragment, HomeActivity.homeActivity, getResString("lbl_maintenance_report"));
    }
}
