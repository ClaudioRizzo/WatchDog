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
public class SecretAnswerAndPublicKeyHashSentCodeMessage extends ParsableSMS implements SocialistMillionaireMessageInterface{

	public SecretAnswerAndPublicKeyHashSentCodeMessage(String header, String body) {
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
		// e se è segnato che ho inviato la domanda segreta
		String hashReceivedKey = otherNumber + MyPrefFiles.HASH_RECEIVED;
		String secretQuestionForwardedKey = otherNumber + MyPrefFiles.SECRET_QUESTION_FORWARDED;
		if (MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, hashReceivedKey, ctx) || !MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, secretQuestionForwardedKey, ctx)) {
			Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_4 REJECTED");
			throw new MessageWillBeIgnoredException();
		}
		Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_4 ACCEPTED");
	}
}