package it.polimi.dima.watchdog.sms.socialistMillionare;

public class SecretAnswerAndPublicKeyHashSentCodeMessage extends SMSProtocol
		implements SocialistMillionareMessageInterface {

	public SecretAnswerAndPublicKeyHashSentCodeMessage(byte[] header,
			byte[] body) {
		super(header, body);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		// TODO Auto-generated method stub

	}

}
