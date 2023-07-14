package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.button.MaterialButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.GRadioGroup;
import app.com.arresto.arresto_connect.custom_views.My_Checkbox;
import app.com.arresto.arresto_connect.data.models.Dynamic_Var;
import app.com.arresto.arresto_connect.interfaces.AlertClickListener;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.CalibrationService;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.MeanModel;
import butterknife.BindView;
import butterknife.ButterKnife;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.network.All_Api.calibration_threshold_api;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.DEVICE;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.THRESHOLD_DATA_CHAR;

public class PeakValueFragment extends Base_Fragment {
    View view;
    @BindView(R.id.mean_table)
    TableLayout mean_table;
    @BindView(R.id.calculate_btn)
    TextView calculate_btn;

    public static PeakValueFragment newInstance() {
        PeakValueFragment sitesMapFragment = new PeakValueFragment();
        return sitesMapFragment;
    }

    ArrayList<MeanModel> meanArray;
    String minValue, maxValue, calibration_name, asset_code, alias_name, uin, master_id, sensor_name;
    Handler handler = new Handler();
    BluetoothDevice dv;
    String update_id;
    boolean isUpdateAsset;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.peak_value_fragment, parent, false);
            ButterKnife.bind(this, view);
            if (getArguments() != null) {
                if (getArguments().containsKey(DEVICE))
                    dv = getArguments().getParcelable(DEVICE);
                meanArray = new ArrayList<>(Arrays.asList(baseActivity.getGson().fromJson(getArguments().getString("data"), MeanModel[].class)));
                minValue = getArguments().getString("minValue");
                maxValue = getArguments().getString("maxValue");
                calibration_name = getArguments().getString("calibration_name");
                asset_code = getArguments().getString("asset_code");
                alias_name = getArguments().getString("alias_name");
                uin = getArguments().getString("uin");
                master_id = getArguments().getString("master_id");
                sensor_name = getArguments().getString("sensor_name");
                update_id = getArguments().getString("update_id");

                if (meanArray != null && meanArray.size() > 0) {
                    show_progress();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            slctdModels = new ArrayList<>();
                            init();
                        }
                    }, 500);
                }
            }
            calculate_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    show_progress();
                    showPeakData();
                }
            });

            service_init();
        }
        return view;
    }

    ArrayList<String> headings = new ArrayList<>(Arrays.asList(" Sl.No ", " TimeStamp ", " Value "));
    ArrayList<MeanModel> slctdModels;
    boolean allSelected = false;

    public void init() {
        mean_table.removeAllViews();
        TableRow tbrow0 = new TableRow(baseActivity);
        CheckBox checkBox = new CheckBox(baseActivity);
//        checkBox.setScaleX(0.8f);
//        checkBox.setScaleY(0.7f);
        checkBox.setLayoutParams(headingParams);
        checkBox.setChecked(allSelected);
        checkBox.setText("All");
        checkBox.setPadding(10, 5, 15, 5);
        checkBox.setBackgroundColor(getResColor(R.color.app_text));
        checkBox.setTextColor(Color.WHITE);
        checkBox.setButtonTintList(ColorStateList.valueOf(Dynamic_Var.getInstance().getApp_background()));
//        checkBox.setLayoutParams(layoutParams);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                show_progress();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (isChecked) {
                            allSelected = true;
                            slctdModels.clear();
                            slctdModels.addAll(meanArray);
                        } else {
                            allSelected = false;
                            slctdModels.clear();
                        }
                        init();
                    }
                }, 200);
            }
        });
        tbrow0.addView(checkBox);
        for (String h : headings) {
            TextView tv0 = new TextView(baseActivity);
            tv0.setText(h);
            tv0.setGravity(Gravity.CENTER);
            tv0.setLayoutParams(headingParams);
            tv0.setTextColor(Color.WHITE);
            tv0.setBackgroundColor(getResColor(R.color.app_text));
            tv0.setPadding(10, 10, 10, 10);
            tbrow0.addView(tv0);
        }

        mean_table.addView(tbrow0);

        for (int i = 0; i < meanArray.size(); i++) {
            MeanModel model = meanArray.get(i);
            TableRow tbrow = new TableRow(baseActivity);

            My_Checkbox checkBox1 = new My_Checkbox(baseActivity);
            checkBox1.setLayoutParams(layoutParams);
            if (slctdModels.contains(model)) {
                checkBox1.setChecked(true);
            }
            checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        slctdModels.add(model);
                    else
                        slctdModels.remove(model);
                }
            });
            tbrow.addView(checkBox1);
            TextView t1 = newTextview("" + (i + 1));
            t1.setLayoutParams(layoutParams0);
            tbrow.addView(t1);
            TextView t2 = newTextview(model.getTime_stamp());
            tbrow.addView(t2);
            TextView t3 = newTextview("" + model.getMean_value());
            tbrow.addView(t3);
            t1.setText(" " + (mean_table.getChildCount()));
            mean_table.addView(tbrow);
            t3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkBox1.performClick();
                }
            });
        }
        hide_progress();
    }

    TableRow.LayoutParams headingParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 80);
    TableRow.LayoutParams layoutParams0 = new TableRow.LayoutParams(140, 120);
    TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 120);

    private TextView newTextview(String text) {
        TextView textView = new TextView(getActivity());
        textView.setTextSize(12);
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(layoutParams);
        textView.setText(text);
        textView.setMinWidth(200);
        textView.setTextColor(getResColor(R.color.app_text));
        textView.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        textView.setSingleLine(false);
        textView.setBackgroundResource(R.drawable.table_divider1);
        textView.setPadding(5, 2, 5, 2);
        return textView;
    }

    String threshold_value = "";

    public void showPeakData() {
        double ave = 0;
        if (slctdModels != null) {
            for (MeanModel v : slctdModels) {
                ave = ave + v.getMean_value();
            }
            if (slctdModels.size() > 0)
                ave = EC_Base_Fragment.round(ave / slctdModels.size(), 2);
        }
        final Dialog dialog = new Dialog(baseActivity, R.style.theme_dialog);
        dialog.setContentView(R.layout.dialog_sensore_peak);
        final EditText min_edit = dialog.findViewById(R.id.min_edit);
        final EditText max_edit = dialog.findViewById(R.id.max_edit);
        final EditText ave_edit = dialog.findViewById(R.id.ave_edit);
        final TextView cal_name_tv = dialog.findViewById(R.id.cal_name_tv);
        final TextView aset_tv = dialog.findViewById(R.id.aset_tv);
        final EditText alias_edt = dialog.findViewById(R.id.alias_edt);

        final RadioButton mini_radio = dialog.findViewById(R.id.mini_radio);
        final RadioButton max_radio = dialog.findViewById(R.id.max_radio);
        final RadioButton ave_radio = dialog.findViewById(R.id.ave_radio);
        GRadioGroup gr = new GRadioGroup(mini_radio, max_radio, ave_radio);
        cal_name_tv.setText(calibration_name);
        aset_tv.setText(asset_code);
        alias_edt.setText(alias_name);
        MaterialButton set_btn = dialog.findViewById(R.id.set_btn);
        final ImageView close_btn = dialog.findViewById(R.id.close_btn);
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        min_edit.setText("" + minValue);
        max_edit.setText("" + maxValue);
        ave_edit.setText("" + ave);

        set_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                threshold_value = "";
                int slctd_pos = gr.getSelectedPosition();
                printLog("selected position : " + slctd_pos);
                if (slctd_pos == 0)
                    threshold_value = min_edit.getText().toString();
                else if (slctd_pos == 1)
                    threshold_value = max_edit.getText().toString();
                else if (slctd_pos == 2)
                    threshold_value = ave_edit.getText().toString();
                else {
                    show_snak(baseActivity, "Please select Threshold value.");
                    return;
                }
                alias_name = alias_edt.getText().toString();
                if (!threshold_value.equals("")) {
                    if (mService != null && dv != null) {
                        progressDialog.mBuilder.text("Updating Device...");
                        show_progress();
                        mService.connect(dv);
                        setTimeOut();
                    }
                    dialog.dismiss();
                } else show_snak(baseActivity, "Threshold value could't be blank.");
            }
        });
        dialog.show();
        hide_progress();
    }


    private void postData(String threshold_value) {

        JSONObject object = new JSONObject();
        try {
            object.put("client_id", Static_values.client_id);
            object.put("sensor_id", sensor_name);
            object.put("calibration_value", calibration_name);
            object.put("asset_code", asset_code);
            object.put("model_alias", alias_name);
//            if (isUpdateThreshold)
//                object.put("only_threshold", "1");
            if (isUpdateAsset)
                object.put("force_update", "1");
            object.put("threshold_value", threshold_value);
            object.put("uin", uin);
            object.put("sensor_dbindex", update_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = calibration_threshold_api;
//        if (isUpdateThreshold)
//            url = calibration_threshold_update;
        new NetworkRequest(getActivity()).make_contentpost_request(url, object, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);
                    if (jObject.getString("status_code").equals("200")) {
                        show_snak(getActivity(), jObject.getString("message"));
                        ((HomeActivity) baseActivity).submit_action("add_sensor");
                    } else if (jObject.getString("status_code").equals("409")) {
                        duplicacyALert(jObject.getString("message"), threshold_value);
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


    void duplicacyALert(String message, String threshold_value) {
        baseActivity.show_OkAlert("Alert!", message +
                "\nDo you want to reassign it?", "Yes", "No", new AlertClickListener() {
            @Override
            public void onYesClick() {
                isUpdateAsset = true;
                postData(threshold_value);
            }

            @Override
            public void onNoClick() {

            }
        });

    }

    CalibrationService mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((CalibrationService.LocalBinder) rawBinder).getService();
        }

        public void onServiceDisconnected(ComponentName classname) {
            printLog("onServiceDisconnected mService= " + mService);
            mService.disconnect();
            mService = null;
        }
    };


    public void setTimeOut() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mService != null) {
                    if (mService.mConnectionState == mService.STATE_DISCONNECTED || mService.mConnectionState == mService.STATE_CONNECTING) {
                        mService.connect(dv);
                    }
//                    setTimeOut();
                } else {
                    show_snak(baseActivity, "Please try again!");
                    baseActivity.onBackPressed();
                }
            }
        }, 5 * 1000);
    }

    private void service_init() {
        Intent bindIntent = new Intent(baseActivity, CalibrationService.class);
        baseActivity.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(baseActivity).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }


    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CalibrationService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(CalibrationService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(CalibrationService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(CalibrationService.ACTION_CHARACTERISTIC_WRITE);
        return intentFilter;
    }


    boolean isWriteCalled;
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            ArrayList<Parcelable> parcelableArr = intent.getParcelableArrayListExtra(CalibrationService.EXTRA_DATA);
            switch (action) {
                case CalibrationService.ACTION_GATT_CONNECTED:
                    baseActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            printLog("gatt_CONNECT_MSG");
                        }
                    });
                    break;
                case CalibrationService.ACTION_GATT_DISCONNECTED:
                    baseActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            printLog("gatt_DISCONNECT_MSG " + currentDateTimeString);
                            hide_progress();
//                            if (!isStop && mService.mBluetoothGatt != null)
//                                mService.mBluetoothGatt.connect();
//                            else {
//                                hide_progress();
//                            }
                        }
                    });
                    break;
                case CalibrationService.ACTION_GATT_SERVICES_DISCOVERED:
                    if (!isWriteCalled) {
                        int threshold = (int) Float.parseFloat(threshold_value);
                        String txt_to_write = getThresholdName() + threshold;
                        mService.writeThreshold(txt_to_write);
                        isWriteCalled = true;
                    }
                    break;

                case CalibrationService.ACTION_CHARACTERISTIC_WRITE:
                    if (parcelableArr != null && parcelableArr.size() > 0) {
                        baseActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) parcelableArr.get(0);
                                if (characteristic.getUuid().toString().equals(THRESHOLD_DATA_CHAR)) {
                                    mService.writeRebootService();
                                    hide_progress();
                                    postData(threshold_value);
                                }
                            }
                        });
                    }
                    break;
                case CalibrationService.NOTIFICATION_DATA:

                    break;
                case CalibrationService.ACTION_DATA_AVAILABLE:
                    if (parcelableArr != null && parcelableArr.size() > 0) {
                        baseActivity.runOnUiThread(new Runnable() {
                            public void run() {

                            }
                        });
                    }
                    break;
                case CalibrationService.DEVICE_DOES_NOT_SUPPORT_UART:
                    printLog("Device doesn't support UART. Disconnecting");
                    mService.disconnect();
                    break;
            }

        }
    };

    private String getThresholdName() {
        switch (calibration_name) {
            case "Threshold 1":
                return "SET_FA_1:";
            case "Threshold 2":
                return "SET_TS_1:";
            case "Threshold 3":
                return "SET_FF_1:";
            case "Threshold 4":
                return "SET_TH1_1:";
            case "Threshold 5":
                return "SET_TH2_1:";
            case "Threshold 6":
                return "SET_TH3_1:";
            default:
                return "SET_FA_1:";
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mService != null)
            mService.stopCalibration();
        if (UARTStatusChangeReceiver != null)
            LocalBroadcastManager.getInstance(baseActivity).unregisterReceiver(UARTStatusChangeReceiver);
//        stopClicked = true;
//        if (bluetoothGatt != null)
//            bluetoothGatt.disconnect();
    }

}
