package app.com.arresto.arresto_connect.custom_views;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.ui.fragments.Close_projectFragment;
import app.com.arresto.arresto_connect.ui.fragments.Master_detail_fragment;

public class CustomAdapter extends BaseAdapter {

    private Context context;
    ArrayList<String> name;
    ArrayList<Integer> icons;
    ListClick listClick;

    public interface  ListClick{
        void performClick(String position);
    }

    public CustomAdapter(Context context, ArrayList<String> name, ArrayList<Integer> icons,ListClick listClick) {
        this.context = context;
        this.name = name;
        this.icons = icons;
        this.listClick=listClick;
    }

    @Override
    public int getCount() {
        return name.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.layout_master_detail_buttons, parent, false);
        }

        My_TextView sUserTextView = convertView.findViewById(R.id.user);
        My_TextView title = convertView.findViewById(R.id.title);
        ImageView btn_icon = convertView.findViewById(R.id.btn_icon);

        if (name.get(position).contains(context.getString(R.string.lbl_sensor))) {
            convertView.findViewById(R.id.indicator).setVisibility(View.VISIBLE);
        } else {
//            if (!name.get(position).equals(context.getString(R.string.lbl_video)) ||! name.get(position).contains(context.getString(R.string.lbl_sensor)))
                btn_icon.setImageTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getBtn_bg_clr()));
            convertView.findViewById(R.id.indicator).setVisibility(View.GONE);
        }

        sUserTextView.setText("Item " + position);
        title.setText(name.get(position));
        btn_icon.setImageDrawable(context.getDrawable(icons.get(position)));

        My_RelativeLayout sensorBtn = convertView.findViewById(R.id.main_rl);
        sensorBtn.setOnClickListener(v -> {
            listClick.performClick(name.get(position));

        });

        return convertView;
    }
}
