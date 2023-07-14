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

package app.com.arresto.arresto_connect.ui.modules.kowledge_tree;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.share_text;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import app.com.arresto.Flavor_Id.FlavourInfo;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.Font_type;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.showcase_library.Showcase;
import app.com.arresto.arresto_connect.ui.adapters.ViewPagerAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Dealer_detailFragment;
import app.com.arresto.arresto_connect.ui.fragments.Fullscreenview;
import app.com.arresto.arresto_connect.ui.fragments.Gallery_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.Report_webview;

public class Karam_infonet_product_frg extends KnowledgeTreeBase implements View.OnClickListener {
    View view;
    ViewPager karam_info_pager;
    ArrayList<String> image_url;
    RelativeLayout info_btn;
    TextView prdct_name_tv;
    int current_pos;

    ArrayList<String> title, file_url, gallary_url;

    private AnimatedVectorDrawableCompat mMenuDrawable, mBackDrawable;
    String category_id = "", manageId;
    ArrayList<Product_model> product_models;
    String client_id = Static_values.client_id;
    String api_server = "";

    @Override
    public View BaseView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (connected_clientsModel != null) {
            client_id = connected_clientsModel.getClient_id();
            api_server = connected_clientsModel.getApi_server();
        }
        if (view == null) {
            if (getArguments() != null) {
                current_pos = getArguments().getInt("pos", -1);
                manageId = getArguments().getString("manageId", "");
                product_models = new ArrayList<>(Arrays.asList(new Gson().fromJson(getArguments().getString("products"), Product_model[].class)));
                category_id = getArguments().getString("category_id", "");
                Log.e("image_url ", " 6ty " + image_url);
            }
            view = inflater.inflate(R.layout.karam_info_product_frg, container, false);
            ConstantMethods.find_pageVideo(getActivity(), "knowledgetree product");
            setdata_for_knowledge_tree();
            closeFABMenu();
            return view;
        } else {
            return view;
        }
    }


    RelativeLayout ad_crt_btn, share_btn;
    TextView disc_txt;
    ViewPagerAdapter karam_pagerAdapter;

    public void setdata_for_knowledge_tree() {
        Product_model model = product_models.get(current_pos);
        get_service_data(model.getType(), model.getProduct_code(), current_pos);
        karam_info_pager = view.findViewById(R.id.karam_info_pager);
        prdct_name_tv = view.findViewById(R.id.prdct_name_tv);
        disc_txt = view.findViewById(R.id.disc_txt);
        ad_crt_btn = view.findViewById(R.id.ad_crt_btn);
        info_btn = view.findViewById(R.id.info_btn);
        if (client_id.equalsIgnoreCase("376"))
            info_btn.setVisibility(View.GONE);
        share_btn = view.findViewById(R.id.share_btn);
        image_url = new ArrayList<>();
        for (Product_model product_model : product_models) {
            image_url.add(product_model.getProduct_imagepath());
        }
        karam_pagerAdapter = new ViewPagerAdapter(getActivity(), image_url);
        karam_info_pager.setAdapter(karam_pagerAdapter);
        karam_info_pager.setCurrentItem(current_pos);

        prdct_name_tv.setText(model.getProduct_code());
        disc_txt.setText(model.getProduct_description());

        karam_info_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                Product_model model = product_models.get(position);
                prdct_name_tv.setText(model.getProduct_code());
                disc_txt.setText(model.getProduct_description());
                img_url = model.getProduct_imagepath();
                get_service_data(model.getType(), model.getProduct_code(), position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        fab_menu = view.findViewById(R.id.fab_menu);
        fab_menu_lay1 = view.findViewById(R.id.fab_menu_lay1);
        fab_menu_lay = view.findViewById(R.id.fab_menu_lay);
        add_bt = view.findViewById(R.id.add_bt);
        add_bt.setBackgroundTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getBtn_bg_clr()));

        fab = view.findViewById(R.id.fab);
        fab0 = view.findViewById(R.id.fab0);
        fab1 = view.findViewById(R.id.fab1);
        fab2 = view.findViewById(R.id.fab2);
        fab3 = view.findViewById(R.id.fab3);
        fab4 = view.findViewById(R.id.fab4);
        fab5 = view.findViewById(R.id.fab5);
        md_layer = view.findViewById(R.id.md_layer);

        mMenuDrawable = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.ic_menu_animatable);
        mBackDrawable = AnimatedVectorDrawableCompat.create(getActivity(), R.drawable.ic_back_animatable);

        fab_menu_lay1.setOnClickListener(this);
        add_bt.setOnClickListener(this);
        ad_crt_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        info_btn.setOnClickListener(this);
        transition = (TransitionDrawable) fab_menu_lay.getBackground();

        karam_info_pager.postDelayed(new Runnable() {
            @Override
            public void run() {
                Activity activity = getActivity();
                if (activity != null) {
                    Showcase.from(activity)
                            .setContentView(R.layout.showcase_sample)
                            .on(R.id.add_bt) //a view in actionbar
                            .addCircle()
                            .withBorder()
                            .onClick(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
//                                    show_showCase();
                                }
                            })
                            .on(R.id.karam_info_pager)
                            .displaySwipableLeftRight()
                            .showOnce("0");
                }
            }
        }, 1000);
        mesages = new ArrayList<>(Arrays.asList("To view product specifications like video , image ,technical data sheet ,user manual certificate and presentation."));
        views = new ArrayList<View>(Arrays.asList(add_bt));

    }

    String img_url;
    ArrayList<String> mesages;
    ArrayList<View> views;

    ArrayList<String> media_title, media_url;

    private void get_service_data(String product_type, String product_name, final int pos) {
        title = new ArrayList<>();
        file_url = new ArrayList<>();
        gallary_url = new ArrayList<>();
        media_title = new ArrayList<>();
        media_url = new ArrayList<>();
        String url;
        if (!api_server.equals(""))
            url =  api_server + "/api_controller/assetValues?type=";
        else
            url = All_Api.assetValues;
        url = url + product_type + "&client_id=" + client_id + "&typeCode=" + product_name + "&user_id=" + user_id + "&manage_product_id=" + manageId + "&geo_location=" + baseActivity.curr_lat + "," + baseActivity.curr_lng;

        url = url.replaceAll(" ", "%20");
        url = url.replaceAll("\\+", "%2B");
        Log.e("email id url", "" + url);

        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("error", "" + response);
                try {
                    JSONObject dataObject = new JSONObject(response);
                    if (img_url == null || img_url.equals("")) {
                        img_url = dataObject.getString("imagePath");
                        image_url.set(pos, img_url);
                        karam_pagerAdapter.update(image_url);
                    }
                    if (dataObject.has("description"))
                        disc_txt.setText("" + dataObject.getString("description"));
                    JSONArray jsonArray = dataObject.getJSONArray("file");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        title.add(jsonObject.getString("title"));
                        file_url.add(jsonObject.getString("url"));
                    }

                    JSONArray gallary_arr = dataObject.getJSONArray("gallery_images");
                    for (int i = 0; i < gallary_arr.length(); i++) {
                        JSONObject jsonObject = gallary_arr.getJSONObject(i);
                        gallary_url.add(jsonObject.getString("url"));
                    }
                    if (dataObject.has("category_media") && !dataObject.getString("category_media").equals("")) {
                        JSONArray mediaArray = dataObject.getJSONArray("category_media");
                        for (int i = 0; i < mediaArray.length(); i++) {
                            JSONObject jsonObject = mediaArray.getJSONObject(i);
                            media_title.add(jsonObject.getString("title"));
                            media_url.add(jsonObject.getString("url"));
                        }
                    }

                    buNow_url = dataObject.getString("buynow_url");
                    youtube_url = dataObject.getString("youtube_url");
                    dec_of_conformity = dataObject.getString("dec_of_conformity");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "is " + e.getMessage());
                }

                check_data_for_treemenu();

            }

            @Override
            public void onError(String message) {

            }
        });
    }

    String buNow_url, youtube_url, dec_of_conformity;

    boolean doc_available;

    private void check_data_for_treemenu() {
        set_botmmenu_gray_img();
        set_listnr();
        doc_available = false;
        int bgcolor = getResColor(R.color.app_text);
        int textcolor = getResColor(R.color.app_background);
        if (file_url.size() > 0) {
            if (!file_url.get(0).equals("")) {
                set_data(fab, bgcolor, textcolor);
                gallary_url.add(file_url.get(0));
                doc_available = true;
            }
            if (!youtube_url.equals("")) {
                set_data(fab0, bgcolor, textcolor);
                doc_available = true;
            }
            if (!file_url.get(1).equals("")) {
                set_data(fab1, bgcolor, textcolor);
                doc_available = true;
            }
            if (!file_url.get(2).equals("")) {
                set_data(fab2, bgcolor, textcolor);
                doc_available = true;
            }
            if (!file_url.get(3).equals("")) {
                set_data(fab3, bgcolor, textcolor);
                doc_available = true;
            }
            if (!file_url.get(4).equals("")) {
                set_data(fab4, bgcolor, textcolor);
                doc_available = true;
            }
            if (dec_of_conformity != null && !dec_of_conformity.equals("")) {
                set_data(fab5, bgcolor, textcolor);
                doc_available = true;
            }
        }

        if (media_title.size() > 0) {
            md_layer.removeAllViews();
            for (int i = 0; i < media_title.size(); i++) {
                View dly = LayoutInflater.from(baseActivity).inflate(R.layout.media_item, null);
                md_layer.addView(dly);
                final TextView item_tv = dly.findViewById(R.id.item_tv);
                item_tv.setText(media_title.get(i));
                int finalI = i;
                dly.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        load_full_screen_view(new ArrayList<>(Collections.singletonList(media_url.get(finalI))), 0, "Media");
                    }
                });
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.info_btn:
//                baseActivity.fetch_dealer_data();
                LoadFragment.replace(Dealer_detailFragment.newInstance(null, "contact"), baseActivity, getResString("lbl_contact_us"));
                break;
            case R.id.fab_menu_lay1:
                if (isFabOpen)
                    move();
                break;
            case R.id.add_bt:
                if (doc_available)
                    move();
                else
                    show_snak(getActivity(), "Document data not available.");
                break;
            case R.id.fab:
                LoadFragment.replace(Gallery_Fragment.newInstance(gallary_url), getActivity(), getResString("lbl_prdct_img_st"));
//                load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(0))), 0, getResString("prdct_img_st"));
                break;
            case R.id.fab0:
                Report_webview report_webview00 = new Report_webview();
                Bundle bundle00 = new Bundle();
                bundle00.putString("url", youtube_url.replace(" ", "%20"));
                report_webview00.setArguments(bundle00);
                LoadFragment.replace(report_webview00, getActivity(), "Video");
//                Intent intent = new Intent(getActivity(), Video_Fragment.class);
//                intent.putExtra("video_id", extractYTId(youtube_url));
//                getActivity().startActivity(intent);
                break;
            case R.id.fab1:
                load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(1))), 0, getResString("user_manul_st"));
                break;
            case R.id.fab2:
                load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(2))), 0, getResString("lbl_technical_data_sheet"));
                break;
            case R.id.fab3:
                load_full_screen_view(new ArrayList<>(Collections.singletonList(file_url.get(3))), 0, getResString("lbl_test_report"));
                break;
            case R.id.fab4:
                Report_webview report_webview0 = new Report_webview();
                Bundle bundle0 = new Bundle();
                bundle0.putString("url", file_url.get(4));
                report_webview0.setArguments(bundle0);
                LoadFragment.replace(report_webview0, getActivity(), getResString("lbl_presentation"));
//                load_full_screen_view(new ArrayList<>(Collections.singletonList("https://docs.google.com/viewer?url="+file_url.get(4))), 0, getResString("prsntsn_st"));
                break;
            case R.id.fab5:
                load_full_screen_view(new ArrayList<>(Collections.singletonList(dec_of_conformity)), 0, getResString("lbl_dec_of_conformity"));
                break;
            case R.id.share_btn:
                if (!category_id.equals("")) {
                    String share_url = FlavourInfo.Base + getString(R.string.link_prefix) + "/kare/knowledgetree/ktview/" + category_id + "/" + prdct_name_tv.getText();
                    share_text(baseActivity, share_url);
//                    shareImage(getActivity(), getfile(getScreenShot(view), "" + product_models.get(karam_info_pager.getCurrentItem()).getProduct_code() + ".jpg"));
                }
                break;
            case R.id.ad_crt_btn:
                if (buNow_url != null && !buNow_url.equals("")) {
                    Report_webview report_webview1 = new Report_webview();
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("url", buNow_url);
                    report_webview1.setArguments(bundle1);
                    LoadFragment.replace(report_webview1, getActivity(), getResString("lbl_ad_tocart_st"));
                } else {
                    AppUtils.show_snak(getActivity(), "Please try again after some time.");
                }
                break;
        }
    }


    public void load_full_screen_view(ArrayList<String> imgurls, int position, String tag) {
        Fullscreenview fullscreenview = new Fullscreenview();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("imgurl", imgurls);
        bundle.putInt("pos", position);
        fullscreenview.setArguments(bundle);
        LoadFragment.replace(fullscreenview, getActivity(), tag);
    }

    public void set_data(LinearLayout lay, int bg_color, int txt_color) {
        TextView textView = (TextView) lay.getChildAt(0);
        textView.setTextColor(txt_color);
        Font_type.set_background(textView, bg_color, "round_bg");
        lay.setClickable(true);
    }

    public void set_listnr() {
        fab.setOnClickListener(this);
        fab0.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);
        fab5.setOnClickListener(this);

        fab.setClickable(false);
        fab0.setClickable(false);
        fab1.setClickable(false);
        fab2.setClickable(false);
        fab3.setClickable(false);
        fab4.setClickable(false);
        fab5.setClickable(false);
    }


    public void set_botmmenu_gray_img() {
        int textcolor = getResColor(R.color.app_text);
        int bgcolor = getResColor(R.color.app_background);
        set_data(fab, bgcolor, textcolor);
        set_data(fab0, bgcolor, textcolor);
        set_data(fab1, bgcolor, textcolor);
        set_data(fab2, bgcolor, textcolor);
        set_data(fab3, bgcolor, textcolor);
        set_data(fab4, bgcolor, textcolor);
        set_data(fab5, bgcolor, textcolor);
    }

    LinearLayout fab_menu_lay, fab_menu_lay1, fab, fab0, fab1, fab2, fab3, fab4, fab5, md_layer;
    ScrollView fab_menu;
    TransitionDrawable transition;
    FloatingActionButton add_bt;
    Boolean isFabOpen = false;

    private void move() {
        if (!isFabOpen) {
            transition.startTransition(400);
            showFABMenu();
            add_bt.setImageDrawable(mMenuDrawable);
            mMenuDrawable.start();
        } else {
            transition.reverseTransition(400);
            closeFABMenu();
            add_bt.setImageDrawable(mBackDrawable);
            mBackDrawable.start();
        }
    }

    private void closeFABMenu() {
        isFabOpen = false;
        fab.animate().translationY(getResources().getDimension(R.dimen.size_55));
        fab0.animate().translationY(getResources().getDimension(R.dimen.size_55));
        fab1.animate().translationY(getResources().getDimension(R.dimen.size_105));
        fab2.animate().translationY(getResources().getDimension(R.dimen.size_155));
        fab3.animate().translationY(getResources().getDimension(R.dimen.size_155));
        fab4.animate().translationY(getResources().getDimension(R.dimen.size_155));
        fab5.animate().translationY(getResources().getDimension(R.dimen.size_155));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisiblity(4);
                fab_menu_lay1.setBackgroundColor(Color.parseColor("#00000000"));
            }
        }, 200);
    }

    private void showFABMenu() {
        setVisiblity(0);
        isFabOpen = true;
        fab_menu.animate().translationY(0);
        fab_menu_lay.animate().translationY(0);
        fab.animate().translationY(0);
        fab0.animate().translationY(0);
        fab1.animate().translationY(0);
        fab2.animate().translationY(0);
        fab3.animate().translationY(0);
        fab4.animate().translationY(0);
        fab5.animate().translationY(0);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fab_menu_lay1.setBackgroundColor(Color.parseColor("#98000000"));
            }
        }, 200);
    }

    public void setVisiblity(int visibility) {
        fab_menu.setVisibility(visibility);
        fab_menu_lay.setVisibility(visibility);
        fab.setVisibility(visibility);
        fab0.setVisibility(visibility);
        fab1.setVisibility(visibility);
        fab2.setVisibility(visibility);
        fab3.setVisibility(visibility);
        fab4.setVisibility(visibility);
        fab5.setVisibility(visibility);
    }
}
