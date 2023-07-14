package app.com.arresto.arresto_connect.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.gridlayout.widget.GridLayout;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;

public class UserDetailsFragment extends Base_Fragment {


    public static UserDetailsFragment newInstance(GroupUsers user) {
        UserDetailsFragment asmCustomPage = new UserDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user", AppUtils.getGson().toJson(user));
        asmCustomPage.setArguments(bundle);
        return asmCustomPage;
    }

    View view;
    GroupUsers user;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.user_details_fragment, parent, false);
            user = new Gson().fromJson(getArguments().getString("user"), GroupUsers.class);
            findAllIds(view);
            set_updata();
            get_sites_status(All_Api.get_due_sites + user.getUacc_id() + "&group_id=" + user.getUacc_group_fk() + "&cgrp_id=" + user.getCgrp_id());
        }
        return view;
    }

    private void set_updata() {
        if (user != null) {
            usr_name.setText(user.getUacc_username());
            email_tv.setText(user.getGroup_name());
            dsc_text.setText(user.getGroup_desc());
            AppUtils.load_profile(user.getUpro_image(), profil_pic);
        }


        ((ViewGroup) tv1.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv1.getText().toString().equals("0")) {
                    show_snak(getActivity(), getResString("lbl_noreg_dta_msg"));
                } else {
                    dueFragment("register", getResString("lbl_rgstr_prdct_st"), "myassets", tv1.getText().toString());
                }
            }
        });

        ((ViewGroup) tv2.getParent()).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (tv2.getText().toString().equals("0")) {
                    show_snak(getActivity(), getResString("lbl_inspcted_dta_msg"));
                } else {
//                        if (group_id.equals("9") || page_type.equals("inspection"))
                    dueFragment("inspected", getResString("lbl_inspctd_st"), "myassets", tv2.getText().toString());
//                                load_fragment("inspected", getResString("lbl_inspctd_st"), "myassets", tv2.getText().toString());
//                        else {
//                            load_fragment("due", getResString("lbl_main_due_st"), "maintenanceDue", tv2.getText().toString());
//                        }
                }
            }
        });

        ((ViewGroup) tv3.getParent()).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (tv3.getText().toString().equals("0")) {
                            show_snak(getActivity(), getResString("lbl_nodue_dta_msg"));
                        } else {
//                                if (group_id.equals("9") || page_type.equals("inspection"))
                            dueFragment("due", getResString("lbl_inspctndue_txt"), "myassets", tv3.getText().toString());
//                                        load_fragment("due", getResString("lbl_inspctndue_txt"), "myassets", tv3.getText().toString());
//                                else {
//                                    load_fragment("over", getResString("lbl_main_ovdue_st"), "maintenanceDue", tv3.getText().toString());
//                                }
                        }
                    }
                });

        ((ViewGroup) tv4.getParent()).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (tv4.getText().toString().equals("0")) {
                            show_snak(getActivity(), getResString("lbl_noOvrdue_dta_msg"));
                        } else {
//                            if (group_id.equals("9") || page_type.equals("inspection"))
                            dueFragment("over", getResString("lbl_inspctndue_txt"), "myassets", tv4.getText().toString());
                        }
                    }
                });
    }

    // loade due Fragment
    public void dueFragment(String filter, String page_name, String page_type, String total) {
        Group_Filter_fragment group_filter_fragment = new Group_Filter_fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("page_type", page_type);
        bundle1.putString("group_userId", user.getUacc_id());
        bundle1.putString("group_id", user.getUacc_group_fk());
        bundle1.putString("role_id", user.getCgrp_id());
        bundle1.putString("filter", filter);
        bundle1.putString("total", total);
        group_filter_fragment.setArguments(bundle1);
        LoadFragment.replace(group_filter_fragment, baseActivity, page_name);
    }

    private ImageView profil_pic;
    private TextView usr_name, email_tv, dsc_text;

    GridLayout product_hstry_lay;
    TextView tp1, tp2, tp3, tp4, tv1, tv2, tv3, tv4;

    public void findAllIds(View view) {
        profil_pic = view.findViewById(R.id.profil_pic);
        usr_name = view.findViewById(R.id.usr_name);
        email_tv = view.findViewById(R.id.email_tv);
        dsc_text = view.findViewById(R.id.dsc_text);
        product_hstry_lay = view.findViewById(R.id.product_hstry_lay);

        tp1 = view.findViewById(R.id.tp1);
        tp2 = view.findViewById(R.id.tp2);
        tp3 = view.findViewById(R.id.tp3);
        tp4 = view.findViewById(R.id.tp4);

        tv1 = view.findViewById(R.id.tv1);
        tv2 = view.findViewById(R.id.tv2);
        tv3 = view.findViewById(R.id.tv3);
        tv4 = view.findViewById(R.id.tv4);

        tp2.setText(getResString("lbl_inspctd_st"));
        tp3.setText(getResString("lbl_inspctndue_txt"));
        tp4.setText(getResString("lbl_ins_ovrdue_st"));
        if (user.getUacc_group_fk().equals("18")) {
            tp1.setText(getResString("lbl_my_sites"));
            tp4.setText(getResString("lbl_balance"));
        }
        ((ViewGroup) tp4.getParent()).setVisibility(VISIBLE);
    }

    public void get_sites_status(String url) {
        url = url + "&client_id=" + client_id;
        if (isNetworkAvailable(getActivity())) {
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
                            if (jsonObject.has("msg_code")) {
                                if (jsonObject.getString("msg_code").equals("200")) {
                                    product_hstry_lay.setVisibility(VISIBLE);
                                    JSONObject data = jsonObject.getJSONObject("data");
                                    tv1.setText(data.getString("product_data_count"));
                                    tv2.setText(data.getString("inspection_inspected"));
                                    tv3.setText(data.getString("inspection_due"));
                                    tv4.setText(data.getString("inspection_over_due"));
                                }
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
