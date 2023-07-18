package app.com.arresto.arresto_connect.third_party.flir_thermal;

public class Dot {
    public float x, bitmapX;
    public float y, bitmapY;
    public float radius=30;
    public String dotName = "";

    public Dot(float x, float y, float radius) {
        this.x = x;
        this.y = y;
    }

    public Dot(float x, float y, float bitmapX, float bitmapY,float radius) {
        this.x = x;
        this.y = y;
        this.bitmapX = bitmapX;
        this.bitmapY = bitmapY;
        this.radius = radius;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getIntX() {
        return (int)x;
    }

    public int getIntY() {
        return (int)y;
    }

    public int getIntBitmapX() {
        return (int)bitmapX;
    }

    public int getIntBitmapY() {
        return (int)bitmapY;
    }

    public float getBitmapX() {
        return bitmapX;
    }

    public float getBitmapY() {
        return bitmapY;
    }

    public float getRadius() {
        return radius;
    }

    public String getDotName() {
        return dotName;
    }

    public void setDotName(String dotName) {
        this.dotName = dotName;
    }
    public void setRadius(float radius) {
        this.radius = radius;
    }

    public boolean isInside(float x, float y) {
        return (getX() - x) * (getX() - x) + (getY() - y) * (getY() - y) <= radius * radius;
    }
}