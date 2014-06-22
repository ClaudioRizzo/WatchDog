package it.polimi.dima.watchdog.sms.commands;

import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.sms.commands.flags.CommandProtocolFlagsReactionInterface;
import it.polimi.dima.watchdog.sms.commands.flags.StatusFree;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM1Sent;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM2Sent;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM3Sent;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.util.HashMap;
import java.util.Map;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 
 * @author emanuele
 *
 */
public class SMSCommandHandler extends BroadcastReceiver {
	
	private String other;
	private Map<String,CommandProtocolFlagsReactionInterface> statusMap = new HashMap<String,CommandProtocolFlagsReactionInterface>();
	private Context ctx;
	
	public SMSCommandHandler(){
		initStatusMap();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MESSAGGIO RICEVUTO");
		this.ctx = context;
		final Bundle bundle = intent.getExtras();
		String myContext = getMyContext(this.ctx);
		try {
			final Object[] pdusObj = (Object[]) bundle.get(SMSUtility.SMS_EXTRA_NAME);
			SmsMessage message = null;	
			
			for (int i = 0; i < pdusObj.length; i++) {
				message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			}
			this.other = message.getDisplayOriginatingAddress();
			
			if(this.statusMap.containsKey(myContext)) {
				this.statusMap.get(myContext).parse(this.ctx, message, this.other);
			}
			else{
				//altrimenti si ignora il messaggio
				Log.i("debug command","DEBUG COMMAND: messaggio ignorato");
			}	
		} 
		catch (Exception e) 
		{
			if(myContext.equals(StatusM1Sent.CURRENT_STATUS) || myContext.equals(StatusM1Sent.STATUS_RECEIVED) || myContext.equals(StatusM3Sent.CURRENT_STATUS) || myContext.equals(StatusM3Sent.STATUS_RECEIVED)){
				ErrorManager.handleErrorOrExceptionInCommandSession(e, this.other, this.ctx, false);
			}
			else{
				ErrorManager.handleErrorOrExceptionInCommandSession(e, this.other, this.ctx, true);
			}
		}
		
	}
	
	private String getMyContext(Context context) {
		try{
			if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context)){
				String myContext = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context);
				return myContext;
			}
			else{
				return null;
			}
		}
		catch(Exception e){
			return null;
		}
	}

	private void initStatusMap(){
		this.statusMap.put(StatusFree.CURRENT_STATUS, new StatusFree());
		this.statusMap.put(StatusM1Sent.CURRENT_STATUS, new StatusM1Sent());
		this.statusMap.put(StatusM2Sent.CURRENT_STATUS, new StatusM2Sent());
		this.statusMap.put(StatusM3Sent.CURRENT_STATUS, new StatusM3Sent());
	}
}