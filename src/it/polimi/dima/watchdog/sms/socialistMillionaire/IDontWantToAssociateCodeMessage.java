package it.polimi.dima.watchdog.sms.socialistMillionaire;

import android.content.Context;

public class IDontWantToAssociateCodeMessage extends SMSProtocol {

	public IDontWantToAssociateCodeMessage(String header, String body) {
		super(header, body);
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);
	}

	@Override
	public void validate(String otherNumber, Context ctx) {
		// TODO problema: se un malintenzionato manda questo messaggio spoofando
		// il suo numero, verrebbero
		// potenzialmente cancellate le preferenze di un altro senza che
		// quest'ultimo lo sappia. Questo è
		// drammatico perchè l'ignaro non può sapere che io non posso più
		// inviargli comandi (e notificarlo è
		// impossibile --> ricorsione infinita)

	}

}
