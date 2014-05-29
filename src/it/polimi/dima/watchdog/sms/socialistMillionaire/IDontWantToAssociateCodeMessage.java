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
			Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_6 REJECTED");
			throw new MessageWillBeIgnoredException();
		}
		Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_6 ACCEPTED");
	}
}