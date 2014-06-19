package it.polimi.dima.watchdog.sms.commands;

import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import org.spongycastle.crypto.InvalidCipherTextException;

/**
 * Classe che gestisce la costruzione del principale sms di comando (m3).
 * 
 * @author emanuele
 *
 */
public class CommandSMS {
	private PrivateKey myPrivateKey; //chiave per la firma digitale
	private Key encryptionKey; //chiave dell'AES
	private byte[] text;
	private byte[] password; //già con sale
	private byte[] passwordHash; //hash(password)
	private byte[] finalMessage; // hash(password) || text
	private byte[] signature; //firma digitale
	private byte[] finalSignedAndEncryptedMessage; //messaggio firmato e crittografato
	private byte[] iv;
	
	
	public byte[] getSignature(){
		return this.signature;
	}
	
	public byte[] getFinalSignedAndEncryptedMessage(){
		return this.finalSignedAndEncryptedMessage;
	}
	
	/**
	 * Costruttore con testo e password alla fine del quale il messaggio sarà pronto per essere spedito.
	 */
	public CommandSMS(byte[] text, byte[] password, PrivateKey mPriv, Key key, byte[] iv){
		this.text = text;
		this.password = password;
		this.myPrivateKey = mPriv;
		this.encryptionKey = key;
		this.iv = iv;
	}

	/**
	 * Crea un hash della password con SHA-256 e crea la struttura hash(password) || text . Poi chiama i metodi
	 * che firmano e crittano il messaggio.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 * @throws NotECKeyException 
	 * @throws NoSignatureDoneException
	 * @throws IllegalStateException
	 * @throws InvalidCipherTextException
	 */
	public void construct() throws NoSuchAlgorithmException, UnsupportedEncodingException, NotECKeyException, NoSignatureDoneException, IllegalStateException, InvalidCipherTextException{
		if(this.finalMessage == null && this.text != null){
			MessageDigest digest = MessageDigest.getInstance(CryptoUtility.SHA_256);
			//l'hash sarà lungo 256 bit
			this.passwordHash = digest.digest(this.password);
			
			//in pratica si crea una struttura hash(password) || text
			//Sapendo che l'hash è lungo 256 bit, è comodo alla ricezione separare le due parti se l'hash...
			//...è in testa
			this.finalMessage = new byte[this.passwordHash.length + this.text.length];
			System.arraycopy(this.passwordHash, 0, this.finalMessage, 0, this.passwordHash.length);
			System.arraycopy(text, 0, this.finalMessage, this.passwordHash.length, this.text.length);
			
			this.signature = CryptoUtility.doSignature(this.finalMessage, this.myPrivateKey);
			encrypt();
		}
	}


	/**
	 * Critta la struttura  messaggio || ' ' || firma  con l'AES-256-GCM. Il carattere spazio servirà al parser
	 * per separare firma da messaggio, perchè la lunghezza della firma ECDSA è variabile.
	 * @throws InvalidCipherTextException 
	 * @throws IllegalStateException
	 */
	private void encrypt() throws IllegalStateException, InvalidCipherTextException{
		byte[] signaturePlusMessage = new byte[this.finalMessage.length + this.signature.length + 1];
		System.arraycopy(this.finalMessage, 0, signaturePlusMessage, 0, this.finalMessage.length);
		signaturePlusMessage[this.finalMessage.length] = ' ';
		System.arraycopy(this.signature, 0, signaturePlusMessage, this.finalMessage.length + 1, this.signature.length);	
		this.finalSignedAndEncryptedMessage = CryptoUtility.doEncryptionOrDecryption(signaturePlusMessage, this.encryptionKey, this.iv, CryptoUtility.ENC);
	}
}