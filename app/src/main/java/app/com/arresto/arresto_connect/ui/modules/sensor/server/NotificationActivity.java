package app.com.arresto.arresto_connect.ui.modules.sensor.server;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import app.com.arresto.arresto_connect.ui.activity.HomeActivity;

public class NotificationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // If this activity is the root activity of the task, the app is not running
        if (isTaskRoot()) {
            // Start the app before finishing
            final Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtras(getIntent().getExtras()); // copy all extras
            startActivity(intent);
//			final Intent parentIntent = new Intent(this, FeaturesActivity.class);
//			parentIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			final Intent startAppIntent = new Intent(this, DfuActivity.class);
//			startAppIntent.putExtras(getIntent().getExtras());
//			startActivities(new Intent[] { parentIntent, startAppIntent });
        }

        // Now finish, which will drop the user in to the activity that was at the top
        //  of the task stack
        finish();
    }
}