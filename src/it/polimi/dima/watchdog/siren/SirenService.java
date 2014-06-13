package it.polimi.dima.watchdog.siren;

import it.polimi.dima.watchdog.utilities.SMSUtility;

import java.security.NoSuchAlgorithmException;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

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
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		this.context = getApplicationContext();
	    try {
			return handleCommand(intent);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			Log.i("DEBUG", "DEBUG: servizio non partito correttamente!!!");
			this.stopSelf();
			return START_NOT_STICKY;
		}
	}

	private int handleCommand(Intent intent) throws NoSuchAlgorithmException {
		String command = intent.getExtras().getString(SMSUtility.COMMAND);
		
		if(command == SMSUtility.SIREN_ON){
			return doSirenon();
		}
		else {
			throw new IllegalArgumentException("comando non riconosciuto!!!");
		}
	}

	private int doSirenOff() {
		stopRingtoneControl();
		this.sirenHandler.stopAlarmSound();
		this.stopSelf();
		return START_NOT_STICKY; //serve per notificare lo stop
	}

	private int doSirenon() throws NoSuchAlgorithmException {
		startSiren();
		startRingtoneControl();
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
	
	public static boolean isMyServiceRunning(Context context) {
		ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		String myServiceName = "it.polimi.dima.watchdog.siren.SirenService"; //TODO
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (myServiceName.equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}
}
