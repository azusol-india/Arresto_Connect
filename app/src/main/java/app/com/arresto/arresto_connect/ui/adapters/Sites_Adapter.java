package app.com.arresto.arresto_connect.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.ECSites;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_LinePager;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Report_Fragment;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.LoadFragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

public class Sites_Adapter extends RecyclerView.Adapter<Sites_Adapter.ViewHolder> {
    List<ECSites> ecSitesList;
    Activity activity;
    String page_type;

    public Sites_Adapter(Activity activity, List<ECSites> ecSitesList, String page_type) {
        this.ecSitesList = ecSitesList;
        this.activity = activity;
        this.page_type = page_type;
    }

    public void update_Sitelist(List<ECSites> ecSitesList) {
        this.ecSitesList = ecSitesList;
    }

    @NonNull
    @Override
    public Sites_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ec_list, viewGroup, false);
        // set the view's size, margins, paddings and bg_layer parameters
        return new Sites_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull Sites_Adapter.ViewHolder h, int i) {
        final ECSites ecSites = ecSitesList.get(i);
        h.name_tv.setText(ecSites.getSiteName());
        h.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataHolder_Model.getInstance().setSlctd_site(ecSites);
                if (page_type.equalsIgnoreCase("reports")) {
                    LoadFragment.replace(new EC_Report_Fragment(),  activity, getResString("lbl_ec_reports"));
                } else {
                    LoadFragment.replace(new EC_LinePager(),  activity, getResString("lbl_sub_sites"));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return ecSitesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv;

        public ViewHolder(View itemView) {
            super(itemView);
            name_tv = itemView.findViewById(R.id.submenu);
        }
    }
}
