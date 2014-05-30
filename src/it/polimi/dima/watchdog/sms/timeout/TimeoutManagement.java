package it.polimi.dima.watchdog.sms.timeout;

import it.polimi.dima.watchdog.sms.commands.flags.StatusFree;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TimeoutManagement extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//... cancello i riferimenti all'altro utente nella sessione di comando...
		MyPrefFiles.eraseCommandSession(intent.getStringExtra(SMSUtility.OTHER_PHONE), context);
		
		//... e torno in status free.
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + intent.getStringExtra(SMSUtility.OTHER_PHONE), StatusFree.CURRENT_STATUS, context);
		// TODO notificare il fragment di quello che è successo
	}
}