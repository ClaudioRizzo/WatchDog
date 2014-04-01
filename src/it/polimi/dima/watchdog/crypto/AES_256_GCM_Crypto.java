package it.polimi.dima.watchdog.crypto;

import android.annotation.SuppressLint;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.xml.bind.DatatypeConverter;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
//import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

/**
 * Classe che implementa l'algoritmo AES-256-GCM in encryption e decryption. La generazione della chiave è
 * demandata all'utente in qualche altro modo (in pratica i costruttori devono ricevere o una stringa di
 * esadecimali, o un array di byte, o la chiave già pronta).
 * 
 * @author emanuele
 *
 */
public class AES_256_GCM_Crypto implements Crypto{
	
	private String ptx;
	private String keyString;
	private byte[] keyValue;
	private Key key;
	private byte[] ctx;
	private String base64_ctx;
	private final byte[] iv = {

        // This is the authentication tag length (12).
        (byte) 0x0c,

        // This is the length of the authenticated data (10).
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
        (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x0a,

        // This is the GCM IV data used for encryption and decryption.
        // These 12-bytes MUST be unique for a key choice
        // for security reasons.
        (byte) 0x10, (byte) 0x11, (byte) 0x12, (byte) 0x13,
        (byte) 0x14, (byte) 0x15, (byte) 0x16, (byte) 0x17,
        (byte) 0x18, (byte) 0x19, (byte) 0x20, (byte) 0x21,
	}; //serve per generare i parametri per il modo GCM
	private final int tagLength = 128; //serve per generare i parametri per il modo GCM
	
	
	
	/**
	 * Costruttore in encrypt mode con plaintext e chiave sotto forma di stringa di caratteri esadecimali (0-F);
	 * se la stringa non è lunga 64 (ogni carattere esadecimale rappresenta un nibble da 4 bit --> 64*4 = 256 bit
	 * di chiave).
	 * 
	 * @param ptx : il plaintext da cifrare
	 * @param keyString : la chiave sotto forma di stringa di caratteri esadecimali (0-F)
	 */
	public AES_256_GCM_Crypto(String ptx, String keyString){
		
		if(keyString.length() != 64){
			throw new IllegalArgumentException("La chiave non è di 256 bit");
		}
		this.ptx = ptx;
		this.keyString = keyString;
		this.keyValue = DatatypeConverter.parseHexBinary(this.keyString);
		this.key = new SecretKeySpec(this.keyValue, "AES");
	}
	
	/**
	 * Costruttore in encrypt mode con plaintext e chiave sotto forma di stringa di array di byte; viene
	 * lanciata un'eccezione se l'array di byte è lungo meno di 32 byte (32*8 = 256 bit).
	 * 
	 * @param ptx : il plaintext da cifrare
	 * @param keyValue : la chiave sotto forma di array di byte
	 */
	public AES_256_GCM_Crypto(String ptx, byte[] keyValue){
		
		if(keyValue.length != 32){
			throw new IllegalArgumentException("La chiave non è di 256 bit");
		}
		this.ptx = ptx;
		this.keyValue = keyValue;
		this.key = new SecretKeySpec(this.keyValue, "AES");
	}
	
	/**
	 * Costruttore in encrypt mode con plaintext e chiave passata già pronta; se la chiave non avrà la stessa
	 * lunchezza di una generata a partire da una stringa di 256 bit, allora viene lanciata un'eccezione.
	 * 
	 * @param ptx : il plaintext da cifrare
	 * @param key : la chiave già generata
	 */
	public AES_256_GCM_Crypto(String ptx, Key key){
		String dummy = "0000000000000000000000000000000000000000000000000000000000000000";
		byte[] dummyArray = DatatypeConverter.parseHexBinary(dummy);
		Key dummyKey = new SecretKeySpec(dummyArray, "AES");
		
		if(key.getEncoded().length != dummyKey.getEncoded().length){
			throw new IllegalArgumentException("La chiave non è di 256 bit");
		}
		
		this.ptx = ptx;
		this.key = key;
	}
	
	/**
	 * Costruttore in decrypt mode con chiave e ciphertext passato come array di byte.
	 * 
	 * @param key : la chiave
	 * @param ctx : il ciphertext passato come array di byte
	 */
	public AES_256_GCM_Crypto(Key key, byte[] ctx){
		this.key = key;
		this.ctx = ctx;
		this.base64_ctx = DatatypeConverter.printBase64Binary(this.ctx); //giusto per completezza
	}
		
	/**
	 * Costruttore in decrypt mode con chiave e ciphertext passato come stringa in base64; se la stringa non
	 * matcha il pattern delle stringhe ben formate in base64 viene lanciata un'eccezione.
	 * 
	 * @param key : la chiave
	 * @param ctx : il ciphertext passato come stringa in base64
	 */
	public AES_256_GCM_Crypto(Key key, String ctx){
		//nulla di esoterico: solo l'espressione regolare che matcha una stringa in base64: una serie opzionale
		//di gruppi di 4 caratteri (ammessi numeri, lettere maiuscole e non, più e slash) seguita da un gruppo
		//di quattro caratteri in cui uno o due uguali fanno da padding.
		String pattern = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
		if(!ctx.matches(pattern)){
			throw new IllegalArgumentException("La stringa passata come chiave non è in base64");
		}
		this.key = key;
		this.base64_ctx = ctx;
		this.ctx = DatatypeConverter.parseBase64Binary(this.base64_ctx);
	}
	
	/**
	 * Esegue l'encrypt di this.ptx con this.key; mette tutto in this.ctx, lo converte in base64 e lo ritorna.
	 */
	@SuppressLint("NewApi")
	public String encrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException{
		
		//aggiunge il provider che supporta GCM
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		//setta i parametri per il modo GCM a partire dall'iv e dalla tag length
		GCMParameterSpec gmcps = new GCMParameterSpec(this.tagLength, this.iv);
		
		//crea una nuova istanza di uno stato dell'aes con GCM come modo e nessun padding (con GCM non serve)
		Cipher ctx = Cipher.getInstance("AES/GCM/NoPadding", "BC");
		
		//inizializza lo stato
		ctx.init(Cipher.ENCRYPT_MODE, this.key, gmcps);
		
		//esegue tutti i round dell'aes
		this.ctx = ctx.doFinal(this.ptx.getBytes());
		
		//converte lo stato finale in una stringa base64 e la salva in base64_ctx
		this.base64_ctx = DatatypeConverter.printBase64Binary(this.ctx);
		
		//ritorna una stringa in base64 che rappresenta il ciphertext
		return this.base64_ctx;
	}
	
	/**
	 * Esegue il decrypt di this.ctx con this.key; mette tutto in ptx, lo converte in stringa e lo ritorna.
	 */
	@SuppressLint("NewApi")
	public String decrypt() throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException{
		
		//aggiunge il provider che supporta GCM
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		//setta i parametri per il modo GCM a partire dall'iv e dalla tag length
		GCMParameterSpec gmcps = new GCMParameterSpec(this.tagLength, this.iv);
		
		//crea una nuova istanza di uno stato dell'aes con GCM come modo e nessun padding (con GCM non serve)
		Cipher ctx = Cipher.getInstance("AES/GCM/NoPadding", "BC");
		
		//inizializza lo stato
		ctx.init(Cipher.DECRYPT_MODE, this.key, gmcps);
		
		//esegue tutti i round dell'aes al contrario
		byte[] ptx = ctx.doFinal(this.ctx);
		
		//converte lo stato finale in una stringa
		this.ptx = new String(ptx);
		
		//ritorna il messaggio decriptato
		return this.ptx;
	}
	

}
