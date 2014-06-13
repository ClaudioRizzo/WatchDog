package it.polimi.dima.watchdog.siren;

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
	
	
	private SirenWrapper(Context context, File sirenFile){
		ContentValues values = new ContentValues();
	    values.put(MediaStore.MediaColumns.DATA, sirenFile.getAbsolutePath());
	    values.put(MediaStore.MediaColumns.TITLE, "Siren On");
	    values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/*");
	    values.put(MediaStore.Audio.Media.ARTIST, "cloudstrife9999");
	    values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
	    values.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
	    values.put(MediaStore.Audio.Media.IS_ALARM, false);
	    values.put(MediaStore.Audio.Media.IS_MUSIC, false);
	    
		Uri uri = MediaStore.Audio.Media.getContentUriForPath(sirenFile.getAbsolutePath());
		context.getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + sirenFile.getAbsolutePath() + "\"", null);
		Uri newUri = context.getContentResolver().insert(uri, values);
	
		RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, newUri);
		this.siren = RingtoneManager.getRingtone(context, RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE));
	}
	
	public static SirenWrapper getInstance(Context context, File sirenFile){
		if(sirenWrapper == null){
			sirenWrapper = new SirenWrapper(context, sirenFile);
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
