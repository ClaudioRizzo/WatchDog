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
	private String secretQuestion; //non è in Base64
	private String secretAnswer; //non è in Base64
	private String receivedHash; //in Base64
	private String computedHash; //in Base64
	private byte[] hashToSend;
	
	
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
	
	public void setSecretAnswer(String answer){
		this.secretAnswer = answer;
	}
	
	private void setReceivedPublicKey(byte[] receivedPublicKey){
		this.receivedPublicKey = receivedPublicKey;
	}
	
	/**
	 * Riceve la chiave sotto forma di stringa Base64.
	 * 
	 * @param receivedPublicKey
	 */
	public void setReceivedPublicKey(String receivedPublicKey){
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
	 * 
	 * @param key
	 */
	public void setMyPublicKey(byte[] key){
		this.myPublicKey = key;
	}
	
	/**
	 * Riceve la chiave sotto forma di stringa Base64.
	 * 
	 * @param key
	 */
	public void setMyPublicKey(String key){
		this.myPublicKey = Base64.decode(key, Base64.DEFAULT);		
	}

	/**
	 * Computa l'hash di chiave pubblica || risposta segreta che sarà poi da inviare all'altro telefono.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public void doHashToSend() throws NoSuchAlgorithmException{
		this.hashToSend = computeHash(this.myPublicKey, this.secretAnswer.getBytes());
	}
	
	/**
	 * Computa l'hash di verifica.
	 * 
	 * @throws NoSuchAlgorithmException
	 */
	public void doHashToCheck() throws NoSuchAlgorithmException{
		this.computedHash = Base64.encodeToString(computeHash(this.receivedPublicKey, this.secretAnswer.getBytes()), Base64.DEFAULT);
	}
	
	/**
	 * Controlla se l'hash ricevuto e quello computato sono uguali.
	 * 
	 * @return true in caso affermativo, false altrimenti
	 */
	public boolean checkForEquality(){
		return this.receivedHash.equals(this.computedHash);
	}
	
	/**
	 * Computa un hash della concatenazione di firstHalf e secondHalf con SHA256.
	 * 
	 * @param firstHalf : la prima metà del plaintext
	 * @param secondHalf : la seconda metà del plaintext
	 * @return il digest SHA256
	 * @throws NoSuchAlgorithmException
	 */
	private byte[] computeHash(byte[] firstHalf, byte[] secondHalf) throws NoSuchAlgorithmException{
		byte[] input = new byte[firstHalf.length + secondHalf.length];
		System.arraycopy(firstHalf, 0, input, 0, firstHalf.length);
		System.arraycopy(secondHalf, 0, input, firstHalf.length, secondHalf.length);
		
		MessageDigest digest = MessageDigest.getInstance(CryptoUtility.SHA_256);
		return digest.digest(input);
	}
}