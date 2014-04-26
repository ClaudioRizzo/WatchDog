package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.crypto.AES_256_GCM_Crypto;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.NoECDSAKeyPairGeneratedException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;

import android.telephony.SmsManager;

/**
 * Classe che gestisce la costruzione degli SMS. IMPORTANTE: per gli sms di configurazione (scambio chiavi, ecc.)
 * NON si usa questa classe: questa classe è pensata solo per costruire i messaggi che costituiscono i comandi,
 * accompagnati da password. Altra cosa importante: prima di ogni messaggio dovrà essere effettuato lo scambio
 * di chiavi ECDH per concordare il valore della chiave con cui il messaggio sarà crittato.
 * 
 * @author emanuele
 *
 */
public class CommandSMS {
	private PrivateKey myPrivateKey; //chiave per la firma digitale
	private Key encryptionKey; //chiave dell'AES
	private byte[] text;
	private String password;
	private byte[] passwordHash; //hash(password)
	private byte[] finalMessage; // hash(password) || text
	private byte[] signature; //firma digitale
	private byte[] finalSignedAndEncryptedMessage; //messaggio firmato e crittografato
	private final short SMS_PORT = 7777;
	private String dest; //serve per memorizzare il destinatario
	
	
	
	public byte[] getSignature(){
		return this.signature;
	}
	
	public byte[] getFinalSignedAndEncryptedMessage(){
		return this.finalSignedAndEncryptedMessage;
	}
	
	/**
	 * Costruttore con testo e password alla fine del quale il messaggio sarà pronto per essere spedito.
	 */
	public CommandSMS(byte[] text, String password, PrivateKey mPriv, Key key, String dest){
		this.text = text;
		this.password = password;
		this.myPrivateKey = mPriv;
		this.encryptionKey = key;
		this.dest = dest;
	}

	/**
	 * Crea un hash della password con SHA-256 e crea la struttura hash(password) || text . Poi chiama i metodi
	 * che firmano e crittano il messaggio.
	 * 
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 * @throws NoECDSAKeyPairGeneratedException 
	 * @throws NoSignatureDoneException 
	 * @throws DecoderException 
	 * @throws BadPaddingException 
	 * @throws IllegalBlockSizeException 
	 * @throws NoSuchProviderException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws NoSuchPaddingException 
	 * @throws InvalidKeyException 
	 */
	public void construct() throws NoSuchAlgorithmException, IOException, NoECDSAKeyPairGeneratedException, NoSignatureDoneException, DecoderException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException{
		if(this.finalMessage == null && this.text != null){
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			//l'hash sarà lungo 256 bit
			this.passwordHash = digest.digest(this.password.getBytes("UTF-8"));
			
			//in pratica si crea una struttura hash(password) || text
			//Sapendo che l'hash è lungo 256 bit, è comodo alla ricezione separare le due parti se l'hash...
			//...è in testa
			this.finalMessage = new byte[this.passwordHash.length + this.text.length];
			System.arraycopy(this.passwordHash, 0, this.finalMessage, 0, this.passwordHash.length);
			System.arraycopy(text, 0, this.finalMessage, this.passwordHash.length, this.text.length);
			
			sign();
			encrypt();
		}
	}

	/**
	 * Genera la firma digitale del messaggio con SHA-256 ed ECDSA.
	 * @throws NoECDSAKeyPairGeneratedException
	 * @throws NoSignatureDoneException
	 */
	private void sign() throws NoECDSAKeyPairGeneratedException, NoSignatureDoneException {
		ECDSA_Signature sig = new ECDSA_Signature(this.finalMessage, this.myPrivateKey);
		sig.sign();
		this.signature = sig.getSignature();
	}


	/**
	 * Critta la struttura  messaggio || ' ' || firma  con l'AES-256-GCM. Il carattere spazio servirà al parser
	 * per separare firma da messaggio, perchè la lunghezza della firma ECDSA è variabile.
	 * 
	 * @throws DecoderException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws NoSuchProviderException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	private void encrypt() throws DecoderException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException {
		byte[] signaturePlusMessage = new byte[this.finalMessage.length + this.signature.length + 1];
		System.arraycopy(this.finalMessage, 0, signaturePlusMessage, 0, this.finalMessage.length);
		signaturePlusMessage[this.finalMessage.length] = ' ';
		System.arraycopy(this.signature, 0, signaturePlusMessage, this.finalMessage.length + 1, this.signature.length);
		AES_256_GCM_Crypto enc = new AES_256_GCM_Crypto(signaturePlusMessage, this.encryptionKey);
		enc.encrypt();
		this.finalSignedAndEncryptedMessage = enc.getCiphertext();
	}
	
	/**
	 * Tutto da testare ancora (per esempio la porta va cambiata) TODO
	 */
	public void send(){
		SmsManager smsManager = SmsManager.getDefault();
		smsManager.sendDataMessage(this.dest, null, this.SMS_PORT, this.finalSignedAndEncryptedMessage, null, null);
	}
	
	
	
}
