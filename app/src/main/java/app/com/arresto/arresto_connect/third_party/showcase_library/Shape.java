package app.com.arresto.arresto_connect.third_party.showcase_library;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;

public abstract class Shape {
    private int borderColor = Color.parseColor("#AA999999");
    private Paint borderPaint;
    private int color = Color.argb(0, 0, 0, 0);
    private boolean displayBorder = false;
    protected Paint paint = new Paint();

    public abstract void drawOn(Canvas canvas);

    public Shape() {
        this.paint.setColor(getColor());
        this.paint.setAntiAlias(true);
        this.paint.setXfermode(new PorterDuffXfermode(Mode.DST_ATOP));
        this.borderPaint = new Paint();
        this.borderPaint.setAntiAlias(true);
        this.borderPaint.setColor(this.borderColor);
    }

    public void setColor(int color2) {
        this.color = color2;
        this.paint.setColor(this.color);
    }

    public int getColor() {
        return this.color;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public int getBorderColor() {
        return this.borderColor;
    }

    public boolean isDisplayBorder() {
        return this.displayBorder;
    }

    public void setDisplayBorder(boolean displayBorder2) {
        this.displayBorder = displayBorder2;
    }

    public void setBorderColor(int borderColor2) {
        this.borderColor = borderColor2;
        this.paint.setColor(borderColor2);
    }

    public Paint getBorderPaint() {
        return this.borderPaint;
    }
}
