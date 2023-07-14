package app.com.arresto.arresto_connect.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import app.com.arresto.arresto_connect.constants.AppUtils;

import static app.com.arresto.arresto_connect.constants.AppUtils.isLogin;
import static app.com.arresto.arresto_connect.constants.Static_values.client_id;
import static app.com.arresto.arresto_connect.services.AlarmSetter.scheduleServiceUpdates;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        scheduleServiceUpdates(context);
        Log.d("Arresto onReceive", "Arresto onReceive  alarm");
        if (intent.getExtras().containsKey("type")) {
            String type = intent.getExtras().getString("type");
            if (type.equals("Storage"))
                context.startService(new Intent(context, CleanStorage.class));
            else if (type.equals("Location"))
                startLocationBackgroundService(context);
        }
    }

    public void startLocationBackgroundService(Context context) {
        if (isLogin() && !client_id.equals("2069")) {
            Background_LocationService mService = new Background_LocationService();
            Intent mServiceIntent = new Intent(context, mService.getClass());
            if (!AppUtils.isMyServiceRunning(context, mService.getClass())) {
                context.startService(mServiceIntent);
            }
        }
    }

}