package it.polimi.dima.watchdog.sms.commands;

import it.polimi.dima.watchdog.sms.socialistMillionare.SMSProtocol;

/**
 * 
 * @author emanuele
 *
 */
public class MarkLostOrStolenCodeMessage extends SMSProtocol {

	public MarkLostOrStolenCodeMessage(String header, String body) {
		super(header, body);
	}
	
	@Override
	public void handle(SMSCommandVisitorInterface visitor) {
		visitor.visit(this);

	}

}
