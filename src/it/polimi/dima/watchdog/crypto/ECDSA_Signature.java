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

/**
 * Classe che serve per generare la firma digitale "signature" (e la sua verione in stringa "string_signature")
 * di un messaggio "ptx" a sua volta stringa. La coppia di chiavi viene autogenerata se non è passata dall'utente,
 * e vengono lanciate eccezioni nei seguenti casi: le chiavi non sono valide, almeno una chiave è nulla, la firma
 * non è andata a buon fine.
 * 
 * Un altro uso è quello esclusivo di creazione di una coppia di chiavi pubblica e privata da utilizzare
 * esclusivamente per firme digitali. Questo avviene mediante l'istanziazione di un oggetto della classe
 * tramite costruttore vuoto.
 * 
 * @author emanuele
 *
 */
public class ECDSA_Signature {
	
	private String ptx;
	private Signature signature;
	private String string_signature;
	private PublicKey pub;
	private PrivateKey priv;
	private byte[] signatureToVerify;
	
	public String getStringSignature(){
		return this.string_signature;
	}
	
	public Signature getSignature(){
		return this.signature;
	}
	
	public PrivateKey getPrivateKey(){
		return this.priv;
	}
	
	public PublicKey getPublicKey(){
		return this.pub;
	}
	
	
	/**
	 * Costruttore che serve esclusivamente a generare una coppia di chiavi.
	 */
	public ECDSA_Signature(){
		generateKeyPair();		
	}
	
	
	/**
	 * Costruttore in sign-mode che prevede il passaggio anche di una coppia di chiavi; lancia un'eccezione se
	 * almeno una delle due chiavi non è una chiave ECDSA.
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
	 * Costruttore in sign-mode che genera le chiavi sul momento. Lancia un'eccezione se almeno una delle chiavi
	 * non viene generata.
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
	
	/**
	 * Costruttore in decrypt-mode che riceve il messaggio, la chiave pubblica con cui verificare la firma
	 * (quella dell'utente che ha firmato il messaggio) e la firma da verificare sotto forma di array di byte.
	 * @param ptx : il messaggio
	 * @param pub : la chiave pubblica del mittente
	 * @param signature : la firma da verificare
	 */
	public ECDSA_Signature(String ptx, PublicKey pub, byte[] signature){
		this.ptx = ptx;
		this.pub = pub;
		this.signatureToVerify = signature;
	}
	
	
	/**
	 * Costruttore in decrypt-mode che riceve il messaggio, la chiave pubblica con cui verificare la firma
	 * (quella dell'utente che ha firmato il messaggio) e la firma da verificare sotto forma di stringa.
	 * @param ptx : il messaggio
	 * @param pub : la chiave pubblica del mittente
	 * @param signature : la firma da verificare
	 */
	public ECDSA_Signature(String ptx, PublicKey pub, String signature){
		this.ptx = ptx;
		this.pub = pub;
		this.signatureToVerify = signature.getBytes();
	}
	
	
	
	/**
	 * Genera una coppia di chiavi nel caso questa non sia stata passata dall'utente
	 */
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
	
	/**
	 * Effettua la verifica della firma digitale.
	 * @return true se la firma è verificata, false in caso contrario
	 * @throws ErrorInSignatureCheckingException
	 */
	public boolean verifySignature() throws ErrorInSignatureCheckingException{
		try{
			Signature verify = Signature.getInstance("SHA1withECDSA");
			verify.initVerify(this.pub);
			verify.update(this.ptx.getBytes());
			return verify.verify(this.signatureToVerify);
		}
		catch(SignatureException e){
			throw new ErrorInSignatureCheckingException();
		}
		catch(InvalidKeyException e){
			throw new ErrorInSignatureCheckingException();
		}
		catch(NoSuchAlgorithmException e){
			throw new ErrorInSignatureCheckingException();
		}
		
	}

	
}
