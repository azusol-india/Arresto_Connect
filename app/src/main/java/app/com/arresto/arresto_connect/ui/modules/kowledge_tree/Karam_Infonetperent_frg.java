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
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.Karaminfonet_gridAdepter;
import app.com.arresto.arresto_connect.ui.fragments.Gallery_Fragment;

public class Karam_Infonetperent_frg extends KnowledgeTreeBase {
    private View view;
    private String slctd_prntid, slctdcat_id, slctdunic_name;
    private RecyclerView sub_ctgry_recycler, product_recycler;
    private TextView catgry_txt;
    private Karaminfonet_gridAdepter karaminfonet_gridAdepter, product_gridAdepter;
    private LinearLayout radioGroup;
    String client_id = Static_values.client_id;
    String api_server = "";

    @Override
    public View BaseView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (connected_clientsModel != null) {
            client_id = connected_clientsModel.getClient_id();
            api_server = connected_clientsModel.getApi_server();
        }
        if (view == null) {
            view = inflater.inflate(R.layout.karam_infonetperent_frg, parent, false);
            findAllIds(view);
            if (getArguments() != null) {
                if (getArguments().getString("page_type") != null && getArguments().getString("page_type").equals("filtered")) {
                    set_filtered_data();
                } else {
                    slctdcat_id = getArguments().getString("slctdcat_id");
                    slctd_prntid = getArguments().getString("slctdprnt_id");
                    slctdunic_name = getArguments().getString("slctdunic_name");
                    set_grid_data();
                }
            }
            return view;
        } else {
            return view;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "knowledgetree dashboard");
    }

    private void findAllIds(View view) {
        gallary_url = new ArrayList<>();
        sub_ctgry_recycler = view.findViewById(R.id.sub_ctgry_recycler);
        product_recycler = view.findViewById(R.id.product_recycler);
        catgry_txt = view.findViewById(R.id.catgry_txt);
//        prdct_txt = view.findViewById(R.id.prdct_txt);
        radioGroup = view.findViewById(R.id.radioGroup);

        search_btn = view.findViewById(R.id.search_btn);
        srch_prdct = view.findViewById(R.id.srch_prdct);
        srch_prdct.setHint(getResString("lbl_search_txt"));
        ((RelativeLayout) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        setdata();
        setup_searchview();
    }

    private void set_layoutManager(RecyclerView list_view, int type) {
        if (type == 0) {
            list_view.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            list_view.setPadding(3, 6, 3, 6);
        } else {
            list_view.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
            list_view.setPadding(3, 6, 10, 6);
        }
    }

    private void setdata() {
        set_layoutManager(sub_ctgry_recycler, 0);
        set_layoutManager(product_recycler, 1);

        change_rodioGroup(0);
        radioGroup.getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_adapter(asset_series);
                change_rodioGroup(0);
            }
        });
        radioGroup.getChildAt(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_adapter(assets);
                change_rodioGroup(1);
            }
        });
        radioGroup.getChildAt(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_adapter(sub_assets);
                change_rodioGroup(2);
            }
        });
        radioGroup.getChildAt(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gallary_url.size() > 0)
                    LoadFragment.replace(Gallery_Fragment.newInstance(gallary_url), getActivity(), getResString("lbl_prdct_img_st"));
                else
                    show_snak(baseActivity, "Gallery data not found");
            }
        });

    }

    ArrayList<String> gallary_url;

    private void change_rodioGroup(int pos) {
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setBackgroundColor(Dynamic_Var.getInstance().getApp_text());
        }
        radioGroup.getChildAt(pos).setBackgroundColor(Dynamic_Var.getInstance().getBtn_bg_clr());
    }

    private void set_adapter(ArrayList data) {
        if (product_gridAdepter != null)
            product_gridAdepter.Update(data, manageId);
        else {
            product_gridAdepter = new Karaminfonet_gridAdepter(getActivity(), data, slctdcat_id, manageId, "product");
            product_recycler.setAdapter(product_gridAdepter);
        }
    }


    ArrayList categories;

    private void set_grid_data() {
        categories = get_childs(slctdcat_id);
        catgry_txt.setText(toCamelCase(slctdunic_name));
        if (categories.size() > 0) {
            makeJsonRequest();
            Collections.sort(categories, new Comparator<Category_Model>() {
                public int compare(Category_Model obj1, Category_Model obj2) {
                    return obj1.getCat_name().compareToIgnoreCase(obj2.getCat_name());
                }
            });
            karaminfonet_gridAdepter = new Karaminfonet_gridAdepter(getActivity(), categories, "category");
            sub_ctgry_recycler.setAdapter(karaminfonet_gridAdepter);
        } else {
            sub_ctgry_recycler.setVisibility(View.GONE);
            makeJsonRequest();
            show_snak(getActivity(), getResString("lbl_no_subcat_msg"));
        }

    }


//    private ArrayList<String> series_name = new ArrayList<>(), series_img_url = new ArrayList<>(), series_secsn = new ArrayList<>();
//    private ArrayList<String> ast_name = new ArrayList<>(), ast_img_url = new ArrayList<>(), ast_secsn = new ArrayList<>();
//    private ArrayList<String> subAst_name = new ArrayList<>(), subAst_img_url = new ArrayList<>(), subAst_secsn = new ArrayList<>();

    ArrayList<Product_model> assets, sub_assets, asset_series;
    Gson gson = new Gson();
    String manageId = "";

    private void makeJsonRequest() {
        String url;
        if (!api_server.equals(""))
            url = api_server + "/api_controller/itemsInCategory?catID=";
        else
            url = All_Api.itemsInCategory;
        url = url + slctdcat_id + "&parentID=" + slctd_prntid + "&client_id=" + client_id;
        Log.e("url", "" + url);
        url = url.replace(" ", "%20");
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("error fbfgjfukm", "" + response);
//                new CustomToast().Show_Toast(getActivity(), drawerImage, getResString("no_data_msg"));
                try {
                    JSONObject data_object = new JSONObject(response);
                    assets = new ArrayList<>();
                    sub_assets = new ArrayList<>();
                    asset_series = new ArrayList<>();
                    if (data_object.has("id"))
                        manageId = data_object.getString("id");

                    if (data_object.has("assets_series") && data_object.getString("assets_series") != null) {
                        JSONArray jsonArray = data_object.getJSONArray("assets_series");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            Product_model model = gson.fromJson(jsonArray.getString(k), Product_model.class);
                            model.setType("asset_series");
                            asset_series.add(model);
                        }
                    }

                    if (data_object.has("assets") && data_object.getString("assets") != null) {
                        JSONArray jsonArray = data_object.getJSONArray("assets");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            Product_model model = gson.fromJson(jsonArray.getString(k), Product_model.class);
                            model.setType("assets");
                            assets.add(model);
                        }
                    }

                    if (data_object.has("sub_assets") && data_object.getString("sub_assets") != null) {
                        JSONArray jsonArray = data_object.getJSONArray("sub_assets");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            Product_model model = gson.fromJson(jsonArray.getString(k), Product_model.class);
                            model.setType("sub_assets");
                            sub_assets.add(model);
                        }
                    }

                    if (data_object.has("category_media") && data_object.getString("category_media") != null) {
                        JSONArray jsonArray = data_object.getJSONArray("category_media");
                        for (int k = 0; k < jsonArray.length(); k++) {
                            JSONObject obj = jsonArray.getJSONObject(k);
                            gallary_url.add(obj.getString("url"));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("JSONException", "assets is " + e.getMessage());
                    e.printStackTrace();
                }
                if (asset_series.size() > 0) {
                    radioGroup.getChildAt(0).performClick();
                } else if (assets.size() > 0) {
                    radioGroup.getChildAt(1).performClick();
//                    radioGroup.getChildAt(0).setVisibility(View.GONE);
                } else if (sub_assets.size() > 0) {
                    radioGroup.getChildAt(2).performClick();
//                    radioGroup.getChildAt(0).setVisibility(View.GONE);
//                    radioGroup.getChildAt(1).setVisibility(View.GONE);
                } else if (categories == null || categories.size() == 0) {
                    getActivity().onBackPressed();
                } else {
                    set_layoutManager(sub_ctgry_recycler, 1);
//                    radioGroup.getChildAt(0).setVisibility(View.GONE);
//                    radioGroup.getChildAt(1).setVisibility(View.GONE);
                    radioGroup.getChildAt(2).setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {

            }
        });

    }


    public void set_filtered_data() {
        categories = get_childs(slctdcat_id);
        Collections.sort(categories, new Comparator<Category_Model>() {
            public int compare(Category_Model obj1, Category_Model obj2) {
                return obj1.getCat_name().compareToIgnoreCase(obj2.getCat_name());
            }
        });
        karaminfonet_gridAdepter = new Karaminfonet_gridAdepter(getActivity(), categories, "category_pr");
        sub_ctgry_recycler.setAdapter(karaminfonet_gridAdepter);

        ArrayList products = new ArrayList();
        Product_model productModel = new Product_model();
        productModel.setType(getArguments().getString("product_type"));
        productModel.setProductImage(getArguments().getString("product_image"));
        productModel.setProductName(getArguments().getString("product_code"));

        Karam_infonet_product_frg karam_infonet_product_frgm = new Karam_infonet_product_frg();
        Bundle bundle = new Bundle();
        products.add(productModel);
        bundle.putString("products", new Gson().toJson(products));
        bundle.putString("category_id", slctdcat_id);
        bundle.putInt("pos", 0);
        karam_infonet_product_frgm.setArguments(bundle);
        LoadFragment.replace(karam_infonet_product_frgm, getActivity(), getResString("lbl_product_details"));
    }


}
