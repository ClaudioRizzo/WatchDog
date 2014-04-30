package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;
import android.content.Context;
import android.telephony.SmsMessage;

//ho mandato m1, mi aspetto m2, lo parso, invio m3 e passo a m3_sent
public class StatusM1Sent implements CommandProtocolFlagsReactionInterface {

	public static String CURRENT_STATUS = "m1_sent";
	public static String STATUS_RECEIVED = "m2_received";
	public static String NEXT_SENT_STATUS = StatusM3Sent.CURRENT_STATUS;//TODO da correggere

	@Override
	public SMSProtocol parse(Context context, SmsMessage message, String other)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getCurrentStatus() {
		return StatusM1Sent.CURRENT_STATUS;
	}

	

}
