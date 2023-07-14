package app.com.arresto.arresto_connect.ui.modules.safety_management;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;
import app.com.arresto.arresto_connect.ui.modules.inspection.ClientSignatureFragment;
import app.com.arresto.arresto_connect.ui.modules.rams.Customform_Base;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;

/**
 * Created by AZUSOL-PC-02 on 8/5/2019.
 */
public class Safety_Select_Forms extends Customform_Base {
    View view;
    Spinner form_spinr;
    TextView continue_btn;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.safety_select_forms, parent, false);
            find_id();
            update_list();
            continue_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        if (singleSet_array != null)
                            removeNull(singleSet_array, false);
                        if (repeterSet_Array != null)
                            removeNull(repeterSet_Array, true);
                        saveSignature();
                        JSONObject post_field = new JSONObject();
                        post_field.put("single_set", singleSet_array);
                        post_field.put("repeaters", repeterSet_Array);
                        params = new JSONObject();
                        params.put("client_id", client_id);
                        params.put("user_id", Static_values.user_id);
                        params.put("form_id", form_id);
                        params.put("field_set", post_field);

                        LoadFragment.replace(ClientSignatureFragment.newInstance("Safety"), getActivity(), getResString("lbl_suprvisr_msg"));
//                        post_data(post_field);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "safety digitization");
    }

    private void find_id() {
        form_spinr = view.findViewById(R.id.form_spinr);
        continue_btn = view.findViewById(R.id.continue_btn);
    }

    public void setListener() {
        form_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                remove_all_views();
                if (position > 0) {
                    setForm(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    ArrayList forms;

    @SuppressLint("HandlerLeak")
    public void update_list() {
        NetworkRequest.getCustom_Data(getActivity(), All_Api.get_safety_form + client_id, "forms", new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    forms = DataHolder_Model.getInstance().getCustomViews_models();
                    forms.add(0, getResString("lbl_pl_slct_msg"));
                    ChooseForm();
                    ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, forms);
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    form_spinr.setAdapter(adapter);
                    setListener();
                }
            }
        });
    }

    public static JSONObject params;

    private void ChooseForm() {
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        dialog.setCancelable(true);
        dialog.setTitle(getResString("lbl_select_form"));
        TextView header = dialog.findViewById(R.id.header);
        header.setText(getResString("lbl_please_select_form"));

        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        RecyclerView lvLangs = dialog.findViewById(R.id._list);
        lvLangs.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        CustomRecyclerAdapter ad = new CustomRecyclerAdapter(getContext(), DataHolder_Model.getInstance().getCustomViews_models());
        lvLangs.setAdapter(ad);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad != null) {
                    form_spinr.setSelection(ad.lastSelected);
                }
                dialog.cancel();
            }
        });
        dialog.show();
    }


}
