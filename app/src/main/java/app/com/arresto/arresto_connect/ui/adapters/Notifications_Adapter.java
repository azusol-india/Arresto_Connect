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
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Message_model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Close_projectFragment;
import app.com.arresto.arresto_connect.ui.fragments.Notification_frag;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

public class Notifications_Adapter extends RecyclerView.Adapter<Notifications_Adapter.ViewHolder> {
    private Activity activity;
    private Notification_frag notification_frag;
    private ArrayList<Message_model> listMessages;
    private ArrayList<String> receiver_id, project_id, mdata_id, owner_id;

    public Notifications_Adapter(Activity activity, Notification_frag notification_frag, ArrayList<Message_model> listMessages) {
        this.activity = activity;
        this.notification_frag = notification_frag;
        this.listMessages = listMessages;
        receiver_id = new ArrayList<>();
        project_id = new ArrayList<>();
        mdata_id = new ArrayList<>();
        owner_id = new ArrayList<>();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Message_model message_model = listMessages.get(position);
        holder.time_txt.setText(Html.fromHtml("<u> Date:" + message_model.getDate() + "   Time: " + message_model.getTime() + "</u>"));
        Log.e("getMsg_status ", " click " + message_model.getMsg_status());
        try {
            JSONObject jsonObject = new JSONObject(message_model.getMessage());
            holder.msg_tv.setText(jsonObject.getString("message"));
            if (jsonObject.has("type") && jsonObject.getString("type").equalsIgnoreCase("inspection") || jsonObject.getString("type").equalsIgnoreCase("pdm")) {
                final String type = jsonObject.getString("type");
                holder.aprov_btn.setVisibility(View.GONE);
                holder.cancel_btn.setVisibility(View.GONE);
                holder.status_tv.setText("Alert");
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bundle bundle = new Bundle();
                        bundle.putInt("index", 0);
                        if (type.equalsIgnoreCase("pdm")) {
                            bundle.putString("id", "pdm_report");
                            bundle.putString("pdm_url", All_Api.post_pdm_ins_list + client_id + "&inspector_id=" + user_id);
                            Close_projectFragment close_projectFragment = new Close_projectFragment();
                            close_projectFragment.setArguments(bundle);
                            LoadFragment.replace(close_projectFragment, activity, getResString("lbl_maintenance_report"));
                        } else {
                            bundle.putString("id", "report");
                            Close_projectFragment close_projectFragment1 = new Close_projectFragment();
                            close_projectFragment1.setArguments(bundle);
                            LoadFragment.replace(close_projectFragment1, activity, getResString("lbl_inspection_report"));
                        }
                    }
                });
            } else {
                holder.status_tv.setText(Html.fromHtml(AppUtils.getResString("lbl_status") + "-<font color=#ffffff>" + message_model.getMsg_status() + "</font>"));
                if (!message_model.getMsg_status().equals("pending")) {
                    holder.aprov_btn.setVisibility(View.GONE);
                    holder.cancel_btn.setVisibility(View.GONE);
                } else {
                    holder.aprov_btn.setVisibility(View.VISIBLE);
                    holder.cancel_btn.setVisibility(View.VISIBLE);
                }
                if (receiver_id.size() > position) {
                    receiver_id.set(position, jsonObject.getString("receiver_id"));
                    project_id.set(position, jsonObject.getString("project_id"));
                    mdata_id.set(position, jsonObject.getString("mdata_id"));
                    owner_id.set(position, jsonObject.getString("owner_id"));
                } else {
                    receiver_id.add(jsonObject.getString("receiver_id"));
                    project_id.add(jsonObject.getString("project_id"));
                    mdata_id.add(jsonObject.getString("mdata_id"));
                    owner_id.add(jsonObject.getString("owner_id"));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        holder.aprov_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_status(position, "APPROVED");
            }
        });

        holder.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_status(position, "CANCEL");
            }
        });
    }

    public void removeItem(int position) {
        listMessages.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, listMessages.size());
    }

    @Override
    public int getItemCount() {
        return listMessages.size();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_adeptr_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        return new ViewHolder(v);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {
        TextView time_txt, aprov_btn, cancel_btn, msg_tv, status_tv;

        public ViewHolder(View v) {
            super(v);
            itemView.setOnLongClickListener(this);
            time_txt = v.findViewById(R.id.time_txt);
            aprov_btn = v.findViewById(R.id.aprov_btn);
            cancel_btn = v.findViewById(R.id.cancel_btn);
            msg_tv = v.findViewById(R.id.msg_tv);
            status_tv = v.findViewById(R.id.status_tv);
        }

        @Override
        public boolean onLongClick(View view) {
            Log.e("position long", " click " + view.getId() + " pos " + getAdapterPosition());
            return false;
        }
    }

    @SuppressLint("HandlerLeak")
    public void change_status(final int pos, final String action) {
        HashMap<String, String> params = new HashMap<>();
        params.put("u2u_response", action);
        params.put("u2u_receiver", receiver_id.get(pos));
        params.put("u2u_sender", owner_id.get(pos));
        params.put("project_id", project_id.get(pos));
        params.put("mdata_id", mdata_id.get(pos));
        params.put("store_id ", Static_values.user_id);
        params.put("client_id", client_id);
        NetworkRequest.post_data(notification_frag.getActivity(), All_Api.aprove_status_api, params, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && msg.obj.equals("200")) {
                    notification_frag.update_db_msg(pos, action);
                }
            }
        });
    }
}