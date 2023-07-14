package app.com.arresto.arresto_connect.ui.modules.add_data;

import static android.view.View.VISIBLE;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.interfaces.OnLocationSelectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.MapFragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;
import static app.com.arresto.arresto_connect.ui.modules.add_data.ProMasterEdit.oldSite;

public class Add_Site extends AddData_BaseFragment implements View.OnClickListener, OnLocationSelectListener {

    View view;

    public static Add_Site newInstance(String jonNo, String smsNo) {
        Add_Site fragmentFirst = new Add_Site();
        Bundle args = new Bundle();
        args.putString("job", jonNo);
        args.putString("sms", smsNo);
//        args.putString("data", AppUtils.getGson().toJson(brandModel));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    String jobNo = "", smsNo;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_site_fragment, parent, false);
            all_Ids();
            if (oldSite != null) {
                setSiteData(oldSite);
            }
            if (getArguments() != null) {
                jobNo = getArguments().getString("job");
                smsNo = getArguments().getString("sms");
                if (!jobNo.equals("")) {
                    jobcard_tv.setText(jobNo);
                    sms_tv.setText(smsNo);
                }
            }
            heading_tv.setText(this.getTag());
            baseActivity.headerTv.setText("");
            String url = All_Api.getJobCard + Static_values.client_id + "&user_id=" + Static_values.user_id +
                    "&group_id=" + Static_values.user_id + "&jobcard=";
            fetch_data(url, true, null);
            Places.initialize(baseActivity, getResString("lbl_google_maps_key"));

        }
        return view;
    }

    private void setSiteData(Site_Model oldSite) {
        jobcard_tv.setText(oldSite.getSite_jobcard());
        sms_tv.setText(oldSite.getSite_sms());
        site_id_edt.setText(oldSite.getSiteID_id());
        site_loc_edt.setText(oldSite.getSite_location());
        site_city_edt.setText(oldSite.getSite_city());
        site_adrs_edt.setText(oldSite.getSite_address());
        person_name_edt.setText(oldSite.getSite_contact_name());
        person_number_edt.setText(oldSite.getSite_contact_number());
        person_email_edt.setText(oldSite.getSite_contact_email());
        site_lat_edt.setText(oldSite.getSite_lattitude());
        site_long_edt.setText(oldSite.getSite_longitude());
    }


    MaterialButton select_loc_btn, continue_btn;
    TextView heading_tv;
    EditText site_id_edt, site_loc_edt, site_city_edt, site_adrs_edt, person_name_edt, person_number_edt, person_email_edt, site_lat_edt, site_long_edt;
    private TextView jobcard_tv, sms_tv;
    ArrayAdapter na_adapter;

    private void all_Ids() {
        select_loc_btn = view.findViewById(R.id.select_loc_btn);
        continue_btn = view.findViewById(R.id.continue_btn);
        heading_tv = view.findViewById(R.id.heading_tv);
        jobcard_tv = view.findViewById(R.id.jobcard_tv);
        sms_tv = view.findViewById(R.id.sms_tv);
        site_id_edt = view.findViewById(R.id.site_id_edt);
        site_loc_edt = view.findViewById(R.id.site_loc_edt);
        site_city_edt = view.findViewById(R.id.site_city_edt);
        site_adrs_edt = view.findViewById(R.id.site_adrs_edt);
        person_name_edt = view.findViewById(R.id.person_name_edt);
        person_number_edt = view.findViewById(R.id.person_number_edt);
        person_email_edt = view.findViewById(R.id.person_email_edt);
        site_lat_edt = view.findViewById(R.id.site_lat_edt);
        site_long_edt = view.findViewById(R.id.site_long_edt);
        new CustomTextWatcher(site_id_edt, textWatcher);
        new CustomTextWatcher(site_loc_edt, textWatcher);
        new CustomTextWatcher(site_city_edt, textWatcher);
        new CustomTextWatcher(site_adrs_edt, textWatcher);
        select_loc_btn.setOnClickListener(this);
        continue_btn.setOnClickListener(this);
        na_adapter = new ArrayAdapter<>(getActivity(), R.layout.spiner_tv, new ArrayList<>(Arrays.asList("NA")));
        jobcard_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (job_cards.size() > 0) {
                    chooseJobSmsDialog("Job Card", new ObjectListener() {
                        @Override
                        public void onResponse(Object obj) {
                            if (obj instanceof Constant_model) {
                                Constant_model job = (Constant_model) obj;
                                jobcard_tv.setText(job.getName());
                                String url = All_Api.getJobCard + Static_values.client_id + "&user_id=" + Static_values.user_id +
                                        "&group_id=" + Static_values.user_id + "&jobcard=" + job.getName();
                                fetch_data(url, false, null);
                            }
                        }

                        @Override
                        public void onError(String error) {
//                            assetModel.setJobcard(null);
                        }
                    });
                }
            }
        });
        sms_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sms_numbers.size() > 0) {
                    chooseJobSmsDialog("SMS", new ObjectListener() {
                        @Override
                        public void onResponse(Object obj) {
                            if (obj instanceof Constant_model) {
                                Constant_model job = (Constant_model) obj;
                                sms_tv.setText(job.getName());
                            }
                        }

                        @Override
                        public void onError(String error) {
//                            assetModel.setSms(null);
                        }
                    });
                }
            }
        });
    }

    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
        @Override
        public void afterChange(TextView view, String text) {
            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
                ((TextInputLayout) view.getParent().getParent()).setError(null);
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.select_loc_btn:
                MapFragment mapFragment = new MapFragment();
                mapFragment.setFragmentCallback(this);
//                mapFragment.setTargetFragment(this, 1020);
                LoadFragment.replace(mapFragment, baseActivity, "Select a site location");
                break;
            case R.id.continue_btn:
                if (jobcard_tv.getText().toString().equals("")) {
                    show_snak(getActivity(), "Please select jobcard");
                    return;
                }

                if (sms_tv.getText().toString().equals("")) {
                    show_snak(getActivity(), "Please select SMS");
                    return;
                }

                if (isEmpty(site_id_edt) || isEmpty(site_loc_edt) || isEmpty(site_city_edt) || isEmpty(site_adrs_edt)) {
                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                    return;
                }
                if (baseActivity.validateEmail(person_email_edt))
                    postData();
                break;
        }
    }

    private void postData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("client_id", Static_values.client_id);
            jsonObject.put("user_id", Static_values.client_id);
            jsonObject.put("jobcard", jobcard_tv.getText().toString());
            jsonObject.put("sms", sms_tv.getText().toString());
            jsonObject.put("siteID", site_id_edt.getText().toString());
            jsonObject.put("location", site_loc_edt.getText().toString());
            jsonObject.put("region", site_city_edt.getText().toString());
            jsonObject.put("address", site_adrs_edt.getText().toString());
            jsonObject.put("latitude", site_lat_edt.getText().toString());
            jsonObject.put("longitude", site_long_edt.getText().toString());
            jsonObject.put("contact_name", person_name_edt.getText().toString());
            jsonObject.put("contact_phone", person_number_edt.getText().toString());
            jsonObject.put("contact_email", person_email_edt.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new NetworkRequest(baseActivity).make_contentpost_request(All_Api.add_Site, jsonObject, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    show_snak(baseActivity, jsonObject.getString("message"));
                    if (jsonObject.getString("status_code").equals("200")) {
                        homeActivity.submit_action("dashboard");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {

                Log.e("error", "" + message);
            }
        });
    }

    @Override
    public void OnLocationSelect(LatLng point) {
        printLog("Place on Site Fragment " + point);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                site_lat_edt.setText("" + point.latitude);
                site_long_edt.setText("" + point.longitude);

                getCurrentAddress(point);
            }
        }, 200);
    }

    public void getCurrentAddress(LatLng point) {
        try {
            Geocoder geocoder = new Geocoder(baseActivity, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(point.latitude, point.longitude, 1);
            if (addresses != null && addresses.size() > 0) {
                String current_state = addresses.get(0).getAdminArea();
                String current_city = addresses.get(0).getSubAdminArea();
                String current_address = addresses.get(0).getAddressLine(0);
                Log.e("current_address", "address " + addresses.get(0));
                site_city_edt.setText(current_city);
                site_adrs_edt.setText(current_address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
