package it.polimi.dima.watchdog.sms.socialistMillionaire;

public class IDontWantToAssociateCodeMessage extends SMSProtocol {

	public IDontWantToAssociateCodeMessage(String header, String body) {
		super(header, body);
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);
	}

}
