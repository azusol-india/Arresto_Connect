package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.constants.AppUtils;
import app.com.arresto.arresto_connect.constants.LoadFragment;
import app.com.arresto.arresto_connect.custom_views.DialogSpinner;
import app.com.arresto.arresto_connect.data.models.SensorModel;
import app.com.arresto.arresto_connect.data.models.Site_Model;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.sensor.ScannerStateLiveData;
import app.com.arresto.arresto_connect.ui.modules.sensor.ScannerViewModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;
import static app.com.arresto.arresto_connect.constants.AppUtils.isNetworkAvailable;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.constants.Static_values.user_id;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.DEVICE;

public class Add_SensorFragment extends Base_Fragment {
    View view;
    @BindView(R.id.calibration_spinr)
    DialogSpinner calibration_spinr;

    @BindView(R.id.asset_lbl_edt)
    EditText asset_lbl_edt;
    @BindView(R.id.sensor_lbl_edt)
    EditText sensor_lbl_edt;
    @BindView(R.id.alias_edt)
    EditText alias_edt;

    @BindView(R.id.asset_lbl_btn)
    MaterialButton asset_lbl_btn;
    @BindView(R.id.sensor_lbl_btn)
    MaterialButton sensor_lbl_btn;

    @BindView(R.id.start_btn)
    MaterialButton start_btn;

    @BindView(R.id.find_btn)
    MaterialButton find_btn;

    //    @BindView(R.id.no_location)
//    View noLocationView;
    @BindView(R.id.bluetooth_off)
    View noBluetoothView;
    @BindView(R.id.add_view)
    View add_view;

    String uin = "", master_id = "";
    ScannerViewModel scannerViewModel;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.add_sensorfragment, parent, false);
            ButterKnife.bind(this, view);
            change_spiner_array(calibration_spinr, getResources().getStringArray(R.array.calibration_array));
            asset_lbl_btn.setOnClickListener(clickListener);
            sensor_lbl_btn.setOnClickListener(clickListener);
            find_btn.setOnClickListener(clickListener);
            start_btn.setOnClickListener(clickListener);
            ScannerViewModel scannerViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(AppController.getInstance()).create(ScannerViewModel.class);
            scannerViewModel.getScannerState().observe(this, this::checkPermission);
            calibration_spinr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    checkThreshold(adapterView.getSelectedItem().toString());
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        }
        return view;
    }

    String update_id = "";

    private void checkThreshold(String threshold) {
        alias_edt.setText("");
        start_btn.setText("Set Calibration");
        if (sensorModel != null && sensorModel.getThresholds() != null) {
            for (SensorModel.ThresholdsData item : sensorModel.getThresholds()) {
                if (item.getCalibration_value().equals(threshold)) {
                    alias_edt.setText(item.getModel_alias());
                    start_btn.setText("Update Calibration");
                    break;
                }
            }
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.asset_lbl_btn:
                    uin = "";
                    master_id = "";
                    asset_lbl_edt.setText("");
                    scan_barcode(new BarcodeListener() {
                        @Override
                        public void onScanned(String scaned_text) {
                            if (!scaned_text.equals("")) {
                                if (scaned_text.contains("https://arresto.in/")) {
                                    scaned_text = Uri.parse(scaned_text).getQueryParameter("u");
                                    findAsset(scaned_text, "UIN");
                                } else
                                    findAsset(scaned_text, "barcode");

                            } else {
                                show_snak(baseActivity, "Please try again!");
                            }
                        }
                    });
                    break;

                case R.id.sensor_lbl_btn:
                    ((ViewGroup) start_btn.getParent()).setVisibility(View.GONE);
                    find_btn.setVisibility(View.VISIBLE);
                    scan_barcode(new BarcodeListener() {
                        @Override
                        public void onScanned(String scaned_text) {
                            device = null;
                            deviceFound = false;
                            sensor_lbl_edt.setText(scaned_text);
                        }
                    });
                    break;


                case R.id.find_btn:
                    device = null;
                    deviceFound = false;
                    if (sensor_lbl_edt.getText().toString().equals("")) {
                        show_snak(baseActivity, "Please enter or scan device label!");
                    } else {
                        baseActivity.showDialog();
                        scan_CalibrationDevice();
                    }
                    break;
                case R.id.start_btn:
                    if (asset_lbl_edt.getText().toString().equals("")) {
                        show_snak(baseActivity, "Please enter or scan Asset label!");
                    } else if (uin.equals("")) {
                        show_snak(baseActivity, "Please scan a correct Asset label!");
                    } else if (calibration_spinr.getSelectedItemPosition() < 1) {
                        show_snak(baseActivity, "Please select Calibration Name");
                    } else {
                        CalibrationFragment viewFragment = new CalibrationFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("calibration_name", calibration_spinr.getSelectedItem().toString());
                        bundle.putString("asset_code", asset_lbl_edt.getText().toString());
                        bundle.putString("alias_name", alias_edt.getText().toString());
                        bundle.putString("uin", uin);
                        bundle.putString("master_id", master_id);
                        bundle.putString("update_id", update_id);
                        bundle.putString("alias_name", alias_edt.getText().toString());
                        bundle.putParcelable(DEVICE, device);
                        viewFragment.setArguments(bundle);
                        LoadFragment.replace(viewFragment, baseActivity, device.getName());
                    }
                    break;
            }
        }
    };

    private void findAsset(String scaned_text, String input_type) {
        if (isNetworkAvailable(getActivity())) {
            String url = All_Api.search_Data + input_type.toLowerCase() + "=" + scaned_text + "&client_id=" + client_id + "&user_id=" + user_id + "&geo_location=" + baseActivity.curr_lat + "," + baseActivity.curr_lng;
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
                                    Site_Model siteModel = site_models.get(0);
                                    asset_lbl_edt.setText(siteModel.getMdata_item_series());
                                    findAssetThreshold(siteModel.getMdata_item_series());
                                    uin = siteModel.getMdata_uin();
                                    master_id = siteModel.getMaster_id();
                                }
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

    SensorModel sensorModel;

    private void findAssetThreshold(String asset) {
        update_id = "";
        String url = All_Api.calibration_threshold_api + "client_id=" + client_id + "&asset_code=" + asset;
        url = url.replace(" ", "%20");
        NetworkRequest networkRequest = new NetworkRequest();
        networkRequest.make_get_request(url, new NetworkRequest.VolleyResponseListener() {
            @Override
            public void onResponse(String response) {
                Log.e("response", "" + response);
                Object json;
                try {
                    json = new JSONTokener(response).nextValue();
                    if (json instanceof JSONObject) {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("status_code").equals("200")) {
                            sensorModel = AppUtils.getGson().fromJson(jsonObject.getString("data"), SensorModel.class);
                            update_id = sensorModel.getSensor().getId();
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


    BluetoothAdapter.LeScanCallback mLeScanCallback;
    boolean deviceFound;
    BluetoothDevice device;

    private void scan_CalibrationDevice() {
        ArrayList<BluetoothDevice> devices = new ArrayList<>();
        final BluetoothManager bluetoothManager = (BluetoothManager) baseActivity.getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter mBluetoothAdapter = bluetoothManager.getAdapter();
        mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(final BluetoothDevice founded_device, int rssi, byte[] scanRecord) {
                baseActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (founded_device != null && founded_device.getName() != null && founded_device.getName().equalsIgnoreCase(sensor_lbl_edt.getText().toString()) && !deviceFound) {
                            baseActivity.hideDialog();
                            mBluetoothAdapter.stopLeScan(mLeScanCallback);
                            mBluetoothAdapter.cancelDiscovery();
                            deviceFound = true;
                            device = founded_device;
                            changeLayout();
//                                    connect(device);
                        }

                    }
                });
            }
        };
        mBluetoothAdapter.startLeScan(mLeScanCallback);
    }

    public void changeLayout() {
        ((ViewGroup) start_btn.getParent()).setVisibility(View.VISIBLE);
        find_btn.setVisibility(View.GONE);
    }

    private void change_spiner_array(DialogSpinner spnr, String[] array) {
        int pos = spnr.getSelectedItemPosition();
        List<String> strings = new ArrayList<>(Arrays.asList(array));
        if (!strings.contains(getResString("lbl_pl_slct_msg")))
            strings.add(0, getResString("lbl_pl_slct_msg"));
        spnr.setItems(strings, "");
        spnr.setSelection(pos);
    }


    @BindView(R.id.no_location_permission)
    View noLocationPermissionView;
    @BindView(R.id.action_grant_location_permission)
    Button grantPermissionButton;
    @BindView(R.id.action_permission_settings)
    Button permissionSettingsButton;


    private void checkPermission(final ScannerStateLiveData state) {
        // First, check the Location permission. This is required on Marshmallow onwards in order
        // to scan for Bluetooth LE devices.
        if (Utils.isLocationPermissionsGranted(baseActivity)) {
            noLocationPermissionView.setVisibility(View.GONE);
            // Bluetooth must be enabled.
            if (state.isBluetoothEnabled()) {
                noBluetoothView.setVisibility(View.GONE);
                add_view.setVisibility(View.VISIBLE);
            } else {
                noBluetoothView.setVisibility(View.VISIBLE);
                add_view.setVisibility(View.GONE);
            }
        } else {
            noLocationPermissionView.setVisibility(View.VISIBLE);
            noBluetoothView.setVisibility(View.GONE);
            add_view.setVisibility(View.GONE);
            final boolean deniedForever = Utils.isLocationPermissionDeniedForever(baseActivity);
            grantPermissionButton.setVisibility(deniedForever ? View.GONE : View.VISIBLE);
            permissionSettingsButton.setVisibility(deniedForever ? View.VISIBLE : View.GONE);
        }
    }


    private static final int REQUEST_ACCESS_FINE_LOCATION = 1022;

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            scannerViewModel.refresh();
        }
    }

    @OnClick(R.id.action_enable_bluetooth)
    public void onEnableBluetoothClicked() {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableIntent);
    }

    @OnClick(R.id.action_grant_location_permission)
    public void onGrantLocationPermissionClicked() {
        Utils.markLocationPermissionRequested(baseActivity);
        ActivityCompat.requestPermissions(
                baseActivity,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_ACCESS_FINE_LOCATION);
    }

    @OnClick(R.id.action_permission_settings)
    public void onPermissionSettingsClicked() {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", baseActivity.getPackageName(), null));
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
