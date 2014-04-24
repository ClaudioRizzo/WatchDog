package it.polimi.dima.watchdog.sms.socialistMillionare;

public class PublicKeySentCodeMessage extends SMSProtocol implements SocialistMillionareMessageInterface {

	public PublicKeySentCodeMessage(byte[] header, byte[] body) {
		super(header, body);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		// TODO Auto-generated method stub
		
	}

}
