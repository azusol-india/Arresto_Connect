package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResColor;
import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.selected_Site_model;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.EXTRA_DEVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.widget.ImageViewCompat;
import androidx.gridlayout.widget.GridLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.BatteryView;
import app.com.arresto.arresto_connect.data.models.Constant_model;
import app.com.arresto.arresto_connect.data.models.MasterData_model;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.interfaces.ObjectListener;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.fragments.Master_detail_fragment;
import app.com.arresto.arresto_connect.ui.modules.ec_management.EC_Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.FallCountModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.OnDeviceConnect;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorHistoryModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorVibrationModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.TempGattService;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DeviceViewFragment extends Base_Fragment {
    View view;
    DiscoveredBluetoothDevice device;

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
    boolean isHook;
    boolean isHookStarted;

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
            status_tv.setText("Connecting....");
            if (getArguments() != null) {
                if (getArguments().containsKey(EXTRA_DEVICE))
                    device = getArguments().getParcelable(EXTRA_DEVICE);
                setDeviceData();
                setListner();
            }

            reset_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    total_RCount = device.getTotalRotationCount();
                    resetCountService();
                    anti_clockwise_tv.setText("0");
                    clockwise_tv.setText("0");
                    depth_tv.setText("0");
                }
            });
            view_history.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getHookHistory();
                }
            });
            start_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startStopHookService();
                }
            });
            ImageViewCompat.setImageTintList(hook_status_ic, ColorStateList.valueOf(getResColor(R.color.red)));
        }
        return view;
    }

    ArrayList<String> containingThresholds;
    int total_RCount = 0;

    private void setDeviceData() {
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();
        device_tv.setText(deviceName + "\n" + deviceAddress);
        device_tv.setVisibility(View.VISIBLE);

        if (device.getAssetImage() != null) {
            asset_name.setText(device.getAssetName());
            AppUtils.load_image(device.getAssetImage(), asset_img);
            battery_view.setBatteryLevel(device.getBatteryPercentage());
            fall_count_tv.setText("" + device.getFall_count());
            lock_count_tv.setText("" + device.getLock_count());
            used_count_tv.setText("" + device.getFree_fall_count());
            th1_tv.setText("" + device.getTh1_count());
            th2_tv.setText("" + device.getTh2_count());
            th3_tv.setText("" + device.getTh3_count());
            if (device.getFirmwareInfo() != null && device.getFirmwareInfo().getContainingThresholds() != null) {
                containingThresholds = device.getFirmwareInfo().getContainingThresholds();
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

                if (device.getFirmwareInfo().getWinch() != null &&
                        device.getFirmwareInfo().getWinch().equals("1")) {
                    isWinch = true;
                    grid_lay.removeView((ViewGroup) t01_img.getParent());
                    ((ViewGroup) wt_img.getParent()).setVisibility(View.VISIBLE);
                    total_RCount = device.getTotalRotationCount();
                    total_count_tv.setText("" + total_RCount);
                    if (device.getFirmwareInfo().getWinch_data() != null) {
                        for (Constant_model item :
                                device.getFirmwareInfo().getWinch_data()) {
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
                    if (device.getFirmwareInfo().getGear_ratio() != null && !device.getFirmwareInfo().getGear_ratio().equals("")) {
                        gr = Double.parseDouble(device.getFirmwareInfo().getGear_ratio());
                    }
                } else if (device.getFirmwareType().equalsIgnoreCase("hook")) {
                    isHook = true;
                    ((ViewGroup) t01_img.getParent()).setVisibility(View.VISIBLE);
                    view_history.setVisibility(View.VISIBLE);
                    start_btn.setVisibility(View.VISIBLE);
                    AppUtils.load_image(device.getAssetImage(), t01_img);
                    ((ViewGroup) t01_img.getParent()).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToMasterPage("1204863:0001");
                        }
                    });
                }
            }
        }

        device.setFall_count(0);
        device.setLock_count(0);
        device.setFree_fall_count(0);
        device.setTh1_count(0);
        device.setTh2_count(0);
        device.setTh3_count(0);
    }


    @SuppressLint("StaticFieldLeak")
    private void setListner() {
        new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                onDeviceConnect = new OnDeviceConnect() {
                    @Override
                    public void onAssetFetched(FallCountModel fallCountModel) {
                        Log.e("onAssetFetched ", "is = " + fallCountModel);
                        if (fallCountModel != null) {
//                            asset_name.setText(fallCountModel.getAsset_code());
//                            AppUtils.load_image(fallCountModel.getAsset_image(), asset_img);

                            int total_count = total_RCount;
                            for (FallCountModel.DataCount dataCount : fallCountModel.getCount_data()) {
                                if (dataCount.getVibration_type().equals("A") && !dataCount.getCount().equals("")) {
                                    device.setFall_count(device.getFall_count() + Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("T") && !dataCount.getCount().equals("")) {
                                    device.setLock_count(device.getLock_count() + Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("F") && !dataCount.getCount().equals("")) {
                                    device.setFree_fall_count(device.getFree_fall_count() + Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("X") && !dataCount.getCount().equals("")) {
                                    device.setTh1_count(device.getTh1_count() + Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("Y") && !dataCount.getCount().equals("")) {
                                    device.setTh2_count(device.getTh2_count() + Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("Z") && !dataCount.getCount().equals("")) {
                                    device.setTh3_count(device.getTh3_count() + Integer.parseInt(dataCount.getCount()));
                                }
                                if (isWinch) {
                                    if (dataCount.getVibration_type().equals("cl") && !dataCount.getCount().equals("")) {
                                        total_count =+  Integer.parseInt(dataCount.getCount());
                                    } else if (dataCount.getVibration_type().equals("acl") && !dataCount.getCount().equals("")) {
                                        total_count =+ Integer.parseInt(dataCount.getCount());
                                    }
                                }
                                total_RCount = total_count;
                            }
                            total_count_tv.setText("" + total_count);
                            device.setTotalRotationCount(total_RCount);
                            fall_count_tv.setText("" + device.getFall_count());
                            lock_count_tv.setText("" + device.getLock_count());
                            used_count_tv.setText("" + device.getFree_fall_count());
                            th1_tv.setText("" + device.getTh1_count());
                            th2_tv.setText("" + device.getTh2_count());
                            th3_tv.setText("" + device.getTh3_count());
                        }
                    }

                    @Override
                    public void onBatteryFetched(int percentage) {
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
                        baseActivity.runOnUiThread(new Runnable() {
                            public void run() {
                                device.setVersionName(versionName);
                                version_tv.setText("" + versionName);
                                version_tv.setVisibility(View.VISIBLE);
                            }
                        });
                    }


                    @Override
                    public void notifyData(Object rdata) {
                        if (rdata != null && rdata instanceof SensorVibrationModel) {
                            SensorVibrationModel data = (SensorVibrationModel) rdata;
                            baseActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (data.getVibrationType().equals("A")) {
                                        device.setFall_count(device.getFall_count() + 1);
                                        fall_count_tv.setText("" + device.getFall_count());
                                    } else if (data.getVibrationType().equals("T")) {
                                        device.setLock_count(device.getLock_count() + 1);
                                        lock_count_tv.setText("" + device.getLock_count());
                                    } else if (data.getVibrationType().equals("F")) {
                                        device.setFree_fall_count(device.getFree_fall_count() + 1);
                                        used_count_tv.setText("" + device.getFree_fall_count());
                                    } else if (data.getVibrationType().equals("X")) {
                                        device.setTh1_count(device.getTh1_count() + 1);
                                        th1_tv.setText("" + device.getTh1_count());
                                    } else if (data.getVibrationType().equals("Y")) {
                                        device.setTh2_count(device.getTh2_count() + 1);
                                        th2_tv.setText("" + device.getTh2_count());
                                    } else if (data.getVibrationType().equals("Z")) {
                                        device.setTh3_count(device.getTh3_count() + 1);
                                        th3_tv.setText("" + device.getTh3_count());
                                    }
                                    if (isWinch && data.getAntiClockCount() != null) {
                                        if (!data.getClockWiseCount().equals("") && !data.getClockWiseCount().equals("0")) {
                                            lastCl = lastCl + 1;
                                            clockwise_tv.setText("" + lastCl);
                                            total_RCount = total_RCount + 1;
                                        }
                                        if (!data.getAntiClockCount().equals("") && !data.getAntiClockCount().equals("0")) {
                                            lastAcl = lastAcl + 1;
                                            anti_clockwise_tv.setText("" + lastAcl);
                                            total_RCount = total_RCount + 1;
                                        }
                                        calculateDepth(lastAcl, lastCl);
                                        total_count_tv.setText("" + total_RCount);
                                        device.setTotalRotationCount(total_RCount);
                                    } else if (isHook) {
                                        if (data.getHook_mode() == 1) {
                                            stopNotification();
                                            ImageViewCompat.setImageTintList(hook_status_ic, ColorStateList.valueOf(getResColor(R.color.app_green)));
                                            hook_tv.setText("Connected");
                                        } else if (data.getHook_mode() == 0) {
                                            startNotification();
                                            ImageViewCompat.setImageTintList(hook_status_ic, ColorStateList.valueOf(getResColor(R.color.red)));
                                            hook_tv.setText("Not Connected");
                                        }
                                    }
                                }
                            });
                        }
                    }

                    @Override
                    public void onStateChange(String state) {
                        status_tv.setText("" + state);
                        if (state.equals("Connected")) {
                            ImageViewCompat.setImageTintList(status_ic, ColorStateList.valueOf(getResColor(R.color.app_green)));
                        } else {
                            ImageViewCompat.setImageTintList(status_ic, ColorStateList.valueOf(getResColor(R.color.red)));
                        }
                    }
                };
                startService();
                return null;
            }
        }.execute();
    }

    //   String lastCl="0",lastAcl="0";
    int lastCl = 0, lastAcl = 0;

    Intent service;
    OnDeviceConnect onDeviceConnect;

    public void startService() {
        GattResultReceiver bankResultReceiver = new GattResultReceiver(new Handler(baseActivity.getMainLooper()), device, onDeviceConnect);
        service = new Intent(baseActivity, TempGattService.class);
        service.putExtra("resultReceiver", bankResultReceiver);
        service.putExtra(EXTRA_DEVICE, device);
        baseActivity.startService(service);
    }

    public void stopService() {
        if (service != null) {
            Intent service = new Intent(baseActivity, TempGattService.class); //to stop service put null argument
            baseActivity.startService(service);
        }
    }

    public void resetCountService() {
        if (service != null) {
            Intent service = new Intent(baseActivity, TempGattService.class);
            service.putExtra("deviceName", device.getName());
            baseActivity.startService(service);
        }
    }

    public void startStopHookService() {
        if (service != null) {
            Intent service = new Intent(baseActivity, TempGattService.class);
            if (!isHookStarted) {
                service.putExtra("deviceName", device.getName());
                start_btn.setText(getResString("lbl_stop"));
            } else {
                service.putExtra("deviceName", device.getName());
                start_btn.setText(getResString("lbl_start"));
            }
            baseActivity.startService(service);
            isHookStarted = !isHookStarted;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService();
    }

    double n = 0;   //number_of_turn
    double d = 0;   //diameter of wire
    double D = 0;   //diameter of drum
    double gr = 0;  //Gear ratio
    double π = 3.14;

    public void calculateDepth(int a, int c) {
        double E = Math.abs(a - c);
        int x = (int) Math.ceil(E / n);
        double user_depth = (x * π * n * (D + d * (x + 1)) - ((x * n) - E) * π * (D + (2 * x * d))) / (gr * 1000);
        printLog("a === " + a);
        printLog("c === " + c);
        printLog("user_depth === " + user_depth);
        if (user_depth > 0)
            depth_tv.setText("" + EC_Base_Fragment.round(user_depth, 2));
    }

    public void goToMasterPage(String uin) {
        if (isNetworkAvailable(getActivity())) {
            String url = All_Api.search_Data + "uin=" + uin + "&client_id=" + client_id + "&user_id=" + user_id + "&geo_location=" + baseActivity.curr_lat + "," + baseActivity.curr_lng;
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
                MasterData_model masterData_model = (MasterData_model) obj;
                Static_values.selectedMasterData_model = masterData_model;

                Master_detail_fragment master_fragment = new Master_detail_fragment();
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("img_urls", new ArrayList<>(Collections.singletonList(Static_values.selected_Site_model.getImagepath())));
                bundle.putStringArrayList("product_name", new ArrayList<>(Collections.singletonList(masterData_model.getMdata_item_series())));
                if (masterData_model.getMdata_asset().equals(""))
                    bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("asset_series")));
                else
                    bundle.putStringArrayList("section_type", new ArrayList<>(Collections.singletonList("assets")));
                bundle.putString("page_type", "inspection");
                bundle.putInt("pos", 0);
                master_fragment.setArguments(bundle);
                LoadFragment.replace(master_fragment, baseActivity, "" + masterData_model.getMdata_uin());
            }

            @Override
            public void onError(String error) {
                Log.e("error", "" + error);
            }
        });
    }

    private void getHookHistory() {
        new NetworkRequest(baseActivity).make_get_request(All_Api.hook_sensor_history + client_id + "&sensor_id=" + device.getName() , new NetworkRequest.VolleyResponseListener() {
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
            String dateTime=(i+1)+".  "+baseActivity.formatServerDateTime(hookHistorydata.get(i).getDevice_timestamp());
            String vibration=hookHistorydata.get(i).getVibration_type();
            if (vibration.equalsIgnoreCase("hc"))
                check_tv.setText(dateTime+"     Hook Connect");
            else if(vibration.equalsIgnoreCase("hd"))
                check_tv.setText(dateTime+"     Hook Disconnect");
            else check_tv.setText(dateTime+"     Hook "+vibration);
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


    private int i = 0;

    //    int j = 0;
    private void flip_in(final LinearLayout layout) {
        View view = layout.getChildAt(i);
        Animation bounce = AnimationUtils.loadAnimation(baseActivity, R.anim.bounce);
        bounce.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                i++;
                if (i < layout.getChildCount())
                    flip_in(layout);
                else
                    i = 0;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.setVisibility(View.VISIBLE);
        view.startAnimation(bounce);
    }
}
