package app.com.arresto.arresto_connect.ui.modules.rams;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Asset_Adapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class ProjectProductPreview extends Base_Fragment {
    View view;
    RecyclerView product_list;
    Asset_Adapter asset_adapter;

    public static ProjectProductPreview newInstance(String project_id) {
        ProjectProductPreview fragment = new ProjectProductPreview();
        Bundle args = new Bundle();
        args.putString("project_id", project_id);
        fragment.setArguments(args);
        return fragment;
    }

    String project_id;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.users_list_fragment, parent, false);
            find_id();
            product_list.setAdapter(asset_adapter);
            if (getArguments() != null) {
                project_id = getArguments().getString("project_id");
                get_projectcomponent();
            }
        }
        return view;
    }

    private void find_id() {
        view.findViewById(R.id.submit_btn).setVisibility(View.GONE);
        product_list = view.findViewById(R.id.users_list);
        setdata();
    }

    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        product_list.setLayoutManager(layoutManager);
        product_list.setPadding(3, 5, 5, 10);
        product_list.setClipToPadding(false);
        product_list.getLayoutManager().setMeasurementCacheEnabled(false);
    }

    private ArrayList<Component_model> component_models;

    public void get_projectcomponent() {
        component_models = new ArrayList<>();
        if (isNetworkAvailable(getActivity())) {
            String url;
            if (!project_id.equals(""))
                url = All_Api.project_data + user_id + "&project_id=" + project_id;
            else
                return;
            url = url + "&client_id=" + client_id;

            url = url.replace(" ", "%20");
            NetworkRequest networkRequest = new NetworkRequest(getActivity());
            networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getString("status_code").equals("200")) {
                                component_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Component_model[].class)));
                                asset_adapter = new Asset_Adapter(baseActivity, component_models, "project_products");
                                product_list.setAdapter(asset_adapter);
                            } else {
                                show_snak(getActivity(), jsonObject.getString("message"));
                            }
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
        } else {
            show_snak(getActivity(), getResString("lbl_network_alert"));
        }
    }

}
