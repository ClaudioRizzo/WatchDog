package it.polimi.dima.watchdog.sms.timeout;

import it.polimi.dima.watchdog.sms.commands.flags.StatusFree;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Classe che wrappa la gestione di un timeout.
 * 
 * @author emanuele
 *
 */
public class TimeoutManagement extends BroadcastReceiver {

	/**
	 * Cancella nella sessione di comando i riferimenti a un numero di telefono contenuto in intent e setta
	 * il proprio stato come status free.
	 * 
	 * @param context : il contesto corrente
	 * @param intent : l'Intent che contiene il riferimento al numero di telefono dell'altro
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("[DEBUG]", "[DEBUG] timeout-scattato");
		
		//... cancello i riferimenti all'altro utente nella sessione di comando...
		MyPrefFiles.eraseCommandSession(intent.getStringExtra(SMSUtility.OTHER_PHONE), context);
		
		//... e torno in status free.
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + intent.getStringExtra(SMSUtility.OTHER_PHONE), StatusFree.CURRENT_STATUS, context);
		// TODO notificare il fragment di quello che è successo
	}
}