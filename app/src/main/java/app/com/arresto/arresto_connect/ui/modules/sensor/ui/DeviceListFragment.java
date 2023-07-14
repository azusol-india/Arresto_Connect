package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import static app.com.arresto.arresto_connect.constants.AppUtils.getResString;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.appcontroller.AppController;
import app.com.arresto.arresto_connect.ui.fragments.Base_Fragment;
import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;
import app.com.arresto.arresto_connect.ui.modules.sensor.ScannerStateLiveData;
import app.com.arresto.arresto_connect.ui.modules.sensor.ScannerViewModel;
import app.com.arresto.arresto_connect.ui.modules.sensor.Utils;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.TempGattService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DeviceListFragment extends Base_Fragment implements DevicesAdapter.OnItemClickListener {
    View view;
    @BindView(R.id.state_scanning)
    View scanningView;
    @BindView(R.id.no_devices)
    View emptyView;
    @BindView(R.id.no_location_permission)
    View noLocationPermissionView;
    @BindView(R.id.action_grant_location_permission)
    Button grantPermissionButton;
    @BindView(R.id.action_permission_settings)
    Button permissionSettingsButton;
    @BindView(R.id.no_location)
    View noLocationView;
    @BindView(R.id.bluetooth_off)
    View noBluetoothView;
    @BindView(R.id.recycler_view_ble_devices)
    RecyclerView recyclerView;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;

    ScannerViewModel scannerViewModel;
    boolean isGyro=false;
    //    TempGattService mService;
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1022; // random number

//    private void service_init() {
//        baseActivity.startService(new Intent(baseActivity, TempGattService.class));
//    }

    DevicesAdapter adapter;

    @Override
    public View FragmentView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.sensor_device_list, parent, false);
            ButterKnife.bind(this, view);
            startService();
            scannerViewModel = ViewModelProvider.AndroidViewModelFactory.getInstance(AppController.getInstance()).create(ScannerViewModel.class);
//            scannerViewModel = new ViewModelProvider(baseActivity,ScannerViewModel.class);
//                    .get(ScannerViewModel.class);
            scannerViewModel.getScannerState().observe(this, this::startScan);
            scannerViewModel.filterByName(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(baseActivity));
            recyclerView.addItemDecoration(new DividerItemDecoration(baseActivity, DividerItemDecoration.VERTICAL));
            final RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
            if (animator instanceof SimpleItemAnimator) {
                ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
            }
            if(this.getTag().equals(getResString("lbl_gyroscope_accelerometer")))
                isGyro=true;
            adapter = new DevicesAdapter(baseActivity, service,isGyro, scannerViewModel.getDevices());
            adapter.setOnItemClickListener(this);
            recyclerView.setAdapter(adapter);

            swipe.setColorSchemeResources(R.color.app_text, R.color.app_btn_bg, R.color.app_green);
            swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    swipe.setRefreshing(false);
                }
            });
        }
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        scannerViewModel.startScan();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScan();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScan();
        baseActivity.unregisterReceiver(br);
    }

    @Override
    public void onStart() {
        super.onStart();
        baseActivity.registerReceiver((br), new IntentFilter("serviceDevices"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.cancelAllTask();
        }
        stopService();
    }

    @Override
    public void onItemClick(@NonNull final DiscoveredBluetoothDevice device) {
//        DataViewFragment viewFragment = new DataViewFragment();
//        Bundle bundle = new Bundle();
//        bundle.putParcelable(EXTRA_DEVICE, device);
//        viewFragment.setArguments(bundle);
//        LoadFragment.replace(viewFragment, baseActivity, device.getName());
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_ACCESS_FINE_LOCATION) {
            scannerViewModel.refresh();
        }
    }

    @OnClick(R.id.action_enable_location)
    public void onEnableLocationClicked() {
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
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
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                REQUEST_ACCESS_FINE_LOCATION);
    }

    @OnClick(R.id.action_permission_settings)
    public void onPermissionSettingsClicked() {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", baseActivity.getPackageName(), null));
        startActivity(intent);
    }


    private void startScan(final ScannerStateLiveData state) {
        // First, check the Location permission. This is required on Marshmallow onwards in order
        // to scan for Bluetooth LE devices.
        if (Utils.isLocationPermissionsGranted(baseActivity)) {
            noLocationPermissionView.setVisibility(View.GONE);
            // Bluetooth must be enabled.
            if (state.isBluetoothEnabled()) {
                noBluetoothView.setVisibility(View.GONE);
                scannerViewModel.startScan();
                scanningView.setVisibility(View.VISIBLE);
                if (!state.hasRecords()
                        && (scannerViewModel.getDevices().getDevices() == null
                        || scannerViewModel.getDevices().getDevices().size() < 1)) {
                    emptyView.setVisibility(View.VISIBLE);
                    if (!Utils.isLocationRequired(baseActivity) || Utils.isLocationEnabled(baseActivity)) {
                        noLocationView.setVisibility(View.INVISIBLE);
                    } else {
                        noLocationView.setVisibility(View.VISIBLE);
                    }
                } else {
                    emptyView.setVisibility(View.GONE);
                }
            } else {
                noBluetoothView.setVisibility(View.VISIBLE);
                scanningView.setVisibility(View.INVISIBLE);
                emptyView.setVisibility(View.GONE);
                clear();
            }
        } else {
            noLocationPermissionView.setVisibility(View.VISIBLE);
            noBluetoothView.setVisibility(View.GONE);
            scanningView.setVisibility(View.INVISIBLE);
            emptyView.setVisibility(View.GONE);

            final boolean deniedForever = Utils.isLocationPermissionDeniedForever(baseActivity);
            grantPermissionButton.setVisibility(deniedForever ? View.GONE : View.VISIBLE);
            permissionSettingsButton.setVisibility(deniedForever ? View.VISIBLE : View.GONE);
        }
    }

    private void stopScan() {
        scannerViewModel.stopScan();
    }

    private void clear() {
        scannerViewModel.getDevices().clear();
        scannerViewModel.getScannerState().clearRecords();
    }

    BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent i) {
            printLog("old devices found intent== " + i);
            ArrayList<DiscoveredBluetoothDevice> allDevices = i.getExtras().getParcelableArrayList("devices");
            printLog("old devices found " + allDevices);
//            if (allDevices != null && allDevices.size() > 0) {
//                emptyView.setVisibility(View.GONE);
//                scannerViewModel.getDevices().addDevices(allDevices);
//            }
        }
    };

    Intent service;

    public void startService() {
        service = new Intent(baseActivity, TempGattService.class);
        service.putExtra("sendDevice", true);
        service.putExtra("needReconnect", false);
        baseActivity.startService(service);
    }

    public void stopService() {
        if (service != null) {
            Intent service = new Intent(baseActivity, TempGattService.class); //to stop service put null argument
            baseActivity.startService(service);
        }
    }

}