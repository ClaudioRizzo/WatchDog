package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import android.content.Context;
import android.util.Log;

public class KeyValidatedCodeMessage extends ParsableSMS {

	public KeyValidatedCodeMessage(String header, String body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

	/**
	 * Decide se accettare o no il messaggio.
	 */
	@Override
	public void validate(String otherNumber, Context ctx) throws MessageWillBeIgnoredException{
		// la richiesta va accettata solo se in smp_status non è segnato che ne ho già ricevuta una
		// e se è segnato che ho inviato l'hash
		String key = otherNumber + MyPrefFiles.ACK_AND_SALT_RECEIVED;
		String key2 = otherNumber + MyPrefFiles.HASH_FORWARDED;
		if (MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, key, ctx) || !MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, key2, ctx)) {
			Log.i("[DEBUG_SMP]", "CODE_5 REJECTED");
			throw new MessageWillBeIgnoredException();
		}
		Log.i("[DEBUG_SMP]", "CODE_5 ACCEPTED");
	}

}
