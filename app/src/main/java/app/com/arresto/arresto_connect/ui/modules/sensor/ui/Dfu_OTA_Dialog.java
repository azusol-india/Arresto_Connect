package app.com.arresto.arresto_connect.ui.modules.sensor.ui;

import static android.app.Activity.RESULT_OK;
import static android.bluetooth.BluetoothDevice.TRANSPORT_LE;
import static android.content.Context.ACTIVITY_SERVICE;
import static app.com.arresto.arresto_connect.constants.AppUtils.show_snak;
import static app.com.arresto.arresto_connect.ui.modules.sensor.server.SensorConstants.EXTRA_DEVICE;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.material.textview.MaterialTextView;

import java.io.File;
import java.util.UUID;

import app.com.arresto.arresto_connect.BuildConfig;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.custom_views.BatteryView;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;
import app.com.arresto.arresto_connect.ui.modules.sensor.DiscoveredBluetoothDevice;
import app.com.arresto.arresto_connect.ui.modules.sensor.server.DfuService;
import butterknife.BindView;
import butterknife.ButterKnife;
import no.nordicsemi.android.dfu.DfuProgressListener;
import no.nordicsemi.android.dfu.DfuProgressListenerAdapter;
import no.nordicsemi.android.dfu.DfuServiceController;
import no.nordicsemi.android.dfu.DfuServiceInitiator;
import no.nordicsemi.android.dfu.DfuServiceListenerHelper;

public class Dfu_OTA_Dialog extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.device_tv)
    MaterialTextView device_tv;
    @BindView(R.id.battery_view)
    BatteryView battery_view;
    @BindView(R.id.progressbar_file)
    ProgressBar mProgressBar;
    @BindView(R.id.textviewProgress)
    TextView mTextPercentage;
    @BindView(R.id.textviewUploading)
    TextView mTextUploading;
    @BindView(R.id.close_btn)
    ImageView close_btn;
    BaseActivity baseActivity;
    String zipPath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.theme_dialog_wrap);
    }

    public static Dfu_OTA_Dialog newInstance(DiscoveredBluetoothDevice device, String mPath) {
        Dfu_OTA_Dialog dfu_ota_dialog = new Dfu_OTA_Dialog();
        Bundle args = new Bundle();
        args.putParcelable(EXTRA_DEVICE, device);
        args.putString("path", mPath);
        dfu_ota_dialog.setArguments(args);
        return dfu_ota_dialog;
    }

    BluetoothDevice device;
    UploadCancelFragment.CancelFragmentListener cancelListener;
    DiscoveredBluetoothDevice dv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.dfu_update_dialoge, container, false);
        baseActivity = (BaseActivity) getActivity();
        ButterKnife.bind(this, view);
        if (getArguments() != null) {
            dv = getArguments().getParcelable(EXTRA_DEVICE);
           String fileUrl = getArguments().getString("path");
            final String deviceName = dv.getName();
            final String deviceAddress = dv.getAddress();
            device_tv.setText(deviceName + "\n" + deviceAddress);
            device_tv.setVisibility(View.VISIBLE);
            device = dv.getDevice();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                DfuServiceInitiator.createDfuNotificationChannel(baseActivity);
            }
            if (fileUrl != null && !fileUrl.equals(""))
                download_DfuZipFile(fileUrl);
            else
                openFileChooser();
            close_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showUploadCancelDialog();
                }
            });
            cancelListener = new UploadCancelFragment.CancelFragmentListener() {
                @Override
                public void onCancelUpload() {
                    if (controller != null)
                        controller.abort();
                }
            };
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode != RESULT_OK) {
            Dfu_OTA_Dialog.this.dismiss();
            return;
        }
        switch (requestCode) {
            case SELECT_FILE_REQ: {
                zipPath = null;
                mFileStreamUri = null;

                final Uri uri = data.getData();
                /*
                 * The URI returned from application may be in 'file' or 'content' schema. 'File' schema allows us to create a File object and read details from if
                 * directly. Data from 'Content' schema must be read by Content Provider. To do that we are using a Loader.
                 */

                if (uri.getScheme().equals("file")) {
                    // the direct path to the file has been returned
                    final String path = uri.getPath();
                    final File file = new File(path);
                    zipPath = path;
//                    updateFileInfo(file.getName(), file.length(), mFileType);
                } else if (uri.getScheme().equals("content")) {
                    // an Uri has been returned
                    mFileStreamUri = uri;
                    // if application returned Uri for streaming, let's us it. Does it works?
                    // FIXME both Uris works with Google Drive app. Why both? What's the difference? How about other apps like DropBox?
                    final Bundle extras = data.getExtras();
                    if (extras != null && extras.containsKey(Intent.EXTRA_STREAM))
                        mFileStreamUri = extras.getParcelable(Intent.EXTRA_STREAM);

                    // file name and size must be obtained from Content Provider
                    final Bundle bundle = new Bundle();
                    bundle.putParcelable(EXTRA_URI, uri);
                    getLoaderManager().restartLoader(SELECT_FILE_REQ, bundle, Dfu_OTA_Dialog.this);
                }
                break;
            }
            default:
                break;
        }
    }
    @SuppressLint("HandlerLeak")
    public void download_DfuZipFile(String url) {
        mTextUploading.setText("Downloading DFU File...");
        new NetworkRequest(baseActivity).download_file(url, "dfuFile.zip", new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.e("return obj", "" + msg.obj);
                if (msg.obj != null && msg.obj.equals("true")) {
                    zipPath=Static_values.directory +  "dfuFile.zip";
                    startUpload(device);
                }
            }
        });
    }

    DfuServiceController controller;

    public void startUpload(BluetoothDevice mSelectedDevice) {
        if (isDfuServiceRunning()) {
            showUploadCancelDialog();
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mTextPercentage.setVisibility(View.VISIBLE);
        mTextUploading.setText(R.string.dfu_status_uploading);
        mTextUploading.setVisibility(View.VISIBLE);
        final DfuServiceInitiator starter = new DfuServiceInitiator(mSelectedDevice.getAddress())
                .setDeviceName(mSelectedDevice.getName())
                .setKeepBond(false);
        starter.setUnsafeExperimentalButtonlessServiceInSecureDfuEnabled(true);
        starter.setZip(mFileStreamUri, zipPath);
        DfuServiceListenerHelper.registerProgressListener(baseActivity, dfuProgressListener);
        controller = starter.start(baseActivity, DfuService.class);
    }

    private void showUploadCancelDialog() {
        final LocalBroadcastManager manager = LocalBroadcastManager.getInstance(baseActivity);
        final Intent pauseAction = new Intent(DfuService.BROADCAST_ACTION);
        pauseAction.putExtra(DfuService.EXTRA_ACTION, DfuService.ACTION_PAUSE);
        manager.sendBroadcast(pauseAction);
        final UploadCancelFragment fragment = UploadCancelFragment.getInstance();
        fragment.setCancelListner(cancelListener);
        fragment.show(baseActivity.getSupportFragmentManager(), "Upload cancel");
    }

    private boolean isDfuServiceRunning() {
        ActivityManager manager = (ActivityManager) baseActivity.getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (DfuService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        DfuServiceListenerHelper.registerProgressListener(baseActivity, dfuProgressListener);
    }

    @Override
    public void onPause() {
        super.onPause();

        DfuServiceListenerHelper.unregisterProgressListener(baseActivity, dfuProgressListener);
    }

    private final DfuProgressListener dfuProgressListener = new DfuProgressListenerAdapter() {
        @Override
        public void onDeviceConnecting(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_connecting);
        }

        @Override
        public void onDfuProcessStarting(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_starting);
        }

        @Override
        public void onEnablingDfuMode(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_switching_to_dfu);
        }

        @Override
        public void onFirmwareValidating(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_validating);
        }

        @Override
        public void onDeviceDisconnecting(final String deviceAddress) {
            mProgressBar.setIndeterminate(true);
            mTextPercentage.setText(R.string.dfu_status_disconnecting);
        }

        @Override
        public void onDfuCompleted(final String deviceAddress) {
            mTextPercentage.setText(R.string.dfu_status_completed);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            Dfu_OTA_Dialog.this.dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    show_snak(baseActivity, "Uploading Complete. Device updated and rebooting.");
                    rebootDevice();
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) baseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onDfuAborted(final String deviceAddress) {
            mTextPercentage.setText(R.string.dfu_status_aborted);
            // let's wait a bit until we cancel the notification. When canceled immediately it will be recreated by service again.
            Dfu_OTA_Dialog.this.dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    show_snak(baseActivity, "Uploading aborted");
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) baseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }

        @Override
        public void onProgressChanged(final String deviceAddress, final int percent, final float speed, final float avgSpeed, final int currentPart, final int partsTotal) {
            mProgressBar.setIndeterminate(false);
            mProgressBar.setProgress(percent);
            mTextPercentage.setText(getString(R.string.dfu_uploading_percentage, percent));
            if (partsTotal > 1)
                mTextUploading.setText(getString(R.string.dfu_status_uploading_part, currentPart, partsTotal));
            else
                mTextUploading.setText(R.string.dfu_status_uploading);
        }

        @Override
        public void onError(final String deviceAddress, final int error, final int errorType, final String message) {
            Dfu_OTA_Dialog.this.dismiss();
            baseActivity.printLog(message);
            show_snak(baseActivity, message);
            // We have to wait a bit before canceling notification. This is called before DfuService creates the last notification.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // if this activity is still open and upload process was completed, cancel the notification
                    final NotificationManager manager = (NotificationManager) baseActivity.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.cancel(DfuService.NOTIFICATION_ID);
                }
            }, 200);
        }
    };

    private static final String EXTRA_URI = "uri";

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        final Uri uri = bundle.getParcelable(EXTRA_URI);
        return new CursorLoader(baseActivity, uri, null /* all columns, instead of projection */, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToNext()) {
            String filePath = null;
            final int dataIndex = data.getColumnIndex(MediaStore.MediaColumns.DATA);
            if (dataIndex != -1)
                filePath = data.getString(dataIndex /* 2 DATA */);
            if (!TextUtils.isEmpty(filePath))
                zipPath = filePath;
            startUpload(device);
        } else {
            zipPath = null;
            zipPath = null;
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        zipPath = null;
        zipPath = null;
    }


    public int mFileType = 0;
    public static final int SELECT_FILE_REQ = 1;
    public static final int SELECT_INIT_FILE_REQ = 2;
    public Uri mFileStreamUri;
    public String mInitFilePath;
    public Uri mInitFileStreamUri;

    public void openFileChooser() {
        mFileType = DfuService.TYPE_AUTO;
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(mFileType == DfuService.TYPE_AUTO ? DfuService.MIME_TYPE_ZIP : DfuService.MIME_TYPE_OCTET_STREAM);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        if (intent.resolveActivity(baseActivity.getPackageManager()) != null) {
            // file browser has been found on the device
            startActivityForResult(intent, SELECT_FILE_REQ);
        }
    }

    BluetoothGatt mBluetoothGatt;

    public void rebootDevice() {
        mTextPercentage.setText("Rebooting Device...");
        if (device == null) {
            Log.e("TAG", "Device not found.  Unable to connect.");
            return;
        }
        device.createBond();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            mBluetoothGatt = device.connectGatt(baseActivity, false, mGattCallback, TRANSPORT_LE);
        } else {
            mBluetoothGatt = device.connectGatt(baseActivity, false, mGattCallback);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBluetoothGatt.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH);
        }
    }


    BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                showMessage("Connected to GATT server.");
                // Attempts to discover services after successful connection.
                try {
                    Thread.sleep(600);
                    showMessage("Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    showMessage("Thread.sleep error :" + e.getMessage());
                }
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                showMessage("Disconnected from GATT server.");
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                showMessage("mBluetoothGatt = " + mBluetoothGatt);
                writeRebootService();
            } else {
                showMessage("onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            showMessage(" onCharacteristicWrite " + status + "\n" + characteristic.getUuid().toString());
            baseActivity.onBackPressed();
        }
    };


    public void writeRebootService() {
        BluetoothGattService callSer = mBluetoothGatt.getService(UUID.fromString("36870000-16e1-4fc2-be6b-c7e3f7288527"));
        if (callSer == null) {
            showMessage("Reboot service not found! ");
            return;
        }
        BluetoothGattCharacteristic TxChar = callSer.getCharacteristic(UUID.fromString("36870006-16e1-4fc2-be6b-c7e3f7288527"));
        if (TxChar == null) {
            showMessage("Reboot Characteristic service not found! ");
            return;
        }
        byte[] value = new byte[1];
        value[0] = (byte) (01 & 0xFF);
        TxChar.setValue(value);
        mBluetoothGatt.writeCharacteristic(TxChar);
        showMessage(" writeRebootService run");
    }

    private void showMessage(String msg) {
        if (BuildConfig.DEBUG)
            Log.e("TAG", msg);
    }

}
