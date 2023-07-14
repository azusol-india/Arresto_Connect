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

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Periodic_model;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.ui.modules.periodic_maintainance.Periadic_steps;
import app.com.arresto.arresto_connect.ui.modules.periodic_maintainance.Periodic_fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.load_data;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.EMAIL;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_FNAME;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_LNAME;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.slctd_product_image;
import static app.com.arresto.arresto_connect.constants.Static_values.slctd_product_name;
import static app.com.arresto.arresto_connect.custom_views.Font_type.set_background;

public class Peridic_stepAdapter extends RecyclerView.Adapter<Peridic_stepAdapter.ViewHolder> {

    Activity activity;
    private ArrayList<Periodic_model> periodic_models;

    public Peridic_stepAdapter(Activity activity, ArrayList<Periodic_model> periodic_models) {
        this.activity = activity;
        this.periodic_models = periodic_models;
    }

    public void add_data(ArrayList<Periodic_model> periodic_models) {
        this.periodic_models = periodic_models;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.inspector_search_listadeptor, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, final int position) {
        final Periodic_model periodic_model = periodic_models.get(position);
        viewHolder.step_name.setText(periodic_model.getPdm_step());
        viewHolder.step_process.setText(periodic_model.getPdm_process());

        if (Periadic_steps.inspected_steps.contains(position)) {
//            viewHolder.bg_layer.setBackgroundResource(R.drawable.border_bg);
            set_background(viewHolder.bg_layer, Dynamic_Var.getInstance().getApp_text(), "bottom_line_bg");
            viewHolder.refer_btn.setImageResource(R.drawable.right_ok);
//            viewHolder.refer_btn.setImageTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getBtn_bg_clr()));
        } else {
//            viewHolder.bg_layer.setBackgroundResource(R.drawable.edittext_bg1);
            set_background(viewHolder.bg_layer, Dynamic_Var.getInstance().getBtn_bg_clr(), "bottom_line_bg");
            viewHolder.refer_btn.setImageResource(R.drawable.refer);
//            viewHolder.refer_btn.setImageTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getBtn_bg_clr()));
            viewHolder.refer_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String mobile = "";
                    if (Profile_Model.getInstance().getUpro_phone() != null)
                        mobile = Profile_Model.getInstance().getUpro_phone();

                    String html = "<p><b>Product: " + slctd_product_name
                            + "<br><b>Step Name: " + periodic_model.getPdm_step()
                            + "<br><b>Inspector Name: " + mPrefrence.getData(USER_FNAME) + " " + mPrefrence.getData(USER_LNAME)
                            + "<br><b>Email: " + mPrefrence.getData(EMAIL)
                            + "<br><b>Mobile: " + mobile
                            + "</p>";
                    load_data(activity, html, slctd_product_image);
                }
            });
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolder_Model.getInstance().setSlctd_periodic_model(periodic_model);
                Bundle bundle = new Bundle();
                bundle.putInt("pos", position);
                Periodic_fragment periodic_fragment = new Periodic_fragment();
                periodic_fragment.setArguments(bundle);
                LoadFragment.replace(periodic_fragment, activity, periodic_model.getPdm_step());
            }
        });
        viewHolder.bg_layer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHolder.itemView.performClick();
            }
        });

    }

    @Override
    public int getItemCount() {
        return periodic_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView step_name, step_process;
        RelativeLayout bg_layer;
        ImageView refer_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            refer_btn = itemView.findViewById(R.id.refer_btn);
            bg_layer = itemView.findViewById(R.id.bg_layer);
            step_name = itemView.findViewById(R.id.inspector_tv0);
            step_process = itemView.findViewById(R.id.inspector_tv2);
            refer_btn.setVisibility(View.VISIBLE);
            itemView.findViewById(R.id.inspector_tv1).setVisibility(View.GONE);
            itemView.findViewById(R.id.item_img).setVisibility(View.GONE);
        }
    }
}
