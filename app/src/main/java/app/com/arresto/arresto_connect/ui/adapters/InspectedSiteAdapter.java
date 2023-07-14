package app.com.arresto.arresto_connect.ui.adapters;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.selectedMasterData_model;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.custom_views.Font_type.set_background;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.database.AppDatabase;
import app.com.arresto.arresto_connect.network.Upload_Pdm_Steps;
import app.com.arresto.arresto_connect.network.Upload_site_data;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Master_detail_fragment;

public class InspectedSiteAdapter extends RecyclerView.Adapter<InspectedSiteAdapter.ViewHolder> {
    private AppCompatActivity context;
    ArrayList<Site_Model> sites;
    String listType;

    public InspectedSiteAdapter(Activity c, ArrayList<Site_Model> sites, String listType) {
        context = (AppCompatActivity) c;
        this.sites = sites;
        this.listType = listType;
    }


    public long getItemId(int position) {
        return position;
    }

    public void add_item(ArrayList<Site_Model> sites) {
        this.sites = sites;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (sites != null)
            return sites.size();
        else
            return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView0, textView1, textView2;
        ImageView item_img, delete_btn;
        MaterialButton inspect_btn, upload_btn;

        ViewHolder(View v) {
            super(v);
            item_img = v.findViewById(R.id.item_img);
            delete_btn = v.findViewById(R.id.delete_btn);
            inspect_btn = v.findViewById(R.id.inspect_btn);
            upload_btn = v.findViewById(R.id.upload_btn);
            textView0 = v.findViewById(R.id.inspector_tv0);
            textView1 = v.findViewById(R.id.inspector_tv1);
            textView2 = v.findViewById(R.id.inspector_tv2);
        }
    }

    //---returns an ImageView view---
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.site_list_item, parent, false);
        return new ViewHolder(v);
    }

//    private Map<String, Bitmap> bitmapMap = Collections.synchronizedMap(new HashMap<String, Bitmap>());

    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Site_Model site_model = sites.get(position);
        String uniq_id = user_id + client_id + site_model.getSiteID_id() + site_model.getMaster_id();
        holder.textView0.setText(site_model.getClient_name());

        if (site_model.getSite_id() != null && !site_model.getSite_id().equals("")) {
            set_background(holder.textView0, Dynamic_Var.getInstance().getBtn_bg_clr(), "color_bg");
            holder.textView1.setText("Site : " + site_model.getSite_id());
            holder.textView2.setText(site_model.getSite_address() + "\n" + site_model.getSite_city() + ", " + site_model.getSite_location());
            AppUtils.load_image(site_model.getImagepath(), holder.item_img);
        } else {
            set_background(holder.textView0, Dynamic_Var.getInstance().getBtn_bg_clr(), "color_bg");
            holder.textView1.setText("Asset : " + (site_model.getMdata_item_series()));
            holder.textView2.setText("Barcode - " + site_model.getMdata_barcode() + "  \nRFID -" + site_model.getMdata_rfid() + "  \nUIN -" + site_model.getMdata_uin());
            AppUtils.load_image(site_model.getImagepath(), holder.item_img);
        }

        if (listType.equals("periodic")){
            holder.inspect_btn.setText("Start Maintenance");
        }

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.upload_btn:
                        if (listType.equals("inspection"))
                            new Upload_site_data(context).startUpload(uniq_id);
                        else
                            new Upload_Pdm_Steps(context).upload_steps(uniq_id, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    if (msg.obj != null && msg.what == 1) {
                                        AppUtils.show_snak(context, "data submitted success.");
                                        HomeActivity.homeActivity.submit_action("pdm_report");
                                    }
                                }
                            });
                        break;
                    case R.id.inspect_btn:
                        load_fragment(uniq_id, position);
                        break;
                    case R.id.delete_btn:
                        check_alert(position, uniq_id);
                        break;
                }
            }
        };
        holder.delete_btn.setOnClickListener(clickListener);
        holder.inspect_btn.setOnClickListener(clickListener);
        holder.upload_btn.setOnClickListener(clickListener);
    }

    private void check_alert(int position, String uniq_id) {
        final Dialog dialog = new Dialog(context, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_alert);
        final TextView heading_tv = dialog.findViewById(R.id.heading_tv);
        heading_tv.setText(getResString("lbl_confirmation"));
        final TextView msg_tv = dialog.findViewById(R.id.msg_tv);
        msg_tv.setText(getResString("lbl_dlet_cnf_msg"));
        MaterialButton ok_btn = dialog.findViewById(R.id.ok_btn);
        MaterialButton delete_btn = dialog.findViewById(R.id.delete_btn);
        ok_btn.setText(getResString("lbl_cncl_st"));
        delete_btn.setVisibility(View.VISIBLE);
        ok_btn.setVisibility(View.VISIBLE);
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listType.equals("inspection"))
                    AppUtils.delete_uploadedsite_data(uniq_id, sites.get(position).getMaster_id());
                else
                    AppUtils.delete_uploadedPdm_data(uniq_id, sites.get(position).getMaster_id());
                sites.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, sites.size());
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    private void load_fragment(String unique_id, int position) {
        Static_values.unique_id = unique_id;
        Static_values.selected_Site_model = sites.get(position);
        AppDatabase db = AppController.getInstance().getDatabase();
        selectedMasterData_model = new Gson().fromJson(db.getMaster_dataDao().getMaster_data(sites.get(position).getMaster_id(), client_id), MasterData_model.class);
        if (selectedMasterData_model != null) {
            Master_detail_fragment master_fragment = new Master_detail_fragment();
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("img_urls", new ArrayList<>(Collections.singletonList(Static_values.selected_Site_model.getImagepath())));
            bundle.putStringArrayList("product_name", new ArrayList<>(Collections.singletonList(selectedMasterData_model.getMdata_item_series())));
            if (selectedMasterData_model.getMdata_asset().equals(""))
                bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("asset_series")));
            else
                bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("assets")));

            bundle.putString("page_type", listType);
            bundle.putInt("pos", 0);
            master_fragment.setArguments(bundle);
            LoadFragment.replace(master_fragment, context, "" + selectedMasterData_model.getMdata_uin());
        } else {
            show_snak(context, getResString("lbl_nodata_admin_msg"));
        }
    }
}
