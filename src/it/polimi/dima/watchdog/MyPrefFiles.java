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
	 * valore chiave per la password salata
	 */
	public static final String PSSWD_HASH_SALTED = "password_hash_salted";
	
	/**
	 * valore chiave per il sale della password
	 */
	public static final String SALT = "salt";
	
	/**
	 * valore chiave per la key_pub
	 */
	public static final String PUB_KEY = "key_pub";
	
	/**
	 * valore chiave per la key_private
	 */
	public static final String PRI_KEY = "private_key";
	
	/**
	 * Domanda segreta per l'SMP
	 */
	public static final String SECRET_Q = "secretQuestion";
	
	/**
	 * Risposta segreta per l'SMP
	 */
	public static final String SECRET_A = "secretAnswer";
}
