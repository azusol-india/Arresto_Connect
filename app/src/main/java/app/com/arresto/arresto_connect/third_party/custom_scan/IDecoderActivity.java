/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan;

import android.graphics.Bitmap;
import android.os.Handler;

import com.google.zxing.Result;

import app.com.arresto.arresto_connect.third_party.custom_scan.camera.CameraManager;

public interface IDecoderActivity {
    CameraManager getCameraManager();

    Handler getHandler();

    ViewfinderView getViewfinder();

    void handleDecode(Result result, Bitmap bitmap);
}
