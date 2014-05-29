package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import android.content.Context;
import android.util.Log;

/**
 * 
 * @author emanuele
 *
 */
public class KeyValidatedCodeMessage extends ParsableSMS implements SocialistMillionaireMessageInterface{

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
		String ackAndSaltReceivedKey = otherNumber + MyPrefFiles.ACK_AND_SALT_RECEIVED;
		String hashForwardedKey = otherNumber + MyPrefFiles.HASH_FORWARDED;
		if (MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, ackAndSaltReceivedKey, ctx) || !MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, hashForwardedKey, ctx)) {
			Log.i("[DEBUG_SMP]", "CODE_5 REJECTED");
			throw new MessageWillBeIgnoredException();
		}
		Log.i("[DEBUG_SMP]", "CODE_5 ACCEPTED");
	}
}