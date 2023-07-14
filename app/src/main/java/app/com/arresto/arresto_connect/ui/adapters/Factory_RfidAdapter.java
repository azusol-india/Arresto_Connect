package app.com.arresto.arresto_connect.ui.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.custom_views.Font_type;
import app.com.arresto.arresto_connect.database.factory_tables.FactoryMasterTable;
import app.com.arresto.arresto_connect.database.factory_tables.Factory_BachTable;
import app.com.arresto.arresto_connect.ui.modules.factory_data.UpdateRFID;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

public class Factory_RfidAdapter extends RecyclerView.Adapter<Factory_RfidAdapter.ViewHolder> {
    Activity activity;
    private List data;
    private boolean isBatch;

    public Factory_RfidAdapter(Activity activity, List data, boolean isBatch) {
        this.activity = activity;
        this.data = data;
        this.isBatch = isBatch;
    }

    public void addData(List data) {
        this.data = data;
        this.notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Factory_RfidAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.factory_item, parent, false);
        // set the view's size, margins, paddings and bg_layer parameters
        return new Factory_RfidAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Factory_RfidAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (isBatch) {
            final Factory_BachTable factory_bachTable = (Factory_BachTable) data.get(position);
            holder.batch_tv.setText(getResString("lbl_batch_no_st") + " : " + factory_bachTable.getBatch_no());
            holder.uin_tv.setText(getResString("lbl_serial_from") + " : " + factory_bachTable.getSerial_from());
            holder.barcode_tv.setText(getResString("lbl_serial_to") + " : " + factory_bachTable.getSerial_to());
            holder.delete_btn.setVisibility(View.VISIBLE);
            holder.delete_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_alert(position, factory_bachTable.getId());
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoadFragment.replace(UpdateRFID.newInstance(factory_bachTable.getId()), activity, "Update RFID");
                }
            });
        } else {
            final FactoryMasterTable factoryMasterModel = (FactoryMasterTable) data.get(position);
            holder.uin_tv.setText("UIN : " + factoryMasterModel.getMdata_uin());
//            holder.batch_tv.setText("Batch No. : " + factoryMasterModel.getMdata_batch());
//            holder.barcode_tv.setText("Barcode: " + factoryMasterModel.getMdata_barcode());
            holder.batch_tv.setVisibility(View.GONE);
            holder.barcode_tv.setVisibility(View.GONE);

            if (!factoryMasterModel.getMdata_rfid().equals("")) {
                Font_type.set_background(holder.itemView, getResColor(R.color.app_green), "round_bg");
//                holder.itemView.setBackgroundColor(getResColor(R.color.button_bg));
            } else {
                Font_type.set_background(holder.itemView, getResColor(R.color.app_background), "round_bg");
//                holder.itemView.setBackgroundColor(getResColor(R.color.app_background));
            }
        }

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView batch_tv,  uin_tv, barcode_tv;
        ImageView delete_btn;

        public ViewHolder(View itemView) {
            super(itemView);
            uin_tv = itemView.findViewById(R.id.uin_tv);
            batch_tv = itemView.findViewById(R.id.batch_tv);
            barcode_tv = itemView.findViewById(R.id.barcode_tv);
            delete_btn = itemView.findViewById(R.id.delete_btn);
        }
    }

    private void show_alert(final int pos, final long id) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(getResString("lbl_confirmation"))
                .setMessage(getResString("lbl_delete_this_data"))
                .setPositiveButton(getResString("lbl_delete"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        AppController.getInstance().getDatabase().getFactory_bachTable_dao().deleteBatch(id);
                        AppController.getInstance().getDatabase().getFactoryMasterTable_dao().deleteMaster(id);
                        data.remove(pos);
                        Factory_RfidAdapter.this.notifyDataSetChanged();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
}