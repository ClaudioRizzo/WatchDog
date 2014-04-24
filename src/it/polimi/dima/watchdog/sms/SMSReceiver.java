package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.MyPrefFiles;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
//import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;

public class SMSReceiver extends BroadcastReceiver{
	
	private String sender; //sarà il numero di telefono del mittente
	private byte[] message; //sarà il messaggio crittografato ricevuto
	private SMSParser parser;
	private Context ctx;
	
	public String getSender(){
		return this.sender;
	}
	
	public byte[] getMessage(){
		return this.message;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		this.ctx = context;
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
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.PASSWORD_AND_SALT, Context.MODE_PRIVATE);
		//SharedPreferences.Editor editor = sp.edit();
		
		String passwordHash = sp.getString(MyPrefFiles.MY_PASSWORD_HASH, null);
		return Base64.decode(passwordHash, Base64.DEFAULT);
	}

	/**
	 * Ritorna la chiave pubblica del mittente prelevata da un file.
	 * @param sender : il mittente del messaggio (sarà usato come chiave per trovare la corrispondente pubKey
	 * @return la chiave pubblica del mittente
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 */
	private PublicKey getOtherPublicKey(String sender) throws InvalidKeySpecException, NoSuchAlgorithmException {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.KEYRING, Context.MODE_PRIVATE);
		
		byte[] publicKey = Base64.decode(sp.getString(this.sender, null), Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance("EC");
		return kf.generatePublic(new X509EncodedKeySpec(publicKey));
	}

	/**
	 * Ritorna la chiave di decrittazione dell'AES prelevata da un file.
	 * @param sender : il mittente del messaggio (sarà usato come chiave per trovare la corrispondente chiave,
	 * perchè per ogni messaggio è preceduto da un altro messaggio su un'altra porta che comunica la necessità
	 * di effettuare nell'ordine ECDH e generazione della chiave a partire dal segreto condiviso; la chiave così
	 * generata viene messa in corrispondenza biunivoca con il mittente del messaggio, almeno fino ad una nuova
	 * richiesta di generazione chiave da parte dello stesso).
	 * @return
	 * @throws NoSuchAlgorithmException 
	 */
	private Key getAESKey(String sender) throws NoSuchAlgorithmException {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.CURRENT_AES_KEY, Context.MODE_PRIVATE);
		
		byte[] key = Base64.decode(sp.getString(sender, null), Base64.DEFAULT);
		return new SecretKeySpec(key, "AES");
	}

}
