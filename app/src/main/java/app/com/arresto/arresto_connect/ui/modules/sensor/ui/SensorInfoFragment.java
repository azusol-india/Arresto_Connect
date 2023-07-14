package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.ImageViewCompat;
import androidx.gridlayout.widget.GridLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.custom_views.BatteryView;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.FallCountModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorHistoryModel;
import butterknife.BindView;
import butterknife.ButterKnife;

public class SensorInfoFragment extends Base_Fragment {
    View view;

    @BindView(R.id.grid_lay)
    GridLayout grid_lay;

    @BindView(R.id.asset_name)
    TextView asset_name;

    @BindView(R.id.asset_img)
    ImageView asset_img;

    @BindView(R.id.device_tv)
    MaterialTextView device_tv;

    @BindView(R.id.battery_view)
    BatteryView battery_view;

    @BindView(R.id.fall_count_tv)
    TextView fall_count_tv;

    @BindView(R.id.lock_count_tv)
    TextView lock_count_tv;

    @BindView(R.id.used_count_tv)
    TextView used_count_tv;

    @BindView(R.id.th1_tv)
    TextView th1_tv;

    @BindView(R.id.th2_tv)
    TextView th2_tv;

    @BindView(R.id.th3_tv)
    TextView th3_tv;

    @BindView(R.id.clockwise_tv)
    TextView clockwise_tv;

    @BindView(R.id.anti_clockwise_tv)
    TextView anti_clockwise_tv;

    @BindView(R.id.total_count_tv)
    TextView total_count_tv;
    @BindView(R.id.version_tv)
    TextView version_tv;
    @BindView(R.id.depth_tv)
    TextView depth_tv;

    @BindView(R.id.hook_tv)
    TextView hook_tv;
    @BindView(R.id.t01_img)
    ImageView t01_img;
    @BindView(R.id.t1_img)
    ImageView t1_img;
    @BindView(R.id.t2_img)
    ImageView t2_img;
    @BindView(R.id.t3_img)
    ImageView t3_img;
    @BindView(R.id.t4_img)
    ImageView t4_img;
    @BindView(R.id.t5_img)
    ImageView t5_img;
    @BindView(R.id.t6_img)
    ImageView t6_img;
    @BindView(R.id.reset_btn)
    MaterialButton reset_btn;

    @BindView(R.id.wt_img)
    ImageView wt_img;

    @BindView(R.id.status_tv)
    TextView status_tv;

    @BindView(R.id.status_ic)
    ImageView status_ic;

    @BindView(R.id.hook_status_ic)
    ImageView hook_status_ic;

    @BindView(R.id.view_history)
    MaterialButton view_history;

    @BindView(R.id.start_btn)
    MaterialButton start_btn;

    boolean isWinch;

    String sensorId;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.data_view_fragment, parent, false);
            ButterKnife.bind(this, view);
            AppUtils.load_Gif_image(R.drawable.fall_gif, t1_img);
            AppUtils.load_Gif_image(R.drawable.lock_gif, t2_img);
            AppUtils.load_Gif_image(R.drawable.lock1_gif, t3_img);

            AppUtils.load_Gif_image(R.drawable.lock1_gif, t4_img);
            AppUtils.load_Gif_image(R.drawable.lock1_gif, t5_img);
            AppUtils.load_Gif_image(R.drawable.lock1_gif, t6_img);

            AppUtils.load_Gif_image(R.drawable.winch_img, wt_img);

            ImageViewCompat.setImageTintList(status_ic, ColorStateList.valueOf(getResColor(R.color.grey)));
            status_tv.setText("Fetching details....");
            if (getArguments() != null) {
                if (getArguments().containsKey("sensor_id")) {
                    sensorId = getArguments().getString("sensor_id");
                    ((ViewGroup) reset_btn.getParent()).setVisibility(View.GONE);
                    battery_view.setVisibility(View.GONE);
                    if (sensorId != null && !sensorId.equals("")) {
                        getSensorData(sensorId, objectListener);
                        device_tv.setText(sensorId);
                        device_tv.setVisibility(View.VISIBLE);
                    }
                }
            }

            view_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getHookHistory();
                }
            });
        }
        return view;
    }

    boolean isHook = false;
    boolean dataSet = false;
    //    sensor code here
    ObjectListener objectListener = new ObjectListener() {
        @Override
        public void onResponse(Object obj) {
            if (obj instanceof FallCountModel) {
                FallCountModel sensorInfo = (FallCountModel) obj;
                if (sensorInfo.getAsset_type().equalsIgnoreCase("hook") && !dataSet) {
                    isHook = true;
                    ((ViewGroup) t01_img.getParent()).setVisibility(View.VISIBLE);
                    view_history.setVisibility(View.VISIBLE);
                    AppUtils.load_image(sensorInfo.getAsset_image(), t01_img);
                    ImageViewCompat.setImageTintList(hook_status_ic, ColorStateList.valueOf(getResColor(R.color.app_green)));
                    hook_tv.setText("Connected");
//                    start_btn.setVisibility(View.VISIBLE);
                    dataSet = true;
                }
                if (sensorInfo.getSensor_status().equalsIgnoreCase("connected")) {
                    status_tv.setText("Connected with: " + sensorInfo.getName());
                    ImageViewCompat.setImageTintList(status_ic, ColorStateList.valueOf(getResColor(R.color.app_green)));
                } else {
                    ImageViewCompat.setImageTintList(status_ic, ColorStateList.valueOf(getResColor(R.color.red)));
                    status_tv.setText("Last connection : " + sensorInfo.getName());
                }
                setData(sensorInfo);
            }
        }

        @Override
        public void onError(String error) {

        }
    };

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(baseActivity).registerReceiver(mMessageReceiver, new IntentFilter("sensor_alert"));
        if (sensorId != null && !sensorId.equals("")) {
            startTimer();
            device_tv.setText(sensorId);
            device_tv.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTimer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    Timer sensorTimer;
    TimerTask sensorTimer_Tasks;

    public void startTimer() {
        printLog(SensorInfoFragment.class.getName() + "  startTimer");
        sensorTimer_Tasks = new TimerTask() {
            @Override
            public void run() {
                getSensorData(sensorId, objectListener);
            }
        };
        sensorTimer = new Timer();
        sensorTimer.schedule(sensorTimer_Tasks, 1000, 5 * 1000);
    }

    public void stopTimer() {
        printLog(SensorInfoFragment.class.getName() + "  stopTimer");
        if (sensorTimer_Tasks != null) {
            sensorTimer_Tasks.cancel();
        }
        if (sensorTimer != null) {
            sensorTimer.cancel();
        }
    }


    ArrayList<String> containingThresholds;
    int total_RCount = 0;
    boolean isFirmwareSet;

    private void setData(FallCountModel sensorInfo) {
        if (sensorInfo != null) {
            if (!isFirmwareSet)
                setFirmwareData(sensorInfo);
            int total_count = 0;

            for (FallCountModel.DataCount dataCount : sensorInfo.getCount_data()) {
                if (dataCount.getVibration_type().equals("A") && !dataCount.getCount().equals("")) {
                    fall_count_tv.setText("" + dataCount.getCount());
                } else if (dataCount.getVibration_type().equals("T") && !dataCount.getCount().equals("")) {
                    lock_count_tv.setText("" + dataCount.getCount());
                } else if (dataCount.getVibration_type().equals("F") && !dataCount.getCount().equals("")) {
                    used_count_tv.setText("" + dataCount.getCount());
                } else if (dataCount.getVibration_type().equals("X") && !dataCount.getCount().equals("")) {
                    th1_tv.setText("" + dataCount.getCount());
                } else if (dataCount.getVibration_type().equals("Y") && !dataCount.getCount().equals("")) {
                    th2_tv.setText("" + dataCount.getCount());
                } else if (dataCount.getVibration_type().equals("Z") && !dataCount.getCount().equals("")) {
                    th3_tv.setText("" + dataCount.getCount());
                }
                if (isWinch) {
                    if (dataCount.getVibration_type().equals("cl") && !dataCount.getCount().equals("")) {
                        clockwise_tv.setText(dataCount.getCount());
                        total_count = total_count + Integer.parseInt(dataCount.getCount());
                    } else if (dataCount.getVibration_type().equals("acl") && !dataCount.getCount().equals("")) {
                        anti_clockwise_tv.setText(dataCount.getCount());
                        total_count = total_count + Integer.parseInt(dataCount.getCount());
                    }
                }
                total_RCount = total_count;
            }
            total_count_tv.setText("" + total_count);
        }
    }

    public void setFirmwareData(FallCountModel sensorInfo) {
        isFirmwareSet = true;
        asset_name.setText(sensorInfo.getAsset_code());
        AppUtils.load_image(sensorInfo.getAsset_image(), asset_img);
        if (sensorInfo.getFirmware_info() != null && sensorInfo.getFirmware_info().getContainingThresholds() != null) {
            containingThresholds = sensorInfo.getFirmware_info().getContainingThresholds();
            if (containingThresholds.contains("Threshold 1")) {
                ((ViewGroup) fall_count_tv.getParent()).setVisibility(View.VISIBLE);
            } else {
                grid_lay.removeView(((ViewGroup) fall_count_tv.getParent()));
            }
            if (containingThresholds.contains("Threshold 2")) {
                ((ViewGroup) lock_count_tv.getParent()).setVisibility(View.VISIBLE);
            } else {
                grid_lay.removeView(((ViewGroup) lock_count_tv.getParent()));
            }
            if (containingThresholds.contains("Threshold 3")) {
                ((ViewGroup) used_count_tv.getParent()).setVisibility(View.VISIBLE);
            } else {
                grid_lay.removeView(((ViewGroup) used_count_tv.getParent()));
            }
            if (containingThresholds.contains("Threshold 4")) {
                ((ViewGroup) th1_tv.getParent()).setVisibility(View.VISIBLE);
            } else {
                grid_lay.removeView(((ViewGroup) th1_tv.getParent()));
            }
            if (containingThresholds.contains("Threshold 5")) {
                ((ViewGroup) th2_tv.getParent()).setVisibility(View.VISIBLE);
            } else {
                grid_lay.removeView(((ViewGroup) th2_tv.getParent()));
            }
            if (containingThresholds.contains("Threshold 6")) {
                ((ViewGroup) th3_tv.getParent()).setVisibility(View.VISIBLE);
            } else {
                grid_lay.removeView(((ViewGroup) th3_tv.getParent()));
            }

            if (sensorInfo.getFirmware_info().getWinch() != null &&
                    sensorInfo.getFirmware_info().getWinch().equals("1")) {
                isWinch = true;
                ((ViewGroup) wt_img.getParent()).setVisibility(View.VISIBLE);
                if (sensorInfo.getFirmware_info().getWinch_data() != null) {
                    for (Constant_model item :
                            sensorInfo.getFirmware_info().getWinch_data()) {
                        if (!item.getName().equals("")) {
                            if (item.getId().equalsIgnoreCase("Diameter of Block")) {
                                D = Double.parseDouble(item.getName());
                            } else if (item.getId().equalsIgnoreCase("Diameter of Rope")) {
                                d = Double.parseDouble(item.getName());
                            } else if (item.getId().equalsIgnoreCase("No of turns")) {
                                n = Double.parseDouble(item.getName());
                            }
                        }
                    }
                }
                if (sensorInfo.getFirmware_info().getGear_ratio() != null && !sensorInfo.getFirmware_info().getGear_ratio().equals("")) {
                    gr = Double.parseDouble(sensorInfo.getFirmware_info().getGear_ratio());
                }
            }
        }
    }

    double n = 0;   //number_of_turn
    double d = 0;   //diameter of wire
    double D = 0;   //diameter of drum
    double gr = 0;  //Gear ratio
    double π = 3.14;


    private void getHookHistory() {
        new NetworkRequest(baseActivity).make_get_request(All_Api.hook_sensor_history + client_id + "&sensor_id=" + sensorId, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        String status_code = jsonObject.getString("status_code");
                        if (status_code.equals("200")) {
                            String data = jsonObject.getString("data");
                            List<SensorHistoryModel> hookHistorydata = new ArrayList<>(Arrays.asList(AppUtils.getGson().fromJson(data, SensorHistoryModel[].class)));
                            create_Historydialog(hookHistorydata);
                        } else {
                            AppUtils.show_snak(baseActivity, "" + jsonObject.getString("message"));
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


    private AlertDialog dialog;

    private void create_Historydialog(List<SensorHistoryModel> hookHistorydata) {
        AlertDialog.Builder builder = new AlertDialog.Builder(baseActivity);
        LayoutInflater inflater = baseActivity.getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.new_assethistory_dialog, null);

        ImageView close_btn = dialoglayout.findViewById(R.id.close_btn);
        LinearLayout linear_list = dialoglayout.findViewById(R.id.linear_list);
        TextView totl_hrs = dialoglayout.findViewById(R.id.totl_hrs);
        ((ViewGroup) totl_hrs.getParent()).setVisibility(View.GONE);
        TextView head_tv = dialoglayout.findViewById(R.id.head_tv);
        head_tv.setText("Sensor History");
        close_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        make_childview(linear_list, hookHistorydata);
        builder.setView(dialoglayout);
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    private void make_childview(LinearLayout child_layout, List<SensorHistoryModel> hookHistorydata) {
        child_layout.removeAllViews();
        params1.setMargins(10, 5, 10, 5);
        for (int i = 0; i < hookHistorydata.size(); i++) {
            TextView check_tv = get_text_view(baseActivity);
            check_tv.setTextColor(AppUtils.getResColor(R.color.app_text));
            String dateTime = (i + 1) + ".  " + baseActivity.formatServerDateTime(hookHistorydata.get(i).getDevice_timestamp());
            String vibration = hookHistorydata.get(i).getVibration_type();
            if (vibration.equalsIgnoreCase("hc"))
                check_tv.setText(dateTime + "     Hook Connect");
            else if (vibration.equalsIgnoreCase("hd"))
                check_tv.setText(dateTime + "     Hook Disconnect");
            else check_tv.setText(dateTime + "     Hook " + vibration);
            child_layout.addView(check_tv);
        }
//        flip_in(child_layout);
    }


    private TextView get_text_view(Activity activity) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 5, 10, 5);
        TextView chk_tv = new TextView(activity);
        chk_tv.setLayoutParams(params);
        chk_tv.setPadding(10, 15, 5, 15);
//        chk_tv.setGravity(Gravity.CENTER);
        chk_tv.setBackgroundResource(R.drawable.bg_btm_line);
        GradientDrawable gd = (GradientDrawable) chk_tv.getCompoundDrawables()[0];
        if (gd != null) {
            chk_tv.setBackground(gd);
            gd.setStroke(1, AppUtils.getResColor(R.color.app_text));
        }
        chk_tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
//        chk_tv.setVisibility(View.GONE);
        return chk_tv;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(baseActivity).unregisterReceiver(mMessageReceiver);

    }


    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String status = intent.getStringExtra("status");
            Log.e("onReceive", " BroadcastReceiver run");
            if (isHook) {
                if (status.toLowerCase().contains("hd")) {
                    ImageViewCompat.setImageTintList(hook_status_ic, ColorStateList.valueOf(getResColor(R.color.red)));
                    hook_tv.setText("Not Connected");
                } else if (status.toLowerCase().contains("hc")) {
                    ImageViewCompat.setImageTintList(hook_status_ic, ColorStateList.valueOf(getResColor(R.color.app_green)));
                    hook_tv.setText("Connected");
                }
            }
        }
    };

}

