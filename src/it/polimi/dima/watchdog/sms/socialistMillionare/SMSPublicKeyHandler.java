package it.polimi.dima.watchdog.sms.socialistMillionare;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * This class handles the messagges by using the visitor patter which is upon the messages. Should extend BroadCastReceiver
 * @author claudio
 *
 */
public class SMSPublicKeyHandler extends BroadcastReceiver implements SMSPublicKeyVisitorInterface {

	//TODO: aggiungere tutti gli altri messaggi possibili al visitor
	
	private SMSProtocol recMsg;
	
	public SMSPublicKeyHandler() {
		this.recMsg = new SMSProtocol(null, null);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				for (int i = 0; i < pdusObj.length; i++) {
					SmsMessage receivedMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					

				}
			}
			//manageReceivedMessage();
			
		} catch (Exception e) {
			Log.e("SmsReceiver", e.toString());
		}
		
	}
	
	@Override
	public void visit(PublicKeyRequestCodeMessage pubKeyReqMsg) {
		//TODO: gestione messaggio richiesta chiave pubblica
		
	}

	@Override
	public void visit(PublicKeySentCodeMessage pubKeySentMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SecretAnswerAndPublicKeyHashSentCodeMessage secAnswMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SecretQuestionSentCodeMessage secQuestMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(KeyValidatedCodeMessage keyValMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IDontWantToAssociateMessage noAssMsg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visitCollection(SocialistMillionareMessageCollection collection) {
		// TODO Auto-generated method stub
		
	}
	
	private byte[] getHeader(byte[] msg) {
		
		if(msg.length < 1){}
		
		return null;
		
	}

}
