package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
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
	public void validate(String otherNumber, Context ctx) throws MessageWillBeIgnoredException {
		//se SMP è stato completato con successo, allora questo messaggio è un falso o un errore
		//e quindi va ignorato
		if(MyPrefFiles.isSmpSuccessfullyFinished(otherNumber, ctx)){
			throw new MessageWillBeIgnoredException();
		}

	}

}
