package it.polimi.dima.watchdog.crypto;

import it.polimi.dima.watchdog.UTILITIES.CryptoUtility;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
//import java.util.regex.Pattern;

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
	private String secretQuestion; //non è in Base64
	private String secretAnswer;
	private String receivedHash; //in Base64
	private String computedHash; //in Base64
	private byte[] hashToSend;   //come byte[], senza codifica Base64, comodo per essere inviato
	
	
	public String getComputedHash() {
		return this.computedHash;
	}
	
	public byte[] getMyPublicKey(){
		return this.myPublicKey;
	}
	
	public byte[] getReceivedPublicKey(){
		return this.receivedPublicKey;
	}
	
	public String getSecretQuestion(){
		return this.secretQuestion;
	}
	
	public byte[] getHashToSend(){
		return this.hashToSend;
	}
	
	/*public boolean isOtherKeyValidated(){
		return this.otherKeyIsValidated;
	}*/
	
	public void setSecretAnswer(String answer){
		this.secretAnswer = answer;
	}
	
	private void setReceivedPublicKey(byte[] receivedPublicKey){
		this.receivedPublicKey = receivedPublicKey;
	}
	
	/**
	 * Riceve la chiave otto forma di stringa Base64.
	 * @param receivedPublicKey
	 */
	public void setReceivedPublicKey(String receivedPublicKey){
		//TODO gestire il problema
		/*if (!Pattern.matches(CryptoUtility.BASE64_REGEX, receivedPublicKey)) {
			throw new IllegalArgumentException("La stringa passata come chiave non è in base64");
		}*/
		setReceivedPublicKey(Base64.decode(receivedPublicKey, Base64.DEFAULT));
	}
	
	public void setReceivedHash(String receivedHash){
		this.receivedHash = receivedHash;
	}
	
	public void setSecretQuestion(String question){
		this.secretQuestion = question;
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
		this.hashToSend = computeHash(this.myPublicKey, this.secretAnswer.getBytes());
	}
	
	public void doHashToCheck() throws NoSuchAlgorithmException{
		this.computedHash = Base64.encodeToString(computeHash(this.receivedPublicKey, this.secretAnswer.getBytes()), Base64.DEFAULT);
	}
	
	public boolean checkForEquality(){
		return this.receivedHash.equals(this.computedHash);
	}
	
	private byte[] computeHash(byte[] firstHalf, byte[] secondHalf) throws NoSuchAlgorithmException{
		byte[] input = new byte[firstHalf.length + secondHalf.length];
		System.arraycopy(firstHalf, 0, input, 0, firstHalf.length);
		System.arraycopy(secondHalf, 0, input, firstHalf.length, secondHalf.length);
		
		MessageDigest digest = MessageDigest.getInstance(CryptoUtility.SHA_256);
		return digest.digest(input);
	}

}
