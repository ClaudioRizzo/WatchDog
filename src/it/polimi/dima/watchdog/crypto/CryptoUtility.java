package it.polimi.dima.watchdog.crypto;

import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;

import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;

import org.spongycastle.crypto.InvalidCipherTextException;

/**
 * Classe che contiene le stringhe che identificano gli algoritmi usati.
 * 
 * @author emanuele
 *
 */
public class CryptoUtility {
	
	//QUI LE STRINGHE CHE INDICANO ALGORITMI CRITTOGRAFICI
	
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
	
	
	
	//QUI UTILITIES VARIE
	
	/**
	 * Encryption flag per AES
	 */
	public static final int ENC = 0;
	/**
	 * Decryption flag per AES
	 */
	public static final int DEC = 1;
	/**
	 * Spongycastle provider
	 */
	public static final String SC = "SC";
	
	
	
	//QUI I METODI
	
	/**
	 * Wrapper che effettua e ritorna una firma ECDSA a partire da plaintext e chiave privata.
	 * 
	 * @param ptx : il plaintext
	 * @param priv : la chiave privata
	 * @return la firma
	 * @throws NoSignatureDoneException
	 * @throws NotECKeyException
	 */
	public static byte[] doSignature(byte[] ptx, PrivateKey priv) throws NoSignatureDoneException, NotECKeyException{
		ECDSA_Signature signer = new ECDSA_Signature(ptx, priv);
		signer.sign();
		return signer.getSignature();
	}
	
	/**
	 * Wrapper che verifica una firma ECDSA a partire da plaintext, firma e chiave pubblica.
	 * 
	 * @param ptx : il plaintext
	 * @param signature: la firma digitale
	 * @param pub : la chiave pubblica per la verifica
	 * @return true se la firma è verificata, false altrimenti
	 * @throws ErrorInSignatureCheckingException 
	 * @throws NotECKeyException 
	 */
	public static boolean verifySignature(byte[] ptx, byte[] signature, PublicKey pub) throws ErrorInSignatureCheckingException, NotECKeyException{
		ECDSA_Signature verifier = new ECDSA_Signature(ptx, pub, signature);
		return verifier.verifySignature();
	}
	
	/**
	 * Wrapper che effettua encryption o decryption con AES-256-GCM.
	 * 
	 * @param plaintextOrCiphertext : il plaintext o il ciphertext
	 * @param key : la chiave (Key)
	 * @param iv : l'initialization vector di GCM
	 * @param flag : indica se crittare o decrittare
	 * @return il ciphertext in caso di encryption o il plaintext in caso di decryption
	 * @throws IllegalStateException
	 * @throws InvalidCipherTextException
	 */
	public static byte[] doEncryptionOrDecryption(byte[] plaintextOrCiphertext, Key key, byte[] iv, int flag) throws IllegalStateException, InvalidCipherTextException{
		AES256GCM aesManager = new AES256GCM(key, plaintextOrCiphertext, iv, flag);
		if(flag == CryptoUtility.ENC){
			aesManager.encrypt();
			return aesManager.getCiphertext();
		}
		else if (flag == CryptoUtility.DEC){
			return aesManager.decrypt();
		}
		else throw new IllegalArgumentException("Operazione non riconosciuta!!!");
	}
	
	/**
	 * Wrapper che effettua encryption o decryption con AES-256-GCM.
	 * 
	 * @param plaintextOrCiphertext : il plaintext o il ciphertext
	 * @param key : la chiave (byte[])
	 * @param iv : l'initialization vector di GCM
	 * @param flag : indica se crittare o decrittare
	 * @return il ciphertext in caso di encryption o il plaintext in caso di decryption
	 * @throws IllegalStateException
	 * @throws InvalidCipherTextException
	 */
	public static byte[] doEncryptionOrDecryption(byte[] plaintextOrCiphertext, byte[] key, byte[] iv, int flag) throws IllegalStateException, InvalidCipherTextException{
		AES256GCM aesManager = new AES256GCM(key, plaintextOrCiphertext, iv, flag);
		if(flag == CryptoUtility.ENC){
			aesManager.encrypt();
			return aesManager.getCiphertext();
		}
		else if (flag == CryptoUtility.DEC){
			return aesManager.decrypt();
		}
		else throw new IllegalArgumentException("Operazione non riconosciuta!!!");
	}
}
