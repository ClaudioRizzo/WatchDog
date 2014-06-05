package it.polimi.dima.watchdog.sms.timeout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeoutTest extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("[DEBUG]", "[DEBUG] timeout scattato!!!");
	}
}
