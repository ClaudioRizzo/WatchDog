package it.polimi.dima.watchdog.sms.timeout;

import android.os.CountDownTimer;
import android.util.Log;

public class TimeoutCountDown extends CountDownTimer {

	public TimeoutCountDown(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFinish() {
		Log.i("DEBUG", "DEBUG TIMEOUT expired");
		cancel();
		
	}

}
