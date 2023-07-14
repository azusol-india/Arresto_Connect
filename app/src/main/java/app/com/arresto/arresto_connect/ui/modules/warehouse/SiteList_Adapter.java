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

package app.com.arresto.arresto_connect.ui.modules.warehouse;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResDrawable;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.selected_Site_model;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.custom_views.Font_type.set_background;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.widget.ImageViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

public class SiteList_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private BaseActivity activity;
    private Fragment baseFragment;
    private ArrayList<Site_Model> site_models, slectedSites;
    private String type;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public SiteList_Adapter(Base_Fragment baseFragment, ArrayList<Site_Model> site_models, String type) {
        this.baseFragment = baseFragment;
        this.activity = baseFragment.baseActivity;
        this.site_models = site_models;
        this.type = type;
        slectedSites = new ArrayList<>();
        slectedSites.addAll(site_models);
    }

    public void addData(ArrayList<Site_Model> sites) {
        final int count_added = sites.size();
        final int positionStart = site_models.size() + 1;
        this.site_models.addAll(sites);
        slectedSites.addAll(sites);
        notifyItemRangeInserted(positionStart, count_added);
    }

    public void updateData(ArrayList<Site_Model> sites) {
        this.site_models = sites;
        slectedSites.addAll(sites);
        notifyDataSetChanged();
    }

    public boolean isLoaderVisible = false;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    public void addLoading() {
        isLoaderVisible = true;
    }

    public void removeLoading() {
        isLoaderVisible = false;
        int position = site_models.size() - 1;
        if (getItemViewType(position) == VIEW_TYPE_LOADING) {
            Site_Model item = getItem(position);
            if (item != null) {
                site_models.remove(position);
                notifyItemRemoved(position);
            }
        }
    }

    public void clear() {
        site_models.clear();
        notifyDataSetChanged();
    }

    Site_Model getItem(int position) {
        return site_models.get(position);
    }

    public ArrayList<Site_Model> getData() {
        return site_models;
    }

    @Override
    public int getItemViewType(int position) {
        if (isLoaderVisible) {
            return position == site_models.size() - 1 ? VIEW_TYPE_LOADING : VIEW_TYPE_NORMAL;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    public int getItemCount() {
        return site_models.size();
    }

    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_NORMAL:
                return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.inspector_search_listadeptor, parent, false));
            case VIEW_TYPE_LOADING:
                return new ProgressHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false));
            default:
                return null;
        }


    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView0, textView1, textView2, schdl_date, user_tv, assign_by_tv, assign_tv, add_lable, status_tv;
        RelativeLayout bg_layer;
        ImageView item_img, upload, delete_btn, add_user_btn, download;
        ImageView clandar_btn, right_arow, status_ic;
        SwipeRevealLayout swipe_lay;
        CheckBox slect_chek;
        LinearLayout add_user_lay;

        private ViewHolder(View view) {
            super(view);
            swipe_lay = view.findViewById(R.id.swipe_lay);
            bg_layer = view.findViewById(R.id.bg_layer);
            textView0 = view.findViewById(R.id.inspector_tv0);
            textView1 = view.findViewById(R.id.inspector_tv1);
            textView2 = view.findViewById(R.id.inspector_tv2);
            schdl_date = view.findViewById(R.id.schdl_date);
            user_tv = view.findViewById(R.id.user_tv);
            status_ic = view.findViewById(R.id.status_ic);
            status_tv = view.findViewById(R.id.status_tv);
            assign_tv = view.findViewById(R.id.assign_tv);
            assign_by_tv = view.findViewById(R.id.assign_by_tv);
            right_arow = view.findViewById(R.id.right_arow);
            upload = view.findViewById(R.id.upload);
            download = view.findViewById(R.id.dowload);
            item_img = view.findViewById(R.id.item_img);
            add_user_btn = view.findViewById(R.id.add_user_btn);
            delete_btn = view.findViewById(R.id.delete_btn);
            clandar_btn = view.findViewById(R.id.clandar_btn);
            slect_chek = view.findViewById(R.id.slect_chek);

            add_user_lay = view.findViewById(R.id.add_user_lay);
            add_lable = view.findViewById(R.id.add_lable);
        }
    }

    public class ProgressHolder extends RecyclerView.ViewHolder {
        ProgressHolder(View itemView) {
            super(itemView);
//            ButterKnife.bind(this, itemView);
        }
    }

    private void notify_data() {
        this.notifyDataSetChanged();
    }

    public ArrayList<Site_Model> getSelected_Data() {
        return slectedSites;
    }

//    public boolean isSelectEnable;

    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder hldr, final int position) {
        if (hldr instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) hldr;
            final Site_Model site_model = site_models.get(position);
            String uniq_id = make_unique_id(position);
            holder.slect_chek.setVisibility(View.VISIBLE);
//            if (isSelectEnable) {
                if (slectedSites.contains(site_model)) {
                    holder.slect_chek.setChecked(true);
                } else {
                    holder.slect_chek.setChecked(false);
                }
                holder.slect_chek.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        if (b) {
                            if (!slectedSites.contains(site_model)) {
                                slectedSites.add(site_model);
                            }
                        } else {
                            slectedSites.remove(site_model);
                        }
                    }
                });
//            }

            holder.upload.setVisibility(View.GONE);
            holder.download.setVisibility(View.GONE);
            holder.clandar_btn.setVisibility(View.GONE);
            holder.schdl_date.setVisibility(View.GONE);

            if (site_model.getInspection_status() != null && !site_model.getInspection_status().equals("")) {
                ((LinearLayout) holder.status_tv.getParent()).setVisibility(View.VISIBLE);
                setStatus(site_model.getInspection_status(), holder.status_tv, holder.status_ic);
            } else {
                ((LinearLayout) holder.status_tv.getParent()).setVisibility(View.GONE);
            }

            if (site_model.getLabel_user() != null) {
                holder.assign_tv.setVisibility(View.VISIBLE);
                holder.assign_by_tv.setVisibility(View.VISIBLE);
                holder.right_arow.setVisibility(View.VISIBLE);

                if (!site_model.getLabel_user().equals("")) {
                    holder.assign_by_tv.setText(getResString("lbl_issued_by")+" : " + site_model.getLabeled_by());
                    holder.assign_tv.setText(getResString("lbl_issued_to")+" : " + site_model.getLabel_user());
                } else {
                    holder.assign_by_tv.setText(getResString("lbl_issued_by")+" : None");
                    holder.assign_tv.setText(getResString("lbl_issued_to")+" : None");
                }
            } else {
                holder.add_user_btn.setImageDrawable(getResDrawable(R.drawable.add_user));
                holder.assign_tv.setVisibility(View.GONE);
                holder.assign_by_tv.setVisibility(View.GONE);
                holder.right_arow.setVisibility(View.GONE);
            }

            if (type.equals("warehouse_assign") || type.equals("returnAsset")) {
                holder.assign_by_tv.setVisibility(View.VISIBLE);
                holder.right_arow.setVisibility(View.VISIBLE);
                viewBinderHelper.bind(holder.swipe_lay, uniq_id);
                holder.add_user_lay.setVisibility(View.VISIBLE);
                holder.add_lable.setVisibility(View.VISIBLE);
                if (type.equals("returnAsset"))
                    holder.add_lable.setText(getResString("lbl_select_status"));
                else
                    holder.add_lable.setText(getResString("lbl_assign_user"));
            }

            if (site_model.getUserInfo() != null && site_model.getUserInfo().getUacc_email() != null) {
//                holder.delete_btn.setVisibility(View.VISIBLE);
                ((LinearLayout) holder.user_tv.getParent()).setVisibility(View.VISIBLE);
                holder.add_user_lay.setVisibility(View.VISIBLE);
                holder.user_tv.setText("User : " + site_model.getUserInfo().getFullname());
                ((LinearLayout) holder.user_tv.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user_info_dialog(site_model.getUserInfo());
                    }
                });
            } else {
//                holder.delete_btn.setVisibility(View.GONE);
                if (site_model.userInfo == null) {
                    ((LinearLayout) holder.user_tv.getParent()).setVisibility(View.GONE);
                } else {
                    ((LinearLayout) holder.user_tv.getParent()).setVisibility(View.VISIBLE);
                    holder.user_tv.setText("User : Not Registered");
                }
            }

            // set all values here

            holder.textView0.setText(site_model.getClient_name());
            if (site_model.getSite_id() != null && !site_model.getSite_id().equals("")) {
                set_background(holder.textView0, Dynamic_Var.getInstance().getBtn_bg_clr(), "color_bg");
//            holder.textView0.setTextColor(Dynamic_Var.getInstance().getBtn_bg_clr());
                holder.textView1.setText("Site : " + site_model.getSite_id());
                holder.textView2.setText(site_model.getSite_address() + "\n" + site_model.getSite_city() + ", " + site_model.getSite_location());
                AppUtils.load_image(site_model.getImagepath(), holder.item_img);
            } else {
                set_background(holder.textView0, Dynamic_Var.getInstance().getBtn_bg_clr(), "color_bg");
//            holder.textView0.setTextColor(Dynamic_Var.getInstance().getBtn_bg_clr());
                holder.textView1.setText("Asset : " + (site_model.getMdata_item_series()));
                holder.textView2.setText("Barcode - " + site_model.getMdata_barcode() + "  \nRFID -" + site_model.getMdata_rfid() + "  \nUIN -" + site_model.getMdata_uin());
                AppUtils.load_image(site_model.getImagepath(), holder.item_img);
            }

//            holder.delete_btn.setOnClickListener(new OnItemClickListener(position));
            holder.add_user_lay.setOnClickListener(new OnItemClickListener(position));
            holder.itemView.setOnClickListener(new OnItemClickListener(position));
            holder.bg_layer.setOnClickListener(new OnItemClickListener(position));
        }
    }

    private void setStatus(String inspection_status, TextView status_tv, ImageView status_ic) {
        switch (inspection_status) {
            case "over":
                status_tv.setText("Inspection Overdue");
                ImageViewCompat.setImageTintList(status_ic, ColorStateList.valueOf(getResColor(R.color.app_error)));
                break;
            case "due":
                status_tv.setText("Inspection Due");
                ImageViewCompat.setImageTintList(status_ic, ColorStateList.valueOf(getResColor(R.color.yellow_bg)));
                break;
            default:
                status_tv.setText("Inspected");
                ImageViewCompat.setImageTintList(status_ic, ColorStateList.valueOf(getResColor(R.color.app_green)));
                break;
        }
    }

    String make_unique_id(int position) {
        return user_id + client_id + site_models.get(position).getSiteID_id() + site_models.get(position).getMaster_id();
    }

    private class OnItemClickListener implements View.OnClickListener {
        private int position;

        OnItemClickListener(int pos) {
            this.position = pos;
        }

        @SuppressLint("HandlerLeak")
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.add_user_lay:
                    if (baseFragment instanceof Scan_warehouse_asset) {
                        ((Scan_warehouse_asset) baseFragment).status_Dialog(position);
                    } else if (baseFragment instanceof Assign_user_fragment) {
                        ((Assign_user_fragment) baseFragment).assignSingle(position);
                    }
                    break;
                case R.id.delete_btn:
                    if (baseFragment instanceof Assign_user_fragment) {
                        ((Assign_user_fragment) baseFragment).assignSingle(position);
                    }
                    break;
                default:
//                    isSelectEnable = !isSelectEnable;
//                    notify_data();
                    break;
            }
        }
    }


    public void user_info_dialog(Profile_Model profile) {
        final Dialog builder = new Dialog(activity);
//        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(true);
        builder.setContentView(R.layout.dialog_user_info);
        TextView name_tv = builder.findViewById(R.id.name_tv);
        TextView email_tv = builder.findViewById(R.id.email_tv);
        TextView phone_tv = builder.findViewById(R.id.phone_tv);
        TextView emp_id_tv = builder.findViewById(R.id.emp_id_tv);
        TextView ok_btn = builder.findViewById(R.id.ok_btn);
        ImageView profil_pic = builder.findViewById(R.id.profil_pic);
        name_tv.setText("Name: " + profile.getFullname());
        email_tv.setText("Email: " + profile.getUacc_email());
        phone_tv.setText("Phone: " + profile.getUpro_phone());
        emp_id_tv.setText("Emp. id : " + profile.getEmp_id());
        AppUtils.load_profile(profile.getUpro_image(), profil_pic);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
            }
        });
        builder.show();
        builder.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    public void send_data(JSONObject params, String url) {
        NetworkRequest network_request = new NetworkRequest(activity);
        network_request.make_contentpost_request(url, params, new NetworkRequest.VolleyResponseListener() {
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
//                            update_list();
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

}

