package it.polimi.dima.watchdog.sms.socialistMillionaire;

import android.content.Context;
import android.util.Log;

public class SecretQuestionSentCodeMessage extends SMSProtocol {

	public SecretQuestionSentCodeMessage(String header, String body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

	@Override
	public void validate(String otherNumber, Context ctx) {
		// se mi arriva un messaggio con una domanda segreta lascio all'utente
		// la libera scelta di cosa fare,
		// chiunque sia il mittente: sia inviare l'hash, sia rifiutare non porta
		// a nessun problema.
		// Loggo il messaggio giusto per debug
		Log.i("[DEBUG]", "Mi è arrivata una domanda segreta...");
		Log.i("[DEBUG]", "...sono arrivato al punto critico");

	}

}
