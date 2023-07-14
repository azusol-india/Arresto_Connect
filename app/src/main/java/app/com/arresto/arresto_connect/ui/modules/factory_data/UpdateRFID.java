package app.com.arresto.arresto_connect.ui.modules.factory_data;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Scan_RFID;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.factory_tables.FactoryMasterTable;
import app.com.arresto.arresto_connect.database.factory_tables.Factory_BachTable;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NFC_Listner;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.adapters.Factory_RfidAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

public class UpdateRFID extends Base_Fragment {
    RecyclerView product_list;
    View view;
    TextView search_btn, scan_btn;

    Factory_BachTable.Factory_BachTable_Dao factory_bachTable_dao;
    FactoryMasterTable.FactoryMasterTable_Dao factoryMasterTable_dao;
    long batch_id = -1;
    LinearLayout search_lay;
    Handler handler = new Handler();

    public static UpdateRFID newInstance(long batch_id) {
        UpdateRFID fragment = new UpdateRFID();
        Bundle args = new Bundle();
        args.putLong("batch_id", batch_id);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            batch_id = getArguments().getLong("batch_id");
        }
    }

    public EditText searchView;
    String scannedText = "";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_update_rfid, parent, false);
            factory_bachTable_dao = AppController.getInstance().getDatabase().getFactory_bachTable_dao();
            factoryMasterTable_dao = AppController.getInstance().getDatabase().getFactoryMasterTable_dao();
            find_id();
            if (batch_id > -1) {
                view.findViewById(R.id.search_sec).setVisibility(View.VISIBLE);
                baseActivity.upload_btn.setVisibility(View.VISIBLE);
                scan_btn.setVisibility(View.VISIBLE);
                scan_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        scan_barcode(new BarcodeListener() {
                            @Override
                            public void onScanned(String scaned_text) {
                                if (scaned_text != null) {
                                    scannedText = scaned_text;
                                    if (scaned_text.contains("https://arresto.in")) {
                                        scaned_text = Uri.parse(scaned_text).getQueryParameter("m");
                                    }
                                    find_RFID(scaned_text);
                                } else {
                                    show_snak(getActivity(), getResString("lbl_try_again_msg"));
                                }
                            }
                        });
                    }
                });
                updateMasterList();
                baseActivity.upload_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        send_data(factoryMasterTables);
                    }
                });
            } else {
                search_lay.setVisibility(View.VISIBLE);
                search_btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetch_data(All_Api.search_by_batch);
                    }
                });
                updateBatchList();
            }
        } else {
            if (batch_id == -1) {
                updateBatchList();
            }
        }
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        baseActivity.upload_btn.setVisibility(View.GONE);
    }

    EditText btch_no_edt, from_edt, to_edt;

    private void find_id() {
        searchView = view.findViewById(R.id.search_view);
        search_lay = view.findViewById(R.id.search_lay);
        btch_no_edt = view.findViewById(R.id.btch_no_edt);
        from_edt = view.findViewById(R.id.from_edt);
        to_edt = view.findViewById(R.id.to_edt);
        product_list = view.findViewById(R.id.product_list);
        search_btn = view.findViewById(R.id.search_btn);
        scan_btn = view.findViewById(R.id.scan_btn);
        setdata();
    }

    Factory_RfidAdapter factory_masterAdapter;

    private void setdata() {
        if (batch_id > -1) {
            GridLayoutManager gridManager = new GridLayoutManager(getActivity(), 3, GridLayoutManager.VERTICAL, false);
            product_list.setLayoutManager(gridManager);
        } else {
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
            product_list.setLayoutManager(layoutManager);
        }  product_list.setPadding(3, 6, 3, 6);
        product_list.setClipToPadding(false);
        product_list.getLayoutManager().setMeasurementCacheEnabled(false);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String newText = editable.toString();
                if (TextUtils.isEmpty(newText)) {
                    updateMasterList();
                } else {
                    filter_project_Data(newText);
                }
            }
        });
    }

    public void fetch_data(String url) {
        url = url + client_id + "&batch_no=" + btch_no_edt.getText() + "&from=" + from_edt.getText() + "&to=" + to_edt.getText() + "&user_id=" + user_id + "&time=" + System.currentTimeMillis() + "&geo_location=" + baseActivity.curr_lat + "," + baseActivity.curr_lng;
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String status_code = jsonObject.getString("status_code");
                    if (status_code.equals("200")) {
                        String data = jsonObject.getString("data");
                        List<FactoryMasterTable> factoryMasterModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data, FactoryMasterTable[].class)));
                        insert_datainDB(factoryMasterModels);
                        updateBatchList();
                    } else {
                        show_snak(baseActivity, jsonObject.getString("msg"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String message) {
                Log.e("onError ", " Register_request " + message);
            }
        });
    }

    public void updateBatchList() {
        List<Factory_BachTable> factory_bachTables = factory_bachTable_dao.getAll(user_id);
        if (factory_masterAdapter != null) {
            factory_masterAdapter.addData(factory_bachTables);
            factory_masterAdapter.notifyDataSetChanged();
        } else {
            factory_masterAdapter = new Factory_RfidAdapter(getActivity(), factory_bachTables, true);
            product_list.setAdapter(factory_masterAdapter);
        }
    }


    List<FactoryMasterTable> factoryMasterTables;

    private void updateMasterList() {
        factoryMasterTables = factoryMasterTable_dao.getMaster(batch_id);
        Collections.sort(factoryMasterTables, new Comparator<FactoryMasterTable>() {
            @Override
            public int compare(FactoryMasterTable c1, FactoryMasterTable c2) {
                return c2.getMdata_rfid().compareToIgnoreCase(c1.getMdata_rfid());
            }
        });
        if (factory_masterAdapter != null) {
            factory_masterAdapter.addData(factoryMasterTables);
            factory_masterAdapter.notifyDataSetChanged();
        } else {
            factory_masterAdapter = new Factory_RfidAdapter(getActivity(), factoryMasterTables, false);
            product_list.setAdapter(factory_masterAdapter);
        }
    }


    private void filter_project_Data(String newText) {
        if (factoryMasterTables.size() > 0) {
            ArrayList<FactoryMasterTable> new_listData = new ArrayList<>();
            for (int i = 0; i < factoryMasterTables.size(); i++) {
                FactoryMasterTable masterTable = factoryMasterTables.get(i);
                if (masterTable.getMdata_serial().contains(newText) || masterTable.getMdata_uin().contains(newText)
                        || masterTable.getMdata_rfid().contains(newText) || masterTable.getMdata_item_series().contains(newText)) {
                    new_listData.add(masterTable);
                }
            }
            factory_masterAdapter.addData(new_listData);
            factory_masterAdapter.notifyDataSetChanged();
        }
    }

    void insert_datainDB(List<FactoryMasterTable> factoryMasterModels) {
        Factory_BachTable factory_bachTable = new Factory_BachTable();
        factory_bachTable.setClient_id(client_id);
        factory_bachTable.setUser_id(user_id);
        factory_bachTable.setBatch_no(btch_no_edt.getText().toString());
        factory_bachTable.setSerial_from(from_edt.getText().toString());
        factory_bachTable.setSerial_to(to_edt.getText().toString());
        long batch_id = factory_bachTable_dao.insert(factory_bachTable);
        for (FactoryMasterTable factoryMasterTable : factoryMasterModels) {
            factoryMasterTable.setBatch_fk(batch_id);
        }
        factoryMasterTable_dao.insertAll(factoryMasterModels);
    }

    private void find_RFID(String scaned_data) {
        final FactoryMasterTable masterTable = factoryMasterTable_dao.getMasterRow(batch_id, scaned_data);
        if (masterTable != null) {
            if (!masterTable.getMdata_rfid().equals("")) {
//                show_snak(baseActivity, "RFID tag is already written");
                Dialog dialog = baseActivity.show_OkAlert("RFID Already Exists!", "RFID tag is already exists on this product", null, "Close", null);
//                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
//                        .setTitle("RFID Already Exists!")
//                        .setMessage("RFID tag is already exists on this product")
//                        .setMessage("RFID tag is already exists on this product do you want to update it?")
//                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                write_RFID_Tag(masterTable);
//                                dialog.cancel();
//                            }
//                        })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.cancel();
//                            }
//                        })
//                        .setIcon(android.R.drawable.ic_dialog_alert);
//
//                AlertDialog dialog = builder.create();
//                dialog.show();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                }, 1500);
            } else {
                write_RFID_Tag(masterTable);
            }
        } else {
            AppUtils.show_snak(baseActivity, "Batch and serial no. not match list.");
        }
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

    void write_RFID_Tag(final FactoryMasterTable masterTable) {
        final String scan_data = masterTable.getMdata_uin();
        if (!scan_data.equals("")) {
            final Dialog alertDialog = show_Rfid_Dialog();
            baseActivity.write_intent(scannedText, new NFC_Listner.Write_interface() {
                @Override
                public void write_complete() {
                    alertDialog.dismiss();
                    show_snak(getActivity(), "RFID tag write successfully");
                    masterTable.setMdata_rfid(scan_data);
                    factoryMasterTable_dao.updateMaster(masterTable);
                    updateMasterList();
                    scannedText = "";
                }
            });
        } else {
            show_snak(getActivity(), getResString("lbl_try_again_msg"));
//            new CustomToast().Show_Toast(getActivity(), HomeActivity.textHome, );
        }
    }

    public void send_data(final List<FactoryMasterTable> masterTables) {
        JSONObject jsonObject = new JSONObject();
        JSONArray master_array = new JSONArray();
        try {
            for (FactoryMasterTable masterTable : masterTables) {
                if (!masterTable.getMdata_rfid().equals("")) {
                    master_array.put(new JSONObject()
                            .put("mdata_id", masterTable.getMdata_id())
                            .put("mdata_rfid", masterTable.getMdata_rfid()));
                }
            }
            jsonObject.put("client_id", Static_values.client_id);
            jsonObject.put("data", master_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (master_array.length() < 1) {
            show_snak(getActivity(), "Please insert data");
            return;
        }

        Log.e("jsonObject ", " jsonObject is " + jsonObject);

        if (getActivity() != null) {
            new NetworkRequest(getActivity()).make_contentpost_request(All_Api.update_rfid, jsonObject, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        show_snak(getActivity(), jsonObject.getString("message"));
                        if (jsonObject.getString("status_code").equals("200")) {
                            factory_bachTable_dao.deleteBatch(batch_id);
                            factoryMasterTable_dao.deleteMaster(batch_id);
                            getActivity().onBackPressed();
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
}
