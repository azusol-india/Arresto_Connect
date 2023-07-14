package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.BatteryView;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.CalibrationService;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.MeanModel;
import butterknife.BindView;
import butterknife.ButterKnife;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.BATTERY_CHAR;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.DEVICE;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.EXTRA_DEVICE;

public class CalibrationFragment extends Base_Fragment {
    @BindView(R.id.tv_summary)
    MaterialTextView tv_summary;
    @BindView(R.id.device_tv)
    MaterialTextView device_tv;
    @BindView(R.id.mean_tv)
    TextView mean_tv;
    @BindView(R.id.dwn_excl_btn)
    TextView dwn_excl_btn;
    @BindView(R.id.stop_btn)
    MaterialButton stop_btn;
    @BindView(R.id.peak_btn)
    MaterialButton peak_btn;
    @BindView(R.id.mean_list)
    RecyclerView mean_list;
    @BindView(R.id.battery_view)
    BatteryView battery_view;

    BluetoothDevice dv;
    View view;
    MeanAdapter meanAdapter;
    CalibrationService mService;
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mService = ((CalibrationService.LocalBinder) rawBinder).getService();
            if (mService != null)
                mService.connect(dv);
        }

        public void onServiceDisconnected(ComponentName classname) {
            printLog("onServiceDisconnected mService= " + mService);
            mService.disconnect();
            mService = null;
        }
    };

    String calibration_name, asset_code, alias_name, uin, master_id;
     String update_id;
    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.calibration_fragment, parent, false);
            ButterKnife.bind(this, view);
            if (getArguments() != null) {
                if (getArguments().containsKey(EXTRA_DEVICE)) {
                    DiscoveredBluetoothDevice device = getArguments().getParcelable(EXTRA_DEVICE);
                    dv = device.getDevice();
                } else
                    dv = getArguments().getParcelable(DEVICE);
                calibration_name = getArguments().getString("calibration_name");
                asset_code = getArguments().getString("asset_code");
                alias_name = getArguments().getString("alias_name");
                uin = getArguments().getString("uin");
                update_id = getArguments().getString("update_id");
                master_id = getArguments().getString("master_id");
            }

            mean_tv.setMovementMethod(new ScrollingMovementMethod());
            LinearLayoutManager Vertical = new LinearLayoutManager(baseActivity, LinearLayoutManager.VERTICAL, false);
            Vertical.setAutoMeasureEnabled(false);
            meanAdapter = new MeanAdapter(this, new ArrayList<>());
            mean_list.setLayoutManager(Vertical);
            mean_list.setAdapter(meanAdapter);

            final String deviceName = dv.getName();
            final String deviceAddress = dv.getAddress();
            device_tv.setText(deviceName + "\n" + deviceAddress);
            device_tv.setVisibility(View.VISIBLE);

            service_init();

            stop_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    show_progress();
                    mService.setNotifyEnable(isStop);
                    isStop = !isStop;
                }
            });
            peak_btn.setVisibility(View.GONE);
            dwn_excl_btn.setVisibility(View.GONE);
            peak_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    progressDialog.show();
//                    showPeakData();
                    if (mService != null)
                        mService.stopCalibration();
                    PeakValueFragment fragment = PeakValueFragment.newInstance();
                    Bundle bundle = new Bundle();
                    bundle.putString("data", AppUtils.getGson().toJson(mService.meanArray));
                    bundle.putString("minValue", "" + mService.minValue);
                    bundle.putString("maxValue", "" + mService.maxValue);
                    bundle.putString("calibration_name", calibration_name);
                    bundle.putString("asset_code", asset_code);
                    bundle.putString("alias_name", alias_name);
                    bundle.putString("uin", uin);
                    bundle.putString("master_id", master_id);
                    bundle.putString("update_id", update_id);
                    bundle.putString("sensor_name", dv.getName());
                    bundle.putParcelable(DEVICE, dv);
                    fragment.setArguments(bundle);
                    LoadFragment.replace(fragment, baseActivity, dv.getName());
                }
            });

            dwn_excl_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mService != null && mService.meanArray.size() > 0)
                        createExcelSheet(mService.meanArray);
                    else
                        show_snak(baseActivity, "No data to download, please start calibration.");
                }
            });
        }
        return view;
    }

    private void service_init() {
        Intent bindIntent = new Intent(baseActivity, CalibrationService.class);
        baseActivity.bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        LocalBroadcastManager.getInstance(baseActivity).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        printLog("onDestroy call");
        if (mService != null)
            mService.stopCalibration();
//        stopClicked = true;
//        if (bluetoothGatt != null)
//            bluetoothGatt.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (UARTStatusChangeReceiver != null)
            LocalBroadcastManager.getInstance(baseActivity).unregisterReceiver(UARTStatusChangeReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UARTStatusChangeReceiver != null)
            LocalBroadcastManager.getInstance(baseActivity).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }

    boolean isStop;

    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case CalibrationService.ACTION_GATT_CONNECTED:
                    baseActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            printLog("UART_CONNECT_MSG");
                        }
                    });
                    break;
                case CalibrationService.ACTION_GATT_DISCONNECTED:
                    baseActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                            printLog("UART_DISCONNECT_MSG " + currentDateTimeString);
                            if (!isStop && mService.mBluetoothGatt != null)
                                mService.mBluetoothGatt.connect();
                            else {
                                hide_progress();
                            }
                        }
                    });
                    break;
                case CalibrationService.ACTION_GATT_SERVICES_DISCOVERED:
                    ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                    toneGen1.startTone(ToneGenerator.TONE_CDMA_ABBR_ALERT, 200);
                    mService.getbattery();
                    break;

                case CalibrationService.ACTION_DESCRIPTOR_WRITE:
                    if (isStop) {
                        stop_btn.setText("Start Calibration");
                        peak_btn.setVisibility(View.VISIBLE);
                        dwn_excl_btn.setVisibility(View.VISIBLE);
                    } else {
                        stop_btn.setText("Stop Calibration");
                        peak_btn.setVisibility(View.GONE);
                        dwn_excl_btn.setVisibility(View.GONE);
                    }
                    hide_progress();
                    break;
                case CalibrationService.NOTIFICATION_DATA:
                    baseActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            mean_tv.setText(mService.mean_str);
                        }
                    });

                    break;
                case CalibrationService.ACTION_DATA_AVAILABLE:
                    ArrayList<Parcelable> parcelableArr = intent.getParcelableArrayListExtra(CalibrationService.EXTRA_DATA);
                    if (parcelableArr != null && parcelableArr.size() > 0) {
                        baseActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                try {
                                    BluetoothGattCharacteristic characteristic = (BluetoothGattCharacteristic) parcelableArr.get(0);
                                    if (characteristic.getUuid().toString().equals(BATTERY_CHAR)) {
                                        int bt = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                                        printLog("Battery level " + bt);
                                        battery_view.setBatteryLevel(bt);
                                    }
//                            listAdapter.add("[" + currentDateTimeString + "] RX: " + text);
//                            messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                                } catch (Exception e) {
                                    printLog(e.toString());
                                }
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

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CalibrationService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(CalibrationService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(CalibrationService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(CalibrationService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(CalibrationService.DEVICE_DOES_NOT_SUPPORT_UART);
        intentFilter.addAction(CalibrationService.ACTION_DESCRIPTOR_WRITE);
        intentFilter.addAction(CalibrationService.NOTIFICATION_DATA);
        return intentFilter;
    }

    private static ArrayList<String> table_headings = new ArrayList<>(Arrays.asList("DateTime ", "Mean Value"));

    public void createExcelSheet(List<MeanModel> meanArray) {
        String Fnamexls = "MeanData_" + System.currentTimeMillis() + ".xls";
        File directory = new File(Static_values.directory);
        directory.mkdirs();
        File file = new File(directory, Fnamexls);

        WorkbookSettings wbSettings = new WorkbookSettings();

        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(file, wbSettings);
            WritableSheet sheet = workbook.createSheet("Search Sheet", 0);
            try {
                for (int i = 0; i <= meanArray.size(); i++) {
                    Label label;
                    if (i != 0) {
                        label = new Label(0, i, meanArray.get(i - 1).getTime_stamp());
                        sheet.addCell(label);
                        label = new Label(1, i, "" + meanArray.get(i - 1).getMean_value());
                        sheet.addCell(label);
                    } else {
                        for (int j = 0; j < table_headings.size(); j++) {
                            label = new Label(j, i, table_headings.get(j));
                            sheet.addCell(label);
                        }
                    }
                }

            } catch (RowsExceededException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            workbook.write();
            try {
                workbook.close();
            } catch (WriteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //createExcel(excelSheet);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Toast.makeText(baseActivity, "Data Sheet Saved Successfully.", Toast.LENGTH_LONG).show();
//        show_snak(baseActivity, "Data Sheet Saved Successfully.");
    }


}
