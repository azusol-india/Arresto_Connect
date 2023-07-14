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

import static app.com.arresto.Flavor_Id.FlavourInfo.Fl_CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResDrawable;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.load_image_file;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.INSPECTED_PDM;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.SITES_TOUPLOAD;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.downloaded_sites;
import static app.com.arresto.arresto_connect.constants.Static_values.group_id;
import static app.com.arresto.arresto_connect.constants.Static_values.logo_url;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.role_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_typ;
import static app.com.arresto.arresto_connect.custom_views.Font_type.set_background;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Profile_Model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.database.Data_daowload;
import app.com.arresto.arresto_connect.database.inspection_tables.Master_data_table;
import app.com.arresto.arresto_connect.database.inspection_tables.Sites_data_table;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.Get_Save_componnent_subasset;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.network.Upload_Pdm_Steps;
import app.com.arresto.arresto_connect.network.Upload_site_data;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.fragments.Master_detail_fragment;
import app.com.arresto.arresto_connect.ui.modules.add_data.ProMasterEdit;
import app.com.arresto.arresto_connect.ui.modules.inspection.InspectionListItems;

public class Inspector_search_listadapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<String> inspected_sites, inspected_pdm;
    private BaseActivity activity;
    private Data_daowload data_daowload;
    private Get_Save_componnent_subasset get_save_componnent_subasset;
    private Upload_site_data upload_site_data;
    private ArrayList<Site_Model> site_models;
    private String type;
    private MasterData_model masterData_model;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public Inspector_search_listadapter(BaseActivity activity, ArrayList<Site_Model> site_models, String type) {
        this.activity = activity;
        this.site_models = site_models;
        this.type = type;
        data_daowload = new Data_daowload(activity);
        get_save_componnent_subasset = new Get_Save_componnent_subasset(activity);
        upload_site_data = new Upload_site_data(activity);
        inspected_sites = mPrefrence.getArray_Data(SITES_TOUPLOAD);
        inspected_pdm = mPrefrence.getArray_Data(INSPECTED_PDM);
//        if (Main_Fragment.dwn_excl_btn != null) {
//            if (site_models.size() > 0)
//                ((LinearLayout) Main_Fragment.dwn_excl_btn.getParent()).setVisibility(View.VISIBLE);
//            else ((LinearLayout) Main_Fragment.dwn_excl_btn.getParent()).setVisibility(View.GONE);
//        }
    }

    public void addData(ArrayList<Site_Model> sites) {
        final int count_added = sites.size();
        final int positionStart = site_models.size() + 1;
        this.site_models.addAll(sites);
        notifyItemRangeInserted(positionStart, count_added);
    }

    public void updateData(ArrayList<Site_Model> sites) {
        this.site_models = sites;
        this.notifyDataSetChanged();
    }

    public boolean isLoaderVisible = false;
    private static final int VIEW_TYPE_LOADING = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    public void addLoading() {
        isLoaderVisible = true;
//        site_models.add(new Site_Model());
//        notifyItemInserted(site_models.size() - 1);
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
        TextView textView0, textView1, textView2, schdl_date, user_tv, assign_by_tv, assign_tv;
        RelativeLayout bg_layer;
        ImageView item_img, upload, delete_btn, add_user_btn, download;
        ImageView clandar_btn, right_arow;
        LinearLayout add_user_lay;
        SwipeRevealLayout swipe_lay;

        private ViewHolder(View view) {
            super(view);
            add_user_lay = view.findViewById(R.id.add_user_lay);
            swipe_lay = view.findViewById(R.id.swipe_lay);
            bg_layer = view.findViewById(R.id.bg_layer);
            textView0 = view.findViewById(R.id.inspector_tv0);
            textView1 = view.findViewById(R.id.inspector_tv1);
            textView2 = view.findViewById(R.id.inspector_tv2);
            schdl_date = view.findViewById(R.id.schdl_date);
            user_tv = view.findViewById(R.id.user_tv);
            assign_tv = view.findViewById(R.id.assign_tv);
            assign_by_tv = view.findViewById(R.id.assign_by_tv);
            right_arow = view.findViewById(R.id.right_arow);
            upload = view.findViewById(R.id.upload);
            download = view.findViewById(R.id.dowload);
            item_img = view.findViewById(R.id.item_img);
            add_user_btn = view.findViewById(R.id.add_user_btn);
            delete_btn = view.findViewById(R.id.delete_btn);
            clandar_btn = view.findViewById(R.id.clandar_btn);
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

    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder hldr, final int position) {
        if (hldr instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) hldr;
            final Site_Model site_model = site_models.get(position);
            String uniq_id = make_unique_id(position);

            if (group_id.equalsIgnoreCase("9")) {
//         work for dowload and upload button hide
                Log.e("unique id", " is " + uniq_id);
                Log.e("inspection sites", " is " + inspected_sites);
                viewBinderHelper.bind(holder.swipe_lay, uniq_id);
                if (Fl_CLIENT_ID.equalsIgnoreCase("931") || client_id.equalsIgnoreCase("931"))
                    viewBinderHelper.lockSwipe(uniq_id);

                if (!isNetworkAvailable(activity)) {
                    holder.upload.setVisibility(View.GONE);
                    holder.download.setVisibility(View.GONE);
//            } else if (site_model.getInspected_status().equals("Yes") && site_model.getApproved_status().equals("Pending")) {
//                holder.upload.setVisibility(View.GONE);
//                holder.download.setVisibility(View.GONE);
                } else {
                    if (downloaded_sites.contains(uniq_id)) {
                        holder.download.setVisibility(View.GONE);
                    } else {
                        holder.download.setVisibility(View.VISIBLE);
                    }

                    if (type.contains("periodic")) {
                        if (inspected_pdm.contains(uniq_id)) {
                            holder.upload.setVisibility(View.VISIBLE);
                            holder.download.setVisibility(View.GONE);
                        } else {
                            holder.upload.setVisibility(View.GONE);
                        }
                    } else {
                        if (inspected_sites.contains(uniq_id)) {
                            holder.upload.setVisibility(View.VISIBLE);
                            holder.download.setVisibility(View.GONE);
                        } else {
                            holder.upload.setVisibility(View.GONE);
                        }

                        holder.download.setVisibility(View.GONE);
                    }
                }
            } else if (type.equals("warehouse_assign")) {
                viewBinderHelper.bind(holder.swipe_lay, uniq_id);
                holder.upload.setVisibility(View.GONE);
                holder.download.setVisibility(View.GONE);
            } else {
                holder.upload.setVisibility(View.GONE);
                holder.download.setVisibility(View.GONE);
//              holder.bg_layer.setBackgroundResource(R.drawable.edittext_bg1);
            }
//        holder.upload.setVisibility(ShowcaseView.GONE);
//        holder.download.setVisibility(View.GONE);
            if (type.equals("scheduler")) {
                if (holder.upload.getVisibility() != View.VISIBLE) {
                    holder.delete_btn.setVisibility(View.VISIBLE);
                    holder.schdl_date.setVisibility(View.VISIBLE);
                    holder.clandar_btn.setVisibility(View.VISIBLE);
//                  holder.clandar_btn.setColorFilter(getResColor(R.color.button_txt));
                    try {
                        Date d = activity.server_date_format.parse(site_model.getScheduled_date());
                        holder.schdl_date.setText("Schedule Date: " + activity.Date_Format().format(d));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                holder.clandar_btn.setVisibility(View.GONE);
                holder.schdl_date.setVisibility(View.GONE);
                if (type.equals("myassets") || type.equals("client_periodic")) {
                    holder.add_user_lay.setVisibility(View.VISIBLE);
                    if (site_model.getUserInfo() != null && site_model.getUserInfo().getUacc_id() != null) {
                        holder.add_user_lay.setOnClickListener(new OnItemClickListener(position));
                        holder.delete_btn.setVisibility(View.VISIBLE);
                    } else {
                        holder.delete_btn.setVisibility(View.GONE);
                    }
                } else {
                    holder.add_user_lay.setVisibility(View.GONE);
                }
            }
            if (site_model.getLabel_user() != null) {
                holder.assign_tv.setVisibility(View.VISIBLE);
                holder.assign_by_tv.setVisibility(View.VISIBLE);

                if (group_id.equalsIgnoreCase("9") &&
                        (Fl_CLIENT_ID.equalsIgnoreCase("931") || client_id.equalsIgnoreCase("931")))
                    holder.right_arow.setVisibility(View.GONE);
                else
                    holder.right_arow.setVisibility(View.VISIBLE);
                if (!site_model.getLabel_user().equals("")) {
                    holder.add_user_btn.setImageDrawable(getResDrawable(R.drawable.exit_user));
                    holder.assign_by_tv.setText(getResString("lbl_issued_by")+" : " + site_model.getLabeled_by());
                    holder.assign_tv.setText(getResString("lbl_issued_to")+" : " + site_model.getLabel_user());
                } else {
                    holder.add_user_btn.setImageDrawable(getResDrawable(R.drawable.add_user));
                    holder.assign_by_tv.setText(getResString("lbl_issued_by")+" : None");
                    holder.assign_tv.setText(getResString("lbl_issued_to")+" : None");
                }
            } else {
                holder.add_user_btn.setImageDrawable(getResDrawable(R.drawable.add_user));
                holder.assign_tv.setVisibility(View.GONE);
                holder.assign_by_tv.setVisibility(View.GONE);
                holder.right_arow.setVisibility(View.GONE);
            }

            if (site_model.getUserInfo() != null && site_model.getUserInfo().getUacc_email() != null) {
                ((LinearLayout) holder.user_tv.getParent()).setVisibility(View.VISIBLE);
                holder.user_tv.setText(getResString("lbl_user") + " : " + site_model.getUserInfo().getFullname());
                ((LinearLayout) holder.user_tv.getParent()).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user_info_dialog(site_model.getUserInfo());
                    }
                });
            } else {
                if (site_model.userInfo == null) {
                    ((LinearLayout) holder.user_tv.getParent()).setVisibility(View.GONE);
                } else {
                    ((LinearLayout) holder.user_tv.getParent()).setVisibility(View.VISIBLE);
                    holder.user_tv.setText(getResString("lbl_user") + " : " + getResString("lbl_asset_not_registered"));
                }
            }

            // set all values here

            holder.textView0.setText(site_model.getClient_name());
            if (site_model.getSite_id() != null && !site_model.getSite_id().equals("")) {
                set_background(holder.textView0, Dynamic_Var.getInstance().getBtn_bg_clr(), "color_bg");
//            holder.textView0.setTextColor(Dynamic_Var.getInstance().getBtn_bg_clr());
                holder.textView1.setText(getResString("lbl_site_st")+" : " + site_model.getSite_id());
                holder.textView2.setText(site_model.getSite_address() + "\n" + site_model.getSite_city() + ", " + site_model.getSite_location());
                AppUtils.load_image(site_model.getImagepath(), holder.item_img);
            } else {
                set_background(holder.textView0, Dynamic_Var.getInstance().getBtn_bg_clr(), "color_bg");
//            holder.textView0.setTextColor(Dynamic_Var.getInstance().getBtn_bg_clr());
                holder.textView1.setText(getResString("lbl_asset")+" : " + (site_model.getMdata_item_series()));
                holder.textView2.setText("Barcode - " + site_model.getMdata_barcode() + "  \nRFID -" + site_model.getMdata_rfid() + "  \nUIN -" + site_model.getMdata_uin());
                AppUtils.load_image(site_model.getImagepath(), holder.item_img);
            }

            holder.delete_btn.setOnClickListener(new OnItemClickListener(position));
            if (!user_typ.equals("Client") && !user_typ.equals("Public")) {
                holder.download.setOnClickListener(new OnItemClickListener(position));
                holder.upload.setOnClickListener(new OnItemClickListener(position));
            }

            holder.clandar_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_Date_piker(site_model.getSiteID_id(), site_model.getMaster_id(), holder.schdl_date);
                }
            });

            holder.itemView.setOnClickListener(new OnItemClickListener(position));
            holder.bg_layer.setOnClickListener(new OnItemClickListener(position));
        }
    }

    private void serviceCall_1(final String m_id, final int position, final String unique_id, final String type) {
        new NetworkRequest(activity).getMasterData(m_id, user_id, new ObjectListener() {
            @Override
            public void onResponse(Object obj) {
                masterData_model = (MasterData_model) obj;
                if (type.equals("save")) {
                    Master_data_table master_data_table = new Master_data_table();
                    master_data_table.setData(client_id, m_id, AppUtils.getGson().toJson(obj));
                    AppController.getInstance().getDatabase().getMaster_dataDao().insert(master_data_table);
                    Save_to_sqlite(position, unique_id);
                } else {
                    load_fragment(position);
                }
            }

            @Override
            public void onError(String error) {
                Log.e("error", "" + error);
            }
        });
    }

    private void Save_to_sqlite(int position, String unique_id) {
        // TODO Auto-generated method stub
        Site_Model site_model = site_models.get(position);
        Sites_data_table sites_data_table = new Sites_data_table();
        sites_data_table.setData(user_id, client_id, unique_id, new Gson().toJson(site_model));
        AppController.getInstance().getDatabase().getSites_data_Dao().insert(sites_data_table);
        this.notifyDataSetChanged();
        notifyDataSetChanged();
    }

    private void check_mdata_database(String m_id, int position) {
        Site_Model site_model = site_models.get(position);
        data_daowload.open();
        long assetsize = data_daowload.checktblsize("components");
        long subassetsize = data_daowload.checktblsize("subasset");
        long acnprpdsize = data_daowload.DISTINCTtblsize("action_propose");

        String unique_id = make_unique_id(position);

        String entry = AppController.getInstance().getDatabase().getMaster_dataDao().checkEntry(m_id, client_id);
        if (entry != null && !entry.equals(""))
            Save_to_sqlite(position, unique_id);
        else
            serviceCall_1(m_id, position, unique_id, "save");

        if (type.contains("periodic")) {
            get_save_componnent_subasset.download_pdm(site_model.getMdata_item_series());
            if (!downloaded_sites.contains(unique_id))
                downloaded_sites.add(unique_id);

        } else {
            if (site_model.getTotalAsset().equals(String.valueOf(assetsize)) && site_model.getTotalSubAsset().equals(String.valueOf(subassetsize)) && site_model.getTotalAction_proposed().equals(String.valueOf(acnprpdsize))) {
                if (!downloaded_sites.contains(unique_id))
                    downloaded_sites.add(unique_id);
            } else {
                get_save_componnent_subasset.checked_data(String.valueOf(assetsize), String.valueOf(subassetsize), String.valueOf(acnprpdsize));
                get_save_componnent_subasset.getDataVolley(unique_id, site_model.getTotalAsset(), site_model.getTotalSubAsset(), site_model.getTotalAction_proposed());
            }
        }
        new NetworkRequest(activity).save_logs(All_Api.logs_api + user_id + "&eventType=Download Site " + site_model.getSite_id());
    }

    String make_unique_id(int position) {
        return user_id + client_id + site_models.get(position).getSiteID_id() + site_models.get(position).getMaster_id();
    }

    private void load_fragment(int position) {
        Static_values.unique_id = make_unique_id(position);
        Static_values.selected_Site_model = site_models.get(position);
        Static_values.selectedMasterData_model = masterData_model;
        if (site_models.get(position).getMdata_pro() == 1 && masterData_model.getNot_make_inspected() == 1) {
            LoadFragment.replace(ProMasterEdit.newInstance(type), activity, "Link QR code");
        } else {
            if (masterData_model != null) {
                Master_detail_fragment master_fragment = new Master_detail_fragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("img_urls", new ArrayList<>(Collections.singletonList(Static_values.selected_Site_model.getImagepath())));
                bundle.putStringArrayList("product_name", new ArrayList<>(Collections.singletonList(masterData_model.getMdata_item_series())));
                if (masterData_model.getMdata_asset().equals(""))
                    bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("asset_series")));
                else
                    bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("assets")));

                bundle.putString("page_type", type);
                bundle.putInt("pos", 0);
                master_fragment.setArguments(bundle);
                LoadFragment.replace(master_fragment, activity, "" + masterData_model.getMdata_uin());
            } else {
                show_snak(activity, getResString("lbl_nodata_admin_msg"));
            }
        }
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
                case R.id.upload:
                    if (isNetworkAvailable(activity)) {
                        if (type.contains("periodic")) {
                            new Upload_Pdm_Steps(activity).upload_steps(make_unique_id(position), new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.obj != null && msg.what == 1) {
                                        AppUtils.show_snak(activity, "data submitted success.");
                                        inspected_pdm = mPrefrence.getArray_Data(INSPECTED_PDM);
                                        notifyDataSetChanged();
                                    }
                                }
                            });
                        } else {
                            upload_site_data.startUpload(make_unique_id(position));
                        }
                    } else {
                        Toast.makeText(activity, getResString("lbl_network_alert"), Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.dowload:
                    Site_Model site_model = site_models.get(position);
                    String s1 = site_model.getMaster_id();
                    if (type.contains("periodic")) {
                        check_mdata_database(s1, position);
                    } else {
                        if (site_model.getInspected_status().equals("Yes") && site_model.getApproved_status().equals("Pending")) {
                            Toast.makeText(activity, getResString("lbl_sitealrdy_ins_msg"), Toast.LENGTH_LONG).show();
                        } else {
                            check_mdata_database(s1, position);
                        }
                    }
                    break;
                case R.id.add_user_lay:
                    showAlert_box(site_models.get(position), position);
                    break;
                case R.id.delete_btn:
                    delete_dialogbox(activity, position);
                    break;
                default:
                    Site_Model site_model1 = site_models.get(position);
                    String s11 = site_model1.getMaster_id();
                    if ((s11 == null || s11.equals("")) && site_model1.getMaster_data_id() != null && !site_model1.getMaster_data_id().equals("")) {
                        s11 = site_model1.getMaster_data_id();
                    }
                    InspectionListItems.selectedPosition.clear();
                    if (isNetworkAvailable(activity)) {
                        serviceCall_1(s11, position, "", "load_frag");
                    } else {
                        masterData_model = new Gson().fromJson(AppController.getInstance().getDatabase().getMaster_dataDao().getMaster_data(site_models.get(position).getMaster_id(), client_id), MasterData_model.class);
                        load_fragment(position);
                    }
                    break;
            }
        }
    }


    private void show_Date_piker(final String site_id, final String master_id,
                                 final TextView date_txt) {
        final Calendar c = Calendar.getInstance(), c2 = Calendar.getInstance();
        if (!date_txt.getText().toString().equals("")) {
            try {
                Date d = activity.Date_Format().parse(date_txt.getText().toString());
                c.setTime(d);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog.OnDateSetListener pDateSetListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                date_txt.setText(activity.Date_Format().format(c.getTime()));
                NetworkRequest networkRequest = new NetworkRequest(activity);
                HashMap<String, String> params = new HashMap<>();
                params.put("site_id", site_id);
                params.put("user_id", user_id);
                params.put("group_id", Static_values.group_id);
                params.put("scheduler_date", activity.server_date_format.format(c.getTime()));
                params.put("master_data_id", master_id);
                params.put("client_id", client_id);
                networkRequest.add_to_schedule(All_Api.insert_schedule, params, new Handler());
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(activity, pDateSetListener, mYear, mMonth, mDay);
        dialog.getDatePicker().setMinDate(c2.getTimeInMillis());
        dialog.show();
    }

    public void showAlert_box(Site_Model site_model, int position) {
        final Dialog builder = new Dialog(activity);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(true);
        builder.setContentView(R.layout.dialog_uin);
        TextView cancel_btn = builder.findViewById(R.id.cncl_btn);
        TextView ok_btn = builder.findViewById(R.id.ok_btn);
        final EditText edt_dialog = builder.findViewById(R.id.edt_dialog);
        builder.findViewById(R.id.ex_tv).setVisibility(View.GONE);
        if (!logo_url.equals("")) {
            ImageView logo_img = builder.findViewById(R.id.logo_img);
            load_image_file(logo_url, logo_img);
        }
        String labeled_by;
        if (site_model.getLabel_user() != null && !site_model.getLabel_user().equals("")) {
            edt_dialog.setEnabled(false);
            edt_dialog.setBackground(null);
            builder.findViewById(R.id.shadow_line).setVisibility(View.GONE);
            labeled_by = "";
            edt_dialog.setText(getResString("lbl_are_you_sure_to_exit_user_"));
            ok_btn.setText(getResString("lbl_yes"));
        } else {
            builder.findViewById(R.id.shadow_line).setVisibility(View.VISIBLE);
            edt_dialog.setBackground(ContextCompat.getDrawable(activity, R.drawable.round_border));
            edt_dialog.setHint("Enter User Name");
            labeled_by = Static_values.user_email;
        }
        String finalLabeled_by = labeled_by;
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!edt_dialog.getText().toString().equals("")) {
                    JSONArray dta = new JSONArray();
                    JSONObject params = new JSONObject();
                    try {
                        String lbl_user = "";
                        if (edt_dialog.isEnabled()) {
                            lbl_user = edt_dialog.getText().toString();
                        }
                        params.put("label_user", lbl_user);
                        params.put("mdata_uin", site_model.getMdata_uin());
                        params.put("mdata_id", site_model.getMaster_id());
                        params.put("user_id", user_id);
                        params.put("client_id", client_id);
                        params.put("time", System.currentTimeMillis() / 1000);
                        dta.put(params);
                        JSONObject post = new JSONObject();
                        post.put("client_id", client_id);
                        post.put("user_id", user_id);
                        post.put("data", dta);
                        send_data(post, All_Api.assign_user);
                        site_model.setLabel_user(lbl_user);
                        site_model.setLabeled_by(finalLabeled_by);
                        notifyItemChanged(position);
                        builder.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(activity, "Please Enter User Name.", Toast.LENGTH_LONG).show();
            }
        });
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });

        builder.show();

    }

    private void delete_dialogbox(Activity activity, final int position) {
        Site_Model site_model = site_models.get(position);
        final Dialog builder = new Dialog(activity, R.style.theme_dialog);
        builder.setCancelable(true);
        builder.setContentView(R.layout.dialog_alert);
        TextView delete_btn = builder.findViewById(R.id.delete_btn);
        TextView re_assign = builder.findViewById(R.id.ok_btn);
        final TextView msg_tv = builder.findViewById(R.id.msg_tv);
        final TextView heading_tv = builder.findViewById(R.id.heading_tv);
        heading_tv.setBackgroundColor(getResColor(R.color.app_text));
        heading_tv.setTextColor(getResColor(R.color.white));
        if (type.equals("scheduler")) {
            msg_tv.setText("Are you sure you want to delete this asset.");
        } else {
            re_assign.setVisibility(View.VISIBLE);
            msg_tv.setText("Are you sure you want to delete this asset from " + site_model.getUserInfo().getFullname() + " or Reassign to any other?");
            re_assign.setText(getResString("lbl_reassign"));
            re_assign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = All_Api.deleteRegisteredSite + site_model.getUserInfo().getUacc_id() + "&client_id=" + client_id + "&site_id=" + site_model.getSiteID_id() + "&master_data_id=" + site_model.getMaster_id() + "&cgrp_id=" + role_id;
                    delete_data(url, position, true);
                }
            });
        }
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url;
                if (type.equals("scheduler"))
                    url = All_Api.delete_schedule + user_id;
                else {
                    url = All_Api.deleteRegisteredSite + site_model.getUserInfo().getUacc_id();
                }
                delete_data(url + "&client_id=" + client_id + "&site_id=" + site_model.getSiteID_id() + "&master_data_id=" + site_model.getMaster_id() + "&cgrp_id=" + role_id, position, false);
                builder.dismiss();
            }
        });
        builder.show();
    }

    private void delete_data(String url, final int pos, boolean isReassign) {
//        fetch_GroupUsers(site_models.get(pos));
        NetworkRequest networkRequest = new NetworkRequest(activity);
        networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                if (isReassign) {
                    fetch_GroupUsers(site_models.get(pos));
                } else {
                    site_models.remove(pos);
                    notify_data();
                    show_snak(activity, getResString("lbl_prdct_dlet_msg"));
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request new " + message);
            }
        });
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

    ArrayList<GroupUsers> groupUsers;

    public void fetch_GroupUsers(Site_Model site_model) {
        groupUsers = new ArrayList<>();
        String url = All_Api.getGroup_Users + role_id + "&client_id=" + client_id + "&user_id=" + user_id;
        new NetworkRequest(activity).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (object.get("data") instanceof JSONArray) {
                            groupUsers = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), GroupUsers[].class)));
                            selectUsers(groupUsers, site_model);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }
        });
    }

    public void selectUsers(ArrayList<GroupUsers> groupUsers, Site_Model site_model) {
        final Dialog dialog = new Dialog(activity, R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        dialog.setCancelable(true);
        MaterialButton save_btn = dialog.findViewById(R.id.save_btn);
        TextView header = dialog.findViewById(R.id.header);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        header.setText(getResString("lbl_select_user"));
        RecyclerView lvLangs = dialog.findViewById(R.id._list);
        lvLangs.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        CustomRecyclerAdapter ad;
        save_btn.setText("Assign");
        if (groupUsers.size() > 0) {
            ad = new CustomRecyclerAdapter(activity, groupUsers);
            lvLangs.setAdapter(ad);
            dialog.show();
        } else {
            ad = null;
        }
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad != null && ad.lastSelected > -1) {
                    GroupUsers user = groupUsers.get(ad.lastSelected);
                    AssetRegisterToUser(user, site_model);
                    dialog.dismiss();
                } else
                    Toast.makeText(activity, "Please select an user!", Toast.LENGTH_SHORT).show();
            }
        });
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    void AssetRegisterToUser(GroupUsers groupUsers, Site_Model site_model) {
        NetworkRequest networkRequest = new NetworkRequest(activity);
        HashMap<String, String> params = new HashMap<>();
        params.put("site_id", site_model.getSiteID_id());
        params.put("master_data_id", site_model.getMaster_id());
        params.put("user_id", groupUsers.getUacc_id());
        params.put("client_id", client_id);
        params.put("group_id", groupUsers.getGroup_id());
        params.put("cgrp_id", groupUsers.getCgrp_id());
        networkRequest.add_to_schedule(All_Api.register_site, params, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    show_snak(activity, "Asset registered to user. Please refresh page to view changes.");
                }
                //       process incoming messages here
                //       this will run in the thread, which instantiates it
            }
        });
    }
}

