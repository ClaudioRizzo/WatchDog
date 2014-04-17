package it.polimi.dima.watchdog.sms;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.regex.Pattern;

import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
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

	/**
	 * L'utente A vuole aggiungere l'utente B alla lista di telefoni "rescue": per prima cosa lancia questo
	 * costruttore.
	 * @param mPub
	 * @param secretQuestion
	 * @param secretAnswer
	 * @param other
	 */
	public SMSPublicKeyManagement(PublicKey mPub, String secretQuestion, String secretAnswer, String other) {
		this.pka = new PublicKeyAutenticator(mPub.getEncoded(), secretQuestion, secretAnswer);
		this.other = other;
		this.manager = SmsManager.getDefault();
	}

	/**
	 * Costruttore usato da android ogni volta che viene ricevuto un messaggio.
	 */
	public SMSPublicKeyManagement() {
		this.manager = SmsManager.getDefault();
	}
	
	/**
	 * L'utente A vuole aggiungere l'utente B alla lista di telefoni "rescue": dopo aver costruito l'oggetto
	 * lancia questo metodo.
	 */
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

	/**
	 * Metodo che decide cosa fare in base all'header del messaggio (o in alcuni casi in base al messaggio stesso).
	 * @throws ArbitraryMessageReceivedException 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	private void manageReceivedMessage() throws ArbitraryMessageReceivedException, UnsupportedEncodingException, NoSuchAlgorithmException {
		if(this.message.equals(this.PublicKeyRequestCode)){
			this.pka = new PublicKeyAutenticator(getPublicKey(), getSecretQuestion(), getSecretAnswer());
			sendPublicKey();
		}
		else if(contains(this.PublicKeySentCode)){
			this.pka = new PublicKeyAutenticator(null, getSecretQuestion(), null);
			saveOnFileThePublicKeyObtained();
			sendSecretQuestion();
		}
		else if(contains(this.SecretQuestionSentCode)){
			this.pka = new PublicKeyAutenticator(getPublicKey(), null, null);
			this.pka.setReceivedQuestion(unwrapReceivedQuestion());
			this.pka.setSecretAnswer(getSecretAnswerFromUserInput());
			sendValidationHash();
		}
		else if(contains(this.SecretAnswerAndPublicKeyHashSentCode)){
			this.pka = new PublicKeyAutenticator(null, null, getSecretAnswer());
			//recupera da file la chiave pubblica ricevuta precedentemente
			this.pka.setReceivedPublicKey(getKeyToVerify());
			this.pka.doHashToCheck();
			this.pka.setReceivedHash(unwrapReceivedHash());
			if(!this.pka.checkForEquality()){
				//TODO gestire la mancata validazione della chiave.
			}
		}
		else throw new ArbitraryMessageReceivedException("Non ho potuto eseguire il match di nessun Header!!!");
		
	}

	/**
	 * Metodo che separa header e hash e ritorna quest'ultimo.
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String unwrapReceivedHash() throws UnsupportedEncodingException {
		int messageLength = this.message.length - this.SecretAnswerAndPublicKeyHashSentCode.length;
		byte[] message = new byte[messageLength];
		System.arraycopy(this.message, this.SecretAnswerAndPublicKeyHashSentCode.length, message, 0, messageLength);
		
		return new String(message, "UTF-8");
	}


	/**
	 * Prende ciò che l'utente digita come risposta segreta per generare l'hash che servirà a dimostrare di
	 * essere il vero proprietario della chiave pubblica inviata precedentemente.
	 * @return
	 */
	private String getSecretAnswerFromUserInput() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Recupera da file la chiave pubblica collezionata precedentemente.
	 * @return
	 */
	private byte[] getKeyToVerify() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Salva su file la chiave pubblica ricevuta.
	 */
	private void saveOnFileThePublicKeyObtained() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Metodo che separa header e domanda segreta e ritorna quest'ultima.
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String unwrapReceivedQuestion() throws UnsupportedEncodingException {
		int messageLength = this.message.length - this.SecretQuestionSentCode.length;
		byte[] message = new byte[messageLength];
		System.arraycopy(this.message, this.SecretQuestionSentCode.length, message, 0, messageLength);
		
		return new String(message, "UTF-8");
	}

	/**
	 * Metodo che invia all'altro l'hash che dovrebbe dimostrare che non è un MITM, preceduto da un header
	 * appropriato.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	private void sendValidationHash() throws NoSuchAlgorithmException {
		this.pka.doHashToSend();
		String hash = this.pka.getHashToSend();
		
		int hashSize = hash.getBytes().length;
		int headerSize = this.SecretAnswerAndPublicKeyHashSentCode.length;
		
		byte[] message = new byte[hashSize + headerSize];
		System.arraycopy(this.SecretAnswerAndPublicKeyHashSentCode, 0, message, 0, headerSize);
		System.arraycopy(hash.getBytes(), 0, message, headerSize, hashSize);
		
		this.manager.sendDataMessage(this.other, null, (short)999, message, null, null);
	}


	/**
	 * Metodo che vede se il messaggio è formato da header appropriato + qualcos'altro.
	 * @return
	 */
	private boolean contains(byte[] prefix) {
		String prefixBase64 = Base64.encodeToString(prefix, Base64.DEFAULT);
		String messageBase64 = Base64.encodeToString(this.message, Base64.DEFAULT);
		if(Pattern.matches(prefixBase64 + ".+", messageBase64)){
			return true;
		}
		return false;
	}


	/**
	 * Da file ritorna la risposta segreta
	 * @return
	 */
	private String getSecretAnswer() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Da file ritorna la domanda segreta
	 * @return
	 */
	private String getSecretQuestion() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Da file ritorna la propria chiave pubblica
	 * @return
	 */
	private byte[] getPublicKey() {
		// TODO Auto-generated method stub
		return null;
	}


	/**
	 * Metodo che manda la propria chiave pubblica al destinatario preceduta dall'header giusto.
	 */
	private void sendPublicKey() {
		int publicKeySize = this.pka.getMyPublicKey().length;
		int headerSize = this.PublicKeySentCode.length;
		
		byte[] message = new byte[publicKeySize + headerSize];
		System.arraycopy(this.PublicKeySentCode, 0, message, 0, headerSize);
		System.arraycopy(this.pka.getMyPublicKey(), 0, message, headerSize, publicKeySize);
		
		this.manager.sendDataMessage(this.other, null, (short)999, message, null, null);
	}
	
	/**
	 * Metodo che manda la domanda segreta al destinatario preceduta dall'header giusto.
	 */
	private void sendSecretQuestion() {
		int secretQuestionSize = this.pka.getSecretQuestion().getBytes().length;
		int headerSize = this.SecretQuestionSentCode.length;
		
		byte[] message = new byte[secretQuestionSize + headerSize];
		System.arraycopy(this.SecretQuestionSentCode, 0, message, 0, headerSize);
		System.arraycopy(this.pka.getSecretQuestion().getBytes(), 0, message, headerSize, secretQuestionSize);
		
		this.manager.sendDataMessage(this.other, null, (short)999, message, null, null);		
	}


}
