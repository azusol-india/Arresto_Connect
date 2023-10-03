/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.EXTRA_DEVICE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.custom_views.BatteryView;
import app.com.arresto.arresto_connect.data.models.Product_model;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.modules.kowledge_tree.Karam_infonet_product_frg;
import app.com.arresto.arresto_connect.ui.modules.sensor.DeviceDiffCallback;
import app.com.arresto.arresto_connect.ui.modules.sensor.DevicesLiveData;
import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.FallCountModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.GattResultReceiver;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.OnDeviceConnect;
import butterknife.BindView;
import butterknife.ButterKnife;

public class DevicesAdapter extends RecyclerView.Adapter<DevicesAdapter.ViewHolder> {
    List<DiscoveredBluetoothDevice> devices;
    private List<AsyncTask> tasks;
    private OnItemClickListener onItemClickListener;
    BaseActivity activity;
    Intent service;
    boolean isGyro;
    public static final String TAG = "DevicesAdapter";

    @FunctionalInterface
    public interface OnItemClickListener {
        void onItemClick(@NonNull final DiscoveredBluetoothDevice device);
    }

    public void setOnItemClickListener(final OnItemClickListener listener) {
        onItemClickListener = listener;
    }

    public DevicesAdapter(@NonNull final BaseActivity activity, Intent service, boolean isGyro, @NonNull final DevicesLiveData devicesLiveData) {
        setHasStableIds(true);
        this.service = service;
        this.activity = activity;
        this.isGyro = isGyro;
        tasks = new ArrayList<>();
        sensorMap = new HashMap<>();
        devicesLiveData.observe(activity, newDevices -> {
            final DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DeviceDiffCallback(devices, newDevices), false);
            devices = newDevices;
            result.dispatchUpdatesTo(this);
        });
    }

    public void updateAdapter(List<DiscoveredBluetoothDevice> new_devices) {
        for (DiscoveredBluetoothDevice device : new_devices) {
            device.matches(device.getScanResult());
        }
    }

    public void cancelAllTask() {
        for (AsyncTask task : tasks) {
            task.cancel(true);
        }
    }

//    String image_url = "https://karam-bucket.s3.us-west-2.amazonaws.com/productimage/RETRACTABLE-BLOCKS/PCGS12.png";
    String image_url = null;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        final View layoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.device_item, parent, false);
        return new ViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final DiscoveredBluetoothDevice device = devices.get(position);
        final String deviceName = device.getName();
        device.setListPosition(position);
        if (!TextUtils.isEmpty(deviceName))
            holder.deviceName.setText(deviceName);
        else
            holder.deviceName.setText(R.string.unknown_device);
        holder.address_tv.setText(device.getAddress());
        final int rssiPercent = (int) (100.0f * (127.0f + device.getRssi()) / (127.0f + 20.0f));
        holder.conct_tv.setText("Connectivity : " + rssiPercent);

        holder.view_history.setOnClickListener(view -> {
            create_Historydialog(activity);
        });
        holder.update_btn.setVisibility(View.GONE);

        if (sensorMap.containsKey(deviceName)) {
            FallCountModel model = sensorMap.get(deviceName);
            holder.asset_name.setText(model.getAsset_code());
            AppUtils.load_image(model.getAsset_image(), holder.asset_img);
            holder.details_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product_model product = new Product_model();
                    product.setProductName(model.getAsset_code());
                    product.setProduct_description("");
                    product.setProductImage(model.getAsset_image());
                    if (model.getType().equals("series"))
                        product.setType("asset_series");
                    else
                        product.setType("assets");
                    ArrayList products = new ArrayList();
                    products.add(product);
                    Karam_infonet_product_frg karam_infonet_product_frgm = new Karam_infonet_product_frg();
                    Bundle bundle = new Bundle();
                    bundle.putString("products", new Gson().toJson(products));
                    bundle.putInt("pos", 0);
                    karam_infonet_product_frgm.setArguments(bundle);
                    LoadFragment.replace(karam_infonet_product_frgm, activity, AppUtils.getResString("lbl_product_details"));
                }
            });

            holder.status_btn.setOnClickListener(view -> {
                if (!device.getDeviceMode().equals("booting") && device.getThresholds_data() != null && !device.getThresholds_data().equals("")) {
                    String data = getThresholdString(device.getThresholds_data());
                    activity.show_OkAlert("Sensor Threshold Details", data, null, "Close", null);
                }
            });

            holder.view_btn.setOnClickListener(view -> {
                if (!device.getDeviceMode().equals("booting")) {
                    if (isVersionMatch(model, device)) {
                        if (isGyro) {
                            GyroFragment gyroFragment = new GyroFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(EXTRA_DEVICE, device);
                            gyroFragment.setArguments(bundle);
                            LoadFragment.replace(gyroFragment, activity, device.getName());
                        } else {
                            DeviceViewFragment viewFragment = new DeviceViewFragment();
                            Bundle bundle = new Bundle();
                            bundle.putParcelable(EXTRA_DEVICE, device);
                            viewFragment.setArguments(bundle);
                            LoadFragment.replace(viewFragment, activity, device.getName());
                        }
                    } else if (!device.getVersionName().equals("") && !model.getFirmware_info().getFirmware_version().equals("")) {
                        AppUtils.show_snak(activity, "New firmware available, Please update device first!");
                    }
                }
            });
        } else {
            holder.asset_name.setText("Fetching data...");
            AppUtils.load_image(image_url, holder.asset_img);
        }

        if (device.getDeviceMode().equals("booting")) {
            holder.dv_mode_tv.setVisibility(View.VISIBLE);
            holder.dv_mode_tv.setText("Wait device is rebooting...");
        } else {
            holder.dv_mode_tv.setText("");
            holder.dv_mode_tv.setVisibility(View.GONE);
        }

        holder.connect_btn.setOnClickListener(v->{
            Log.d(TAG, "isConnected:started Service");
            makeBackGroundGatt(devices.get(holder.getAdapterPosition()), holder);
        });

        if (device.isConnected()) {
            Log.d(TAG, "isConnected:true "+device.getBatteryPercentage());
            Log.d(TAG, "sensorMap "+sensorMap.containsKey(deviceName));
            if (device.getBatteryPercentage() != -1) {
                holder.battery_view.setBatteryLevel(device.getBatteryPercentage());
                holder.version_tv.setText(device.getVersionName());
                holder.battery_view.setVisibility(View.VISIBLE);
                holder.version_tv.setVisibility(View.VISIBLE);
                if (sensorMap.containsKey(deviceName)) {
                    FallCountModel model = sensorMap.get(deviceName);
                    if (!isVersionMatch(model, device)) {
                        holder.update_btn.setVisibility(View.VISIBLE);
                        holder.update_btn.setOnClickListener(view -> {
                            Dfu_OTA_Dialog.newInstance(device, model.getFirmware_info().getFirmware_path())
                                    .show(activity.getSupportFragmentManager()
                                            .beginTransaction(), device.getName());
                        });
                    }
                }
            } else {
                holder.battery_view.setVisibility(View.GONE);
                holder.version_tv.setVisibility(View.GONE);
            }
        } else {
            Log.d(TAG, "isConnected:false");
//            makeBackGroundGatt(device, holder);
//            device.setConnected(true);
        }

    }

    public boolean isVersionMatch(FallCountModel model, DiscoveredBluetoothDevice device) {
        Log.d(TAG, "isVersionMatch: ");
        if (!device.getVersionName().equals("")
                && model.getFirmware_info().getFirmware_version() != null
                && !model.getFirmware_info().getFirmware_version().equals("")) {
            double deviceVersion = Double.parseDouble(device.getVersionName().replaceAll("[^\\d.]", ""));
            double serverVersion = Double.parseDouble(model.getFirmware_info().getFirmware_version().replaceAll("[^\\d.]", ""));
            if (deviceVersion == serverVersion) {
                return true;
            }
        }
        return false;
    }

    private String getThresholdString(String thresholds_data) {
        String[] sp_data = thresholds_data.split(",");
        String finalString = "";
        for (int i = 0; i < sp_data.length - 1; i++) {
            sp_data[i].split(":");
            String[] threshold_value = sp_data[i].split(":");
            if (!threshold_value[0].equalsIgnoreCase("WT")) {
                if (i == 0)
                    finalString = getThresholdName(threshold_value[0]) + " = " + threshold_value[1] + "\n\n";
                else
                    finalString = finalString + getThresholdName(threshold_value[0]) + " = " + threshold_value[1] + "\n\n";
            }
        }
        return finalString;
    }

    HashMap<String, FallCountModel> sensorMap;


    @Override
    public long getItemId(final int position) {
        return devices.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return devices != null ? devices.size() : 0;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @SuppressLint({"HandlerLeak", "StaticFieldLeak"})
    public void makeBackGroundGatt(DiscoveredBluetoothDevice device, ViewHolder holder) {
        Log.d(TAG, "makeBackGroundGatt ="+device);
        tasks.add(new AsyncTask<Void, String, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d(TAG, "makeBackGroundGatt BG");
                OnDeviceConnect onDeviceConnect = new OnDeviceConnect() {
                    @Override
                    public void onAssetFetched(FallCountModel fallCountModel) {
                        Log.e("onAssetFetched adapter", "is = " + fallCountModel);
                        if (fallCountModel != null) {
                            sensorMap.put(device.getName(), fallCountModel);
                            device.setAssetImage(fallCountModel.getAsset_image());
                            device.setAssetName(fallCountModel.getAsset_code());
                            device.setFirmwareType(fallCountModel.getAsset_type());
                            device.setFirmwareInfo(fallCountModel.getFirmware_info());
                            holder.asset_name.setText(fallCountModel.getAsset_code());
                            AppUtils.load_image(fallCountModel.getAsset_image(), holder.asset_img);
                            int total_count = 0;
                            for (FallCountModel.DataCount dataCount : fallCountModel.getCount_data()) {
                                if (dataCount.getVibration_type().equals("A") && !dataCount.getCount().equals("")) {
                                    device.setFall_count(Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("T") && !dataCount.getCount().equals("")) {
                                    device.setLock_count(Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("F") && !dataCount.getCount().equals("")) {
                                    device.setFree_fall_count(Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("X") && !dataCount.getCount().equals("")) {
                                    device.setTh1_count(Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("Y") && !dataCount.getCount().equals("")) {
                                    device.setTh2_count(Integer.parseInt(dataCount.getCount()));
                                } else if (dataCount.getVibration_type().equals("Z") && !dataCount.getCount().equals("")) {
                                    device.setTh3_count(Integer.parseInt(dataCount.getCount()));
                                }
                                if (dataCount.getVibration_type().equals("cl") && !dataCount.getCount().equals("")) {
                                    total_count = total_count + Integer.parseInt(dataCount.getCount());
                                } else if (dataCount.getVibration_type().equals("acl") && !dataCount.getCount().equals("")) {
                                    total_count = total_count + Integer.parseInt(dataCount.getCount());
                                }
                            }
                            device.setTotalRotationCount(total_count);

                        }
                    }

                    @Override
                    public void onBatteryFetched(int percentage) {
                        Log.e("onBatteryFetched ", " is : " + percentage);
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                device.setBatteryPercentage(percentage);
                                holder.battery_view.setBatteryLevel(percentage);
                                holder.battery_view.setVisibility(View.VISIBLE);
                            }
                        });
                    }

                    @Override
                    public void onVersionFetched(String versionName) {
                        Log.e("onVersionFetched ", " is : " + versionName);
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                device.setVersionName(versionName);
                                holder.version_tv.setText("" + versionName);
                                holder.version_tv.setVisibility(View.VISIBLE);
                                notifyItemChanged(device.getListPosition());
                            }
                        });
                    }

                    @Override
                    public void notifyData(Object data) {

                    }

                    @Override
                    public void onStateChange(String state) {
                        Log.e(TAG, "onDeviceBoot "+" is : " + state);
                        if (state.equals("booting")) {
                            device.setDeviceMode(state);
                        } else {
                            device.setDeviceMode("");
                        }
                        device.setConnected(true);
                        activity.runOnUiThread(new Runnable() {
                            public void run() {
                                notifyItemChanged(device.getListPosition());
                            }
                        });
                    }
                };
                GattResultReceiver resultReceiver = new GattResultReceiver(new Handler(activity.getMainLooper()), device, onDeviceConnect);
                service.putExtra(EXTRA_DEVICE, device);
                service.putExtra("resultReceiver", resultReceiver);
                service.putExtra("needReconnect", false);
                activity.startService(service);
                return null;
            }
        }.execute());
    }

    private AlertDialog dialog;

    private void create_Historydialog(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
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
        make_childview(linear_list);
        builder.setView(dialoglayout);
        // create and show the alert dialog
        dialog = builder.create();
        dialog.show();
    }

    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    ArrayList<String> used_data = new ArrayList<>(Arrays.asList("Test used 1   20-08-2021 10:32 PM", "Test used 2   20-08-2021 10:32 PM", "Test used 3   20-08-2021 10:32 PM",
            "Test used 4   20-08-2021 10:32 PM", "Test used 5   20-08-2021 10:32 PM", "Test used 6   20-08-2021 10:32 PM", "Test used 7   20-08-2021 10:32 PM",
            "Test used 8   20-08-2021 10:32 PM", "Test used 9   20-08-2021 10:32 PM", "Test used 10   20-08-2021 10:32 PM"));

    private void make_childview(LinearLayout child_layout) {
        child_layout.removeAllViews();
        params1.setMargins(10, 5, 10, 5);
        for (int i = 0; i < used_data.size(); i++) {
            TextView check_tv = get_text_view(activity);
            check_tv.setTextColor(AppUtils.getResColor(R.color.app_text));
            check_tv.setText(used_data.get(i));
            child_layout.addView(check_tv);
        }
        flip_in(child_layout);
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
        chk_tv.setVisibility(View.GONE);
        return chk_tv;
    }


    private int i = 0;

    //    int j = 0;
    private void flip_in(final LinearLayout layout) {
        View view = layout.getChildAt(i);
        Animation bounce = AnimationUtils.loadAnimation(activity, R.anim.bounce);
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

    private String getThresholdName(String threshold_name) {
        switch (threshold_name) {
            case "FA":
                return "Threshold 1";
            case "T":
                return "Threshold 2";
            case "FF":
                return "Threshold 3";
            case "TH1":
                return "Threshold 4";
            case "TH2":
                return "Threshold 5";
            case "TH3":
                return "Threshold 6";
            default:
                return "";
        }
    }


    final class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.asset_name)
        TextView asset_name;
        @BindView(R.id.device_name)
        TextView deviceName;
        @BindView(R.id.asset_img)
        ImageView asset_img;

        @BindView(R.id.address_tv)
        TextView address_tv;
        @BindView(R.id.conct_tv)
        TextView conct_tv;

        @BindView(R.id.battery_view)
        BatteryView battery_view;


        @BindView(R.id.details_btn)
        MaterialButton details_btn;

        @BindView(R.id.status_btn)
        MaterialButton status_btn;

        @BindView(R.id.view_btn)
        MaterialButton view_btn;

        @BindView(R.id.connect_btn)
        MaterialButton connect_btn;

        @BindView(R.id.view_history)
        MaterialButton view_history;

        @BindView(R.id.update_btn)
        LinearLayout update_btn;

        @BindView(R.id.version_tv)
        TextView version_tv;
        @BindView(R.id.dv_mode_tv)
        TextView dv_mode_tv;


        private ViewHolder(@NonNull final View view) {
            super(view);
            ButterKnife.bind(this, view);
            view.findViewById(R.id.device_container).setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(devices.get(getAdapterPosition()));
                }
            });

        }

    }

}
