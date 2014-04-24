package it.polimi.dima.watchdog.sms.socialistMillionare;

public class IDontWantToAssociateMessage extends SMSProtocol {

	public IDontWantToAssociateMessage(String header, String body) {
		super(header, body);
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);
	}

}
