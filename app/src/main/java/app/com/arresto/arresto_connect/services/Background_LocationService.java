package app.com.arresto.arresto_connect.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.data.Preferences;
import app.com.arresto.arresto_connect.network.All_Api;
import app.com.arresto.arresto_connect.network.NetworkRequest;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static app.com.arresto.arresto_connect.constants.AppUtils.isLogin;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.CLIENT_ID;
import static app.com.arresto.arresto_connect.constants.PrefernceConstants.USER_ID;
import static app.com.arresto.arresto_connect.constants.Static_values.mPrefrence;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class Background_LocationService extends Service {
    public int counter = 0;
    //    private long UPDATE_INTERVAL = 10 * 60 * 1000;  /* 10 minutes */
//    private long UPDATE_INTERVAL = 10 * 1000;  /* 5 Sec */
//    private long FASTEST_INTERVAL = 2000; /* 2 sec */
    private LocationRequest mLocationRequest;
    String NOTIFICATION_CHANNEL_ID = "Background_Location_id";
    String channelName = "Background Location Service";
    String user_id, client_id;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mPrefrence = Preferences.getInstance(getApplicationContext());
        client_id = mPrefrence.getData(CLIENT_ID);
        user_id = mPrefrence.getData(USER_ID);
        startLocationUpdates();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setSmallIcon(R.drawable.logo)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request to start receiving updates
        mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        mLocationRequest.setInterval(UPDATE_INTERVAL);
//        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(50);   // 50 meters
        // Create LocationSettingsRequest object using location request
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        // Check whether location settings are satisfied
        SettingsClient settingsClient = LocationServices.getSettingsClient(this);
        settingsClient.checkLocationSettings(locationSettingsRequest);

        // new Google API SDK v11 uses getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (locationClient == null)
            locationClient = getFusedLocationProviderClient(this);
        locationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        // do work here
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper());
    }

    FusedLocationProviderClient locationClient;

    public void getLastLocation() {
        // Get last known recent location using new Google Play Services SDK (v11+)
        if (locationClient == null)
            locationClient = getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // GPS location can be null if GPS is switched off
                Log.e("Location Activity", "location fetched  : " + location);
                if (location != null) {
                    onLocationChanged(location);
                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Location Activity", "Error trying to get last GPS location");
                        e.printStackTrace();
                    }
                });
    }

    public void onLocationChanged(Location location) {
        // New location has now been determined
        String msg = "Updated Location: " + location.getLatitude() + "," + location.getLongitude();
        // You can now create a LatLng Object for use with maps
        Log.e("Latitude", " is " + msg);
        showNotificationMessage("Location notification", msg);
        save_location("" + location.getLatitude(), "" + location.getLongitude());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Service_Restarter.class);
        this.sendBroadcast(broadcastIntent);
    }


    private Timer timer;
    private TimerTask timerTask;

    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                Log.e("Count", "=========  " + (counter++));
                getLastLocation();
            }
        };
        timer.schedule(timerTask, 1000,  60 * 1000); //
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    NotificationCompat.Builder notification_builder;
    NotificationManager notificationManager;


    public void showNotificationMessage(String title, String message) {
        if (!TextUtils.isEmpty(message)) {
            if (notification_builder == null) {
                Intent intent = new Intent(this, BaseActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                notification_builder = new NotificationCompat.Builder(this, CHANNEL_ID);
                Uri alarmSound = RingtoneManager.getDefaultUri(2);
                if (Build.VERSION.SDK_INT >= 26) {
                    notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, channel_Name, NotificationManager.IMPORTANCE_LOW));
                }
                Notification notification = notification_builder
                        .setTicker(title)
                        .setAutoCancel(true)
                        .setContentTitle(title)
                        .setContentIntent(resultPendingIntent)
                        .setSound(alarmSound).setPriority(1)
                        .setChannelId(CHANNEL_ID)
                        .setOngoing(false)
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.drawable.logo)
                        .setContentText(message).build();
                notificationManager.notify(1, notification);
                playNotificationSound();
            } else {
                notification_builder.setContentText(message);
                notificationManager.notify(1, notification_builder.build());
            }
        }
    }

    String CHANNEL_ID = "Location channel_id";
    String channel_Name = "Location Service";

    public void playNotificationSound() {
        try {
            RingtoneManager.getRingtone(this, RingtoneManager.getDefaultUri(2)).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void save_location(String latitude, String longitude) {
        if (isLogin()) {
            HashMap<String, String> params = new HashMap<>();
            params.put("client_id", client_id);
            params.put("user_id", user_id);
            params.put("latitute", latitude);
            params.put("longitute", longitude);
            params.put("local_time", "" + BaseActivity.server_date_time.format(new Date()));
            Static_values.user_id = user_id;
            new NetworkRequest().make_post_request(All_Api.save_location_api, params, new NetworkRequest.VolleyResponseListener() {
                @Override
                public void onResponse(String response) {
                    Log.e("response", " =   " + response);
                }

                @Override
                public void onError(String error) {
                    Log.e("error", "= " + error);
                }
            });
        }
    }

}