package it.polimi.dima.watchdog.sms.socialistMillionare;

/**
 * This Interface is needed to implement the visitor pattern in the SMP with reference to the exchanging message part.
 * @author claudio
 *
 */
public interface SocialistMillionareMessageInterface {

	public void handle(SMSPublicKeyVisitorInterface visitor);
	
}
