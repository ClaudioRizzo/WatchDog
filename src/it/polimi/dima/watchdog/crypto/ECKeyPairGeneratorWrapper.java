package it.polimi.dima.watchdog.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.interfaces.ECPrivateKey;
import org.spongycastle.jce.interfaces.ECPublicKey;
import org.spongycastle.jce.spec.ECParameterSpec;

import android.annotation.SuppressLint;
import android.util.Log;
import it.polimi.dima.watchdog.UTILITIES.CryptoUtility;


public class ECKeyPairGeneratorWrapper {
		
	private ECPublicKey pub;
	private ECPrivateKey priv;
	
	public ECPublicKey getPublicKey() {
		return pub;
	}

	public void setPublicKey(ECPublicKey pub) {
		this.pub = pub;
	}

	public ECPrivateKey getPrivateKey() {
		return priv;
	}

	public void setPrivateKey(ECPrivateKey priv) {
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
			ECParameterSpec ecps = ECNamedCurveTable.getParameterSpec("prime192v1");
			//ECDomainParameters ecdp = new ECDomainParameters(ecps.getCurve(), ecps.getG(), ecps.getN());
			KeyPairGenerator eckpg = KeyPairGenerator.getInstance(CryptoUtility.EC, CryptoUtility.SC);
			SecureRandom random = SecureRandom.getInstance(CryptoUtility.SHA1_PRNG);
			//ECKeyGenerationParameters eckgp = new ECKeyGenerationParameters(ecdp, random);
			
			eckpg.initialize(ecps, random);
			KeyPair pair = eckpg.generateKeyPair();
			
			if(pair == null){
	        	Log.i("[DEBUG]", "NULL key generation");
	        }
			
			this.pub = ((ECPublicKey) pair.getPublic());
			this.priv = ((ECPrivateKey) pair.getPrivate());
			
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
