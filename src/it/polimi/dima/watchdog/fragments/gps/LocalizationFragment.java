package it.polimi.dima.watchdog.fragments.gps;

import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Random;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.crypto.AESKeyGenerator;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.sms.timeout.TimeoutWrapper;
import it.polimi.dima.watchdog.utilities.CryptoUtility;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author claudio, emanuele
 *
 */
public class LocalizationFragment extends Fragment implements OnClickListener {
	
	private String otherNumber;
	private Context ctx;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
		this.ctx = getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fragment_localization, container, false);
        Button mButton = (Button) v.findViewById(R.id.button_localization);
        mButton.setOnClickListener(this);
        return v;
    }

	@Override
	public void onClick(View v) {
		try{
			View mFragView = getView();
			String insertedPassword = getPassword(mFragView);
			String command = SMSUtility.LOCATE; //TODO in realtà il tipo di comando va preso dal tipo di bottone cliccato
			this.otherNumber = getPhoneNumber(mFragView);
			
			Log.i("[DEBUG]", "[DEBUG] Questa è la chiave: " + MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, this.otherNumber, this.ctx));
			
			if(!MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, this.otherNumber, this.ctx)){
				throw new NoSuchPreferenceFoundException("Non si può iniziare una sessione di comando con un utente con cui non è stato fatto SMP!!!");
			}
			
			storeDataToReuseInM3(insertedPassword, command);
			byte[] body = getBody();
			SMSUtility.sendMessage(this.otherNumber, SMSUtility.COMMAND_PORT, SMSUtility.M1_HEADER.getBytes(), body);
			TimeoutWrapper.addTimeout(SMSUtility.MY_PHONE, this.otherNumber, this.ctx);
		}
		catch (Exception e){
			SMSUtility.handleErrorOrExceptionInCommandSession(e, this.otherNumber, this.ctx);
		}
	}
	
	private void storeDataToReuseInM3(String insertedPassword, String command) {
		String passwordKey = this.otherNumber + MyPrefFiles.OTHER_PASSWORD;
		String commandKey = this.otherNumber + MyPrefFiles.TEMP_COMMAND;
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, passwordKey, insertedPassword, this.ctx);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, commandKey, command, this.ctx);
	}

	private byte[] getBody() throws NoSuchPreferenceFoundException, NotECKeyException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSignatureDoneException {
		byte[] iv = generateAndStoreIV();
		byte[] salt = generateSalt();
		byte[] secret = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.SHARED_SECRETS, this.otherNumber, this.ctx), Base64.DEFAULT);
		generateAndStoreAesKey(secret, salt);
		byte[] partialBody = constructPartialBody(iv, salt);
		byte[] signature = generateSignature(partialBody);
		return packBody(partialBody, signature);
	}

	private byte[] packBody(byte[] partialBody, byte[] signature) {
		byte[] body = new byte[partialBody.length + signature.length];
		System.arraycopy(partialBody, 0, body, 0, partialBody.length);
		System.arraycopy(signature, 0, body, partialBody.length, signature.length);
		return body;
	}

	private byte[] generateSignature(byte[] partialBody) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException {
		PrivateKey mPriv = retrieveMyPrivateKey();
		ECDSA_Signature sigMaker = new ECDSA_Signature(partialBody, mPriv);
		sigMaker.sign();
		return sigMaker.getSignature();
	}

	private PrivateKey retrieveMyPrivateKey() throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException {
		byte[] myPriv = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, this.ctx), Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(myPriv));
	}

	private byte[] constructPartialBody(byte[] iv, byte[] salt) {
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

	private String getPhoneNumber(View view) {
		EditText mEditText = (EditText) view.findViewById(R.id.phone_number1);
		String phoneNum = mEditText.getText().toString();
		return phoneNum;
	}
	
	private String getPassword(View view) {
		EditText mEditText = (EditText) view.findViewById(R.id.password_localize_1);
		String cleanPassword = mEditText.getText().toString();
		return cleanPassword;
	}
}