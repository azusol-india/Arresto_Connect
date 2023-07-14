package app.com.arresto.arresto_connect.ui.adapters;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.com.arresto.Flavor_Id.FlavourInfo;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Client_model;
import app.com.arresto.arresto_connect.ui.fragments.Dealer_detailFragment;
import app.com.arresto.arresto_connect.ui.fragments.Dealer_info_fragment;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Base_Fragment;

public class Dealer_Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List data;
    public Dealer_info_fragment dealerInfoFragment;
    public String type;

    public Dealer_Adapter(Dealer_info_fragment dealerInfoFragment, List data) {
        this.dealerInfoFragment = dealerInfoFragment;
        this.data = data;
    }


    public void update_list(List data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.dealer_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, final int position) {
        final ViewHolder holder = (ViewHolder) h;
        final Client_model temp_client = (Client_model) data.get(position);
        holder.name_tv.setText(temp_client.getClientName());
        holder.address_tv.setText(temp_client.getClientAddress());
        holder.distance_tv.setText(EC_Base_Fragment.round(temp_client.getDistance(), 2) + FlavourInfo.unit_name);
        holder.map_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                String map = "http://maps.google.co.in/maps?q=" + temp_client.getClientAddress();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                dealerInfoFragment.getActivity().startActivity(intent);
            }
        });

        holder.click_lay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadFragment.replace(Dealer_detailFragment.newInstance(temp_client, "dealer"), dealerInfoFragment.getActivity(), AppUtils.getResString("lbl_dealer_info"));
                dealerInfoFragment.dismiss();
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name_tv, address_tv, distance_tv;
        LinearLayout click_lay, map_lay;
//        RelativeLayout  call_lay;

        public ViewHolder(View itemView) {
            super(itemView);
            distance_tv = itemView.findViewById(R.id.distance_tv);
            name_tv = itemView.findViewById(R.id.name_tv);
            address_tv = itemView.findViewById(R.id.address_tv);

            click_lay = itemView.findViewById(R.id.click_lay);
            map_lay = itemView.findViewById(R.id.map_lay);
        }
    }

}
