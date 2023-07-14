package app.com.arresto.arresto_connect.third_party.showcase_library;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class RoundRect extends Shape {
    public static final int BORDER_PADDING = 30;
    private int height;
    private int width;
    private int x;
    private int y;

    public RoundRect(int x2, int y2, int width2, int height2) {
        this.x = x2;
        this.y = y2;
        this.width = width2;
        this.height = height2;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public void drawOn(Canvas canvas) {
        if (isDisplayBorder()) {
            drawRoundedRect(canvas, (float) (getX() - 30), (float) (getY() - 30), (float) (getX() + getWidth() + 30), (float) (getY() + getHeight() + 30), getBorderPaint());
        }
        drawRoundedRect(canvas, (float) getX(), (float) getY(), (float) (getX() + getWidth()), (float) (getY() + getHeight()), this.paint);
    }

    private static void drawRoundedRect(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        float radius = (bottom - top) / 2.0f;
        canvas.drawRoundRect(new RectF(left, top, right, bottom), radius, radius, paint);
    }
}
