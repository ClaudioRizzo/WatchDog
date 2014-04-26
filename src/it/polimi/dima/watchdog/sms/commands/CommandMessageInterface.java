package it.polimi.dima.watchdog.sms.commands;

/**
 * 
 * @author emanuele
 *
 */
public interface CommandMessageInterface {
	public void handle(SMSCommandVisitorInterface visitor);

}
