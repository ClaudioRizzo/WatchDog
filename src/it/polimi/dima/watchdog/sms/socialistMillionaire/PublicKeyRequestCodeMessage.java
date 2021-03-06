package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import android.content.Context;
import android.util.Log;

/**
 * One of the message of the SMP. This message is sent/received when a p_key exchange is attempted.
 * 
 * @author claudio, emanuele
 * 
 */
public class PublicKeyRequestCodeMessage extends ParsableSMS implements SocialistMillionaireMessageInterface {

	public PublicKeyRequestCodeMessage(String header, String body) {
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
		//la richiesta va accettata solo se in smp_status non è segnato che ne ho già ricevuta una
		String publicKeyRequestReceivedKey = otherNumber + MyPrefFiles.PUB_KEY_REQUEST_RECEIVED;
		if(MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, publicKeyRequestReceivedKey, ctx)){
			Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_1 REJECTED");
			throw new MessageWillBeIgnoredException();
		}
		Log.i("[DEBUG_SMP]", "[DEBUG_SMP] CODE_1 ACCEPTED");
	}
}