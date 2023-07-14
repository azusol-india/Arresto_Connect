package app.com.arresto.arresto_connect.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import static android.os.Build.VERSION.SDK_INT;

public class AlarmSetter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Do your stuff
        Log.e("Arresto AlarmSetter", "Arresto AlarmSetter  alarm");
        scheduleServiceUpdates(context);
        scheduleBackgroundService(context);
    }

    public static void scheduleServiceUpdates(final Context context) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("type", "Storage");

        PendingIntent pendingIntent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getActivity(context,0,intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        }else {
            pendingIntent = PendingIntent.getActivity(context,0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

        }

        // compute first call time 1 minute from now
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 1);
        long trigger = calendar.getTimeInMillis();
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pendingIntent);
        }
        // you can use RTC_WAKEUP instead of RTC to wake up the device
    }

    public static void scheduleBackgroundService(final Context context) {
        final Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra("type", "Location");
        final PendingIntent pending = PendingIntent.getBroadcast(context, 112, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long trigger = System.currentTimeMillis() + 10 * 1000;
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION_CODES.KITKAT <= SDK_INT && SDK_INT < Build.VERSION_CODES.M) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigger, pending);
        } else if (SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pending);
        }
    }

}