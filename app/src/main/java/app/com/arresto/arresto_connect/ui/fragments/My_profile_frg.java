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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import app.com.arresto.Flavor_Id.FlavourInfo;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.group_email_alert;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;


public class My_profile_frg extends Base_Fragment implements View.OnClickListener {
    View view;
    private ImageView profile_img, info_ic;
    private ScrollView scrollableview;
    private String usr_img_path = "";
    private FloatingActionButton img_get;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.my_profile_frg, parent, false);
        all_id(view);
        setdata();
        set_listner();

        setupUI(view, getActivity());

        selection_check();
        autocomplete_itemclick();

        get_address_data(All_Api.contry_base, "country");
        if (FlavourInfo.Fl_CLIENT_ID.equals("1040")) {
            ((ViewGroup)view.findViewById(R.id.g_ic).getParent()).setVisibility(View.GONE);
        }

        show_showCase();
//        download_file("https://arresto.in/connectkare/uploads/376/users/856b24e2c1cecf6d67ae05a1d1efb378FLIR0015.jpg");
        return view;
    }

    //    @SuppressLint("HandlerLeak")
//    public void download_file(String url) {
//        String fileName = url.substring(url.lastIndexOf('/') + 1);
//        new NetworkRequest(baseActivity).download_file(url, fileName, new Handler() {
//            @Override
//            public void handleMessage(Message msg) {
//                Log.e("return obj", "" + msg.obj);
//                if (msg.obj != null && msg.obj.equals("true")) {
//
//                }
//            }
//        });
//    }
    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "profile");
//        ConstantMethods.find_pageVideo(getActivity(),"Landing Page - Profile");
    }

    private void show_showCase() {
        ArrayList<String> mesages = new ArrayList<>(Arrays.asList("Click to update profile"));
        ArrayList<View> views = new ArrayList<View>(Arrays.asList(sav_btn));
        Add_Showcase.getInstance(getActivity()).setData(mesages, views);
    }

    Profile_Model profile_model;

    private void setdata() {
        profile_model = Profile_Model.getInstance();
        fname_edt.setText(profile_model.getUpro_first_name());
        lname_edt.setText(profile_model.getUpro_last_name());
        group_email_edt.setText(profile_model.getGroup_email());
        emp_edt.setText(profile_model.getEmp_id());
        dob_txt.setText(profile_model.getUpro_dob());
        adrs_edt.setText(profile_model.getUpro_address());

        contry_edt.setText(profile_model.getUpro_country_id());
        state_edt.setText(profile_model.getUpro_zone_id());
        city_edt.setText(profile_model.getUpro_city_id());

        cmpny_edt.setText(profile_model.getUpro_company());
        cmpny_adrs_edt.setText(profile_model.getUpro_company_address());

        group_name_edt.setText(profile_model.getUser_group_name());
        group_id_edt.setText(profile_model.getUser_group_code());


        email_txt.setText(Static_values.user_email);
//        phn_edt.setText(profile_model.getUpro_phone());
        setPhoneCode(profile_model.getUpro_phone());
        AppUtils.load_profile(profile_model.getUpro_image(), profile_img);

//        if (HomeActivity.bmimage != null)
//            profile_img.setImageBitmap(HomeActivity.bmimage);
    }

    //            code_tv
    private TextView email_txt, dob_txt, sav_btn;
    private EditText fname_edt, lname_edt, phn_edt, emp_edt, group_email_edt, adrs_edt,group_id_edt,group_name_edt, cmpny_edt, cmpny_adrs_edt;
    private SwipeRefreshLayout swipe;
    CountryCodePicker code_piker;

    private void all_id(View view) {
        swipe = view.findViewById(R.id.swipe);
        profile_img = view.findViewById(R.id.profile_img);
        img_get = view.findViewById(R.id.img_get);
        fname_edt = view.findViewById(R.id.fname_txt);
        lname_edt = view.findViewById(R.id.lname_txt);
        phn_edt = view.findViewById(R.id.phn_no);
        email_txt = view.findViewById(R.id.email_txt);
        group_email_edt = view.findViewById(R.id.group_email_edt);
        emp_edt = view.findViewById(R.id.emp_edt);
        info_ic = view.findViewById(R.id.info_ic);
//        code_tv = view.findViewById(R.id.code_tv);
        code_piker = view.findViewById(R.id.code_piker);
        dob_txt = view.findViewById(R.id.dob_txt);
        sav_btn = view.findViewById(R.id.sav_btn);
        adrs_edt = view.findViewById(R.id.adrs_edt);
        group_id_edt= view.findViewById(R.id.group_id_edt);
        group_name_edt = view.findViewById(R.id.group_name_edt);

        contry_edt = view.findViewById(R.id.contry_edt);
        city_edt = view.findViewById(R.id.city_edt);
        state_edt = view.findViewById(R.id.stat_edt);

        cmpny_edt = view.findViewById(R.id.cmpny_edt);
        cmpny_adrs_edt = view.findViewById(R.id.cmpny_adrs_edt);
        scrollableview = view.findViewById(R.id.scrollableview);
        swipe.setColorSchemeResources(R.color.app_text, R.color.app_btn_bg, R.color.app_green);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ((BaseActivity) getActivity()).get_profileData(false);
                if (swipe.isRefreshing())
                    swipe.setRefreshing(false);
            }
        });
        swipe.setDistanceToTriggerSync(450);
    }

    private void set_listner() {
        dob_txt.setOnClickListener(this);
        img_get.setOnClickListener(this);
        sav_btn.setOnClickListener(this);
        profile_img.setOnClickListener(this);
        info_ic.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dob_txt:
                show_Date_piker();
                break;
            case R.id.info_ic:
                group_email_alert(getActivity());
                break;
            case R.id.img_get:
                chooseImage(new OnImageCapture() {
                    @Override
                    public void onCapture(String path) {
                        usr_img_path = path;
                        load_image_file(usr_img_path, profile_img);
                        profile_img.setVisibility(View.VISIBLE);
                    }
                });
                break;
            case R.id.sav_btn:
                if (group_email_edt.getText().toString().equals(Static_values.user_email)) {
                    show_snak(getActivity(), getResString("lbl_designated_email"));
                } else {
                    update_profile();
                }
                break;
            case R.id.profile_img:
                if (imagePath != null && !imagePath.equals(""))
                    FullScreenDialog.newInstance((AppCompatActivity) getActivity(), imagePath);
                else if (profile_model.getUpro_image() != null && !profile_model.getUpro_image().equals(""))
                    FullScreenDialog.newInstance((AppCompatActivity) getActivity(), profile_model.getUpro_image());
                break;
        }
    }

    public void setPhoneCode(String mobile) {
//        String cntry_code = getCountryCode(country_name);
        if (mobile.contains("+")) {
            String[] cntry_code = mobile.split(" ");
            if (cntry_code.length > 1) {
                code_piker.setCountryForPhoneCode(Integer.parseInt(cntry_code[0].replaceAll("[^a-zA-Z0-9]", "")));
                phn_edt.setText(cntry_code[1]);
            }
        } else {
            phn_edt.setText(mobile);
        }

    }


    private int mYear, mMonth, mDay;

    private void show_Date_piker() {
        final Calendar c = Calendar.getInstance(), c2 = Calendar.getInstance();
        c2.add(Calendar.YEAR, -18);
        if (!dob_txt.getText().toString().equals("")) {
            try {
                Date d = baseActivity.Date_Format().parse(dob_txt.getText().toString());
                c.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                dob_txt.setText(baseActivity.Date_Format().format(c.getTime()));
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(getActivity(), pDateSetListener, mYear, mMonth, mDay);
        dialog.getDatePicker().setMaxDate(c2.getTimeInMillis());
//        ShowcaseView view = getActivity().getLayoutInflater().inflate(R.layout.title_lay, null);
//        dialog.setCustomTitle(view);
        dialog.show();
    }

    private void update_profile() {
        HashMap<String, String> params = new HashMap<>();

        params.put("user_id", Static_values.user_id);
        params.put("client_id", client_id);
        params.put("first_name", fname_edt.getText().toString());
        params.put("last_name", lname_edt.getText().toString());
        params.put("emp_id", emp_edt.getText().toString());

        params.put("group_email", group_email_edt.getText().toString());
//        if (phn_edt.getText().toString().contains("+")) {
//            String[] phone = phn_edt.getText().toString().split(" ");
//            if (phone.length > 0)
//                params.put("mobile_no", phone[phone.length - 1]);
//            else
//                params.put("mobile_no", "");
//        } else {
        params.put("mobile_no", code_piker.getTextView_selectedCountry().getText() + " " + phn_edt.getText().toString());
//        }

        params.put("dob", dob_txt.getText().toString());
        params.put("address", adrs_edt.getText().toString());

        params.put("country_id", getAdressID(contry_list, contry_code, contry_edt.getText().toString()));
        params.put("state_id", getAdressID(state_list, state_code, state_edt.getText().toString()));
        params.put("city_id", getAdressID(city_list, city_code, city_edt.getText().toString()));

        params.put("upro_company", cmpny_edt.getText().toString());
        params.put("upro_company_address", cmpny_adrs_edt.getText().toString());

        HashMap<String, File> files = new HashMap<>();
        if (!usr_img_path.equals("")) {
            files.put("user_image", new File(usr_img_path));
        }
        new NetworkRequest(getActivity()).upload_param_file(All_Api.update_profile, params, files, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject1 = new JSONObject(response);
                        show_snak(getActivity(), jsonObject1.getString("msg"));
                        ((BaseActivity) getActivity()).get_profileData(false);
                        scrollableview.fullScroll(ScrollView.FOCUS_UP);
                        usr_img_path = "";
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

    }


    private void get_address_data(String url, final String type) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                switch (type) {
                    case "country":
                        parsh_contry_data(response);
                        break;
                    case "state":
                        parsh_state_data(response);
                        break;
                    case "city":
                        parsh_city_data(response);
                        break;
                }
                Log.e("response ", " address " + response);
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }

        });

    }

    private ArrayList<String> contry_list = new ArrayList<>(), state_list = new ArrayList<>(), city_list = new ArrayList<>();
    private ArrayList<String> contry_short_name = new ArrayList<>(), contry_code = new ArrayList<>(), state_code = new ArrayList<>(), city_code = new ArrayList<>();
    private ArrayAdapter<String> contry_state_adepter, state_adepter, city_adepter;
    private AutoCompleteTextView contry_edt, state_edt, city_edt;

    private void parsh_contry_data(String response) {
        try {
            contry_code = new ArrayList<>();
            contry_short_name = new ArrayList<>();
            contry_list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("country");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                contry_code.add(jsonObject1.getString("id"));
                contry_short_name.add(jsonObject1.getString("abb"));
                contry_list.add(jsonObject1.getString("name"));
            }
            contry_state_adepter = new ArrayAdapter<>(baseActivity, android.R.layout.simple_list_item_1, contry_list);
            contry_edt.setAdapter(contry_state_adepter);

            // auto get state data first time when fragment load

            if (state_list.size() < 1 && contry_edt.getText().length() > 0 && contry_list.size() > 0) {
                int index = contry_list.indexOf(contry_edt.getText().toString());
                if (index >= 0) {
                    String cntry_id = contry_code.get(index);
                    get_address_data(All_Api.state_base + cntry_id, "state");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parsh_state_data(String response) {
        try {
            state_code = new ArrayList<>();
            state_list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("state");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                state_code.add(jsonObject1.getString("id"));
                state_list.add(jsonObject1.getString("name"));
            }
            state_adepter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, state_list);
            state_edt.setAdapter(state_adepter);

            if (city_list.size() < 1 && city_edt.getText().length() > 0 && state_list.size() > 0) {
                int index = state_list.indexOf(city_edt.getText().toString());
                if (index >= 0) {
                    String state_id = state_code.get(index);
                    get_address_data(All_Api.city_base + state_id, "city");
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parsh_city_data(String response) {
        try {
            city_code = new ArrayList<>();
            city_list = new ArrayList<>();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("city");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                city_code.add(jsonObject1.getString("city_id"));
                city_list.add(jsonObject1.getString("city_name"));
            }
            city_adepter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, city_list);
            city_edt.setAdapter(city_adepter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void autocomplete_itemclick() {
        contry_edt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String state_id = contry_code.get(contry_list.indexOf(contry_state_adepter.getItem(position)));
                get_address_data(All_Api.state_base + state_id, "state");
            }
        });
        state_edt.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String city_id = state_code.get(state_list.indexOf(state_adepter.getItem(position)));
                get_address_data(All_Api.city_base + city_id, "city");
            }
        });
    }

    private void selection_check() {
        contry_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (!focus) {
                    if (!contry_list.contains(contry_edt.getText().toString())) {
                        contry_edt.setError("Please Add a Correct Country Name");
                        if (city_adepter != null)
                            city_adepter.clear();
                        if (state_adepter != null)
                            state_adepter.clear();
                    }
                } else {
                    city_edt.setText("");
                    state_edt.setText("");
                }
            }
        });

        state_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (!focus) {
                    if (!state_list.contains(state_edt.getText().toString())) {
                        state_edt.setError("Please Add a Correct State Name");
                        if (city_adepter != null)
                            city_adepter.clear();
                    }
                } else {
                    city_edt.setText("");
                }
            }
        });
        city_edt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (focus) {
                    if (city_list.size() < 1 && state_edt.getText().length() > 0 && state_list.size() > 1) {
                        int index = state_list.indexOf(state_edt.getText().toString());
                        String city_id = state_code.get(index);
                        get_address_data(All_Api.city_base + city_id, "city");
                    }
                } else {
                    if (!city_list.contains(city_edt.getText().toString())) {
                        city_edt.setError("Please Add a Correct City Name");
                    }
                }
            }
        });
    }

    private String getAdressID(ArrayList<String> name_array, ArrayList<String> id_array, String text) {
        int index = name_array.indexOf(text);
        String selected_id = "";
        if (index > -1) {
            selected_id = id_array.get(index);
        }
        return selected_id;
    }
}
