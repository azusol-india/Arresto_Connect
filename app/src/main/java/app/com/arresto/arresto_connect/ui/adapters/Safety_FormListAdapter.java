package app.com.arresto.arresto_connect.ui.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.data.models.CustomForm_Model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.R;

/**
 * Created by AZUSOL-PC-02 on 8/5/2019.
 */
public class Safety_FormListAdapter extends RecyclerView.Adapter<Safety_FormListAdapter.ViewHolder> {
    private ArrayList<CustomForm_Model> formList;
    Activity activity;
    private boolean selected[];

    public Safety_FormListAdapter(Activity activity) {
        this.activity = activity;
        this.formList = DataHolder_Model.getInstance().getCustomViews_models();
        selected = new boolean[formList.size()];
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.spiner_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int i) {
        CustomForm_Model customForm_model = formList.get(i);
        holder.spnr_text.setText(customForm_model.getForm_name());
        holder.check_btn.setChecked(selected[i]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selected[i] = !selected[i];
                notifyItemChanged(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return formList.size();
    }

    public boolean[] getSelectedItem() {
        return selected;
    }

    public void update_list(ArrayList<CustomForm_Model> customViews_models) {
        formList = customViews_models;
        selected = new boolean[formList.size()];
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView spnr_text;
        CheckBox check_btn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            spnr_text = itemView.findViewById(R.id.spnr_text);
            check_btn = itemView.findViewById(R.id.check_btn);
        }
    }
}
