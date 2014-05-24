package it.polimi.dima.watchdog.crypto;

import android.annotation.SuppressLint;
import android.util.Base64;
import it.polimi.dima.watchdog.utilities.CryptoUtility;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.KeyAgreement;



/**
 * Questa classe crea un segreto condiviso tra due utenti, a partire dalle chiavi EC di entrambi, senza che il 
 * segreto stesso sia trasmesso su alcun canale. Il segreto potrà poi essere utilizzato in AESKeyGenerator
 * insieme ad un sale random per derivare le chiavi dell'AES. Il protocollo utilizzato è ECDH (in mancanza di
 * implementazioni in java già pronte di FHMQV e ECMQV). Le chiavi di partenza si suppongono già autenticate.
 * 
 * @author emanuele
 * 
 */
public class SharedSecretAgreement {
	
	private PrivateKey myPrivateKey;
	private PublicKey otherPublicKey;
	private byte[] sharedSecret;
	 
	public SharedSecretAgreement(){
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}
	
	/**
	 * Converte la chiave privata da Base64 a byte[], la rigenera e la salva nell'attributo corrispondente.
	 * 
	 * @param myPriv : la chiave privata
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchProviderException
	 */
	public void setMyPrivateKey(String myPriv) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException{
		setMyPrivateKey(Base64.decode(myPriv, Base64.DEFAULT));
	}
	
	/**
	 * Rigenera la chiave privata a partire dai suoi byte e la salva nell'attributo corrispondente.
	 * 
	 * @param myPriv : i byte della chiave privata
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchProviderException
	 */
	private void setMyPrivateKey(byte[] myPriv) throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException{
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		this.myPrivateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(myPriv));
	}
	
	/**
	 * Converte la chiave pubblica da Base64 a byte[], la rigenera e la salva nell'attributo corrispondente.
	 * 
	 * @param otherPub : la chiave pubblica
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	public void setReceivedOtherPublicKey(String otherPub) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException{
		setReceivedOtherPublicKey(Base64.decode(otherPub, Base64.DEFAULT));
	}
	
	/**
	 * Rigenera la chiave pubblica a partire dai suoi byte e la salva nell'attributo corrispondente.
	 * 
	 * @param otherPub : la chiave pubblica
	 * @throws InvalidKeySpecException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	private void setReceivedOtherPublicKey(byte[] otherPub) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException {
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		this.otherPublicKey = keyFactory.generatePublic(new X509EncodedKeySpec(otherPub));
	}

	/**
	 * Ritorna il segreto condiviso che è stato computato da ECDH o null se il segreto non è stato ancora
	 * computato.
	 * 
	 * @return il segreto condiviso (byte[]) che è stato computato da ECDH o null se il segreto non è stato
	 * ancora computato.
	 */
	public byte[] getSharedSecret(){
		return this.sharedSecret;
	}
	
	/**
	 * Genera lo shared secret con l'algoritmo ECDH.
	 * 
	 * @throws InvalidKeyException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeySpecException 
	 * @throws NoSuchProviderException 
	 */
	@SuppressLint("TrulyRandom")
	public void generateSharedSecret() throws InvalidKeyException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException{
		KeyAgreement keyAgreement = KeyAgreement.getInstance(CryptoUtility.ECDH, CryptoUtility.SC);
		keyAgreement.init(this.myPrivateKey);
		keyAgreement.doPhase(this.otherPublicKey, true);
		this.sharedSecret = keyAgreement.generateSecret();
	}

}
