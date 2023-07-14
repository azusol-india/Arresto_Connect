package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;

public class MeanAdapter extends RecyclerView.Adapter<MeanAdapter.ViewHolder> {
    private Fragment fragment;
    ArrayList<String> data;

    public MeanAdapter(Fragment fragment, ArrayList<String> meanArray) {
        this.fragment = fragment;
        this.data = meanArray;
    }

    public int getItemCount() {
        return data.size();
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.mean_item, parent, false);
        // set the view's size, margins, paddings and bg_layer parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    public void update(ArrayList<String> meanArray) {
        this.data.addAll(meanArray);
        notifyDataSetChanged();
    }

    public void addItem(String s) {
        data.add(0, s);
        notifyItemInserted(0);
//        notifyDataSetChanged();
    }

    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final String s = data.get(position);
        holder.mean_tv.setText(s);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mean_tv;

        public ViewHolder(View v) {
            super(v);
            mean_tv = v.findViewById(R.id.mean_tv);
        }
    }


}

