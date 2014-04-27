package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import android.content.Context;

public class KeyValidatedCodeMessage extends SMSProtocol {

	public KeyValidatedCodeMessage(String header, String body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

	@Override
	public void validate(String otherNumber, Context ctx) throws ArbitraryMessageReceivedException {
		// se ricevo questo messaggio e ho il sale della password dell'altro
		// utente già salvato, allora questo
		// messaggio è un falso o un errore, perchè se qualcuno mi invia un
		// sale, io devo aver già cancellato
		// quello vecchio e questo avviene solo se l'altro mi ha chiesto di
		// farlo perchè ha perso i miei dati
		// e vuole ripetere l'associazione o se io ho perso i dati dell'altro e
		// devo ripetere l'associazione.
		// In ogni caso quando l'altro mi dice che la mia chiave è validata, io
		// nonn posso avere il sale della
		// sua password salvato. Quindi questo messaggio verrà ignorato e non si
		// prosegue oltre.
		if (MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, otherNumber,
				ctx)) {
			throw new ArbitraryMessageReceivedException(
					"Il sale è già presente!!!");
		}

	}

}
