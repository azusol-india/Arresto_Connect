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
import android.app.AlertDialog;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Component_history_Model;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Project_Model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.database.inspection_tables.Master_data_table;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.fragments.Master_detail_fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.calculate_inspection;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.indicatorDrawable;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;


public class Asset_Adapter extends RecyclerView.Adapter<Asset_Adapter.ViewHolder> {
    BaseActivity activity;
    private ArrayList<Component_model> component_models;
    private List<String> asset_status;
    private String page_type, filter_type = "";
    Handler refreshHandler;
    Project_Model project_model;

    public Asset_Adapter(BaseActivity activity, Project_Model project_model, ArrayList<Component_model> component_models, String page_type, Handler refreshHandler) {
        this.activity = activity;
        this.project_model = project_model;
        this.page_type = page_type;
        this.component_models = component_models;
        this.refreshHandler = refreshHandler;
        asset_status = Arrays.asList(new String[component_models.size()]);
    }

    public Asset_Adapter(BaseActivity activity, String page_type, String filter_type) {
        this.activity = activity;
        this.page_type = page_type;
        if (filter_type != null)
            this.filter_type = filter_type;
        component_models = DataHolder_Model.getInstance().getComponent_models();
        asset_status = new ArrayList<>(Arrays.asList(new String[component_models.size()]));
    }

    public Asset_Adapter(BaseActivity activity, ArrayList<Component_model> component_models, String page_type) {
        this.activity = activity;
        this.page_type = page_type;
        this.component_models = component_models;
        asset_status = new ArrayList<>(Arrays.asList(new String[component_models.size()]));
    }

    public void addData(ArrayList<Component_model> component_models) {
        this.component_models = component_models;
        this.notifyDataSetChanged();
    }

    public String statusAll = "";

    public void updateStatus(String status) {
//        if (!activity.isEOD(project_model)) {
        statusAll = status;
        isSellectAll = true;
        for (Component_model o : component_models) {
            if (statusAll.equals("checkin") && !o.getAsm_product_status().equals("checkin")) {
                o.setSelected(true);
                o.setSelectedStatus("checkin");
            } else if (statusAll.equals("checkout") && !o.getAsm_product_status().equals("") && !o.getAsm_product_status().equals("checkout")) {
                o.setSelected(true);
                o.setSelectedStatus("checkout");
            } else {
                o.setSelected(false);
                o.setSelectedStatus("");
            }
        }
        notifyDataSetChanged();
//        }
    }

    public List<String> getAssetStatus() {
        return asset_status;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.asset_adapter, parent, false);
        // set the view's size, margins, paddings and bg_layer parameters
        return new ViewHolder(v);
    }

    private String get_hours_from_Decimal(double decimal_value) {
        int ttl_secs = (int) (decimal_value * 3600);
        int hours = ttl_secs / 3600;
        int minutes = (ttl_secs % 3600) / 60;
        if (hours <= 0) {
            return String.format("%02d:%02d", 0, 0);
        } else
            return String.format("%02d:%02d", hours, minutes);
    }

    boolean isSellectAll;

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Component_model component_model = component_models.get(position);
        holder.asst_name_tv.setText(component_model.getComponent_code());
        holder.dscrptn_tv.setText(component_model.getComponent_description());
//        holder.dscrptn_tv.setText(component_model.getComponent_description()+"UIN : dsg rthfd \n feggfv b gav \n fsdfvzxvagadSDVdDf\ndagvadfhgsdb zdfb\nfvzfhsfgh\nsdfasgdg\ndfsfadgadfgfgagadsgadfgszagSDgz adsgagh gfc        grhsfnvcnvx" );
        holder.uin_tv.setText("UIN : " + component_model.getMdata_uin());

        holder.ttl_life_tv.setVisibility(View.GONE);
        holder.used_hr_tv.setVisibility(View.GONE);
        holder.remn_hr_tv.setVisibility(View.GONE);

        int inp_stats = calculate_inspection(component_model.getInspection_due_date(), 30);
        holder.indictr_img.setImageResource(indicatorDrawable[inp_stats]);
        if (inp_stats == 0) {
            holder.status_tv.setText(getResString("lbl_ins_ovrdue_st"));
        } else if (inp_stats == 1) {
            holder.status_tv.setText(getResString("lbl_inspctndue_txt"));
        } else {
            holder.status_tv.setText(getResString("lbl_inspctd_st"));
        }

        if (page_type.equals("project_data")) {
            holder.check_btn.setVisibility(View.VISIBLE);
            asset_status.set(position, component_model.getAsm_product_status());
            if (component_model.getAsm_product_status() != null) {
                if (component_model.getAsm_product_status().equals("checkin")) {
                    ((ImageView) holder.check_btn.getChildAt(0)).setImageResource(R.drawable.check_out);
                    ((TextView) holder.check_btn.getChildAt(1)).setText(getResString("lbl_checkout_st"));

                    holder.asst_name_tv.setBackgroundColor(Dynamic_Var.getInstance().getApp_text());
                    holder.desc_tv.setBackgroundColor(Dynamic_Var.getInstance().getApp_text());
//                    holder.asst_name_tv.setTextColor(Dynamic_Var.getInstance().getApp_background());
//                    holder.desc_tv.setTextColor(Dynamic_Var.getInstance().getApp_background());

                    GradientDrawable gd = (GradientDrawable) holder.prnt_layr.getBackground();
                    gd.setStroke(1, Dynamic_Var.getInstance().getApp_text());
                    holder.prnt_layr.setBackground(gd);
                } else {
                    ((ImageView) holder.check_btn.getChildAt(0)).setImageResource(R.drawable.check_in);
                    ((TextView) holder.check_btn.getChildAt(1)).setText(getResString("lbl_checkin_st"));

                    holder.asst_name_tv.setBackgroundColor(Dynamic_Var.getInstance().getBtn_bg_clr());
                    holder.desc_tv.setBackgroundColor(Dynamic_Var.getInstance().getBtn_bg_clr());
//                    holder.asst_name_tv.setTextColor(Dynamic_Var.getInstance().getApp_text());
//                    holder.desc_tv.setTextColor(Dynamic_Var.getInstance().getApp_text());

                    GradientDrawable gd = (GradientDrawable) holder.prnt_layr.getBackground();
                    gd.setStroke(1, Dynamic_Var.getInstance().getBtn_bg_clr());
                    holder.prnt_layr.setBackground(gd);
                }
            }
            holder.check_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    if (!activity.isEOD(project_model)) {
                    JSONObject params = new JSONObject();
                    try {
                        params.put("product_id", component_model.getComponent_code());
                        params.put("user_id", Static_values.user_id);
                        params.put("client_id", client_id);
                        params.put("time", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                        params.put("mdata_id", component_model.getMdata_id());
                        if (component_model.getAsm_product_status() != null && component_model.getAsm_product_status().equals("checkin")) {
                            params.put("action", "checkout");
                            Checkout_Dialog(params);
                        } else {
                            params.put("action", "checkin");
                            submitStatus(params, "", "");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
//                    }
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serviceCall_1(component_model.getMdata_id());
                }
            });

            if (isSellectAll && !statusAll.equalsIgnoreCase(component_model.getAsm_product_status()) && component_model.getSelectedStatus() != null && !component_model.getSelectedStatus().equals("")) {
                holder.gray_view.setVisibility(View.VISIBLE);
                holder.select_btn.setChecked(component_model.isSelected());
//                if ((component_model.getSelectedStatus().equals("")))
//                    holder.gray_view.setVisibility(View.GONE);
            } else {
                holder.gray_view.setVisibility(View.GONE);
            }

            holder.select_btn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    component_model.setSelected(b);
                    if (component_model.getSelectedStatus().equals("checkin"))
                        component_model.setSelectedStatus("checkout");
                    else
                        component_model.setSelectedStatus("checkin");
                    holder.itemView.post(new Runnable() {
                        @Override
                        public void run() {
                            notifyItemChanged(position);
                        }
                    });

                }
            });
        } else if (filter_type.equals("used")) {
            holder.check_btn.setVisibility(View.VISIBLE);
            ((TextView) holder.check_btn.getChildAt(1)).setText("Return to Store");
            holder.check_btn.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("HandlerLeak")
                @Override
                public void onClick(View v) {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("store_id", Static_values.user_id);
                    params.put("user_id", component_model.getAsm_user_id());
                    params.put("product_id", component_model.getComponent_code());
                    params.put("project_id", component_model.getAsm_project_id());
                    params.put("mdata_id", component_model.getMdata_id());
                    params.put("client_id", client_id);
                    NetworkRequest.post_data(activity, All_Api.return_to_store, params, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            Log.e("return obj", "" + msg.obj);
                            if (msg.obj != null && (msg.obj.toString()).equals("200")) {
//                                create_Historydialog(activity);
                                update_list(All_Api.filtrd_storeData + Static_values.user_id + "&client_id=" + client_id + "&filter=" + filter_type);
                            }
                        }
                    });
                }
            });
        } else {
            holder.check_btn.setVisibility(View.GONE);
            ((LinearLayout) holder.check_btn.getParent()).setWeightSum(2);
            if (page_type.equals("project_products"))
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        serviceCall_1(component_model.getMdata_id());
                    }
                });
        }

        final String image_url = component_model.getComponent_imagepath();

        AppUtils.load_image(image_url, holder.ast_image);

        try {
            final int totl_hour = Integer.parseInt((component_model.getComponent_lifespan_hours().equals("") ? "0" : component_model.getComponent_lifespan_hours()));
            int ttl_sec = Integer.parseInt((component_model.getAsm_log_seconds().equals("") ? "0" : component_model.getAsm_log_seconds()));
            final double used_decimal = ttl_sec / 3600.0;
            final double remain_decimal = totl_hour - used_decimal;

            holder.view_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    get_history(component_model.getComponent_code(), totl_hour, used_decimal, remain_decimal);
                }
            });
        } catch (NullPointerException e) {
            Log.e("exception  ", " is" + e.getMessage());
            ((LinearLayout) holder.view_btn.getParent()).setWeightSum(2);
            holder.view_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return component_models.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ast_image, indictr_img;
        TextView asst_name_tv, uin_tv, dscrptn_tv, desc_tv, ttl_life_tv, used_hr_tv, remn_hr_tv, status_tv;
        LinearLayout prnt_layr, indicator_view;
        LinearLayout view_details, check_btn, view_btn;
        RelativeLayout gray_view;
        CheckBox select_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            indicator_view = itemView.findViewById(R.id.indicator_view);
            prnt_layr = itemView.findViewById(R.id.prnt_layr);
            desc_tv = itemView.findViewById(R.id.desc_tv);
            uin_tv = itemView.findViewById(R.id.uin_tv);
            ast_image = itemView.findViewById(R.id.ast_image);
            indictr_img = itemView.findViewById(R.id.indictr_img);
            status_tv = itemView.findViewById(R.id.status_tv);
            asst_name_tv = itemView.findViewById(R.id.asst_name_tv);
            check_btn = itemView.findViewById(R.id.check_btn);
            view_details = itemView.findViewById(R.id.view_details);
            view_btn = itemView.findViewById(R.id.view_btn);
            dscrptn_tv = itemView.findViewById(R.id.dscrptn_tv);
            ttl_life_tv = itemView.findViewById(R.id.ttl_life_tv);
            used_hr_tv = itemView.findViewById(R.id.used_hr_tv);
            remn_hr_tv = itemView.findViewById(R.id.remn_hr_tv);
            gray_view = itemView.findViewById(R.id.gray_view);
            select_btn = itemView.findViewById(R.id.select_btn);
            fade_anim(indicator_view);

            ((ImageView) view_details.getChildAt(0)).setColorFilter(Dynamic_Var.getInstance().getApp_background());
            ((ImageView) check_btn.getChildAt(0)).setColorFilter(Dynamic_Var.getInstance().getApp_background());
            ((ImageView) view_btn.getChildAt(0)).setColorFilter(Dynamic_Var.getInstance().getApp_background());
        }
    }

    public void submitStatus(JSONObject obj, String reason, String reason_detail) {
        JSONArray data = new JSONArray();
        JSONObject post_data = new JSONObject();
        try {
            post_data.put("client_id", client_id);
            post_data.put("project_id", project_model.getUp_id());
            if (obj == null) {
                for (Component_model o : component_models) {
                    if (o.isSelected()) {
                        JSONObject object = new JSONObject();
                        object.put("mdata_id", o.getMdata_id());
                        object.put("product_id", o.getComponent_code());
                        object.put("user_id", user_id);
                        object.put("action", o.getSelectedStatus());
                        object.put("time", String.valueOf(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())));
                        object.put("client_id", client_id);
                        object.put("reason", reason);
                        object.put("reason_detail", reason_detail);
                        data.put(object);
                    }
                }
            } else {
                obj.put("reason", reason);
                obj.put("reason_detail", reason_detail);
                data.put(obj);
            }
            if (data.length() > 0) {
                post_data.put("team_data", data);
                send_data(post_data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    public void Checkout_Dialog(final JSONObject single_object) { // for user
        final AlertDialog builder = new AlertDialog.Builder(activity).create();
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.usr_addto_store_lay, null);

        final LinearLayout remark_lay = dialoglayout.findViewById(R.id.remark_lay);
        final EditText remark_edt = dialoglayout.findViewById(R.id.remark_edt);
        TextView title_tv = dialoglayout.findViewById(R.id.title_tv);
        title_tv.setText("Select Status");
        TextView add_btn = dialoglayout.findViewById(R.id.add_btn);
        add_btn.setText("Check out");
        final Spinner spinner = dialoglayout.findViewById(R.id.prjct_spnr);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 6) {
                    remark_lay.setVisibility(View.VISIBLE);
                } else {
                    remark_lay.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, activity.getResources().getStringArray(R.array.check_out_value));
        spinner.setAdapter(arrayAdapter);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spinner.getSelectedItemPosition() > 0) {
                    String reason = activity.getResources().getStringArray(R.array.check_out_key)[spinner.getSelectedItemPosition()];
                    String reason_detail = "";
                    if (spinner.getSelectedItemPosition() == 6)
                        reason_detail = "" + remark_edt.getText();
                    submitStatus(single_object, reason, reason_detail);
                    builder.cancel();
                } else {
                    AppUtils.show_snak(activity, "Please select Product current status");
                }
            }
        });
        builder.setView(dialoglayout);
        builder.show();
    }

    private void send_data(JSONObject params) {
        Log.e("params", "" + params);
        NetworkRequest network_request = new NetworkRequest(activity);
        network_request.make_contentpost_request(All_Api.checkin_out_api, params, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String msg_code = jsonObject.getString("status_code");
                        if (!msg_code.equals("200")) {
                            AppUtils.show_snak(activity, jsonObject.getString("message"));
                        } else {
                            AppUtils.show_snak(activity, jsonObject.getString("message"));
                            if (!project_model.getUp_id().equals("")) {
                                update_list(All_Api.project_data + Static_values.user_id + "&client_id=" + client_id + "&project_id=" + project_model.getUp_id() + "&date=" + System.currentTimeMillis());
                                if (refreshHandler != null) {
                                    Message message = new Message();
                                    message.obj = true;
                                    refreshHandler.sendMessage(message);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSONException", "" + e.getMessage());
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
            }
        });
    }

    private int i = 0;

    //    int j = 0;
    private void flip_in(final LinearLayout layout) {
        View view = layout.getChildAt(i);
        Animation bounce = AnimationUtils.loadAnimation(activity, R.anim.bounce);
        bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                i++;
                if (i < layout.getChildCount())
                    flip_in(layout);
                else
                    i = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setVisibility(View.VISIBLE);
        view.startAnimation(bounce);
    }

    @SuppressLint("HandlerLeak")
    private void get_history(String asset_id, final int totl_hour, final double used_decimal, final double remain_decimal) {
        NetworkRequest.get_AssetHistory(activity, All_Api.asst_histry_api + asset_id + "&client_id=" + client_id + "&date=" + System.currentTimeMillis(), new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    create_Historydialog(activity, totl_hour, used_decimal, remain_decimal);
//                   new  Asset_history_frag()
//                    LoadFragment.replace(new Asset_history_frag(),activity,"Product");
                }
            }
        });
    }

    private void make_childview(LinearLayout child_layout) {
        child_layout.removeAllViews();
        params1.setMargins(10, 5, 10, 5);

        for (int i = 0; i < DataHolder_Model.getInstance().getComponent_history_models().size(); i++) {
            Component_history_Model component_history_model = DataHolder_Model.getInstance().getComponent_history_models().get(i);
            TextView user_name_tv = new TextView(activity);
            user_name_tv.setText("Used by : " + component_history_model.getName());
            user_name_tv.setTextColor(AppUtils.getResColor(R.color.app_text));
            user_name_tv.setBackgroundResource(R.drawable.round_bg1);
            user_name_tv.setPadding(10, 5, 5, 5);
            user_name_tv.setGravity(Gravity.CENTER);
            user_name_tv.setVisibility(View.GONE);
            child_layout.addView(user_name_tv);
            Collections.sort(component_history_model.getData(), new Comparator<Component_history_Model.CheckStatus>() {
                public int compare(Component_history_Model.CheckStatus obj1, Component_history_Model.CheckStatus obj2) {
                    if (obj1.getPu_checkin() != null && obj2.getPu_checkin() != null)
                        return obj2.getPu_checkin().compareToIgnoreCase(obj1.getPu_checkin());
                    else if (obj1.getPu_checkin() != null && obj2.getPu_checkout() != null)
                        return obj2.getPu_checkout().compareToIgnoreCase(obj1.getPu_checkin());
                    else if (obj1.getPu_checkout() != null && obj2.getPu_checkin() != null)
                        return obj2.getPu_checkin().compareToIgnoreCase(obj1.getPu_checkout());
                    else return obj2.getPu_checkout().compareToIgnoreCase(obj1.getPu_checkout());
                }
            });

            for (int j = 0; j < component_history_model.getData().size(); j++) {
                TextView check_tv = get_text_view(activity);
                if (component_history_model.getData().get(j).getPu_checkout() != null && !component_history_model.getData().get(j).getPu_checkout().equals("")) {
                    check_tv.setTextColor(AppUtils.getResColor(R.color.app_error));
                    check_tv.setText(activity.formatServerDate(component_history_model.getData().get(j).getPu_checkout()) + ", CheckOut");
                    child_layout.addView(check_tv);
                } else if (component_history_model.getData().get(j).getPu_checkin() != null && !component_history_model.getData().get(j).getPu_checkin().equals("")) {
                    check_tv.setTextColor(AppUtils.getResColor(R.color.app_text));
                    check_tv.setText(activity.formatServerDate(component_history_model.getData().get(j).getPu_checkin()) + ", CheckIn");
                    child_layout.addView(check_tv);
                }

            }
        }
        flip_in(child_layout);
    }

    private TextView get_text_view(Activity activity) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 5, 10, 5);
        TextView chk_tv = new TextView(activity);
        chk_tv.setLayoutParams(params);
        chk_tv.setPadding(10, 15, 5, 15);
//        chk_tv.setGravity(Gravity.CENTER);
        chk_tv.setBackgroundResource(R.drawable.bg_btm_line);
        GradientDrawable gd = (GradientDrawable) chk_tv.getCompoundDrawables()[0];
        if (gd != null) {
            chk_tv.setBackground(gd);
            gd.setStroke(1, AppUtils.getResColor(R.color.app_text));
        }
        chk_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        chk_tv.setVisibility(View.GONE);
        return chk_tv;
    }

    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    private String get_formated_date(String dateString) {
        Date date = null;
        try {
            date = activity.server_date_format.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return activity.DateTime_Format().format(date);
    }

    @SuppressLint("HandlerLeak")
    private void update_list(String url) {
        NetworkRequest.getComponents(activity, url, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    addData(DataHolder_Model.getInstance().getComponent_models());
                }
                // process incoming messages here
                // this will run in the thread, which instantiates it
            }
        });
    }

    private AlertDialog dialog;

    private void create_Historydialog(final Activity activity, int totl_hour, double used_decimal, double remain_decimal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.new_assethistory_dialog, null);

        ImageView close_btn = dialoglayout.findViewById(R.id.close_btn);
        LinearLayout linear_list = dialoglayout.findViewById(R.id.linear_list);
        TextView totl_hrs = dialoglayout.findViewById(R.id.totl_hrs);
        TextView used_hrs = dialoglayout.findViewById(R.id.used_hrs);
        TextView balance_hrs = dialoglayout.findViewById(R.id.balance_hrs);

        totl_hrs.setText(getResString("lbl_product_life") + " :" + totl_hour + " Hrs");
        used_hrs.setText(getResString("lbl_used") + " :" + get_hours_from_Decimal(used_decimal) + " Hrs");
        balance_hrs.setText(getResString("lbl_balance") + " :" + get_hours_from_Decimal(remain_decimal) + " Hrs");

        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        make_childview(linear_list);
        builder.setView(dialoglayout);
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    private void fade_anim(View view) {
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(2000);
        anim.setStartOffset(3000);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        view.startAnimation(anim);
    }


    MasterData_model masterData_model;

    private void serviceCall_1(final String m_id) {
            new NetworkRequest(activity).getMasterData(m_id, user_id, new ObjectListener() {
                @Override
                public void onResponse(Object obj) {
                    masterData_model= (MasterData_model) obj;
                    load_fragment(m_id);
                }

                @Override
                public void onError(String error) {
                    Log.e("error", "" + error);
                }
            });
    }

    private void load_fragment(String master_id) {
        Static_values.unique_id = Static_values.user_id + client_id + "" + master_id;
        Static_values.selected_Site_model = new Site_Model();
        Static_values.selectedMasterData_model = masterData_model;
        if (masterData_model != null) {
            Master_detail_fragment master_fragment = new Master_detail_fragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("img_urls", new ArrayList<>(Collections.singletonList(Static_values.selected_Site_model.getImagepath())));
            bundle.putStringArrayList("product_name", new ArrayList<>(Collections.singletonList(masterData_model.getMdata_item_series())));
            if (masterData_model.getMdata_asset().equals(""))
                bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("asset_series")));
            else
                bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("assets")));

            bundle.putString("page_type", "ASM_project");
            bundle.putInt("pos", 0);
            master_fragment.setArguments(bundle);
            LoadFragment.replace(master_fragment, activity, getResString("lbl_detail_st"));
        } else {
            show_snak(activity, getResString("lbl_nodata_admin_msg"));
        }
    }
}
