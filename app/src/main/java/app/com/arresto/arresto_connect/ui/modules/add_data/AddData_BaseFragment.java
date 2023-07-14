package app.com.arresto.arresto_connect.ui.modules.add_data;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.data.models.Client_model;
import app.com.arresto.arresto_connect.data.models.Component_model;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.DataHolder_Model;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.interfaces.OnItemClickListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.adapters.CustomRecyclerAdapter;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;

import static app.com.arresto.arresto_connect.constants.AppUtils.getGson;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;

public abstract class AddData_BaseFragment extends Base_Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanseState) {
        return FragmentView(inflater, parent, savedInstanseState);
    }


    public void chooseAssetDialog(String type, boolean needDetails, Handler handler) {
        Dialog dialog = new Dialog(getContext(), R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        TextView header = dialog.findViewById(R.id.header);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        header.setText("Select " + type + " from the list below");
        ArrayList<Constant_model> data;
        if (type.equals("Asset")) {
            data = assets;
        } else {
            data = asset_series;
        }
        if (data != null && data.size() > 0) {
            OnItemClickListener onItemClickListener = new OnItemClickListener() {
                @Override
                public void onItemClicked(int position, View v, Object data) {
                    Constant_model slctdAsset = (Constant_model) data;
                    if (needDetails) {
                        if (type.equals("Asset")) {
                            get_Component_data(All_Api.components_service + slctdAsset.getId() + "?client_id=" + client_id, handler);
                        }else{
                            get_Product_data(All_Api.product_service + slctdAsset.getId() + "?client_id=" + client_id,handler);
                        }} else {
                        Message msg = new Message();
                        msg.what = 100;
                        Bundle b = new Bundle();
                        b.putString("data", slctdAsset.getName());
                        b.putString("id", slctdAsset.getId());
                        b.putString("type", type);
                        msg.setData(b);
                        handler.sendMessage(msg);
                    }
                    dialog.dismiss();
                }
            };
            Log.e("data length ", " is " + data.size());
            CustomRecyclerAdapter ad = new CustomRecyclerAdapter(getContext(), data, onItemClickListener);
            RecyclerView listView = dialog.findViewById(R.id._list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false);
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(ad);

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    handler.sendEmptyMessage(101);
//                    initView();
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad != null && ad.lastSelected != -1)
//                        setSelection(ad.lastSelected);
                        dialog.cancel();
                    handler.sendEmptyMessage(102);
                }
            });
            srch_prdct.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!srch_prdct.getText().toString().equals("")) {
                        filter(editable.toString().toLowerCase(), data, ad);
                    }
                }
            });

        }
        dialog.show();

    }

    void filter(String filter_txt, ArrayList<Constant_model> assets, CustomRecyclerAdapter ad) {
        List<Constant_model> temp = new ArrayList();
        for (Constant_model d : assets) {
            if (d.getName().toLowerCase().contains(filter_txt)) {
                temp.add(d);
            }
        }
        ad.UpdateData(temp);
    }

    public ArrayList<Constant_model> assets = new ArrayList<>();
    public ArrayList<Constant_model> asset_series = new ArrayList<>();

    public void fetch_Assetsdata(final String url) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    JSONObject object = new JSONObject(response);
                    if (object.getString("status_code").equals("200")) {
                        if (url.contains(All_Api.getAllSeries)) {
                            asset_series = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            Collections.sort(asset_series, new Comparator<Constant_model>() {
                                public int compare(Constant_model obj1, Constant_model obj2) {
                                    return obj1.getName().compareToIgnoreCase(obj2.getName());
                                }
                            });
                        } else {
                            assets = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            Collections.sort(assets, new Comparator<Constant_model>() {
                                public int compare(Constant_model obj1, Constant_model obj2) {
                                    return obj1.getName().compareToIgnoreCase(obj2.getName());
                                }
                            });
                        }
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

    public Component_model selected_asset;

    @SuppressLint("HandlerLeak")
    public void get_Component_data(String components_service, Handler handler) {
        selected_asset = null;
        NetworkRequest.getComponents(getActivity(), components_service, new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    selected_asset = DataHolder_Model.getInstance().getComponent_models().get(0);
                    Message msg1 = new Message();
                    msg1.what = 100;
                    Bundle b = new Bundle();
                    b.putString("type", "Asset");
                    msg1.setData(b);
                    handler.sendMessage(msg1);
                } else {
                    handler.sendEmptyMessage(101);
                }

//                initView();
            }
        });
    }


    public ArrayList<Constant_model> job_cards = new ArrayList<>();
    public ArrayList<Constant_model> sms_numbers = new ArrayList<>();

    public void fetch_data(String url, boolean isJob, ObjectListener listener) {
        new NetworkRequest(getActivity()).make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response ", " is " + response);
                try {
                    Object json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONArray && url.contains(All_Api.all_clientinfo)) {
                        ArrayList<Client_model> clientModels = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(response, Client_model[].class)));
                        listener.onResponse(clientModels);
                    } else {
                        JSONObject object = new JSONObject(response);
                        if (object.getString("status_code").equals("200")) {
                            if (isJob) {
                                job_cards = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            } else {
                                sms_numbers = new ArrayList<>(Arrays.asList(getGson().fromJson(object.getString("data"), Constant_model[].class)));
                            }
                        }
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


    public void chooseJobSmsDialog(String type, ObjectListener listener) {
        Dialog dialog = new Dialog(getContext(), R.style.theme_dialog);
        dialog.setContentView(R.layout.list_dialoge);
        TextView header = dialog.findViewById(R.id.header);
        MaterialButton btn_ok = dialog.findViewById(R.id.save_btn);
        ImageView close_btn = dialog.findViewById(R.id.close_btn);
        EditText srch_prdct = dialog.findViewById(R.id.srch_prdct);
        ((ViewGroup) srch_prdct.getParent()).setVisibility(View.VISIBLE);
        header.setText("Select " + type + " from the list below");
        ArrayList<Constant_model> data;
        if (type.toLowerCase().contains("sms")) {
            data = sms_numbers;
        } else {
            data = job_cards;
        }
        if (data != null && data.size() > 0) {
            OnItemClickListener onItemClickListener = new OnItemClickListener() {
                @Override
                public void onItemClicked(int position, View v, Object data) {
                    Constant_model slctdAsset = (Constant_model) data;
                    listener.onResponse(slctdAsset);
                    dialog.dismiss();
                }
            };
            Log.e("data length ", " is " + data.size());
            CustomRecyclerAdapter ad = new CustomRecyclerAdapter(getContext(), data, onItemClickListener);
            RecyclerView listView = dialog.findViewById(R.id._list);
            LinearLayoutManager layoutManager = new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false);
            listView.setLayoutManager(layoutManager);
            listView.setAdapter(ad);

            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    listener.onError("Data not selected");
                }
            });
            btn_ok.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ad != null && ad.lastSelected != -1)
//                        setSelection(ad.lastSelected);
                        dialog.cancel();
                    listener.onError("Data not selected");
                }
            });
            srch_prdct.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (!srch_prdct.getText().toString().equals("")) {
                        filter(editable.toString().toLowerCase(), data, ad);
                    }
                }
            });

        }
        dialog.show();

    }


    public Product_model selected_asset_series;

    @SuppressLint("HandlerLeak")
    public void get_Product_data(String components_service, Handler handler) {
        selected_asset_series = null;
        NetworkRequest.getProduct(getActivity(), components_service, new Handler() {
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && (msg.obj.toString()).equals("200")) {
                    selected_asset_series = DataHolder_Model.getInstance().getProduct_models().get(0);
                    Message msg1 = new Message();
                    msg1.what = 100;
                    Bundle b = new Bundle();
                    b.putString("type", "Series");
                    msg1.setData(b);
                    handler.sendMessage(msg1);
                } else {
                    handler.sendEmptyMessage(101);
                }

//                initView();
            }
        });
    }

}
