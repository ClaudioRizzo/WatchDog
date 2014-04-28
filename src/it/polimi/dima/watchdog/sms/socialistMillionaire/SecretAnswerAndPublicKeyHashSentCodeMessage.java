package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import android.content.Context;

public class SecretAnswerAndPublicKeyHashSentCodeMessage extends SMSProtocol {

	public SecretAnswerAndPublicKeyHashSentCodeMessage(String header,
			String body) {
		super(header, body);
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

	@Override
	public void validate(String otherNumber, Context ctx) throws MessageWillBeIgnoredException {
		// la richiesta va accettata solo se in smp_status non è segnato che ne ho già ricevuta una
		// e se è segnato che ho inviato la domanda segreta
		String key = otherNumber + MyPrefFiles.HASH_RECEIVED;
		String key2 = otherNumber + MyPrefFiles.SECRET_QUESTION_FORWARDED;
		if (MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, key, ctx) || !MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, key2, ctx)) {
			throw new MessageWillBeIgnoredException();
		}

	}

}
