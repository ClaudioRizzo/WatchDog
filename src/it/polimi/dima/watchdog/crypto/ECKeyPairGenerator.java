package it.polimi.dima.watchdog.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;
import android.annotation.SuppressLint;

/**
 * Classe che genera una coppia di chiavi pubblica/privata a partire dalla curva ellittica secp256r1 e da un
 * numero casuale generato con SHA1PRNG.
 * 
 * @author emanuele
 *
 */
public class ECKeyPairGenerator {
		
	private PublicKey pub;
	private PrivateKey priv;
	private SecureRandom secureRandom;
	
	//TODO si può fare qualcosa?
	@SuppressLint("TrulyRandom")
	public ECKeyPairGenerator() throws NoSuchAlgorithmException, NoSuchProviderException{
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
		this.secureRandom = SecureRandom.getInstance(CryptoUtility.SHA1_PRNG);
	}
	
	public PublicKey getPublicKey() {
		return this.pub;
	}

	public void setPublicKey(PublicKey pub) {
		this.pub = pub;
	}

	public PrivateKey getPrivateKey() {
		return this.priv;
	}

	public void setPrivateKey(PrivateKey priv) {
		this.priv = priv;
	}
	
	/**
	 * Genera una coppia di chiavi pubblica/privata a partire dalla curva ellittica secp256r1 e da un numero
	 * casuale generato con SHA1PRNG.
	 * 
	 * @throws InvalidAlgorithmParameterException 
	 */
	public void generateKeyPair() {
		try{
			//521 bit di lunghezza per le chiavi
			ECParameterSpec ellipticCurvesParameterSpecifiers = ECNamedCurveTable.getParameterSpec("secp256r1");
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CryptoUtility.EC, CryptoUtility.SC);
			keyPairGenerator.initialize(ellipticCurvesParameterSpecifiers, this.secureRandom);
			KeyPair pair = keyPairGenerator.generateKeyPair();
			
			this.pub = pair.getPublic();
			this.priv = pair.getPrivate();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
}