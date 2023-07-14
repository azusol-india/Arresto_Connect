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

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.treeNodes;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.data.TreeNode;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.Connected_ClientsModel;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.database.Data_daowload;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Karaminfonet_gridAdepter;

public class Karam_infonet_frg extends KnowledgeTreeBase {
    View view;
    RecyclerView kdmngr_grid;
    Karaminfonet_gridAdepter karaminfonet_gridAdepter;
    Data_daowload data_daowload;
    SwipeRefreshLayout swipe;

    public static Karam_infonet_frg newInstance(String ref_url) {
        Karam_infonet_frg karam_infonet_frg = new Karam_infonet_frg();
        Bundle args = new Bundle();
        args.putString("ref_url", ref_url);
        karam_infonet_frg.setArguments(args);
        return karam_infonet_frg;
    }

    String ref_url;

    @Override
    public View BaseView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            connected_clientsModel = null;
            view = inflater.inflate(R.layout.karam_infonet_frg, parent, false);

            if (getArguments() != null) {
                ref_url = getArguments().getString("ref_url", "");
                parshReferenceLink();
            }

            data_daowload = new Data_daowload(getActivity());
            findAllIds(view);
            setdata();
            makeJsonRequest("", client_id);
            getConnectedClients();
            swipe.setColorSchemeResources(R.color.app_text, R.color.app_btn_bg, R.color.app_green);
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getFragmentManager().beginTransaction().detach(Karam_infonet_frg.this).attach(Karam_infonet_frg.this).commit();
                }
            });

            setup_searchview();
        } else {
            Log.e("onrefresh is run", " now ");
            if (swipe.isRefreshing())
                swipe.setRefreshing(false);
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "knowledgetree dashboard");
    }

    DialogSpinner customer_spnr;

    public void findAllIds(View view) {
        swipe = view.findViewById(R.id.swipe);
        kdmngr_grid = view.findViewById(R.id.kdmngr_grid);

        search_btn = view.findViewById(R.id.search_btn);
        srch_prdct = view.findViewById(R.id.srch_prdct);
        srch_prdct.setHint(getResString("lbl_search_txt"));
        customer_spnr = view.findViewById(R.id.customer_spnr);
    }

    private void setdata() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
        kdmngr_grid.setLayoutManager(gridLayoutManager);
        kdmngr_grid.setPadding(3, 6, 3, 6);
    }

    ArrayList categories;

    private void makeJsonRequest(String api_server, String client_id) {
        String url;
        if (!api_server.equals(""))
            url = api_server + "/api_controller/kdCategory?client_id=" + client_id;
        else
            url = All_Api.karam_infonet + client_id;
        Log.e("email id url", "" + url);
        DataHolder_Model.getInstance().setKnowledge_treeProducts(new ArrayList());
        data_search = DataHolder_Model.getInstance().getKnowledge_treeProducts();
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data_object = new JSONObject(response);
                    JSONArray jsonArray = data_object.getJSONArray("success");
                    treeNodes = new HashMap<>();
                    TreeNode root = new TreeNode<>("0", new Category_Model());
                    treeNodes.put("0", root);
                    categories = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Category_Model category_model = new Gson().fromJson(jsonArray.getString(i), Category_Model.class);
                        categories.add(category_model);
                        treeNodes.put(category_model.getId(), new TreeNode<>(category_model.getId(), category_model));
                    }
                    make_tree(categories);
                    data_search.addAll(categories);
                    DataHolder_Model.getInstance().setKnowledge_treeProducts(data_search);
                    search_adapter.addData(data_search);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                get_all_product(api_server, client_id);
            }

            @Override
            public void onError(String message) {

            }
        });
    }

    ArrayList<Connected_ClientsModel> clientsModels;

    private void getConnectedClients() {
        String url = All_Api.connected_clients + client_id;
        Log.e("email id url", "" + url);

        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status_code").equals("200")) {
                        String data = jsonObject.getString("data");
                        clientsModels = new ArrayList<>(Arrays.asList(
                                AppUtils.getGson().fromJson(data, Connected_ClientsModel[].class)));
//                        Connected_ClientsModel self = new Connected_ClientsModel();
//                        self.setClient_id(client_id);
//                        self.setClient_name(getResString("app_name"));
//                        clientsModels.add(0, self);
                        Connected_ClientsModel choose = new Connected_ClientsModel();
                        choose.setClient_id("-1");
                        choose.setClient_name(getResString("lbl_choose_brand"));
                        clientsModels.add(0, choose);
//                        customer_spnr.setItems(clientsModels, getResString("lbl_choose_brand"));
                        customer_spnr.setItems(clientsModels, "");
                        ((ViewGroup) customer_spnr.getParent()).setVisibility(View.VISIBLE);
                    } else {
                        ((ViewGroup) customer_spnr.getParent()).setVisibility(View.GONE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {

            }
        });

        customer_spnr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected_item = parent.getSelectedItem().toString();
                if (!selected_item.equals(getResString("lbl_choose_brand")) && customer_spnr.getSelectedItemPosition() > 0) {
                    Connected_ClientsModel item = clientsModels.get(customer_spnr.getSelectedItemPosition());
                    if (item.getClient_id().equals(client_id)) {
                        connected_clientsModel = null;
                        makeJsonRequest("", client_id);
                    } else {
                        connected_clientsModel = item;
                        makeJsonRequest(connected_clientsModel.getApi_server(), connected_clientsModel.getClient_id());
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("nothing slct ", "main obsr  ");
            }
        });
    }

    private void make_tree(ArrayList<Category_Model> category_models) {
        for (Category_Model category_model : category_models) {
            if (treeNodes.containsKey(category_model.getCat_parentid())) {
                treeNodes.get(category_model.getCat_parentid()).addChild(category_model.getId(), category_model);
            }
            if (ref_cat == null && ref_cat_id != null && ref_cat_id.equals(category_model.getId())) {
                ref_cat = category_model;
            }
        }
        ArrayList<Category_Model> categoryModels = get_childs("0");
        Collections.sort(categoryModels, new Comparator<Category_Model>() {
            public int compare(Category_Model obj1, Category_Model obj2) {
                return obj1.getCat_name().compareToIgnoreCase(obj2.getCat_name());
            }
        });
        karaminfonet_gridAdepter = new Karaminfonet_gridAdepter(getActivity(), categoryModels, "category");
        kdmngr_grid.setAdapter(karaminfonet_gridAdepter);
    }

    private void get_all_product(String api_server, String client_id) {

        String url;
        if (!api_server.equals(""))
            url = api_server + "/api_controller/kt_search_product?client_id=" + client_id;
        else
            url =All_Api.karam_product + client_id;
        Log.e("url", "" + url);
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject data_object = new JSONObject(response);
                    data_search.addAll(Arrays.asList(AppUtils.getGson().fromJson(data_object.getString("data"), Product_model[].class)));
                    DataHolder_Model.getInstance().setKnowledge_treeProducts(data_search);
//                    ArrayList<String> data = new ArrayList<>(Arrays.asList("anuj", "tyagi", "search", "not", "work"));
                    search_adapter.addData(data_search);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                checkReference();
            }

            @Override
            public void onError(String message) {
            }
        });

    }

    private void checkReference() {
        if (ref_cat != null) {
            Karam_Infonetperent_frg karam_infonetperent_frg = new Karam_Infonetperent_frg();
            Bundle bundle = new Bundle();
            bundle.putString("slctdcat_id", ref_cat.getId());
            bundle.putString("slctdunic_name", ref_cat.getCat_name());
            bundle.putString("slctdprnt_id", ref_cat.getCat_parentid());
            karam_infonetperent_frg.setArguments(bundle);
            ref_cat_id = null;
            LoadFragment.replace(karam_infonetperent_frg, baseActivity, ref_cat.getCat_name());
            if (ref_productName != null && !ref_productName.equals("")) {
                get_search_data(ref_productName);
            }
        }
    }

    String ref_cat_id, ref_productName;
    Category_Model ref_cat;

    private void parshReferenceLink() {
        if (ref_url != null && !ref_url.equals("") && ref_url.contains("knowledgetree")) {
            String[] txt = ref_url.split("ktview/");
            if (txt[1].contains("/")) {
                String[] txt2 = txt[1].split("/");
                ref_cat_id = txt2[0];
                ref_productName = txt2[1];
                printLog("product_name=== " + ref_productName);
            } else {
                ref_cat_id = txt[1];
            }
            printLog("category_id=== " + ref_cat_id);
        }
    }


}