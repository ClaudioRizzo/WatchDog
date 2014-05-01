package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;
import android.content.Context;
import android.telephony.SmsMessage;

/**
 * 
 * @author emanuele
 *
 */
//Ho mandato m3, mi aspetto m4 e dopo averlo parsato passo a free
public class StatusM3Sent implements CommandProtocolFlagsReactionInterface {

	public static String CURRENT_STATUS = "m3_sent";
	private static String STATUS_RECEIVED = "m4_received";
	public static String NEXT_SENT_STATUS = StatusFree.CURRENT_STATUS;

	@Override
	public SMSProtocol parse(Context context, SmsMessage message, String other)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentStatus() {
		return StatusM3Sent.CURRENT_STATUS;
	}

	@Override
	public String getNextSentStatus() {
		return StatusM3Sent.NEXT_SENT_STATUS;
	}
}