package it.polimi.dima.watchdog.fragments.gps;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.crypto.AESKeyGenerator;
import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.sms.commands.flags.StatusM1Sent;
import it.polimi.dima.watchdog.sms.timeout.TimeoutWrapper;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class GpsLocalizeClickHandler implements OnClickListener {

	private String pswd;
	private String otherNumber;
	private byte[] keySalt;
	private Context ctx;
	private View fragView;
	
	public GpsLocalizeClickHandler(View fragView,String otherNumber, Context ctx) {
		this.fragView = fragView;
		this.ctx = ctx;
		this.otherNumber = otherNumber;
	}
	
	@Override
	public void onClick(View v) {
		try{
			this.pswd = getPassword();
			//this.otherNumber = getPhoneNumber();
			
			byte[] command = SMSUtility.hexStringToByteArray(SMSUtility.LOCATE); //TODO in realtà il tipo di comando va preso dal tipo di bottone cliccato
			
			if(!MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.otherNumber, this.ctx)){
				throw new NoSuchPreferenceFoundException("Non si può iniziare una sessione di comando con un utente con cui non è stato fatto SMP!!!");
			}
			
			storeDataToReuseInM3(pswd, command);
			
			byte[] body = packIvAndSalt();
			byte[] secret = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.SHARED_SECRETS, this.otherNumber, this.ctx), Base64.DEFAULT);
			generateAndStoreAesKey(secret, this.keySalt);
			byte[] header = SMSUtility.hexStringToByteArray(SMSUtility.M1_HEADER);
			byte[] message = packHeaderAndBody(header, body);
			byte[] signature = generateSignature(message);
			byte[] finalMessage = packMessage(message, signature);
			
			SMSUtility.sendCommandMessage(this.otherNumber, SMSUtility.COMMAND_PORT, finalMessage);
			MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.otherNumber, StatusM1Sent.CURRENT_STATUS, this.ctx);
			TimeoutWrapper.addTimeout(SMSUtility.MY_PHONE, this.otherNumber, this.ctx);
			
			
		}
		catch (Exception e){
			SMSUtility.handleErrorOrExceptionInCommandSession(e, this.otherNumber, this.ctx);
		}
	}
	
	private void storeDataToReuseInM3(String insertedPassword, byte[] command) {
		String passwordKey = this.otherNumber + MyPrefFiles.OTHER_PASSWORD;
		String commandKey = this.otherNumber + MyPrefFiles.TEMP_COMMAND;
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, passwordKey, insertedPassword, this.ctx);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, commandKey, Base64.encodeToString(command, Base64.DEFAULT), this.ctx);
	}

	private byte[] packIvAndSalt() throws NoSuchPreferenceFoundException, NotECKeyException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSignatureDoneException {
		byte[] iv = generateAndStoreIV();
		byte[] salt = generateSalt();
		this.keySalt = salt;
		return constructBody(iv, salt);
	}

	private byte[] packHeaderAndBody(byte[] header, byte[] body) {
		byte[] message = new byte[header.length + body.length];
		System.arraycopy(header, 0, message, 0, header.length);
		System.arraycopy(body, 0, message, header.length, body.length);
		return message;
	}
	
	private byte[] packMessage(byte[] partialMessage, byte[] signature) {
		byte[] finalMessage = new byte[partialMessage.length + signature.length];
		System.arraycopy(partialMessage, 0, finalMessage, 0, partialMessage.length);
		System.arraycopy(signature, 0, finalMessage, partialMessage.length, signature.length);
		return finalMessage;
	}

	private byte[] generateSignature(byte[] message) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException {
		PrivateKey mPriv = MyPrefFiles.getMyPrivateKey(this.ctx);
		return CryptoUtility.doSignature(message, mPriv);
	}

	private byte[] constructBody(byte[] iv, byte[] salt) {
		byte[] partialBody = new byte[iv.length + salt.length];
		System.arraycopy(iv, 0, partialBody, 0, iv.length);
		System.arraycopy(salt, 0, partialBody, iv.length, salt.length);
		return partialBody;
	}

	private void generateAndStoreAesKey(byte[] secret, byte[] salt) {
		AESKeyGenerator aesKeyGenerator = new AESKeyGenerator(secret, salt);
		String sessionKey = Base64.encodeToString(aesKeyGenerator.generateKey().getEncoded(), Base64.DEFAULT);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, this.otherNumber + MyPrefFiles.SESSION_KEY, sessionKey, this.ctx);
	}

	private byte[] generateSalt() {
		byte[] salt = new byte[32];
		new Random().nextBytes(salt);
		return salt;
	}

	private byte[] generateAndStoreIV() {
		byte[] iv = new byte[12];
		new Random().nextBytes(iv);
		String initializationVector = Base64.encodeToString(iv, Base64.DEFAULT);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, this.otherNumber + MyPrefFiles.IV, initializationVector, this.ctx);
		return iv;
	}
	
	/*private String getPhoneNumber() {
		EditText mEditText = (EditText) fragView.findViewById(R.id.phone_number1);
		String phoneNum = mEditText.getText().toString();
		return phoneNum;
	}*/
	
	private String getPassword() {
		EditText mEditText = (EditText) fragView.findViewById(R.id.edit_text_associated_password);
		String cleanPassword = mEditText.getText().toString();
		return cleanPassword;
	}


}
