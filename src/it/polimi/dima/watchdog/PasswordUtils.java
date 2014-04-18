package it.polimi.dima.watchdog;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtils {

	public static final int SALT_LENGTH = 32; // 256 bit, come la lunghezza del
												// digest di sha256

	public static byte[] nextSalt() {
		byte[] salt = new byte[SALT_LENGTH];
		SecureRandom sr = new SecureRandom();
		sr.nextBytes(salt);
		return salt;
	}

	public static byte[] getByteHash(String text, String algorithm)
			throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance(algorithm);
		try {
			byte[] hash = digest.digest(text.getBytes("UTF-8"));
			return hash;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean comparePasswords(String p1, String p2) {
		if (p1.equals(p2)) {
			return true;
		} else {
			return false;
		}

	}

}