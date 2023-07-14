package app.com.arresto.arresto_connect.ui.modules.add_data;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.SmsModel;
import app.com.arresto.arresto_connect.interfaces.AlertClickListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;
import static app.com.arresto.arresto_connect.ui.modules.add_data.ProMasterEdit.oldSms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Add_SMS_Component_Fragment extends AddData_BaseFragment implements View.OnClickListener {
    View view;

    public static Add_SMS_Component_Fragment newInstance(AddAssetModel asset_model) {
        Add_SMS_Component_Fragment fragmentFirst = new Add_SMS_Component_Fragment();
        if (asset_model != null) {
            Bundle args = new Bundle();
            args.putString("aData", new Gson().toJson(asset_model));
            fragmentFirst.setArguments(args);
        }
        return fragmentFirst;
    }

    AddAssetModel asset_model;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_sms_component_fragment, parent, false);
            all_Ids();
            if (oldSms != null) {
                setSmsData(oldSms);
            }
            if (getArguments() != null) {
                if (getArguments().containsKey("aData")) {
                    asset_model = new Gson().fromJson(getArguments().getString("aData"), AddAssetModel.class);
                    if (asset_model.getJobcard() != null) {
                        job_edt.setText(asset_model.getJobcard());
                    }
                    if (asset_model.getSms() != null) {
                        sms_edt.setText(asset_model.getSms());
                    }
                    if (asset_model.getAsset_code() != null) {
                        aset_kit_tv.setText(asset_model.getAsset_code());
                        get_Product_data(All_Api.product_service + asset_model.getAsset_code() + "?client_id=" + client_id, dialogHandler);
                    }
                }
            }
            fetch_Assetsdata(All_Api.getAllSeries + client_id + "&user_id=" + user_id);
            fetch_Assetsdata(All_Api.getAllAssets + client_id + "&user_id=" + user_id);
        }
        return view;
    }

    private void setSmsData(SmsModel oldSms) {
        sms_edt.setText(oldSms.getSms_number());
        aset_kit_tv.setText(oldSms.getSeries());
        job_edt.setText(oldSms.getJc_number());
        line_edt.setText(oldSms.getNo_of_lines());
    }

    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
        @Override
        public void afterChange(TextView view, String text) {
            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
                ((TextInputLayout) view.getParent().getParent()).setError(null);
        }
    };

    MaterialButton continue_btn, add_asset_btn;
    EditText sms_edt, job_edt, line_edt;
    LinearLayout assets_list;
    TextView aset_kit_tv;

    private void all_Ids() {
        sms_edt = view.findViewById(R.id.sms_edt);
        aset_kit_tv = view.findViewById(R.id.aset_kit_tv);
        job_edt = view.findViewById(R.id.job_edt);
        line_edt = view.findViewById(R.id.line_edt);

        assets_list = view.findViewById(R.id.assets_list);
        add_asset_btn = view.findViewById(R.id.add_asset_btn);
        continue_btn = view.findViewById(R.id.continue_btn);

        aset_kit_tv.setOnClickListener(this);
        add_asset_btn.setOnClickListener(this);
        continue_btn.setOnClickListener(this);
        new CustomTextWatcher(job_edt, textWatcher);
        new CustomTextWatcher(sms_edt, textWatcher);

        assets_post = new JSONArray();
        assets_list.removeAllViews();

        assets_post.put(new JSONObject());
        add_asset_layout();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.continue_btn:
                if (isEmpty(job_edt) || isEmpty(sms_edt) || isEmpty(aset_kit_tv) || isEmpty(line_edt)) {
                    show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
                    return;
                }

                JSONObject postJson = new JSONObject();
                try {
//                    for (int i = 0; i < assets_post.length(); i++) {
//                        if (assets_post.getJSONObject(i).getString("quantity").equals("0")) {
//                            show_snak(getActivity(), "Asset quantity should be minimum 1");
//                            return;
//                        }
//                    }
                    JSONArray _postArray=new JSONArray();
                    for (int i = assets_post.length() - 1; i >= 0; i--) {
                        if (!assets_post.getJSONObject(i).getString("quantity").equals("0")) {
                            _postArray.put(assets_post.getJSONObject(i));
//                            assets_post.remove(i);
                        }
                    }
                    if (_postArray.length() < 1) {
                        show_snak(getActivity(), "At least one asset should be in list with minimum quantity 1");
                        return;
                    }
                    postJson.put("client_id", client_id);
                    postJson.put("jobcard_no", job_edt.getText().toString());
                    postJson.put("sms_no", sms_edt.getText().toString());
                    postJson.put("series", aset_kit_tv.getText().toString());
                    postJson.put("no_of_line", line_edt.getText().toString());
                    postJson.put("assets", _postArray);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                printLog("" + postJson);
                postData(postJson);
                break;
            case R.id.add_asset_btn:
                TextView name_tv = assets_list.getChildAt(assets_list.getChildCount() - 1).findViewById(R.id.name_tv);
                if (!isEmpty(name_tv)) {
                    assets_post.put(new JSONObject());
                    add_asset_layout();
                } else show_snak(baseActivity, "Please fill last added Asset first");
                break;
            case R.id.aset_kit_tv:
                chooseAssetDialog("Series", true, dialogHandler);
                break;
        }
    }

    private void postData(JSONObject postJson) {
        NetworkRequest network_request = new NetworkRequest(getActivity());
        network_request.make_contentpost_request(All_Api.post_sms_components, postJson, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject1 = new JSONObject(response);
                        show_snak(getActivity(), jsonObject1.getString("message"));
                        if (jsonObject1.getString("status_code").equals("200")) {
                            baseActivity.show_OkAlert("Confirmation", "Do you want to Add SiteId for this SMS component?", "Yes", "No", new AlertClickListener() {
                                @Override
                                public void onYesClick() {
                                    LoadFragment.replace(Add_Site.newInstance(job_edt.getText().toString(), sms_edt.getText().toString()), baseActivity, getResString("lbl_add_site_id_data"));
                                }

                                @Override
                                public void onNoClick() {
                                    homeActivity.submit_action("dashboard");
                                }
                            });
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Log.e("error", "" + error);
            }
        });

    }

    @SuppressLint("HandlerLeak")
    Handler dialogHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: // data selected

                    String code = msg.getData().getString("data");
                    String id = msg.getData().getString("id");

                    if (msg.getData().getString("type").equals("Asset")) {
                        if (selectedTextview != null) {
                            try {
//                                JSONObject object = assets_post.getJSONObject(clicked_position);
                                selectedObject.put("asset_code", code);
                                selectedObject.put("quantity", "0");
//                                selectedTextview = assets_list.getChildAt(clicked_position).findViewById(R.id.name_tv);
                                selectedTextview.setText(code);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            selectedTextview = null;
                        }
                    } else {
                        if (selected_asset_series != null) {
                            assets_post = new JSONArray();
                            assets_list.removeAllViews();
                            aset_kit_tv.setText(selected_asset_series.getProduct_code());
                            String assets_txt = selected_asset_series.getProduct_components();
                            if (assets_txt != null && !assets_txt.equals("0") && !assets_txt.equals("")) {
                                final List<String> sub_ast_name = Arrays.asList(assets_txt.split("##"));
                                for (int j = 0; j < sub_ast_name.size(); j++) {
                                    String[] row_data = sub_ast_name.get(j).split("#");
                                    if (row_data[0] != null && !row_data[0].equals("")) {
                                        try {
                                            JSONObject object = new JSONObject();
                                            object.put("asset_code", row_data[0]);
                                            object.put("quantity", "0");
                                            assets_post.put(object);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                add_asset_layout();
                            }
                        }
                    }
                    break;
                case 101:
                    // close clicked

                    break;
                case 102:
                    // save clicked
                    break;
            }
        }
    };
    JSONArray assets_post;
    TextView selectedTextview;
    JSONObject selectedObject;

    private void add_asset_layout() {

        try {
            assets_list.removeAllViews();
            for (int i = 0; i < assets_post.length(); i++) {
                JSONObject object = assets_post.getJSONObject(i);
                View dly = LayoutInflater.from(baseActivity).inflate(R.layout.spare_qty_item, null);
                dly.setId(i);
                final TextView name_tv = dly.findViewById(R.id.name_tv);
                name_tv.setHint(getResString("lbl_enter_asset"));
                final ImageView delete_btn = dly.findViewById(R.id.delete_btn);
                if (assets_post.length() > 0)
                    delete_btn.setVisibility(View.VISIBLE);
                else
                    delete_btn.setVisibility(View.GONE);
                final EditText qty_edt = dly.findViewById(R.id.qty_edt);
                try {
                    if (object.has("asset_code") && !object.getString("asset_code").equals(""))
                        name_tv.setText(object.getString("asset_code"));
                    if (object.has("quantity") && !object.getString("quantity").equals(""))
                        qty_edt.setText(object.getString("quantity"));
                    else qty_edt.setText("0");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                name_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedObject = object;
                        selectedTextview = name_tv;
                        chooseAssetDialog("Asset", false, dialogHandler);
                    }
                });
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
                delete_btn.setOnClickListener(v -> {
                    assets_post.remove(dly.getId());
                    add_asset_layout();
                });
                assets_list.addView(dly);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
