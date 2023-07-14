/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.ec_management;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.EC_Product_Model;
import app.com.arresto.arresto_connect.database.ec_tables.EC_productsTable;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.EC_Product_listAdapter;
import app.com.arresto.arresto_connect.ui.adapters.Section_recycler_Adepter;

import static app.com.arresto.arresto_connect.constants.AppUtils.DpToPixel;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.nested_catgrs;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class EC_Products extends Fragment {
    View view;
    String slctd_prntid, slctdcat_id, slctdunic_name, slctd_boq;
    RecyclerView product_recycler;
    TextView catgry_txt, cntnu_btn, back_main_btn;
    EC_Product_listAdapter product_gridAdepter;
    public String page_type;
    String selected_boq, line_type;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.karam_infonetperent_frg, container, false);

            findAllIds(view);
            if (this.getTag().equalsIgnoreCase("boq")) {
                page_type = "boq";
                if (getArguments() != null) {
                    selected_boq = getArguments().getString("selected_boqs");
                    line_type = getArguments().getString("line_type");
                }
                get_DatabaseData();
            } else {
                page_type = "";
                if (getArguments() != null) {
//                if (getArguments().getString("page_type") != null && getArguments().getString("page_type").equals("filtered")) {
//                    set_filtered_data();
//                } else {
                    slctdcat_id = getArguments().getString("slctdcat_id");
                    slctd_prntid = getArguments().getString("slctdprnt_id");
                    slctdunic_name = getArguments().getString("slctdunic_name");
                    slctd_boq = getArguments().getString("slctd_boq");
                    makeJsonRequest();
//                }
                }
                back_main_btn.setVisibility(View.VISIBLE);
                cntnu_btn.setText(getResString("lbl_next"));
            }

            back_main_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Category_Model last_index = nested_catgrs.get(nested_catgrs.size() - 1);
                    Category_Model sec_last_index = nested_catgrs.get(nested_catgrs.size() - 2);
                    nested_catgrs.clear();
                    nested_catgrs.add(sec_last_index);
                    nested_catgrs.add(last_index);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
//                    Fragment fragment = fragmentManager.findFragmentByTag("root_category");
//                    fragmentManager.popBackStack("root_category", 0);
//                    int index = getIndex(fragmentManager, "root_category");
                    int index = 3;
                    if (index > 0)
                        fragmentManager.popBackStack(index, 0);
                }
            });

            cntnu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (EC_Products.this.getTag().equalsIgnoreCase("boq")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("boq_id", selected_boq);
                        bundle.putString("line_type", line_type);
                        EC_Project_details ec_project = new EC_Project_details();
                        ec_project.setArguments(bundle);
                        LoadFragment.replace(ec_project, getActivity(), getResString("lbl_life_line_details"));
                    } else {
                        getActivity().onBackPressed();
                    }
                }
            });
        }
        return view;
    }

//    private int getIndex(FragmentManager manager, String tag) {
//        for (int i = 0; i < manager.getBackStackEntryCount(); i++) {
//            Fragment frag = manager.findFragmentById(manager.getBackStackEntryAt(i).getName());
//            if (frag.getTag().equals(tag)) {
//                return i;
//            }
//        }
//        return 0;
//    }

    private void findAllIds(View view) {
        view.findViewById(R.id.sub_ctgry_recycler).setVisibility(View.GONE);
        view.findViewById(R.id.line_view).setVisibility(View.GONE);
        product_recycler = view.findViewById(R.id.product_recycler);
        catgry_txt = view.findViewById(R.id.catgry_txt);
        cntnu_btn = view.findViewById(R.id.cntnu_btn);
        back_main_btn = view.findViewById(R.id.back_main_btn);
        view.findViewById(R.id.radioGroup).setVisibility(View.GONE);
        ((LinearLayout) cntnu_btn.getParent()).setVisibility(View.VISIBLE);
        product_recycler.setLayoutManager(new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false));
        product_recycler.setPadding(3, 6, 10, DpToPixel(50));
        product_recycler.setClipToPadding(true);
    }

    public void set_adapter() {
        ArrayList<Section_recycler_Adepter.Section> sections = new ArrayList<>();
        int current_size = 0;
//        if (ec_product_model.getAssets_series() != null && ec_product_model.getAssets_series().size() > 0) {
//            sections.add(new Section_recycler_Adepter.Section(0, "Asset series"));
//            current_size = ec_product_model.getAssets_series().size();
//        }
//        if (ec_product_model.getAssets() != null && ec_product_model.getAssets().size() > 0) {
//            sections.add(new Section_recycler_Adepter.Section(current_size, "Assets"));
//            current_size = current_size + ec_product_model.getAssets().size();
//        }
//        if (ec_product_model.getSub_assets() != null && ec_product_model.getSub_assets().size() > 0) {
//            sections.add(new Section_recycler_Adepter.Section(current_size, "Sub Asset"));
//        }
        product_gridAdepter = new EC_Product_listAdapter(getActivity(), ec_product_model, slctd_boq, page_type);
//        Section_recycler_Adepter.Section[] dummy = new Section_recycler_Adepter.Section[sections.size()];
//        Section_recycler_Adepter mSectionedAdapter = new Section_recycler_Adepter(getActivity(), R.layout.section_layout, R.id.section_text, product_recycler, product_gridAdepter);
//        mSectionedAdapter.setSections(sections.toArray(dummy));
//        product_recycler.setAdapter(mSectionedAdapter);
        product_recycler.setAdapter(product_gridAdepter);

//        if (product_gridAdepter != null)
//            product_gridAdepter.Update(ec_product_model);
//        else {
//            product_gridAdepter = new EC_Product_listAdapter(getActivity(), ec_product_model);
//            product_recycler.setAdapter(product_gridAdepter);
//        }
    }

    EC_Product_Model ec_product_model = new EC_Product_Model();

    private void makeJsonRequest() {
        String url = All_Api.itemsInECCategory + slctdcat_id + "&parentID=" + slctd_prntid + "&client_id=" + client_id;
        Log.e("email id url", "" + url);
        url = url.replace(" ", "%20");
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("error ", "" + response);
                if (!response.contains("error")) {
                    ec_product_model = new Gson().fromJson(response, EC_Product_Model.class);
//                    if (ec_product_model.getAssets_series().size() > 0 || ec_product_model.getAssets().size() > 0 || ec_product_model.getSub_assets().size() > 0) {
                    if (ec_product_model.getAssets().size() > 0) {
                        set_adapter();
                    } else {
                        getActivity().onBackPressed();
                    }
                } else {
                    getActivity().onBackPressed();
                    AppUtils.show_snak(getActivity(), "No data found!");
                }
            }

            @Override
            public void onError(String message) {

            }
        });

    }

    public void get_DatabaseData() {
        ArrayList<EC_Product_Model.Data> asset = new ArrayList<>();
//        String query = "SELECT * FROM ec_productstable WHERE user_id =" + user_id + " AND client_id =" + client_id + " AND (";
//        for (String s : selected_boqs) {
//            query = query + "BOQ_id=" + s + " OR ";
//        }
//        query = query.substring(0, query.length() - 4) + ")";
//        List<EC_productsTable> ec_productsTables = AppController.getInstance().getDatabase().getEC_products_Dao().getBoqProducts(new SimpleSQLiteQuery(query));
        List<EC_productsTable> ec_productsTables = AppController.getInstance().getDatabase().getEC_products_Dao().getProducts(user_id, client_id, selected_boq);
        DataHolder_Model.getInstance().setBoq_products(ec_productsTables);

        for (EC_productsTable ec_productsTable : ec_productsTables) {
            if (ec_productsTable.getType().equalsIgnoreCase("asset"))
                asset.add(new Gson().fromJson(new Gson().toJson(ec_productsTable), EC_Product_Model.Data.class));
        }
        remove_NA_product(asset);
        ec_product_model.setAssets(asset);
        set_adapter();
    }


    public void remove_NA_product(ArrayList<EC_Product_Model.Data> asset) {
        for (int k = asset.size() - 1; k >= 0; k--) {
            if (asset.get(k).getName().equalsIgnoreCase("NA"))
                asset.remove(k);
        }
    }

}
