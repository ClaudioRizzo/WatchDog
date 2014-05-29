package it.polimi.dima.watchdog.sms.commands;

import it.polimi.dima.watchdog.sms.ParsableSMS;

/**
 * 
 * @author emanuele
 *
 */
public class MarkLostOrStolenCodeMessage extends ParsableSMS {

	public MarkLostOrStolenCodeMessage(String header, String body) {
		super(header, body);
	}
	
	@Override
	public void handle(SMSCommandVisitorInterface visitor) {
		visitor.visit(this);
	}
}
