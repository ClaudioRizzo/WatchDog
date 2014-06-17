package it.polimi.dima.watchdog.siren;

import it.polimi.dima.watchdog.R;

import java.io.File;

import android.content.ContentValues;
import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;

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
		Uri uri =  Uri.parse("android.resource://it.polimi.watchdog/raw/sirenon");
		context.getContentResolver().delete(uri, null, null);
		Uri newUri = context.getContentResolver().insert(uri, new ContentValues());
	
		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
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
