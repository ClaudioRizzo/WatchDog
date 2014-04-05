package it.polimi.dima.watchdog.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import android.util.Base64;

/**
 * In questa classe avverrà l'autenticazione della chiave pubblica dell'altro telefono mediante il
 * protocollo del socialista milionario.
 * 
 * @author emanuele
 *
 */
public class PublicKeyAutenticator {
	public byte[] myPublicKey;
	public byte[] receivedPublicKey;
	public String secretQuestion;
	public String secretAnswer;
	public String receivedHash; //hash(bPublicKey,secretAnswerGivenByB) --> l'hash che l'altro mi ha inviato
	public String computedHash; //hash(receivedPublicKey,secretAnswer) --> l'hash che devo calcolare per vedere se l'altro sa la risposta
	public String hashToSend;   //hash(myPublicKey,secretAnswer) --> l'hash che devo mandare per provare che so la risposta
	
	public void setReceivedPublicKey(byte[] receivedPublicKey){
		this.receivedPublicKey = receivedPublicKey;
	}
	
	public void setReceivedHash(String receivedHash){
		this.receivedHash = receivedHash;
	}
	
	public PublicKeyAutenticator(byte[] myPublicKey, String secretQuestion, String secretAnswer){
		this.myPublicKey = myPublicKey;
		this.secretQuestion = secretQuestion;
		this.secretAnswer = secretAnswer;
	}
	
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
