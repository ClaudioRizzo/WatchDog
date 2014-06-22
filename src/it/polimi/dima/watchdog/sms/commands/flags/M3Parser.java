package it.polimi.dima.watchdog.sms.commands.flags;

import java.security.Key;
import java.security.PublicKey;

import org.spongycastle.crypto.InvalidCipherTextException;

import android.util.Base64;
import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.password.PasswordUtils;

/**
 * Classe che si occupa di spacchettare il contenuto di un sms, dividendolo in hash della password e testo.
 * Esegue anche la comparazione di tale hash con quello salvato per verificare che il telefono
 * del mittente (già autenticato) non sia utilizzato da malintenzionati.
 * 
 * @author emanuele
 *
 */
public class M3Parser {
	
	private byte[] smsEncrypted; //sms crittato
	private Key decryptionKey; //chiave dell'AES
	private byte[] iv;
	private PublicKey oPub; //chiave pubblica del mittente, usata per verificare la firma
	private byte[] sms; //sms decrittato (hash(password) || text)
	private byte[] signature; //firma scorporata dal messaggio
	private byte[] plaintext; //parte testuale del messaggio senza la password
	private byte[] passwordHash; //l'hash della password proveniente dal messaggio
	private byte[] storedPasswordHash; //l'hash della password presente nel telefono ricevente
	
	public byte[] getPlaintext(){
		return this.plaintext;
	}
	
	/**
	 * Costruttore che popola la classe con messaggio crittato, chiave di decrittazione, iv, chiave pubblica
	 * dell'altro e hash della propria password.
	 */
	public M3Parser(byte[] smsEncrypted, Key decryptionKey, PublicKey oPub, byte[] storedPasswordHash, byte[] iv){
		this.smsEncrypted = smsEncrypted;
		this.decryptionKey = decryptionKey;
		this.oPub = oPub;
		this.storedPasswordHash = storedPasswordHash;
		this.iv = iv;
	}
	
	/**
	 * Decritta il messaggio eseguendo tutti i round dell'AES al contrario.
	 * 
	 * @throws ErrorInSignatureCheckingException 
	 * @throws ArbitraryMessageReceivedException 
	 * @throws InvalidCipherTextException 
	 * @throws IllegalStateException 
	 * @throws NotECKeyException 
	 */
	public void decrypt() throws IllegalStateException, InvalidCipherTextException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NotECKeyException {
		byte[] decryptedSMS = CryptoUtility.doEncryptionOrDecryption(this.smsEncrypted, this.decryptionKey, this.iv, CryptoUtility.DEC); // messaggio || ' ' || firma
		int spacePosition = getSpacePosition(decryptedSMS);
		//copia la firma
		this.signature = new byte[decryptedSMS.length - spacePosition - 1];
		System.arraycopy(decryptedSMS, spacePosition + 1, this.signature, 0, decryptedSMS.length - spacePosition - 1);
		//copia il messaggio
		this.sms = new byte[spacePosition];
		System.arraycopy(decryptedSMS, 0, this.sms, 0, spacePosition);
		validateSignature();
		parse();
	}

	/**
	 * Ritorna la posizione dello spazio separatore.
	 * 
	 * @param string : la struttura messaggio || ' ' || firma
	 * @return un intero che indica la posizione del separatore
	 * @throws ArbitraryMessageReceivedException 
	 */
	private int getSpacePosition(byte[] string) throws ArbitraryMessageReceivedException {
		for(int i = PasswordUtils.HASH_LENGTH; i < string.length; i++){
			if(string[i] == ' '){
				return i;
			}
		}
		throw new ArbitraryMessageReceivedException(ErrorFactory.INTEGRITY_EXCEPTION);
	}

	/**
	 * Tenta di validare la firma digitale: se non ci riesce lancia un'eccezione
	 * 
	 * @throws ArbitraryMessageReceivedException
	 * @throws ErrorInSignatureCheckingException
	 * @throws NotECKeyException 
	 */
	private void validateSignature() throws ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NotECKeyException {
		if(!CryptoUtility.verifySignature(this.sms, this.signature, this.oPub)){
			throw new ArbitraryMessageReceivedException(ErrorFactory.SIGNATURE_EXCEPTION);
		}
	}
	
	/**
	 * Scompone il messaggio separando testo e password. Chiama poi il metodo per verificare la password.
	 * 
	 * @throws ArbitraryMessageReceivedException
	 */
	private void parse() throws ArbitraryMessageReceivedException{
		decompose();
		if(!validate()){
			throw new ArbitraryMessageReceivedException(ErrorFactory.WRONG_PASSWORD);
		}
	}

	/**
	 * Scompone l'sms ricevuto (già decrittato e scoprorato dalla firma) in hash della password e testo in chiaro.
	 */
	private void decompose() {
		this.passwordHash = new byte[PasswordUtils.HASH_LENGTH];
		System.arraycopy(this.sms, 0, this.passwordHash, 0, PasswordUtils.HASH_LENGTH);
		byte[] plaintext = new byte[this.sms.length - PasswordUtils.HASH_LENGTH];
		System.arraycopy(this.sms, PasswordUtils.HASH_LENGTH, plaintext, 0, this.sms.length - PasswordUtils.HASH_LENGTH);
		this.plaintext = plaintext;
	}

	/**
	 * Verifica che l'hash ricevuto della password coincida con quello salvato sul telefono ricevente.
	 * @return true in caso affermativo, false altrimenti
	 */
	private boolean validate() {
		String received = Base64.encodeToString(this.passwordHash, Base64.DEFAULT);
		String stored = Base64.encodeToString(this.storedPasswordHash, Base64.DEFAULT);
		return received.equals(stored);
	}
}