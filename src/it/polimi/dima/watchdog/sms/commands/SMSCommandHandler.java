package it.polimi.dima.watchdog.sms.commands;

import java.util.HashMap;
import java.util.Map;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.commands.flags.CommandProtocolFlagsReactionInterface;
import it.polimi.dima.watchdog.sms.commands.flags.StatusFree;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM1Sent;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM2Sent;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM3Sent;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;
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

	private SMSProtocol recMsg;
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
						MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, context);
						MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, this.statusMap.get(myContext).getNextSentStatus(), context);
						//TODO far partire il timeout solo se il messaggio ricevuto non è m3 o m4
					}
					else{
						//si ignora il messaggio
					}
					
				}
				catch(Exception e)
				{
					//TODO: se arriva un timeout smettere immediatamente quello che si stava facendo e
					//chiamare manageTimeout()
					MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, context);
					MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, this.statusMap.get(myContext).getCurrentStatus(), context);
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
	
	private void manageTimeoutA(Context ctx){
		MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, ctx);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, StatusFree.CURRENT_STATUS, ctx);
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, this.other + MyPrefFiles.TEMP_COMMAND, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, this.other + MyPrefFiles.TEMP_COMMAND, ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, this.other + MyPrefFiles.OTHER_PASSWORD, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, this.other + MyPrefFiles.OTHER_PASSWORD, ctx);
		}
	}
	
	private void manageTimeoutB(Context ctx){
		MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, ctx);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, StatusFree.CURRENT_STATUS, ctx);
	}
	
	private void initStatusMap(){
		this.statusMap.put(StatusFree.CURRENT_STATUS, new StatusFree());
		this.statusMap.put(StatusM1Sent.CURRENT_STATUS, new StatusM1Sent());
		this.statusMap.put(StatusM2Sent.CURRENT_STATUS, new StatusM2Sent());
		this.statusMap.put(StatusM3Sent.CURRENT_STATUS, new StatusM3Sent());
	}

}
