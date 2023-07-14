package app.com.arresto.arresto_connect.ui.modules.add_data;

import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.selectedMasterData_model;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.network.All_Api.getMasterSmsSite;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.data.models.SmsModel;
import app.com.arresto.arresto_connect.interfaces.AlertClickListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;

public class ProMasterEdit extends AddData_BaseFragment implements View.OnClickListener {
    public static ProMasterEdit newInstance(String pageType) {
        ProMasterEdit fragmentFirst = new ProMasterEdit();
        Bundle args = new Bundle();
        args.putString("pageType", pageType);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.manufacturing_dt_tv:
            case R.id.first_use_dt_tv:
//            case R.id.pm_dt_tv:
                show_Date_piker((TextView) v);
                break;
            case R.id.aset_tv:
                if (mdataType.equals("Asset") && assets.size() > 0) {
                    chooseAssetDialog("Asset", true, dialogHandler);
                } else if (mdataType.equalsIgnoreCase("Series") && asset_series.size() > 0) {
                    chooseAssetDialog("Series", false, dialogHandler);
                }
                break;
            case R.id.add_btn:
                if (type_spinr.getSelectedItem().toString().equalsIgnoreCase("asset"))
                    LoadFragment.replace(AddAssetFragment.newInstance("pro"), baseActivity, getResString("lbl_add_asset_details"));
                else
                    LoadFragment.replace(Add_Asset_Series_Fragment.newInstance("pro"), baseActivity, getResString("lbl_add_asset_kit"));
                break;
            case R.id.continue_btn:
                if (manufacturing_dt_tv.getTag() != null) {
                    masterData_model.setMdata_material_invoice_date("" + manufacturing_dt_tv.getTag());
                    assetModel.setInvoice_date("" + manufacturing_dt_tv.getTag());
                }
                if (pm_dt_tv.getTag() != null) {
                    masterData_model.setPdm_due_date("" + pm_dt_tv.getTag());
                    assetModel.setPdm_due_date("" + pm_dt_tv.getTag());
                }
                if (first_use_dt_tv.getTag() != null) {
                    masterData_model.setDate_of_first_use("" + first_use_dt_tv.getTag());
                    assetModel.setDate_of_first_use("" + first_use_dt_tv.getTag());
                }
                masterData_model.setMdata_item_series("" + aset_tv.getText());
                if (mdataType.equals("Asset")) {
                    masterData_model.setMdata_asset("" + aset_tv.getText());
                }
                if (masterData_model != null)
                    assetModel.setMaster_id(masterData_model.getMdata_id());
                assetModel.setType(mdataType);
                assetModel.setUin(uin_tv.getText().toString());
                assetModel.setAsset_code("" + aset_tv.getText());
                if (existing_rg.indexOfChild(existing_rg.findViewById(existing_rg.getCheckedRadioButtonId())) == 1) {
                    if (mdataType.equalsIgnoreCase("Series")) {
                        assetModel.setJobcard(masterData_model.getMdata_jobcard());
                        assetModel.setSms(masterData_model.getMdata_sms());
                        LoadFragment.replace(Add_SMS_Component_Fragment.newInstance(assetModel), baseActivity, getResString("lbl_edit_sms_component"));
                    } else {
                        submitData();
                    }
                } else {
                    LoadFragment.replace(Add_masterData.newInstance("edit_master", true, masterData_model, page_type), baseActivity, getResString("lbl_edit_master_data"));
                }
//                submitData();
                break;
        }
    }

    View view;
    TextView heading_tv, uin_tv, type_tv, aset_tv, manufacturing_dt_tv, first_use_dt_tv, pm_dt_tv, msg_txt;
    MaterialButton continue_btn, add_btn;
    Spinner type_spinr;
    ArrayList<String> type_array = new ArrayList<>(Arrays.asList(getResString("lbl_asset"), getResString("lbl_asset_series")));

    @SuppressLint("HandlerLeak")
    Handler dialogHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 100: // data selected
                    String code = "";
                    String type = "asset";
                    if (selected_asset != null && mdataType.equals("Asset")) {
                        code = selected_asset.getComponent_code();
                        pdm_frequency = selected_asset.getComponent_pdm_frequency();
                        calculatePdmDate();
                    } else if (msg.getData() != null && msg.getData().containsKey("data")) {
                        code = msg.getData().getString("data");
                        type = "series";
                    }
                    aset_tv.setText(code);
//                   || client_id.equalsIgnoreCase("2069") ||  client_id.equalsIgnoreCase("376")
                    if (client_id.equalsIgnoreCase("952")) {
                        hideDates();
                        duplicacyDialogue(code, type);
                    }
                    break;
                case 101:// close clicked
                    break;
                case 102:// save clicked
                    break;
            }
        }
    };

    private void hideDates() {
        existing_rg.check(existing_rg.getChildAt(1).getId());
        view.findViewById(R.id.rg_view).setVisibility(View.GONE);
        view.findViewById(R.id.add_view).setVisibility(View.GONE);
        view.findViewById(R.id.dates_view).setVisibility(View.GONE);

        final Calendar c = Calendar.getInstance();
        manufacturing_dt_tv.setText(baseActivity.Date_Format().format(c.getTime()));
        manufacturing_dt_tv.setTag(baseActivity.server_date_format.format(c.getTime()));
        first_use_dt_tv.setText(baseActivity.Date_Format().format(c.getTime()));
        first_use_dt_tv.setTag(baseActivity.server_date_format.format(c.getTime()));
    }

    String pdm_frequency = "";
    MasterData_model masterData_model;
    RadioGroup existing_rg;
    AddAssetModel assetModel;
    String mdataType = "Asset";
    String page_type;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            if (getArguments() != null) {
                page_type = getArguments().getString("pageType");
            }
            view = inflater.inflate(R.layout.pro_master_fragment, parent, false);
            masterData_model = Static_values.selectedMasterData_model;
            assetModel = AddAssetModel.getNewInstance();
            assetModel.setClient_id(client_id);
            assetModel.setUser_id(user_id);
            all_Ids();
            setdata();
        }
        return view;
    }

    private void all_Ids() {
        type_spinr = view.findViewById(R.id.type_spinr);
        heading_tv = view.findViewById(R.id.heading_tv);
        continue_btn = view.findViewById(R.id.continue_btn);
        uin_tv = view.findViewById(R.id.uin_tv);
        type_tv = view.findViewById(R.id.type_tv);
        aset_tv = view.findViewById(R.id.aset_tv);
        manufacturing_dt_tv = view.findViewById(R.id.manufacturing_dt_tv);
        first_use_dt_tv = view.findViewById(R.id.first_use_dt_tv);
        pm_dt_tv = view.findViewById(R.id.pm_dt_tv);
        existing_rg = view.findViewById(R.id.existing_rg);
        msg_txt = view.findViewById(R.id.msg_txt);
        add_btn = view.findViewById(R.id.add_btn);
        continue_btn.setOnClickListener(this);
        aset_tv.setOnClickListener(this);
        manufacturing_dt_tv.setOnClickListener(this);
        first_use_dt_tv.setOnClickListener(this);
        pm_dt_tv.setOnClickListener(this);
        add_btn.setOnClickListener(this);

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.spiner_tv, type_array);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        type_spinr.setAdapter(adapter);

        type_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                    mdataType = "Asset";
                    aset_tv.setHint("Click to select an Asset code");
                    msg_txt.setText("If your Asset is not in the list, please add Asset");
//                    type_tv.setText("Select Asset");
                } else if (i == 1) {
                    mdataType = "Series";
                    aset_tv.setHint("Click to select an Asset kit");
                    msg_txt.setText("If your Asset Kit is not in the list, please add Asset Kit");
//                    type_tv.setText("Select Asset kit");
                }
                type_tv.setText(mdataType);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        existing_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton checkedRadioButton = radioGroup.findViewById(i);
                if (checkedRadioButton.getText().toString().equalsIgnoreCase("yes")) {
                    continue_btn.setText(getResString("lbl_cntnue_st"));
                } else {
                    continue_btn.setText(getResString("lbl_sbmit_st"));
                }
            }
        });
        existing_rg.check(existing_rg.getChildAt(0).getId());
//        new CustomTextWatcher(manufacturing_dt_tv, new CustomTextWatcher.OnTextChange() {
//            @Override
//            public void afterChange(TextView view, String text) {
//                calculatePdmDate();
//            }
//        });
        manufacturing_dt_tv.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                calculatePdmDate();
            }
        });


        heading_tv.setText(this.getTag());
        baseActivity.headerTv.setText("");
    }

    private void calculatePdmDate() {
        if (!manufacturing_dt_tv.getText().equals("") && !pdm_frequency.equals("")) {
            Calendar calendar = Calendar.getInstance();
            try {
                calendar.setTime(BaseActivity.Date_Format().parse("" + manufacturing_dt_tv.getText()));
                calendar.add(Calendar.DAY_OF_YEAR, Integer.parseInt(pdm_frequency));
                pm_dt_tv.setText(baseActivity.Date_Format().format(calendar.getTime()));
                pm_dt_tv.setTag(baseActivity.server_date_format.format(calendar.getTime()));
                assetModel.setPdm_due_date("" + pm_dt_tv.getTag());
                masterData_model.setPdm_due_date("" + pm_dt_tv.getTag());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    private void setdata() {
//
        if(client_id.equals("1040")||client_id.equals("786")){
            existing_rg.check(existing_rg.getChildAt(1).getId());
        }
        if(client_id.equals("376")||client_id.equals("2069"))
            view.findViewById(R.id.rg_view).setVisibility(View.GONE);

//        fetch_data(All_Api.getAllSubassets + client_id);
        uin_tv.setText(selectedMasterData_model.getMdata_uin());
        first_use_dt_tv.setText(selectedMasterData_model.getDate_of_first_use());
        pm_dt_tv.setText(selectedMasterData_model.getPdm_inspection_date());
        try {
            manufacturing_dt_tv.setText(BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(masterData_model.getMdata_material_invoice_date())));
        } catch (ParseException e) {
            e.printStackTrace();
            manufacturing_dt_tv.setText(masterData_model.getMdata_material_invoice_date());
        }

        if (selectedMasterData_model.getMdata_asset() != null && !selectedMasterData_model.getMdata_asset().equals("")) {
            aset_tv.setText(selectedMasterData_model.getMdata_asset());
//            fetch_Assetsdata(All_Api.getAllAssets + client_id + "&user_id=" + user_id);
        } else if (!selectedMasterData_model.getMdata_jobcard().equals("") && !selectedMasterData_model.getMdata_sms().equals("")) {
            aset_tv.setText(selectedMasterData_model.getMdata_item_series());
//            fetch_Assetsdata(All_Api.getAllSeries + client_id + "&user_id=" + user_id);
            type_spinr.setSelection(1);
            type_spinr.setEnabled(false);
        } else if (selectedMasterData_model.getMdata_item_series() != null && !selectedMasterData_model.getMdata_item_series().equals("")) {
            aset_tv.setText(selectedMasterData_model.getMdata_item_series());
            type_spinr.setSelection(1);
        } else {
            aset_tv.setHint("Click to select an Asset code");
//            fetch_Assetsdata(All_Api.getAllAssets + client_id + "&user_id=" + user_id);
//            fetch_Assetsdata(All_Api.getAllSeries + client_id + "&user_id=" + user_id);
        }
        fetch_Assetsdata(All_Api.getAllAssets + client_id + "&user_id=" + user_id);
        fetch_Assetsdata(All_Api.getAllSeries + client_id + "&user_id=" + user_id);
    }

    String submit_url = All_Api.add_Master;

    private void submitData() {
        if (assetModel.getAsset_code() == null || assetModel.getAsset_code().equals("")) {
            show_snak(getActivity(), "Please select an Asset");
            return;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(getGson().toJson(assetModel));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (baseActivity != null) {
            new NetworkRequest(getActivity()).make_contentpost_request(submit_url, jsonObject, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jObject = new JSONObject(response);
                        show_snak(getActivity(), jObject.getString("message"));
                        if (jObject.getString("status_code").equals("200")) {
                            homeActivity.load_home_fragment(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String message) {
                    Log.e("error", "" + message);
                }
            });
        } else {
            show_snak(HomeActivity.homeActivity, getResString("lbl_try_again_msg"));
        }
    }

    private void duplicacyDialogue(String code, String type) {
        baseActivity.show_OkAlert("Confirmation", "Do you want to fetch recently added data for this " + type, "Yes", "No", new AlertClickListener() {
            @Override
            public void onYesClick() {
                getOldData(code, type);
            }

            @Override
            public void onNoClick() {
                existing_rg.check(existing_rg.getChildAt(0).getId());
                view.findViewById(R.id.rg_view).setVisibility(View.VISIBLE);
            }
        });
    }

    public static MasterData_model oldMaster;
    public static SmsModel oldSms;
    public static Site_Model oldSite;

    private void getOldData(String asset_code, String type) {
        new NetworkRequest(getActivity()).make_get_request(getMasterSmsSite + client_id + "&asset_code=" + asset_code + "&type=" + type, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);
                    show_snak(getActivity(), jObject.getString("message"));
                    if (jObject.getString("status_code").equals("200")) {
                        JSONObject data = jObject.getJSONObject("data");
                        oldMaster = new Gson().fromJson(data.getString("mData"), MasterData_model.class);
                        oldSite = new Gson().fromJson(data.getString("siteData"), Site_Model.class);
                        oldSms = new Gson().fromJson(data.getString("smsData"), SmsModel.class);
                    } else {
                        existing_rg.check(existing_rg.getChildAt(0).getId());
                        view.findViewById(R.id.rg_view).setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("error", "" + message);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        oldMaster = null;
        oldSite = null;
        oldSms = null;
    }
}
