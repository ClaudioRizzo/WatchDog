package it.polimi.dima.watchdog.sms.commands;

import java.util.HashMap;
import java.util.Map;

import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.UTILITIES.SMSUtility;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.commands.flags.CommandProtocolFlagsReactionInterface;
import it.polimi.dima.watchdog.sms.commands.flags.StatusFree;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM1Sent;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM2Sent;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM3Sent;
import it.polimi.dima.watchdog.sms.timeout.Timeout;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/**
 * 
 * @author emanuele
 *
 */
public class SMSCommandHandler extends BroadcastReceiver implements SMSCommandVisitorInterface{

	private ParsableSMS recMsg;
	private String other;
	private Map<String,CommandProtocolFlagsReactionInterface> statusMap = new HashMap<String,CommandProtocolFlagsReactionInterface>();
	
	
	public SMSCommandHandler(){
		initStatusMap();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get(SMSUtility.SMS_EXTRA_NAME);
				SmsMessage message = null;
				
				for (int i = 0; i < pdusObj.length; i++) {
					message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}
				this.other = message.getDisplayOriginatingAddress();
				String myContext = getMyContext(context);
				
				try{
					if(this.statusMap.containsKey(this.statusMap.get(myContext))){
						this.recMsg = this.statusMap.get(myContext).parse(context, message, this.other);
						if(this.recMsg != null){//accade solo se devo parsare il comando di m3
							this.recMsg.handle(this);
						}
						//dopo aver inviato il messaggio setto come status il fatto che ho appena inviato quel messaggio (o free)
						MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, this.statusMap.get(myContext).getNextSentStatus(), context);
						
						//se non sono in status free (e lo sono solo se ho ricevuto m3 o m4) faccio partire il timeout
						if(!MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context).equals(StatusFree.CURRENT_STATUS)){
							Timeout.getInstance(context).addTimeout(MyPrefFiles.getMyPreference(MyPrefFiles.MY_NUMBER_FILE, MyPrefFiles.MY_PHONE_NUMBER, context), myContext, SMSUtility.TIMEOUT_LENGTH);
						}
					}
					else{
						//si ignora il messaggio
					}
					
				}
				catch(Exception e)
				{
					MyPrefFiles.eraseCommandSession(this.other, context);
					MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, StatusFree.CURRENT_STATUS, context);
				}			
			}
			
			
		} 
		catch (NoSuchPreferenceFoundException e)
		{
			//TODO ignorare il messaggio (ovvero fare in modo che non venga "visitato")
		}
		catch (Exception e) 
		{
			//TODO ignorare il messaggio (ovvero fare in modo che non venga "visitato")
			e.printStackTrace();
		}
		
	}
	
	
	
	private String getMyContext(Context context) throws NoSuchPreferenceFoundException{
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context)){
			return MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context);
		}
		return null;
	}

	@Override
	public void visit(SirenOnCodeMessage sirenOnCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SirenOffCodeMessage sirenOffCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkLostCodeMessage markLostCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkStolenCodeMessage markStolenCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkLostOrStolenCodeMessage markLostOrStolenCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkFoundCodeMessage markFoundCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LocateCodeMessage locateCodeMessage) {
		// TODO Auto-generated method stub
		
	}
	
	private void initStatusMap(){
		this.statusMap.put(StatusFree.CURRENT_STATUS, new StatusFree());
		this.statusMap.put(StatusM1Sent.CURRENT_STATUS, new StatusM1Sent());
		this.statusMap.put(StatusM2Sent.CURRENT_STATUS, new StatusM2Sent());
		this.statusMap.put(StatusM3Sent.CURRENT_STATUS, new StatusM3Sent());
	}

}
