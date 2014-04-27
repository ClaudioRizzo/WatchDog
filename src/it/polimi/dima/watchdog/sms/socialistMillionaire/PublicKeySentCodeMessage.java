package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import android.content.Context;

public class PublicKeySentCodeMessage extends SMSProtocol {

	public PublicKeySentCodeMessage(String header, String body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

	@Override
	public void validate(String otherNumber, Context ctx) throws ArbitraryMessageReceivedException {
		// se mi arriva un messaggio del genere e ho qualche preferenza di quel
		// numero già salvata
		// devo bloccare tutto perchè è sicuramente un errore o un messaggio
		// falso, in quanto nessuno
		// deve inviare ad un altro la propria chiave pubblica se prima non è
		// arrivato un messaggio
		// di richiesta. Tale messaggio non può partire se chi lo manderebbe non
		// ha prima cancellato
		// tutte le preferenze che riguardano il destinatario.
		if (MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, otherNumber,
				ctx)
				|| MyPrefFiles.existsPreference(MyPrefFiles.KEYRING,
						otherNumber, ctx)
				|| MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS,
						otherNumber, ctx)
				|| MyPrefFiles.existsPreference(MyPrefFiles.HASHRING,
						otherNumber, ctx)) {
			throw new ArbitraryMessageReceivedException(
					"Messaggio ricevuto da un numero già presente!!!");
		}

	}

}
