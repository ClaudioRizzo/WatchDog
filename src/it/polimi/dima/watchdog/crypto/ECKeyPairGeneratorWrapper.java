package it.polimi.dima.watchdog.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.spec.ECParameterSpec;

import android.annotation.SuppressLint;
import android.util.Log;
import it.polimi.dima.watchdog.UTILITIES.CryptoUtility;


public class ECKeyPairGeneratorWrapper {
		
	private PublicKey pub;
	private PrivateKey priv;
	
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
	 * Genera una coppia di chiavi pubblica/privata tramite le curve ellittiche.
	 * @throws InvalidAlgorithmParameterException 
	 */
	//TODO leggere il warning per android <= 4.3 e agire di conseguenza
	@SuppressLint("TrulyRandom")
	public void generateKeyPair() {
		try{
			Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
			ECParameterSpec ellipticCurvesParameterSpecifiers = ECNamedCurveTable.getParameterSpec("secp256r1");
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CryptoUtility.EC, CryptoUtility.SC);
			SecureRandom secureRandom = SecureRandom.getInstance(CryptoUtility.SHA1_PRNG);
			
			keyPairGenerator.initialize(ellipticCurvesParameterSpecifiers, secureRandom);
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
