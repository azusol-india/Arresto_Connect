/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.add_data;

import static android.view.View.VISIBLE;
import static app.com.arresto.arresto_connect.constants.AppUtils.delete_uploadedsite_data;
import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.setupUI;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.SENSOR_PERMISSION;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static app.com.arresto.arresto_connect.constants.Static_values.selectedMasterData_model;
import static app.com.arresto.arresto_connect.constants.Static_values.selected_Site_model;
import static app.com.arresto.arresto_connect.constants.Static_values.unique_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.activity.HomeActivity.homeActivity;
import static app.com.arresto.arresto_connect.ui.fragments.Main_Fragment.is_refresh;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.ConstantMethods;
import app.com.arresto.arresto_connect.constants.CustomTextWatcher;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.data.models.AddAssetModel;
import app.com.arresto.arresto_connect.data.models.Client_model;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.interfaces.AlertClickListener;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NFC_Listner;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Master_detail_fragment;

public class Add_masterData extends AddData_BaseFragment implements View.OnClickListener {
    View view;
    private TextView aset_tv, heading_tv, type_tv, invoice_dt_tv, barcode_btn, first_use_tv, rfid_btn, label_btn, spnr_heading;
    String page_type = "";
    MasterData_model masterData_model;
    MaterialButton continue_btn;
    Spinner type_spinr;
    String mdataType = "", main_page = "";
    boolean isPro;

    /*Three type master we can add here
     * 1st type = add_master that from home Screen that is fresh new master and there is no any bundle data provided
     * 2nd type = Asset_master that is coming from add asset page and add master data for recently added asset and asset details provided in bundle for master
     * 3rd type = Edit_master that is edit selected master data and master data details provided in bundle*/

    public static Add_masterData newInstance(String type, MasterData_model masterData_model) {
        Add_masterData fragmentFirst = new Add_masterData();
        Bundle args = new Bundle();
        args.putString("type", type);
        if (masterData_model != null)
            args.putString("mData", new Gson().toJson(masterData_model));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    public static Add_masterData newInstance(String type, boolean isPro,
                                             MasterData_model masterData_model, String main_page) {
        Add_masterData fragmentFirst = new Add_masterData();
        Bundle args = new Bundle();
        args.putString("type", type);
        args.putBoolean("isPro", isPro);
        args.putString("main_page", main_page);
        if (masterData_model != null)
            args.putString("mData", new Gson().toJson(masterData_model));
        fragmentFirst.setArguments(args);
        return fragmentFirst;
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
                    assetModel.setAsset_code(code);
                    aset_tv.setText(code);
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

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_master_data, parent, false);
            setupUI(view, getActivity());
            assetModel = AddAssetModel.getInstance();
            all_Ids();
            if (getArguments() != null) {
                page_type = getArguments().getString("type");
                if (getArguments().containsKey("mData")) {
                    masterData_model = new Gson().fromJson(getArguments().getString("mData"), MasterData_model.class);
                    assetModel.setAsset_code(masterData_model.getMdata_item_series());
                    assetModel.setMdata_client(masterData_model.getMdata_client_id());
                    set_master_data(masterData_model);
                }
                if (getArguments().containsKey("isPro")) {
                    isPro = getArguments().getBoolean("isPro", false);
                }
                if (getArguments().containsKey("main_page")) {
                    main_page = getArguments().getString("main_page");
                }
            }
            if (!isPro) {
                fetch_data(All_Api.all_clientinfo + "?client_id=" + client_id, false, new ObjectListener() {
                    @Override
                    public void onResponse(Object obj) {
                        if (obj instanceof ArrayList) {
                            clientModels = (ArrayList<Client_model>) obj;
                            ArrayAdapter name_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, clientModels);
                            client_edt.setAdapter(name_adapter);
                            if (masterData_model != null && masterData_model.getMdata_client() != null) {
                                find_client(masterData_model.getMdata_client());
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {

                    }
                });
            }
            mdataType = "Asset";
            if (!page_type.equals("asset_master")) {
                submit_url = All_Api.add_Master;
                assetModel.setClient_id(client_id);
                assetModel.setUser_id(user_id);
                aset_tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mdataType.equalsIgnoreCase("Asset") && assets.size() > 0) {
                            chooseAssetDialog("Asset", false, dialogHandler);
                        } else if (mdataType.equalsIgnoreCase("Series") && asset_series.size() > 0) {
                            chooseAssetDialog("Series", false, dialogHandler);
                        }
                    }
                });
                assets = new ArrayList<>();
                asset_series = new ArrayList<>();
                if ((page_type.equals("edit_master") && (client_id.equals("419") || client_id.equals("376")
                        || client_id.equals("952") || client_id.equals("395") || client_id.equals("2069"))) || page_type.equals("add_master")) {
                    if (isPro) {
                        aset_tv.setOnClickListener(null);
                        if (!assetModel.getType().equals("Asset")) {
                            ((ViewGroup) jobcard_tv.getParent()).setVisibility(VISIBLE);
                            String url = All_Api.getJobCard + Static_values.client_id + "&user_id=" + Static_values.user_id +
                                    "&group_id=" + Static_values.user_id + "&jobcard=";
                            fetch_data(url, true, null);
                            mdataType = "Series";
                        }
                    } else {
                        ((ViewGroup) type_spinr.getParent()).setVisibility(VISIBLE);
                        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), R.layout.spiner_tv, type_array);
                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        type_spinr.setAdapter(adapter);
                        fetch_Assetsdata(All_Api.getAllSeries + client_id + "&user_id=" + user_id);
                        fetch_Assetsdata(All_Api.getAllAssets + client_id + "&user_id=" + user_id);
                        type_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                if (i == 0) {
                                    mdataType = "Asset";
                                    ((ViewGroup) jobcard_tv.getParent()).setVisibility(View.GONE);
                                } else if (i == 1) {
                                    mdataType = "Series";
                                    ((ViewGroup) jobcard_tv.getParent()).setVisibility(VISIBLE);
                                    String url = All_Api.getJobCard + Static_values.client_id + "&user_id=" + Static_values.user_id +
                                            "&group_id=" + Static_values.user_id + "&jobcard=";
                                    fetch_data(url, true, null);
                                }
                                type_tv.setText(mdataType);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }

                    ((ViewGroup) first_use_tv.getParent()).setVisibility(VISIBLE);
                } else if (masterData_model != null && masterData_model.getMdata_asset().equals("")) {
                    mdataType = "Series";
                    fetch_Assetsdata(All_Api.getAllSeries + client_id + "&user_id=" + user_id);
                } else {
                    fetch_Assetsdata(All_Api.getAllAssets + client_id + "&user_id=" + user_id);
                }
                type_tv.setText(mdataType);
            }
            aset_tv.setText(assetModel.getAsset_code());
        }
        return view;
    }

//    String fetched_url = "";

    private void set_master_data(MasterData_model masterData_model) {
        client_edt.setText(masterData_model.getMdata_client());
        serial_edt.setText(masterData_model.getMdata_serial());
        batch_edt.setText(masterData_model.getMdata_batch());
        uin_edt.setText(masterData_model.getMdata_uin());
        rfid_edt.setText(masterData_model.getMdata_rfid());
        barcode_edt.setText(masterData_model.getMdata_barcode());
        first_use_tv.setText(masterData_model.getDate_of_first_use());
        invoice_dt_tv.setTag(masterData_model.getMdata_material_invoice_date());
        address_txt.setText(masterData_model.getMdata_location());

        make_txt.setText(masterData_model.getMdata_make());
        swl_txt.setText(masterData_model.getMdata_swl());
        form11_remark_txt.setText(masterData_model.getForm11_remark());
        frm13_make_addrs_txt.setText(masterData_model.getFm13_maker_address());
        frm13_process_txt.setText(masterData_model.getFm13_process());
        frm13_thickness_txt.setText(masterData_model.getFm13_thickness());
        frm13_safe_pressure_txt.setText(masterData_model.getFm13_safe_pressure());

        if (!masterData_model.getMdata_jobcard().equals("")) {
            jobcard_tv.setText(masterData_model.getMdata_jobcard());
            assetModel.setJobcard(masterData_model.getMdata_jobcard());
            String url = All_Api.getJobCard + Static_values.client_id + "&user_id=" + Static_values.user_id +
                    "&group_id=" + Static_values.user_id + "&jobcard=" + masterData_model.getMdata_jobcard();
            fetch_data(url, false, null);
        }
        if (!masterData_model.getMdata_sms().equals("")) {
            sms_tv.setText(masterData_model.getMdata_sms());
            assetModel.setSms(masterData_model.getMdata_sms());
        }

        uin_edt.setEnabled(false);
        if (!client_id.equals("419") && !client_id.equals("376") && !client_id.equals("952") && !client_id.equals("395") && !client_id.equals("2069"))
            disable_edit();
        else
            ((ViewGroup) client_edt.getParent().getParent().getParent()).setVisibility(View.GONE);
        if (client_id.equals("419")) {
            ((ViewGroup) client_edt.getParent().getParent().getParent()).setVisibility(VISIBLE);
            client_edt.setEnabled(false);
        }
        try {
            invoice_dt_tv.setText(BaseActivity.Date_Format().format(BaseActivity.server_date_format.parse(masterData_model.getMdata_material_invoice_date())));
        } catch (ParseException e) {
            e.printStackTrace();
            invoice_dt_tv.setText(masterData_model.getMdata_material_invoice_date());
        }
    }

    public void disable_edit() {
        view.findViewById(R.id.or_tv).setVisibility(View.GONE);
        label_btn.setVisibility(View.GONE);
        rfid_btn.setVisibility(View.GONE);
        barcode_btn.setVisibility(View.GONE);
        serial_edt.setEnabled(false);
        batch_edt.setEnabled(false);
        uin_edt.setEnabled(false);
        rfid_edt.setEnabled(false);
        client_edt.setEnabled(false);
        barcode_edt.setEnabled(false);
        invoice_dt_tv.setOnClickListener(null);
        spnr_heading.setVisibility(View.GONE);
        status_spinr.setVisibility(View.GONE);
    }

    @Override
    public void onResume() {
        super.onResume();
        ConstantMethods.find_pageVideo(getActivity(), "inspection master data");
    }

    private TextView jobcard_tv, sms_tv;
    private EditText uin_edt, rfid_edt, barcode_edt, serial_edt, batch_edt, address_txt,
            make_txt,swl_txt,form11_remark_txt,frm13_make_addrs_txt,frm13_process_txt,frm13_thickness_txt,frm13_safe_pressure_txt;
    private AutoCompleteTextView client_edt;
    Spinner status_spinr;
    ArrayList<String> type_array = new ArrayList<>(Arrays.asList(getResString("lbl_asset"), getResString("lbl_asset_series")));

    private void all_Ids() {
        make_txt= view.findViewById(R.id.make_txt);
        swl_txt= view.findViewById(R.id.swl_txt);
        form11_remark_txt= view.findViewById(R.id.form11_remark_txt);
        frm13_make_addrs_txt= view.findViewById(R.id.frm13_make_addrs_txt);
        frm13_process_txt= view.findViewById(R.id.frm13_process_txt);
        frm13_thickness_txt= view.findViewById(R.id.frm13_thickness_txt);
        frm13_safe_pressure_txt= view.findViewById(R.id.frm13_safe_pressure_txt);

        type_spinr = view.findViewById(R.id.type_spinr);
        heading_tv = view.findViewById(R.id.heading_tv);
        type_tv = view.findViewById(R.id.type_tv);
        aset_tv = view.findViewById(R.id.aset_tv);
        jobcard_tv = view.findViewById(R.id.jobcard_tv);
        sms_tv = view.findViewById(R.id.sms_tv);
        client_edt = view.findViewById(R.id.client_edt);
        serial_edt = view.findViewById(R.id.serial_edt);
        batch_edt = view.findViewById(R.id.batch_edt);
        invoice_dt_tv = view.findViewById(R.id.invoice_dt_tv);
        first_use_tv = view.findViewById(R.id.first_use_tv);
        address_txt = view.findViewById(R.id.address_txt);
//        first_dt_tv = view.findViewById(R.id.first_dt_tv);
//        inspc_dt_tv = view.findViewById(R.id.inspc_dt_tv);
        uin_edt = view.findViewById(R.id.uin_edt);
        rfid_edt = view.findViewById(R.id.rfid_edt);
        barcode_edt = view.findViewById(R.id.barcode_edt);
        label_btn = view.findViewById(R.id.label_btn);
        rfid_btn = view.findViewById(R.id.rfid_btn);
        barcode_btn = view.findViewById(R.id.barcode_btn);
        continue_btn = view.findViewById(R.id.continue_btn);
        status_spinr = view.findViewById(R.id.status_spinr);
        spnr_heading = view.findViewById(R.id.spnr_heading);
        status_spinr.setSelection(1);
//
//        adrs_edt = view.findViewById(R.id.adrs_edt);
//        phn_edt =  view.findViewById(R.id.phn_edt);
//        email_edt = view.findViewById(R.id.email_edt);
        heading_tv.setText(this.getTag());
        baseActivity.headerTv.setText("");
        set_listener();
    }

    CustomTextWatcher.OnTextChange textWatcher = new CustomTextWatcher.OnTextChange() {
        @Override
        public void afterChange(TextView view, String text) {
            switch (view.getId()) {
                case R.id.serial_edt:
                case R.id.batch_edt:
                    if (uin_edt.getText().toString().equals(""))
                        uin_edt.setText(batch_edt.getText() + ":" + serial_edt.getText());
                    break;
            }

            if (view.getParent().getParent() instanceof TextInputLayout && ((TextInputLayout) view.getParent().getParent()).getError() != null)
                ((TextInputLayout) view.getParent().getParent()).setError(null);
        }
    };
    ArrayAdapter na_adapter;

    public void set_listener() {
        new CustomTextWatcher(uin_edt, textWatcher);
        new CustomTextWatcher(rfid_edt, textWatcher);
        new CustomTextWatcher(barcode_edt, textWatcher);
        new CustomTextWatcher(serial_edt, textWatcher);
        new CustomTextWatcher(batch_edt, textWatcher);
        new CustomTextWatcher(client_edt, textWatcher);
        invoice_dt_tv.setOnClickListener(this);
        first_use_tv.setOnClickListener(this);
        label_btn.setOnClickListener(this);
        rfid_btn.setOnClickListener(this);
        barcode_btn.setOnClickListener(this);

        client_edt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                assetModel.setMdata_client(((Client_model) parent.getItemAtPosition(position)).getClientId());
            }
        });
        continue_btn.setOnClickListener(this);
        na_adapter = new ArrayAdapter<>(getActivity(), R.layout.spiner_tv, new ArrayList<>(Arrays.asList("NA")));

        jobcard_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (job_cards.size() > 0) {
                    chooseJobSmsDialog("Job Card", new ObjectListener() {
                        @Override
                        public void onResponse(Object obj) {
                            if (obj instanceof Constant_model) {
                                Constant_model job = (Constant_model) obj;
                                assetModel.setJobcard(job.getName());
                                jobcard_tv.setText(job.getName());

                                String url = All_Api.getJobCard + Static_values.client_id + "&user_id=" + Static_values.user_id +
                                        "&group_id=" + Static_values.user_id + "&jobcard=" + job.getName();
                                fetch_data(url, false, null);
                            }
                        }

                        @Override
                        public void onError(String error) {
//                            assetModel.setJobcard(null);
                        }
                    });
                }
            }
        });
        sms_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sms_numbers.size() > 0) {
                    chooseJobSmsDialog("SMS", new ObjectListener() {
                        @Override
                        public void onResponse(Object obj) {
                            if (obj instanceof Constant_model) {
                                Constant_model job = (Constant_model) obj;
                                assetModel.setSms(job.getName());
                                sms_tv.setText(job.getName());
                            }
                        }

                        @Override
                        public void onError(String error) {
//                            assetModel.setSms(null);
                        }
                    });
                }
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.invoice_dt_tv:
            case R.id.first_use_tv:
                show_Date_piker((TextView) v);
                break;
            case R.id.label_btn:
                scan_Ocr(new OCRListener() {
                    @Override
                    public void onScanned(String batch, String serial) {
                        batch_edt.setText(batch);
                        serial_edt.setText(serial);
                    }

                    @Override
                    public void onUIDScanned(String uid) {
                        if (isEmpty(uin_edt))
                            uin_edt.setText(uid);
                    }
                });
                break;
            case R.id.rfid_btn:
                if (baseActivity.mUhfrManager == null) {
                    if (!baseActivity.scan_rfid.isNFCSupport) {
                        AppUtils.show_snak(getActivity(), "Your Device Does Not support NFC.");
                        return;
                    } else if (!baseActivity.scan_rfid.checkNfcEnabled()) {
                        return;
                    }
                }
                if (isPro) {
                    baseActivity.show_OkAlert(getResString("lbl_confirmation"),
                            "Do you wish to add traceability data present on your existing product label instead of new RFID", getResString("lbl_yes"), null, new AlertClickListener() {
                                @Override
                                public void onYesClick() {
                                    scanRFID();
                                }

                                @Override
                                public void onNoClick() {

                                }
                            });
                } else {
                    scanRFID();
                }
                break;
            case R.id.barcode_btn:
                if (isPro) {
                    baseActivity.show_OkAlert(getResString("lbl_confirmation"),
                            "Do you wish to add traceability data present on your existing product label instead of new QR code", getResString("lbl_yes"), null, new AlertClickListener() {
                                @Override
                                public void onYesClick() {
                                    scanBarcode();
                                }

                                @Override
                                public void onNoClick() {

                                }
                            });
                } else {
                    scanBarcode();
                }
                break;
            case R.id.continue_btn:
//                refreshAllData();
                submitData();
                break;
        }
    }

    public void scanBarcode() {
        scan_barcode(new BarcodeListener() {
            @Override
            public void onScanned(String scaned_text) {
                barcode_edt.setText(scaned_text);
                if (isEmpty(uin_edt))
                    uin_edt.setText(scaned_text);
            }
        });
    }

    public void scanRFID() {
        final Dialog alertDialog = show_Rfid_Dialog();
        scan_rfid(new NFC_Listner.Read_interface() {
            @Override
            public void read_complete(String read_text) {
                alertDialog.dismiss();
                if (!read_text.equals("")) {
                    rfid_edt.setText(read_text);
                }
                AppUtils.show_snak(getActivity(), "Tag " + read_text + " scanned");
            }
        });
    }

    private AddAssetModel assetModel;
    String submit_url = All_Api.add_AssetMaster;

    private void submitData() {
        if (assetModel.getAsset_code() == null || assetModel.getAsset_code().equals("")) {
            show_snak(getActivity(), "Please select an Asset");
            return;
        }
        if (barcode_edt.getText().equals("") && !uin_edt.getText().equals(""))
            barcode_edt.setText(uin_edt.getText());
        else if (uin_edt.getText().equals("") && !barcode_edt.getText().equals(""))
            uin_edt.setText(barcode_edt.getText());

        if (isEmpty(batch_edt) || isEmpty(serial_edt) || isEmpty(uin_edt)) {
            show_snak(getActivity(), AppUtils.getResString("lbl_field_mndtry"));
            return;
        }
        if (status_spinr.getSelectedItemPosition() < 1) {
            show_snak(getActivity(), "Please select status");
            return;
        }
        if (((ViewGroup) jobcard_tv.getParent()).getVisibility() == VISIBLE && isPro) {
            if (assetModel.getJobcard() == null || assetModel.getJobcard().equals("")) {
                show_snak(getActivity(), "Please select jobcard");
                return;
            }
            if (assetModel.getSms() == null || assetModel.getSms().equals("")) {
                show_snak(getActivity(), "Please select SMS");
                return;
            }
        }
//        assetModel.setJobcard(jobcard_edt.getText().toString());
        if (page_type.equals("edit_master") && masterData_model != null)
            assetModel.setMaster_id(masterData_model.getMdata_id());
        assetModel.setType(mdataType);
        assetModel.setBatch(batch_edt.getText().toString());
        assetModel.setSerial(serial_edt.getText().toString());
        assetModel.setUin(uin_edt.getText().toString());
        assetModel.setRfid(rfid_edt.getText().toString());
        assetModel.setBarcode(barcode_edt.getText().toString());
        assetModel.setMdata_location(address_txt.getText().toString());
        assetModel.setMdata_status(status_spinr.getSelectedItem().toString());

        assetModel.setMdata_make(make_txt.getText().toString());
        assetModel.setMdata_swl(swl_txt.getText().toString());
        assetModel.setForm11_remark(form11_remark_txt.getText().toString());
        assetModel.setFm13_maker_address(frm13_make_addrs_txt.getText().toString());
        assetModel.setFm13_process(frm13_process_txt.getText().toString());
        assetModel.setFm13_thickness(frm13_thickness_txt.getText().toString());
        assetModel.setFm13_safe_pressure(frm13_safe_pressure_txt.getText().toString());

        if (first_use_tv.getTag() != null)
            assetModel.setDate_of_first_use(first_use_tv.getTag().toString());
        if (invoice_dt_tv.getTag() != null)
            assetModel.setInvoice_date(invoice_dt_tv.getTag().toString());
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(getGson().toJson(assetModel));
            String path = jsonObject.getString("asset_image");
            if (!path.contains("http")) {
                String extension = MimeTypeMap.getFileExtensionFromUrl(path);
                String filetype = null;
                if (extension != null) {
                    filetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                }
                if (path != null && !path.equals("") && new File(path).exists())
                    jsonObject.put("asset_image", "data:" + filetype + ";base64," + AppUtils.Image_toBase64(path));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (getActivity() != null) {
            new NetworkRequest(getActivity()).make_contentpost_request(submit_url, jsonObject, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jObject = new JSONObject(response);
                        show_snak(getActivity(), jObject.getString("message"));
                        if (jObject.getString("status_code").equals("200")) {
                            if (page_type.equals("edit_master")) {
                                if (masterData_model != null && !unique_id.equals("")) {
                                    delete_uploadedsite_data(unique_id, masterData_model.getMdata_id());
                                    if (selected_Site_model != null)
                                        selected_Site_model.setMdata_item_series(assetModel.getAsset_code());
                                }
                                if (isPro) {
                                    is_refresh = true;
                                    if (assetModel.getJobcard() != null && !assetModel.getJobcard().equals(""))
                                        baseActivity.show_OkAlert("Confirmation", "Do you want to update SMS component?", "Yes", "No", new AlertClickListener() {
                                            @Override
                                            public void onYesClick() {
                                                LoadFragment.replace(Add_SMS_Component_Fragment.newInstance(assetModel), baseActivity, getResString("lbl_edit_sms_component"));
                                            }

                                            @Override
                                            public void onNoClick() {
                                                refreshAllData();
                                            }
                                        });
                                    else
                                        refreshAllData();
                                } else {
//                                homeActivity.submit_action("dashboard");
                                    refreshAllData();
//                                baseActivity.onBackPressed();
//                                baseActivity.onBackPressed();
                                }
                            } else if (page_type.equals("add_master"))
                                show_DuplicateAlert(getResString("lbl_add_new_masterdata"), "Master");
                            else
                                show_DuplicateAlert(getResString("lbl_add_newasset"), "Asset");
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


    public void refreshAllData() {
        if (isNetworkAvailable(getActivity())) {
            String url = All_Api.search_Data + "uin=" + masterData_model.getMdata_uin() + "&client_id=" + client_id + "&user_id=" + user_id + "&geo_location=" + baseActivity.curr_lat + "," + baseActivity.curr_lng;
            url = url.replace(" ", "%20");
            NetworkRequest networkRequest = new NetworkRequest(getActivity());
            networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", "" + response);
                    Object json;
                    try {
                        json = new JSONTokener(response).nextValue();
                        if (json instanceof JSONArray) {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            if (jsonObject.has("error")) {
                                show_snak(getActivity(), jsonObject.getString("error"));
                            } else {
                                selected_Site_model = AppUtils.getGson().fromJson(jsonObject.toString(), Site_Model.class);
                                getMaster(selected_Site_model.getMaster_id());
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("JSONException", "" + e.getMessage());
                    }
                }

                @Override
                public void onError(String error) {
                    Log.e("error", "" + error);
                }
            });
        } else {
            show_snak(getActivity(), getResString("lbl_network_alert"));
        }
    }


    private void getMaster(String master_id) {
        new NetworkRequest(baseActivity).getMasterData(master_id, user_id, new ObjectListener() {
            @Override
            public void onResponse(Object obj) {
                masterData_model = (MasterData_model) obj;
                Static_values.selectedMasterData_model = masterData_model;
                if (masterData_model != null && masterData_model.getNot_make_inspected() != 1) {
                    Master_detail_fragment master_fragment = new Master_detail_fragment();
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("img_urls", new ArrayList<>(Collections.singletonList(Static_values.selected_Site_model.getImagepath())));
                    bundle.putStringArrayList("product_name", new ArrayList<>(Collections.singletonList(masterData_model.getMdata_item_series())));
                    if (masterData_model.getMdata_asset().equals(""))
                        bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("asset_series")));
                    else
                        bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("assets")));
                    bundle.putString("page_type", main_page);
                    bundle.putInt("pos", 0);
                    master_fragment.setArguments(bundle);
                    LoadFragment.replace(master_fragment, baseActivity, "" + masterData_model.getMdata_uin());
                } else {
                    homeActivity.submit_action("dashboard");
                }
            }

            @Override
            public void onError(String error) {
                Log.e("error", "" + error);
            }
        });
    }

    private ArrayList<Client_model> clientModels = new ArrayList<>();


    private void find_client(String mdata_client) {
        for (Client_model client_model : clientModels) {
            if (client_model.getClientName().equalsIgnoreCase(mdata_client)) {
                assetModel.setMdata_client(client_model.getClientId());
                break;
            }
        }
    }

    public AlertDialog show_DuplicateAlert(String title, String type) {
        return new AlertDialog.Builder(getActivity())
                .setTitle(getResString("lbl_confirmation"))
                .setMessage(title)
                .setPositiveButton(getResString("lbl_duplicate"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (type.equals("Asset"))
                            homeActivity.submit_action("ins_addAsset");
                        else
                            homeActivity.submit_action("ins_addMaster");

                    }
                })
                .setNegativeButton(getResString("lbl_no"), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        homeActivity.load_home_fragment(false);
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


}