package it.polimi.dima.watchdog.sms.socialistMillionaire;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Map;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.PasswordUtils;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.activities.MainActivity;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.crypto.SharedSecretAgreement;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
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

	
	
	private SMSProtocol recMsg; //RICORDARSI che il body è SEMPRE in Base64 !!!
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
				//ricordarsi che getBody prende il body e lo restituisce convertito in Base64 !!!
				//Oppure torna null se il body non c'è
				this.recMsg.setBody(SMSUtility.getBody(message.getUserData()));
				this.recMsg.handle(this);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("[DEBUG]", "SMSPublicKey: Sono sempre io ... -.-'");
			Log.e("[Error] PublicKeyHandler", e.toString());
			handleErrorOrException();
		}
		
	}
	
	@Override
	public void visit(PublicKeyRequestCodeMessage pubKeyReqMsg) {
		try 
		{
			validateMessage(pubKeyReqMsg);
			
			this.pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, this.ctx));
			SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE2), this.pka.getMyPublicKey());
			Log.i("[DEBUG]", "ok sono nella gestione richiesta");
			
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
			validateMessage(pubKeySentMsg);
			
			//la secret question settata non è in Base64 (è una stringa normale -> vedi AssociateNumberFragment),
			//quindi niente conversione richiesta
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
		catch (ArbitraryMessageReceivedException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			//L'idea è che il messaggio va ignorato senza cancellare niente.
		}

		
	}

	@Override
	public void visit(SecretQuestionSentCodeMessage secQuestMsg) {
		try 
		{
			//serve per debug: siamo arrivati al punto critico
			validateMessage(secQuestMsg);
			
			this.pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, this.ctx));
			String question = new String(Base64.decode(secQuestMsg.getBody(), Base64.DEFAULT), PasswordUtils.UTF_8);
			this.pka.setSecretQuestion(question);
			//TODO notificare all'utente la domanda segreta
			//TODO aspettare la risposta dell'utente
			this.pka.setSecretAnswer("DUMMY"); //ovviamente al posto di dummy ci va ciò che l'utente ha inserito.
			this.pka.doHashToSend();
			Map<String, ?> prova =  ctx.getSharedPreferences(MyPrefFiles.KEYSQUARE, Context.MODE_PRIVATE).getAll();
			
			for(String n: prova.keySet()) {
				Log.i("[DEBUG]", "sono il numero "+n);
			}
			
			//TODO: scommentare quando si è finita la gestione utente 
			//SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE4), this.pka.getHashToSend());
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
		catch (UnsupportedEncodingException e)
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
			validateMessage(secAnswMsg);
			
			this.pka.setSecretAnswer(MyPrefFiles.getMyPreference(MyPrefFiles.SECRET_Q_A, MyPrefFiles.SECRET_ANSWER, this.ctx));
			this.pka.setReceivedPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx));
			this.pka.doHashToCheck();
			//giustamente è in Base64
			this.pka.setReceivedHash(secAnswMsg.getBody());
			//la chiave è cancellata dal keysquare sempre e comunque
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx);
			if(!this.pka.checkForEquality()){
				handleErrorOrException();
			}
			else{
				String keyValidated = Base64.encodeToString(this.pka.getReceivedPublicKey(), Base64.DEFAULT);
				MyPrefFiles.setMyPreference(MyPrefFiles.KEYRING, this.other, keyValidated, this.ctx);
				String salt = MyPrefFiles.getMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_SALT, this.ctx);
				SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE5), salt.getBytes());
				
				//supponendo che ECDH possa essere fatto una volta per tutte
				doECDH();
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
		catch (ArbitraryMessageReceivedException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			//L'idea è che il messaggio va ignorato senza cancellare niente.
		}
		
	}

	@Override
	public void visit(KeyValidatedCodeMessage keyValMsg) {
		try{
			validateMessage(keyValMsg);
			
			//Se ricevo questo messaggio ed esso passa la validazione, l'altro ha validato la mia chiave pubblica.
			//Se non ho già validato la chiave pubblica dell'altro faccio partire smp in modo simmetrico.
			if (!MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.other, this.ctx)) {
				SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE1), null);
			}
			//Prendo il sale che mi è stato inviato insieme alla conferma di validazione e lo salvo nell'hashring.
			String salt = new String(Base64.decode(this.recMsg.getBody(), Base64.DEFAULT),PasswordUtils.UTF_8);
			MyPrefFiles.setMyPreference(MyPrefFiles.HASHRING, this.other, salt, this.ctx);
		}
		catch (UnsupportedEncodingException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		}
		catch (ArbitraryMessageReceivedException e){
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			//L'idea è che il messaggio va ignorato senza cancellare niente.
		}
		
	
	}

	@Override
	public void visit(IDontWantToAssociateCodeMessage noAssMsg) {
		validateMessage(noAssMsg);
		
		MyPrefFiles.erasePreferences(this.other, this.ctx);
		//TODO notificare il fragment di quello che è successo
	}
	
	/**
	 * Metodo che genera il segreto condiviso alla fine della validazione delle chiavi pubbliche.
	 * 
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
	 * Se qualcosa va storto vengono cancellate tutte le preferenze relative all'altro utente e quest'ultimo viene
	 * esortato a fare lo stesso.
	 */
	private void handleErrorOrException(){
		MyPrefFiles.erasePreferences(this.other, this.ctx);
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
	
	/**
	 * Chiama il metodo per generare il segreto codiviso e salva quest'ultimo nelle preferenze.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPreferenceFoundException
	 */
	private void doECDH() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException{
		byte[] secret = generateCommonSecret();
		String secretBase64 = Base64.encodeToString(secret, Base64.DEFAULT);
		MyPrefFiles.setMyPreference(MyPrefFiles.SHARED_SECRETS, this.other, secretBase64, this.ctx);
	}
	
	private void validateMessage(PublicKeyRequestCodeMessage pubKeyReqMsg){
		//se la richiesta deriva da un telefono che compare già da qualche parte nelle mie preferenze,
		//allora per qualche motivo il suo proprietario non ha più i miei dati, quindi io devo cancellare
		//i suoi e ripartire da zero.
		MyPrefFiles.erasePreferences(this.other, this.ctx);
	}
	
	private void validateMessage(PublicKeySentCodeMessage pubKeySentMsg) throws ArbitraryMessageReceivedException{
		//se mi arriva un messaggio del genere e ho qualche preferenza di quel numero già salvata
		//devo bloccare tutto perchè è sicuramente un errore o un messaggio falso, in quanto nessuno
		//deve inviare ad un altro la propria chiave pubblica se prima non è arrivato un messaggio
		//di richiesta. Tale messaggio non può partire se chi lo manderebbe non ha prima cancellato
		//tutte le preferenze che riguardano il destinatario.
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx) || MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.other, this.ctx) || MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS, this.other, this.ctx) || MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, this.other, this.ctx)){
			throw new ArbitraryMessageReceivedException("Messaggio ricevuto da un numero già presente!!!");
		}
	}
	
	private void validateMessage(SecretQuestionSentCodeMessage secQuestMsg){
		//se mi arriva un messaggio con una domanda segreta lascio all'utente la libera scelta ci cosa fare,
		//chiunque sia il mittente: sia inviare l'hash, sia rifiutare non porta a nessun problema.
		//Loggo il messaggio giusto per debug
		Log.i("[DEBUG]", "Mi è arrivata una domanda segreta...");
		Log.i("[DEBUG]", "...sono arrivato al punto critico");
	}
	
	private void validateMessage(SecretAnswerAndPublicKeyHashSentCodeMessage secAnswMsg) throws ArbitraryMessageReceivedException{
		//se ricevo un messaggio del genere da un numero non in attesa di validazione devo ignorarlo
		//e non proseguire oltre.
		if(!MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx)){
			throw new ArbitraryMessageReceivedException("Messaggio ricevuto da un numero non presente nel keysquare!!!");
		}
	}
	
	private void validateMessage(KeyValidatedCodeMessage keyValMsg) throws ArbitraryMessageReceivedException{
		//se ricevo questo messaggio e ho il sale della password dell'altro utente già salvato, allora questo
		//messaggio è un falso o un errore, perchè se qualcuno mi invia un sale, io devo aver già cancellato
		//quello vecchio e questo avviene solo se l'altro mi ha chiesto di farlo perchè ha perso i miei dati
		//e vuole ripetere l'associazione o se io ho perso i dati dell'altro e devo ripetere l'associazione.
		//In ogni caso quando l'altro mi dice che la mia chiave è validata, io nonn posso avere il sale della
		//sua password salvato. Quindi questo messaggio verrà ignorato e non si prosegue oltre.
		if(MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, this.other, this.ctx)){
			throw new ArbitraryMessageReceivedException("Il sale è già presente!!!");
		}
	}
	
	private void validateMessage(IDontWantToAssociateCodeMessage noAssMsg){
		
		//TODO problema: se un malintenzionato manda questo messaggio spoofando il suo numero, verrebbero
		//potenzialmente cancellate le preferenze di un altro senza che quest'ultimo lo sappia. 
	}
	
}