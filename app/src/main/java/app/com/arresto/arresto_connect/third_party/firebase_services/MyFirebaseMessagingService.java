package app.com.arresto.arresto_connect.third_party.firebase_services;

import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.facebook.share.internal.ShareConstants;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import app.com.arresto.arresto_connect.constants.Static_values;
import app.com.arresto.arresto_connect.database.Data_daowload;
import app.com.arresto.arresto_connect.ui.activity.HomeActivity;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    Data_daowload dataBase = new Data_daowload(this);
    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        Log.e("NEW_TOKEN", s);
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        StringBuilder sb = new StringBuilder();
        sb.append("refreshedToken: ");
        sb.append(refreshedToken);
        Log.e("MyFirbasInstnceIDSrvic", sb.toString());
        Static_values.device_id = refreshedToken;
        Intent registrationComplete = new Intent("tokenReceiver");
        registrationComplete.putExtra("token", refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        String str = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("From: ");
        sb.append(remoteMessage);
        Log.e(str, sb.toString());
        if (remoteMessage != null) {
            if (remoteMessage.getData().size() > 0) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append(" received data");
                sb2.append(remoteMessage.getData());
                Log.e("message  ", sb2.toString());
                handleDataMessage(remoteMessage.getData());
            }
            if (remoteMessage.getNotification() != null) {
                String str2 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Notification Body: ");
                sb3.append(remoteMessage.getNotification().getBody());
                Log.e(str2, sb3.toString());
                handleNotification(remoteMessage.getNotification().getBody());
            }
        }
    }

    private void handleNotification(String message) {
        if (!NotificationUtils.isAppIsInBackground(this)) {
            Log.e(TAG, "handleNotification: app is in foreground, broadcast the push message");
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra(ShareConstants.WEB_DIALOG_PARAM_MESSAGE, message);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);
            new NotificationUtils(this).playNotificationSound();
            return;
        }
        Log.e(TAG, "handleNotification: If the app is in background, firebase itself handles the notification");
    }

    private void handleDataMessage(Map<String, String> remoteMessage) {
        try {
            JSONObject jsonObject = new JSONObject(remoteMessage);
            String message = jsonObject.getString("message");
            String status = jsonObject.getString("status");
            String time = jsonObject.getString("timestamp");
            String title = jsonObject.getString("title");
            if (jsonObject.has("type") && jsonObject.getString("type").equals("sensor_alert")) {
                broadcastSensorIntent(title, status);
            } else {
                dataBase.open();
                long id = dataBase.insert_notification(getSharedPreferences("Arresto_Connect", 0).getString("user_id", ""), jsonObject.toString(), "pending", time);
                dataBase.close();
                broadcastIntent(id, title, jsonObject.toString(), time);
            }
            showNotificationMessage(title, message, time);
        } catch (JSONException e) {
            e.printStackTrace();
            StringBuilder sb = new StringBuilder();
            sb.append(" is ");
            sb.append(e.getMessage());
            Log.e(" JSONException ", sb.toString());
        }
    }

    public void broadcastIntent(long id, String title, String data, String time) {
        Intent intent = new Intent("new_message");
        String str = ShareConstants.WEB_DIALOG_PARAM_ID;
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(id);
        intent.putExtra(str, sb.toString());
        intent.putExtra("title", title);
        intent.putExtra(ShareConstants.WEB_DIALOG_PARAM_MESSAGE, data);
        intent.putExtra("time", time);
        intent.putExtra("status", "pending");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public void broadcastSensorIntent(String title, String status) {
        Intent intent = new Intent("sensor_alert");
        String str = ShareConstants.WEB_DIALOG_PARAM_ID;
        StringBuilder sb = new StringBuilder();
        intent.putExtra(str, sb.toString());
        intent.putExtra("title", title);
        intent.putExtra("status", status);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void showNotificationMessage(String title, String message, String time) {
        notificationUtils = new NotificationUtils(this);
        notificationUtils.showNotificationMessage(title, message, time, new Intent(this, HomeActivity.class));
    }

    private void showNotificationMessageWithBigImage(String title, String message, String timeStamp, String imageUrl) {
        notificationUtils = new NotificationUtils(this);
    }
}
