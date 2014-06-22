package it.polimi.dima.watchdog.password;

import android.annotation.SuppressLint;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * 
 * @author claudio, emanuele
 *
 */
public class PasswordUtils {
	/**
	 * Codifica Utf-8
	 */
	public static final String UTF_8 = "UTF-8";
	/**
	 * Regex per le password
	 */
	public static final String PASSWORD_REGEX = "(?!^[0-9]*$)(?!^[a-zA-Z]*$)^([a-zA-Z0-9-_@%&#,;:./<!>=?`~]{8,20})$";
	/**
	 * Lunghezza del sale con cui verrà salata una password
	 */
	public static final int SALT_LENGTH = 32;
	/**
	 * Lunghezza dell'hash di una password
	 */
	public static final int HASH_LENGTH = 32;

	/**
	 * Genera il prossimo sale con un generatore di numeri casuali.
	 * @return il prossimo sale
	 */
	@SuppressLint("TrulyRandom")
	public static byte[] nextSalt() {
		byte[] salt = new byte[SALT_LENGTH];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(salt);
		return salt;
	}
	
	/**
	 * Crea e ritorna un hash a partire da plaintext, sale e algoritmo.
	 * 
	 * @param plaintext : l plaintext di cui si vuole calcolare l'hash
	 * @param salt : il sale
	 * @param algorithm : l'algoritmo
	 * @return l'hash salato del plaintext
	 * @throws NoSuchAlgorithmException se l'algoritmo passato non esiste
	 */
	public static byte[] computeHash(byte[] plaintext, byte[] salt, String algorithm) throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		byte[] ptx;
		
		if(salt != null){
			ptx = new byte[plaintext.length + salt.length];
			System.arraycopy(plaintext, 0, ptx, 0, plaintext.length);
			System.arraycopy(salt, 0, ptx, plaintext.length, salt.length);
		}
		else{
			ptx = new byte[plaintext.length];
			System.arraycopy(plaintext, 0, ptx, 0, plaintext.length);
		}
		return digest.digest(ptx);
	}

	/**
	 * Compara due stringhe.
	 * 
	 * @param p1 : la prima stringa
	 * @param p2 : la seconda stringa
	 * @return true se le stringhe sono uguali, false altrimenti
	 */
	public static boolean comparePasswords(String p1, String p2) {
		if (p1.equals(p2)) {
			return true;
		} else {
			return false;
		}
	}
	
	public static boolean isEmpty(String string){
		return string == null || string.equals("");
	}
}