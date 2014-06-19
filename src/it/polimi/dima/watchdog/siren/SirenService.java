package it.polimi.dima.watchdog.siren;

import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.security.NoSuchAlgorithmException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/**
 * Servizio che fa partire l'handler della sirena e i controller che la rilanciano qualora dovesse essere
 * spenta e che mantengono il volume al massimo.
 * 
 * @author emanuele
 *
 */
public class SirenService extends Service {

	private Context context;
	private SirenHandler sirenHandler;
	private int mInterval = 5000;
	private Handler mHandler;
	
	Runnable mStatusChecker = new Runnable() {
	    @Override 
	    public void run() {
	    	sirenHandler.turnVolumeToMax();
	    	sirenHandler.playAlarmSound();
	    	mHandler.postDelayed(mStatusChecker, mInterval);
	    }
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i("DEBUG", "DEBUG: servizio delle sirene partito correttamente");
		this.context = getApplicationContext();
	    try {
			return handleCommand(intent);
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("DEBUG", "DEBUG: servizio delle sirene non partito correttamente!!!");
			this.stopSelf();
			return START_NOT_STICKY;
		}
	}

	private int handleCommand(Intent intent) throws NoSuchAlgorithmException, NoSuchPreferenceFoundException {
		String command = null;
		if(intent != null){
			if(intent.hasExtra(SMSUtility.COMMAND)){
				Bundle extras = intent.getExtras();
				command = extras.getString(SMSUtility.COMMAND);
				MyPrefFiles.setMyPreference(MyPrefFiles.SIREN_FILE, MyPrefFiles.SIREN_FILE_KEY, command, this.context);
			}
		}
		else{
			command = MyPrefFiles.getMyPreference(MyPrefFiles.SIREN_FILE, MyPrefFiles.SIREN_FILE_KEY, this.context);
		}
		
		
		if(command.equals(SMSUtility.SIREN_ON)){
			return doSirenon();
		}
		else if(command.equals(SMSUtility.SIREN_OFF)){
			return doSirenOff();
		}
		else {
			throw new IllegalArgumentException("comando non riconosciuto!!!");
		}
	}

	private int doSirenOff() {
		stopRingtoneControl();
		this.sirenHandler.stopAlarmSound();
		Log.i("DEBUG", "DEBUG: sirena e looper disattivati correttamente");
		this.stopSelf();
		return START_NOT_STICKY; //serve per notificare lo stop
	}

	private int doSirenon() throws NoSuchAlgorithmException {
		startSiren();
		startRingtoneControl();
		Log.i("DEBUG", "DEBUG: sirena e looper attivati correttamente");
		return START_STICKY; //serve per continuare a far runnare il service
	}
	
	private void startRingtoneControl() throws NoSuchAlgorithmException {
		this.mHandler = new Handler();
		mStatusChecker.run();
	}
	
	private void stopRingtoneControl() {
		this.mHandler.removeCallbacks(mStatusChecker);
	}

	private void startSiren(){
		this.sirenHandler = new SirenHandler(this.context);
		this.sirenHandler.turnVolumeToMax();
		this.sirenHandler.playAlarmSound();
	}
	
	@Override
	public void onDestroy(){
		Log.i("DEBUG", "DEBUG: servizio delle sirene fermato correttamente");
		try{
			doSirenOff();
		}
		catch (Exception e){
			//non si fa proprio niente se la sirena non era attiva
			Log.i("DEBUG", "DEBUG: la sirena non era attiva: non ho fatto niente");
		}
	}
}