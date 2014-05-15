package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import android.content.Context;

public class IDontWantToAssociateCodeMessage extends ParsableSMS {

	public IDontWantToAssociateCodeMessage(String header, String body) {
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
		//se SMP è stato completato con successo, allora questo messaggio è un falso o un errore
		//e quindi va ignorato
		if(MyPrefFiles.isSmpSuccessfullyFinishedByBoth(otherNumber, ctx)){
			throw new MessageWillBeIgnoredException();
		}

	}

}
