package it.polimi.dima.watchdog.sms.socialistMillionare;

public class SecretAnswerAndPublicKeyHashSentCodeMessage extends SMSProtocol {

	public SecretAnswerAndPublicKeyHashSentCodeMessage(byte[] header,
			byte[] body) {
		super(header, body);
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

}
