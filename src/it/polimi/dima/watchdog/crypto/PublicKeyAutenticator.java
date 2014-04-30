package it.polimi.dima.watchdog.crypto;

import it.polimi.dima.watchdog.CryptoUtility;
import it.polimi.dima.watchdog.MyPrefFiles;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;

import android.util.Base64;
import android.util.Log;

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
	
	//TODO: cancella
	public String getReceivedHash() {
		return this.receivedHash;
	}
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
		if (false && !Pattern.matches(CryptoUtility.BASE64_REGEX, receivedPublicKey)) {
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
		byte[] myPub = this.myPublicKey;
		byte[] answ = this.secretAnswer.getBytes();
		byte[] input = new byte[myPub.length + answ.length];
		System.arraycopy(myPub, 0, input, 0, myPub.length);
		System.arraycopy(answ, 0, input, myPub.length, answ.length);
		int length = input.length;
		String input64 = Base64.encodeToString(input, Base64.DEFAULT);
		Log.i("[DEBUG : lunghezza input a hashToSend]", String.valueOf(length));
		Log.i("[DEBUG : input a hashToSend]", input64);
		
		
		MessageDigest digest = MessageDigest.getInstance(CryptoUtility.SHA_256);
		this.hashToSend = digest.digest(input);
	}
	
	public void doHashToCheck() throws NoSuchAlgorithmException{
		byte[] oPub = this.receivedPublicKey;
		byte[] answ = this.secretAnswer.getBytes();
		byte[] input = new byte[oPub.length + answ.length];
		System.arraycopy(oPub, 0, input, 0, oPub.length);
		System.arraycopy(answ, 0, input, oPub.length, answ.length);
		int length = input.length;
		String input64 = Base64.encodeToString(input, Base64.DEFAULT);
		Log.i("[DEBUG : lunghezza input a hashToCheck]", String.valueOf(length));
		Log.i("[DEBUG : input a hashToCheck]", input64);
		
		
		MessageDigest digest = MessageDigest.getInstance(CryptoUtility.SHA_256);
		byte[] hash = digest.digest(input);
		
		this.computedHash = Base64.encodeToString(hash, Base64.DEFAULT);
	}
	
	public boolean checkForEquality(){
		return this.receivedHash.equals(this.computedHash);
	}

}
