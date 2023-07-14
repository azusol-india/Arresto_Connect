package app.com.arresto.arresto_connect.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.HomeModel;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.activity.LoginActivity;
import app.com.arresto.arresto_connect.ui.modules.inspection.thermal.ThermalImageProcessing;
import app.com.arresto.arresto_connect.ui.modules.kowledge_tree.Karam_infonet_frg;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.IS_LOGIN;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.IS_WELCOME_DONE;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.directory;
import static app.com.arresto.arresto_connect.constants.Static_values.group_id;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;

public class Home_Fragment extends Base_Fragment implements View.OnClickListener {

    View view;
    LinearLayout root_layer, rgister_lay, my_ast_lay, shedulr_lay;
    DataHolder_Model dataHolder_model;
    ImageView logo_img;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.home_fragment, parent, false);
            setupUI(view, baseActivity);
            dataHolder_model = DataHolder_Model.getInstance();
//            all_Ids();
            root_layer = view.findViewById(R.id.root_layer);
            logo_img = view.findViewById(R.id.logo_img);
            view.findViewById(R.id.kdt_lay).setOnClickListener(this);
            shedulr_lay = view.findViewById(R.id.shedulr_lay);
            my_ast_lay = view.findViewById(R.id.my_ast_lay);
            rgister_lay = view.findViewById(R.id.rgister_lay);
            shedulr_lay.setOnClickListener(this);
            rgister_lay.setOnClickListener(this);
            my_ast_lay.setOnClickListener(this);
            view.findViewById(R.id.dashbrd_lay).setOnClickListener(this);
            view.findViewById(R.id.logout_lay).setOnClickListener(this);
            view.findViewById(R.id.profile_lay).setOnClickListener(this);
            view.findViewById(R.id.msg_lay).setOnClickListener(this);
            ((CheckBox) view.findViewById(R.id.show_chk)).setChecked(mPrefrence.getBoolean(IS_WELCOME_DONE));
            ((CheckBox) view.findViewById(R.id.show_chk)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    mPrefrence.saveBoolean(IS_WELCOME_DONE, isChecked);
                }
            });

            if (group_id.equals("9")) {
                ((LinearLayout) shedulr_lay.getParent()).setWeightSum(3);
                shedulr_lay.setVisibility(View.VISIBLE);
                rgister_lay.setVisibility(View.GONE);
                my_ast_lay.setVisibility(View.GONE);
            } else if (group_id.equals("19")) {
                if (!logo_url.equals("")) {
                    load_image_file(logo_url, logo_img);
                }
                ((LinearLayout) rgister_lay.getParent()).setVisibility(View.GONE);
                logo_img.setVisibility(View.VISIBLE);
            }


            if (dataHolder_model.getHomeModels() == null || dataHolder_model.getHomeModels().size() < 1)
                fetch_data(All_Api.home_data + client_id);
            else
                set_Hometext(dataHolder_model.getHomeModels());
            show_showCase();
            return view;
        } else {
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        ConstantMethods.find_pageVideo(baseActivity, "Landing Page - Welcome");
        ConstantMethods.find_pageVideo(baseActivity, "welcome");
    }

    public void show_showCase() {
        if (group_id.equals("19")) {
            ArrayList<String> mesages = new ArrayList<>(Arrays.asList(
                    getResString("lbl_click_on_alerts_to_view_updates"),
                    getResString("lbl_click_to_open_your_profile_details"),
                    getResString("lbl_click_here_to_logout")));
            ArrayList<View> views = new ArrayList<>(Arrays.asList(view.findViewById(R.id.msg_lay), view.findViewById(R.id.profile_lay), view.findViewById(R.id.logout_lay)));
            Add_Showcase.getInstance(baseActivity).setData(mesages, views);
        } else {
            ArrayList<String> mesages = new ArrayList<>(Arrays.asList(getResString("lbl_knowledgetree_ppe"),
                    getResString("lbl_click_dashboard_to_start"),
                    getResString("lbl_register_for_inspection_updates"),
                    getResString("lbl_assets_status_ppe"),
                    getResString("lbl_your_scheduled_products"),
                    getResString("lbl_click_on_alerts_to_view_updates"),
                    getResString("lbl_click_to_open_your_profile_details"),
                    getResString("lbl_click_here_to_logout")));
            ArrayList<View> views = new ArrayList<>(Arrays.asList(view.findViewById(R.id.kdt_lay), view.findViewById(R.id.dashbrd_lay), view.findViewById(R.id.rgister_lay)
                    , view.findViewById(R.id.my_ast_lay), view.findViewById(R.id.shedulr_lay), view.findViewById(R.id.msg_lay), view.findViewById(R.id.profile_lay), view.findViewById(R.id.logout_lay)));
            Add_Showcase.getInstance(baseActivity).setData(mesages, views);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logout_lay:
                Intent intent = new Intent(homeActivity, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mPrefrence.saveBoolean(IS_LOGIN, false);
                break;
            case R.id.profile_lay:
                LoadFragment.replace(new My_profile_frg(), homeActivity, getResString("lbl_profile"));
                break;
            case R.id.msg_lay:
                LoadFragment.replace(new Notification_frag(), homeActivity, getResString("lbl_message_txt"));
                break;
            case R.id.kdt_lay:
                LoadFragment.replace(new Karam_infonet_frg(), homeActivity, getResString("lbl_knowledge_tree"));
                break;
            case R.id.dashbrd_lay:
                homeActivity.load_home_fragment(false);
                break;
            case R.id.my_ast_lay:
                homeActivity.submit_action("myAsset");
                break;
            case R.id.shedulr_lay:
                homeActivity.submit_action("schedule");
                break;
            case R.id.rgister_lay:
                homeActivity.submit_action("register");
                break;
        }
    }

    public void fetch_data(final String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        dataHolder_model.setHomeModels(new ArrayList<>(Arrays.asList(new Gson().fromJson(object.getString("data"), HomeModel[].class))));
                        set_Hometext(dataHolder_model.getHomeModels());
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

    private void set_Hometext(ArrayList<HomeModel> text_data) {
        root_layer.removeAllViews();
        for (HomeModel homeModel : text_data) {
            View view = LayoutInflater.from(baseActivity).inflate(R.layout.home_lay_item, null);
            TextView heading_tv = view.findViewById(R.id.heading_tv);
            TextView text_tv = view.findViewById(R.id.text_tv);
            heading_tv.setText(Html.fromHtml(homeModel.getPage()));
            text_tv.setText(Html.fromHtml(homeModel.getContent()));
            root_layer.addView(view);
        }
    }
}
