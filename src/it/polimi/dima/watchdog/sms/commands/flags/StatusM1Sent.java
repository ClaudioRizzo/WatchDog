package it.polimi.dima.watchdog.sms.commands.flags;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import org.spongycastle.crypto.InvalidCipherTextException;
import it.polimi.dima.watchdog.crypto.AESKeyGenerator;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.commands.CommandSMS;
import it.polimi.dima.watchdog.sms.timeout.TimeoutWrapper;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;

/**
 * 
 * @author emanuele
 *
 */
//ho mandato m1, mi aspetto m2, lo parso, invio m3 e passo a m3_sent
public class StatusM1Sent implements CommandProtocolFlagsReactionInterface {

	private M2Parser parser;
	public static String CURRENT_STATUS = "m1_sent";
	public static String STATUS_RECEIVED = "m2_received";
	public static String NEXT_SENT_STATUS = StatusM3Sent.CURRENT_STATUS;

	
	public StatusM1Sent(){
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}
	
	
	@Override
	public void parse(Context context, SmsMessage message, String other) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NotECKeyException, UnsupportedEncodingException, IllegalStateException, InvalidCipherTextException, NoSignatureDoneException  {
		TimeoutWrapper.removeTimeout(SMSUtility.MY_PHONE, other, context);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM1Sent.STATUS_RECEIVED, context);
		
		PublicKey oPub = MyPrefFiles.getOtherPublicKey(context, other);
		
		this.parser = new M2Parser(message.getUserData(), oPub);
		this.parser.parse();
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m2 received and parsed");
		storeParametersToParseM4(other, context);
		generateAndSendM3(other, context);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m3 sent");
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM1Sent.NEXT_SENT_STATUS, context);
		TimeoutWrapper.addTimeout(SMSUtility.MY_PHONE, other, context);
	}

	private void storeParametersToParseM4(String other, Context context) throws NoSuchPreferenceFoundException {
		String iv = Base64.encodeToString(this.parser.getIv(), Base64.DEFAULT);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV_FOR_M4, iv, context);
		byte[] secret = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.SHARED_SECRETS, other, context), Base64.DEFAULT);
		Log.i("DEBUG", "DEBUG lato ricevente: sale prima di generare la chiave: " + Base64.encodeToString(this.parser.getSalt(), Base64.DEFAULT));
		AESKeyGenerator keygen = new AESKeyGenerator(secret, this.parser.getSalt());
		Key aesKey = keygen.generateKey();
		String aesKeyBase64 = Base64.encodeToString(aesKey.getEncoded(), Base64.DEFAULT);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.KEY_FOR_M4, aesKeyBase64, context);
		Log.i("DEBUG", "DEBUG chiave lato ricevente: " + aesKeyBase64);
	}


	private void generateAndSendM3(String phoneNumber, Context context) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, IllegalStateException, InvalidCipherTextException, NotECKeyException, NoSignatureDoneException, NoSuchProviderException {
		byte[] command = MyPrefFiles.getSessionCommand(context, phoneNumber);
		byte[] password = MyPrefFiles.getPreviouslyStoredPassword(context, phoneNumber);
		byte[] passwordSalt = MyPrefFiles.getOtherPasswordSalt(context, phoneNumber);
		byte[] saltedPassword = new byte[password.length + passwordSalt.length];
		System.arraycopy(password, 0, saltedPassword, 0, password.length);
		System.arraycopy(passwordSalt, 0, saltedPassword, password.length, passwordSalt.length);
		
		Key encryptionKey = MyPrefFiles.getSymmetricCryptoKey(context, phoneNumber, true);
		byte[] iv = MyPrefFiles.getIV(context, phoneNumber, true);
		PrivateKey mPriv = MyPrefFiles.getMyPrivateKey(context);
		
		CommandSMS sms = new CommandSMS(command, saltedPassword, mPriv, encryptionKey, iv);
		sms.construct();
		//cancello le preferenze ormai inutili
		MyPrefFiles.deleteUselessCommandSessionPreferencesForM3(phoneNumber, context);
		SMSUtility.sendSingleMessage(phoneNumber, SMSUtility.COMMAND_PORT, sms.getFinalSignedAndEncryptedMessage());
	}

	@Override
	public String getCurrentStatus() {
		return StatusM1Sent.CURRENT_STATUS;
	}
	
	@Override
	public String getNextSentStatus() {
		return StatusM1Sent.NEXT_SENT_STATUS;
	}
}