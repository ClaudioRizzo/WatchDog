package it.polimi.dima.watchdog.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
//import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver{
	
	private String sender; //sarà il numero di telefono del mittente
	private byte[] message; //sarà il messaggio crittografato ricevuto
	
	public String getSender(){
		return this.sender;
	}
	
	public byte[] getMessage(){
		return this.message;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		//final SmsManager man = SmsManager.getDefault();
		final Bundle bundle = intent.getExtras();
		
		try {
			if(bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				for(int i=0; i<pdusObj.length; i++) {
					SmsMessage receivedMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					this.sender = receivedMessage.getDisplayOriginatingAddress();
					String message = new String(receivedMessage.getUserData());
					this.message = receivedMessage.getUserData();
					Log.i("[SmsReceiver]", sender +" "+ message);
					
					
				}
			}
		}catch(Exception e) {Log.e("SmsReceiver", e.toString());}
	}

}
