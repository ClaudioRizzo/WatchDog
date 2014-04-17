package it.polimi.dima.watchdog.crypto;

import android.annotation.SuppressLint;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;




/**
 * Questa classe creerà un segreto condiviso tra due utenti, a partire dalle chiavi EC di entrambi, senza
 * che il segreto stesso sia trasmesso su alcun canale. Il segreto potrà poi essere utilizzato dalla classe
 * AESKeyGenerator per derivare le chiavi dell'AES. Il protocollo utilizzato è ECDH (in mancanza di implementazioni
 * in java già pronte di FHMQV e ECMQV). Le chiavi di partenza si suppongono già autenticate.
 * 
 * @author emanuele
 *
 */
public class SharedSecretAgreement {
	private PrivateKey myPrivateKey;
	private byte[] otherPublicKey;
	private byte[] sharedSecret;
	 
	
	
	public SharedSecretAgreement(PrivateKey mPr, byte[] otherPublicKey){
		this.myPrivateKey = mPr;
		this.otherPublicKey = otherPublicKey;
	}
	
	public void setTokenReceived(byte[] token){
		this.otherPublicKey = token;
	}
	
	public byte[] getSharedSecret(){
		return this.sharedSecret;
	}
	
	/**
	 * Metodo che genera lo shared secret con l'algoritmo ECDH.
	 * @return un segreto che sarà lo stesso computato dall'altro utente.
	 * @throws InvalidKeyException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 */
	@SuppressLint("TrulyRandom")
	public void generateSharedSecret() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException{
		KeyAgreement ka = KeyAgreement.getInstance("ECDH");
		ka.init(this.myPrivateKey);
		KeyFactory kf = KeyFactory.getInstance("EC");
		PublicKey otherPub = kf.generatePublic(new X509EncodedKeySpec(this.otherPublicKey));
		ka.doPhase(otherPub, true);
		this.sharedSecret = ka.generateSecret();
	}

}
