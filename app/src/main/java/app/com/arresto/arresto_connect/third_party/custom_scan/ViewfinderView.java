/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.google.zxing.ResultPoint;

import java.util.ArrayList;
import java.util.List;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.third_party.custom_scan.camera.CameraManager;

public class ViewfinderView extends View {
    private static final int CURRENT_POINT_OPACITY = 160;
    private final int blueColor;
    private CameraManager cameraManager;
    private final Paint paint = new Paint(1);
    private List<ResultPoint> possibleResultPoints;
    private Bitmap resultBitmap;

    public ViewfinderView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        Resources resources = getResources();
        this.blueColor = resources.getColor(R.color.app_error);
        this.possibleResultPoints = new ArrayList(5);
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public void onDraw(Canvas canvas) {
        CameraManager cameraManager = this.cameraManager;
        if (cameraManager != null) {
            Rect framingRect = cameraManager.getFramingRect();
            if (framingRect != null && framingRect.bottom >= 88 && framingRect.right >= 88) {
                int i;
                canvas.getWidth();
                canvas.getHeight();
                if (this.resultBitmap != null) {
                    this.paint.setAlpha(CURRENT_POINT_OPACITY);
                    canvas.drawBitmap(this.resultBitmap, null, framingRect, this.paint);
                } else {
                    int i2;
                    if (framingRect.bottom - framingRect.top > framingRect.right - framingRect.left) {
                        i = framingRect.right;
                        i2 = framingRect.left;
                    } else {
                        i = framingRect.bottom;
                        i2 = framingRect.top;
                    }
                    i = (i - i2) / 5;
                    i2 = (framingRect.right - framingRect.left) / 15;
                    int i3 = (i + 15) + i2;
                    if (framingRect.right > i3 && framingRect.bottom > i3) {
                        this.paint.setColor(this.blueColor);
                        Canvas canvas2 = canvas;
                        canvas2.drawRect((float) (framingRect.left + i2), (float) framingRect.top, (float) (((framingRect.left + i2) + 15) + i), (float) (framingRect.top + 15), this.paint);
                        canvas2.drawRect((float) (framingRect.left + i2), (float) framingRect.top, (float) ((framingRect.left + i2) + 15), (float) ((framingRect.top + 15) + i), this.paint);
                        canvas2.drawRect((float) (((framingRect.right - i2) - 15) - i), (float) framingRect.top, (float) (framingRect.right - i2), (float) (framingRect.top + 15), this.paint);
                        canvas2.drawRect((float) ((framingRect.right - i2) - 15), (float) framingRect.top, (float) (framingRect.right - i2), (float) ((framingRect.top + 15) + i), this.paint);
                        canvas2.drawRect((float) (framingRect.left + i2), (float) ((framingRect.bottom - 15) - i), (float) ((framingRect.left + i2) + 15), (float) framingRect.bottom, this.paint);
                        canvas2.drawRect((float) (framingRect.left + i2), (float) (framingRect.bottom - 15), (float) (((framingRect.left + i) + i2) + 15), (float) framingRect.bottom, this.paint);
                        canvas2.drawRect((float) (((framingRect.right - i2) - 15) - i), (float) (framingRect.bottom - 15), (float) (framingRect.right - i2), (float) framingRect.bottom, this.paint);
                        canvas2.drawRect((float) ((framingRect.right - i2) - 15), (float) ((framingRect.bottom - 15) - i), (float) (framingRect.right - i2), (float) framingRect.bottom, this.paint);
                    }
                }
            }
        }
    }

    public void drawViewfinder() {
        Bitmap bitmap = this.resultBitmap;
        this.resultBitmap = null;
        if (bitmap != null) {
            bitmap.recycle();
        }
        invalidate();
    }

    public void addPossibleResultPoint(ResultPoint resultPoint) {
        List list = this.possibleResultPoints;
        synchronized (resultPoint) {
            list.add(resultPoint);
            int size = list.size();
            if (size > 20) {
                list.subList(0, size - 10).clear();
            }
        }
    }
}
