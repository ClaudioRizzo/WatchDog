package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import android.content.Context;

/**
 * One of the message of the SMP. This message is sent/received when a p_key
 * exchange is attempted
 * 
 * @author claudio
 * 
 */
public class PublicKeyRequestCodeMessage extends SMSProtocol implements
		SocialistMillionaireMessageInterface {

	public PublicKeyRequestCodeMessage(String header, String body) {
		super(header, body);
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);
	}

	@Override
	public void validate(String otherNumber, Context ctx) throws MessageWillBeIgnoredException {
		//la richiesta va accettata solo se in smp_status non è segnato che ne ho già ricevuta una
		String key = otherNumber + MyPrefFiles.PUB_KEY_REQUEST_RECEIVED;
		if(MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, key, ctx)){
			throw new MessageWillBeIgnoredException();
		}
	}

}
