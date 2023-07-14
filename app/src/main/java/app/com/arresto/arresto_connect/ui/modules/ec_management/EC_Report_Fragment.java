package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.EC_report_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.EC_reportAdapter;

import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class EC_Report_Fragment extends Fragment {
    View view;

    ExpandableListView report_list;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.ec_report_frag, container, false);
            findAllIds(view);
            get_reports_data(All_Api.Ec_report + client_id + "&user_id=" + user_id);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "ec reports");
    }

    public void findAllIds(View view) {
        report_list = view.findViewById(R.id.report_list);
    }

    ArrayList<EC_report_Model> ec_report_models;

    public void get_reports_data(String url) {
        url = url + "&cgrp_id=" + Static_values.role_id + "&site_id=" + DataHolder_Model.getInstance().getSlctd_site().getId() +
                "&project_id=" + DataHolder_Model.getInstance().getSlctd_ec_project().getEcp_id();
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.has("success")) {
                            if (jsonObject.getString("success").equals("No data Found")) {
                                show_snak(getActivity(), "No data Found!");
                            }
                        } else {
                            if (jsonObject.getString("status_code").equals("200")) {
//                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                ec_report_models = new ArrayList<>(Arrays.asList(new Gson().fromJson("" + jsonObject.getJSONArray("data"), EC_report_Model[].class)));

                            } else {
                                AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                            }
                        }
                    }
                    if (ec_report_models != null)
                        set_fragment(getActivity());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    public void set_fragment(Activity cntxt) {
        FragmentActivity activity = (FragmentActivity) cntxt;
        EC_reportAdapter myPagerAdapter = new EC_reportAdapter(activity, ec_report_models, report_list);
        report_list.setAdapter(myPagerAdapter);
    }
}
