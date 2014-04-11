package it.polimi.dima.watchdog.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.KeyAgreement;


/**
 * Questa classe creerà un segreto condiviso tra due utenti, a partire dalle chiavi rsa di entrambi, senza
 * che il segreto stesso sia trasmesso su alcun canale. Il segreto potrà poi essere utilizzato dalla classe
 * KeyGenerator per derivare le chiavi dell'AES. Il protocollo utilizzato è ECDH (in mancanza di implementazioni
 * in java già pronte di FHMQV e ECMQV). Le chiavi di partenza si suppongono già autenticate.
 * 
 * @author emanuele
 *
 */
public class SharedSecretAgreement {
	private PrivateKey myPrivateKey;
	private Key tokenReceivedFromOther; //da rivedere
	private byte[] sharedSecret;
	 
	
	
	public SharedSecretAgreement(PrivateKey mPr, Key tokenReceivedFromOther){
		this.myPrivateKey = mPr;
		this.tokenReceivedFromOther = tokenReceivedFromOther;
	}
	
	public void setTokenReceived(Key token){
		this.tokenReceivedFromOther = token;
	}
	
	/**
	 * Metodo che genera lo shared secret con l'algoritmo ECDH.
	 * @return un segreto che sarà lo stesso computato dall'altro utente.
	 * @throws InvalidKeyException 
	 * @throws NoSuchAlgorithmException 
	 */
	public byte[] generateSharedSecret() throws InvalidKeyException, NoSuchAlgorithmException{
		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(this.myPrivateKey);
		ka.doPhase(this.tokenReceivedFromOther, true); //da rivedere
		this.sharedSecret = ka.generateSecret();
		
		return this.sharedSecret;
	}

}
