package it.polimi.dima.watchdog.crypto;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.SignatureException;

import it.polimi.dima.watchdog.exceptions.*;

public class ECDSA_Signature {
	
	private String ptx;
	private Signature signature;
	private String string_signature;
	private PublicKey pub;
	private PrivateKey priv;
	
	public String getStringSignature(){
		return this.string_signature;
	}
	
	public Signature getSignature(){
		return this.signature;
	}
	
	
	/**
	 * Costruttore che prevede il passaggio anche di una coppia di chiavi; lancia un'eccezione se almeno una
	 * delle due chiavi non è una chiave ECDSA.
	 * @param ptx : il messaggio da firmare
	 * @param pub : la chiave pubblica
	 * @param priv : la chiave privata
	 * @throws NoECDSAKeyPairGeneratedException
	 */
	public ECDSA_Signature(String ptx, PublicKey pub, PrivateKey priv) throws NoECDSAKeyPairGeneratedException{
		this.ptx = ptx;
		this.pub = pub;
		this.priv = priv;
		
		if(!this.pub.getAlgorithm().toString().equals("EC")){
			throw new NoECDSAKeyPairGeneratedException();
		}
	}
	
	/**
	 * Costruttore che genera le chiavi sul momento. Lancia un'eccezione se almeno una delle chiavi non viene
	 * generata.
	 * @param ptx : il messaggio da firmare
	 * @throws NoECDSAKeyPairGeneratedException
	 */
	public ECDSA_Signature(String ptx) throws NoECDSAKeyPairGeneratedException{
		this.ptx = ptx;
		generateKeyPair();
		
		if(this.pub == null || this.priv == null){
			throw new NoECDSAKeyPairGeneratedException();
		}
	}
	
	//TODO leggere il warning per android <= 4.3 e agire di conseguenza
	private void generateKeyPair() {
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
	
	/**
	 * Effettua la firma digitale aggiornando signature e string_signature, oppure, se qualunque cosa
	 * va storta, lancia un'eccezione.
	 * @throws NoSignatureDoneException
	 */
	public void sign() throws NoSignatureDoneException{
		try {
			this.signature = Signature.getInstance("SHA1withECDSA");
			this.signature.initSign(this.priv);

	        byte[] strByte = this.ptx.getBytes("UTF-8");
	        this.signature.update(strByte);

	        byte[] realSig = this.signature.sign();
	        //this.string_signature = new BigInteger(1, realSig).toString(16);
	        this.string_signature = new BigInteger(1, realSig).toString(16);
		}
		catch (NoSuchAlgorithmException e) {
			this.signature = null;
		} 
		catch (SignatureException e) {
			this.signature = null;
		}
		catch (InvalidKeyException e) {
			this.signature = null;
		}
		catch (UnsupportedEncodingException e) {
			this.signature = null;
		}
		
		if(this.signature == null){
			throw new NoSignatureDoneException();
		}
	}

	
}
