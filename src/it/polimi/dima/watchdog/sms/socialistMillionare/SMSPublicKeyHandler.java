package it.polimi.dima.watchdog.sms.socialistMillionare;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.socialistMillionare.factory.SocialistMillionaireFactory;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;

/**
 * This class handles the messagges by using the visitor patter which is upon the messages. Should extend BroadCastReceiver
 * @author claudio
 *
 */
public class SMSPublicKeyHandler extends BroadcastReceiver implements SMSPublicKeyVisitorInterface {

	//TODO: aggiungere tutti gli altri messaggi possibili al visitor
	
	private SMSProtocol recMsg;
	private String other; //a turno sarà sender o receiver
	private SocialistMillionaireFactory mSocMilFactory;
	private PublicKeyAutenticator pka;
	private Context ctx;
	
	
	
	
	public SMSPublicKeyHandler() {
		this.mSocMilFactory = new SocialistMillionaireFactory();
		this.pka = new PublicKeyAutenticator();
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.ctx = context;
		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				SmsMessage message = null;
				
				for (int i = 0; i < pdusObj.length; i++) {
					message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}
				
				this.other = message.getDisplayOriginatingAddress();
				this.recMsg = mSocMilFactory.getMessage(getHeader(message.getUserData()));
				this.recMsg.setBody(getBody(message.getUserData()));
				this.recMsg.handle(this);
			}
			
			
		} catch (Exception e) {
			Log.e("SmsReceiver", e.toString());
		}
		
	}
	
	@Override
	public void visit(PublicKeyRequestCodeMessage pubKeyReqMsg) {
		
		
	}

	@Override
	public void visit(PublicKeySentCodeMessage pubKeySentMsg) {
		
		try {
			this.pka.setSecretQuestion(MyPrefFiles.getMyPreference(MyPrefFiles.SECRET_Q_A, MyPrefFiles.SECRET_QUESTION, this.ctx));
			MyPrefFiles.setMyPreference(MyPrefFiles.KEYSQUARE, this.other, Base64.encodeToString(pubKeySentMsg.getBody(), Base64.DEFAULT), ctx);
			sendMessage(this.other, (short) 999, this.pka.getSecretQuestion().getBytes());
		} catch (NoSuchPreferenceFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
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

	
	/**
	 * Ritorna l'header del messaggio
	 * @param msg
	 * @return
	 * @throws ArbitraryMessageReceivedException
	 */
	private byte[] getHeader(byte[] msg) throws ArbitraryMessageReceivedException {
		
		//-1 è perchè c'è anche il terminatore
		if(msg.length - 1 < SMSProtocol.HEADER_LENGTH){
			throw new ArbitraryMessageReceivedException("Messaggio inaspettato: troppo corto!!!");
		}
		byte[] header = new byte[SMSProtocol.HEADER_LENGTH];
		System.arraycopy(msg, 0, header, 0, SMSProtocol.HEADER_LENGTH);
		return header;
		
	}
	
	private byte[] getBody(byte[] msg) throws ArbitraryMessageReceivedException {
		
		if(msg.length - 1 < SMSProtocol.HEADER_LENGTH){
			throw new ArbitraryMessageReceivedException("Messaggio inaspettato: troppo corto!!!");
		}
		//android pare (dalla javadoc) non aggiungere un terminatore all'array di byte quando si usa getUserData(). In caso contrario la lunghezza
		//va decurtata di 1.
		//Il -1 che c'è già serve a tenere conto del terminatore nullo dell'header.
		int bodyLength = msg.length - SMSProtocol.HEADER_LENGTH -1;
		byte[] body = new byte[bodyLength];
		//+1 perchè non voglio copiare il terminatore dell'header
		System.arraycopy(msg, SMSProtocol.HEADER_LENGTH + 1, body, 0, bodyLength);
		return body;
		
	}
	
	private void sendMessage(String number, short port, byte[] data) {
		SmsManager man = SmsManager.getDefault();
		man.sendDataMessage(number, null, port, data, null, null);
	}

}
