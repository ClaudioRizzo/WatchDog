package it.polimi.dima.watchdog.crypto;

import it.polimi.dima.watchdog.utilities.CryptoUtility;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;

import android.util.Base64;

/**
 * Classe che wrappa la crittazione e la decrittazione con AES 256 in GCM mode of operation.
 * 
 * @author emanuele
 *
 */
public class AES256GCM implements Crypto{
	
	private static final boolean ENCRYPT = true;
	private static final boolean DECRYPT = false;
	private static final int TAG_LENGTH = 128; //in bit
	private byte[] plaintext;
	private Key key;
	private byte[] iv;
	private byte[] ciphertext;
	
	/**
	 * Costruttore in encrypt mode a cui vengono passati la chiave, il messaggio da crittografare e il
	 * vettore di inizializzazione.
	 * 
	 * @param key : la chiave crittografica
	 * @param plaintext : il messaggio da crittografare
	 * @param iv : il vettore di inizializzazione
	 */
	public AES256GCM(Key key, String plaintext, byte[] iv){
		this.key = key;
		this.plaintext = plaintext.getBytes();
		this.iv = iv;
	}
	
	/**
	 * Costruttore in encrypt mode a cui vengono passati i byte della chiave, il messaggio da crittografare
	 * e il vettore di inizializzazione.
	 * 
	 * @param key : i byte della chiave crittografica
	 * @param plaintext : il messaggio da crittografare
	 * @param iv : il vettore di inizializzazione
	 */
	public AES256GCM(byte[] key, String plaintext, byte[] iv) {
		this.key = new SecretKeySpec(key, CryptoUtility.AES_256);
		this.plaintext = plaintext.getBytes();
		this.iv = iv;
	}
	
	/**
	 * Costruttore in decrypt mode a cui vengono passati la chiave, il ciphertext da decrittare e il vettore
	 * di inizializzazione.
	 * 
	 * @param key : la chiave crittografica
	 * @param ciphertext : il messaggio da decrittare
	 * @param iv : il vettore di inizializzazione
	 */
	public AES256GCM(Key key, byte[] ciphertext, byte[] iv){
		this.key = key;
		this.ciphertext = ciphertext;
		this.iv = iv;
	}
	
	/**
	 * Costruttore in decrypt mode a cui vengono passati i byte della chiave, il ciphertext da decrittare e
	 * il vettore di inizializzazione.
	 * 
	 * @param key : i byte della chiave crittografica
	 * @param ciphertext : il messaggio da decrittare
	 * @param iv : il vettore di inizializzazione
	 */
	public AES256GCM(byte[] key, byte[] ciphertext, byte[] iv) {
		this.key = new SecretKeySpec(key, CryptoUtility.AES_256);
		this.ciphertext = ciphertext;
		this.iv = iv;
	}
	
	/**
	 * Ottiene il ciphertext sotto forma di byte[]
	 * @return
	 */
	public byte[] getCiphertext(){
		return this.ciphertext;
	}
	
	/**
	 * Critta il plaintext con la chiave e torna il ciphertext convertito in Base64 dopo aver assegnato la
	 * sua versione byte[] all'attributo ciphertext.
	 */
	public String encrypt() throws IllegalStateException, InvalidCipherTextException {
		KeyParameter key = new KeyParameter(this.key.getEncoded());
		AEADParameters parameters = new AEADParameters(key, AES256GCM.TAG_LENGTH, this.iv);
		GCMBlockCipher cipher = new GCMBlockCipher(new AESFastEngine());
		cipher.init(AES256GCM.ENCRYPT, parameters);
		byte[] ciphertext = new byte[cipher.getOutputSize(this.plaintext.length)];
		int ciphertextLength = cipher.processBytes(this.plaintext, 0, this.plaintext.length, ciphertext, 0);
		ciphertextLength += cipher.doFinal(ciphertext, ciphertextLength);
		this.ciphertext = ciphertext;
		
		return Base64.encodeToString(this.ciphertext, Base64.DEFAULT);
	}
	
	/**
	 * Decritta il ciphertext con la chiave e torna il plaintext come array di byte dopo averlo assegnato
	 * all'attributo plaintext.
	 */
	public byte[] decrypt() throws IllegalStateException, InvalidCipherTextException {
		KeyParameter key = new KeyParameter(this.key.getEncoded());
		AEADParameters parameters = new AEADParameters(key, AES256GCM.TAG_LENGTH, this.iv);
		GCMBlockCipher cipher = new GCMBlockCipher(new AESFastEngine());
		cipher.init(AES256GCM.DECRYPT, parameters);
        byte[] plaintext = new byte[cipher.getOutputSize(this.ciphertext.length)];
        int plaintextLength = cipher.processBytes(this.ciphertext, 0, this.ciphertext.length, plaintext, 0);
        plaintextLength += cipher.doFinal(plaintext, plaintextLength);
        this.plaintext = plaintext;
        
        return this.plaintext;
	}

}
