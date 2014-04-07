package it.polimi.dima.watchdog.sms;

import org.apache.commons.codec.binary.Base64;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;

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
	public byte[] sms;
	public String plaintext;
	public byte[] passwordHash; //l'hash della password proveniente dal messaggio
	public byte[] storedPasswordHash; //l'hash della password presente nel telefono ricevente
	
	public SMSParser(byte[] sms, byte[] storedPasswordHash) throws ArbitraryMessageReceivedException{
		this.sms = sms;
		this.storedPasswordHash = storedPasswordHash;
		decompose();
		if(!validate()){
			throw new ArbitraryMessageReceivedException("La password non corrisponde!!!");
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
