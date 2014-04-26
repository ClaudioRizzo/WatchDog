package it.polimi.dima.watchdog.crypto;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.regex.Pattern;

import android.util.Base64;
import it.polimi.dima.watchdog.CryptoUtility;
import it.polimi.dima.watchdog.PasswordUtils;
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
	private byte[] plaintext;
	private Signature sig;
	private String stringSignature; //Base64
	private byte[] signature;
	private PublicKey oPub;
	private PrivateKey mPriv;
	private byte[] signatureToVerify;
	
	public String getStringSignature(){
		return this.stringSignature;
	}
	
	public byte[] getSignature(){
		return this.signature;
	}
	
	public PrivateKey getPrivateKey(){
		return this.mPriv;
	}
	
	public PublicKey getPublicKey(){
		return this.oPub;
	}
	
	
	
	
	
	/**
	 * Costruttore in sign-mode che prevede il passaggio anche di una chiave privata; lancia un'eccezione se
	 * non è una chiave ECDSA.
	 * 
	 * @param ptx : il messaggio da firmare
	 * @param priv : la chiave privata
	 * @throws NoECDSAKeyPairGeneratedException
	 */
	public ECDSA_Signature(String ptx, PrivateKey priv) throws NoECDSAKeyPairGeneratedException{
		this.ptx = ptx;
		this.mPriv = priv;
		
		if(!this.mPriv.getAlgorithm().toString().equals(CryptoUtility.EC)){
			throw new NoECDSAKeyPairGeneratedException();
		}
	}
	
	/**
	 * Costruttore in sign-mode che prevede il passaggio anche di una chiave privata; lancia un'eccezione se
	 * non è una chiave ECDSA.
	 * 
	 * @param ptx : il messaggio da firmare
	 * @param priv : la chiave privata
	 * @throws NoECDSAKeyPairGeneratedException
	 */
	public ECDSA_Signature(byte[] ptx, PrivateKey priv) throws NoECDSAKeyPairGeneratedException{
		this.plaintext = ptx;
		this.mPriv = priv;
		
		if(!this.mPriv.getAlgorithm().toString().equals(CryptoUtility.EC)){
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
	public ECDSA_Signature(byte[] ptx, PublicKey pub, byte[] signature){
		this.plaintext = ptx;
		this.oPub = pub;
		this.signatureToVerify = signature;
	}
	
	
	/**
	 * Costruttore in decrypt-mode che riceve il messaggio, la chiave pubblica con cui verificare la firma
	 * (quella dell'utente che ha firmato il messaggio) e la firma da verificare sotto forma di stringa.
	 * @param ptx : il messaggio
	 * @param pub : la chiave pubblica del mittente
	 * @param signature : la firma da verificare in Base64
	 */
	public ECDSA_Signature(String ptx, PublicKey pub, String signature){//signature è in Base64
		if (!Pattern.matches(CryptoUtility.BASE64_REGEX, signature)) {
			throw new IllegalArgumentException("La stringa passata come firma non è in base64");
		}
		this.plaintext = ptx.getBytes();
		this.oPub = pub;
		this.signatureToVerify = Base64.decode(signature, Base64.DEFAULT);
	}
	
	
	/**
	 * Effettua la firma digitale aggiornando signature e string_signature, oppure, se qualunque cosa
	 * va storta, lancia un'eccezione.
	 * @throws NoSignatureDoneException
	 */
	public void sign() throws NoSignatureDoneException{
		try {
			this.sig = Signature.getInstance(CryptoUtility.ECDSA_SHA1);
			this.sig.initSign(this.mPriv);
			
			if(this.plaintext == null && this.ptx != null){
				byte[] strByte = this.ptx.getBytes(PasswordUtils.UTF_8);
		        this.sig.update(strByte);
			}
			else{
				this.sig.update(this.plaintext);
			}
	        

	        this.signature = this.sig.sign();
	        
	        this.stringSignature = Base64.encodeToString(this.signature, Base64.DEFAULT);
		}
		catch (NoSuchAlgorithmException e) {
			this.sig = null;
		} 
		catch (SignatureException e) {
			this.sig = null;
		}
		catch (InvalidKeyException e) {
			this.sig = null;
		}
		catch (UnsupportedEncodingException e) {
			this.sig = null;
		}
		
		if(this.sig == null){
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
			Signature verify = Signature.getInstance(CryptoUtility.ECDSA_SHA1);
			verify.initVerify(this.oPub);
			verify.update(this.plaintext);
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
