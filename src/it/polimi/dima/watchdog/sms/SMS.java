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
	private Key encryptionKey; //chiave dell'AES
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
	 * Costruttore con testo e password alla fine del quale il messaggio sarà pronto per essere spedito.
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
		this.text = sanitize(text);
		this.password = password;
		this.myPrivateKey = mPriv;
		this.encryptionKey = key;
		construct();
	}
	

	/**
	 * Sostituisce tutti gli spazi presenti nel messaggio con dei caratteri underscore. Questo perchè il
	 * carattere spazio sarà utilizzato come separatore tra messaggio e firma
	 * 
	 * @param text : il testo del messaggio
	 * @return text privato di tutti gli eventuali spazi in coda
	 */
	private String sanitize(String text) {
		if(text == null || text == " "){
			throw new IllegalArgumentException("Un messaggio nullo o formato solo da uno spazio non è accettabile!!!");
		}
		byte[] string = text.getBytes();
		for(int i=0; i<string.length; i++){
			if(string[i] == ' '){
				string[i] = '_';
			}
		}
		return new String(string);
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
	private void construct() throws NoSuchAlgorithmException, IOException, NoECDSAKeyPairGeneratedException, NoSignatureDoneException, DecoderException, InvalidKeyException, NoSuchPaddingException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException{
		if(this.finalMessage == null && this.text != null){
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			//l'hash sarà lungo 256 bit
			this.passwordHash = digest.digest(this.password.getBytes("UTF-8"));
			byte[] text = this.text.getBytes();
			
			//in pratica si crea una struttura hash(password) || text
			//Sapendo che l'hash è lungo 256 bit, è comodo alla ricezione separare le due parti se l'hash...
			//...è in testa
			this.finalMessage = new byte[this.passwordHash.length + text.length];
			System.arraycopy(this.passwordHash, 0, this.finalMessage, 0, this.passwordHash.length);
			System.arraycopy(text, 0, this.finalMessage, this.passwordHash.length, text.length);
			
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
	 * per separare firma da messaggio, perchè la lunghezza della firma ECDSA è veriabile.
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
	
	
	
}
