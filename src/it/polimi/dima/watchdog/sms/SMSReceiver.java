package it.polimi.dima.watchdog.sms;

import java.security.Key;
import java.security.PublicKey;

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
	private SMSParser parser;
	
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
					Log.i("[SmsReceiver]", this.sender +" "+ message);
					
					
				}
				//La costruzione dell'oggetto dà automaticamente il via alla decrittazione e alla verifica di firma e password, nonchè alla
				//reazione al messaggio.
				}
			this.parser = new SMSParser(this.message, getAESKey(this.sender), getOtherPublicKey(this.sender), getStoredPasswordHash());
			this.parser.decrypt();
		}catch(Exception e) {Log.e("SmsReceiver", e.toString());}
	}

	/**
	 * Legge da file l'hash della password e lo ritorna.
	 * @return l'hash della password salvato.
	 */
	private byte[] getStoredPasswordHash() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Ritorna la chiave pubblica del mittente prelevata da un file.
	 * @param sender : il mittente del messaggio (sarà usato come chiave per trovare la corrispondente pubKey
	 * @return la chiave pubblica del mittente
	 */
	private PublicKey getOtherPublicKey(String sender) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Ritorna la chiave di decrittazione dell'AES prelevata da un file.
	 * @param sender : il mittente del messaggio (sarà usato come chiave per trovare la corrispondente chiave,
	 * perchè per ogni messaggio è preceduto da un altro messaggio su un'altra porta che comunica la necessità
	 * di effettuare nell'ordine ECDH e generazione della chiave a partire dal segreto condiviso; la chiave così
	 * generata viene messa in corrispondenza biunivoca con il mittente del messaggio, almeno fino ad una nuova
	 * richiesta di generazione chiave da parte dello stesso).
	 * @return
	 */
	private Key getAESKey(String sender) {
		// TODO Auto-generated method stub
		return null;
	}

}
