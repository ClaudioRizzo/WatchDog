package it.polimi.dima.watchdog.sms.socialistMillionaire;

public class KeyValidatedCodeMessage extends SMSProtocol {

	public KeyValidatedCodeMessage(String header, String body) {
		super(header, body);
		
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

}
