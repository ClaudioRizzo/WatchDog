package it.polimi.dima.watchdog.sms.commands;

import it.polimi.dima.watchdog.sms.socialistMillionare.SMSProtocol;

/**
 * 
 * @author emanuele
 *
 */
public class LocateCodeMessage extends SMSProtocol {

	public LocateCodeMessage(String header, String body) {
		super(header, body);
	}
	
	@Override
	public void handle(SMSCommandVisitorInterface visitor) {
		visitor.visit(this);

	}

}
