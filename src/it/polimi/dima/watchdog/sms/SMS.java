package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.crypto.AES_256_GCM_Crypto;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.NoECDSAKeyPairGeneratedException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;

import java.io.ByteArrayOutputStream;
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

/**
 * Classe che gestisce la costruzione degli SMS. IMPORTANTE: per gli sms di configurazione (scambio chiavi, ecc.)
 * NON si usa questa classe: questa classe è pensata solo per costruire i messaggi che costituiscono i comandi,
 * accompagnati da password. Altra cosa importante: prima di ogni messaggio dovrà essere effettuato lo scambio
 * di chiavi ECDH per concordare il valore della chiave con cui il messaggio sarà crittato.
 * 
 * @author emanuele
 *
 */
public class SMS {
	private PrivateKey myPrivateKey; //chiave per la firma digitale
	private Key key; //chiave dell'AES
	private String text;
	private String password;
	private byte[] passwordHash; //hash(password)
	private byte[] finalMessage; // hash(password) || text
	private byte[] signature; //firma digitale
	private byte[] finalSignedAndEncryptedMessage; //messaggio firmato e crittografato
	
	
	
	public byte[] getSignature(){
		return this.signature;
	}
	
	public byte[] getFinalSignedAndEncryptedMessage(){
		return this.finalSignedAndEncryptedMessage;
	}
	/**
	 * Costruttore con testo e password che richiama il metodo di incapsulamento di text e password nel messaggio
	 * finale.
	 * 
	 * @param text : il testo in chiaro del messaggio
	 * @param password : la password che verrà "allegata" al messaggio
	 * @param mPriv : la chiave privata del mittente che verrà usata per firmare digitalmente il messaggio
	 * @param key : la chiave che varrà usata per crittare il messaggio
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
	public SMS(String text, String password, PrivateKey mPriv, Key key) throws NoSuchAlgorithmException, IOException, NoECDSAKeyPairGeneratedException, NoSignatureDoneException, DecoderException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException{
		this.text = text;
		this.password = password;
		this.myPrivateKey = mPriv;
		this.key = key;
		construct();
	}
	

	
	/**
	 * Crea un hash della password con sha-256 e crea la struttura hash(password) || text . Poi chiama i metodi
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
	private void construct() throws NoSuchAlgorithmException, IOException, NoECDSAKeyPairGeneratedException, NoSignatureDoneException, DecoderException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException{
		if(this.finalMessage == null && this.text != null){
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			//l'hash sarà lungo 256 bit
			this.passwordHash = digest.digest(this.password.getBytes("UTF-8"));
			
			//in pratica si crea una struttura hash(password) || text
			//Sapendo che l'hash è lungo 256 bit, è comodo alla ricezione separare le due parti se l'hash...
			//...è in testa
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			outputStream.write(this.passwordHash);
			outputStream.write(this.text.getBytes());
			this.finalMessage = outputStream.toByteArray();
			sign();
			encrypt();
		}
	}

	private void sign() throws NoECDSAKeyPairGeneratedException, NoSignatureDoneException {
		ECDSA_Signature sig = new ECDSA_Signature(this.finalMessage, this.myPrivateKey);
		sig.sign();
		this.signature = sig.getSignature();
	}



	private void encrypt() throws DecoderException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException {
		byte[] signaturePlusMessage = new byte[this.finalMessage.length + this.signature.length];
		System.arraycopy(this.signature, 0, signaturePlusMessage, 0, this.signature.length);
		System.arraycopy(this.finalMessage, 0, signaturePlusMessage, this.signature.length, this.finalMessage.length);
		AES_256_GCM_Crypto enc = new AES_256_GCM_Crypto(signaturePlusMessage, this.key);
		enc.encrypt();
		this.finalSignedAndEncryptedMessage = enc.getCiphertext();
	}
	
	
	
}
