package app.com.arresto.arresto_connect.ui.modules.factory_data;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NFC_Listner;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.adapters.Factory_MasterAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

public class AttachSensor extends Base_Fragment {
    View view;
    MaterialButton add_btn, submit_btn;
    RecyclerView data_list;
    Factory_MasterAdapter factory_masterAdapter;
    ArrayList data_models;

    public static AttachSensor newInstance(String heading) {
        AttachSensor fragmentFirst = new AttachSensor();
        Bundle args = new Bundle();
        args.putString("type", heading);
        fragmentFirst.setArguments(args);
        return fragmentFirst;
    }

    String type = "";

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.attach_sensor_master, parent, false);
            searched_array = new ArrayList<>();
            data_models = new ArrayList<>();
            if (getArguments() != null) {
                type = getArguments().getString("type", "");
            }
            find_id();
        }
        return view;
    }


    private void setdata() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        data_list.setLayoutManager(layoutManager);
        data_list.setPadding(3, 6, 3, 6);
        data_list.getLayoutManager().setMeasurementCacheEnabled(false);
        factory_masterAdapter = new Factory_MasterAdapter(getActivity(), data_models);
        data_list.setAdapter(factory_masterAdapter);
    }
   TextView blank_tv;
    private void find_id() {
        add_btn = view.findViewById(R.id.add_btn);
        submit_btn = view.findViewById(R.id.submit_btn);
        data_list = view.findViewById(R.id.product_list);
        blank_tv = view.findViewById(R.id.blank_tv);
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showScanDialog();
            }
        });
        submit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             send_data();
            }
        });
        setdata();
    }


    public void showScanDialog() {
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_attach_sensor);

        final EditText asset_edit = dialog.findViewById(R.id.asset_edit);
        final EditText sensor_edit = dialog.findViewById(R.id.sensor_edit);
        final TextView scan_asset_btn = dialog.findViewById(R.id.scan_asset_btn);
        final TextView scan_sensor_btn = dialog.findViewById(R.id.scan_sensor_btn);
        MaterialButton attach_btn = dialog.findViewById(R.id.attach_btn);
        final ImageView close_btn = dialog.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        scan_asset_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_barcode(new BarcodeListener() {
                    @Override
                    public void onScanned(String scaned_text) {
                        asset_edit.setTag(scaned_text);
                        if (scaned_text.contains("arresto.in"))
                            asset_edit.setText(Uri.parse(scaned_text).getQueryParameter("u"));
                        else
                            asset_edit.setText(scaned_text);
                    }
                });
            }
        });

        scan_sensor_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan_barcode(new BarcodeListener() {
                    @Override
                    public void onScanned(String scaned_text) {
                        if (!scaned_text.isEmpty())
                            sensor_edit.setText(scaned_text);
                    }
                });
            }
        });
        attach_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!asset_edit.getText().toString().equals("") && !sensor_edit.getText().toString().equals("")) {
                    Constant_model constantModel = new Constant_model();
                    constantModel.setName(sensor_edit.getText().toString());
                    if (asset_edit.getTag()!=null&&asset_edit.getTag().toString().contains("arresto.in")) {
                        constantModel.setId(asset_edit.getText().toString());
                        data_models.add(constantModel);
                        factory_masterAdapter.addData(data_models);
                        checkBlankList();
                    } else {
                        verify_barcode_data(asset_edit.getText().toString(),constantModel);
                    }
                    dialog.dismiss();
                } else {
                    Toast.makeText(getActivity(), "Please scan or enter asset and sensor!", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialog.show();
    }

    void checkBlankList(){
        if(data_models.size()>0){
            blank_tv.setVisibility(View.GONE);
        }else{
            blank_tv.setVisibility(View.VISIBLE);
        }
    }

    ArrayList<String> searched_array;

    public void verify_barcode_data(final String key, Constant_model constantModel) {
        if (isNetworkAvailable(getActivity())) {
            String url;
            url = All_Api.search_Data + "uin=" + key + "&client_id=" + client_id + "&user_id=" + user_id + "&geo_location=" + baseActivity.curr_lat + "," + baseActivity.curr_lng;
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
                                ArrayList<Site_Model> site_models = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(response, Site_Model[].class)));
                                if (site_models.size() > 0) {
                                    Site_Model selected_model = site_models.get(0);
                                    constantModel.setId(selected_model.getMdata_uin());
                                    data_models.add(constantModel);
                                    factory_masterAdapter.addData(data_models);
                                    checkBlankList();
                                }
                            }
                        }
                        if (json instanceof JSONObject) {
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


    public void send_data() {
        JSONObject jsonObject = new JSONObject();
        JSONArray master_array = new JSONArray();
        try {
            jsonObject.put("client_id", Static_values.client_id);
            jsonObject.put("data", master_array);
            for (Object item: data_models) {
                Constant_model model= (Constant_model) item;
                JSONObject json = new JSONObject();
                json.put("uin", model.getId());
                json.put("sensor_id", model.getName());
                master_array.put(json);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (master_array.length() < 1) {
            show_snak(getActivity(), "Please insert data");
            return;
        }

        printLog(" jsonObject is " + jsonObject);

        if (getActivity() != null) {
            new NetworkRequest(getActivity()).make_contentpost_request(All_Api.attachSensor, jsonObject, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        show_snak(getActivity(), jsonObject.getString("message"));
                        if (jsonObject.getString("status_code").equals("200")) {
                            show_snak(baseActivity, "All sensors attached successfully.");
                            data_models.clear();
                            factory_masterAdapter.notifyDataSetChanged();
                            checkBlankList();
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
            show_snak(baseActivity, getResString("lbl_try_again_msg"));
        }
    }



}