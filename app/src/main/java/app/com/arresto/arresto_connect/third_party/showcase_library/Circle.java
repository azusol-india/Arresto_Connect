package app.com.arresto.arresto_connect.third_party.showcase_library;

import android.graphics.Canvas;

public class Circle extends Shape {
    private int radius;
    private int x;
    private int y;

    public Circle(int x2, int y2, int radius2) {
        this.x = x2;
        this.y = y2;
        this.radius = radius2;
    }

    public int getX() {
        return this.x;
    }

    public void setX(int x2) {
        this.x = x2;
    }

    public int getY() {
        return this.y;
    }

    public void setY(int y2) {
        this.y = y2;
    }

    public int getRadius() {
        return this.radius;
    }

    public void setRadius(int radius2) {
        this.radius = radius2;
    }

    public void drawOn(Canvas canvas) {
        if (isDisplayBorder()) {
            canvas.drawCircle((float) getX(), (float) getY(), ((float) getRadius()) * 1.2f, getBorderPaint());
        }
        canvas.drawCircle((float) getX(), (float) getY(), (float) getRadius(), getPaint());
    }
}
