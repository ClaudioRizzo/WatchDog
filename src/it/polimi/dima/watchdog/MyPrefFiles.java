package it.polimi.dima.watchdog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe che raccoglie tutte le preferenze che si usano in questa applicazione
 * @author claudio, emanuele
 *
 */
public class MyPrefFiles {

	private MyPrefFiles() {
		throw new IllegalAccessError("Costruttore privato!!!");
	}	
	
	//QUI I NOMI DEI FILE
	
	/**
	 * Stringa che identifica il nome di un file
	 */
	public static final String PREF_INIT = "my_init_file";
	
	/**
	 * File che contiene l'hash della propria password e il sale corrispondente.
	 */
	public static final String PASSWORD_AND_SALT = "password_and_salt";
	
	/**
	 * file che contiene le chiavi pubbliche già validate.
	 */
	public static final String KEYRING = "keyring";
	
	/**
	 * file che contiene i sali delle password di tutti i telefoni abbinati (in modo che quando bisogna digitare
	 * su questo telefono una password di un altro telefono da "salvare" si possa fare l'hash correttamente).
	 */
	public static final String HASHRING = "hashring"; //OVVIO PUN xD
	
	/**
	 * file che contiene le proprie chiavi.
	 */
	public static final String MY_KEYS = "mykeys";
	
	/**
	 * file che contiene domanda e risposta segrete per l'SMP.
	 */
	public static final String SECRET_Q_A = "secret_question_and_answer";
	
	/**
	 * file che contiene le chiavi pubbliche non ancora validate.
	 */
	public static final String KEYSQUARE = "keysquare"; //OVVIO PUN xD
	
	/**
	 * file che contiene la chiave pubblica da usare per inviare il prossimo messaggio o per decrittare il primo
	 * che arriva.
	 */
	public static final String CURRENT_AES_KEY = "current_aes_key";
	
	/**
	 * file che contiene i segreti condivisi tra me e ogni telefono associato.
	 */
	public static final String SHARED_SECRETS = "shared_secrets";
	
	/**
	 * file che per ogni numero di telefono tiene traccia di cosa è stato già fatto e di cosa manca da fare
	 * nel SMP. I riferimenti possono essere cancellati se incompleti alla ricezione di IDontWantToAssociate
	 * e se completi alla ricezione di un apposito messaggio di reset.
	 */
	public static final String SMP_STATUS = "smp_status";
	
	//DA QUI IN POI CHIAVI
	
	/**
	 * valore chiave per sapere se il wizard è finito o no.
	 */
	public static final String WIZARD_DONE = "wizardDone";
	
	/**
	 * valore "chiave" per la propria chiave pubblica.
	 */
	public static final String MY_PUB = "my_public_key";
	
	/**
	 * valore "chiave" per la propria chiave privata. 
	 */
	public static final String MY_PRIV = "my_private_key";
	
	/**
	 * valore chiave per l'hash della propria password.
	 */
	public static final String MY_PASSWORD_HASH = "my_password_hash";
	
	/**
	 * valore chiave per il sale con cui è stato costruito l'hash della propria password.
	 */
	public static final String MY_PASSWORD_SALT = "my_password_salt";
	
	/**
	 * valore chiave per la risposta segreta.  
	 */
	public static final String SECRET_ANSWER = "secret_answer";
	
	/**
	 * valore chiave per la domanda segreta.  
	 */
	public static final String SECRET_QUESTION = "secret_question";
	
	/**
	 * valore "chiave" per la chiave corrente dell'AES.
	 */
	public static final String SESSION_KEY = "session_key";
	
	
	//Qui i nomi di chiave parziali per il file smp_status
	
	public static final String PUB_KEY_REQUEST_FORWARDED = "pkrf";
	public static final String PUB_KEY_RECEIVED = "pkr";
	public static final String SECRET_QUESTION_FORWARDED = "sqf";
	public static final String HASH_RECEIVED = "hr";
	public static final String ACK_AND_SALT_FORWARDED = "asf";
	public static final String PUB_KEY_REQUEST_RECEIVED = "pkrr";
	public static final String PUB_KEY_FORWARDED = "pkf";
	public static final String SECRET_QUESTION_RECEIVED = "sqr";
	public static final String HASH_FORWARDED = "hf";
	public static final String ACK_AND_SALT_RECEIVED = "asr";
	
	
	//Qui iniziano i metodi
	
	public static String getMyPreference(String fileName, String key, Context ctx) throws NoSuchPreferenceFoundException {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String preference = sp.getString(key, null);
		
		if(preference == null){
			throw new NoSuchPreferenceFoundException("I/O Error");
		}
		return preference;
	}
	
	//se value è una chiave deve essere Base64
	public static void setMyPreference(String fileName, String key, String value,  Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	public static void setMyPreference(String filename, String firstKeyHalf, String secondKeyHalf, String value, Context ctx){
		
	}
	
	public static void deleteMyPreference(String fileName, String key, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
	
	public static boolean existsPreference(String fileName, String key, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String value = sp.getString(key, null);
		if(value == null){
			return false;
		}
		return true;
	}
	
	/**
	 * Se qualcosa va storto in SMP o ECDH tutte le preferenze relative all'altro utente vanno cancellate.
	 */
	public static void erasePreferences(String phoneNumber, Context ctx){
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYRING, phoneNumber, ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYSQUARE, phoneNumber, ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.HASHRING, phoneNumber, ctx);
		}
	}
	
	/**
	 * Metodo che cerca nelle preferenze se esistono riferimenti ad un determinato numero di telefono.
	 * @param phoneNumber
	 * @param ctx
	 * @return
	 */
	public static boolean iHaveSomeReferencesToThisUser(String phoneNumber, Context ctx){
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, phoneNumber, ctx)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, phoneNumber, ctx)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, ctx)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, phoneNumber, ctx)){
			return true;
		}
		return false;
	}
	
	
	public static boolean isSmpSuccessfullyFinished(String phoneNumber, Context ctx){
		List<String> keys = MyPrefFiles.createKeysForSmpStatus(phoneNumber);
		
		for(String s : keys){
			if(!existsPreference(MyPrefFiles.SMP_STATUS, s, ctx)){
				return false;
			}
		}
		return true;
	}
	
	public static Map<String, ?> getPrefMap(String fileName, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getAll();
	}
	
	private static List<String> createKeysForSmpStatus(String phoneNumber){
		List<String> keys = new ArrayList<String>();
		keys.add(phoneNumber + MyPrefFiles.PUB_KEY_REQUEST_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.PUB_KEY_RECEIVED);
		keys.add(phoneNumber + MyPrefFiles.SECRET_QUESTION_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.HASH_RECEIVED);
		keys.add(phoneNumber + MyPrefFiles.ACK_AND_SALT_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.PUB_KEY_REQUEST_RECEIVED);
		keys.add(phoneNumber + MyPrefFiles.PUB_KEY_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.SECRET_QUESTION_RECEIVED);
		keys.add(phoneNumber + MyPrefFiles.HASH_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.ACK_AND_SALT_RECEIVED);
		return keys;
	}
	
}
