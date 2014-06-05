package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;

import java.security.PrivateKey;

/**
 * Classe che contiene le stringhe che identificano gli algoritmi usati.
 * 
 * @author emanuele
 *
 */
public class CryptoUtility {
	/**
	 * Spongycastle provider
	 */
	public static final String SC = "SC";
	/**
	 * Elliptic Curves
	 */
	public static final String EC = "EC";
	/**
	 * Advanced Encryption Standard con chiave a 256 bit
	 */
	public static final String AES_256 = "AES";
	/**
	 * Advanced Encryption Standard in Galois/Counter Mode of operation senza padding e con chiave a 256 bit
	 */
	public static final String AES_256_GCM_NO_PADDING = "AES/GCM/NoPadding";
	/**
	 * Keyed-Hash Message Authentication Code con Secure-Hash-Algorithm-2 con 256 bit di digest
	 */
	public static final String HMAC_SHA_256 = "HmacSHA256";
	/**
	 * Elliptic Curves Digital Signature Algorithm con Secure-Hash-Algorithm-1
	 */
	public static final String ECDSA_SHA1 = "SHA1withECDSA";
	/**
	 * Pseudo Random Number Generator basato su Secure-Hash-Algorithm-1
	 */
	public static final String SHA1_PRNG = "SHA1PRNG";
	/**
	 * Secure-Hash-Algorithm-2 con 256 bit di digest
	 */
	public static final String SHA_256 = "SHA-256";
	/**
	 * Elliptic Curves Diffie-Hellman key exchange
	 */
	public static final String ECDH = "ECDH";
	/**
	 * Il PRNG di default
	 */
	public static final String SUN = "SUN";
	/**
	 * Encryption flag per AES
	 */
	public static final int ENC = 1;
	
	
	public static byte[] doSignature(byte[] ptx, PrivateKey priv) throws NoSignatureDoneException, NotECKeyException{
		ECDSA_Signature signer = new ECDSA_Signature(ptx, priv);
		signer.sign();
		return signer.getSignature();
	}
}
