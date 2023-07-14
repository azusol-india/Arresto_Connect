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

import static android.app.Activity.RESULT_OK;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.share_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.group_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.third_party.PdfView.createWebPrintJob;
import static app.com.arresto.arresto_connect.ui.fragments.Close_projectFragment.index;
import static app.com.arresto.arresto_connect.ui.fragments.Close_projectFragment.need_refresh;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.PdfView;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressConstant;
import app.com.arresto.arresto_connect.third_party.progress_lib.ACProgressFlower;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;

public class Report_webview extends Base_Fragment {
    String url;
    ACProgressFlower dialog;
    WebView web_view;
    LinearLayout header_lay;
    String type = "", report_no = "test";
    ImageView share_btn;
    LinearLayout btn_section;
    View rootView;
    TextView replace_btn, approve_btn, reject_btn;
    String status = "", id, inspected_by, thermal_id = "";

    public void find_id() {
        header_lay = rootView.findViewById(R.id.header_lay);
        web_view = rootView.findViewById(R.id.registerView);
        share_btn = rootView.findViewById(R.id.share_btn);
        btn_section = rootView.findViewById(R.id.btn_section);
        replace_btn = rootView.findViewById(R.id.replace_btn);
        reject_btn = rootView.findViewById(R.id.reject_btn);
        approve_btn = rootView.findViewById(R.id.approve_btn);

        if (client_id.equals("952") || client_id.equals("2069")) {
            reject_btn.setText(getResString("lbl_asset_failed"));
            approve_btn.setText(getResString("lbl_asset_passed"));
        }
    }

    public View FragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.report_webview, container, false);
        find_id();
        if (getArguments() != null) {
            url = getArguments().getString("url");
            type = getArguments().getString("type", "");
            report_no = getArguments().getString("report_no", "");
            status = getArguments().getString("status", "");
            id = getArguments().getString("id", "");
            thermal_id = getArguments().getString("thermal_id", "");
            inspected_by = getArguments().getString("inspected_by", "");
//            url=url+"&time="+System.currentTimeMillis();
            Log.e("web url", " is " + url);
        }


        Log.e("web url", " is " + getTag());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            share_btn.setImageTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getBtn_bg_clr()));
        }
        header_lay.setVisibility(View.GONE);
        dialog = new ACProgressFlower.Builder(getActivity())
                .direction(ACProgressConstant.DIRECT_CLOCKWISE)
                .themeColor(Color.WHITE)
//                .bgColor(Color.BLACK)
                .text(getResString("lbl_loading_str")).textSize(16).textMarginTop(5)
                .petalThickness(2)
                .sizeRatio((float) 0.22)
                .fadeColor(Color.YELLOW).build();
        dialog.show();
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.getSettings().setLoadWithOverviewMode(true);
        web_view.getSettings().setUseWideViewPort(true);
        web_view.getSettings().setSupportZoom(true);
        web_view.getSettings().setBuiltInZoomControls(true);
        web_view.getSettings().setDisplayZoomControls(false);
        web_view.getSettings().setDomStorageEnabled(true);
        web_view.getSettings().setAllowContentAccess(true);
        web_view.getSettings().setAllowFileAccess(true);
        web_view.getSettings().setPluginState(WebSettings.PluginState.ON);
        web_view.setWebChromeClient(new MyWebChromeClient());
        web_view.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("OverrideUrlLoading", " is " + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                web_view.loadUrl("javascript:(function() { document.querySelector('[role=toolbar]').remove();})()");
            }

            public void onPageFinished(WebView view, String url) {
                if (view.getTitle().equals(""))
                    view.reload();
                else {
                    dialog.cancel();
                    share_btn.setVisibility(View.VISIBLE);
                    web_view.loadUrl("javascript:(function() { document.querySelector('[role=toolbar]').remove();})()");
                }
            }
        });
        if (type.equals("ASM") || (type.equals("safety") && !status.equalsIgnoreCase("pending"))) {
            get_file_url(url);
        } else {
            //&& !thermal_id.equals("")
            if ((status.equalsIgnoreCase("pending") && !id.equals("") && client_id.equals("419")) &&
                    (!group_id.equals("8") || group_id.equals("3") || (group_id.equals("9") && inspected_by.equals(user_id)))) {
                replace_btn.setVisibility(View.VISIBLE);
                replace_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getGroupUsers();
                    }
                });
            } else {
                replace_btn.setVisibility(View.GONE);
            }
            if (status.equalsIgnoreCase("pending") && !id.equals("") &&
                    ((group_id.equals("18") && client_id.equals("376"))
                            || (group_id.equals("9") && !client_id.equals("376") && !client_id.equals("419"))
                            || (client_id.equals("419") && Profile_Model.getInstance().getCan_approve_reports() != null && Profile_Model.getInstance().getCan_approve_reports().equals("1"))
                            || (!client_id.equals("419") && group_id.equals("8"))
//                            || (client_id.equals("952")&& group_id.equals("20"))
                            || group_id.equals("3") || group_id.equals("20")
                            || ((group_id.equals("1") || group_id.equals("15")) && type.equals("safety")))) {
                openButtonSection();
            }

//            else if (!status.equalsIgnoreCase("pending") && (type.equals("report") || type.equals("history_report"))) {
//                isfile_url = true;
//            }
//            if (isfile_url || url.contains(".pdf") || url.contains(".xls") || url.contains(".xlsx") || url.contains(".doc")) {
            if (url.contains(".pdf") || url.contains(".xls") || url.contains(".xlsx") || url.contains(".doc")) {
                share_btn.setTag(url);
                url = "https://docs.google.com/viewer?url=" + url + "?time=" + System.currentTimeMillis();// not loading pdf file
//                url = "http://docs.google.com/gview?embedded=true&url=" + url + "?time=" + System.currentTimeMillis();
            } else if (!url.contains("?")) {
                url = url + "?time=" + System.currentTimeMillis();
                url = url + "&login_id=" + user_id;
            } else {
                url = url + "&time=" + System.currentTimeMillis();
                url = url + "&login_id=" + user_id;
            }

            url = url.replaceAll(" ", "%20");
            Log.e("final web url", " is " + url);
            web_view.loadUrl(url);
        }


        share_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (share_btn.getTag() != null) {
                    share_file(baseActivity, "" + share_btn.getTag(), Report_webview.this.getTag());
                } else {
                    create_pdf_new();
                }
            }
        });

        if (url.contains(All_Api.add_master) || url.contains(All_Api.add_assets)) {
            share_btn.setVisibility(View.GONE);
        }
        return rootView;
    }


    public void openButtonSection() {
        btn_section.setVisibility(View.VISIBLE);

        approve_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put("value", "approved");
                params.put("action", "approved");
                params.put("inspection_id", id);
                params.put("type", type);
                params.put("user_id", Static_values.user_id);
                params.put("client_id", Static_values.client_id);
                params.put("report_no", report_no);
                update_status(params);
            }
        });
        reject_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String, String> params = new HashMap<>();
                params.put("value", "rejected");
                params.put("action", "rejected");
                params.put("inspection_id", id);
                params.put("type", type);
                params.put("user_id", Static_values.user_id);
                params.put("client_id", Static_values.client_id);
                params.put("report_no", report_no);
                update_status(params);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    public void getGroupUsers() {
        String url;
        url = All_Api.getAll_Users + client_id + "&user_id=" + user_id;
//        url = All_Api.getGroup_Users + role_id + "&client_id=" + client_id + "&user_id=" + user_id;
//        url = All_Api.get_child_users + Static_values.client_id + "&user_id=" + Static_values.user_id;
        Log.e("url ", " is  " + url);
        new NetworkRequest(baseActivity).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
                            ArrayList<GroupUsers> groupUsers = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data").toString(), GroupUsers[].class)));
                            Collections.sort(groupUsers, new Comparator<GroupUsers>() {
                                public int compare(GroupUsers obj1, GroupUsers obj2) {
                                    return obj1.getUacc_username().compareToIgnoreCase(obj2.getUacc_username());
                                }
                            });
                            chooseUser(groupUsers);
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


    private void chooseUser(ArrayList<GroupUsers> groupUsers) {
        Dialog dialog = new Dialog(getContext(), R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        TextView header = dialog.findViewById(R.id.header);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        header.setText("Select a user to Assign");
        if (groupUsers != null && groupUsers.size() > 0) {
            Log.e("data length ", " is " + groupUsers.size());
            CustomRecyclerAdapter ad = new CustomRecyclerAdapter(getContext(), groupUsers);
            RecyclerView listView = dialog.findViewById(R.id._list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false);
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(ad);

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
//                    handler.sendEmptyMessage(101);
//                    initView();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad != null && ad.lastSelected != -1) {
                        Object slected_item = ad.getItem(ad.lastSelected);
                        if (slected_item != null) {
                            HashMap<String, String> params = new HashMap<>();
                            params.put("value", "replace");
                            params.put("action", "replace");
                            params.put("inspection_id", id);
                            params.put("thermal_id", thermal_id);
                            params.put("type", "replace");
                            params.put("assign_user", ((GroupUsers) slected_item).getUacc_id());
                            params.put("user_id", Static_values.user_id);
                            params.put("client_id", Static_values.client_id);
                            params.put("report_no", report_no);
                            update_status(params);
                        }
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

    private void get_file_url(String url) {
        new NetworkRequest(baseActivity).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status_code").equals("200")) {
                        share_btn.setTag(jsonObject.getString("report_pdf"));
                        web_view.loadUrl("https://docs.google.com/viewer?url=" + jsonObject.getString("report_pdf"));
                        share_btn.setVisibility(View.VISIBLE);
                    } else {
                        share_btn.setTag("");
                        dialog.cancel();
                        show_snak(getActivity(), "Report not generated please try again.");
                        getActivity().onBackPressed();
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

    public void create_pdf_new() {
        File direct = new File(Static_values.directory);
        if (!direct.exists()) {
            if (direct.mkdir()) {
                Toast.makeText(getActivity(), "Folder Is created in sd card", Toast.LENGTH_SHORT).show();
            }
        }
        final String fileName;
        if (report_no != null) {
            fileName = Report_webview.this.getTag() + report_no + ".pdf";
        } else fileName = Report_webview.this.getTag() + ".pdf";

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("Please wait");
        progressDialog.show();
        createWebPrintJob(baseActivity, web_view, direct, fileName, new PdfView.Callback() {

            @Override
            public void success(String path) {
                progressDialog.dismiss();
                AppUtils.sharePDF(getActivity(), path);
            }

            @Override
            public void failure() {
                progressDialog.dismiss();

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (web_view != null)
            web_view.destroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        web_view = null; // remove webView, prevent chromium to crash
    }

    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;

    class MyWebChromeClient extends WebChromeClient {
        // For 3.0+ Devices (Start)
        // onActivityResult attached before constructor
        protected void openFileChooser(ValueCallback uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }


        // For Lollipop 5.0+ Devices
        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }

            uploadMessage = filePathCallback;

            Intent intent = fileChooserParams.createIntent();
            try {
                startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(getActivity(), "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        //For Android 4.1 only
        protected void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            mUploadMessage = uploadMsg;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(Intent.createChooser(intent, "File Chooser"), FILECHOOSER_RESULTCODE);
        }

        protected void openFileChooser(ValueCallback<Uri> uploadMsg) {
            mUploadMessage = uploadMsg;
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(getActivity(), "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    public void update_status(final HashMap<String, String> params) {
        String url;
        if (params.get("type").equals("pdm_report"))
            url = All_Api.pdm_approve_api;
        else if (params.get("type").equals("safety"))
            url = All_Api.safety_approve_api;
        else if (params.get("type").equals("replace"))
            url = All_Api.replace_api;
        else
            url = All_Api.approve_api;
        NetworkRequest network_request = new NetworkRequest(baseActivity);
        network_request.make_post_request(url, params, new NetworkRequest.VolleyResponseListener() {
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
                            if (params.get("value").equalsIgnoreCase("replace"))
                                index = 1;
                            else if (params.get("value").equalsIgnoreCase("approved"))
                                index = 1;
                            else index = 2;
                            need_refresh = true;
                            getActivity().onBackPressed();
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
