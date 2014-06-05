package it.polimi.dima.watchdog.sms.timeout;

import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.utilities.CryptoUtility;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

/**
 * Classe che wrappa la creazione e la distruzione di un timeout.
 * 
 * @author emanuele
 *
 */
public class TimeoutWrapper {
	
	private static int PENDING_INTENT_ID;
	
	/**
	 * Aggiunge un timeout in cui waiting aspetta un messaggio da parte di waited.
	 * 
	 * @param waiting : il numero di telefono di colui che aspetta
	 * @param waited : il numero di telefono di colui da cui ci si aspetta un messaggio
	 * @param ctx : il contesto corrente
	 * @throws NoSuchAlgorithmException
	 */
	/*public static void addTimeout(String waiting, String waited, Context ctx) throws NoSuchAlgorithmException{
		Intent intent = TimeoutWrapper.createAndFillIntent(waited, ctx);
		PendingIntent pendingIntent = TimeoutWrapper.createPendingIntent(ctx, intent);
		TimeoutWrapper.createTimeout(ctx, pendingIntent);
		TimeoutWrapper.storeTimeoutId(waiting, waited, ctx, TimeoutWrapper.PENDING_INTENT_ID);
	}*/
	
	/**
	 * Rimuove il timeout in cui waiting aspetta un messaggio da parte di waited.
	 * 
	 * @param waiting : il numero di telefono di colui che aspetta
	 * @param waited : il numero di telefono di colui da cui ci si aspetta un messaggio
	 * @param ctx : il contesto corrente
	 * @throws NumberFormatException
	 * @throws NoSuchPreferenceFoundException
	 */
	/*public static void removeTimeout(String waiting, String waited, Context ctx) throws NumberFormatException, NoSuchPreferenceFoundException {
		Intent intent = new Intent(ctx, TimeoutManagement.class);
		PendingIntent pendingIntent = TimeoutWrapper.createPendingIntentForTimeoutDeletion(waiting, waited, ctx, intent);
		TimeoutWrapper.cancelTimeout(ctx, pendingIntent);
	}*/
	
	private static Intent createAndFillIntent(String waited, Context ctx){
		Intent intent = new Intent(ctx, TimeoutManagement.class);
		intent.putExtra(SMSUtility.OTHER_PHONE, waited);
		return intent;
	}
	
	private static PendingIntent createPendingIntent(Context ctx, Intent intent) throws NoSuchAlgorithmException{
		TimeoutWrapper.PENDING_INTENT_ID = SecureRandom.getInstance(CryptoUtility.SHA1_PRNG).nextInt(Integer.MAX_VALUE);
		return PendingIntent.getBroadcast(ctx, TimeoutWrapper.PENDING_INTENT_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	private static void createTimeout(Context ctx, PendingIntent pendingIntent){
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SMSUtility.TIMEOUT_LENGTH, pendingIntent);//qui cambia timer
	}
	
	private static void storeTimeoutId(String waiting, String waited, Context ctx, int pendingIntentId){
		String timeoutKey = MyPrefFiles.TIMEOUT_ID + waiting + waited;
		MyPrefFiles.setMyPreference(MyPrefFiles.TIMEOUTS_IDS, timeoutKey, String.valueOf(pendingIntentId), ctx);
	}
	
	private static PendingIntent createPendingIntentForTimeoutDeletion(String waiting, String waited, Context ctx, Intent intent) throws NumberFormatException, NoSuchPreferenceFoundException{
		String timeoutKey = MyPrefFiles.TIMEOUT_ID + waiting + waited;
		TimeoutWrapper.PENDING_INTENT_ID = Integer.valueOf(MyPrefFiles.getMyPreference(MyPrefFiles.TIMEOUTS_IDS, timeoutKey, ctx)).intValue();
		return PendingIntent.getBroadcast(ctx, TimeoutWrapper.PENDING_INTENT_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	private static void cancelTimeout(Context ctx, PendingIntent pendingIntent){
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}
}