package app.com.arresto.arresto_connect.interfaces;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public interface OnItemClickListener{
        void onItemClicked(int position, View v,Object data);
}