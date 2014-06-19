package it.polimi.dima.watchdog.password;

import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.util.Base64;
import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.WrongPasswordException;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;

/**
 * Classe che si occupa di cambiare la propria password. Utilizzo: prima costruire la classe con le stringhe
 * vecchia password e nuova password, poi chiamare nell'ordine changePassword(), storePasswordHashAndSalt() e
 * notifyAllContacts().
 * 
 * @author emanuele
 *
 */
public class PasswordResetter {

	private byte[] actualPasswordHash;
	private byte[] actualSalt;
	private byte[] typedPassword;
	private byte[] newPassword;
	private byte[] computedHash;
	private byte[] newSalt;
	private byte[] newPasswordHash;
	private Context context;
	
	
	/**
	 * Crea un PasswordResetter a partire dalle password digitate dall'utente.
	 * 
	 * @param typedPassword : la vecchia password digitata dall'utente
	 * @param newPassword : la nuova password digitata dall'utente
	 * @param context : il contesto corrente
	 * @throws NoSuchPreferenceFoundException
	 */
	public PasswordResetter(String typedPassword, String newPassword, Context context) throws NoSuchPreferenceFoundException{
		this.context = context;
		this.actualPasswordHash = MyPrefFiles.getMyPasswordHash(this.context);
		this.actualSalt = MyPrefFiles.getMyPasswordSalt(this.context);
		this.typedPassword = typedPassword.getBytes();
		this.newPassword = newPassword.getBytes();
	}
	
	/**
	 * Controlla se la password inserita è quella corretta.
	 * 
	 * @return true in caso affermativo, false altrimenti.
	 * @throws NoSuchAlgorithmException
	 */
	private boolean verifyInsertedPassword() throws NoSuchAlgorithmException{
		this.computedHash = PasswordUtils.computeHash(this.typedPassword, this.actualSalt, CryptoUtility.SHA_256);
		if(!Arrays.equals(this.actualPasswordHash, this.computedHash)){
			return true;
		}
		return false;
	}
	
	/**
	 * Cambia la password e ritorna l'hash di quella nuova.
	 * 
	 * @return l'hash della nuova password
	 * @throws WrongPasswordException se la vecchia password non è corretta
	 * @throws NoSuchAlgorithmException
	 */
	public byte[] changePassword() throws WrongPasswordException, NoSuchAlgorithmException{
		if(!verifyInsertedPassword()){
			throw new WrongPasswordException("La password inserita non è giusta!!!");
		}
		else{
			this.newSalt = PasswordUtils.nextSalt();
			this.newPasswordHash = PasswordUtils.computeHash(this.newPassword, this.newSalt, CryptoUtility.SHA_256);
			return this.newPasswordHash;
		}
	}

	/**
	 * Salva nelle preferenze l'hash della propria password e il sale corrispondente.
	 */
	public void storePasswordHashAndSalt() {
		String hash = Base64.encodeToString(this.newPasswordHash, Base64.DEFAULT);
		String salt = Base64.encodeToString(this.newSalt, Base64.DEFAULT);
		MyPrefFiles.setMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_HASH, hash, this.context);
		MyPrefFiles.setMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_SALT, salt, this.context);
	}
	
	
	public void notifyAllContacts() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException{
		byte[] header = SMSUtility.hexStringToByteArray(SMSUtility.PASSWORD_CHANGE);
		byte[] messageWithoutSignature = constructMessage(header);
		PrivateKey mPriv = MyPrefFiles.getMyPrivateKey(this.context);
		byte[] signature = CryptoUtility.doSignature(messageWithoutSignature, mPriv);
		byte[] message = packMessage(messageWithoutSignature, signature);
		
		broadcastPasswordChange(message);
	}

	private void broadcastPasswordChange(byte[] message) {
		Map<String, ?> contactsMap = MyPrefFiles.getPrefMap(MyPrefFiles.ASSOCIATED, this.context);
		Set<String> contacts = contactsMap.keySet();
		
		for(String contact : contacts){
			SMSUtility.sendSingleMessage(contact, SMSUtility.PASSWORD_RESET_PORT, message);
		}
	}

	private byte[] packMessage(byte[] messageWithoutSignature, byte[] signature) {
		byte[] message = new byte[messageWithoutSignature.length + signature.length];
		
		System.arraycopy(messageWithoutSignature, 0, message, 0, messageWithoutSignature.length);
		System.arraycopy(signature, 0, message, messageWithoutSignature.length, signature.length);
		
		return message;
	}

	private byte[] constructMessage(byte[] header) {
		byte[] message = new byte[header.length + this.newSalt.length];
		
		System.arraycopy(header, 0, message, 0, header.length);
		System.arraycopy(this.newSalt, 0, message, header.length, this.newSalt.length);
		
		return message;
	}
}