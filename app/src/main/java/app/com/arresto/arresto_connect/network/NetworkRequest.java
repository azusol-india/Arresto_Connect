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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.DownloadListener;
import com.androidnetworking.interfaces.DownloadProgressListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.androidnetworking.interfaces.UploadProgressListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.acra.ACRA;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Component_history_Model;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.EC_Project;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.database.Language_table;
import app.com.arresto.arresto_connect.database.inspection_tables.Master_data_table;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressConstant;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressFlower;
import okhttp3.OkHttpClient;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

//import com.facebook.stetho.okhttp3.StethoInterceptor;

public class NetworkRequest {
    private Activity mContext;
    private ACProgressFlower progressDialog;
    //    private ProgressDialog progressDialog;
    OkHttpClient client;

    public NetworkRequest(Activity context) {
        mContext = context;
        if (context != null) {
//            progressDialog = new ProgressDialog(context);
//            progressDialog.setMessage(getResString("lbl_wait_st"));
            progressDialog = new ACProgressFlower.Builder(mContext)
                    .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                    .themeColor(Color.YELLOW)
//                .bgColor(Color.BLACK)
                    .text(getResString("lbl_wait_st")).textSize(16).textMarginTop(5)
                    .petalThickness(2)
                    .sizeRatio((float) 0.22)
                    .fadeColor(Color.WHITE).build();
            progressDialog.setCancelable(false);
            client = new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.MINUTES)
                    .readTimeout(2, TimeUnit.MINUTES)
                    .writeTimeout(2, TimeUnit.MINUTES)
                    .hostnameVerifier(new AllowAllHostnameVerifier())
//                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();
        }
    }

    public NetworkRequest() {
        client = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.MINUTES)
                .readTimeout(2, TimeUnit.MINUTES)
                .writeTimeout(2, TimeUnit.MINUTES)
                .hostnameVerifier(new AllowAllHostnameVerifier())
//                .addNetworkInterceptor(new StethoInterceptor())
                .build();
    }

    public void make_get_request(String url, final VolleyResponseListener listener) {
        show_dialog();
//        if (!url.contains("user_id") && user_id != null && !user_id.equals("")) {
        if (url.contains("?"))
            url = url + "&login_id=" + user_id;
        else
            url = url + "?login_id=" + user_id;
//        }
        url=url.replace(" ","%20");
        Log.e("get url ", " is " + url);
        AndroidNetworking.get(url)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(client)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        hide_dialog();
                        listener.onResponse(response);
                    }

                    @Override
                    public void onError(ANError error) {
                        Log.e("get error 1 ", " is " + error.getErrorDetail());
                        Log.e("get error 2 ", " is " + error.getErrorBody());
                        Log.e("get error 3", " is " + error.getErrorCode());
                        listener.onError(error.toString());
                        hide_dialog();
                    }
                });
    }


    public void upload_param_file(String url, final HashMap<String, String> params,
                                  final HashMap<String, File> files, final VolleyResponseListener listener) {
        if (!AppUtils.isNetworkAvailable(mContext)) {
            AppUtils.show_snak(mContext, AppUtils.getResString("lbl_network_alert"));
        } else {
            show_dialog();
//            params.put("flag","development");
            Log.e("file ", " is " + files.toString());
            Log.e("params ", " is " + params.toString());

            AndroidNetworking.upload(url)
                    .addMultipartParameter(params)
                    .addMultipartFile(files)
                    .setOkHttpClient(client)
                    .setPriority(Priority.HIGH)
                    .build()
                    .setUploadProgressListener(new UploadProgressListener() {
                        public void onProgress(long bytesUploaded, long totalBytes) {
                            // do anything with progress
                            Log.e("bytesUploaded ", " is " + bytesUploaded);
                            Log.e("totalBytes ", " is " + totalBytes);

//                    byte[] bytes = params.toString().getBytes();
//                    LongBuffer tmpBuf = ByteBuffer.wrap(bytes).asLongBuffer();
//
//                    long[] lArr = new long[tmpBuf.remaining()];
//                    for (int i = 0; i < lArr.length; i++)
//                        lArr[i] = tmpBuf.get();
//
//                    Log.e("total bytes before 122 "," is "+Arrays.toString(lArr).length());

                        }
                    })
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            listener.onResponse(response);
                            hide_dialog();
                        }

                        @Override
                        public void onError(ANError error) {
                            listener.onError(error.toString());
                            hide_dialog();
                        }
                    });
        }
    }

    public void make_post_request(String url, final HashMap<String, String> params, final VolleyResponseListener listener) {
//        params.put("flag","development");

//        if (!params.containsKey("user_id") && user_id != null && !user_id.equals(""))
        params.put("login_id", user_id);
        if (mContext != null && !AppUtils.isNetworkAvailable(mContext)) {
            AppUtils.show_snak(mContext, AppUtils.getResString("lbl_network_alert"));
        } else {
            Log.e("params", "  =   " + params);
            show_dialog();
            AndroidNetworking.post(url)
                    .addBodyParameter(params)
                    .setOkHttpClient(client)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            listener.onResponse(response);
                            hide_dialog();
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("error 0 ", " is " + error.getMessage());
                            Log.e("error 1 ", " is " + error.getErrorDetail());
                            Log.e("error 2 ", " is " + error.getErrorBody());
                            Log.e("error 3", " is " + error.getErrorCode());
                            listener.onError(error.toString());
                            hide_dialog();
                        }
                    });
        }
    }

    public void make_contentpost_request(String url, final JSONObject params, final VolleyResponseListener listener) {

        try {
//            if (!params.has("user_id") && user_id != null && !user_id.equals(""))
            params.put("login_id", user_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (mContext != null && !AppUtils.isNetworkAvailable(mContext)) {
            AppUtils.show_snak(mContext, AppUtils.getResString("lbl_network_alert"));
        } else {
            show_dialog();
            AndroidNetworking.post(url)
                    .addJSONObjectBody(params)
                    .setPriority(Priority.HIGH)
                    .setOkHttpClient(client)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            listener.onResponse(response);
                            hide_dialog();
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("error 0 ", " is " + error.getMessage());
                            Log.e("error 1 ", " is " + error.getErrorDetail());
                            Log.e("error 2 ", " is " + error.getErrorBody());
                            Log.e("error 3", " is " + error.getErrorCode());
                            listener.onError(error.toString());
                            hide_dialog();
                            ACRA.getErrorReporter().handleSilentException(error);
                        }
                    });
        }
    }

    public void make_Arraypost_request(String url, final JSONArray params,
                                       final VolleyResponseListener listener) {
        if (!AppUtils.isNetworkAvailable(mContext)) {
            AppUtils.show_snak(mContext, AppUtils.getResString("lbl_network_alert"));
        } else {
            show_dialog();
            AndroidNetworking.post(url)
                    .addJSONArrayBody(params)
                    .setPriority(Priority.MEDIUM)
                    .setOkHttpClient(client)
                    .build()
                    .getAsString(new StringRequestListener() {
                        @Override
                        public void onResponse(String response) {
                            listener.onResponse(response);
                            hide_dialog();
                        }

                        @Override
                        public void onError(ANError error) {
                            Log.e("error 0 ", " is " + error.getMessage());
                            Log.e("error 1 ", " is " + error.getErrorDetail());
                            Log.e("error 2 ", " is " + error.getErrorBody());
                            Log.e("error 3", " is " + error.getErrorCode());
                            listener.onError(error.toString());
                            hide_dialog();
                        }
                    });
        }
    }

    public void add_to_schedule(String url, HashMap<String, String> prams,
                                final Handler handler) {
        Log.e("url ", " is " + url);
        Log.e("prams ", " is " + prams);
        final Message mesg = new Message();
        prams.put("client_id", client_id);
        make_post_request(url, prams, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status_code;
                    if (jsonObject.has("status_code"))
                        status_code = jsonObject.getString("status_code");
                    else
                        status_code = jsonObject.getString("msg_code");
                    String msg;
                    if (jsonObject.has("message"))
                        msg = jsonObject.getString("message");
                    else
                        msg = jsonObject.getString("msg");
                    AppUtils.show_snak(mContext, msg);
                    mesg.obj = status_code;
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.obj = 404;
                    Log.e("JSONException", "" + e.getMessage());
                }
                handler.sendMessage(mesg);
            }

            @Override
            public void onError(String message) {
                mesg.obj = 404;
                handler.sendMessage(mesg);
                Log.e("onError ", " Register_request " + message);
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public void save_logs(final String url) {
        show_dialog();
//        url=url+"&flag=development";
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.e("get url ", " is " + url);
                AndroidNetworking.get(url.replaceAll(" ", "%20") + "&client_id=" + client_id)
                        .setPriority(Priority.HIGH)
                        .setOkHttpClient(client)
                        .build()
                        .getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                hide_dialog();
                            }

                            @Override
                            public void onError(ANError error) {
                                hide_dialog();
                            }
                        });
                return null;
            }

        }.execute();

    }

    public static void get_projects_data(final Activity activity, String url,
                                         final boolean is_EC, final Handler handler) {
//      url=url+"&flag=development";
        Log.e("url ", " is  " + url);
        final Message mesg = new Message();
        NetworkRequest network_request = new NetworkRequest(activity);
        network_request.make_get_request(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response 1", "   " + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String status_code = jsonObject.getString("status_code");
                        if (status_code.equals("200")) {
                            String data = jsonObject.getString("data");
                            if (is_EC)
                                DataHolder_Model.getInstance().setEc_project(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data, EC_Project[].class))));
                            else
                                DataHolder_Model.getInstance().setProject_models(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data, Project_Model[].class))));
                        } else {
                            AppUtils.show_snak(activity, "" + jsonObject.getString("message"));
                        }
                        mesg.obj = status_code;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.obj = 404;
                    Log.e("JSONException", "" + e.getMessage());
                }
                handler.sendMessage(mesg);
            }

            @Override
            public void onError(String message) {
                mesg.obj = 404;
                handler.sendMessage(mesg);
                Log.e("message 1", "   " + message);
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void getMasterData(String m_id,   String user_id, ObjectListener listener) {
        String url = All_Api.inspection_service + "userID=" + user_id + "&userGroupID=" + Static_values.group_id + "&mdata_id=" + m_id + "&client_id=" + client_id + "&datetime=" + System.currentTimeMillis();
        NetworkRequest networkRequest = new NetworkRequest(mContext);
        networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONArray) {
                        JSONArray jsonArray = new JSONArray(response);
                        try {
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            MasterData_model masterData_model = new Gson().fromJson(jsonObject.toString(), MasterData_model.class);
                             if(listener!=null)
                            listener.onResponse(masterData_model);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (JSONException ignored) {
                    Log.e("JSONException", "" + ignored.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                if(listener!=null)
                    listener.onError(message);
                Log.e("error", "" + message);
            }
        });
    }

    public static void getComponents(final Activity activity, String url, final Handler handler) {
//       url=url+"&flag=development";
        Log.e("url ", " is  " + url);
        final Message mesg = new Message();
        new NetworkRequest(activity).make_get_request(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
                            DataHolder_Model.getInstance().setComponent_models(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Component_model[].class))));
                        } else {
                            AppUtils.show_snak(activity, "" + jsonObject.getString("message"));
                        }
                        mesg.obj = msg_code;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.obj = 404;
                    Log.e("JSONException", "" + e.getMessage());
                }
                handler.sendMessage(mesg);
            }

            @Override
            public void onError(String message) {
                mesg.obj = 404;
                handler.sendMessage(mesg);
                Log.e("message 1", "   " + message);
            }
        });
    }

    public static void getProduct(final Activity activity, String url, final Handler handler) {
//       url=url+"&flag=development";
        Log.e("url ", " is  " + url);
        final Message mesg = new Message();
        new NetworkRequest(activity).make_get_request(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONArray) {
                        JSONArray array = new JSONArray(response);
                        if (array.getJSONObject(0) instanceof JSONObject) {
                            DataHolder_Model.getInstance().setProduct_models(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(response, Product_model[].class))));
                            mesg.obj = 200;
                        } else {
                            AppUtils.show_snak(activity, "Product Not Found");
                            mesg.obj = 404;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.obj = 404;
                    Log.e("JSONException", "" + e.getMessage());
                }
                handler.sendMessage(mesg);
            }

            @Override
            public void onError(String message) {
                mesg.obj = 404;
                handler.sendMessage(mesg);
                Log.e("message 1", "   " + message);
            }
        });
    }

    public static void getCustom_Data(final Activity activity, String url, final String tag, final Handler handler) {
        Log.e("url ", " is  " + url);
        final Message mesg = new Message();
        new NetworkRequest(activity).make_get_request(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        Log.e("response", "   " + response);
                        if (msg_code.equals("200")) {
                            GsonBuilder b = new GsonBuilder();
                            if (tag.equals("forms")) {
//                                b.registerTypeAdapter(CustomForm_Model.Field_set.class, new CustomForm_Model.Custom_Form_Deserial());
//                                DataHolder_Model.getInstance().setCustomViews_models(new ArrayList<>(Arrays.asList(b.create().fromJson(jsonObject.getString("data"), CustomForm_Model[].class))));
                                DataHolder_Model.getInstance().setCustomViews_models(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), CustomForm_Model[].class))));
                            } else {
                                b.registerTypeAdapter(CustomForm_Model.FieldData.class, new CustomForm_Model.Custom_Form_Deserial());
                                DataHolder_Model.getInstance().setCustomViewsData_models(new ArrayList<>(Arrays.asList(b.create().fromJson(jsonObject.getJSONObject("data").getString("post_forms"), CustomForm_Model[].class))));
                            }
                        } else {
                            AppUtils.show_snak(activity, "" + jsonObject.getString("message"));
                        }
                        mesg.obj = msg_code;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.obj = 404;
                    Log.e("JSONException", "" + e.getMessage());
                }
                handler.sendMessage(mesg);
            }

            @Override
            public void onError(String message) {
                mesg.obj = 404;
                handler.sendMessage(mesg);
                Log.e("message 1", "   " + message);
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public static void get_AssetHistory(final Activity activity, String url,
                                        final Handler handler) {

//    url=url+"&flag=development";
        Log.e("url ", " is  " + url);
        final Message mesg = new Message();
        NetworkRequest network_request = new NetworkRequest(activity);
        network_request.make_get_request(url, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response 1", "   " + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
                            String data = jsonObject.getString("data");
                            DataHolder_Model.getInstance().setComponent_history_models(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data, Component_history_Model[].class))));
                        } else {
                            AppUtils.show_snak(activity, "" + jsonObject.getString("message"));
                        }
                        mesg.obj = msg_code;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.obj = 404;
                    Log.e("JSONException", "" + e.getMessage());
                }
                handler.sendMessage(mesg);
            }

            @Override
            public void onError(String message) {
                mesg.obj = 404;
                handler.sendMessage(mesg);
                Log.e("message 1", "   " + message);
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public static void post_data(final Activity activity, String
            url, HashMap<String, String> params, final Handler handler) {

//      params.put("flag","development");
        Log.e("url ", " is  " + url);
        Log.e("params ", " is  " + params);
        final Message mesg = new Message();
        NetworkRequest network_request = new NetworkRequest(activity);
        network_request.make_post_request(url, params, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response 1", "   " + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (!msg_code.equals("200")) {
                            AppUtils.show_snak(activity, "" + jsonObject.getString("message"));
                        }
                        mesg.obj = msg_code;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.obj = 404;
                    Log.e("JSONException", "" + e.getMessage());
                }
                handler.sendMessage(mesg);
            }

            @Override
            public void onError(String message) {
                mesg.obj = 404;
                handler.sendMessage(mesg);
                Log.e("message 1", "   " + message);
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void download_file(String url, String file_name, final Handler handler) {

//     url=url+"&flag=development";
        Log.e("url ", " is  " + url);
        final Message mesg = new Message();
        show_dialog();
        AndroidNetworking.download(url, directory, file_name)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(client)
                .build()
                .setDownloadProgressListener(new DownloadProgressListener() {
                    @Override
                    public void onProgress(long bytesDownloaded, long totalBytes) {

                    }
                })
                .startDownload(new DownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        // do anything after completion
                        hide_dialog();
                        mesg.obj = "true";
                        handler.sendMessage(mesg);
                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        hide_dialog();
                        mesg.obj = "false";
                        handler.sendMessage(mesg);
                    }
                });
    }

    public static void getLanguage(Activity activity, String key, final Handler handler) {
        final Message mesg = new Message();
        new NetworkRequest(activity).make_get_request(All_Api.getLanguage + key, new VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    Language_table language_table = new Language_table();
                    language_table.setLang_key(object.getString("lang_key"));
                    language_table.setLanguage_data(object.getString("data"));
                    language_table.setModified_date(object.getString("create_date"));
                    AppController.getInstance().getDatabase().getLanguageDao().insert(language_table);
                    mesg.obj = "true";
                } catch (JSONException e) {
                    e.printStackTrace();
                    mesg.obj = "false";
                }
                handler.sendMessage(mesg);
            }

            @Override
            public void onError(String message) {
                mesg.obj = "false";
                handler.sendMessage(mesg);
            }
        });
    }

    public void show_dialog() {
        if (progressDialog != null && !mContext.isFinishing())
            progressDialog.show();
    }

    public void hide_dialog() {
        if (progressDialog != null && !mContext.isFinishing())
            progressDialog.cancel();
    }

    public interface VolleyResponseListener {
        void onResponse(String response);

        void onError(String message);
    }


}
