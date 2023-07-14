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


import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Reports_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.third_party.showcase_library.Add_Showcase;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionListItems;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.currently_inspected;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;

public class Close_projectFragment extends Fragment implements Reports_list.onRefreshListner {
    View view;
    String url;
    String type;
    public static int index;
    public static boolean need_refresh = false;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.reports_pager, container, false);
            findAllIds(view);
            if (getArguments() != null) {
                type = getArguments().getString("id", "");
                index = getArguments().getInt("index", 0);
                if (!client_id.equals("419") && index > 1) {
                    index = index - 1;
                }
                switch (type) {
                    case "warehouse_report":
                    case "report":
                        ConstantMethods.find_pageVideo(getActivity(), "inspection reports");
                        url = All_Api.inspectorInspectionList + Static_values.user_id + "&groupID=" + Static_values.group_id + "&client_id=" + client_id + "&cgrp_id=" + role_id;
                        break;
                    case "history_report":
                        url = getArguments().getString("history_url");
                        break;
                    case "pdm_report":
                        ConstantMethods.find_pageVideo(getActivity(), "pm reports");
                        url = getArguments().getString("pdm_url");
                        break;
                    case "asm_report":
                        url = getArguments().getString("asm_url");
                        tabLayout.setVisibility(View.GONE);
                        break;
                    case "safety":
                        url = getArguments().getString("safety_url");
//                        tabLayout.setVisibility(View.GONE);
                        break;
                    case "preuse":
                        url = getArguments().getString("preuse_url");
                        break;
                    default:
                        url = All_Api.inspector_mngsite + Static_values.user_id + "&group_id=" + Static_values.group_id;
                        break;
                }
            }

            InspectionListItems.selectedPosition.clear();
            serviceCall_1();
            return view;
        } else {
            if (need_refresh) {
                serviceCall_1();
                need_refresh = false;
            }
            return view;
        }
    }

    ViewPager wrk_histryPager;
    MyPagerAdapter myPagerAdapter;
    CharSequence Titles[] = {getResString("lbl_pending"), getResString("lbl_approved"), getResString("lbl_rejected")};
    CharSequence thermalTitles[] = {getResString("lbl_pending"), getResString("lbl_waiting_for_replace"), getResString("lbl_approved"), getResString("lbl_rejected")};
    CharSequence preuseTitles[] = {getResString("lbl_ok_st"), getResString("lbl_not_ok")};
    TabLayout tabLayout;

    private void show_showCase() {
        ArrayList<String> mesages = new ArrayList<>(Arrays.asList(
                getResString("lbl_pending_report"),
                getResString("lbl_replace_report"),
                getResString("lbl_approved_report"),
                getResString("lbl_rejected_report")));
//        ArrayList<View> views = new ArrayList<>(Arrays.asList(((ViewGroup) tabLayout.getChildAt(0)).getChildAt(0), ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(1),  ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(2)));
        ArrayList<View> views = new ArrayList<>();
        LinearLayout tabStrip = (LinearLayout) tabLayout.getChildAt(0);
        for (int i = 0; i < tabStrip.getChildCount(); i++) {
            View tabView = tabStrip.getChildAt(i);
            switch (i) {
                case 0:
                    tabView.setId(R.id.pending);
                    break;
                case 1:
                    tabView.setId(R.id.replaced);
                    break;
                case 2:
                    tabView.setId(R.id.approved);
                    break;
                case 3:
                    tabView.setId(R.id.rejected);
                    break;
            }
            if (tabView != null) {
                views.add(tabView);
            }
        }

        if (!client_id.equals("419")){
            mesages.remove(1);
            views.remove(1);
        }
        Add_Showcase.getInstance(getActivity()).setData(mesages, views);
    }

    public void findAllIds(View view) {
        tabLayout = view.findViewById(R.id.tabLayout);
        View root = tabLayout.getChildAt(0);
        if (root instanceof LinearLayout) {
            ((LinearLayout) root).setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setColor(getResources().getColor(R.color.app_background));
            drawable.setSize(1, 1);
            ((LinearLayout) root).setDividerDrawable(drawable);
        }
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        wrk_histryPager = view.findViewById(R.id.wrk_histryPager);
        tabLayout.setupWithViewPager(wrk_histryPager);

    }

    public void set_fragment(Activity cntxt) {
        FragmentActivity activity = (FragmentActivity) cntxt;
        if (type.equals("preuse"))
            myPagerAdapter = new MyPagerAdapter(activity.getSupportFragmentManager(), preuseTitles, preuseTitles.length);
        else if (client_id.equals("419"))
            myPagerAdapter = new MyPagerAdapter(activity.getSupportFragmentManager(), thermalTitles, thermalTitles.length);
        else
            myPagerAdapter = new MyPagerAdapter(activity.getSupportFragmentManager(), Titles, Titles.length);
        wrk_histryPager.setAdapter(myPagerAdapter);
        wrk_histryPager.setCurrentItem(index);

        if (!currently_inspected && tabLayout.getVisibility() == View.VISIBLE) {
            tabLayout.post(new Runnable() {
                @Override
                public void run() {
                    show_showCase();
                }
            });
        }
    }

    ArrayList<Reports_model> approved, rejected, pending, replaced;

    public void serviceCall_1() {
        approved = new ArrayList<>();
        rejected = new ArrayList<>();
        pending = new ArrayList<>();
        replaced = new ArrayList<>();
        url = url + "&cgrp_id=" + Static_values.role_id;
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    ArrayList<Reports_model> reportsModels = new ArrayList<>();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);

                        if (jsonObject.has("success")) {
                            if (jsonObject.getString("success").equals("No data Found")) {
                                AppUtils.show_snak(getActivity(), jsonObject.getString("success"));
                            }
                        } else {
                            if (jsonObject.getString("status_code").equals("200")) {
                                reportsModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data"), Reports_model[].class)));
//                                set_data(reportsModels);
                            } else {
                                AppUtils.show_snak(getActivity(), jsonObject.getString("message"));
                            }
                        }
                    } else {
                        reportsModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(response, Reports_model[].class)));
                    }
                    set_data(reportsModels);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {

            }
        });


    }

    void set_data(ArrayList<Reports_model> data) {
        for (Reports_model report : data) {
            switch (report.getApproved_status().toLowerCase()) {
                case "approved":
                    approved.add(report);
                    break;
                case "pending":
                    pending.add(report);
                    break;
                case "replace":
                    replaced.add(report);
                    break;
                default:// reject
                    rejected.add(report);
                    break;
            }
        }
        set_fragment(getActivity());
    }

    @Override
    public void onRefresh() {
        serviceCall_1();
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        int tabCount;
        CharSequence Titles[];

        MyPagerAdapter(FragmentManager fm, CharSequence mTitles[], int tabCount) {
            super(fm);
            this.Titles = mTitles;
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int pos) {
            if (type.equals("preuse")) {
                switch (pos) {
                    case 0:
                        return Reports_list.newInstance(type, approved, Close_projectFragment.this);
                    case 1:
                        return Reports_list.newInstance(type, rejected, Close_projectFragment.this);
                    default:
                        return null;
                }
            } else if (client_id.equals("419")) {
                switch (pos) {
                    case 0:
                        return Reports_list.newInstance(type, pending, Close_projectFragment.this);
                    case 1:
                        return Reports_list.newInstance(type, replaced, Close_projectFragment.this);
                    case 2:
                        return Reports_list.newInstance(type, approved, Close_projectFragment.this);
                    case 3:
                        return Reports_list.newInstance(type, rejected, Close_projectFragment.this);
                    default:
                        return null;
                }
            } else {
                switch (pos) {
                    case 0:
                        return Reports_list.newInstance(type, pending, Close_projectFragment.this);
                    case 1:
                        return Reports_list.newInstance(type, approved, Close_projectFragment.this);
                    case 2:
                        return Reports_list.newInstance(type, rejected, Close_projectFragment.this);
                    default:
                        return null;
                }
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Titles[position];
        }

        @Override
        public int getCount() {
            return tabCount;
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

}
