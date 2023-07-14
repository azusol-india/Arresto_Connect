/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import java.util.Collection;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.third_party.custom_scan.camera.CameraManager;

public class DecoderActivityHandler extends Handler {
    private static final String TAG = "DecoderActivityHandler";
    private final IDecoderActivity activity;
    private final CameraManager cameraManager;
    private final DecodeThread decodeThread;
    private State state = State.SUCCESS;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    DecoderActivityHandler(IDecoderActivity iDecoderActivity, Collection<BarcodeFormat> collection, String str, CameraManager cameraManager) {
        activity = iDecoderActivity;
        decodeThread = new DecodeThread(iDecoderActivity, collection, str, new ViewfinderResultPointCallback(iDecoderActivity.getViewfinder()));
        decodeThread.start();
        this.cameraManager = cameraManager;
        restartPreviewAndDecodeOne();
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case R.id.auto_focus:
                if (state == State.PREVIEW) {
                    cameraManager.requestAutoFocus(this, R.id.auto_focus);
                    return;
                }
                return;
            case R.id.decode_failed :
                state = State.PREVIEW;
                cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                return;
            case R.id.decode_succeeded:
                state = State.SUCCESS;
                Bundle data = message.getData();
                Bitmap bitmap = null;
                if (data != null) {
                    byte[] byteArray = data.getByteArray(DecodeThread.BARCODE_BITMAP);
                    if (byteArray != null) {
                        bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length, null).copy(Config.ARGB_8888, true);
                    }
                }
                activity.handleDecode((Result) message.obj, bitmap);
                return;
            case R.id.restart_preview :
                restartPreviewAndDecode();
                return;
            case R.id.return_scan_result :
                IDecoderActivity iDecoderActivity = activity;
                if (iDecoderActivity instanceof Activity) {
                    ((Activity) iDecoderActivity).setResult(-1, (Intent) message.obj);
                    ((Activity) activity).finish();
                    return;
                }
                return;
            default:
                return;
        }
    }

    public void quitSynchronously() {
        state = State.DONE;
        cameraManager.stopPreview();
        Message.obtain(decodeThread.getHandler(), R.id.quit).sendToTarget();
        try {
            decodeThread.join(500);
        } catch (InterruptedException unused) {
        }
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    /* Access modifiers changed, original: 0000 */
    public void restartPreviewAndDecodeOne() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            new Thread(new Runnable() {
                public void run() {
                    try {
                        cameraManager.startPreview();
                     cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                     cameraManager.requestAutoFocus(DecoderActivityHandler.this, R.id.auto_focus);
                    } catch (Exception unused) {
                    }
                }
            }).start();
            activity.getViewfinder().drawViewfinder();
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void restartPreviewAndDecode() {
        if (state == State.SUCCESS) {
            state = State.PREVIEW;
            new Thread(new Runnable() {
                public void run() {
                    try {
                     cameraManager.requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                     cameraManager.requestAutoFocus(DecoderActivityHandler.this, R.id.auto_focus);
                    } catch (Exception unused) {
                    }
                }
            }).start();
            activity.getViewfinder().drawViewfinder();
        }
    }
}
