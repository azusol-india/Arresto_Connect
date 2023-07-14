package app.com.arresto.arresto_connect.ui.modules.sensor.server;

import android.app.Activity;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {

	@Override
	protected Class<? extends Activity> getNotificationTarget() {
		return NotificationActivity.class;
	}

}