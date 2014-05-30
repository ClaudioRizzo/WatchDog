package it.polimi.dima.watchdog.sms.commands;

import it.polimi.dima.watchdog.sms.ParsableSMS;

/**
 * 
 * @author emanuele
 *
 */
public class LocateCodeMessage extends ParsableSMS {

	public LocateCodeMessage(String header, String body) {
		super(header, body);
	}
	
	@Override
	public void handle(SMSCommandVisitorInterface visitor) {
		visitor.visit(this);
	}
}
