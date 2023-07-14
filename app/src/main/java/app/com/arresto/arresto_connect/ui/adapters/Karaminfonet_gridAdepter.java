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

package app.com.arresto.arresto_connect.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Category_Model;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.ui.modules.kowledge_tree.Karam_Infonetperent_frg;
import app.com.arresto.arresto_connect.ui.modules.kowledge_tree.Karam_infonet_product_frg;

import static app.com.arresto.arresto_connect.constants.AppUtils.load_image;

public class Karaminfonet_gridAdepter extends RecyclerView.Adapter<Karaminfonet_gridAdepter.ViewHolder> {
    private String cat_type,product_catgry_id="";
    private String manageId = "";
    private Activity context;
    private ArrayList data;

    public Karaminfonet_gridAdepter(Activity activity, ArrayList data, String catgry_typ) {
        this.context = activity;
        this.data = data;
        this.cat_type = catgry_typ;
    }

    public Karaminfonet_gridAdepter(Activity activity, ArrayList data, String catgry_id,String manageId, String catgry_typ) {
        this.context = activity;
        this.data = data;
        this.product_catgry_id = catgry_id;
        this.cat_type = catgry_typ;
        this.manageId = manageId;
    }

    public void Update(ArrayList data, String manageId) {
        this.data = data;
        this.manageId = manageId;
        notifyDataSetChanged();
    }


    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (data != null)
            return data.size();
        else
            return 0;
    }

    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.karam_infonetgrid_item1, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint({"CheckResult", "RecyclerView"})
    public void onBindViewHolder(@NonNull final ViewHolder holder,  int position) {
        if (data.get(position) instanceof Category_Model) {
            final Category_Model category = (Category_Model) data.get(position);
            holder.ctgry_txt.setText(category.getCat_name());
            load_image(category.getCat_image(), holder.imageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Karam_Infonetperent_frg karam_infonetperent_frg = new Karam_Infonetperent_frg();
                    Bundle bundle = new Bundle();
                    bundle.putString("slctdcat_id", category.getId());
                    bundle.putString("slctdunic_name", category.getCat_name());
                    bundle.putString("slctdprnt_id", category.getCat_parentid());
                    karam_infonetperent_frg.setArguments(bundle);
                    LoadFragment.replace(karam_infonetperent_frg, context, category.getCat_name());
                }
            });
        } else {
            final Product_model productModel = (Product_model) data.get(position);
            holder.ctgry_txt.setText(productModel.getProduct_code());
            final String image_url = productModel.getProduct_imagepath();
            load_image(image_url, holder.imageView);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("item on click ", " is run now ");
                    Karam_infonet_product_frg karam_infonet_product_frgm = new Karam_infonet_product_frg();
                    Bundle bundle = new Bundle();
                    bundle.putString("products", new Gson().toJson(data));
                    bundle.putInt("pos", position);
                    bundle.putString("category_id",product_catgry_id);
                    bundle.putString("manageId", manageId);
                    karam_infonet_product_frgm.setArguments(bundle);
                    LoadFragment.replace(karam_infonet_product_frgm, context, AppUtils.getResString("lbl_product_details"));

                }
            });

        }

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView ctgry_txt;

        public ViewHolder(View v) {
            super(v);
            imageView = v.findViewById(R.id.ctgry_img);
            ctgry_txt = v.findViewById(R.id.ctgry_txt);
        }
    }
}