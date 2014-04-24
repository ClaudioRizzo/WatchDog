package it.polimi.dima.watchdog.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
	private boolean otherKeyIsValidated;
	
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
	
	public boolean isOtherKeyValidated(){
		return this.otherKeyIsValidated;
	}
	
	public void setSecretAnswer(String answer){
		this.secretAnswer = answer;
	}
	
	public boolean isMyKeyValidatedByTheOther(){
		return this.myKeyIsValidated;
	}
	
	public void setReceivedPublicKey(byte[] receivedPublicKey){
		this.receivedPublicKey = receivedPublicKey;
	}
	
	public void setReceivedHash(String receivedHash){
		this.receivedHash = receivedHash;
	}
	
	public void setSecretQuestion(String question){
		this.secretQuestion = question;
	}
	
	public void setOtherKeyValidated(boolean value){
		this.otherKeyIsValidated = value;
	}
	
	public void setMyKeyValidatedByTheOther(boolean value){
		this.otherKeyIsValidated = value;
	}
	
	public PublicKeyAutenticator(byte[] myPublicKey, String secretQuestion, String secretAnswer){
		this.myPublicKey = myPublicKey;
		this.secretQuestion = secretQuestion;
		this.secretAnswer = secretAnswer;
	}
	
	public PublicKeyAutenticator() {}

	public void doHashToSend() throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(new String(this.myPublicKey + this.secretAnswer).getBytes());
		this.hashToSend = Base64.encodeToString(hash, Base64.DEFAULT);
	}
	
	public void doHashToCheck() throws NoSuchAlgorithmException{
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		byte[] hash = digest.digest(new String(this.receivedPublicKey + this.secretAnswer).getBytes());
		this.computedHash = Base64.encodeToString(hash, Base64.DEFAULT);
	}
	
	public boolean checkForEquality(){
		return this.receivedHash.equals(this.computedHash);
	}

}
