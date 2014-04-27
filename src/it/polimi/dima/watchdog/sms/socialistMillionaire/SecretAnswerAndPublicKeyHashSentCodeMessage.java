package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
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
	public void validate(String otherNumber, Context ctx) throws ArbitraryMessageReceivedException {
		// se ricevo un messaggio del genere da un numero non in attesa di
		// validazione devo ignorarlo
		// e non proseguire oltre.
		if (!MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, otherNumber,
				ctx)) {
			throw new ArbitraryMessageReceivedException(
					"Messaggio ricevuto da un numero non presente nel keysquare!!!");
		}

	}

}
