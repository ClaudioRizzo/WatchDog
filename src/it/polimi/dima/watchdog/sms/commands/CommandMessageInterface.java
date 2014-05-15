package it.polimi.dima.watchdog.sms.commands;

import android.content.Context;

/**
 * 
 * @author emanuele
 *
 */
public interface CommandMessageInterface {
	public void handle(SMSCommandVisitorInterface visitor);
	public  void validate(String otherNumber, Context ctx) throws Exception;
}
