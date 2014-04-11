package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.crypto.SharedSecretAgreement;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * In questa classe verranno richiamate tutte le azioni da compiere per ottenere le chiavi necessarie a crittare
 * e a firmare un messaggio. In particolare le azioni che coinvolgono scambi di dati tra due telefoni (ECDH
 * fondamentalmente) avverranno su un canale diverso da quello su cui vengono scambiati i messaggi di controllo
 * remoto.
 * 
 * @author emanuele
 *
 */
public class SMSKeyManagement {
	
	private SharedSecretAgreement ssa;
	
	public SMSKeyManagement(PrivateKey mPriv){
		this.ssa = new SharedSecretAgreement(mPriv, null);
	}
	
	public String getStoredPasswordHash(){
		//TODO accedere a file e prelefare l'hash
		return null;
	}
	
	public void initiateECDHExchange(PublicKey oPub){
		//TODO mandare un messaggio con il proprio token all'altro.
	}
	
	public void manageReceivedToken(Key token) throws InvalidKeyException, NoSuchAlgorithmException{
		//TODO gestire il messaggio ricevuto dall'altro con il token dell'altro.
		this.ssa.setTokenReceived(token);
		byte[] secret = ssa.generateSharedSecret();
		//solo a questo punto ciascuna delle due parti lancia il key generator sul segreto condiviso appena
		//generato. E poi TODO lo salva nel file accanto al numero di telefono. Ogni volta che viene ricevuto
		//un token si genera una nuova chiave che rimpiazza quella vecchia.
	}

}
