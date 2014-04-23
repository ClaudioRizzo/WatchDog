package it.polimi.dima.watchdog.sms;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.DummyClass.DummyInterface;
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
 * Classe in cui il protocollo del socialista milionario è utilizzato tramite invio di SMS per validare la
 * chiave pubblica di un altro telefono.
 * 
 * @author emanuele
 *
 */
public class SMSPublicKeyManagement extends BroadcastReceiver implements DummyInterface {

	private byte[] PublicKeyRequestCode = new BigInteger("0xC0DE1FFF").toByteArray();
	private byte[] PublicKeySentCode = new BigInteger("0xC0DE2FFF").toByteArray();
	private byte[] SecretQuestionSentCode = new BigInteger("0xC0DE3FFF").toByteArray();
	private byte[] SecretAnswerAndPublicKeyHashSentCode = new BigInteger("0xC0DE4FFF").toByteArray();
	private byte[] KeyValidatedCode = new BigInteger("0xC0DE5FFF").toByteArray();
	private byte[] IDontWantToAssociate = new BigInteger("0xC0DE6FFF").toByteArray();
	
	private PublicKeyAutenticator pka;
	private SmsManager manager;
	private String other;
	private byte[] message;
	
	private Context ctx;

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
		this.ctx = context;
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
	 * @throws NoSuchPreferenceFoundException 
	 */
	private void manageReceivedMessage() throws ArbitraryMessageReceivedException, UnsupportedEncodingException, NoSuchAlgorithmException, NoSuchPreferenceFoundException {
		if(this.message.equals(this.PublicKeyRequestCode)){
			DummyClass.getDumyInstance().setListener(this);
		}
		else if(this.message.equals(this.IDontWantToAssociate)){
			//TODO notificare l'utente che la richiesta è stata rifiutata.
		}
		else if(receivedMessageStartsWith(this.PublicKeySentCode)){
			this.pka = new PublicKeyAutenticator(null, getSecretQuestion(), null);
			saveOnFileThePublicKeyObtained(unwrapContent(this.PublicKeySentCode));
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
				deleteKeyNotValidated();
				//TODO notificare l'attività della mancata validazione in modo da restituire un messaggio di
				//errore
			}
			else{
				this.pka.setOtherKeyValidated(true);
				saveOnFileTheCoupleTelephoneAndKey();
				sendMessage("ack");
			}
		}
		else if(receivedMessageStartsWith(this.KeyValidatedCode)){
			this.pka = new PublicKeyAutenticator();
			this.pka.setOtherKeyValidated(isOtherKeyValidatedByMe());
			if(!this.pka.isOtherKeyValidated()){
				initiateSMP();
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
		
		if(prefix.equals(this.PublicKeySentCode) || prefix.equals(this.SecretAnswerAndPublicKeyHashSentCode)){
			return Base64.encodeToString(message, Base64.DEFAULT);
		}
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
	 * @throws NoSuchPreferenceFoundException 
	 */
	private byte[] getKeyToVerify() throws NoSuchPreferenceFoundException {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.KEYSQUARE, Context.MODE_PRIVATE);
		String otherPubMaybe = sp.getString(this.other, null);
		if(otherPubMaybe == null){
			throw new NoSuchPreferenceFoundException("Non esiste tale chiave pubblica!!!");
		}
		return Base64.decode(otherPubMaybe, Base64.DEFAULT);
	}

	/**
	 * Salva su file la chiave pubblica ricevuta.
	 */
	private void saveOnFileThePublicKeyObtained(String otherPubMaybe) {//è già in base64
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.KEYSQUARE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(this.other, otherPubMaybe);
		editor.commit();
	}
	
	/**
	 * Dopo la validazione salva su file la coppia telefono dell'altro/chiave.
	 */
	private void saveOnFileTheCoupleTelephoneAndKey() {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.KEYRING, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(this.other, Base64.encodeToString(this.pka.getReceivedPublicKey(), Base64.DEFAULT));
		editor.commit();
	}
	
	/**
	 * Metodo chiamato dopo il fallimento della validazione: la chiave in "standby" viene cancellata.
	 */
	private void deleteKeyNotValidated() {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.KEYSQUARE, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(this.other);
		editor.commit();
	}
	
	/**
	 * Controlla se su file esiste una coppia valida telefono dell'altro/chiave
	 * @return
	 */
	private boolean isOtherKeyValidatedByMe() {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.KEYRING, Context.MODE_PRIVATE);
		String otherPub = sp.getString(this.other, null);
		if(otherPub == null){
			return false;
		}
		return true;
	}

	/**
	 * Da file ritorna la risposta segreta (TODO bisogna lasciarla in chiaro?)
	 * @return
	 * @throws NoSuchPreferenceFoundException 
	 */
	private String getSecretAnswer() throws NoSuchPreferenceFoundException {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.SECRET_Q_A, Context.MODE_PRIVATE);
		String answer = sp.getString(MyPrefFiles.SECRET_ANSWER, null);//TODO ricordarsi di settarla in inizializzazione
		if(answer == null){
			throw new NoSuchPreferenceFoundException("Non esiste la risposta segreta!!!");
		}
		return answer;
	}

	/**
	 * Da file ritorna la domanda segreta
	 * @return
	 * @throws NoSuchPreferenceFoundException 
	 */
	private String getSecretQuestion() throws NoSuchPreferenceFoundException {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.SECRET_Q_A, Context.MODE_PRIVATE);
		String question = sp.getString(MyPrefFiles.SECRET_QUESTION, null);//TODO ricordarsi di settarla in inizializzazione
		if(question == null){
			throw new NoSuchPreferenceFoundException("Non esiste la domanda segreta!!!");
		}
		return question;
	}

	/**
	 * Da file ritorna la propria chiave pubblica
	 * @return
	 * @throws NoSuchPreferenceFoundException 
	 */
	private byte[] getPublicKey() throws NoSuchPreferenceFoundException {
		SharedPreferences sp = this.ctx.getSharedPreferences(MyPrefFiles.MY_KEYS, Context.MODE_PRIVATE);
		String myPub = sp.getString(MyPrefFiles.MY_PUB, null);
		if(myPub == null){
			throw new NoSuchPreferenceFoundException("Non ho una chiave pubblica!!!");
		}
		return Base64.decode(myPub, Base64.DEFAULT);
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
		byte[] header;
		byte[] body;
		
		if(switcher.equals("key")){
			bodySize = this.pka.getMyPublicKey().length;
			headerSize = this.PublicKeySentCode.length;
			header = this.PublicKeySentCode;
			body = this.pka.getMyPublicKey();
		}
		else if(switcher.equals("idontwantyou")){
			bodySize = 0;
			headerSize = this.IDontWantToAssociate.length;
			header = this.IDontWantToAssociate;
			body = "".getBytes();
		}
		else if(switcher.equals("question")){
			bodySize = this.pka.getSecretQuestion().getBytes().length;
			headerSize = this.SecretQuestionSentCode.length;
			body = this.pka.getSecretQuestion().getBytes();
			header = this.SecretQuestionSentCode;
			
		}
		else if(switcher.equals("hash")){
			this.pka.doHashToSend();
			String hash = this.pka.getHashToSend();
			
			bodySize = hash.getBytes().length;
			headerSize = this.SecretAnswerAndPublicKeyHashSentCode.length;
			header = this.SecretAnswerAndPublicKeyHashSentCode;
			body = hash.getBytes();
		}
		else if(switcher.equals("ack")){
			//mando due volte l'header perchè il parser si aspetta per forza dati dopo l'header.
			bodySize = this.KeyValidatedCode.length;
			headerSize = this.KeyValidatedCode.length;
			header = this.KeyValidatedCode;
			body = this.KeyValidatedCode;
		}
		else{
			throw new IllegalArgumentException();
		}
		send(bodySize, headerSize, header, body);
	}
	
	/**
	 * Costruisce e manda il messaggio.
	 * @param bodySize : dimensione in byte del corpo del messaggio
	 * @param headerSize : dimensione in byte dell'header del messaggio
	 */
	private void send(int bodySize, int headerSize, byte[] header, byte[] body){
		byte[] message = new byte[bodySize + headerSize];
		System.arraycopy(header, 0, message, 0, headerSize);
		System.arraycopy(body, 0, message, headerSize, bodySize);
		
		this.manager.sendDataMessage(this.other, null, (short)999, message, null, null);
	}

	@Override
	public void notifyChoice(boolean choice) throws NoSuchAlgorithmException, NoSuchPreferenceFoundException {
		if(choice){
			this.pka = new PublicKeyAutenticator(getPublicKey(), null, null);
			sendMessage("key");
		}
		else{
			sendMessage("idontwantyou");
		}
		
	}


}
