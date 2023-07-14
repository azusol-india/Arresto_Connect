/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

/*
 *  *The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.constants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class AppLocationService extends Service implements LocationListener {
    private static final long MIN_DISTANCE_FOR_UPDATE = 10;
    private static final long MIN_TIME_FOR_UPDATE = 1000 * 5;
    protected LocationManager locationManager;
    Context mcontext;

    OnLocationChange listner;

    public AppLocationService(Context context, OnLocationChange onLocationChange) {
        this.mcontext = context;
        this.listner = onLocationChange;
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        mlocation = getLocation();
        if (mlocation != null) {
            onLocationChanged(mlocation);
        }
    }

    Location mlocation;

    public Location getLocation() {
        if (ActivityCompat.checkSelfPermission(mcontext, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcontext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        if (locationManager.getAllProviders().contains(LocationManager.NETWORK_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);
        if (locationManager.getAllProviders().contains(LocationManager.GPS_PROVIDER))
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_FOR_UPDATE, MIN_DISTANCE_FOR_UPDATE, this);

        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, true);
        if (locationManager != null && bestProvider != null) {
            mlocation = locationManager.getLastKnownLocation(bestProvider);
            return mlocation;
        }
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e("location 1", " current lat " + location.getLatitude());
        Log.e("location 2", " current long " + location.getLongitude());
        if (listner != null) {
            listner.locationChange(location, location.getLatitude(), location.getLongitude());
        }
    }

    public void onProviderDisabled(String provider) {
//        Toast.makeText(mcontext, "Gps Disabled", Toast.LENGTH_SHORT).show();
        mlocation = getLocation();
        if (mlocation != null)
            onLocationChanged(mlocation);
    }


    public void onProviderEnabled(String provider) {
//        Toast.makeText(mcontext, "Gps Enabled", Toast.LENGTH_SHORT).show();
        mlocation = getLocation();
//        mlocation = getLocation(provider);
        if (mlocation != null)
            onLocationChanged(mlocation);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    public interface OnLocationChange {
        void locationChange(Location location, double latitude, double longitude);
    }
}