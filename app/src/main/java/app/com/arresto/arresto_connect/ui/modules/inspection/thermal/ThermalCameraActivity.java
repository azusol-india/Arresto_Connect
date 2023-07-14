package app.com.arresto.arresto_connect.ui.modules.inspection.thermal;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.flir.thermalsdk.androidsdk.ThermalSdkAndroid;
import com.flir.thermalsdk.androidsdk.live.connectivity.UsbPermissionHandler;
import com.flir.thermalsdk.live.CommunicationInterface;
import com.flir.thermalsdk.live.Identity;
import com.flir.thermalsdk.log.ThermalLog;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.com.arresto.arresto_connect.BuildConfig;
import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.third_party.flir_thermal.CameraHandler;
import app.com.arresto.arresto_connect.third_party.flir_thermal.StatusChangeListener;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;


public class ThermalCameraActivity extends BaseActivity implements StatusChangeListener {
    @Override
    protected int getLayoutResourceId() {
        return R.layout.thermal_camera_activity;
    }
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final String TAG = this.getClass().getSimpleName();
    private ImageView image;
    Handler handler = new Handler();
    Runnable connection_Runnable = new Runnable() {
        @Override
        public void run() {
            Log.e(TAG, "runConnectionTimer: now");
            executor.execute(cameraHandler::connect);
        }
    };


    public void runConnectionTimer() {
        handler.removeCallbacks(connection_Runnable);
        handler.postDelayed(connection_Runnable, 3000);
    }

    public void onFrameUpdated() {
        if (image != null) {
            runOnUiThread(() -> image.setImageBitmap(cameraHandler.getMostRecentBitmap()));
        }
    }

    public void onNewCameraFound(Identity identity) {
        Log.e(TAG, "Found device: " + identity);
        runConnectionTimer();
    }

    @Override
    public void onCameraConnect(Object status) {
        Log.e(TAG, "connect status: " + status);
        executor.execute(cameraHandler::startStreaming);
    }

    @Override
    public void onCapture(boolean isCapture) {
        if (isCapture) {
            Intent sendBackResult = new Intent();
            sendBackResult.putExtra("path", imagePath);
            setResult(RESULT_OK, sendBackResult);
            finish();
        }
    }

    public void onStatusUpdated(String text) {
        Log.e(TAG, "onStatusUpdated:" + text);
        if (text.equals("streaming") || text.equals("streamingError")) {
            hideDialog();
        }
    }

    public void showMessage(String message) {
        runOnUiThread(() ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show());
    }


    private CameraHandler cameraHandler;

    private final UsbPermissionHandler usbPermissionHandler = new UsbPermissionHandler();
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getExtras() != null) {
            imagePath = getIntent().getStringExtra("path") + getIntent().getStringExtra("name");
        }
        progressDialog.mBuilder.text("Connecting to camera...");
        showDialog();
        image = findViewById(R.id.image);
        ThermalLog.LogLevel enableLoggingInDebug = BuildConfig.DEBUG ? ThermalLog.LogLevel.DEBUG : ThermalLog.LogLevel.NONE;
        ThermalSdkAndroid.init(getApplicationContext(), enableLoggingInDebug);
        cameraHandler = new CameraHandler(this);
        executor.execute(cameraHandler::startDiscovering);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Always close the connection with a connected mobile phone when going into background
        executor.execute(() -> {
            cameraHandler.disconnect();
            cameraHandler.stopDiscovering();
        });
    }


    public void saveImage(View view) {
        cameraHandler.saveThermalimage(imagePath);
    }

    public void ensurePermissions(Identity identity) {
        if (identity.communicationInterface == CommunicationInterface.INTEGRATED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 100);
            }
        }
        if (UsbPermissionHandler.isFlirOne(identity)) {
            usbPermissionHandler.requestFlirOnePermisson(identity, this, new UsbPermissionHandler.UsbPermissionListener() {
                @Override
                public void permissionGranted(Identity identity) {
                }

                @Override
                public void permissionDenied(Identity identity) {
                    showMessage("Permission was denied for identity ");
                }

                @Override
                public void error(UsbPermissionHandler.UsbPermissionListener.ErrorType errorType, final Identity identity) {
                    showMessage("Error when asking for permission for mobile phone, error:" + errorType + " identity:" + identity);
                }
            });
        }
    }

}
