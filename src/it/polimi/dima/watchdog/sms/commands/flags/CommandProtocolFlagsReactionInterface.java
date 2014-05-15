package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.sms.ParsableSMS;
import android.content.Context;
import android.telephony.SmsMessage;

/**
 * 
 * @author emanuele
 *
 */
public interface CommandProtocolFlagsReactionInterface {
	
	public abstract String getCurrentStatus();
	public abstract String getNextSentStatus();
	public ParsableSMS parse(Context context, SmsMessage message, String other) throws Exception;

}
