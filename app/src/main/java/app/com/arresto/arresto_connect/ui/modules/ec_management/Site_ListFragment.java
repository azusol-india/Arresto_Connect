package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.ECSites;
import app.com.arresto.arresto_connect.data.models.EC_Project;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Sites_Adapter;

import static app.com.arresto.arresto_connect.constants.AppUtils.DpToPixel;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;

public class Site_ListFragment extends Fragment {

    View view;
    TextView ad_site_btn;
    RecyclerView site_list;
    Sites_Adapter sites_adapter;
    EC_Project ec_project;
    public String page_type = "";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.projects_frag_lay, container, false);
            setupUI(view, getActivity());
            ec_project = DataHolder_Model.getInstance().getSlctd_ec_project();
            find_id();
            update_list();
            site_list.setAdapter(sites_adapter);
            if (this.getTag().equalsIgnoreCase(getResString("lbl_ec_reports"))) {
                page_type = "reports";
                ad_site_btn.setVisibility(View.GONE);
            } else {
                ad_site_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ec_project != null) {
                            create_SiteDialog();
                        }
                    }
                });
                blank_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ad_site_btn.performClick();
                    }
                });
            }


        } else {
//            refresh list
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "ec site");
    }

    TextView blank_tv;

    private void find_id() {
        view.findViewById(R.id.ad_project_btn).setVisibility(View.GONE);
        ad_site_btn = view.findViewById(R.id.finish_btn);
        ad_site_btn.setText("Add EC Project Site");
        site_list = view.findViewById(R.id.projects_list);
        blank_tv = view.findViewById(R.id.blank_tv);
        blank_tv.setText("Add EC Project Site");
//        ad_site_btn.setVisibility(View.VISIBLE);
//        ad_project_btn.setImageTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getApp_text()));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            ad_site_btn.setImageTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getApp_text()));
//        }

        setdata();
    }

    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        site_list.setLayoutManager(layoutManager);
        site_list.setPadding(3, 5, 5, DpToPixel(50));
        site_list.setClipToPadding(false);
        site_list.getLayoutManager().setMeasurementCacheEnabled(false);
    }

    public void create_SiteDialog() {
        final Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(true);
        builder.setContentView(R.layout.dialog_uin);
        TextView cancel_btn = builder.findViewById(R.id.cncl_btn);
        TextView create_btn = builder.findViewById(R.id.ok_btn);
        TextView header = builder.findViewById(R.id.header);
        final EditText edt_dialog = builder.findViewById(R.id.edt_dialog);
        builder.findViewById(R.id.ex_tv).setVisibility(View.GONE);
        header.setVisibility(View.VISIBLE);
        header.setText(getResString("lbl_add_site_name"));
        if (!logo_url.equals("")) {
            ImageView logo_img = builder.findViewById(R.id.logo_img);
            load_image_file(logo_url, logo_img);
        }

        edt_dialog.setHint("Enter Site Name");
        create_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_dialog.getText().toString().equals("")) {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("site_name", edt_dialog.getText().toString());
                        params.put("user_id", Static_values.user_id);
                        params.put("client_id", client_id);
                        params.put("project_id", ec_project.getEcp_id());
                        send_data(params, All_Api.ec_sites);

//                        else
//                            send_data(params, All_Api.ec_add_project);
                        builder.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getActivity(), "Please Enter Site Name.", Toast.LENGTH_LONG).show();

            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.show();
    }


    public void send_data(JSONObject params, String url) {
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(url, params, new NetworkRequest.VolleyResponseListener() {
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
                        } else {
                            AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                            update_list();
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

    @SuppressLint("HandlerLeak")
    public void update_list() {
        String url = All_Api.ec_sites + "?user_id=" + Static_values.user_id + "&client_id=" + client_id + "&project_id=" + ec_project.getEcp_id();
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response 1", "   " + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        List<ECSites> ecSites = null;
                        JSONObject jsonObject = new JSONObject(response);
                        String status_code = jsonObject.getString("status_code");
                        if (status_code.equals("200")) {
                            String data = jsonObject.getString("data");
                            ecSites = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data, ECSites[].class)));

                            if (sites_adapter != null) {
                                sites_adapter.update_Sitelist(ecSites);
                                sites_adapter.notifyDataSetChanged();
                            } else {
                                sites_adapter = new Sites_Adapter(getActivity(), ecSites, page_type);
                                site_list.setAdapter(sites_adapter);
                            }

                        } else {
                            AppUtils.show_snak(getActivity(), "" + jsonObject.getString("message"));
                        }

                        if (ecSites != null && ecSites.size() > 0) {
                            blank_tv.setVisibility(View.GONE);
                            ad_site_btn.setVisibility(View.VISIBLE);
                        } else {
                            blank_tv.setVisibility(View.VISIBLE);
                            ad_site_btn.setVisibility(View.GONE);
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
//                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
