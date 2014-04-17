package it.polimi.dima.watchdog.sms;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Classe in cui il protocollo del socialista milionario è utilizzato tramite invio di SMS per validare la
 * chiave pubblica di un altro telefono.
 * 
 * @author emanuele
 *
 */
public class SMSPublicKeyManagement extends BroadcastReceiver {

	private byte[] PublicKeyRequestCode = new BigInteger("0xC0DE1FFF").toByteArray();
	private byte[] PublicKeySentCode = new BigInteger("0xC0DE2FFF").toByteArray();
	private byte[] SecretQuestionSentCode = new BigInteger("0xC0DE3FFF").toByteArray();
	private byte[] SecretAnswerAndPublicKeyHashSentCode = new BigInteger("0xC0DE4FFF").toByteArray();
	
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
	public SMSPublicKeyManagement(String other) {
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
	
	/**
	 * Metodo che prende il messaggio ricevuto e il mittente e li salva negli attributi della classe; poi chiama
	 * il metodo che parsa il messaggio e agisce di conseguenza.
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				for (int i = 0; i < pdusObj.length; i++) {
					SmsMessage receivedMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
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
	 * Metodo che decide cosa fare in base all'header del messaggio (o in alcuni casi in base al messaggio stesso).
	 * @throws ArbitraryMessageReceivedException 
	 * @throws UnsupportedEncodingException 
	 * @throws NoSuchAlgorithmException 
	 */
	private void manageReceivedMessage() throws ArbitraryMessageReceivedException, UnsupportedEncodingException, NoSuchAlgorithmException {
		if(this.message.equals(this.PublicKeyRequestCode)){
			this.pka = new PublicKeyAutenticator(getPublicKey(), null, null);
			sendMessage("key");
		}
		else if(receivedMessageStartsWith(this.PublicKeySentCode)){
			this.pka = new PublicKeyAutenticator(null, getSecretQuestion(), null);
			saveOnFileThePublicKeyObtained();
			sendMessage("question");
		}
		else if(receivedMessageStartsWith(this.SecretQuestionSentCode)){
			this.pka = new PublicKeyAutenticator(getPublicKey(), null, null);
			this.pka.setReceivedQuestion(unwrapContent(this.SecretQuestionSentCode));
			this.pka.setSecretAnswer(getSecretAnswerFromUserInput());
			sendMessage("hash");
		}
		else if(receivedMessageStartsWith(this.SecretAnswerAndPublicKeyHashSentCode)){
			this.pka = new PublicKeyAutenticator(null, null, getSecretAnswer());
			//recupera da file la chiave pubblica ricevuta precedentemente
			this.pka.setReceivedPublicKey(getKeyToVerify());
			this.pka.doHashToCheck();
			this.pka.setReceivedHash(unwrapContent(this.SecretAnswerAndPublicKeyHashSentCode));
			if(!this.pka.checkForEquality()){
				//TODO gestire la mancata validazione della chiave.
			}
		}
		else throw new ArbitraryMessageReceivedException("Non ho potuto eseguire il match di nessun Header!!!");
		
	}

	/**
	 * Metodo che separa header da corpo del messaggio e ritorna quest'ultimo.
	 * @param prefix
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	private String unwrapContent(byte[] prefix) throws UnsupportedEncodingException{
		int messageLength = this.message.length - prefix.length;
		byte[] message = new byte[messageLength];
		System.arraycopy(this.message, prefix.length, message, 0, messageLength);
		
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
	 * Metodo che vede se il messaggio è formato da header appropriato + qualcos'altro.
	 * @return
	 * @throws ArbitraryMessageReceivedException 
	 */
	private boolean receivedMessageStartsWith(byte[] prefix) throws ArbitraryMessageReceivedException {
		byte[] temp = new byte[prefix.length];
		System.arraycopy(this.message, 0, temp, 0, prefix.length);
		if(Arrays.equals(temp, prefix)){
			if(this.message.length == prefix.length){
				throw new ArbitraryMessageReceivedException("C'è solo il prefisso: manca il corpo!!!");
			}
		}
		return false;
	}
	
	/**
	 * Metodo che a seconda del messaggio che bisogna inviare all'altro decide le lunghezze di header e body e
	 * poi richiama il metodo che costruisce e spedisce l'sms. Nel caso il messaggio da mandare sia l'hash, lo
	 * costruisce.
	 * 
	 * @param switcher
	 * @throws NoSuchAlgorithmException
	 */
	private void sendMessage(String switcher) throws NoSuchAlgorithmException{
		int bodySize;
		int headerSize;
		
		if(switcher.equals("key")){
			bodySize = this.pka.getMyPublicKey().length;
			headerSize = this.PublicKeySentCode.length;
		}
		else if(switcher.equals("question")){
			bodySize = this.pka.getSecretQuestion().getBytes().length;
			headerSize = this.SecretQuestionSentCode.length;
		}
		else if(switcher.equals("hash")){
			this.pka.doHashToSend();
			String hash = this.pka.getHashToSend();
			
			bodySize = hash.getBytes().length;
			headerSize = this.SecretAnswerAndPublicKeyHashSentCode.length;
		}
		else{
			throw new IllegalArgumentException();
		}
		send(bodySize, headerSize);
	}
	
	/**
	 * Costruisce e manda il messaggio.
	 * @param bodySize : dimensione in byte del corpo del messaggio
	 * @param headerSize : dimensione in byte dell'header del messaggio
	 */
	private void send(int bodySize, int headerSize){
		byte[] message = new byte[bodySize + headerSize];
		System.arraycopy(this.SecretQuestionSentCode, 0, message, 0, headerSize);
		System.arraycopy(this.pka.getSecretQuestion().getBytes(), 0, message, headerSize, bodySize);
		
		this.manager.sendDataMessage(this.other, null, (short)999, message, null, null);
	}


}
