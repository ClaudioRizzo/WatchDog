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
public class SecretQuestionSentCodeMessage extends ParsableSMS implements SocialistMillionaireMessageInterface{

	public SecretQuestionSentCodeMessage(String header, String body) {
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
		// la richiesta va accettata solo se in smp_status non è segnato che ne ho già ricevuta una
		// e se è segnato che ho inviato la mia chiave pubblica all'altro
		String secretQuestionReceivedKey = otherNumber + MyPrefFiles.SECRET_QUESTION_RECEIVED;
		String publicKeyForwardedKey = otherNumber + MyPrefFiles.PUB_KEY_FORWARDED;
		if (MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, secretQuestionReceivedKey, ctx) || !MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, publicKeyForwardedKey, ctx)) {
			Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_3 REJECTED");
			throw new MessageWillBeIgnoredException();
		}
		Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_3 ACCEPTED");
	}

}