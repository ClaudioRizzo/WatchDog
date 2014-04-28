package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import android.content.Context;

public class SecretQuestionSentCodeMessage extends SMSProtocol {

	public SecretQuestionSentCodeMessage(String header, String body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

	@Override
	public void validate(String otherNumber, Context ctx) throws MessageWillBeIgnoredException {
		// la richiesta va accettata solo se in smp_status non è segnato che ne ho già ricevuta una
		// e se è segnato che ho inviato la mia chiave pubblica all'altro
		String key = otherNumber + MyPrefFiles.SECRET_QUESTION_RECEIVED;
		String key2 = otherNumber + MyPrefFiles.PUB_KEY_FORWARDED;
		if (MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, key, ctx) || !MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, key2, ctx)) {
			throw new MessageWillBeIgnoredException();
		}

	}

}
