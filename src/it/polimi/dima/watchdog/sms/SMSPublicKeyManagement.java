package it.polimi.dima.watchdog.sms;

import java.math.BigInteger;
import java.security.PublicKey;
import java.util.regex.Pattern;

import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;

public class SMSPublicKeyManagement extends BroadcastReceiver {

	private byte[] PublicKeyRequestCode = new BigInteger("0xC0DE1FFF").toByteArray();
	private byte[] PublicKeySentCode = new BigInteger("0xC0DE2FFF").toByteArray();
	private byte[] SecretQuestionSentCode = new BigInteger("0xC0DE3FFF").toByteArray();
	private byte[] SecretAnswerAndPublicKeyHashSentCode = new BigInteger("0xC0DE4FFF").toByteArray();
	
	//private boolean iAmTheSender;
	private PublicKeyAutenticator pka;
	private SmsManager manager;
	private String other;
	private byte[] message;

	
	public SMSPublicKeyManagement(PublicKey mPub, String secretQuestion, String secretAnswer, String other) {
		this.pka = new PublicKeyAutenticator(mPub.getEncoded(), secretQuestion, secretAnswer);
		this.other = other;
		this.manager = SmsManager.getDefault();
	}

	
	public SMSPublicKeyManagement() {
		this.manager = SmsManager.getDefault();
	}
	
	
	public void initiateSMP(){
		byte[] message = this.PublicKeyRequestCode;
		this.manager.sendDataMessage(this.other, null, (short)999, message, null, null);
	}
	

	@Override
	public void onReceive(Context context, Intent intent) {
		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				for (int i = 0; i < pdusObj.length; i++) {
					SmsMessage receivedMessage = SmsMessage
							.createFromPdu((byte[]) pdusObj[i]);
					this.other = receivedMessage
							.getDisplayOriginatingAddress();
					//String message = new String(receivedMessage.getUserData());
					this.message = receivedMessage.getUserData();
					//Log.i("[SmsReceiver]", this.other + " " + message);

				}
			}
			//constructClass();
			manageReceivedMessage();
			
		} catch (Exception e) {
			Log.e("SmsReceiver", e.toString());
		}

	}

	private void manageReceivedMessage() {
		if(this.message.equals(this.PublicKeyRequestCode)){
			this.pka = new PublicKeyAutenticator(getPublicKey(), getSecretQuestion(), getSecretAnswer());
			sendPublicKey();
		}
		else if(containsAPublicKey()){
			this.pka = new PublicKeyAutenticator(getPublicKey(), getSecretQuestion(), getSecretAnswer());
			this.pka.setReceivedPublicKey(splitPrefixAndPublicKey());
			//TODO send secret question
		}
		
	}

	private boolean containsAPublicKey() {
		byte[] prefix = new BigInteger("0xC0DE2FFF").toByteArray();
		String prefixBase64 = Base64.encodeToString(prefix, Base64.DEFAULT);
		String messageBase64 = Base64.encodeToString(this.message, Base64.DEFAULT);
		if(Pattern.matches(prefixBase64 + ".*", messageBase64)){
			return true;
		}
		return false;
	}


	private byte[] splitPrefixAndPublicKey() {
		//TODO
		return null;
	}


	/**
	 * Da file
	 * @return
	 */
	private String getSecretAnswer() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Da file
	 * @return
	 */
	private String getSecretQuestion() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Da file
	 * @return
	 */
	private byte[] getPublicKey() {
		// TODO Auto-generated method stub
		return null;
	}


	private void sendPublicKey() {
		this.manager.sendDataMessage(this.other, null, (short)999, this.pka.getMyPublicKey(), null, null);
	}


}
