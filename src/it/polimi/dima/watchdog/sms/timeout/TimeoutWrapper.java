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
	
	/**
	 * Aggiunge un timeout in cui waiting aspetta un messaggio da parte di waited.
	 * 
	 * @param waiting : il numero di telefono di colui che aspetta
	 * @param waited : il numero di telefono di colui da cui ci si aspetta un messaggio
	 * @param ctx : il contesto corrente
	 * @throws NoSuchAlgorithmException
	 */
	public static void addTimeout(String waiting, String waited, Context ctx) throws NoSuchAlgorithmException{
		Intent intent = new Intent(ctx, TimeoutManagement.class);
		intent.putExtra(SMSUtility.OTHER_PHONE, waited);
		SecureRandom secureRandom = SecureRandom.getInstance(CryptoUtility.SHA1_PRNG);
		int pendingIntentId = secureRandom.nextInt(Integer.MAX_VALUE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, pendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) 120000, pendingIntent);
		String timeoutKey = MyPrefFiles.TIMEOUT_ID + waiting + waited;
		MyPrefFiles.setMyPreference(MyPrefFiles.TIMEOUTS_IDS, timeoutKey, String.valueOf(pendingIntentId), ctx);
	}
	
	/**
	 * Rimuove il timeout in cui waiting aspetta un messaggio da parte di waited.
	 * 
	 * @param waiting : il numero di telefono di colui che aspetta
	 * @param waited : il numero di telefono di colui da cui ci si aspetta un messaggio
	 * @param ctx : il contesto corrente
	 * @throws NumberFormatException
	 * @throws NoSuchPreferenceFoundException
	 */
	public static void removeTimeout(String waiting, String waited, Context ctx) throws NumberFormatException, NoSuchPreferenceFoundException {
		Intent intent = new Intent(ctx, TimeoutManagement.class);
		String timeoutKey = MyPrefFiles.TIMEOUT_ID + waiting + waited;
		int pendingIntentId = Integer.valueOf(MyPrefFiles.getMyPreference(MyPrefFiles.TIMEOUTS_IDS, timeoutKey, ctx)).intValue();
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, pendingIntentId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}
}