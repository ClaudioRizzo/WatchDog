package it.polimi.dima.watchdog.sms.socialistMillionaire;

public class PublicKeySentCodeMessage extends SMSProtocol {

	public PublicKeySentCodeMessage(String header, String body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);
		
	}

}
