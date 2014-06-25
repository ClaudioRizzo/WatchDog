package it.polimi.dima.watchdog.sms.commands.timeout;

import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Calendar;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
	 * @param context : il contesto corrente
	 * @throws NoSuchAlgorithmException
	 */
	public static void addTimeout(String waiting, String waited, Context context) throws NoSuchAlgorithmException{
		Calendar nextTimeoutCalendar = Calendar.getInstance();
		Intent intent = TimeoutWrapper.createAndFillIntent(waited, context);
		PendingIntent pendingIntent = TimeoutWrapper.createPendingIntent(context, intent);
		TimeoutWrapper.createTimeout(context, pendingIntent, nextTimeoutCalendar);
		Log.i("DEBUG", "DEBUG TIMEOUT: timeout creato");
		TimeoutWrapper.storeTimeoutId(waiting, waited, context, TimeoutWrapper.PENDING_INTENT_ID);
	}
	
	/**
	 * Rimuove il timeout in cui waiting aspetta un messaggio da parte di waited.
	 * 
	 * @param waiting : il numero di telefono di colui che aspetta
	 * @param waited : il numero di telefono di colui da cui ci si aspetta un messaggio
	 * @param context : il contesto corrente
	 * @throws NumberFormatException
	 * @throws NoSuchPreferenceFoundException
	 */
	public static void removeTimeout(String waiting, String waited, Context context) throws NumberFormatException, NoSuchPreferenceFoundException {
		Intent intent = new Intent(context, TimeoutManagement.class);
		PendingIntent pendingIntent = TimeoutWrapper.createPendingIntentForTimeoutDeletion(waiting, waited, context, intent);
		TimeoutWrapper.cancelTimeout(context, pendingIntent);
	}
	
	private static Intent createAndFillIntent(String waited, Context context){
		Intent intent = new Intent(context, TimeoutManagement.class);
		intent.putExtra(SMSUtility.OTHER_PHONE, waited);
		return intent;
	}
	
	private static PendingIntent createPendingIntent(Context context, Intent intent) throws NoSuchAlgorithmException{
		TimeoutWrapper.PENDING_INTENT_ID = SecureRandom.getInstance(CryptoUtility.SHA1_PRNG).nextInt(Integer.MAX_VALUE);
		return PendingIntent.getBroadcast(context, TimeoutWrapper.PENDING_INTENT_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	private static void createTimeout(Context context, PendingIntent pendingIntent, Calendar nextTimeoutCalendar){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		nextTimeoutCalendar.add(Calendar.SECOND, SMSUtility.TIMEOUT_LENGTH);
		alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTimeoutCalendar.getTimeInMillis(), pendingIntent);
	}
	
	private static void storeTimeoutId(String waiting, String waited, Context context, int pendingIntentId){
		String timeoutKey = MyPrefFiles.TIMEOUT_ID + waiting + waited;
		MyPrefFiles.setMyPreference(MyPrefFiles.TIMEOUTS_IDS, timeoutKey, String.valueOf(pendingIntentId), context);
	}
	
	private static PendingIntent createPendingIntentForTimeoutDeletion(String waiting, String waited, Context context, Intent intent) throws NumberFormatException, NoSuchPreferenceFoundException{
		String timeoutKey = MyPrefFiles.TIMEOUT_ID + waiting + waited;
		TimeoutWrapper.PENDING_INTENT_ID = Integer.valueOf(MyPrefFiles.getMyPreference(MyPrefFiles.TIMEOUTS_IDS, timeoutKey, context)).intValue();
		return PendingIntent.getBroadcast(context, TimeoutWrapper.PENDING_INTENT_ID, intent, PendingIntent.FLAG_CANCEL_CURRENT);
	}
	
	private static void cancelTimeout(Context context, PendingIntent pendingIntent){
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}
}