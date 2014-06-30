package it.polimi.dima.watchdog.crypto;

import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.crypto.InvalidCipherTextException;
import org.spongycastle.crypto.engines.AESFastEngine;
import org.spongycastle.crypto.modes.GCMBlockCipher;
import org.spongycastle.crypto.params.AEADParameters;
import org.spongycastle.crypto.params.KeyParameter;

/**
 * Classe che wrappa la crittazione e la decrittazione con AES 256 in GCM mode of operation.
 * 
 * @author emanuele
 *
 */
public class AES256GCM {
	
	private static final boolean ENCRYPT = true;
	private static final boolean DECRYPT = false;
	private static final int TAG_LENGTH = 128; //in bit
	private byte[] plaintext;
	private Key key;
	private byte[] iv;
	private byte[] ciphertext;
	
	
	/**
	 * Costruttore a cui vengono passati chiave (Key), plaintext o ciphertext e flag di encryption/decryption.
	 * (WARNING: chiamare solo dal wrapper)
	 * 
	 * @param key : la chiave dell'AES sotto forma di Key
	 * @param plaintextOrCiphertext : un array di byte interpretabile come plaintext o ciphertext
	 * @param iv : l'initialization vector di GCM
	 * @param flag : indica se crittare o decrittare
	 */
	protected AES256GCM(Key key, byte[] plaintextOrCiphertext, byte[] iv, int flag){
		if(flag == CryptoUtility.ENC){
			encryptionConstructorAES256GCM(key, plaintextOrCiphertext, iv);
		}
		else if(flag == CryptoUtility.DEC){
			decryptionConstructorAES256GCM(key, plaintextOrCiphertext, iv);
		}
		else{
			throw new IllegalArgumentException("Operazione non riconosciuta!!!");
		}
	}
	
	/**
	 * Costruttore a cui vengono passati chiave (byte[]), plaintext o ciphertext e flag di encryption/decryption.
	 * (WARNING: chiamare solo dal wrapper)
	 * 
	 * @param key : la chiave dell'AES sotto forma di array di byte
	 * @param plaintextOrCiphertext : un array di byte interpretabile come plaintext o ciphertext
	 * @param iv : l'initialization vector di GCM
	 * @param flag : indica se crittare o decrittare
	 */
	protected AES256GCM(byte[] key, byte[] plaintextOrCiphertext, byte[] iv, int flag){
		if(flag == CryptoUtility.ENC){
			encryptionConstructorAES256GCM(key, plaintextOrCiphertext, iv);
		}
		else if(flag == CryptoUtility.DEC){
			decryptionConstructorAES256GCM(key, plaintextOrCiphertext, iv);
		}
		else{
			throw new IllegalArgumentException("Operazione non riconosciuta!!!");
		}
	}
	
	
	/**
	 * Metodo ausiliario del costruttore in encrypt mode a cui vengono passati la chiave, il messaggio da crittografare e il
	 * vettore di inizializzazione.
	 * 
	 * @param key : la chiave crittografica
	 * @param plaintext : il messaggio da crittografare
	 * @param iv : il vettore di inizializzazione
	 */
	private void encryptionConstructorAES256GCM(Key key, byte[] plaintext, byte[] iv){
		this.key = key;
		this.plaintext = plaintext;
		this.iv = iv;
	}
	
	/**
	 * Metodo ausiliario del costruttore in encrypt mode a cui vengono passati i byte della chiave, il messaggio da crittografare
	 * e il vettore di inizializzazione.
	 * 
	 * @param key : i byte della chiave crittografica
	 * @param plaintext : il messaggio da crittografare
	 * @param iv : il vettore di inizializzazione
	 */
	private void encryptionConstructorAES256GCM(byte[] key, byte[] plaintext, byte[] iv) {
		this.key = new SecretKeySpec(key, CryptoUtility.AES_256);
		this.plaintext = plaintext;
		this.iv = iv;
	}
	
	/**
	 * Metodo ausiliario del costruttore in decrypt mode a cui vengono passati la chiave, il ciphertext da decrittare e il vettore
	 * di inizializzazione.
	 * 
	 * @param key : la chiave crittografica
	 * @param ciphertext : il messaggio da decrittare
	 * @param iv : il vettore di inizializzazione
	 */
	private void decryptionConstructorAES256GCM(Key key, byte[] ciphertext, byte[] iv){
		this.key = key;
		this.ciphertext = ciphertext;
		this.iv = iv;
	}
	
	/**
	 * Metodo ausiliario del costruttore in decrypt mode a cui vengono passati i byte della chiave, il ciphertext da decrittare e
	 * il vettore di inizializzazione.
	 * 
	 * @param key : i byte della chiave crittografica
	 * @param ciphertext : il messaggio da decrittare
	 * @param iv : il vettore di inizializzazione
	 */
	private void decryptionConstructorAES256GCM(byte[] key, byte[] ciphertext, byte[] iv) {
		this.key = new SecretKeySpec(key, CryptoUtility.AES_256);
		this.ciphertext = ciphertext;
		this.iv = iv;
	}
	
	/**
	 * Ottiene il ciphertext sotto forma di byte[].
	 * @return il ciphertext
	 */
	public byte[] getCiphertext(){
		return this.ciphertext;
	}
	
	/**
	 * Critta il plaintext con la chiave e torna il ciphertext convertito in Base64 dopo aver assegnato la
	 * sua versione byte[] all'attributo ciphertext. (WARNING: chiamare solo dal wrapper)
	 */
	protected byte[] encrypt() throws IllegalStateException, InvalidCipherTextException {
		KeyParameter key = new KeyParameter(this.key.getEncoded());
		AEADParameters parameters = new AEADParameters(key, AES256GCM.TAG_LENGTH, this.iv);
		GCMBlockCipher cipher = new GCMBlockCipher(new AESFastEngine());
		cipher.init(AES256GCM.ENCRYPT, parameters);
		byte[] ciphertext = new byte[cipher.getOutputSize(this.plaintext.length)];
		int ciphertextLength = cipher.processBytes(this.plaintext, 0, this.plaintext.length, ciphertext, 0);
		ciphertextLength += cipher.doFinal(ciphertext, ciphertextLength);
		this.ciphertext = ciphertext;
		return this.ciphertext;
	}
	
	/**
	 * Decritta il ciphertext con la chiave e torna il plaintext come array di byte dopo averlo assegnato
	 * all'attributo plaintext. (WARNING: chiamare solo dal wrapper)
	 */
	protected byte[] decrypt() throws IllegalStateException, InvalidCipherTextException {
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