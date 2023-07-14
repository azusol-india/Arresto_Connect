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

package app.com.arresto.arresto_connect.ui.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.hbb20.CountryCodePicker;

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
import app.com.arresto.arresto_connect.constants.CameraActivity;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.custom_scan.DecoderActivity;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;

import static app.com.arresto.Flavor_Id.FlavourInfo.Fl_CLIENT_ID;
import static app.com.arresto.Flavor_Id.FlavourInfo.webData;
import static app.com.arresto.Flavor_Id.FlavourInfo.webData_spenish;
import static app.com.arresto.arresto_connect.constants.AppUtils.bytes_toBase64;
import static app.com.arresto.arresto_connect.constants.AppUtils.getIMEI;
import static app.com.arresto.arresto_connect.constants.AppUtils.getRealPathFromURI;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.group_email_alert;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.CURRENT_LANGUAGE;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.Host;

public class Sign_upActivity extends BaseActivity {
    EditText name, phone, email, psswrd, cmpny_edt, group_id_edt, group_email_edt, cnfrmPsswrd;
    MaterialButton createAcount, scan_code;
    FloatingActionButton img_get;
    ImageView profile_img;
    String usr_img_path = "";
    FrameLayout bottom_sheet;
    ImageView info_ic;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.activity_signup;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        find_all_id();
        Log.e("params is ", " now " + getIMEI(this));

        createAcount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                term_dialog();
                String pass = psswrd.getText().toString();
                String passcnf = cnfrmPsswrd.getText().toString();
                if (!isEmpty(name) && validateEmail(email) && validatePassword(cnfrmPsswrd) && !isEmpty(cmpny_edt)) {
                    if (!phone.getText().toString().equals("") && !validatePhone(phone)) {
                        return;
                    }
                    if (!pass.equals(passcnf))
                        show_snak(Sign_upActivity.this, getResString("lbl_match_pass"));
                    else if ((!group_email_edt.getText().toString().equals("") && !validateEmail(group_email_edt))
                            || group_email_edt.getText().toString().equals(email.getText().toString())) {
                        show_snak(Sign_upActivity.this, getResString("lbl_designated_email"));
                    } else if (client_id == null || client_id.equals("")) {
                        show_snak(Sign_upActivity.this, getResString("lbl_scn_barcode_msg"));
                    } else {
                        term_dialog();
                    }
                }
            }
        });
        scan_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStoragePermissionGranted())
                    return;

                Intent intent = new Intent(Sign_upActivity.this, DecoderActivity.class);
                startActivityForResult(intent, 10);

            }
        });

        img_get.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!isStoragePermissionGranted())
                    return;
                selectImage();
            }
        });
        if (Fl_CLIENT_ID.equals("1040")) {
            ((ViewGroup)findViewById(R.id.ic7).getParent()).setVisibility(View.GONE);
        }
        if (Fl_CLIENT_ID.equals("952")) {
            group_email_edt.setFocusable(false);
            findViewById(R.id.grp_id_layer).setVisibility(View.GONE);
            group_email_edt.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    String group_txt = group_id_edt.getText().toString();
                    if (!group_txt.isEmpty())
                        getGroupUsers(group_txt);
                    else show_snak(Sign_upActivity.this, "Please enter your group id.");
                }
            });
        }
        info_ic.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                group_email_alert(Sign_upActivity.this);
            }
        });
//        setTextViewHTML(trm_chek, getResString("lbl_trms_st"));
        show_showCase();
        setUpHelpVideo();
    }

    public void setUpHelpVideo() {
        getHelpVideoData(new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 1) {
                    if (help_videos != null) {
                        ConstantMethods.find_pageVideo(Sign_upActivity.this, "landing");
//                        ConstantMethods.find_pageVideo(Sign_upActivity.this, "Landing Page - Register New User");
                    }
                }
            }
        });

    }

    private void show_showCase() {
        ArrayList<String> mesages = new ArrayList<>(Arrays.asList("Create new account"));
        ArrayList<View> views = new ArrayList<View>(Arrays.asList(createAcount));
        if (scan_code.getVisibility() == View.VISIBLE) {
            mesages.add("To register scan company's QR Code");
            views.add(scan_code);
        }
        Add_Showcase.newInstance(this).setData(mesages, views);
    }

    String client_id = "";

    CountryCodePicker code_piker;
    TextInputLayout email_input;

    public void find_all_id() {

        profile_img = findViewById(R.id.prf_img);
        img_get = findViewById(R.id.img_get);
        info_ic = findViewById(R.id.info_ic);
        name = findViewById(R.id.name);
//        lastName = findViewById(R.id.lastName);
//        adrs_edt = findViewById(R.id.adrs_edt);
        code_piker = findViewById(R.id.code_piker);
        phone = findViewById(R.id.phone);
        email_input = findViewById(R.id.email_input);
        email = findViewById(R.id.email);
        cmpny_edt = findViewById(R.id.cmpny_edt);
        group_id_edt = findViewById(R.id.group_id_edt);
        group_email_edt = findViewById(R.id.group_email);
//        cmpny_adrs_edt = findViewById(R.id.cmpny_adrs_edt);
        psswrd = findViewById(R.id.password);
        cnfrmPsswrd = findViewById(R.id.cnfrmPasswrd);
//        trm_chek = findViewById(R.id.trm_chek);
        createAcount = findViewById(R.id.createAcount);
        bottom_sheet = findViewById(R.id.bottom_sheet);

//        contry_edt = findViewById(R.id.contry_edt);
//        city_edt = findViewById(R.id.city_edt);
//        state_edt = findViewById(R.id.stat_edt);

        scan_code = findViewById(R.id.scan_code);
        if (!Fl_CLIENT_ID.equals("")) {
            scan_code.setVisibility(View.GONE);
            client_id = bytes_toBase64(Fl_CLIENT_ID.getBytes()).trim();
        }
        email_input.setHint("*" + getResString("lbl_email"));

        new CustomTextWatcher(name, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                isEmpty(view);
            }
        });
        new CustomTextWatcher(email, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                if (text != null && text.length() > 4)
                    validateEmail(view);
            }
        });
        new CustomTextWatcher(phone, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                if (text != null && text.length() > 4)
                    validatePhone(view);
                else if (text.equals(""))
                    ((TextInputLayout) phone.getParent().getParent()).setErrorEnabled(false);
            }
        });
        new CustomTextWatcher(psswrd, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                if (text != null && text.length() > 3)
                    validatePassword(view);
            }
        });

        new CustomTextWatcher(cnfrmPsswrd, new CustomTextWatcher.OnTextChange() {
            @Override
            public void afterChange(TextView view, String text) {
                if (!isEmpty(view)) {
                    if (!psswrd.getText().toString().equals(text)) {
                        setError(cnfrmPsswrd, getResString("lbl_match_pass"));
                    }
                }
            }
        });

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                load_image_file(usr_img_path, profile_img);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                profile_img.setImageURI(selectedImage);
                profile_img.setVisibility(View.VISIBLE);
                usr_img_path = getRealPathFromURI(Sign_upActivity.this, selectedImage);
            } else if (requestCode == 10) {
                if (data != null) {
                    String scaned_data = data.getDataString();
                    if (scaned_data.contains(Host)) {
                        if (scaned_data != null) {
                            client_id = Uri.parse(scaned_data).getQueryParameter("client");
//                        client_id = scaned_data;
                        } else {
                            show_snak(this, getResString("lbl_try_again_msg"));
                        }
                    } else {
                        show_snak(this, "Invalid code! Please try again");
                    }
                } else {
                    show_snak(this, getResString("lbl_try_again_msg"));
                }

            }
        }
    }

    private void selectImage() {
        final Dialog dialog = new Dialog(this, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_choose_image);
        final ImageView close_btn = dialog.findViewById(R.id.close_btn);
        final TextView capture_tv = dialog.findViewById(R.id.capture_tv);
        final TextView choose_tv = dialog.findViewById(R.id.choose_tv);
        final MaterialButton cancel_btn = dialog.findViewById(R.id.cancel_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        capture_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = System.currentTimeMillis() + ".jpg";
                new File(directory).mkdirs();
                usr_img_path = directory + name;
                startCamera(1, name);
                dialog.dismiss();
            }
        });
        choose_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 2);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void startCamera(int request_code, String name) {
        Intent camera = new Intent(this, CameraActivity.class);
//        Intent camera = new Intent(this, CameraTest.class);
        camera.putExtra("path", directory);
        camera.putExtra("name", name);
        startActivityForResult(camera, request_code);
    }

    public void register_account() {
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email.getText().toString());
        params.put("phone", code_piker.getTextView_selectedCountry().getText() + " " + phone.getText().toString());
        String[] name_st = name.getText().toString().split(" ", 2);
        params.put("firstname", name_st[0]);
        if (name_st.length > 1)
            params.put("lastname", name_st[1]);
        params.put("password", psswrd.getText().toString());
        params.put("imei_no", getIMEI(this));
//        params.put("address", adrs_edt.getText().toString());
        params.put("client_id", client_id);
        params.put("group_email", group_email_edt.getText().toString());
//        params.put("country_id", getAdressID(contry_list, contry_code, contry_edt.getText().toString()));
//        params.put("state_id", getAdressID(state_list, state_code, state_edt.getText().toString()));
//        params.put("city_id", getAdressID(city_list, city_code, city_edt.getText().toString()));
        params.put("upro_company", cmpny_edt.getText().toString());
//        params.put("upro_company_address", cmpny_adrs_edt.getText().toString());


        HashMap<String, File> files = new HashMap<>();
        if (!usr_img_path.equals("")) {
            files.put("user_img", new File(usr_img_path));
        }
        Log.e("params is ", " now " + params);

        new NetworkRequest(this).upload_param_file(All_Api.register_URL, params, files, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String error = jsonObject.getString("error");
                        if (!error.equals("")) {
                            show_snak(Sign_upActivity.this, error);
                        } else {
                            show_snak(Sign_upActivity.this, getResString("lbl_ac_creat_st"));
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Sign_upActivity.this, LoginActivity.class);
                                    intent.putExtra("email", email.getText().toString());
                                    intent.putExtra("password", psswrd.getText().toString());
                                    intent.putExtra("type", "signUp");
                                    intent.putExtra("logout_click", "run_logout");
                                    startActivity(intent);
                                }
                            }, 2000);

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error ", " is " + message);
            }
        });

    }

    BottomSheetDialog mBottomSheetDialog;

    public void term_dialog() {
        final View bottomSheetLayout = getLayoutInflater().inflate(R.layout.term_dialog, null);
        TextView html_tv = bottomSheetLayout.findViewById(R.id.html_tv);
        WebView web_view = bottomSheetLayout.findViewById(R.id.web_view);
//        web_view.loadUrl("http://arresto.in/terms-and-conditions/");
//        web_view.loadData(webData, "text/html", "utf-8");
        WebSettings settings = web_view.getSettings();
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setDefaultFontSize(30);
        if (mPrefrence.getData(CURRENT_LANGUAGE) != null && !mPrefrence.getData(CURRENT_LANGUAGE).isEmpty()
                && mPrefrence.getData(CURRENT_LANGUAGE).equalsIgnoreCase("spanish") &&
                (Fl_CLIENT_ID.equals("931") || Fl_CLIENT_ID.equals("1978") || Fl_CLIENT_ID.equals("1979")))
            web_view.loadData(webData_spenish, "text/html", "utf-8");
        else web_view.loadData(webData, "text/html", "utf-8");
        Button button_close = bottomSheetLayout.findViewById(R.id.button_close);
        Button button_ok = bottomSheetLayout.findViewById(R.id.button_ok);
        button_close.setText(getResString("lbl_close"));
        button_ok.setText(getResString("lbl_agree"));
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBottomSheetDialog.dismiss();
            }
        });
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_account();
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(bottomSheetLayout);
        mBottomSheetDialog.show();
    }

    @SuppressLint("HandlerLeak")
    public void getGroupUsers(String role_id) {
        String url;
        url = All_Api.getGroup_clients + Fl_CLIENT_ID + "&user_group_code=" + role_id;
        Log.e("url ", " is  " + url);
        new NetworkRequest(this).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
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
                            AppUtils.show_snak(Sign_upActivity.this, "" + jsonObject.getString("message"));
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
        Dialog dialog = new Dialog(this, R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        TextView header = dialog.findViewById(R.id.header);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        EditText email_edt = dialog.findViewById(R.id.email_edt);
//        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        dialog.findViewById(R.id.email_layer).setVisibility(View.VISIBLE);
        header.setText("Select or Enter Designated Email");
        if (groupUsers != null && groupUsers.size() > 0) {
            Log.e("data length ", " is " + groupUsers.size());
            CustomRecyclerAdapter ad = new CustomRecyclerAdapter(this, groupUsers);
            RecyclerView listView = dialog.findViewById(R.id._list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
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
                    String email_ = email_edt.getText().toString();
                    if (isValidEmail(email_)) {
                        group_email_edt.setText(email_);
                    } else if (ad != null && ad.lastSelected != -1) {
                        Object slected_item = ad.getItem(ad.lastSelected);
                        if (slected_item != null) {
                            group_email_edt.setText(((GroupUsers)slected_item).getUacc_email());
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

}