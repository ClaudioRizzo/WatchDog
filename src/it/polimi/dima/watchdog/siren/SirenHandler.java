package it.polimi.dima.watchdog.siren;

import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.io.File;
import android.content.Context;
import android.media.AudioManager;

public class SirenHandler {

	private File siren;
	private Context context;
	private AudioManager audioManager;
	private SirenWrapper sirenWrapper;
	
	public SirenHandler(Context context){
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
	
	public void turnVolumeToMax(){
		this.audioManager = (AudioManager) this.context.getSystemService(Context.AUDIO_SERVICE);
		this.audioManager.setStreamVolume(AudioManager.STREAM_RING, this.audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), AudioManager.FLAG_ALLOW_RINGER_MODES);
	}
	
	public void playAlarmSound() {
		this.sirenWrapper = SirenWrapper.getInstance(this.context, this.siren);
		if(!this.sirenWrapper.getSiren().isPlaying()){
			this.sirenWrapper.play();
		}
	}

	public void stopAlarmSound() {
		this.sirenWrapper = SirenWrapper.getInstance(this.context, this.siren);
		this.sirenWrapper.stop();
	}
}