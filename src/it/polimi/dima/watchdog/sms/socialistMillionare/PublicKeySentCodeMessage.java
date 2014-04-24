package it.polimi.dima.watchdog.sms.socialistMillionare;

public class PublicKeySentCodeMessage extends SMSProtocol {

	public PublicKeySentCodeMessage(String header, String body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);
		
	}

}
