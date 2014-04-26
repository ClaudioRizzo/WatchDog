package it.polimi.dima.watchdog.sms.ecdh;

import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;

/**
 * 
 * @author emanuele
 *
 */
public class HereIsMyPublicKeyTooCodeMessage extends SMSProtocol {

	public HereIsMyPublicKeyTooCodeMessage(String header, String body) {
		super(header, body);
	}
	
	@Override
	public void handle(SMSKeyExchangeVisitorInterface visitor) {
		visitor.visit(this);

	}

}
