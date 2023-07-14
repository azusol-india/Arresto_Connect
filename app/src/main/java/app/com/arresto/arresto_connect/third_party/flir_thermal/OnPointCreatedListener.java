package app.com.arresto.arresto_connect.third_party.flir_thermal;

import android.graphics.Bitmap;

import java.util.ArrayList;

public interface OnPointCreatedListener {
    void OnPointCreated(Bitmap finalBitmap, ArrayList<Dot> points);
}