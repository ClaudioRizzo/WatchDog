package it.polimi.dima.watchdog.siren;

import it.polimi.dima.watchdog.R;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Classe che wrappa la sirena da riprodurre in seguito a siren on.
 * 
 * @author emanuele
 *
 */
public class SirenWrapper {
	
	private static SirenWrapper sirenWrapper;
	private Ringtone siren;
	
	
	private SirenWrapper(Context context){
		Uri uri =  Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.sirenon);
		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, uri);
		this.siren = RingtoneManager.getRingtone(context, RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE));
	}
	
	public static SirenWrapper getInstance(Context context){
		if(sirenWrapper == null){
			sirenWrapper = new SirenWrapper(context);
		}
		return sirenWrapper;
	}
	
	public Ringtone getSiren(){
		return this.siren;
	}
	
	public void play(){
		this.siren.play();
	}
	
	public void stop(){
		this.siren.stop();
	}
}
