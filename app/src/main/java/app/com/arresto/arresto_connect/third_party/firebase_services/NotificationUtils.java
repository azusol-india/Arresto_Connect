package app.com.arresto.arresto_connect.third_party.firebase_services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.TextUtils;
import android.util.Patterns;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationCompat.BigPictureStyle;

import com.facebook.share.internal.ShareConstants;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;

import app.com.arresto.arresto_connect.R;
import app.com.arresto.arresto_connect.ui.activity.BaseActivity;

public class NotificationUtils {
    private static String TAG = NotificationUtils.class.getSimpleName();
    public static ArrayList<Integer> count = new ArrayList<>();
    public static Context mcontext;
    String CHANNEL_ID = "Arresto";
    CharSequence name = "channel_name";

    public NotificationUtils(Context mContext) {
        mcontext = mContext;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent) {
        showNotificationMessage(title, message, timeStamp, null, intent);
    }

    public void showNotificationMessage(String title, String message, String timeStamp, String imageUrl, Intent intent) {
        if (!TextUtils.isEmpty(message)) {
            intent.putExtra(ShareConstants.WEB_DIALOG_PARAM_MESSAGE, message);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("key", "notification");
            count.add(Integer.valueOf(1));

            PendingIntent resultPendingIntent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                resultPendingIntent =   PendingIntent.getActivity(mcontext,0,intent,
                        PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
            } else {
                resultPendingIntent = PendingIntent.getActivity(mcontext, 0, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
            }
            if (TextUtils.isEmpty(imageUrl)) {
                showSmallNotification(title, message, timeStamp, resultPendingIntent);
                playNotificationSound();
            } else if (imageUrl != null && imageUrl.length() > 4 && Patterns.WEB_URL.matcher(imageUrl).matches()) {
                Bitmap bitmap = getBitmapFromURL(imageUrl);
                if (bitmap != null) {
                    showBigNotification(bitmap, title, message, timeStamp, resultPendingIntent);
                } else {
                    showSmallNotification(title, message, timeStamp, resultPendingIntent);
                }
            }
        }
    }

    private void showSmallNotification(String title, String message, String timeStamp, PendingIntent resultPendingIntent) {
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        NotificationManager notificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_LOW));
        }
        inboxStyle.addLine(message);
        int icon = R.drawable.logo;
        Uri alarmSound = RingtoneManager.getDefaultUri(2);
        Notification notification = new NotificationCompat.Builder(mcontext, CHANNEL_ID).setSmallIcon(icon)
                .setTicker(title)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setPriority(1)
                .setChannelId(CHANNEL_ID)
                .setStyle(inboxStyle)
                .setOngoing(false)
                .setWhen(getTimeMilliSec(timeStamp))
                .setLargeIcon(BitmapFactory.decodeResource(mcontext.getResources(), icon))
                .setContentText(message).build();
//        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(m, notification);
        if (!isAppIsInBackground(mcontext)) {
            load_fragment();
        } else {
            setBadge(count.size());
        }
    }

    private void showBigNotification(Bitmap bitmap, String title, String message, String timeStamp, PendingIntent resultPendingIntent) {
        BigPictureStyle bigPictureStyle = new BigPictureStyle();
        bigPictureStyle.setBigContentTitle(title);
        bigPictureStyle.setSummaryText(Html.fromHtml(message).toString());
        bigPictureStyle.bigPicture(bitmap);
        NotificationManager notificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (VERSION.SDK_INT >= 26) {
            notificationManager.createNotificationChannel(new NotificationChannel(this.CHANNEL_ID, this.name, NotificationManager.IMPORTANCE_LOW));
        }
        int icon = R.drawable.logo;
        Uri alarmSound = RingtoneManager.getDefaultUri(2);
        Notification notification = new NotificationCompat.Builder(mcontext, CHANNEL_ID).setSmallIcon(icon)
                .setTicker(title)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSound(alarmSound)
                .setStyle(bigPictureStyle)
                .setChannelId(this.CHANNEL_ID)
                .setOngoing(false)
                .setWhen(getTimeMilliSec(timeStamp))
                .setLargeIcon(BitmapFactory.decodeResource(mcontext.getResources(), icon))
                .setContentText(message).build();
//        notification.flags |= Notification.FLAG_ONGOING_EVENT;

        int m = (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE);
        notificationManager.notify(m, notification);
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(strURL).openConnection();
            connection.setDoInput(true);
            connection.connect();
            return BitmapFactory.decodeStream(connection.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void playNotificationSound() {
        try {
            RingtoneManager.getRingtone(mcontext, RingtoneManager.getDefaultUri(2)).play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (VERSION.SDK_INT > 20) {
            for (ActivityManager.RunningAppProcessInfo processInfo : am.getRunningAppProcesses()) {
                if (processInfo.importance == 100) {
                    boolean isInBackground2 = isInBackground;
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground2 = false;
                        }
                    }
                    isInBackground = isInBackground2;
                }
            }
            return isInBackground;
        } else if ((am.getRunningTasks(1).get(0).topActivity).getPackageName().equals(context.getPackageName())) {
            return false;
        } else {
            return true;
        }
    }

    public static void clearNotifications(Context context) {
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
    }

    public static int count_pnding_Notific() {
        return count.size();
    }

    public static long getTimeMilliSec(String timeStamp) {
        try {
            return BaseActivity.server_date_time.parse(timeStamp).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public void load_fragment() {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
            }
        }, 1000);
    }

    public static void setBadge(int count2) {
        String launcherClassName = getLauncherClassName(mcontext);
        if (launcherClassName != null) {
            Intent intent = new Intent("android.intent.action.BADGE_COUNT_UPDATE");
            intent.putExtra("badge_count", count2);
            intent.putExtra("badge_count_package_name", mcontext.getPackageName());
            intent.putExtra("badge_count_class_name", launcherClassName);
            mcontext.sendBroadcast(intent);
        }
    }

    public static String getLauncherClassName(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        for (ResolveInfo resolveInfo : pm.queryIntentActivities(intent, 0)) {
            if (resolveInfo.activityInfo.applicationInfo.packageName.equalsIgnoreCase(context.getPackageName())) {
                return resolveInfo.activityInfo.name;
            }
        }
        return null;
    }
}
