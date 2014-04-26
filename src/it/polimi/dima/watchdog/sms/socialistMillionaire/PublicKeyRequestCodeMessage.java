package it.polimi.dima.watchdog.sms.socialistMillionaire;



/**
 * One of the message of the SMP.
 * This message is sent/received when a p_key exchange is attempted
 * @author claudio
 *
 */
public class PublicKeyRequestCodeMessage extends SMSProtocol implements SocialistMillionaireMessageInterface {

	public PublicKeyRequestCodeMessage(String header, String body) {
		super(header, body);
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);
	}
	
	
	

}
