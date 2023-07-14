/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.PaginationListener;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Filter_GroupModel;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Filter_GroupAdapter;
import app.com.arresto.arresto_connect.ui.adapters.Inspector_search_listadapter;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PaginationListener.PAGE_START;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.network.All_Api.search_register_assets;

public class Group_Filter_fragment extends Base_Fragment {
    RecyclerView group_list, data_list;
    View view;
    TextView ttl_tv, blank_tv;
    Filter_GroupAdapter filter_groupAdapter;
    String page_type = "myassets";
    EditText search_view;
    TextView dwn_excl_btn;
    SwipeRefreshLayout swipe;
    String group_url;
    String search_url = "";
    String group_userId;
    String total_size = "";
    String filter = "";
    String group_id = "";
    String role_id = "";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.groups_list, parent, false);
            setupUI(view, getActivity());
            find_id();
            groups = new ArrayList<>();
            group_list.setAdapter(filter_groupAdapter);
            group_url = All_Api.get_group_assets + Static_values.client_id + "&filter=" + filter + "&user_id=" + Static_values.user_id;
            if (getArguments() != null) {
                page_type = getArguments().getString("page_type");
                if (page_type.equals("warehouseOverdue")) {
                    ArrayList<Site_Model> site_models = new ArrayList<>(Arrays.asList(baseActivity.getGson().fromJson(getArguments().getString("sites"), Site_Model[].class)));
                    setupList(site_models);
                    ((ViewGroup)search_view.getParent()).setVisibility(View.GONE);
                } else {
                    group_userId = getArguments().getString("group_userId", "");
                    filter = getArguments().getString("filter", "");
                    role_id = getArguments().getString("role_id", "");
                    group_id = getArguments().getString("group_id", "");
                    update_list(group_userId);
                    if (group_userId.equals("")) {
                        search_url = search_register_assets + client_id + "&user_id=" + Static_values.user_id + "&search=";
                    } else {
                        search_url = search_register_assets + client_id + "&user_id=" + group_userId + "&search=";
                        group_url = All_Api.get_group_assets + Static_values.client_id + "&filter=" + filter + "&user_id=" + group_userId;
                        getSiteData(group_url);
                    }
                    if (getArguments().containsKey("total"))
                        total_size = getArguments().getString("total", "");
                    group_name = this.getTag();
                }
            }
            enableSearch = true;
            dwn_excl_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (inspector_search_listadapter != null && inspector_search_listadapter.getData().size() > 0)
                        AppUtils.createExcelSheet(getActivity(), group_name, inspector_search_listadapter.getData());
                    else
                        AppUtils.show_snak(getActivity(), "No data to import.");
                }
            });
            search_view.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String newText = editable.toString();
                    if (TextUtils.isEmpty(newText) || newText.length() < 3) {
                        if (inspector_search_listadapter != null && enableSearch)
                            data_list.setAdapter(inspector_search_listadapter);
                    } else {
                        getSiteData(search_url + newText.toLowerCase());
                    }
                    enableSearch = true;
                }
            });
        } else {
            if (self_groupModels != null)
                baseActivity.headerTv.setText(self_groupModels.getCgrp_name());
            enableSearch = false;
        }
        return view;
    }

    private boolean enableSearch;
    private int currentPage = PAGE_START;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    LinearLayoutManager layoutManager;

    private void setdata() {
        group_list.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        group_list.setPadding(10, 10, 10, 10);
        group_list.setClipToPadding(false);
        group_list.getLayoutManager().setMeasurementCacheEnabled(false);
        layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        data_list.setLayoutManager(layoutManager);
        swipe.setColorSchemeResources(R.color.app_text, R.color.app_btn_bg, R.color.app_green);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                currentPage = PAGE_START;
                isLastPage = false;
                inspector_search_listadapter.clear();
                getSiteData(group_url);
            }
        });
        swipe.setDistanceToTriggerSync(450);
        data_list.addOnScrollListener(new PaginationListener(layoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentPage++;
                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getSiteData(group_url);
                    }
                }, 1000);
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public void isFirstPage(boolean b) {

            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });
        setupList(null);
    }

    private void find_id() {
        swipe = view.findViewById(R.id.swipe);
        dwn_excl_btn = view.findViewById(R.id.dwn_excl_btn);
        group_list = view.findViewById(R.id.groups_list);
        data_list = view.findViewById(R.id.data_list);
        blank_tv = view.findViewById(R.id.blank_tv);
        ttl_tv = view.findViewById(R.id.ttl_tv);
        ttl_tv.setText(getResString("lbl_total") + ": 0");
        search_view = view.findViewById(R.id.search_view);
        setdata();
    }

    String group_name;
    Filter_GroupModel self_groupModels;
    ArrayList<Filter_GroupModel> groups;
    ArrayList<GroupUsers> groupUsers;
    boolean groupFetching, siteFetching;
    String url;

    @SuppressLint("HandlerLeak")
    public void update_list(String user_id) {
        if (user_id.equals(""))
            user_id = Static_values.user_id;
        if (page_type.equals("warehouse"))
            url = All_Api.get_child_users + Static_values.client_id + "&user_id=" + user_id;
        else
            url = All_Api.get_childgroups_v1 + Static_values.client_id + "&user_id=" + user_id;

        groupFetching = true;
        Log.e("url ", " is  " + url);
        new NetworkRequest(baseActivity).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (msg_code.equals("200")) {
                            if (url.contains(All_Api.get_child_users)) {
                                groupUsers = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getString("data").toString(), GroupUsers[].class)));
                            } else {
                                JSONObject data = jsonObject.getJSONObject("data");
                                if (!data.getString("info").equals("") && !data.getString("info").equals("[]")) {
                                    self_groupModels = AppUtils.getGson().fromJson(data.getString("info"), Filter_GroupModel.class);
                                    if (self_groupModels != null) {
                                        group_name = self_groupModels.getCgrp_name();
                                        baseActivity.headerTv.setText(self_groupModels.getCgrp_name());
                                    } else baseActivity.headerTv.setText("");
                                }
                                if (!data.getString("child_groups").equals("") && !data.getString("child_groups").equals("[]")) {
                                    groups = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data.getString("child_groups"), Filter_GroupModel[].class)));
                                }
                                if (!data.getString("users").equals("")) {
                                    groupUsers = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data.getString("users"), GroupUsers[].class)));
                                }
                            }
                            if (group_userId.equals("")) {
                                getSiteData(group_url);
                            }
                            groupFetching = false;
                            set_group();
                        } else {
                            groupFetching = false;
                            checkBlankPage();
//                            AppUtils.show_snak(baseActivity, "" + jsonObject.getString("message"));
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
            }
        });
    }

    void checkBlankPage() {
        printLog("user id for group =" + group_userId);
        if ((!siteFetching && (inspector_search_listadapter == null || inspector_search_listadapter.getData().isEmpty())
                && !groupFetching && (filter_groupAdapter == null || finaldata.isEmpty()))) {
            if (!Static_values.user_id.equals(group_userId)) {
                baseActivity.onBackPressed();
            } else {
                getActivity().getSupportFragmentManager().popBackStack();
                if (!filter.equals(""))
                    load_fragment(filter, getResString("lbl_my_asst_txt"), "myassets");
                else
                    load_fragment("register", getResString("lbl_my_asst_txt"), "myassets");
            }
        }
    }

    public void load_fragment(String filter, String page_name, String page_type) {
        Main_Fragment basefragment = new Main_Fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("filter", filter);
        bundle1.putString("page_type", page_type);
        bundle1.putString("user_id", group_userId);
        if (!group_id.equals(""))
            bundle1.putString("group_id", group_id);
        if (!role_id.equals(""))
            bundle1.putString("role_id", role_id);
        if (!total_size.equals(""))
            bundle1.putString("total", total_size);
        basefragment.setArguments(bundle1);
        LoadFragment.replace(basefragment, getActivity(), page_name);
    }

    ArrayList finaldata;

    public void set_group() {
        finaldata = new ArrayList();
        if (groups != null && groups.size() > 0) {
            finaldata.addAll(groups);
        }
        if (groupUsers != null && groupUsers.size() > 0) {
            finaldata.addAll(groupUsers);
        }
        if (finaldata.size() > 0) {
            if (filter_groupAdapter != null) {
                filter_groupAdapter.update_list(finaldata);
            } else {
                filter_groupAdapter = new Filter_GroupAdapter(baseActivity, finaldata, page_type, filter);
                group_list.setAdapter(filter_groupAdapter);
            }
        } else {
            checkBlankPage();
            group_list.setVisibility(View.GONE);
        }
    }

    Inspector_search_listadapter inspector_search_listadapter;

    public void getSiteData(String url) {
        url = url + "&page=" + currentPage + "&limit=" + 20;
        if (page_type.equals("warehouse"))
            url = url + "&module=warehouse";
        url = url.replace(" ", "%20");
        if (!url.equals("")) {
            siteFetching = true;
            if (isNetworkAvailable(getActivity())) {
                ArrayList<Site_Model> site_models = new ArrayList<>();
                NetworkRequest networkRequest;
                if (currentPage == 1 || url.contains(search_register_assets))
                    networkRequest = new NetworkRequest();
                else
                    networkRequest = new NetworkRequest();
                String finalUrl = url;
                networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response", "" + response);
                        Object json;
                        siteFetching = false;
                        try {
                            json = new JSONTokener(response).nextValue();
                            if (json instanceof JSONObject) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("status_code")) {
                                    if (jsonObject.getString("status_code").equals("200")) {
                                        site_models.addAll(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(jsonObject.getJSONObject("data").getString("data"), Site_Model[].class))));
                                    } else {
                                        isLastPage = true;
                                        if (!group_id.equals("8"))
                                            show_snak(getActivity(), jsonObject.getString("message"));
                                    }
                                }
                            }
                            if (finalUrl.contains(search_register_assets)) {
                                data_list.setVisibility(VISIBLE);
                                Inspector_search_listadapter inspector_search_listadapter = new Inspector_search_listadapter(baseActivity, site_models, page_type);
                                data_list.setAdapter(inspector_search_listadapter);
                            } else if ((site_models == null || site_models.isEmpty())) {
                                checkBlankPage();
                                return;
                            } else {
                                setupList(site_models);
                            }
                            Log.e("scroll ", "run now");
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

    public void setupList(ArrayList<Site_Model> site_models) {
        if (site_models != null) {
            if (site_models.size() == 0)
                isLastPage = true;
            if (site_models.size() > 0) {
                if (inspector_search_listadapter != null) {
                    if (currentPage != PAGE_START)
                        inspector_search_listadapter.removeLoading();
                    isLoading = false;
                    swipe.setRefreshing(false);
                    inspector_search_listadapter.addData(site_models);
                } else {
                    data_list.setVisibility(VISIBLE);
                    inspector_search_listadapter = new Inspector_search_listadapter(baseActivity, site_models, page_type);
                    data_list.setAdapter(inspector_search_listadapter);
                }
                if (inspector_search_listadapter.getItemCount() > 0) {
                    ttl_tv.setVisibility(VISIBLE);
                    blank_tv.setVisibility(View.GONE);
                    ((ViewGroup) search_view.getParent()).setVisibility(VISIBLE);
                    ttl_tv.setText(getResString("lbl_total") + ": " + inspector_search_listadapter.getItemCount());
                } else {
                    blank_tv.setVisibility(VISIBLE);
                    ttl_tv.setVisibility(View.GONE);
                    ((ViewGroup) search_view.getParent()).setVisibility(View.GONE);
                }
            } else if (inspector_search_listadapter != null) {
                if (currentPage != PAGE_START && inspector_search_listadapter.isLoaderVisible)
                    inspector_search_listadapter.removeLoading();
                isLoading = false;
                swipe.setRefreshing(false);
            } else if (inspector_search_listadapter == null && site_models.size() == 0) {
                blank_tv.setVisibility(VISIBLE);
                ttl_tv.setVisibility(View.GONE);
                ((ViewGroup) search_view.getParent()).setVisibility(View.GONE);
            }
            if (!isLastPage && site_models.size() > 15) {
                inspector_search_listadapter.addLoading();
            }
        } else {
            ttl_tv.setVisibility(View.GONE);
            ((ViewGroup) search_view.getParent()).setVisibility(View.GONE);
        }
    }
}
