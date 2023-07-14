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

package app.com.arresto.arresto_connect.ui.modules.periodic_maintainance;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Periodic_model;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Peridic_stepAdapter;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.slctd_product_name;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.network.All_Api.get_pdm_steps;

public class Periadic_steps extends Fragment {
    View view;
    RecyclerView steps_recycler;
    Peridic_stepAdapter peridic_stepAdapter;
    TextView finish_btn;
    public static ArrayList<Integer> inspected_steps;
    ArrayList<Periodic_model> periodic_models;
    public static String inspection_id = "";


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.projects_frag_lay, container, false);
            setupUI(view, getActivity());
            periodic_models = new ArrayList<>();
            inspected_steps = new ArrayList<>();
            steps_recycler = view.findViewById(R.id.projects_list);
            view.findViewById(R.id.ad_project_btn).setVisibility(View.GONE);
            finish_btn = view.findViewById(R.id.finish_btn);
            finish_btn.setVisibility(View.VISIBLE);
            finish_btn.setText(getResString("lbl_cntnue_st"));
            steps_recycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            get_DataFromDB(unique_id);

            if (isNetworkAvailable(getActivity()))
                get_periodic_data();
            else
                periodic_models = new ArrayList<>(Arrays.asList(new Gson().fromJson(AppController.getInstance().getDatabase().getAsset_stepsDao().getAsset_steps(slctd_product_name, client_id), Periodic_model[].class)));

            peridic_stepAdapter = new Peridic_stepAdapter(getActivity(), periodic_models);
            steps_recycler.setAdapter(peridic_stepAdapter);

            finish_btn.setVisibility(View.VISIBLE);

            finish_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("HandlerLeak")
                @Override
                public void onClick(View v) {
                    if (peridic_stepAdapter != null) {
                        if (inspected_steps.size() >= periodic_models.size()) {
                            LoadFragment.replace(new Periodic_steps_subitem(), getActivity(), getResString("lbl_sub_asset"));
                        } else {
                            AppUtils.show_snak(getActivity(), "Please Inspect all steps.");
                        }
                    }
                }
            });
        }

        if (peridic_stepAdapter != null)
            peridic_stepAdapter.notifyDataSetChanged();
        return view;
    }

    public static long start_time = 0;

    private void get_DataFromDB(String unique_id) {
        inspected_steps.addAll(AppController.getInstance().getDatabase().getPdm_stepsDao().getInspectedPos(unique_id));
        if (inspected_steps.size() < 1) {
            start_time = System.currentTimeMillis();
        }
    }


    public void get_periodic_data() {
        NetworkRequest network_request = new NetworkRequest(getActivity());
        String url = get_pdm_steps + slctd_product_name + "&client_id=" + client_id;
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
                            AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                            getActivity().onBackPressed();
                        } else {
                            String data = jsonObject.getString("data");
                            periodic_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data, Periodic_model[].class)));
                            peridic_stepAdapter.add_data(periodic_models);
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
