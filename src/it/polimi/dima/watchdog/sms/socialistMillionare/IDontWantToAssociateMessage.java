package it.polimi.dima.watchdog.sms.socialistMillionare;

public class IDontWantToAssociateMessage extends SMSProtocol implements
		SocialistMillionareMessageInterface {

	public IDontWantToAssociateMessage(byte[] header, byte[] body) {
		super(header, body);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		// TODO Auto-generated method stub

	}

}
