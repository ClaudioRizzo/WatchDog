package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.crypto.SharedSecretAgreement;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * In questa classe verranno richiamate tutte le azioni da compiere per ottenere le chiavi necessarie a crittare
 * e a firmare un messaggio. In particolare le azioni che coinvolgono scambi di dati tra due telefoni (ECDH
 * fondamentalmente) avverranno su un canale diverso da quello su cui vengono scambiati i messaggi di controllo
 * remoto.
 * 
 * @author emanuele
 *
 */
public class SMSKeyManagement extends BroadcastReceiver{
	
	private SharedSecretAgreement ssa;
	private boolean iAmTheSender;
	private byte[] message;
	private SmsManager manager;
	private String other; //numero di telefono dell'altro utente
	private PublicKey mPub;
	private byte[] sharedSecret;
	
	
	public byte[] getSharedSecret(){
		return this.sharedSecret;
	}
	
	
	/**
	 * Questo costruttore è utilizzato dal lato A (chi inizia lo scambio)
	 * @param mPriv : la chiave privata di chi inizia lo scambio di chiavi.
	 */
	public SMSKeyManagement(PrivateKey mPriv, PublicKey mPub, String other){
		this.ssa = new SharedSecretAgreement(mPriv, null);
		this.iAmTheSender = true;
		this.manager = SmsManager.getDefault();
		this.other = other;
		this.mPub = mPub;
	}
	
	/**
	 * Questo costruttore è utilizzato dal lato B (chi riceve una richiesta di scambio) ed è usato
	 * automaticamente da android.
	 */
	public SMSKeyManagement(){
		this.iAmTheSender = false;
		this.manager = SmsManager.getDefault();
	}
	
	/*public SMSKeyManagement(PrivateKey mPriv, Key tokenReceivedFromOther){
		this.ssa = new SharedSecretAgreement(mPriv, tokenReceivedFromOther);
	}*/
	
	public String getStoredPasswordHash(){
		//TODO accedere a file e prelevare l'hash
		return null;
	}
	
	/**
	 * Utilizato dal lato A per far partire ECDH inviando a B la propria chiave pubblica.
	 */
	public void initiateECDHExchange(){
		this.manager.sendDataMessage(this.other, null, (short) 9999, this.mPub.getEncoded(), null, null);
		this.iAmTheSender = true;
	}
	
	/**
	 * Utilizzato dal lato B (quando riceve il messaggio di A) per creare il proprio segreto condiviso e
	 * mandare ad A la propria chiave pubblica affinché anche A possa generare il proprio segreto condiviso
	 * (che sarà ovviamente uguale a quello di B)
	 * 
	 * @param token : il contenuto del messaggio --> chiave pubblica di A
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 */
	private void respondToInitiatedECDHExchange(PublicKey token, String other) throws InvalidKeyException, NoSuchAlgorithmException{
		this.other = other;
		this.manager.sendDataMessage(this.other, null, (short)9999, getMyPublicKey(), null, null);
		PrivateKey mPriv = getMyPrivateKey(); //from file
		this.ssa = new SharedSecretAgreement(mPriv, token);
		this.sharedSecret = ssa.generateSharedSecret();
		//a questo punto io (che sono colui che ha ricevuto la richiesta di ECDH) posso lanciare l'algoritmo che
		//genera la chiave a partire da secret. L'altro farà lo stesso dopo aver generato il suo segrato a
		//partire dal token che gli ho appena inviato.
	}
	
	/**
	 * TODO: usato da B per recuperare la propria chiave pubblica (da mandare ad A in risposta al suo messaggio).
	 * @return
	 */
	private byte[] getMyPublicKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * TODO: usato da B per recuperare la propria chiave privata (da usare per generare il proprio segreto
	 * condiviso insieme al token appena ricevuto da A).
	 * @return
	 */
	private PrivateKey getMyPrivateKey() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Utilizzato da A per generare il proprio segreto condiviso a partire dalla chiave pubblica di B appena
	 * ricevuta per messaggio (notare che B prima di inviarla ha già creato il suo segreto condiviso)
	 * 
	 * @param token : il contenuto del messaggio
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 */
	private void manageReceivedToken(PublicKey token) throws InvalidKeyException, NoSuchAlgorithmException{
		this.ssa.setTokenReceived(token);
		this.sharedSecret = ssa.generateSharedSecret();
		//a questo punto io (che sono colui che ha iniziato la richiesta di ECDH) posso lanciare l'algoritmo che
		//genera la chiave a partire da secret. L'altro avrà già fatto lo stesso prima di inviarmi il suo token.
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		final Bundle bundle = intent.getExtras();
		
		try {
			if(bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				for(int i=0; i<pdusObj.length; i++) {
					SmsMessage receivedMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
					this.other = receivedMessage.getDisplayOriginatingAddress();
					//String message = new String(receivedMessage.getUserData());
					this.message = receivedMessage.getUserData();
					//Log.i("[SmsReceiver]", this.sender +" "+ message);
				}
				if(this.iAmTheSender){
					manageReceivedToken(KeyFactory.getInstance("ECDH").generatePublic(new X509EncodedKeySpec(this.message)));
				}
				else{
					respondToInitiatedECDHExchange(KeyFactory.getInstance("ECDH").generatePublic(new X509EncodedKeySpec(this.message)),this.other);
				}
			}
		}catch(Exception e) {Log.e("SmsReceiver", e.toString());}
		
	}

}
