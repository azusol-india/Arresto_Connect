/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.EC_Product_Model;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.ec_tables.Category_Table;
import app.com.arresto.arresto_connect.database.ec_tables.EC_productsTable;

import static app.com.arresto.arresto_connect.constants.AppUtils.load_image;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.nested_catgrs;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

/**
 * Created by AZUSOL-PC-02 on 5/24/2019.
 */

public class EC_Product_listAdapter extends RecyclerView.Adapter<EC_Product_listAdapter.ViewHolder> {
    private Activity context;
    private EC_Product_Model productModel;
    private boolean is_longClick;
    private int size = 0,
    //            asset_seriesSize = 0,
    assetSize = 0;
    //            subAssetSize = 0;
    private List<String> slected;
    EC_productsTable.EC_productsTable_Dao ec_products_dao;
    Category_Model parent_category;
    String slctd_boq, type;

    public EC_Product_listAdapter(Activity activity, EC_Product_Model productModel, String slctd_boq, String type) {
        context = activity;
        this.productModel = productModel;
        this.slctd_boq = slctd_boq;
        this.type = type;
        slected = new ArrayList<>();
        ec_products_dao = AppController.getInstance().getDatabase().getEC_products_Dao();


//        if (productModel.getAssets_series() != null) {
//            asset_seriesSize = productModel.getAssets_series().size();
//            size = size + asset_seriesSize;
//        }
        if (productModel.getAssets() != null) {
            assetSize = productModel.getAssets().size();
            size = size + assetSize;

            if (!type.equals("boq")) {
                if (productModel.getCategory_id().equalsIgnoreCase(nested_catgrs.get(0).getId())) {
                    parent_category = nested_catgrs.get(0);
                    if (parent_category.getMax_qty() == 0) {
                        parent_category.setMax_qty("1");
                        size++;
                        EC_Product_Model.Data data = new EC_Product_Model.Data();
                        data.setName("NA");
                        data.setPrice("");
                        data.setUrl("");
                        data.setDescription("");
                        data.setHsn_code("");
                        productModel.getAssets().add(data);
                    }
                }
            }
        }


//        if (productModel.getSub_assets() != null) {
//            subAssetSize = productModel.getSub_assets().size();
//            size = size + subAssetSize;
//        }
    }

    public void Update(EC_Product_Model productModel) {
        this.productModel = productModel;
    }


    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (productModel != null) {
            return size;
        } else
            return 0;
    }

    @NonNull
    public EC_Product_listAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ec_adapter_item, parent, false);
        return new EC_Product_listAdapter.ViewHolder(v);
    }

    @SuppressLint("CheckResult")
    public void onBindViewHolder(@NonNull final EC_Product_listAdapter.ViewHolder holder, final int position) {
        final EC_Product_Model ec_product_model = productModel;
        EC_Product_Model.Data data;

        data = ec_product_model.getAssets().get(position);
        data.setType("asset");

        holder.ctgry_txt.setText(data.getName());

        final String image_url = data.getUrl();
        load_image(image_url, holder.imageView);

        if (!type.equalsIgnoreCase("boq")) {
            holder.lng_clik_view.setVisibility(View.VISIBLE);
            holder.lng_clik_view.setVisibility(View.VISIBLE);
            if (ec_products_dao.isItemExist(user_id, productModel.getCategory_id(), slctd_boq, data.getName())) {
                if (!slected.contains("" + position))
                    slected.add("" + position);
                holder.check_btn.setChecked(true);
            } else {
                if (slected.contains("" + position))
                    slected.remove("" + position);
                holder.check_btn.setChecked(false);
//                holder.lng_clik_view.setVisibility(View.GONE);
            }
            holder.check_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_clicked_DB(holder, position);
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    item_clicked_DB(holder, position);
                }
            });

        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView ctgry_txt;
        RelativeLayout lng_clik_view;
        CheckBox check_btn;

        public ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.ctgry_img);
            ctgry_txt = v.findViewById(R.id.ctgry_txt);
            lng_clik_view = v.findViewById(R.id.lng_clik_view);
            check_btn = v.findViewById(R.id.check_btn);
//            check_btn.setButtonTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getBtn_bg_clr()));
        }
    }

    private void item_clicked_DB(EC_Product_listAdapter.ViewHolder holder, int pos) {

        EC_Product_Model.Data data = productModel.getAssets().get(pos);

        if (slected.contains("" + pos)) {
            slected.remove("" + pos);
            holder.check_btn.setChecked(false);
            holder.lng_clik_view.setVisibility(View.GONE);
            databaseAction("remove", data);
        } else {
            if (slected.size() < parent_category.getMax_qty() || data.getName().equals("NA")) {
                slected.add("" + pos);
                holder.check_btn.setChecked(true);
                holder.lng_clik_view.setVisibility(View.VISIBLE);
                databaseAction("insert", data);
            } else {
                AppUtils.show_snak(context, "You exceeds maximum quantity.");
            }
        }
//        } else {
//            if(slected.size()>=Integer.parseInt(parent_category.getMax_qty())){
//            is_longClick = true;
//            slected.add("" + pos);
//            holder.lng_clik_view.setVisibility(View.VISIBLE);
//            holder.check_btn.setChecked(true);
//            databaseAction("insert", data);
//        }
    }


    private void databaseAction(String action, EC_Product_Model.Data dataProduct) {
        if (action.equalsIgnoreCase("insert")) {
            EC_productsTable ec_productsTable = new EC_productsTable();
            ec_productsTable.setClient_id(Static_values.client_id);
            ec_productsTable.setUser_id(Static_values.user_id);
            ec_productsTable.setName(dataProduct.getName());
            ec_productsTable.setUrl(dataProduct.getUrl());
            ec_productsTable.setType(dataProduct.getType());
            ec_productsTable.setDescription(dataProduct.getDescription());
            ec_productsTable.setHsn_code(dataProduct.getHsn_code());
            ec_productsTable.setPrice(dataProduct.getPrice());
            ec_productsTable.setTax(dataProduct.getTax());
            ec_productsTable.setGroup(dataProduct.getGroup());
            ec_productsTable.setCat_id(productModel.getCategory_id());
            ec_productsTable.setCat_name(productModel.getCategory_name());
            ec_productsTable.setBOQ_id(slctd_boq);
            ec_products_dao.insert(ec_productsTable);
        } else {
            ec_products_dao.deleteProduct(Static_values.user_id, Static_values.client_id, dataProduct.getName(), productModel.getCategory_id(), slctd_boq);
        }

        if (slected.size() >= parent_category.getMin_qty() && slected.size() > 0) {
            if (ec_products_dao.isItemExist(user_id, productModel.getCategory_id(), slctd_boq, dataProduct.getName()))
                category_add_database();
        } else if (slected.size() < parent_category.getMin_qty() || slected.size() == 0) {
            category_remove_database();
        }
    }

    private void category_add_database() {
        for (Category_Model category_model : nested_catgrs) {
            if (category_model != null) {
                //make optional single select 692 for post and corner and 584 for over the roof that has two child
                if (category_model.getId() != null && (category_model.getId().equals("692") || category_model.getCat_parentid().equals("584")))// make Posts And Corners for single select optional
                    category_model.setMissing(1);
                if (category_model.getMissing() < 2) {
                    category_model.setMissing(0);
                    category_model.setIs_completed(true);
                    Category_Table category_table = new Category_Table();
                    category_table.setUser_id(user_id);
                    category_table.setClient_id(client_id);
                    category_table.setCat_id(category_model.getId());
                    category_table.setParent_cat_id(category_model.getCat_parentid());
                    category_table.setBOQ_id(slctd_boq);
                    AppController.getInstance().getDatabase().getCategory_Table_Dao().insert(category_table);
                } else {
//                there never comes a leaf node
                    category_model.setMissing(category_model.getMissing() - 1);
                    return;
                }
            }
        }
    }

    private void category_remove_database() {
        for (Category_Model category_model : nested_catgrs) {
            if (category_model.getMissing() < 1) {
                category_model.setMissing(1);
                category_model.setIs_completed(false);
                AppController.getInstance().getDatabase().getCategory_Table_Dao().deleteCategory(user_id, client_id, category_model.getId(), slctd_boq);
            } else {
//                there never comes a leaf node
                category_model.setMissing(category_model.getMissing() + 1);
                return;
            }
        }
    }

}
