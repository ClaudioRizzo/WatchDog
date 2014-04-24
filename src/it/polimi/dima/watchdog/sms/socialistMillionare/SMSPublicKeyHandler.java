package it.polimi.dima.watchdog.sms.socialistMillionare;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
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
	private SmsMessage message;
	public String other; //a turno sarà sender o receiver
	
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
					this.message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}
				this.other = this.message.getDisplayOriginatingAddress();
				this.recMsg.setHeader(getHeader(this.message.getUserData()));
				this.recMsg.setBody(getBody(this.message.getUserData()));
			}
			
			
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

}
