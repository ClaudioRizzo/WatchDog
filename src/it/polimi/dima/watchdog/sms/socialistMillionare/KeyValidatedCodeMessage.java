package it.polimi.dima.watchdog.sms.socialistMillionare;

public class KeyValidatedCodeMessage extends SMSProtocol implements
		SocialistMillionareMessageInterface {

	public KeyValidatedCodeMessage(byte[] header, byte[] body) {
		super(header, body);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		// TODO Auto-generated method stub

	}

}
