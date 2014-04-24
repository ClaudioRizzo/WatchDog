package it.polimi.dima.watchdog.sms.socialistMillionare;

public class SecretQuestionSentCodeMessage extends SMSProtocol {

	public SecretQuestionSentCodeMessage(String header, String body) {
		super(header, body);

	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

}
