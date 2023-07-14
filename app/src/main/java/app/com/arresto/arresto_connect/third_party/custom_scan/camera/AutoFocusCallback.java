/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan.camera;

import android.hardware.Camera;
import android.os.Handler;

class AutoFocusCallback implements Camera.AutoFocusCallback {
   private static final long AUTOFOCUS_INTERVAL_MS = 2500;
   private Handler autoFocusHandler;
   private int autoFocusMessage;

   AutoFocusCallback() {
   }

   void setHandler(Handler handler, int i) {
       this.autoFocusHandler = handler;
       this.autoFocusMessage = i;
   }

   public void onAutoFocus(boolean z, Camera camera) {
       Handler handler = this.autoFocusHandler;
       if (handler != null) {
           this.autoFocusHandler.sendMessageDelayed(handler.obtainMessage(this.autoFocusMessage, z), AUTOFOCUS_INTERVAL_MS);
       }
   }
}
