package it.polimi.dima.watchdog.sms.socialistMillionaire;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.activities.MainActivity;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.crypto.SharedSecretAgreement;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.socialistMillionaire.factory.SocialistMillionaireFactory;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
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
				final Object[] pdusObj = (Object[]) bundle.get(SMSUtility.SMS_EXTRA_NAME);
				SmsMessage message = null;
				
				for (int i = 0; i < pdusObj.length; i++) {
					message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}
				
				this.other = message.getDisplayOriginatingAddress();
				this.recMsg = this.mSocMilFactory.getMessage(SMSUtility.getHeader(message.getUserData()));
				this.recMsg.setBody(SMSUtility.getBody(message.getUserData()));
				this.recMsg.handle(this);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("[DEBUG]", "SMSPubblicKey: Sono sempre io ... -.-'");
			Log.e("[Error] PublicKeyHandler", e.toString());
			handleErrorOrException();
		}
		
	}
	
	@Override
	public void visit(PublicKeyRequestCodeMessage pubKeyReqMsg) {
		try 
		{
			//se la richiesta deriva da un telefono già precedentemente associato o in attesa di associazione,
			//allora per qualche motivo il suo proprietario non ha più i miei dati, quindi io devo cancellare
			//i suoi e ripartire da zero.
			erasePreferences();
			
			this.pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, this.ctx));
			SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE2), this.pka.getMyPublicKey());
			Log.i("[DEBUG]", "ok sono nella gestione richiesta");
			this.notifyUser();
			
		} 
		catch (NoSuchPreferenceFoundException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
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
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}

		
	}

	@Override
	public void visit(SecretQuestionSentCodeMessage secQuestMsg) {
		try 
		{
			Log.i("[DEBUG]", "Sono arrivato al punto critico");
			this.pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, this.ctx));
			this.pka.setSecretQuestion(secQuestMsg.getBody());
			//TODO aspettare la risposta dell'utente
			this.pka.setSecretAnswer("DUMMY"); //ovviamente al posto di dummy ci va ciò che l'utente ha inserito.
			this.pka.doHashToSend();
			//TODO: scommentare quando si è finita la gestione utente 
			//SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE4), this.pka.getHashToSend().getBytes());
		} 
		catch (NoSuchPreferenceFoundException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}
		catch (NoSuchAlgorithmException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
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
				handleErrorOrException();
			}
			else{
				String keyValidated = Base64.encodeToString(this.pka.getReceivedPublicKey(), Base64.DEFAULT);
				MyPrefFiles.setMyPreference(MyPrefFiles.KEYRING, this.other, keyValidated, this.ctx);
				SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE5), null);
				
				//supponendo che ECDH possa essere fatto una volta per tutte
				byte[] secret = generateCommonSecret();
				String secretBase64 = Base64.encodeToString(secret, Base64.DEFAULT);
				MyPrefFiles.setMyPreference(MyPrefFiles.SHARED_SECRETS, this.other, secretBase64, this.ctx);
			}
		} 
		catch (NoSuchPreferenceFoundException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}
		catch (NoSuchAlgorithmException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}
		catch (InvalidKeySpecException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}
		
	}

	@Override
	public void visit(KeyValidatedCodeMessage keyValMsg) {
		try
		{
			this.pka.setMyKeyValidatedByTheOther(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.other, this.ctx));
			if(!this.pka.isMyKeyValidatedByTheOther()){
				SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE1), null);
			}
			else{
				
				//supponendo che ECDH possa essere fatto una volta per tutte
				byte[] secret = generateCommonSecret();
				String secretBase64 = Base64.encodeToString(secret, Base64.DEFAULT);
				MyPrefFiles.setMyPreference(MyPrefFiles.SHARED_SECRETS, this.other, secretBase64, this.ctx);
				//TODO mandare all'altro il sale con cui è stata salata la propria password, gestire
				//l'arrivo di tale messaggio, rispondere con un ack e gestire l'arrivo dell'ack.
			}
		}
		catch (NoSuchPreferenceFoundException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}
		catch (InvalidKeySpecException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}
		catch (NoSuchAlgorithmException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}
		
		
	}

	@Override
	public void visit(IDontWantToAssociateCodeMessage noAssMsg) {
		erasePreferences();
		//TODO notificare il fragment di quello che è successo
	}
	
	/**
	 * Metodo che genera il segreto condiviso alla fine della validazione delle chiavi pubbliche.
	 * @throws NoSuchPreferenceFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private byte[] generateCommonSecret() throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException{
		SharedSecretAgreement ssa = new SharedSecretAgreement();
		String myPriv = MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, this.ctx);
		String otherPub = MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, this.other, this.ctx);
		ssa.setMyPrivateKey(myPriv);
		ssa.setTokenReceived(otherPub);
		
		return ssa.getSharedSecret();
	}
	
	/**
	 * Se qualcosa va storto in SMP o ECDH tutte le preferenze relative all'altro utente vanno cancellate.
	 */
	private void erasePreferences(){
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.other, this.ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYRING, this.other, this.ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS, this.other, this.ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.SHARED_SECRETS, this.other, this.ctx);
		}
	}
	
	/**
	 * Se qualcosa va storto vengono cancellate tutte le preferenze relative all'altro utente e quest'ultimo viene
	 * esortato a fare lo stesso.
	 */
	private void handleErrorOrException(){
		erasePreferences();
		//TODO notificare il fragment di quello che è successo
		SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
	}
	
	private void notifyUser() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(ctx)
		.setSmallIcon(android.R.drawable.stat_notify_chat)
		.setContentTitle("prova")
		.setContentText("hello world")
		.setAutoCancel(true);
		
		Intent resultIntent = new Intent(ctx, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());
	
	}
	
}
