package it.polimi.dima.watchdog.sms.commands.flags;

import android.content.Context;
import android.telephony.SmsMessage;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;

//sono libero, ricevo m1, lo parso, mando m2 e passo a m2_sent
public class StatusFree implements CommandProtocolFlagsReactionInterface{
	
	public static String CURRENT_STATUS = "free";
	public static String STATUS_RECEIVED = "m1_received";
	public static String NEXT_SENT_STATUS = StatusM2Sent.CURRENT_STATUS;//TODO da correggere
	
	@Override
	public SMSProtocol parse(Context context, SmsMessage message, String other) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentStatus() {
		return StatusFree.CURRENT_STATUS;
	}

	
}
