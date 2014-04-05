package it.polimi.dima.watchdog.crypto;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;

public class ECKeyPairGenerator {
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
	 */
	//TODO leggere il warning per android <= 4.3 e agire di conseguenza
	public void generateKeyPair() {
		try{
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");
	        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

	        keyGen.initialize(256, random);

	        KeyPair pair = keyGen.generateKeyPair();
	        this.priv = pair.getPrivate();
	        this.pub = pair.getPublic();
		}
		catch(NoSuchAlgorithmException e){}
		
	}

}
