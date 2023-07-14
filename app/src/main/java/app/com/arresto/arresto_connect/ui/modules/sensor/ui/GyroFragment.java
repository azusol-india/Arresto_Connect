package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.EXTRA_DEVICE;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textview.MaterialTextView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.BatteryView;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.FallCountModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.GyroModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.OnDeviceConnect;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.TempGattService;
import butterknife.BindView;
import butterknife.ButterKnife;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class GyroFragment extends Base_Fragment {
    View view;
    DiscoveredBluetoothDevice device;

    @BindView(R.id.asset_name)
    TextView asset_name;

    @BindView(R.id.asset_img)
    ImageView asset_img;

    @BindView(R.id.device_tv)
    MaterialTextView device_tv;

    @BindView(R.id.battery_view)
    BatteryView battery_view;

    @BindView(R.id.table_1)
    TableLayout table_1;

    @BindView(R.id.count_tv)
    TextView data_count_tv;

    @BindView(R.id.dwn_excl_btn)
    TextView dwn_excl_btn;

    int dataLimit=500;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.gyro_view_fragment, parent, false);
            ButterKnife.bind(this, view);
            if (getArguments() != null) {
                if (getArguments().containsKey(EXTRA_DEVICE))
                    device = getArguments().getParcelable(EXTRA_DEVICE);
//                baseActivity.showDialog();
                setDeviceData();
                setListner();
            }
        }
        return view;

    }

    private void setDeviceData() {
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();
        device_tv.setText(deviceName + "\n" + deviceAddress);
        device_tv.setVisibility(View.VISIBLE);

        if (device.getAssetImage() != null) {
            asset_name.setText(device.getAssetName());
            AppUtils.load_image(device.getAssetImage(), asset_img);
            battery_view.setBatteryLevel(device.getBatteryPercentage());
        }
    }

    ArrayList<String> headings = new ArrayList<>(Arrays.asList(" S.No. ", " TimeStamp ", " Acc-X ", " Acc-Y ", " Acc-Z ", " Gyro-X ",
            " Gyro-Y ", " Gyro-Z ", " Clock Count ", " Anticlock count "));
    TableRow.LayoutParams headingParams = new TableRow.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 80);
    List<GyroModel> gyroModels;


    SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");

    @SuppressLint("StaticFieldLeak")
    private void setListner() {
        df.setTimeZone(TimeZone.getTimeZone("IST"));
        dwn_excl_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dwn_excl_btn.getText().equals(getResString("lbl_view")) && excel_file != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.fromFile(excel_file);
                    intent.setDataAndType(uri, "application/vnd.ms-excel");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    try {
                        if (Build.VERSION.SDK_INT >= 24) {
                            Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                            m.invoke(null);
                        }
                        baseActivity.startActivity(Intent.createChooser(intent, "Gyroscope Data"));
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(baseActivity, "No App Available", Toast.LENGTH_SHORT).show();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (service != null && gyroModels.size() > 0)
                        createExcelSheet(gyroModels);
                    else
                        show_snak(baseActivity, "No data to download, please start calibration.");
                }
            }
        });
        TableRow tbrow0 = new TableRow(baseActivity);
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
        gyroModels = new ArrayList<>();
        table_1.addView(tbrow0);

        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onDeviceConnect = new OnDeviceConnect() {
                    @Override
                    public void onAssetFetched(FallCountModel fallCountModel) {
                        Log.e("onAssetFetched ", "is = " + fallCountModel);
                    }

                    @Override
                    public void onBatteryFetched(int percentage) {
                        Log.e("onServiceFetched ", percentage + " and v= " + device.getVersionName());
                        baseActivity.runOnUiThread(new Runnable() {
                            public void run() {
//                                version_tv.setText("" + device.getVersionName());
//                                version_tv.setVisibility(View.VISIBLE);
                                battery_view.setBatteryLevel(percentage);
                                battery_view.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onVersionFetched(String versionName) {

                    }

                    //                    {time:1649844172,x:-15.92,y:-25.86,z:522.95,r:177,p:272,y:58,cl:0,acl:0}
                    @Override
                    public void notifyData(Object rdData) {
                        String data = "" + rdData;
                        printLog("rdData= " + rdData);
                        if (data != null) {
                            baseActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    baseActivity.hideDialog();
                                    try {
                                        GyroModel gyroModel = new GyroModel();
                                        JSONObject notiData = new JSONObject((String) rdData);
                                        String dateTime = df.format(new Date(Long.parseLong(notiData.getString("time")) * 1000L));
                                        gyroModel.setTimeStamp(dateTime);
                                        gyroModel.setAcc_X(notiData.getString("x"));
                                        gyroModel.setAcc_Y(notiData.getString("y"));
                                        gyroModel.setAcc_Z(notiData.getString("z"));
                                        gyroModel.setGyro_X(notiData.getString("r"));
                                        gyroModel.setGyro_Y(notiData.getString("p"));
                                        gyroModel.setGyro_Z(notiData.has("w") ?notiData.getString("w"):"NA");
                                        gyroModel.setClockWiseCount(notiData.has("cl") ? notiData.getString("cl") : "NA");
                                        gyroModel.setAntiClockWiseCount(notiData.has("acl") ? notiData.getString("acl") : "NA");

                                        TableRow row = new TableRow(baseActivity);
                                        int count=gyroModels.size() + 1;
                                        TextView count_tv = newTextView();
                                        count_tv.setText("" + count);

                                        TextView time_tv = newTextView();
                                        time_tv.setText("" + dateTime);

                                        TextView acc_x = newTextView();
                                        acc_x.setText("" + gyroModel.getAcc_X());

                                        TextView acc_y = newTextView();
                                        acc_y.setText("" + gyroModel.getAcc_Y());

                                        TextView acc_z = newTextView();
                                        acc_z.setText("" + gyroModel.getAcc_Z());

                                        TextView gyro_x = newTextView();
                                        gyro_x.setText("" + gyroModel.getGyro_X());

                                        TextView gyro_y = newTextView();
                                        gyro_y.setText("" + gyroModel.getGyro_Y());

                                        TextView gyro_z = newTextView();
                                        gyro_z.setText("" + gyroModel.getGyro_Z());

                                        TextView cl_count = newTextView();
                                        cl_count.setText("" + gyroModel.getClockWiseCount());

                                        TextView acl_count = newTextView();
                                        acl_count.setText("" + gyroModel.getAntiClockWiseCount());

                                        row.addView(count_tv);
                                        row.addView(time_tv);
                                        row.addView(acc_x);
                                        row.addView(acc_y);
                                        row.addView(acc_z);
                                        row.addView(gyro_x);
                                        row.addView(gyro_y);
                                        row.addView(gyro_z);

                                        row.addView(cl_count);
                                        row.addView(acl_count);
                                        table_1.addView(row);
                                        gyroModels.add(gyroModel);
                                        data_count_tv.setText("Count: "+gyroModels.size());
                                        if (gyroModels.size() >= dataLimit) {
                                            dwn_excl_btn.setVisibility(View.VISIBLE);
                                            stopService();
                                        }
                                    } catch (Exception e) {
                                        printLog("Exception= " + e.getMessage());
                                        show_snak(baseActivity, "Please check your sensor version");
                                        stopService();
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onStateChange(String state) {

                    }
                };
                startService();
                return null;
            }
        }.execute();
    }

    TextView newTextView() {
        TextView new_tv = new TextView(getActivity());
        new_tv.setGravity(Gravity.CENTER_HORIZONTAL);
        new_tv.setBackgroundResource(R.drawable.table_divider1);
        new_tv.setPadding(30, 10, 30, 10);
        return new_tv;
    }

    File excel_file;

    public void createExcelSheet(List<GyroModel> gyroModels) {
        String file_name = "GyroData_" + System.currentTimeMillis() + ".xls";
        baseActivity.checkDir(Static_values.directory);
        excel_file = new File(Static_values.directory + file_name);
        WorkbookSettings wbSettings = new WorkbookSettings();
        wbSettings.setLocale(new Locale("en", "EN"));

        WritableWorkbook workbook;
        try {
            workbook = Workbook.createWorkbook(excel_file, wbSettings);
            WritableSheet sheet = workbook.createSheet("Search Sheet", 0);
            try {
                for (int i = 0; i <= gyroModels.size(); i++) {
                    Label label;
                    if (i != 0) {
                        label = new Label(0, i, gyroModels.get(i).getTimeStamp());
                        sheet.addCell(label);
                        label = new Label(1, i, "" + gyroModels.get(i).getAcc_X());
                        sheet.addCell(label);
                        label = new Label(2, i, "" + gyroModels.get(i).getAcc_Y());
                        sheet.addCell(label);
                        label = new Label(3, i, "" + gyroModels.get(i).getAcc_Z());
                        sheet.addCell(label);
                        label = new Label(4, i, "" + gyroModels.get(i).getGyro_X());
                        sheet.addCell(label);
                        label = new Label(5, i, "" + gyroModels.get(i).getGyro_Y());
                        sheet.addCell(label);
                        label = new Label(6, i, "" + gyroModels.get(i).getGyro_Z());
                        sheet.addCell(label);
                    } else {
                        for (int j = 0; j < headings.size(); j++) {
                            label = new Label(j, i, headings.get(j));
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
        dwn_excl_btn.setText(getResString("lbl_view"));
        Toast.makeText(baseActivity, "Data Sheet Saved Successfully.", Toast.LENGTH_LONG).show();
//        show_snak(baseActivity, "Data Sheet Saved Successfully.");
    }


    Intent service;
    OnDeviceConnect onDeviceConnect;

    public void startService() {
        service = new Intent(baseActivity, TempGattService.class);
        service.putExtra("isGyro", true);
        service.putExtra("needReconnect", true);
        GattResultReceiver bankResultReceiver = new GattResultReceiver(new Handler(baseActivity.getMainLooper()), device, onDeviceConnect);
        service.putExtra(EXTRA_DEVICE, device);
        service.putExtra("resultReceiver", bankResultReceiver);
        baseActivity.startService(service);
    }

    public void stopService() {
        if (service != null) {
            Intent service = new Intent(baseActivity, TempGattService.class); //to stop service put null argument
            baseActivity.startService(service);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
    }
}
