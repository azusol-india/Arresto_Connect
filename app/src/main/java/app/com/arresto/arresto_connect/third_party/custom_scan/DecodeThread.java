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
import android.os.Handler;
import android.os.Looper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.ResultPointCallback;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

class DecodeThread extends Thread {
   public static final String BARCODE_BITMAP = "barcode_bitmap";
   private static final String TAG = "DecodeThread";
   private final IDecoderActivity activity;
   private Handler handler;
   private final CountDownLatch handlerInitLatch = new CountDownLatch(1);
   private final Map<DecodeHintType, Object> hints = new EnumMap(DecodeHintType.class);

   DecodeThread(IDecoderActivity iDecoderActivity, Collection<BarcodeFormat> collection2, String str, ResultPointCallback resultPointCallback) {
       this.activity = iDecoderActivity;
       if ((collection2 == null || collection2.isEmpty()) && (iDecoderActivity instanceof Activity)) {
           collection2 = EnumSet.noneOf(BarcodeFormat.class);
           collection2.addAll(DecodeFormatManager.PRODUCT_FORMATS);
           collection2.addAll(DecodeFormatManager.INDUSTRIAL_FORMATS);
           collection2.addAll(DecodeFormatManager.QR_CODE_FORMATS);
           collection2.addAll(DecodeFormatManager.AZTEC_FORMATS);
           collection2.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);
           collection2.addAll(DecodeFormatManager.MAXICODE_FORMATS);
           collection2.addAll(DecodeFormatManager.PDF417_FORMATS);
       }
       this.hints.put(DecodeHintType.POSSIBLE_FORMATS, collection2);
       if (str != null) {
           this.hints.put(DecodeHintType.CHARACTER_SET, str);
       }
       this.hints.put(DecodeHintType.NEED_RESULT_POINT_CALLBACK, resultPointCallback);
   }

   /* Access modifiers changed, original: 0000 */
   public Handler getHandler() {
       try {
           this.handlerInitLatch.await();
       } catch (InterruptedException unused) {
       }
       return this.handler;
   }

   public void run() {
       Looper.prepare();
       this.handler = new DecodeHandler(this.activity, this.hints);
       this.handlerInitLatch.countDown();
       Looper.loop();
   }
}
