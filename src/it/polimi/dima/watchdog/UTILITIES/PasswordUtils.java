package it.polimi.dima.watchdog.UTILITIES;

import android.annotation.SuppressLint;
import java.io.UnsupportedEncodingException;
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
	public static final int SALT_LENGTH = 32; // 256 bit, come la lunghezza del
												// digest di sha256

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
	 * Genera il digest a partire dal plaintext e da un algoritmo.
	 * 
	 * @param text : il plaintext
	 * @param algorithm : l'algoritmo scelto
	 * @return l'hash di text con algorithm
	 * @throws NoSuchAlgorithmException se algorithm non è un algoritmo di hash valido
	 */
	public static byte[] getByteHash(String text, String algorithm)
			throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		try {
			byte[] hash = digest.digest(text.getBytes(PasswordUtils.UTF_8));
			return hash;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Compara due stringhe
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

}
