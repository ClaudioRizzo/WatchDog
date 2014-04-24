package it.polimi.dima.watchdog.sms.socialistMillionare;



/**
 * One of the message of the SMP.
 * This message is sent/received when a p_key exchange is attempted
 * @author claudio
 *
 */
public class PublicKeyRequestCodeMessage extends SMSProtocol implements SocialistMillionareMessageInterface {

	public PublicKeyRequestCodeMessage(byte[] header, byte[] body) {
		super(header, body);
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);
	}
	
	
	

}
