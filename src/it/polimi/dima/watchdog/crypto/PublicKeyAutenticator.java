package it.polimi.dima.watchdog.crypto;

import it.polimi.dima.watchdog.CryptoUtility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import android.util.Base64;

/**
 * In questa classe avverrà l'autenticazione della chiave pubblica dell'altro telefono mediante il
 * protocollo del socialista milionario.
 * 
 * @author emanuele
 *
 */
public class PublicKeyAutenticator {
	private byte[] myPublicKey;
	private byte[] receivedPublicKey;
	private String secretQuestion;
	private String secretAnswer;
	private String receivedHash; //hash(bPublicKey,secretAnswerGivenByB) --> l'hash che l'altro mi ha inviato
	private String computedHash; //hash(receivedPublicKey,secretAnswer) --> l'hash che devo calcolare per vedere se l'altro sa la risposta
	private String hashToSend;   //hash(myPublicKey,secretAnswer) --> l'hash che devo mandare per provare che so la risposta
	private boolean myKeyIsValidated;
	//private boolean otherKeyIsValidated;
	
	public byte[] getMyPublicKey(){
		return this.myPublicKey;
	}
	
	public byte[] getReceivedPublicKey(){
		return this.receivedPublicKey;
	}
	
	public String getSecretQuestion(){
		return this.secretQuestion;
	}
	
	public String getHashToSend(){
		return this.hashToSend;
	}
	
	/*public boolean isOtherKeyValidated(){
		return this.otherKeyIsValidated;
	}*/
	
	public void setSecretAnswer(String answer){
		this.secretAnswer = answer;
	}
	
	public boolean isMyKeyValidatedByTheOther(){
		return this.myKeyIsValidated;
	}
	
	private void setReceivedPublicKey(byte[] receivedPublicKey){
		this.receivedPublicKey = receivedPublicKey;
	}
	
	/**
	 * Riceve la chiave otto forma di stringa Base64.
	 * @param receivedPublicKey
	 */
	public void setReceivedPublicKey(String receivedPublicKey){
		if (!Pattern.matches(CryptoUtility.BASE64_REGEX, receivedPublicKey)) {
			throw new IllegalArgumentException("La stringa passata come chiave non è in base64");
		}
		setReceivedPublicKey(Base64.decode(receivedPublicKey, Base64.DEFAULT));
	}
	
	public void setReceivedHash(String receivedHash){
		this.receivedHash = receivedHash;
	}
	
	public void setSecretQuestion(String question){
		this.secretQuestion = question;
	}
	
	
	public void setMyKeyValidatedByTheOther(boolean value){
		this.myKeyIsValidated = value;
	}
	
	/**
	 * Riceve direttamente un array di byte.
	 * @param key
	 */
	public void setMyPublicKey(byte[] key){
		this.myPublicKey = key;
	}
	
	/**
	 * Riceve la chiave sotto forma di stringa Base64.
	 * @param key
	 */
	public void setMyPublicKey(String key){
		this.myPublicKey = Base64.decode(key, Base64.DEFAULT);		
	}
	
	public PublicKeyAutenticator(byte[] myPublicKey, String secretQuestion, String secretAnswer){
		this.myPublicKey = myPublicKey;
		this.secretQuestion = secretQuestion;
		this.secretAnswer = secretAnswer;
	}
	
	public PublicKeyAutenticator() {}

	public void doHashToSend() throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance(CryptoUtility.SHA_256);
		byte[] hash = digest.digest(new String(this.myPublicKey + this.secretAnswer).getBytes());
		this.hashToSend = Base64.encodeToString(hash, Base64.DEFAULT);
	}
	
	public void doHashToCheck() throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance(CryptoUtility.SHA_256);
		byte[] hash = digest.digest(new String(this.receivedPublicKey + this.secretAnswer).getBytes());
		this.computedHash = Base64.encodeToString(hash, Base64.DEFAULT);
	}
	
	public boolean checkForEquality(){
		return this.receivedHash.equals(this.computedHash);
	}

}
