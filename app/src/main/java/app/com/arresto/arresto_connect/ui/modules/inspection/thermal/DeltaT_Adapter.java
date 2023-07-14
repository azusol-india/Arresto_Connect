package app.com.arresto.arresto_connect.ui.modules.inspection.thermal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.data.models.ThermalSubassetModel;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

public class DeltaT_Adapter extends RecyclerView.Adapter<DeltaT_Adapter.ViewHolder> {
    ArrayList data_object;
    ThermalImageProcessing activity;

    public DeltaT_Adapter(ThermalImageProcessing activity, ArrayList data_object) {
        this.activity = activity;
        this.data_object = data_object;
    }

    public void notifyData(ArrayList imag) {
        this.data_object = imag;
        notifyDataSetChanged();
    }

    public int getItemCount() {
        if (data_object != null)
            return data_object.size();
        else
            return 0;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.delta_t_list_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        holder.imageView.setImageResource(imgurls[position]);
        final ThermalSubassetModel item = (ThermalSubassetModel) data_object.get(position);
        holder.temp2_layer.setTag(item);
        holder.subasset_tv.setText(item.getSubAssetName());
        holder.temperature_tv.setText(activity.convertTemperature(item.getTemperature().getTemp()));
        holder.delta_tv.setText(activity.convertTemperature(item.getDelta_t()));
        holder.obser_tv.setText(item.getObservation().getObservation());
        holder.excerpt_tv.setText(item.getObservation().getAction_proposed());
        holder.result_tv.setText(item.getObservation().getResult());
        holder.remark_tv.setText(item.getRemark());
        if (item.getSubAsset2Name() != null && !item.getSubAsset2Name().equals("")) {
            holder.temp2_layer.setVisibility(View.VISIBLE);
            holder.subasset2_tv.setText(item.getSubAsset2Name());
            holder.temperature2_tv.setText(activity.convertTemperature(item.getTemperature2().getTemp()));
        } else {
            holder.temp2_layer.setVisibility(View.GONE);
        }
        holder.delete_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                data_object.remove(position);
                notifyItemRemoved(position);
                activity.printLog("delete clicked");
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView subasset_tv, temperature_tv, subasset2_tv, temperature2_tv, delta_tv, obser_tv, excerpt_tv, result_tv,remark_tv;
        LinearLayout temp2_layer;
        ImageView delete_btn;

        public ViewHolder(View v) {
            super(v);
            subasset_tv = v.findViewById(R.id.subasset_tv);
            temperature_tv = v.findViewById(R.id.temperature_tv);
            subasset2_tv = v.findViewById(R.id.subasset2_tv);
            temperature2_tv = v.findViewById(R.id.temperature2_tv);
            delta_tv = v.findViewById(R.id.delta_tv);
            obser_tv = v.findViewById(R.id.obser_tv);
            excerpt_tv = v.findViewById(R.id.excerpt_tv);
            result_tv = v.findViewById(R.id.result_tv);
            remark_tv = v.findViewById(R.id.remark_tv);
            temp2_layer = v.findViewById(R.id.temp2_layer);
            delete_btn = v.findViewById(R.id.delete_btn);
        }
    }
}
