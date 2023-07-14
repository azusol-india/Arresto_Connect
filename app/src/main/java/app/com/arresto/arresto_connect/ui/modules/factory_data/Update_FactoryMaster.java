/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.ui.modules.factory_data;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Scan_RFID;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NFC_Listner;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.adapters.Factory_MasterAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

public class Update_FactoryMaster extends Base_Fragment {
    View view;
    MaterialButton scan_btn;
    RecyclerView data_list;
    Factory_MasterAdapter factory_masterAdapter;
    ArrayList component_models;
    String input_type = "";
    RadioGroup rg1;
    EditText uin_edt;
    boolean isAuto, isBroadCasting;
    CheckBox auto_chk;

    public static Update_FactoryMaster newInstance(String heading) {
        Update_FactoryMaster fragmentFirst = new Update_FactoryMaster();
        Bundle args = new Bundle();
        args.putString("type", heading);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    String type = "";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.update_factory_master, parent, false);
            searched_array = new ArrayList<>();
            component_models = new ArrayList<>();
            if (getArguments() != null) {
                type = getArguments().getString("type", "");
            }
            find_id();
            scan_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (input_type.equalsIgnoreCase("Barcode")) {
                        scan_barcode(new BarcodeListener() {
                            @Override
                            public void onScanned(String scaned_text) {
                                checkData(scaned_text);
                            }
                        });
                    } else if (input_type.equalsIgnoreCase("UIN")) {
                        if (!uin_edt.getText().toString().equalsIgnoreCase("")) {
                            String entered_txt = uin_edt.getText().toString();
                            checkData(entered_txt);
                        } else
                            AppUtils.show_snak(getActivity(), getResString("lbl_entr_uin_msg"));
                    } else
                        AppUtils.show_snak(getActivity(), getResString("lbl_slct_inpt_msg"));
                }
            });
//            check_alert();
            if (type.equals("scan_write")) {
                auto_chk.setChecked(true);
                rg1.check(rg1.getChildAt(1).getId());
                scan_btn.performClick();
            } else {
                ((ViewGroup) rg1.getParent()).setVisibility(View.VISIBLE);
            }
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        if (baseActivity.scan_rfid == null || baseActivity.scan_rfid.mNfcAdapter == null) {
            baseActivity.scan_rfid = new Scan_RFID(baseActivity);
        }
        baseActivity.scan_rfid.enableForegroundDispatch();
    }

    @Override
    public void onPause() {
        super.onPause();
        baseActivity.scan_rfid.disable_adapter();
    }


    public void checkData(String _text) {
        if (_text.contains("arresto.in")) {
//         scaned_text = Uri.parse(_text).getQueryParameter("u");
//         search_scaned_data(scaned_text, "UIN");
            find_RFID(_text);
        } else
            search_scaned_data(_text, input_type);
    }

    private BroadcastReceiver mBarcodeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String barcode = intent.getExtras().getString("barcode");
            if (!barcode.equals("")) {
                if (alertDialog != null && alertDialog.isShowing()) {
                    alertDialog.dismiss();
                    alertDialog = null;
                }
                rg1.check(rg1.getChildAt(1).getId());
                printLog("BarCode = " + barcode);
                isBroadCasting = true;
                checkData(barcode);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver((mBarcodeReceiver), new IntentFilter("deviceBarcode"));
    }

    @Override
    public void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBarcodeReceiver);
    }

    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        data_list.setLayoutManager(layoutManager);
        data_list.setPadding(3, 6, 3, 6);
        data_list.getLayoutManager().setMeasurementCacheEnabled(false);
        factory_masterAdapter = new Factory_MasterAdapter(getActivity(), component_models);
        data_list.setAdapter(factory_masterAdapter);
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                input_type = "" + (group.findViewById(checkedId)).getTag();
                if (input_type.equals("UIN")) {
                    uin_edt.setText("");
                    uin_edt.setVisibility(View.VISIBLE);
                    auto_chk.setVisibility(View.GONE);
                } else {
                    uin_edt.setVisibility(View.GONE);
                    auto_chk.setVisibility(View.VISIBLE);
                }
            }
        });
        auto_chk.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isAuto = isChecked;
            }
        });


    }

    private void find_id() {
        scan_btn = view.findViewById(R.id.scan_btn);
        data_list = view.findViewById(R.id.product_list);
        uin_edt = view.findViewById(R.id.uin_edt);
        auto_chk = view.findViewById(R.id.auto_chk);
        rg1 = view.findViewById(R.id.rg1);
        scan_btn.setVisibility(View.VISIBLE);
        setdata();
    }

    ArrayList<String> searched_array;

    public void search_scaned_data(final String key, String input_type) {
        if (isNetworkAvailable(getActivity())) {
            String url;
            if (searched_array.contains(key.toLowerCase())) {
                show_snak(getActivity(), getResString("lbl_alrdy_lst_msg"));
                return;
            }
            url = All_Api.search_Data + input_type.toLowerCase() + "=" + key + "&client_id=" + client_id + "&user_id=" + user_id + "&geo_location=" + baseActivity.curr_lat + "," + baseActivity.curr_lng;
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
//                                component_models.addAll(new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(response, Site_Model[].class))));
//                                factory_masterAdapter.addData(component_models);
//                                data_list.setVisibility(View.VISIBLE);
                                ArrayList<Site_Model> site_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(response, Site_Model[].class)));
                                if (site_models.size() > 0) {
                                    selected_model = site_models.get(0);
                                    find_RFID(selected_model.getMdata_uin());
                                }
                            }
                        } if (json instanceof JSONObject) {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("message")) {
                                show_snak(getActivity(), jsonObject.getString("message"));
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

    Site_Model selected_model;

    public void send_data() {
        JSONObject jsonObject = new JSONObject();
        JSONArray master_array = new JSONArray();
        try {
            master_array.put(new JSONObject()
                    .put("mdata_id", selected_model.getMaster_id())
                    .put("mdata_rfid", selected_model.getMdata_rfid()));
            jsonObject.put("client_id", Static_values.client_id);
            jsonObject.put("data", master_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (master_array.length() < 1) {
            show_snak(getActivity(), "Please insert data");
            return;
        }

        printLog(" jsonObject is " + jsonObject);

        if (getActivity() != null) {
            show_snak(getActivity(), "Product read successfully");
            new NetworkRequest(getActivity()).make_contentpost_request(All_Api.update_rfid, jsonObject, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        show_snak(getActivity(), jsonObject.getString("message"));
                        if (jsonObject.getString("status_code").equals("200")) {
                            component_models.add(selected_model);
                            factory_masterAdapter.addData(component_models);
                            searched_array.add(selected_model.getMdata_rfid().toLowerCase());
                            selected_model = null;
                            uin_edt.setText("");
                            if (isAuto && !isBroadCasting) {
                                scan_btn.performClick();
                            }
                            isBroadCasting = false;
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

    Dialog alertDialog;

    private void find_RFID(final String text_towrite) {
        if (!text_towrite.equals("")) {
            alertDialog = show_Rfid_Dialog();
            baseActivity.write_intent(text_towrite, new NFC_Listner.Write_interface() {
                @Override
                public void write_complete() {
                    alertDialog.dismiss();
                    if (text_towrite.contains("arresto.in")) {
                        component_models.add(text_towrite);
                        factory_masterAdapter.addData(component_models);
                        uin_edt.setText("");
                        if (isAuto && !isBroadCasting) {
                            scan_btn.performClick();
                        }
                        isBroadCasting = false;
                    } else {
                        selected_model.setMdata_rfid(text_towrite);
                        send_data();
                    }
                }
            });
        } else {
            show_snak(getActivity(), getResString("lbl_try_again_msg"));
        }

    }


}
