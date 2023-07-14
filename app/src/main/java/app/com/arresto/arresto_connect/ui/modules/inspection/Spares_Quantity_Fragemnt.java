package app.com.arresto.arresto_connect.ui.modules.inspection;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.custom_views.MultiSpinner;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

public class Spares_Quantity_Fragemnt extends Base_Fragment {
    View view;
    MultiSpinner spares_spnr;
    LinearLayout spares_layer;
    List<String> spares_array;

    public static Spares_Quantity_Fragemnt newInstance(String asset_code) {
        Spares_Quantity_Fragemnt fragmentFirst = new Spares_Quantity_Fragemnt();
        Bundle args = new Bundle();
        args.putString("asset_code", asset_code);
//        args.putString("data", AppUtils.getGson().toJson(brandModel));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.spare_qty_edit_layout, parent, false);
            spares_layer = view.findViewById(R.id.spares_layer);
            spares_spnr = view.findViewById(R.id.spares_spnr);
            continue_btn = view.findViewById(R.id.continue_btn);
            spares_lay = view.findViewById(R.id.spares_lay);
            if (getArguments() != null) {
                String asset_string = getArguments().getString("asset_code");
                setSpares(asset_string);
            }
        }
        return view;
    }

    private void setSpares(String asset_string) {
        spares_array = new ArrayList<>();
        if (asset_string.toLowerCase().startsWith("pn9000") || asset_string.toLowerCase().startsWith("pn-9000"))
            spares_array = Arrays.asList(getActivity().getResources().getStringArray(R.array.alu_rail_spares));
        else if (asset_string.toLowerCase().startsWith("pn7000") || asset_string.toLowerCase().startsWith("pn-7000"))
            spares_array = Arrays.asList(getActivity().getResources().getStringArray(R.array.wire_spares));
        if (spares_array != null && spares_array.size() > 0) {
            spares_layer.setVisibility(View.VISIBLE);
            spares_spnr.setItems(spares_array, getResString("lbl_pl_slct_msg"), new MultiSpinner.MultiSpinnerListener() {
                @Override
                public void onItemsSelected(Boolean[] selected) {
                    ArrayList slctd_spares = new ArrayList<>();
                    for (int i = 0; i < selected.length; i++) {
                        if (selected[i]) {
                            slctd_spares.add(spares_array.get(i));
                        }
                    }
                    create_qty_layout(slctd_spares);
                }
            });
        }
    }

    TextView continue_btn;
    JSONArray spares_post;
    LinearLayout spares_lay;

    private void create_qty_layout(ArrayList<String> spares) {
        ((ViewGroup) spares_lay.getParent()).setVisibility(View.VISIBLE);
        spares_lay.setVisibility(View.VISIBLE);
        spares_lay.removeAllViews();
        spares_post = new JSONArray();
        try {
            for (int i = 0; i < spares.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("name", spares.get(i));
                View dly = LayoutInflater.from(baseActivity).inflate(R.layout.spare_qty_item, null);
                final TextView name_tv = dly.findViewById(R.id.name_tv);
                name_tv.setText(spares.get(i));
                final EditText qty_edt = dly.findViewById(R.id.qty_edt);
                qty_edt.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        try {
                            object.put("quantity", editable.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                qty_edt.setText("1");
                spares_lay.addView(dly);
                spares_post.put(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoadFragment.replace(InspectorSignature.newInstance("inspection", ""+spares_post), getActivity(), getResString("lbl_inspector_remark"));
            }
        });
    }

}
