package it.polimi.dima.watchdog.crypto;


import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import android.util.Base64;
import it.polimi.dima.watchdog.exceptions.*;
import it.polimi.dima.watchdog.utilities.CryptoUtility;

/**
 * Classe che serve per generare la firma digitale "signature" (e la sua verione in stringa "string_signature")
 * di un messaggio "ptx" a sua volta stringa. La coppia di chiavi viene autogenerata se non è passata dall'utente,
 * e vengono lanciate eccezioni nei seguenti casi: le chiavi non sono valide, almeno una chiave è nulla, la firma
 * non è andata a buon fine.
 * 
 * @author emanuele
 *
 */
public class ECDSA_Signature {
	
	private byte[] plaintext;
	private Signature sig;
	private String stringSignature; // in Base64
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
	 * @throws NotECKeyException
	 */
	public ECDSA_Signature(String ptx, PrivateKey priv) throws NotECKeyException{
		this.plaintext = ptx.getBytes();
		this.mPriv = priv;
		
		if(!this.mPriv.getAlgorithm().toString().equals(CryptoUtility.EC)){
			throw new NotECKeyException();
		}
	}
	
	/**
	 * Costruttore in sign-mode che prevede il passaggio anche di una chiave privata; lancia un'eccezione se
	 * non è una chiave ECDSA.
	 * 
	 * @param ptx : il messaggio da firmare
	 * @param priv : la chiave privata
	 * @throws NotECKeyException
	 */
	public ECDSA_Signature(byte[] ptx, PrivateKey priv) throws NotECKeyException{
		this.plaintext = ptx;
		this.mPriv = priv;
		
		if(!this.mPriv.getAlgorithm().toString().equals(CryptoUtility.EC)){
			throw new NotECKeyException();
		}
	}
	
	
	/**
	 * Costruttore in decrypt-mode che riceve il messaggio, la chiave pubblica con cui verificare la firma
	 * (quella dell'utente che ha firmato il messaggio) e la firma da verificare sotto forma di array di byte.
	 * 
	 * @param ptx : il messaggio
	 * @param pub : la chiave pubblica del mittente
	 * @param signature : la firma da verificare
	 * @throws NotECKeyException 
	 */
	public ECDSA_Signature(byte[] ptx, PublicKey pub, byte[] signature) throws NotECKeyException{
		this.plaintext = ptx;
		this.oPub = pub;
		this.signatureToVerify = signature;
		
		if(!this.oPub.getAlgorithm().toString().equals(CryptoUtility.EC)){
			throw new NotECKeyException();
		}
	}
	
	
	/**
	 * Costruttore in decrypt-mode che riceve il messaggio, la chiave pubblica con cui verificare la firma
	 * (quella dell'utente che ha firmato il messaggio) e la firma da verificare sotto forma di stringa.
	 * 
	 * @param ptx : il messaggio
	 * @param pub : la chiave pubblica del mittente
	 * @param signature : la firma da verificare in Base64
	 * @throws NotECKeyException 
	 */
	public ECDSA_Signature(String ptx, PublicKey pub, String signature) throws NotECKeyException{
		this.plaintext = ptx.getBytes();
		this.oPub = pub;
		this.signatureToVerify = Base64.decode(signature, Base64.DEFAULT);
		
		if(!this.oPub.getAlgorithm().toString().equals(CryptoUtility.EC)){
			throw new NotECKeyException();
		}
	}
	
	
	/**
	 * Effettua la firma digitale aggiornando signature e string_signature, oppure, se qualunque cosa
	 * va storta, lancia un'eccezione.
	 * 
	 * @throws NoSignatureDoneException
	 */
	public void sign() throws NoSignatureDoneException{
		try {
			this.sig = Signature.getInstance(CryptoUtility.ECDSA_SHA1);
			this.sig.initSign(this.mPriv);
		    this.sig.update(this.plaintext);
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
		if(this.sig == null){
			throw new NoSignatureDoneException();
		}
	}
	
	/**
	 * Effettua la verifica della firma digitale.
	 * 
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
