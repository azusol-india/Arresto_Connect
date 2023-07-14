package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
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
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.data.TreeNode;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static app.com.arresto.arresto_connect.constants.Static_values.nested_catgrs;
import static app.com.arresto.arresto_connect.constants.Static_values.treeNodes;

public class EC_LinePager extends EC_Base_Fragment {
    View view;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.reports_pager, parent, false);
            findAllIds(view);
            get_ECCategories();
        } else {
//            onback pressed
            if (myPagerAdapter != null && myPagerAdapter.db_fragment != null) {
                myPagerAdapter.db_fragment.update_selected_list();
                linePager.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        linePager.setCurrentItem(0, true);
                    }
                }, 100);
            }
            if (nested_catgrs.size() > 0)
                nested_catgrs.remove(0);
        }
        return view;
    }

    ViewPager linePager;
    MyPagerAdapter myPagerAdapter;
    CharSequence Titles[] = {"New Sub Sites", "Submitted Sub Sites"};
    TabLayout tabLayout;

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
        linePager = view.findViewById(R.id.wrk_histryPager);
        tabLayout.setupWithViewPager(linePager);
    }

    public void set_fragment(Activity cntxt) {
        FragmentActivity activity = (FragmentActivity) cntxt;
        myPagerAdapter = new MyPagerAdapter(activity.getSupportFragmentManager(), Titles, Titles.length);
        linePager.setAdapter(myPagerAdapter);
    }


    private void get_ECCategories() {
        String url = All_Api.ec_category;
        Log.e("email id url", "" + url);

        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data_object = new JSONObject(response);
                    if (data_object.getString("status_code").equalsIgnoreCase("200")) {
                        JSONArray jsonArray = data_object.getJSONArray("data");
                        ArrayList<Category_Model> category_models = new ArrayList<>();
                        ArrayList<String> cat_name = new ArrayList<>();
                        treeNodes = new HashMap<>();
                        TreeNode root = new TreeNode<>("0", new Category_Model());
                        treeNodes.put("0", root);
                        for (int k = 0; k < jsonArray.length(); k++) {
                            Category_Model category_model = new Gson().fromJson(jsonArray.getString(k), Category_Model.class);
                            cat_name.add(category_model.getCat_name());
                            category_models.add(category_model);
                            treeNodes.put(category_model.getId(), new TreeNode<>(category_model.getId(), category_model));
                        }
                        make_tree(category_models);
                        set_fragment(getActivity());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {

            }
        });

    }


    private void make_tree(ArrayList<Category_Model> category_models) {
        for (Category_Model category_model : category_models) {
            if (treeNodes.containsKey(category_model.getCat_parentid())) {
                treeNodes.get(category_model.getCat_parentid()).addChild(category_model.getId(), category_model);
            }
        }
//        ArrayList<Category_Model> categoryModels=  get_childs("0", "0");

    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {
        int tabCount;
        CharSequence Titles[];
        EC_Linelist db_fragment = EC_Linelist.newInstance("db_lines");

        MyPagerAdapter(FragmentManager fm, CharSequence mTitles[], int tabCount) {
            super(fm);
            this.Titles = mTitles;
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int pos) {
            switch (pos) {
                case 0:
                    return db_fragment;
                case 1:
                default:
                    return EC_Linelist.newInstance("submitted_lines");
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
