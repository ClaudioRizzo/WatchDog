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
public class PublicKeySentCodeMessage extends ParsableSMS implements SocialistMillionaireMessageInterface{

	public PublicKeySentCodeMessage(String header, String body) {
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
	public void validate(String otherNumber, Context ctx) throws MessageWillBeIgnoredException {
		// la richiesta va accettata solo se in smp_status non è segnato che ne
		// ho già ricevuta una
		// e se è segnato che ne ho richiesta una.
		String publicKeyReceivedKey = otherNumber + MyPrefFiles.PUB_KEY_RECEIVED;
		String publicKeyRequestForwardedKey = otherNumber + MyPrefFiles.PUB_KEY_REQUEST_FORWARDED;
		if (MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, publicKeyReceivedKey, ctx) || !MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, publicKeyRequestForwardedKey, ctx)) {
			Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_2 REJECTED");
			throw new MessageWillBeIgnoredException();
		}
		Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_2 ACCEPTED");
	}
}
