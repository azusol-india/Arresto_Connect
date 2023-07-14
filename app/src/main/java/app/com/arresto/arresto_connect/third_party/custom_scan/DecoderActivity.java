/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collection;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.third_party.custom_scan.camera.CameraManager;
import app.com.arresto.arresto_connect.third_party.custom_scan.result.ResultHandler;
import app.com.arresto.arresto_connect.third_party.custom_scan.result.ResultHandlerFactory;

public class DecoderActivity extends AppCompatActivity implements IDecoderActivity, Callback {
    private AutoTerminationTimer autoTerminationTimer;
    private boolean cameraInitialFlag;
    protected CameraManager cameraManager;
    protected String characterSet = null;
    protected Collection<BarcodeFormat> decodeFormats;
    protected DecoderActivityHandler handler;
    protected boolean hasSurface = false;
    private boolean inScanMode = false;

    private final int MESSAGE_DECODER_CAMERA_OPEN_FAIL = 88001;
    private final int MESSAGE_DECODER_CAMERA_OPEN_SUCCESS = 88000;
    @SuppressLint("HandlerLeak")
    private Handler uiHandler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case MESSAGE_DECODER_CAMERA_OPEN_SUCCESS:
                    LinearLayout linearLayout = findViewById(R.id.progressLayout);
                    if (linearLayout != null) {
                        linearLayout.setVisibility(View.GONE);
                    }
                    SurfaceHolder surfaceHolder = (SurfaceHolder) message.obj;
                    if (surfaceHolder == null) {
                        surfaceHolder = ((SurfaceView) findViewById(R.id.preview_view)).getHolder();
                    }
                    initCamera2(surfaceHolder);
                    break;
                case MESSAGE_DECODER_CAMERA_OPEN_FAIL:
                    LinearLayout linearLayout2 = findViewById(R.id.progressLayout);
                    if (linearLayout2 != null) {
                        linearLayout2.setVisibility(View.GONE);
                    }
                    cameraInitialFlag = false;

                    String string = "";
                    Builder builder = new Builder(DecoderActivity.this);
                    builder.setMessage(string);
                    builder.setCancelable(false);
                    builder.setPositiveButton("Close", new OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (!isFinishing()) {
                                finish();
                            }
                        }
                    });
                    if (!isFinishing()) {
                        builder.show();
                        break;
                    }

                    return;
            }
        }
    };
    protected ViewfinderView viewfinderView = null;

    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }


    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.decoder);
        handler = null;
        hasSurface = false;
        cameraInitialFlag = false;
        inScanMode = false;
        autoTerminationTimer = new AutoTerminationTimer(this);
        final ImageView flash_btn = findViewById(R.id.flash_btn);
        flash_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(cameraManager == null || cameraManager.getCamera() == null || !cameraManager.getFlashlightSupport())) {
                    Camera.Parameters parameters = cameraManager.getCamera().getParameters();
                    if (parameters.getFlashMode().equals("off")) {
                        parameters.setFlashMode("torch");
                        flash_btn.setImageResource(R.drawable.flashon);
                    } else {
                        flash_btn.setImageResource(R.drawable.flashoff);
                        parameters.setFlashMode("off");
                    }
                    cameraManager.getCamera().setParameters(parameters);
                }
            }
        });
    }

    public void onDestroy() {
        autoTerminationTimer.shutdown();
        super.onDestroy();
    }

    public void onResume() {
        super.onResume();
        autoTerminationTimer.onResume();
        inScanMode = false;
        if (cameraManager == null) {
            cameraManager = new CameraManager(getApplication());
        }
        SurfaceHolder holder = ((SurfaceView) findViewById(R.id.preview_view)).getHolder();
        if (hasSurface) {
            initCamera(holder);
        } else {
            holder.addCallback(this);
            holder.setType(3);
        }
        if (viewfinderView == null) {
            viewfinderView = findViewById(R.id.viewfinder_view);
            viewfinderView.setCameraManager(cameraManager);
        }
        showScanner();
        return;
    }

    public void onPause() {
        autoTerminationTimer.onPause();
        DecoderActivityHandler decoderActivityHandler = handler;
        if (decoderActivityHandler != null) {
            decoderActivityHandler.quitSynchronously();
            handler = null;
        }
        CameraManager cameraManager = this.cameraManager;
        if (cameraManager != null) {
            cameraManager.closeDriver();
        }
        super.onPause();
    }

    public boolean getScanMode() {
        return inScanMode;
    }

    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (!hasSurface) {
            hasSurface = true;
            initCamera(surfaceHolder);
        }
    }

    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        hasSurface = false;
        CameraManager cameraManager = this.cameraManager;
        if (cameraManager != null) {
            cameraManager.closeDriver();
        }
    }

    public ViewfinderView getViewfinder() {
        return viewfinderView;
    }

    public Handler getHandler() {
        return handler;
    }

    public Handler getUIHandler() {
        return uiHandler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void handleDecode(Result result, Bitmap bitmap) {
        if (inScanMode) {
            ResultHandler makeResultHandler = ResultHandlerFactory.makeResultHandler(this, result);
            if (makeResultHandler == null) {
                Message.obtain(handler, R.id.restart_preview).sendToTarget();
                return;
            }
            Intent sendBackResult = new Intent();
            sendBackResult.setData(Uri.parse(result.getText()));
            setResult(RESULT_OK, sendBackResult);
            finish();
            return;
        }
        Message.obtain(handler, R.id.restart_preview).sendToTarget();
    }


    /* Access modifiers changed, original: protected */
    public void initCamera(final SurfaceHolder surfaceHolder) {
        if (!cameraInitialFlag) {
            cameraInitialFlag = true;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        cameraManager.openCameraFacingBack();
                        Message.obtain(uiHandler, MESSAGE_DECODER_CAMERA_OPEN_SUCCESS, surfaceHolder).sendToTarget();
                    } catch (Exception unused) {
                        Message.obtain(uiHandler, MESSAGE_DECODER_CAMERA_OPEN_FAIL, null).sendToTarget();
                    }
                }
            }).start();
        }
    }

    /* Access modifiers changed, original: protected */
    public void initCamera2(SurfaceHolder surfaceHolder) {
        try {
            cameraManager.openDriver(surfaceHolder);
            cameraInitialFlag = false;
            if (handler == null) {
                handler = new DecoderActivityHandler(this, decodeFormats, characterSet, cameraManager);
            }
        } catch (Exception e) {
            cameraInitialFlag = false;
            e.printStackTrace();
            String string = "No camera found";
            Builder builder = new Builder(this);
            builder.setMessage(string);
//                builder.setIcon(17301543);
            builder.setCancelable(false);
            builder.setPositiveButton("Close", new OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (!isFinishing()) {
                        finish();
                    }
                }
            });
            if (!isFinishing()) {
                builder.show();
            }
        }
    }

    /* Access modifiers changed, original: protected */
    public void showScanner() {
        inScanMode = true;
    }


}