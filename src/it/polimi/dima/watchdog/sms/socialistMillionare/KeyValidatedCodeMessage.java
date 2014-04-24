package it.polimi.dima.watchdog.sms.socialistMillionare;

public class KeyValidatedCodeMessage extends SMSProtocol {

	public KeyValidatedCodeMessage(byte[] header, byte[] body) {
		super(header, body);
		
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		visitor.visit(this);

	}

}
