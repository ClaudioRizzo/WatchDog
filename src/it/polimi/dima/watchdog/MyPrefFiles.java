package it.polimi.dima.watchdog;

import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Classe che raccoglie tutte le preferenze che si usano in questa applicazione
 * @author claudio
 *
 */
public class MyPrefFiles {

	private MyPrefFiles() {
		throw new IllegalAccessError("This constructur is private");
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
	
	
	//Qui iniziano i metodi
	
	public static String getMyPreference(String fileName, String key, Context ctx) throws NoSuchPreferenceFoundException {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String answer = sp.getString(key, null);//TODO ricordarsi di settarla in inizializzazione
		
		if(answer == null){
			throw new NoSuchPreferenceFoundException("I/O Error");
		}
		return answer;
	}
	
	public static void setMyPreference(String fileName, String key, String value,  Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(MyPrefFiles.KEYRING, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
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
	
	
}
