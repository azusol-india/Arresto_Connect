/*
 *  Copyright (c)
 *  @website: http://arresto.in/
 *  @author: Arresto Solutions Pvt. Ltd.
 *  @license: http://arresto.in/
 *
 *  The below module/code/specifications belong to Arresto Solutions Pvt. Ltd. solely.
 */

package app.com.arresto.arresto_connect.third_party.custom_scan;

import android.app.Activity;
import android.os.AsyncTask;

public class AutoTerminationTimer {
    private static final long INACTIVITY_DELAY_MS = 240000;
    private final Activity activity;
    private AsyncTask<Object, Object, Object> inactivityTask;

    private final class InactivityAsyncTask extends AsyncTask<Object, Object, Object> {
        private InactivityAsyncTask() {
        }

        /* Access modifiers changed, original: protected|varargs */
        public Object doInBackground(Object... objArr) {
            try {
                Thread.sleep(AutoTerminationTimer.INACTIVITY_DELAY_MS);
                AutoTerminationTimer.this.activity.finish();
            } catch (InterruptedException unused) {
            }
            return null;
        }
    }

   public AutoTerminationTimer(Activity activity) {
        this.activity = activity;
        onActivity();
    }

    /* Access modifiers changed, original: declared_synchronized */
    public synchronized void onActivity() {
        cancel();
        this.inactivityTask = new InactivityAsyncTask();
        this.inactivityTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Object[0]);
    }

    /* Access modifiers changed, original: declared_synchronized */
    public synchronized void onPause() {
        cancel();
    }

    /* Access modifiers changed, original: declared_synchronized */
    public synchronized void onResume() {
        onActivity();
    }

    private synchronized void cancel() {
        AsyncTask asyncTask = this.inactivityTask;
        if (asyncTask != null) {
            asyncTask.cancel(true);
            this.inactivityTask = null;
        }
    }

    /* Access modifiers changed, original: 0000 */
    public void shutdown() {
        cancel();
    }
}
