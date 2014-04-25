package it.polimi.dima.watchdog.sms.socialistMillionare;

import java.security.NoSuchAlgorithmException;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.socialistMillionare.factory.SocialistMillionaireFactory;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

/**
 * This class handles the messagges by using the visitor patter which is upon the messages. Should extend BroadCastReceiver
 * @author claudio, emanuele
 *
 */
public class SMSPublicKeyHandler extends BroadcastReceiver implements SMSPublicKeyVisitorInterface {

	
	
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
			e.printStackTrace();
			Log.i("[DEBUG]", "SMSPubblicKey: Sono sempre io ... -.-'");
			Log.e("[Error] PublicKeyHandler", e.toString());
		}
		
	}
	
	@Override
	public void visit(PublicKeyRequestCodeMessage pubKeyReqMsg) {
		try 
		{
			this.pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, this.ctx));
			//SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE2), this.pka.getMyPublicKey());
			Log.i("[DEBUG]", "ok sono nella gestione richiesta");
			this.showShortToastMessage("RICEVUTO");
		} 
		catch (NoSuchPreferenceFoundException e) 
		{
			this.showShortToastMessage(e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	public void visit(PublicKeySentCodeMessage pubKeySentMsg) {
		
		try 
		{
			this.pka.setSecretQuestion(MyPrefFiles.getMyPreference(MyPrefFiles.SECRET_Q_A, MyPrefFiles.SECRET_QUESTION, this.ctx));
			MyPrefFiles.setMyPreference(MyPrefFiles.KEYSQUARE, this.other, pubKeySentMsg.getBody(), this.ctx);
			SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE3), this.pka.getSecretQuestion().getBytes());
			Log.i("[DEBUG]", "Visitor sembra funzionare");
		}
		catch (NoSuchPreferenceFoundException e) 
		{
			this.showShortToastMessage(e.getMessage());
			e.printStackTrace();
		}

		
	}

	@Override
	public void visit(SecretQuestionSentCodeMessage secQuestMsg) {
		try 
		{
			this.pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, this.ctx));
			this.pka.setSecretQuestion(secQuestMsg.getBody());
			//TODO aspettare la risposta dell'utente
			this.pka.setSecretAnswer("DUMMY"); //ovviamente al posto di dummy ci va ciò che l'utente ha inserito.
			this.pka.doHashToSend();
			SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE4), this.pka.getHashToSend().getBytes());
		} 
		catch (NoSuchPreferenceFoundException e) 
		{
			this.showShortToastMessage(e.getMessage());
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			this.showShortToastMessage(e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void visit(SecretAnswerAndPublicKeyHashSentCodeMessage secAnswMsg) {
		try 
		{
			this.pka.setSecretAnswer(MyPrefFiles.getMyPreference(MyPrefFiles.SECRET_Q_A, MyPrefFiles.SECRET_ANSWER, this.ctx));
			this.pka.setReceivedPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx));
			this.pka.doHashToCheck();
			this.pka.setReceivedHash(secAnswMsg.getBody());
			//la chiave è cancellata dal keysquare sempre e comunque
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx);
			if(!this.pka.checkForEquality()){
				//TODO notificare a sè stesso e all'altro la mancata validazione.
			}
			else{
				String keyValidated = Base64.encodeToString(this.pka.getReceivedPublicKey(), Base64.DEFAULT);
				MyPrefFiles.setMyPreference(MyPrefFiles.KEYRING, this.other, keyValidated, this.ctx);
				SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE5), SMSUtility.hexStringToByteArray(SMSUtility.CODE5));
			}
		} 
		catch (NoSuchPreferenceFoundException e) 
		{
			this.showShortToastMessage(e.getMessage());
			e.printStackTrace();
		}
		catch (NoSuchAlgorithmException e)
		{
			this.showShortToastMessage(e.getMessage());
			e.printStackTrace();
		}
		
	}

	@Override
	public void visit(KeyValidatedCodeMessage keyValMsg) {
		this.pka.setOtherKeyValidated(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.other, this.ctx));
		if(!this.pka.isOtherKeyValidated()){
			SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE1), null);
		}
		
	}

	@Override
	public void visit(IDontWantToAssociateCodeMessage noAssMsg) {
		// TODO gestire l'errore
		
	}

	
	/**
	 * Ritorna l'header del messaggio
	 * @param msg
	 * @return
	 * @throws ArbitraryMessageReceivedException
	 */
	private String getHeader(byte[] msg) throws ArbitraryMessageReceivedException {
		
		if(msg.length < SMSProtocol.HEADER_LENGTH){
			throw new ArbitraryMessageReceivedException("Messaggio inaspettato: troppo corto!!!");
		}
		byte[] header = new byte[SMSProtocol.HEADER_LENGTH];
		System.arraycopy(msg, 0, header, 0, SMSProtocol.HEADER_LENGTH);		
		
		
		return SMSUtility.bytesToHex(header);
		
	}
	
	
	
	private String getBody(byte[] msg) throws ArbitraryMessageReceivedException {
		
		if(msg.length < SMSProtocol.HEADER_LENGTH){
			throw new ArbitraryMessageReceivedException("Messaggio inaspettato: troppo corto!!!");
		}
		//TODO: controlla body null
		//android pare (dalla javadoc) non aggiungere un terminatore all'array di byte quando si usa getUserData(). In caso contrario la lunghezza
		//va decurtata di 1.
		int bodyLength = msg.length - SMSProtocol.HEADER_LENGTH;
		
		if(bodyLength == 0){
			return null;
		}
		
		byte[] body = new byte[bodyLength];
		System.arraycopy(msg, SMSProtocol.HEADER_LENGTH, body, 0, bodyLength);
		String bodyStr = Base64.encodeToString(body, Base64.DEFAULT);
		return bodyStr;
		
		
	}
	
	
	

	private void showShortToastMessage(String message) {
		Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
		toast.show();
	}

}
