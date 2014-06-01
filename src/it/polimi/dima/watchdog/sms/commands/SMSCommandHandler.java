package it.polimi.dima.watchdog.sms.commands;

import java.util.HashMap;
import java.util.Map;

import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.commands.flags.CommandProtocolFlagsReactionInterface;
import it.polimi.dima.watchdog.sms.commands.flags.StatusFree;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM1Sent;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM2Sent;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM3Sent;
import it.polimi.dima.watchdog.sms.timeout.TimeoutWrapper;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
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
public class SMSCommandHandler extends BroadcastReceiver implements SMSCommandVisitorInterface{
	
	private ParsableSMS recMsg;
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
		try {
			final Object[] pdusObj = (Object[]) bundle.get(SMSUtility.SMS_EXTRA_NAME);
			SmsMessage message = null;	
			
			for (int i = 0; i < pdusObj.length; i++) {
				message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			}
			this.other = message.getDisplayOriginatingAddress();
			String myContext = getMyContext(this.ctx);
			
			if(this.statusMap.containsKey(this.statusMap.get(myContext))){
				this.recMsg = this.statusMap.get(myContext).parse(this.ctx, message, this.other);
				if(this.recMsg != null){//accade solo se devo parsare il comando di m3
					this.recMsg.handle(this);
				}
				//dopo aver inviato il messaggio setto come status il fatto che ho appena inviato quel messaggio (o free)
				MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, this.statusMap.get(myContext).getNextSentStatus(), this.ctx);
					
				//se non sono in status free (e lo sono solo se ho ricevuto m3 o m4) faccio partire il timeout
				if(!MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, this.ctx).equals(StatusFree.CURRENT_STATUS)){
					TimeoutWrapper.addTimeout(SMSUtility.MY_PHONE, this.other, this.ctx);						
				}
			} else {
				Log.i("[DEBUG]", "[DEBUG-onReceive command] non succede niente: mappa a puttane");
			}
			//altrimenti si ignora il messaggio		
		} 
		catch (Exception e) 
		{
			SMSUtility.handleErrorOrExceptionInCommandSession(e, this.other, this.ctx);
		}
		
	}
	
	private String getMyContext(Context context) throws NoSuchPreferenceFoundException{
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context)){
			String myContext = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context);
			Log.i("[DEBUG]", "[DEBUG-GetMyContext] "+myContext);
			return myContext;
		}
		return null;
	}

	@Override
	public void visit(SirenOnCodeMessage sirenOnCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN ON RECEIVED");
	}

	@Override
	public void visit(SirenOffCodeMessage sirenOffCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN OFF RECEIVED");
	}

	@Override
	public void visit(MarkLostCodeMessage markLostCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK LOST RECEIVED");
	}

	@Override
	public void visit(MarkStolenCodeMessage markStolenCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK STOLEN RECEIVED");
	}

	@Override
	public void visit(MarkLostOrStolenCodeMessage markLostOrStolenCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK LOST OR STOLEN RECEIVED");
	}

	@Override
	public void visit(MarkFoundCodeMessage markFoundCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK FOUND RICEVUTO");
	}

	@Override
	public void visit(LocateCodeMessage locateCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] LOCATE RICEVUTO");
		
	}
	
	private void initStatusMap(){
		this.statusMap.put(StatusFree.CURRENT_STATUS, new StatusFree());
		this.statusMap.put(StatusM1Sent.CURRENT_STATUS, new StatusM1Sent());
		this.statusMap.put(StatusM2Sent.CURRENT_STATUS, new StatusM2Sent());
		this.statusMap.put(StatusM3Sent.CURRENT_STATUS, new StatusM3Sent());
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND]" + StatusFree.CURRENT_STATUS);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND]" + StatusM1Sent.CURRENT_STATUS);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND]" + StatusM2Sent.CURRENT_STATUS);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND]" + StatusM3Sent.CURRENT_STATUS);
	}
}