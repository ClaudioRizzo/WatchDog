package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.crypto.AESKeyGenerator;
import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.password.PasswordUtils;
import it.polimi.dima.watchdog.sms.commands.session.StatusM1Sent;
import it.polimi.dima.watchdog.sms.commands.timeout.TimeoutWrapper;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Random;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class GenericSmsClickHandler {
	
	private String password;
	private String otherNumber;
	private byte[] keySalt;
	private Context context;
	private View fragView;
	
	
	public GenericSmsClickHandler(View fragView,String otherNumber, Context context) {
		this.fragView = fragView;
		this.context = context;
		this.otherNumber = otherNumber;
	}
	
	public void handleClick(View v, String commandString) {
		try{
			Log.i("[DEBUG]", "Ho cliccato");
			this.password = getPassword();
			
			if(PasswordUtils.isEmpty(this.password)){
				ErrorManager.handleNonFatalError(ErrorFactory.BLANK_FIELD, this.context);
			}
			else{
				byte[] command = SMSUtility.hexStringToByteArray(commandString); 
				
				if(!MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.otherNumber, this.context)){
					throw new NoSuchPreferenceFoundException(ErrorFactory.COMMAND_TO_NOT_ASSOCIATED);
				}
				
				storeDataToReuseInM3(this.password, command);
				
				byte[] body = packIvAndSalt();
				byte[] secret = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.SHARED_SECRETS, this.otherNumber, this.context), Base64.DEFAULT);
				generateAndStoreAesKey(secret, this.keySalt);
				byte[] header = SMSUtility.hexStringToByteArray(SMSUtility.M1_HEADER);
				byte[] message = packHeaderAndBody(header, body);
				byte[] signature = generateSignature(message);
				byte[] finalMessage = packMessage(message, signature);
				
				SMSUtility.sendSingleMessage(this.otherNumber, SMSUtility.COMMAND_PORT, finalMessage);
				MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.otherNumber, StatusM1Sent.CURRENT_STATUS, this.context);
				TimeoutWrapper.addTimeout(SMSUtility.MY_PHONE, this.otherNumber, this.context);
			}
		}
		catch (Exception e){
			ErrorManager.handleErrorOrExceptionInCommandSession(e, this.otherNumber, this.context, false);
		}
	}
	
	
	private void storeDataToReuseInM3(String insertedPassword, byte[] command) {
		String passwordKey = this.otherNumber + MyPrefFiles.OTHER_PASSWORD;
		String commandKey = this.otherNumber + MyPrefFiles.TEMP_COMMAND;
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, passwordKey, insertedPassword, this.context);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, commandKey, Base64.encodeToString(command, Base64.DEFAULT), this.context);
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
		PrivateKey mPriv = MyPrefFiles.getMyPrivateKey(this.context);
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
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, this.otherNumber + MyPrefFiles.SESSION_KEY, sessionKey, this.context);
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
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, this.otherNumber + MyPrefFiles.IV, initializationVector, this.context);
		return iv;
	}
	
	private String getPassword() {
		EditText mEditText = (EditText) fragView.findViewById(R.id.edit_text_associated_password);
		String cleanPassword = mEditText.getText().toString();
		return cleanPassword;
	}
}