package it.polimi.dima.watchdog.sms.socialistMillionaire;

import it.polimi.dima.watchdog.MyPrefFiles;
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
	public void validate(String otherNumber, Context ctx) {
		// se la richiesta deriva da un telefono che compare già da qualche
		// parte nelle mie preferenze,
		// allora per qualche motivo il suo proprietario non ha più i miei dati,
		// quindi io devo cancellare
		// i suoi e ripartire da zero.
		MyPrefFiles.erasePreferences(otherNumber, ctx);

	}

}
