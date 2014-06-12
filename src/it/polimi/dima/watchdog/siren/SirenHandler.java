package it.polimi.dima.watchdog.siren;

import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.io.File;
import android.content.Context;
import android.media.AudioManager;

public class SirenHandler {

	private File siren;
	private Context context;
	
	private SirenHandler(Context context){
		this.context = context;
		this.siren = new File(MyPrefFiles.SIREN_ON_FILE);
	}
	
	public void manageRequest(String code) {
		if(code.equals(SMSUtility.SIREN_ON)){
			turnVolumeToMax();
			playAlarmSound();
		}
		else if(code.equals(SMSUtility.SIREN_OFF)){
			stopAlarmSound();
		}
		else throw new IllegalArgumentException("Operazione non riconosciuta!!!");
	}
	
	private void turnVolumeToMax(){
		AudioManager man = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
		man.setStreamVolume(AudioManager.STREAM_RING, man.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_ALLOW_RINGER_MODES);
	}
	
	private void playAlarmSound() {
		SirenWrapper sirenWrapper = SirenWrapper.getInstance(this.context, this.siren);
		if(!sirenWrapper.getSiren().isPlaying()){
			sirenWrapper.play();
		}
	}

	private void stopAlarmSound() {
		SirenWrapper sirenWrapper = SirenWrapper.getInstance(this.context, this.siren);
		sirenWrapper.stop();
	}
}