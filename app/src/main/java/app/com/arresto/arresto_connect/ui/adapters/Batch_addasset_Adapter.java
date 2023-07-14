/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.adapters;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Batch_addasset_Adapter extends RecyclerView.Adapter<Batch_addasset_Adapter.ViewHolder>{
    ArrayList<String> total_batch, scaned_data;
    LinearLayout.LayoutParams layoutParams;
    private Activity context;

    public Batch_addasset_Adapter(Activity activity, ArrayList<String> total_batch, ArrayList<String> scaned_data){
        this.context = activity;
        this.total_batch = total_batch;
        this.scaned_data = scaned_data;
        layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 100);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(5, 5, 5, 5);
    }

    public void addData(ArrayList<String> scaned_data){
        this.scaned_data = scaned_data;
    }

    public long getItemId(int position){
        return position;
    }

    @Override
    public int getItemCount(){
        if (total_batch != null)
            return total_batch.size();
        else
            return 0;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View v = new TextView(context);
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suggetion_adptr, parent, false);
        return new ViewHolder(v);
    }

    public void onBindViewHolder(final ViewHolder holder, final int position){
        holder.itemView.setLayoutParams(layoutParams);
//        holder.batch_txt.setLayoutParams(layoutParams);

        holder.batch_txt.setText(total_batch.get(position));
        if (!scaned_data.get(position).equals("")){
            holder.itemView.setBackgroundColor(Color.YELLOW);
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
        }


    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView batch_txt;

        public ViewHolder(View v){
            super(v);
//            batch_txt = (TextView) v.findViewById(R.id.suggestion_list_textView);
            batch_txt = (TextView) v;
            // batch_txt.setBackgroundColor(Color.WHITE);
            batch_txt.setGravity(Gravity.CENTER);

        }
    }

}
