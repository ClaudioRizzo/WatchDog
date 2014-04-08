package it.polimi.dima.watchdog.sms;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;

import it.polimi.dima.watchdog.crypto.AES_256_GCM_Crypto;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;

/**
 * Classe che si occupa di spacchettare il contenuto di un sms, dividendolo in hash della password e testo.
 * Esegue anche la comparazione di tale hash con quello salvato (dove? TODO) per verificare che il telefono
 * del mittente (già autenticato) non sia utilizzato da malintenzionati. TODO: implementare la decodifica e il
 * controllo della firma digitale (in questa classe).
 * 
 * @author emanuele
 *
 */
public class SMSParser {
	public byte[] smsEncrypted; //sms crittato
	public Key decryptionKey; //chiave dell'AES
	public PublicKey oPub; //chiave pubblica del mittente, usata per verificare la firma
	public byte[] sms; //sms decrittato
	public byte[] signature; //firma scorporata dal messaggio
	public String plaintext; //parte testuale del messaggio senza la password
	public byte[] passwordHash; //l'hash della password proveniente dal messaggio
	public byte[] storedPasswordHash; //l'hash della password presente nel telefono ricevente
	
	/**
	 * Costurttore che alla fine popola la classe con il messaggio originale dopo averne controllato l'integrità
	 * mediante firma e la correttezza della password.
	 * 
	 * @param smsEncrypted
	 * @param decryptionKey
	 * @param oPub
	 * @param storedPasswordHash
	 * @throws DecoderException
	 * @throws InvalidKeyException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws ArbitraryMessageReceivedException
	 * @throws ErrorInSignatureCheckingException
	 */
	public SMSParser(byte[] smsEncrypted, Key decryptionKey, PublicKey oPub, byte[] storedPasswordHash) throws DecoderException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException{
		this.smsEncrypted = smsEncrypted;
		this.decryptionKey = decryptionKey;
		this.oPub = oPub;
		this.storedPasswordHash = storedPasswordHash;
		decrypt();
	}
	
	private void decrypt() throws DecoderException, InvalidKeyException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException {
		AES_256_GCM_Crypto dec = new AES_256_GCM_Crypto(this.smsEncrypted, this.decryptionKey);
		dec.decrypt();
		byte[] decryptedSMS = dec.getPlaintext(); // firma + messaggio
		//TODO TODO TODO trovare un modo di parsing alternativo
		/* poi:
		 * copiare la parte di decryptedSMS che corrisponde alla firma in this.signature
		 * copiare l'altra parte in this.sms
		 */
		validateSignature();
		parse();
		
	}

	private void validateSignature() throws ArbitraryMessageReceivedException, ErrorInSignatureCheckingException{
		ECDSA_Signature ver = new ECDSA_Signature(this.sms, this.oPub, this.signature);
		if(!ver.verifySignature()){
			throw new ArbitraryMessageReceivedException("Firma non valida/non corrispondente!!!");
		}
	}
	
	private void parse() throws ArbitraryMessageReceivedException{
		decompose();
		if(!validate()){
			throw new ArbitraryMessageReceivedException("La password non è corretta!!!");
		}
	}

	/**
	 * Scompone l'sms ricevuto in hash della password e testo in chiaro.
	 */
	private void decompose() {
		System.arraycopy(this.sms, 0, this.passwordHash, 0, 32);
		byte[] plaintext = new byte[this.sms.length-32];
		System.arraycopy(this.sms, 32, this.passwordHash, 0, this.sms.length-32);
		this.plaintext = new String(plaintext);
	}

	/**
	 * Verifica che l'hash ricevuto della password coincida con quello salvato sul telefono ricevente.
	 * @return true in caso affermativo, false altrimenti
	 */
	private boolean validate() {
		String received = Base64.encodeBase64String(this.passwordHash);
		String stored = Base64.encodeBase64String(this.storedPasswordHash);
		return received.equals(stored);
	}
	

}