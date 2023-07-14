/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;

public class Factory_MasterAdapter extends RecyclerView.Adapter {
    Activity activity;
    private ArrayList component_models;

    private final int VIEW_SITE = 1;
    private final int VIEW_TV = 0;

    public Factory_MasterAdapter(Activity activity, ArrayList component_models) {
        this.activity = activity;
        this.component_models = component_models;
    }

    public void addData(ArrayList component_models) {
        this.component_models = component_models;
        this.notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (component_models.get(position) instanceof Site_Model) ? VIEW_SITE : VIEW_TV;
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_SITE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.factory_masteradapter, parent, false);
            vh = new ViewHolder(v);
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.spinner_item, parent, false);
            vh = new ViewHolder_2(v);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder h, final int position) {
        if (h instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) h;
            final Site_Model component_model = (Site_Model) component_models.get(position);
            holder.asst_name_tv.setText(component_model.getMdata_item_series());
            holder.dscrptn_tv.setText(component_model.getClient_name());
            holder.uin_tv.setText("UIN : " + component_model.getMdata_uin());
            holder.scaned_data.setText("RFID: " + component_model.getMdata_rfid());
            final String image_url = component_model.getImagepath();
            AppUtils.load_image(image_url, holder.ast_image);
        } else {
            ViewHolder_2 holder = (ViewHolder_2) h;
            Object item = component_models.get(position);
            if (item instanceof Constant_model) {
                Constant_model model = (Constant_model) item;
                holder.scaned_data_tv.setText("UIN : " + model.getId()+"\nSensor id : " + model.getName());
            } else
                holder.scaned_data_tv.setText(component_models.get(position).toString());
        }
    }

    @Override
    public int getItemCount() {
        return component_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ast_image;
        TextView asst_name_tv, uin_tv, scaned_data, dscrptn_tv, scan_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            uin_tv = itemView.findViewById(R.id.uin_tv);
            ast_image = itemView.findViewById(R.id.ast_image);
            asst_name_tv = itemView.findViewById(R.id.asst_name_tv);
            scaned_data = itemView.findViewById(R.id.scaned_data);
            scan_btn = itemView.findViewById(R.id.scan_btn);
            dscrptn_tv = itemView.findViewById(R.id.dscrptn_tv);
        }
    }

    public class ViewHolder_2 extends RecyclerView.ViewHolder {
        TextView scaned_data_tv;

        public ViewHolder_2(View itemView) {
            super(itemView);
            scaned_data_tv = (TextView) itemView;
        }
    }

}
