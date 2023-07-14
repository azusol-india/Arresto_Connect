package app.com.arresto.arresto_connect.custom_views;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Html;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;

public class DialogSpinner extends AppCompatSpinner implements DialogInterface.OnCancelListener {
    List items;
    String defaultText = "";
    CustomRecyclerAdapter ad;

    public DialogSpinner(Context context) {
        super(context);
    }

    public DialogSpinner(Context arg0, AttributeSet arg1) {
        super(arg0, arg1);
    }

    public DialogSpinner(Context arg0, AttributeSet arg1, int arg2) {
        super(arg0, arg1, arg2);
    }

    @Override
    public void onCancel(DialogInterface dialogInterface) {

    }

    public void setItems(List items, String defaultText) {
        this.items = items;
        this.defaultText = defaultText;
        // all text on the spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spiner_tv, R.id.spnr_text, items) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View row;
                if (null == convertView) {
                    LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    row = inflater.inflate(R.layout.spiner_tv, null);
                } else {
                    row = convertView;
                }
                TextView tv =  row.findViewById(R.id.spnr_text);
                tv.setText(Html.fromHtml(items.get(position).toString()));
                return row;
            }

        };
        setAdapter(adapter);
    }

    public void setLastSelected(int position) {
        if (ad != null) {
            ad.lastSelected = position;
            ad.notifyDataSetChanged();
        }
        if (items != null && items.size() >= position) {
            setSelection(position);
        }
    }


    @Override
    public boolean performClick() {
        Dialog dialog = new Dialog(getContext(), R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        TextView header = dialog.findViewById(R.id.header);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        RecyclerView listView = dialog.findViewById(R.id._list);
        listView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        if (!defaultText.equals("")) {
            header.setText(defaultText);
            if (items != null) {
                ad = new CustomRecyclerAdapter(getContext(), items);
                listView.setAdapter(ad);
            }
        } else if (items != null && items.size() > 1) {
            header.setText("" + items.get(0));
            ad = new CustomRecyclerAdapter(getContext(), items.subList(1, items.size()));
            listView.setAdapter(ad);
        }
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_ok.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad != null) {
                    if (!defaultText.equals(""))
                        setSelection(ad.lastSelected);
                    else if (ad.lastSelected != -1)
                        setSelection(ad.lastSelected + 1);
                }
                dialog.cancel();
            }
        });
        dialog.setOnCancelListener(this);
        dialog.show();
        return true;

    }

}