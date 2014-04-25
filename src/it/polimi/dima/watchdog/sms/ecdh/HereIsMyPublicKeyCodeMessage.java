package it.polimi.dima.watchdog.sms.ecdh;

import it.polimi.dima.watchdog.sms.socialistMillionare.SMSProtocol;

public class HereIsMyPublicKeyCodeMessage extends SMSProtocol {

	public HereIsMyPublicKeyCodeMessage(String header, String body) {
		super(header, body);
	}
	
	@Override
	public void handle(SMSKeyExchangeVisitorInterface visitor) {
		visitor.visit(this);

	}

}
