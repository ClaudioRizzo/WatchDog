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

public class TimeoutWrapper {
	
	public static void addTimeout(String waiting, String waited, Context ctx) throws NoSuchAlgorithmException{
		Intent i = new Intent(ctx, TimeoutManagement.class);
		i.putExtra(SMSUtility.OTHER_PHONE, waited);
		SecureRandom sr = SecureRandom.getInstance(CryptoUtility.SHA1_PRNG);
		int id = 0;
		sr.nextInt(id);
		PendingIntent pi = PendingIntent.getBroadcast(ctx, id, i, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, (long) 120000, pi);
		String timeoutKey = MyPrefFiles.TIMEOUT_ID + waiting + waited;
		MyPrefFiles.setMyPreference(MyPrefFiles.TIMEOUTS_IDS, timeoutKey, String.valueOf(id), ctx);
	}
	
	public static void removeTimeout(String waiting, String waited, Context ctx) throws NumberFormatException, NoSuchPreferenceFoundException {
		Intent i = new Intent(ctx, TimeoutManagement.class);
		String timeoutKey = MyPrefFiles.TIMEOUT_ID + waiting + waited;
		int id = Integer.valueOf(MyPrefFiles.getMyPreference(MyPrefFiles.TIMEOUTS_IDS, timeoutKey, ctx)).intValue();
		PendingIntent pi = PendingIntent.getBroadcast(ctx, id, i, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}
}