package it.polimi.dima.watchdog.sms.socialistMillionare;

public class SecretQuestionSentCodeMessage extends SMSProtocol {

	public SecretQuestionSentCodeMessage(byte[] header, byte[] body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

}
