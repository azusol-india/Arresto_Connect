package app.com.arresto.arresto_connect.constants;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.view.View;

import app.com.arresto.arresto_connect.R;

public class Box extends View {

    int w;
    int h;

    Box(Context context, int width, int hieght) {
        super(context);
        w = width;
        h = hieght;
    }


    @Override
    protected void onDraw(Canvas canvas) { // Override the onDraw() Method
        super.onDraw(canvas);
        int range = 350;
        //center
        int left = w / 2 - range;
        int right = w / 2 + range;
        int top =  (h / 3 - range);
        int btm =  (h / 3 + range);

        RectF rect = new RectF(left, top, right, btm);
        float radius = 10.0f; // should be retrieved from resources and defined as dp

        int outerFillColor = 0xffffffff;
// first create an off-screen bitmap and its canvas
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas auxCanvas = new Canvas(bitmap);

// then fill the bitmap with the desired outside color
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(outerFillColor);
        paint.setStyle(Paint.Style.FILL);
        auxCanvas.drawPaint(paint);

// then punch a transparent hole in the shape of the rect
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        auxCanvas.drawRoundRect(rect, radius, radius, paint);

// then draw the white rect border (being sure to get rid of the xfer mode!)
        paint.setXfermode(null);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.STROKE);
        auxCanvas.drawRoundRect(rect, radius, radius, paint);

// finally, draw the whole thing to the original canvas
        canvas.drawBitmap(bitmap, 0, 0, paint);
        Bitmap bitmapToDrawInTheCenter = BitmapFactory.decodeResource(getResources(), R.drawable.sample_label);

        bitmapToDrawInTheCenter = Bitmap.createScaledBitmap(bitmapToDrawInTheCenter, w/2+30, w/2, false);
        canvas.drawBitmap(bitmapToDrawInTheCenter, (bitmap.getWidth() - bitmapToDrawInTheCenter.getWidth()) / 2, btm + 50, new Paint());

    }
}