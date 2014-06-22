package it.polimi.dima.watchdog.sms.passwordReset;

import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;

public class PasswordResetHandler extends BroadcastReceiver {

	private String other;
	private Context context;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("[DEBUG]", "[DEBUG_PASSWORD_RESET] MESSAGGIO RICEVUTO");
		this.context = context;
		final Bundle bundle = intent.getExtras();
		try {
			final Object[] pdusObj = (Object[]) bundle.get(SMSUtility.SMS_EXTRA_NAME);
			SmsMessage message = null;	
			
			for (int i = 0; i < pdusObj.length; i++) {
				message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
			}
			this.other = message.getDisplayOriginatingAddress();
			
			PasswordResetMessageParser parser = new PasswordResetMessageParser(message.getUserData(), this.other, this.context);
			parser.parse();
			MyPrefFiles.setMyPreference(MyPrefFiles.HASHRING, this.other, Base64.encodeToString(parser.getSalt(), Base64.DEFAULT), this.context);
		} 
		catch (Exception e) 
		{
			//TODO necessario fare qualcosa? Forse no.
		}
	}

}