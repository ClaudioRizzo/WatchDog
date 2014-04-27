package it.polimi.dima.watchdog.sms.commands;

import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;

/**
 * 
 * @author emanuele
 *
 */
public class SirenOffCodeMessage extends SMSProtocol {

	public SirenOffCodeMessage(String header, String body) {
		super(header, body);
	}
	
	@Override
	public void handle(SMSCommandVisitorInterface visitor) {
		visitor.visit(this);

	}

}