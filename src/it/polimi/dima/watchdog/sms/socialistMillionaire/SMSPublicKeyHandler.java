package it.polimi.dima.watchdog.sms.socialistMillionaire;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.UTILITIES.PasswordUtils;
import it.polimi.dima.watchdog.UTILITIES.SMSUtility;
import it.polimi.dima.watchdog.activities.PendingRequestsActivity;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.crypto.SharedSecretAgreement;
import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
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
 * This class handles the messagges by using the visitor patter which is upon
 * the messages. Should extend BroadCastReceiver
 * 
 * @author claudio, emanuele
 * 
 */
public class SMSPublicKeyHandler extends BroadcastReceiver implements
		SMSPublicKeyVisitorInterface {

	private ParsableSMS recMsg; // RICORDARSI che il body è SEMPRE in Base64 !!!
	private String other; // a turno sarà sender o receiver
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

		Log.i("[DEBUG-SMP]", "HO RICEVUTO IL MESSAGGIO");
		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get(SMSUtility.SMS_EXTRA_NAME);
				SmsMessage message = null;

				for (int i = 0; i < pdusObj.length; i++) {
					message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}

				this.other = message.getDisplayOriginatingAddress();
				this.recMsg = this.mSocMilFactory.getMessage(SMSUtility.getHeader(message.getUserData()));
				// ricordarsi che getBody prende il body e lo restituisce
				// convertito in Base64 !!!
				// Oppure torna null se il body non c'è
				this.recMsg.setBody(SMSUtility.getBody(message.getUserData()));
				this.recMsg.handle(this);
			}

		} catch (Exception e) {
			e.printStackTrace();
			Log.e("[Error] PublicKeyHandler", e.toString());
			handleErrorOrException();
		}

	}

	/**
	 * Gestisco la ricezione di un messaggio che chiede la mia chiave pubblica
	 */
	@Override
	public void visit(PublicKeyRequestCodeMessage pubKeyReqMsg) {
		Log.i("[DEBUG-SMP]", "CODE_1");
		try {
			//vedo se accettare il messaggio
			pubKeyReqMsg.validate(this.other, this.ctx);
			
			//segno in SMP_STATUS che ho ricevuto una richiesta di invio della mia chiave pubblica
			String prefKey = this.other + MyPrefFiles.PUB_KEY_REQUEST_RECEIVED;
			MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, prefKey, this.other, this.ctx);
			
			//preparo la mia chiave pubblica...
			this.pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, this.ctx));
			
			//...poi la invio a chi me l'ha chiesta...
			SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE2), this.pka.getMyPublicKey());
			
			//... e infine segno in SMP_STATUS che l'ho inviata
			String preferenceKey = this.other + MyPrefFiles.PUB_KEY_FORWARDED;
			MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, this.other, this.ctx);
			
		} catch (NoSuchPreferenceFoundException e) {
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		} catch (MessageWillBeIgnoredException e) {
			// appunto si ignora il messaggio
		}

	}

	/**
	 * Gestisco la ricezione di un messaggio che contiene la chiave pubblica di un altro
	 */
	@Override
	public void visit(PublicKeySentCodeMessage pubKeySentMsg) {
		Log.i("[DEBUG-SMP_BEFORE_TRY]", "CODE_2");
		try {
			//vedo se accettare il messaggio
			pubKeySentMsg.validate(this.other, this.ctx);

			//salvo nel keysquare la chiave ricevuta...
			MyPrefFiles.setMyPreference(MyPrefFiles.KEYSQUARE, this.other, pubKeySentMsg.getBody(), this.ctx);
			
			//... e anche in SMP_STATUS il fatto che l'abbia ricevuta
			String prefKey = this.other + MyPrefFiles.PUB_KEY_RECEIVED;
			MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, prefKey, this.other, this.ctx);
			
			//preparo la mia secret question (non è salvata in Base64, quindi non è richiesta la converione)...
			this.pka.setSecretQuestion(MyPrefFiles.getMyPreference(MyPrefFiles.SECRET_Q_A, this.other + MyPrefFiles.SECRET_QUESTION, this.ctx));
			
			//... poi la invio a chi mi ha mandato la sua chiave pubblica...
			SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE3), this.pka.getSecretQuestion().getBytes());
			
			//... e segno in SMP_STATUS di avergliela mandata
			String preferenceKey = this.other + MyPrefFiles.SECRET_QUESTION_FORWARDED;
			MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, this.other, this.ctx);
			
			Log.i("[DEBUG-SMP_AFTER_SEND]", "CODE_2");
			
		} catch (NoSuchPreferenceFoundException e) {
			Log.i("[DEBUG-SMP-ERROR]", "CODE_2");
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		} catch (MessageWillBeIgnoredException e) {
			Log.i("[DEBUG-SMP-ERROR]", "CODE_2");
			// appunto si ignora il messaggio
		}

	}

	@Override
	public void visit(SecretQuestionSentCodeMessage secQuestMsg) {
		Log.i("[DEBUG-SMP]", "CODE_3");
		try {
			//vedo se accettare il messaggio
			secQuestMsg.validate(this.other, this.ctx);
			
			//segno in SMP_STATUS di aver ricevuto una domanda segreta
			String prefKey = this.other + MyPrefFiles.SECRET_QUESTION_RECEIVED;
			MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, prefKey, this.other, this.ctx);
			
			//segno in PENDENT il fatto che ho una richiesta pendente da parte di this.other...
			String question = new String(Base64.decode(secQuestMsg.getBody(), Base64.DEFAULT), PasswordUtils.UTF_8);
			MyPrefFiles.setMyPreference(MyPrefFiles.PENDENT, this.other, question, ctx);

			//... e notifico a me stesso di ciò
			this.notifyUser();
			
		} catch (UnsupportedEncodingException e) {
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		} catch (MessageWillBeIgnoredException e) {
			// appunto si ignora il messaggio
		}

	}

	@Override
	public void visit(SecretAnswerAndPublicKeyHashSentCodeMessage secAnswMsg) {
		Log.i("[DEBUG-SMP]", "CODE_4");
		try {
			//vedo se accettare il messaggio
			secAnswMsg.validate(this.other, this.ctx);

			//recupero la risposta segreta dalle preferenze...
			this.pka.setSecretAnswer(MyPrefFiles.getMyPreference(MyPrefFiles.SECRET_Q_A, this.other + MyPrefFiles.SECRET_ANSWER, this.ctx));
			
			//... poi recupero dal keysquare la chiave pubblica che mi era stata inviata...
			this.pka.setReceivedPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx));
			
			//... computo l'hash di chiave || risposta ...
			this.pka.doHashToCheck();
			
			//... e controllo se coincide con quello che ho ricevuto
			this.pka.setReceivedHash(secAnswMsg.getBody());

			//la chiave è cancellata dal keysquare sempre e comunque
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYSQUARE, this.other, this.ctx);
			
			//se gli hash non coincidono il SMP si interrompe
			if (!this.pka.checkForEquality()) {
				Log.i("DEBUG", "CHIAVE NON VALIDATA!!!");
				handleErrorOrException();
			} else {
				Log.i("DEBUG", "CHIAVE VALIDATA!!!");
				
				//salvo nel keyring la chiave appena validata...
				String keyValidated = Base64.encodeToString(this.pka.getReceivedPublicKey(), Base64.DEFAULT);
				MyPrefFiles.setMyPreference(MyPrefFiles.KEYRING, this.other, keyValidated, this.ctx);
				
				//... recupero il sale della mia password...
				String salt = MyPrefFiles.getMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_SALT, this.ctx);
				
				//... invio un ack che contiene anche il sale...
				SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE5), salt.getBytes());
				
				//... e segno in SMP_STATUS di averlo inviato
				String preferenceKey = this.other + MyPrefFiles.ACK_AND_SALT_FORWARDED;
				MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, this.other, this.ctx);
				
				//infine computo il segreto condiviso
				doECDH();
			}
		} catch (NoSuchPreferenceFoundException e) {
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		} catch (NoSuchAlgorithmException e) {
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		} catch (InvalidKeySpecException e) {
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		} catch (MessageWillBeIgnoredException e) {
			// appunto si ignora il messaggio
		}

	}

	@Override
	public void visit(KeyValidatedCodeMessage keyValMsg) {
		try {
			Log.i("[DEBUG]", "CODE_5");
			keyValMsg.validate(this.other, this.ctx);

			// Se ricevo questo messaggio ed esso passa la validazione, l'altro
			// ha validato la mia chiave pubblica.
			// Se non ho già validato la chiave pubblica dell'altro faccio
			// partire smp in modo simmetrico.
			if (!MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.other, this.ctx)) {
				SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE1), null);
				String preferenceKey = this.other + MyPrefFiles.PUB_KEY_REQUEST_FORWARDED;
				MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, this.other, this.ctx);
			}
			// Prendo il sale che mi è stato inviato insieme alla conferma di
			// validazione e lo salvo nell'hashring.
			String salt = new String(Base64.decode(this.recMsg.getBody(), Base64.DEFAULT), PasswordUtils.UTF_8);
			MyPrefFiles.setMyPreference(MyPrefFiles.HASHRING, this.other, salt, this.ctx);
		} catch (UnsupportedEncodingException e) {
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
			handleErrorOrException();
		} catch (MessageWillBeIgnoredException e) {
			// appunto si ignora il messaggio
		}

	}

	@Override
	public void visit(IDontWantToAssociateCodeMessage noAssMsg) {
		try {
			noAssMsg.validate(this.other, this.ctx);
			if (MyPrefFiles.iHaveSomeReferencesToThisUser(this.other, this.ctx)) {
				MyPrefFiles.erasePreferences(this.other, this.ctx);
				// TODO notificare il fragment di quello che è successo

				// la catena non va avanti all'infinito a causa dell'if. Dopo al
				// massimo un messaggio di
				// questo tipo a testa, entrambi non hanno più riferimenti
				// all'altro
				SMSUtility
						.sendMessage(
								this.other,
								SMSUtility.SMP_PORT,
								SMSUtility
										.hexStringToByteArray(SMSUtility.CODE6),
								null);
			}

		} catch (MessageWillBeIgnoredException e) {
			// appunto si ignora il messaggio
		}

	}

	/**
	 * Metodo che genera il segreto condiviso alla fine della validazione delle
	 * chiavi pubbliche.
	 * 
	 * @throws NoSuchPreferenceFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	private byte[] generateCommonSecret()
			throws NoSuchPreferenceFoundException, NoSuchAlgorithmException,
			InvalidKeySpecException {
		SharedSecretAgreement ssa = new SharedSecretAgreement();
		String myPriv = MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS,
				MyPrefFiles.MY_PRIV, this.ctx);
		String otherPub = MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING,
				this.other, this.ctx);
		ssa.setMyPrivateKey(myPriv);
		ssa.setTokenReceived(otherPub);

		return ssa.getSharedSecret();
	}

	/**
	 * Se qualcosa va storto vengono cancellate tutte le preferenze relative
	 * all'altro utente e quest'ultimo viene esortato a fare lo stesso.
	 */
	private void handleErrorOrException() {
		MyPrefFiles.erasePreferences(this.other, this.ctx);
		// TODO notificare il fragment di quello che è successo
		SMSUtility.sendMessage(this.other, SMSUtility.SMP_PORT,
				SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
	}

	private void notifyUser() {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this.ctx).setSmallIcon(android.R.drawable.stat_notify_chat).setContentTitle("prova").setContentText("hello world").setAutoCancel(true);

		Intent resultIntent = new Intent(this.ctx, PendingRequestsActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this.ctx);

		stackBuilder.addParentStack(PendingRequestsActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) this.ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(0, mBuilder.build());

	}

	/**
	 * Chiama il metodo per generare il segreto codiviso e salva quest'ultimo
	 * nelle preferenze.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPreferenceFoundException
	 */
	private void doECDH() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException {
		byte[] secret = generateCommonSecret();
		String secretBase64 = Base64.encodeToString(secret, Base64.DEFAULT);
		MyPrefFiles.setMyPreference(MyPrefFiles.SHARED_SECRETS, this.other, secretBase64, this.ctx);
	}

}
