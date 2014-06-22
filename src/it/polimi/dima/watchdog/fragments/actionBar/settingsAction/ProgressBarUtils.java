package it.polimi.dima.watchdog.fragments.actionBar.settingsAction;

import it.polimi.dima.watchdog.errors.ErrorManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;

public class ProgressBarUtils {
	
	private ProgressDialog progressBar;
	private int progressBarStatus = 0;
	private Context context;
	private Handler progressBarHandler = new Handler();
	
	public static int messageNumber = 0;
	
	public ProgressBarUtils(Context context) {
		this.context = context;
	}
	
	public void initiProgressBar(String message) {
		
		this.progressBar = new ProgressDialog(this.context);
		this.progressBar.setCancelable(true);
		this.progressBar.setMessage(message);
		this.progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		this.progressBar.setProgress(0);
		this.progressBar.setMax(100);
		this.progressBar.show();
		
		//reset progress bar status
		this.progressBarStatus = 0;
		//reset messaggeNumber
		messageNumber = 0;
	}
	
	public void runProgressBar() {
		new Thread(new Runnable() {
			  public void run() {
				while (progressBarStatus < 100) {

				  // process some tasks
				  progressBarStatus = checkSMP();

				  // your computer is too fast, sleep 1 second
				  try {
					Thread.sleep(1000);
				  } catch (InterruptedException e) {
					  ErrorManager.handleFatalError(e.getMessage(), context);
				  }

				  // Update the progress bar
				  progressBarHandler.post(new Runnable() {
					public void run() {
					  progressBar.setProgress(progressBarStatus);
					}
				  });
				}

				// ok, file is downloaded,
				if (progressBarStatus >= 100) {

					// sleep 2 seconds, so that you can see the 100%
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						ErrorManager.handleFatalError(e.getMessage(), context);
					}

					// close the progress bar dialog
					progressBar.dismiss();
				}
			  }
		       }).start();
	}
	
	public int checkSMP() {
		
		
		while(messageNumber < 5) {
			
			switch(messageNumber) {
			case 1: return 20;
			case 2:	return 40;
			case 3: return 60;
			case 4:	return 80;
			}
		}
		return 100;
		
	}

}
