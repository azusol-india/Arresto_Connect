/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan.camera;

import android.graphics.Point;
import android.hardware.Camera;
import android.os.Handler;
import android.util.Log;

class PreviewCallback implements Camera.PreviewCallback {
   private static final String TAG = "PreviewCallback";
   private final CameraConfigurationManager configManager;
   private Handler previewHandler;
   private int previewMessage;

   PreviewCallback(CameraConfigurationManager cameraConfigurationManager) {
       this.configManager = cameraConfigurationManager;
   }

   /* Access modifiers changed, original: 0000 */
   public void setHandler(Handler handler, int i) {
       this.previewHandler = handler;
       this.previewMessage = i;
   }

   public void onPreviewFrame(byte[] bArr, Camera camera) {
       Point cameraResolution = this.configManager.getCameraResolution();
       Handler handler = this.previewHandler;
       if (handler != null) {
           handler.obtainMessage(this.previewMessage, cameraResolution.x, cameraResolution.y, bArr).sendToTarget();
           this.previewHandler = null;
           return;
       }
       Log.d(TAG, "Got preview callback, but no handler for it");
   }
}
