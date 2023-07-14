/*
 * <!--
 *   ~  Copyright (c)
 *   ~  @website: http://runpro.co.in/
 *   ~  @author: RunPro
 *   ~  @license:  http://runpro.co.in/
 *   ~
 *   ~  The below module/code/specifications belong to RunPro solely.
 *   -->
 *
 */

package app.com.arresto.arresto_connect.constants;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import app.com.arresto.arresto_connect.R;

public class RingtonePlayer {

    private static final String TAG = RingtonePlayer.class.getSimpleName();
    private MediaPlayer mediaPlayer;
    private Context context;

    public RingtonePlayer(Context context, int resource) {
        this.context = context;
        mediaPlayer = MediaPlayer.create(context, resource);
    }

    public RingtonePlayer(Context context) {
        this.context = context;
//        Uri notification = getNotification();
//        if (notification != null) {
//        mediaPlayer = MediaPlayer.create(context,notification);
//        }
        mediaPlayer = MediaPlayer.create(context, R.raw.buzzer_alert);
        AudioManager am =
                (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        am.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                0);
    }

    private Uri getNotification() {
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALL);

        if (notification == null) {
            // notification is null, using backup
            notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            // I can't see this ever being null (as always have a default notification)
            // but just incase
            if (notification == null) {
                // notification backup is null, using 2nd backup
                notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            }
        }
        return notification;
    }

    public void play(boolean looping) {
        if (mediaPlayer == null) {
            Log.e(TAG, "mediaPlayer isn't created ");
            return;
        }
        mediaPlayer.setLooping(looping);
        mediaPlayer.start();
    }

    public synchronized void stop() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.pause();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }

        }
    }

    public void releaseMedia() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.stop();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}