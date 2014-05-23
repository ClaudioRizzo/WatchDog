package it.polimi.dima.watchdog.crypto;

import android.annotation.SuppressLint;
import android.util.Log;
import it.polimi.dima.watchdog.UTILITIES.CryptoUtility;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;

import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.interfaces.ECPrivateKey;
import org.spongycastle.jce.interfaces.ECPublicKey;
import org.spongycastle.jce.spec.ECParameterSpec;

public class ECKeyPairGeneratorWrapper {
	private PublicKey pub;
	private PrivateKey priv;
	
	public PublicKey getPublicKey(){
		return this.pub;
	}
	
	public PrivateKey getPrivateKey(){
		return this.priv;
	}
	
	
	/**
	 * Genera una coppia di chiavi pubblica/privata tramite le curve ellittiche.
	 * @throws InvalidAlgorithmParameterException 
	 */
	//TODO leggere il warning per android <= 4.3 e agire di conseguenza
	@SuppressLint("TrulyRandom")
	public void generateKeyPair() throws InvalidAlgorithmParameterException {
		try{
			//TODO spqualcosa
			ECGenParameterSpec param = new ECGenParameterSpec(/*"prime192v1"*/"secp256r1");
	        SecureRandom random = SecureRandom.getInstance(CryptoUtility.SHA1_PRNG);

	        KeyPairGenerator keygen = KeyPairGenerator.getInstance(CryptoUtility.EC);
	        keygen.initialize(param, random);
	        
	        KeyPair pair = keygen.generateKeyPair();
	        if(pair == null){
	        	Log.i("[DEBUG]", "NULL key generation");
	        }
	        this.priv = pair.getPrivate();
	        this.pub = pair.getPublic();
	        if(this.priv == null){
	        	Log.i("[DEBUG]", "NULL privkey generation");
	        }
	        if(this.pub == null){
	        	Log.i("[DEBUG]", "NULL pubkey generation");
	        }
		}
		catch(NoSuchAlgorithmException e){}
		
	}

}
