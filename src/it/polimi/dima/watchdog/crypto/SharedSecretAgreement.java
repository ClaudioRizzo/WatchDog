package it.polimi.dima.watchdog.crypto;

import android.annotation.SuppressLint;
import android.util.Base64;
import it.polimi.dima.watchdog.UTILITIES.CryptoUtility;

import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.KeyAgreement;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;
import org.spongycastle.jce.spec.ECPrivateKeySpec;
import org.spongycastle.jce.spec.ECPublicKeySpec;




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
	private PublicKey otherPublicKey;
	private byte[] sharedSecret;
	 
	public SharedSecretAgreement(){}
	
	public void setMyPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException{
		setMyPrivateKey(Base64.decode(key, Base64.DEFAULT));
	}
	
	private void setMyPrivateKey(byte[] key) throws NoSuchAlgorithmException, InvalidKeySpecException{
		KeyFactory kf = KeyFactory.getInstance(CryptoUtility.EC);
		ECParameterSpec param = ECNamedCurveTable.getParameterSpec("secp256r1");
		ECPrivateKeySpec ecparam = new ECPrivateKeySpec(new BigInteger(key), param);
		this.myPrivateKey = kf.generatePrivate(ecparam);
	}
	
	public void setTokenReceived(String token) throws InvalidKeySpecException, NoSuchAlgorithmException{
		setTokenReceived(Base64.decode(token, Base64.DEFAULT));
	}
	
	private void setTokenReceived(byte[] token) throws InvalidKeySpecException, NoSuchAlgorithmException {
		KeyFactory kf = KeyFactory.getInstance(CryptoUtility.EC);
		ECParameterSpec param = ECNamedCurveTable.getParameterSpec("secp256r1");
		ECPublicKeySpec ecparam = new ECPublicKeySpec(param.getG(), param);
		this.otherPublicKey = kf.generatePublic(ecparam);
		
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
		KeyAgreement ka = KeyAgreement.getInstance(CryptoUtility.ECDH);
		ka.init(this.myPrivateKey);
		ka.doPhase(this.otherPublicKey, true);
		this.sharedSecret = ka.generateSecret();
	}

}
