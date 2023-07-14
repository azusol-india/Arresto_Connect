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
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.datamatrix.DataMatrixReader;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.ByteArrayOutputStream;
import java.util.Map;

import app.com.arresto.arresto_connect.R;

class DecodeHandler extends Handler {
    private final IDecoderActivity activity;
    private final DataMatrixReader dataMatrixReader = new DataMatrixReader();
    private Map<DecodeHintType, Object> hints;
    private final MultiFormatReader multiFormatReader = new MultiFormatReader();
    private final QRCodeReader qrCodeReader = new QRCodeReader();
    private boolean running = true;

    DecodeHandler(IDecoderActivity iDecoderActivity, Map<DecodeHintType, Object> map) {
        this.multiFormatReader.setHints(map);
        this.activity = iDecoderActivity;
        this.hints = map;
    }

    public void handleMessage(Message message) {
        if (this.running) {
            int i = message.what;
            if (i == R.id.decode) {
                decode((byte[]) message.obj, message.arg1, message.arg2);
            } else if (i == R.id.quit) {
                this.running = false;
                Looper.myLooper().quit();
            }
        }
    }

    private void decode(byte[] bArr, int i, int i2) {
        if (bArr != null) {
            PlanarYUVLuminanceSource buildLuminanceSourceRotation;
            Point screenResolution = this.activity.getCameraManager().getConfigurationManager().getScreenResolution();
            int length = bArr.length;
            int i3 = i * i2;
            if (i3 > length) {
                length = i3;
            }
            byte[] bArr2 = new byte[length];
            for (int i4 = 0; i4 < i2; i4++) {
                for (int i5 = 0; i5 < i; i5++) {
                    bArr2[(((i5 * i2) + i2) - i4) - 1] = bArr[(i4 * i) + i5];
                }
            }
            if (screenResolution.x < screenResolution.y) {
                buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSourceRotation(bArr, i, i2);
            } else {
                buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSourceRotation(bArr2, i2, i);
            }
            Object decodeQRcodeDataMatrixHybridBinarizer = decodeQRcodeDataMatrixHybridBinarizer(buildLuminanceSourceRotation);
            if (decodeQRcodeDataMatrixHybridBinarizer == null) {
                if (screenResolution.x < screenResolution.y) {
                    buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSource(bArr2, i2, i);
                } else {
                    buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSource(bArr, i, i2);
                }
                decodeQRcodeDataMatrixHybridBinarizer = decodeAllHybridBinarizer(buildLuminanceSourceRotation);
            }
            if (decodeQRcodeDataMatrixHybridBinarizer == null) {
                if (screenResolution.x < screenResolution.y) {
                    buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSourceRotation(bArr, i, i2);
                } else {
                    buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSourceRotation(bArr2, i2, i);
                }
                decodeQRcodeDataMatrixHybridBinarizer = decodeQRcodeDataMatrixGlobalHistogramBinarizer(buildLuminanceSourceRotation);
            }
            if (decodeQRcodeDataMatrixHybridBinarizer == null) {
                if (screenResolution.x < screenResolution.y) {
                    buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSource(bArr2, i2, i);
                } else {
                    buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSource(bArr, i, i2);
                }
                decodeQRcodeDataMatrixHybridBinarizer = decodeAllGlobalHistogramBinarizer(buildLuminanceSourceRotation);
            }
            if (decodeQRcodeDataMatrixHybridBinarizer == null) {
                for (int i6 = 0; i6 < length; i6++) {
                    bArr[i6] = (byte) (16777215 ^ bArr[i6]);
                }
                for (int i7 = 0; i7 < length; i7++) {
                    bArr2[i7] = (byte) (bArr2[i7] ^ 16777215);
                }
            }
            if (decodeQRcodeDataMatrixHybridBinarizer == null) {
                if (screenResolution.x < screenResolution.y) {
                    buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSource(bArr2, i2, i);
                } else {
                    buildLuminanceSourceRotation = this.activity.getCameraManager().buildLuminanceSource(bArr, i, i2);
                }
                decodeQRcodeDataMatrixHybridBinarizer = decodeAllHybridBinarizer(buildLuminanceSourceRotation);
                if (decodeQRcodeDataMatrixHybridBinarizer == null) {
                    decodeQRcodeDataMatrixHybridBinarizer = decodeAllGlobalHistogramBinarizer(buildLuminanceSourceRotation);
                }
            }
            Handler handler = this.activity.getHandler();
            if (decodeQRcodeDataMatrixHybridBinarizer != null) {
                if (handler != null) {
                    Message obtain = Message.obtain(handler, R.id.decode_succeeded, decodeQRcodeDataMatrixHybridBinarizer);
                    Bundle bundle = new Bundle();
                    bundleThumbnail(buildLuminanceSourceRotation, bundle);
                    obtain.setData(bundle);
                    obtain.sendToTarget();
                }
            } else if (handler != null) {
                Message.obtain(handler, R.id.decode_failed).sendToTarget();
            }
        }
    }

    private static void bundleThumbnail(PlanarYUVLuminanceSource planarYUVLuminanceSource, Bundle bundle) {
        int[] renderThumbnail = planarYUVLuminanceSource.renderThumbnail();
        int thumbnailWidth = planarYUVLuminanceSource.getThumbnailWidth();
        Bitmap createBitmap = Bitmap.createBitmap(renderThumbnail, 0, thumbnailWidth, thumbnailWidth, planarYUVLuminanceSource.getThumbnailHeight(), Config.ARGB_8888);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        createBitmap.compress(CompressFormat.JPEG, 50, byteArrayOutputStream);
        bundle.putByteArray(DecodeThread.BARCODE_BITMAP, byteArrayOutputStream.toByteArray());
    }

    private Result decodeAllHybridBinarizer(PlanarYUVLuminanceSource planarYUVLuminanceSource) {
        if (planarYUVLuminanceSource != null) {
            Result decodeWithState = null;
            try {
                int height = planarYUVLuminanceSource.getHeight();
                int width = planarYUVLuminanceSource.getWidth();
                if(height<10||width<10) {
                    return null;
                }
                decodeWithState = this.multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer(planarYUVLuminanceSource)));
            } catch (ReaderException ex) {
//                Log.e("scan"," error");
            } finally {
                this.multiFormatReader.reset();
            }
            return decodeWithState;
        }
        return null;
    }

    private Result decodeAllGlobalHistogramBinarizer(PlanarYUVLuminanceSource planarYUVLuminanceSource) {
        if (planarYUVLuminanceSource != null) {
            try {
                return this.multiFormatReader.decodeWithState(new BinaryBitmap(new GlobalHistogramBinarizer(planarYUVLuminanceSource)));
            } catch (Exception unused) {
                this.multiFormatReader.reset();
            }
        }
        return null;
    }

    private Result decodeQRcodeDataMatrixHybridBinarizer(PlanarYUVLuminanceSource planarYUVLuminanceSource) {
        if (planarYUVLuminanceSource == null) {
            return null;
        }
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(planarYUVLuminanceSource));
        try {
            return this.qrCodeReader.decode(binaryBitmap, this.hints);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Result decodeQRcodeDataMatrixGlobalHistogramBinarizer(PlanarYUVLuminanceSource planarYUVLuminanceSource) {
        if (planarYUVLuminanceSource == null) {
            return null;
        }
        BinaryBitmap binaryBitmap = new BinaryBitmap(new GlobalHistogramBinarizer(planarYUVLuminanceSource));
        try {
            return this.qrCodeReader.decode(binaryBitmap, this.hints);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
