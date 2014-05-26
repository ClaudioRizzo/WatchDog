package it.polimi.dima.watchdog.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Random;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;

import android.annotation.SuppressLint;
import android.util.Log;
import it.polimi.dima.watchdog.utilities.CryptoUtility;

/**
 * Classe che genera una coppia di chiavi pubblica/privata a partire dalla curva ellittica secp256r1 e da un
 * numero casuale generato con SHA1PRNG.
 * 
 * @author emanuele
 *
 */
public class ECKeyPairGeneratorWrapper {
		
	private PublicKey pub;
	private PrivateKey priv;
	private SecureRandom secureRandom;
	
	//TODO si può fare qualcosa?
	@SuppressLint("TrulyRandom")
	public ECKeyPairGeneratorWrapper() throws NoSuchAlgorithmException, NoSuchProviderException{
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
			
			if(pair == null){
	        	Log.i("[DEBUG]", "NULL key generation");
	        }
			
			this.pub = pair.getPublic();
			this.priv = pair.getPrivate();
			
			if(this.priv == null){
	        	Log.i("[DEBUG]", "NULL privkey generation");
	        }
	        if(this.pub == null){
	        	Log.i("[DEBUG]", "NULL pubkey generation");
	        }
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
	}
}
