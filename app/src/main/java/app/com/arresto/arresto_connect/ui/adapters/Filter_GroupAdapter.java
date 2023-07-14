/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.adapters;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Filter_GroupModel;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.fragments.Group_Filter_fragment;
import app.com.arresto.arresto_connect.ui.fragments.Main_Fragment;

public class Filter_GroupAdapter extends RecyclerView.Adapter<Filter_GroupAdapter.ViewHolder> {
    private ArrayList filter_groupModels;
    BaseActivity activity;
    public static String page_type;
    public String filter;

    public Filter_GroupAdapter(BaseActivity activity, ArrayList groups_models, String page_type, String filter) {
        this.activity = activity;
        this.page_type = page_type;
        this.filter = filter;
        this.filter_groupModels = groups_models;
    }

    public void update_list(ArrayList<Filter_GroupModel> groups_models) {
        this.filter_groupModels = groups_models;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.group_list_item, viewGroup, false);
        // set the view's size, margins, paddings and bg_layer parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (filter_groupModels.get(i) instanceof Filter_GroupModel) {
            final Filter_GroupModel filter_groupModel = (Filter_GroupModel) filter_groupModels.get(i);
            viewHolder.text_view.setBackgroundColor(Dynamic_Var.getInstance().getBtn_bg_clr());
            viewHolder.text_view.setText(filter_groupModel.getCgrp_name());
            viewHolder.text_view1.setText(filter_groupModel.getCgrp_desc());
            if (filter_groupModel.getCgrp_type() != null) {
                String type = filter_groupModel.getCgrp_type();
                type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                viewHolder.type_tv.setText("" + type);
            }
            if (filter_groupModel.getAsset_count_flag().equals("1")) {
                viewHolder.right_btn.setVisibility(View.VISIBLE);
            } else {
                viewHolder.right_btn.setVisibility(View.GONE);
            }
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slected_group = filter_groupModel.getCgrp_user_id();
                    load_next_group(filter_groupModel.getCgrp_user_id(), filter_groupModel.getCgrp_name(), page_type, false);
                }
            });
        } else if (filter_groupModels.get(i) instanceof GroupUsers) {
            final GroupUsers groupUser = (GroupUsers) filter_groupModels.get(i);
            viewHolder.text_view1.setText(groupUser.getUacc_email());
//            set_background(viewHolder.text_view, Dynamic_Var.getInstance().getApp_text(), "color_bg");
            viewHolder.text_view.setBackgroundColor(Dynamic_Var.getInstance().getApp_text());
            viewHolder.text_view.setText(groupUser.getUacc_username());
            if (groupUser.getCgrp_type() != null) {
                String type = groupUser.getCgrp_type();
                type = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
                viewHolder.type_tv.setText("" + type);
            }
            viewHolder.right_btn.setVisibility(View.GONE);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    slected_group = groupUser.getUacc_id();
                    if (!filter.equals("") && !page_type.equals("warehouse"))
                        load_fragment(filter, groupUser, page_type);
                    else
                        load_next_group(groupUser.getUacc_id(), groupUser.getUacc_username(), page_type, true);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return filter_groupModels.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView text_view, text_view1, type_tv;
        ImageView right_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            text_view = itemView.findViewById(R.id.inspector_tv0);
            text_view1 = itemView.findViewById(R.id.inspector_tv1);
            type_tv = itemView.findViewById(R.id.type_tv);
            right_btn = itemView.findViewById(R.id.ok_btn);
            right_btn.setImageResource(R.drawable.right_ok);
            ImageViewCompat.setImageTintList(right_btn, ColorStateList.valueOf(Dynamic_Var.getInstance().getBtn_bg_clr()));

        }
    }

    public static String slected_group = "";

    private void load_next_group(String uacc_id, String page_name, String page_type, boolean isUser) {
        Group_Filter_fragment group_filter_fragment = new Group_Filter_fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("page_type", page_type);
        bundle1.putString("group_userId", uacc_id);
        bundle1.putBoolean("isUser", isUser);
        bundle1.putString("filter", filter);
        group_filter_fragment.setArguments(bundle1);
        LoadFragment.replace(group_filter_fragment, activity, page_name);
    }

    public void load_fragment(String filter, GroupUsers user, String page_type) {
        Main_Fragment basefragment = new Main_Fragment();
        Bundle bundle1 = new Bundle();
        bundle1.putString("filter", filter);
        bundle1.putString("page_type", page_type);
        bundle1.putString("user_id", user.getUacc_id());
        bundle1.putString("group_id", user.getUacc_group_fk());
        bundle1.putString("role_id", user.getCgrp_id());
        basefragment.setArguments(bundle1);
        LoadFragment.replace(basefragment, activity, user.toString());
    }

}
