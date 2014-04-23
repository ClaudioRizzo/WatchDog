package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.crypto.SharedSecretAgreement;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;

/**
 * In questa classe viene implementato ECDH tramite l'invio di sms.
 * 
 * @author emanuele
 * 
 */
public class SMSKeyManagement extends BroadcastReceiver {

	private byte[] hereIsMyPublicKey = new BigInteger("0xC0DE1FFF")
			.toByteArray();
	private byte[] hereIsMyPublicKeyToo = new BigInteger("0xC0DE2FFF")
			.toByteArray();

	private SharedSecretAgreement ssa;
	private SmsManager manager;
	private byte[] message;
	private String other;
	private PublicKey mPub;
	
	private Context ctx;

	/**
	 * A vuole iniziare ECDH con B: questo è il costruttore usato da A.
	 * 
	 * @param mPub
	 * @param other
	 */
	public SMSKeyManagement(PublicKey mPub, String other) {
		this.mPub = mPub;
		this.other = other;
		this.manager = SmsManager.getDefault();
	}

	/**
	 * Costruttore utilizzato da android ogni volta che arriva un messaggio.
	 */
	public SMSKeyManagement() {
		this.manager = SmsManager.getDefault();
	}

	/**
	 * Dovrà essere chiamato alla fine quando un altro oggetto vorrà generare la
	 * chiave a partire dal segreto condiviso.
	 * 
	 * @return
	 */
	public byte[] getSharedSecret() {
		return this.ssa.getSharedSecret();
	}

	/**
	 * Il primo metodo chiamato da A per iniziare ECDH.
	 */
	public void initiateECDH() {
		byte[] mPub = this.mPub.getEncoded();
		int headerSize = this.hereIsMyPublicKey.length;
		int bodySize = mPub.length;
		sendPublicKey(mPub, headerSize, bodySize);

	}

	/**
	 * Metodo che invia all'altro utente la propria chiave pubblica
	 * 
	 * @param key
	 * @param headerSize
	 * @param bodySize
	 */
	private void sendPublicKey(byte[] key, int headerSize, int bodySize) {
		byte[] message = new byte[headerSize + bodySize];

		System.arraycopy(this.hereIsMyPublicKey, 0, message, 0, headerSize);
		System.arraycopy(mPub, 0, message, headerSize, bodySize);

		this.manager.sendDataMessage(this.other, null, (short) 9999, message,
				null, null);
	}

	/**
	 * Metodo chiamato ogni volta che arriva un messaggio: salva messaggio e
	 * mittente e chiama il metodo che parsa il messaggio e agisce di
	 * conseguenza.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		this.ctx = context;
		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				for (int i = 0; i < pdusObj.length; i++) {
					SmsMessage receivedMessage = SmsMessage
							.createFromPdu((byte[]) pdusObj[i]);
					this.other = receivedMessage.getDisplayOriginatingAddress();
					this.message = receivedMessage.getUserData();
				}
			}
			manageReceivedMessage();
		} catch (Exception e) {
			Log.e("SmsReceiver", e.toString());
		}

	}

	/**
	 * Controlla l'header del messaggio e agisce di conseguenza.
	 * 
	 * @throws ArbitraryMessageReceivedException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private void manageReceivedMessage()
			throws ArbitraryMessageReceivedException, InvalidKeyException,
			NoSuchAlgorithmException, InvalidKeySpecException {
		if (receivedMessageStartsWith(this.hereIsMyPublicKey)) {
			this.ssa = new SharedSecretAgreement(getPrivateKey(),
					unwrapContent(this.hereIsMyPublicKey));
			this.ssa.generateSharedSecret();
			this.mPub = getPublicKey();
			sendPublicKey(mPub.getEncoded(), this.hereIsMyPublicKeyToo.length,
					mPub.getEncoded().length);
		} else if (receivedMessageStartsWith(this.hereIsMyPublicKeyToo)) {
			this.ssa = new SharedSecretAgreement(getPrivateKey(),
					unwrapContent(this.hereIsMyPublicKeyToo));
			this.ssa.generateSharedSecret();
		}

	}

	/**
	 * Controlla se il parametro passato coincide con l'header del messaggio.
	 * 
	 * @param prefix
	 * @return
	 * @throws ArbitraryMessageReceivedException
	 */
	private boolean receivedMessageStartsWith(byte[] prefix)
			throws ArbitraryMessageReceivedException {
		byte[] temp = new byte[prefix.length];
		System.arraycopy(this.message, 0, temp, 0, prefix.length);
		if (Arrays.equals(temp, prefix)) {
			if (this.message.length == prefix.length) {
				throw new ArbitraryMessageReceivedException(
						"C'è solo il prefisso: manca il corpo!!!");
			}
		}
		return false;
	}

	/**
	 * Restituisce il messaggio senza header.
	 * 
	 * @param prefix
	 * @return
	 */
	private byte[] unwrapContent(byte[] prefix) {
		int messageLength = this.message.length - prefix.length;
		byte[] message = new byte[messageLength];
		System.arraycopy(this.message, prefix.length, message, 0, messageLength);

		return message;
	}

	/**
	 * Carica da file la propria chiave pubblica.
	 * 
	 * @return
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	private PublicKey getPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		SharedPreferences keys = this.ctx.getSharedPreferences(MyPrefFiles.MY_KEYS, Context.MODE_PRIVATE);
		byte[] myPub = Base64.decode(keys.getString(MyPrefFiles.MY_PUB, null), Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance("EC");
		
		return kf.generatePublic(new X509EncodedKeySpec(myPub));
	}

	/**
	 * Carica da file la propria chiave privata.
	 * 
	 * @return
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchAlgorithmException 
	 */
	private PrivateKey getPrivateKey() throws InvalidKeySpecException, NoSuchAlgorithmException {
		SharedPreferences keys = this.ctx.getSharedPreferences(MyPrefFiles.MY_KEYS, Context.MODE_PRIVATE);
		byte[] myPriv = Base64.decode(keys.getString(MyPrefFiles.MY_PRIV, null), Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance("EC");
		
		return kf.generatePrivate(new X509EncodedKeySpec(myPriv));
	}

}
