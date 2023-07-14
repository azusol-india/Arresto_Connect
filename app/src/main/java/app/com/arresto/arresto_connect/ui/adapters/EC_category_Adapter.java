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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.TreeNode;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.database.ec_tables.Category_Table;
import app.com.arresto.arresto_connect.database.ec_tables.EC_productsTable;
import app.com.arresto.arresto_connect.database.ec_tables.Project_Boq_table;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_LinePager;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Preview_Fragment;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Products;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_category;

import static app.com.arresto.arresto_connect.constants.AppUtils.check_singleNode;
import static app.com.arresto.arresto_connect.constants.AppUtils.check_status;
import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.horizontal;
import static app.com.arresto.arresto_connect.constants.Static_values.nested_catgrs;
import static app.com.arresto.arresto_connect.constants.Static_values.treeNodes;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_alurail;
import static app.com.arresto.arresto_connect.constants.Static_values.vertical_wire;

public class EC_category_Adapter extends RecyclerView.Adapter<EC_category_Adapter.ViewHolder> {
    Activity context;
    private ArrayList<Category_Model> category_models;
    private boolean is_selected, is_submitted;
    private String boq_id;
    private EC_Base_Fragment fragment;
    private ArrayList<String> name_arr, boq_ids, image_paths;
    private Category_Table.Category_Table_Dao category_table_dao;

    public EC_category_Adapter(Activity activity, ArrayList<Category_Model> category_models, String boq_id) {
        context = activity;
        this.boq_id = boq_id;
        this.category_models = category_models;
        this.is_selected = false;
        category_table_dao = AppController.getInstance().getDatabase().getCategory_Table_Dao();
    }

    Project_Boq_table.Project_Boq_Dao project_boq_dao;

    public EC_category_Adapter(Activity activity, EC_Base_Fragment fragment, ArrayList<Category_Model> category_models, ArrayList<String> name_arr, ArrayList<String> boq_ids, ArrayList<String> image_paths, boolean is_submitted) {
        context = activity;
        this.fragment = fragment;
        this.boq_ids = boq_ids;
        this.name_arr = name_arr;
        this.category_models = category_models;
        this.image_paths = image_paths;
        this.is_submitted = is_submitted;
        this.is_selected = true;
        project_boq_dao = AppController.getInstance().getDatabase().getProject_Boq_Dao();
        category_table_dao = AppController.getInstance().getDatabase().getCategory_Table_Dao();
        AppUtils.category_table_dao = category_table_dao;
    }

    public void update_list(ArrayList<Category_Model> category_models, ArrayList<String> name_arr, ArrayList<String> boq_ids, ArrayList<String> image_paths) {
        this.category_models = category_models;
        this.boq_ids = boq_ids;
        this.name_arr = name_arr;
        this.image_paths = image_paths;
        notifyDataSetChanged();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (category_models != null)
            return category_models.size();
        else
            return 0;
    }

    @NonNull
    public EC_category_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (is_selected)
            return new EC_category_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ec_selected_line_item, parent, false));
        else
            return new EC_category_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.ec_adapter_item, parent, false));
    }

    @SuppressLint("CheckResult")
    public void onBindViewHolder(@NonNull final EC_category_Adapter.ViewHolder holder, final int position) {
        final Category_Model category_model = category_models.get(position);
        holder.count_txt.setText("S.No : " + (position + 1));
        holder.ctgry_txt.setText("" + category_model.getCat_name());

        if (is_selected && boq_ids != null) {
            holder.name_tv.setText("Sub Site : " + name_arr.get(position));
            final String image_url = image_paths.get(position);
            if (is_submitted) {
                category_model.setIs_completed(false);
                holder.cart_btn.setVisibility(View.GONE);
                holder.add_btn.setVisibility(View.GONE);
                holder.delete_btn.setVisibility(View.GONE);
                holder.view_btn.setText(getResString("lbl_view"));
                load_image(image_url, holder.imageView);
            } else {
                load_image_file(image_url, holder.imageView);
                if (category_table_dao.isItemExist(user_id, category_model.getId(), boq_ids.get(position))) {
                    category_model.setIs_completed(true);
                    holder.add_btn.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.app_green)));
//                    holder.add_btn.setVisibility(View.GONE);
                } else {
                    category_model.setIs_completed(false);
                    holder.add_btn.setBackgroundTintList(ColorStateList.valueOf(getResColor(R.color.app_text)));
//                    holder.add_btn.setVisibility(View.VISIBLE);
                }
                Project_Boq_table projectBoqTable = project_boq_dao.getSingle_boq(user_id, fragment.project_id, fragment.site_id, boq_ids.get(position));
                if (projectBoqTable != null && projectBoqTable.getData() != null && !projectBoqTable.getData().equals("")) {
                    holder.view_btn.setVisibility(View.VISIBLE);
                    holder.add_btn.setVisibility(View.GONE);
                } else {
                    holder.view_btn.setVisibility(View.GONE);
                    holder.add_btn.setVisibility(View.VISIBLE);
                }
            }

            holder.cart_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String boq_str = boq_ids.get(position);
                    if (category_model.getId().equals(horizontal)) {
                        if (check_status(boq_str, "588", position) && check_singleNode(boq_str, "692", position)) {
                            EC_Products ec_products = new EC_Products();
                            Bundle bundle = new Bundle();
                            bundle.putString("selected_boqs", boq_str);
                            bundle.putString("line_type", category_model.getId());
                            ec_products.setArguments(bundle);
                            LoadFragment.replace(ec_products, context, "BOQ");
                        } else {
                            holder.itemView.performClick();
                        }
                    } else {
                        if ((category_model.getId().equals(vertical_alurail) && check_status(boq_str, vertical_alurail, position)) || (category_model.getId().equals(vertical_wire) && check_status(boq_str, vertical_wire, position))) {
                            EC_Products ec_products = new EC_Products();
                            Bundle bundle = new Bundle();
                            bundle.putString("selected_boqs", boq_str);
                            bundle.putString("line_type", category_model.getId());
                            ec_products.setArguments(bundle);
                            LoadFragment.replace(ec_products, context, "BOQ");
                        } else {
                            holder.itemView.performClick();
                        }
                    }
                }
            });

            holder.add_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    holder.itemView.performClick();
                }
            });
            holder.view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    if (is_submitted) {
                        bundle.putString("data", boq_ids.get(position));
                        bundle.putString("type", "online");
                    } else {
                        bundle.putString("boq_id", boq_ids.get(position));
                        bundle.putString("type", "db");
                    }
                    EC_Preview_Fragment ec_preview_fragment = new EC_Preview_Fragment();
                    ec_preview_fragment.setArguments(bundle);
                    LoadFragment.replace(ec_preview_fragment, context, getResString("lbl_line_overview"));
                }
            });
            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    delete_dialogbox(context, position);
                }
            });
        } else {
            final String image_url = category_model.getCat_image();
            load_image(image_url, holder.imageView);
            if (!is_selected && boq_id != null && category_table_dao.isItemExist(user_id, category_model.getId(), boq_id)) {
                category_model.setIs_completed(true);
            } else {
                category_model.setIs_completed(false);
            }
            holder.add_layer.setVisibility(View.VISIBLE);

            holder.lng_clik_view.setVisibility(View.VISIBLE);
            if (category_model.is_completed()) {
                holder.check_btn.setChecked(true);
            } else {
                holder.check_btn.setChecked(false);
//                holder.lng_clik_view.setVisibility(View.GONE);
            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is_submitted) {
                    holder.view_btn.performClick();
                    return;
                }
                if (is_selected && boq_ids != null) { // when root clicked
                    boq_id = boq_ids.get(position);
                    root_tag = "root_category";
                }
                Category_Table category_table = category_table_dao.getLastEntry(user_id, category_model.getId());
                if (is_selected && !category_table_dao.isBoqExist(user_id, boq_id) && category_table != null) {
//                    show_snak(context, "Last item found!");
                    productCopyAlert(boq_id, category_model, category_table);
                } else {
                    get_serch(category_model);
                }
            }
        });
    }

    public void productCopyAlert(String newBoqId, Category_Model newCat, Category_Table oldCatTab) {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog;
        builder.setTitle("Confirmation!");
        builder.setMessage("Do you want to copy products from your last entered line?");
        // add a button
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                get_serch(newCat);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Yes Copy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                copyAllAsset(newBoqId, newCat, oldCatTab);
                dialog.dismiss();
            }
        });
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    private void copyAllAsset(String newBoqId, Category_Model newCat, Category_Table oldCatTab) {
        String oldBoq = oldCatTab.getBOQ_id();
        List<Category_Table> oldCats = category_table_dao.getBoqCategories(oldBoq);
        List<Category_Table> newCats = new ArrayList<>(Arrays.asList(getGson().fromJson(getGson().toJson(oldCats), Category_Table[].class)));
        for (Category_Table cat : newCats) {
            cat.setId(0);
            cat.setBOQ_id(newBoqId);
//            category_table_dao.insert(cat);
        }
        category_table_dao.insertAll(newCats);
        EC_productsTable.EC_productsTable_Dao productsTableDao = AppController.getInstance().getDatabase().getEC_products_Dao();
        List<EC_productsTable> old_products = productsTableDao.getBoqProducts(oldBoq);
        List<EC_productsTable> newProds = new ArrayList<>(Arrays.asList(getGson().fromJson(getGson().toJson(old_products), EC_productsTable[].class)));
        for (EC_productsTable prod : newProds) {
            prod.setId(0);
            prod.setBOQ_id(newBoqId);
//            productsTableDao.insert(prod);
        }
        productsTableDao.insertAll(newProds);
        LoadFragment.backTo_fragment((BaseActivity) context, getResString("lbl_sub_sites"));
        LoadFragment.replace(new EC_LinePager(), context, getResString("lbl_sub_sites"));
    }

    String root_tag = "";

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, delete_btn;
        TextView ctgry_txt, name_tv, count_txt, view_btn, add_btn, cart_btn;
        CheckBox check_btn;
        RelativeLayout lng_clik_view, add_layer;

        public ViewHolder(View v) {
            super(v);
            name_tv = v.findViewById(R.id.name_tv);
            count_txt = v.findViewById(R.id.count_txt);
            imageView = v.findViewById(R.id.ctgry_img);
            ctgry_txt = v.findViewById(R.id.ctgry_txt);
            check_btn = v.findViewById(R.id.check_btn);
            lng_clik_view = v.findViewById(R.id.lng_clik_view);
            delete_btn = v.findViewById(R.id.delete_btn);
            cart_btn = v.findViewById(R.id.cart_btn);
            view_btn = v.findViewById(R.id.view_btn);
            add_btn = v.findViewById(R.id.add_btn);
            add_layer = v.findViewById(R.id.add_layer);
        }

    }

    private void get_serch(final Category_Model category_model) {
        Bundle bundle = new Bundle();
        bundle.putString("slctdcat_id", category_model.getId());
        bundle.putString("slctdunic_name", category_model.getCat_name());
        bundle.putString("slctdprnt_id", category_model.getCat_parentid());
        bundle.putString("slctd_boq", boq_id);

        Comparable<String> searchCriteria = new Comparable<String>() {
            @Override
            public int compareTo(@NonNull String treeData) {
                if (treeData == null)
                    return 1;
                boolean nodeOk = treeData.contains(category_model.getId());
                return nodeOk ? 0 : 1;
            }
        };

        TreeNode treeRoot = treeNodes.get(category_model.getId());
        if (treeRoot != null) {
            TreeNode found = treeRoot.findTreeNode(searchCriteria);
            Log.e("is leaf ", " " + found.isLeaf());
            if (found.isLeaf()) {
                if (category_table_dao.isItemExist(user_id, category_model.getId(), boq_id)) {
                    category_model.setMissing(0);
                } else {
                    category_model.setMissing(1);
                }
                EC_Products ec_products = new EC_Products();
                ec_products.setArguments(bundle);
                LoadFragment.replace(ec_products, context, "Select " + category_model.getCat_name());
                nested_catgrs.add(0, category_model);
                return;
            } else if (nested_catgrs.size() >= 2 && nested_catgrs.get(nested_catgrs.size() - 2).getId().equals("692")) {
                category_model.setMissing(1);
            } else {
                List<TreeNode> childs = found.getChildrens();
                int missing_count = 0;
                for (TreeNode treeNode : childs) {
                    Category_Model child_model = (Category_Model) treeNode.getData();
                    if (!category_table_dao.isItemExist(user_id, child_model.getId(), boq_id)) {
                        missing_count++;
                    }
                }
                category_model.setMissing(missing_count);
            }

            EC_category ec_category = new EC_category();
            ec_category.setArguments(bundle);
            if (root_tag.equals(""))
                LoadFragment.replace(ec_category, context, "Select " + category_model.getCat_name());
            else
                LoadFragment.Stack_replace(ec_category, context, "Select " + category_model.getCat_name(), root_tag);
            nested_catgrs.add(0, category_model);
        }
    }

    private void delete_boq(int pos) {
        project_boq_dao.deleteBOQ(user_id, fragment.project_id, fragment.site_id, boq_ids.get(pos));
        category_table_dao.deleteCategory(user_id, client_id, boq_ids.get(pos));
        AppController.getInstance().getDatabase().getEC_products_Dao().deleteProduct(user_id, client_id, boq_ids.get(pos));
        category_models.remove(pos);
        boq_ids.remove(pos);
        notifyDataSetChanged();
    }

    private void delete_dialogbox(Activity activity, final int position) {
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        alert.setTitle(getResString("lbl_dlet_entry"));
        alert.setMessage(getResString("lbl_dlet_cnf_msg"));
        alert.setPositiveButton(getResString("lbl_yes"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue with delete
                delete_boq(position);
            }
        });
        alert.setNegativeButton(getResString("lbl_no"), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }
}
