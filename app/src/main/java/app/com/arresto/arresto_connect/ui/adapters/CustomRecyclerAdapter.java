package app.com.arresto.arresto_connect.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.GroupUsers;
import app.com.arresto.arresto_connect.interfaces.OnItemClickListener;

public class CustomRecyclerAdapter extends RecyclerView.Adapter<CustomRecyclerAdapter.ViewHolder> {
    Context context;
    List items;
    OnItemClickListener clickListener;
    public int lastSelected = -1;

    public CustomRecyclerAdapter(Context context, List items) {
        this.context = context;
        this.items = items;
    }

    public CustomRecyclerAdapter(Context context, List items, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.items = items;
        this.clickListener = onItemClickListener;
    }

    public void UpdateData(List items) {
        this.items = items;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (items != null)
            return items.size();
        else
            return 0;
    }


    public Object getItem(int position) {
        if (items != null)
            return items.get(position);
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_list_item, parent, false);
        // set the view's size, margins, paddings and bg_layer parameters
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomRecyclerAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Object item = items.get(position);
        String txt = item.toString();
        if (txt != null && !txt.equals(""))
            txt = txt.substring(0, 1).toUpperCase() + txt.substring(1).toLowerCase();
        if (item instanceof GroupUsers) {
            txt = txt + "<font color=#999999><br>" + ((GroupUsers) item).getUacc_email() + "</font>";
        }
        holder.textView.setText(Html.fromHtml(txt));
        if (lastSelected == position)
            holder.check_btn.setChecked(true);
        else
            holder.check_btn.setChecked(false);

        if (txt.equals(AppUtils.getResString("lbl_pl_slct_msg"))) {
            holder.check_btn.setVisibility(View.INVISIBLE);
        } else {
            holder.check_btn.setVisibility(View.VISIBLE);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int lastPos = lastSelected;
                lastSelected = position;
                if (clickListener != null)
                    clickListener.onItemClicked(position, holder.itemView, item);
                else {
                    if (lastPos == position) {
                        return;
                    }
                    notifyItemChanged(lastPos);
                    notifyItemChanged(position);
                }
            }
        });
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        RadioButton check_btn;

        public ViewHolder(View v) {
            super(v);
            textView = v.findViewById(R.id.text1);
            check_btn = v.findViewById(R.id.check_btn);
            if (clickListener != null)
                check_btn.setVisibility(View.GONE);
        }
    }
}
