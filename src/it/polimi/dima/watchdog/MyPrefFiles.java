package it.polimi.dima.watchdog;

/**
 * Classe che raccoglie tutte le preferenze che si usano in questa applicazione
 * @author claudio
 *
 */
public class MyPrefFiles {

	private MyPrefFiles() {
		throw new IllegalAccessError("This constructur is private");
	}
	
	/**
	 * Stringa che identifica il nome di un file
	 */
	public static final String PREF_INIT = "MyInitFile";
	
	/**
	 * valore chiave per sapere se il wizard è finito o no  
	 */
	public static final String WIZARD_DONE = "wizardDone";
	
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
}
