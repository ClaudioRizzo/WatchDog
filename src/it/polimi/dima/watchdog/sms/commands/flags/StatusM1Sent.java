package it.polimi.dima.watchdog.sms.commands.flags;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.InvalidCipherTextException;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NonExistentTimeoutException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.commands.CommandSMS;
import it.polimi.dima.watchdog.sms.timeout.Timeout;
import it.polimi.dima.watchdog.utilities.CryptoUtility;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
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
	private static String STATUS_RECEIVED = "m2_received";
	public static String NEXT_SENT_STATUS = StatusM3Sent.CURRENT_STATUS;

	
	public StatusM1Sent(){
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}
	
	
	@Override
	public ParsableSMS parse(Context context, SmsMessage message, String other) throws NonExistentTimeoutException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NotECKeyException, UnsupportedEncodingException, IllegalStateException, InvalidCipherTextException, NoSignatureDoneException  {
		Timeout.getInstance(context).removeTimeout(MyPrefFiles.getMyPreference(MyPrefFiles.MY_NUMBER_FILE, MyPrefFiles.MY_PHONE_NUMBER, context) /*TODO inizializzarlo nel wizard*/, other);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM1Sent.STATUS_RECEIVED, context);
		
		byte[] publicKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, context),Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		PublicKey oPub = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
		
		this.parser = new M2Parser(message.getUserData(), oPub);
		this.parser.parse();
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m2 received and parsed");
		generateAndSendM3(other, context);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m3 sent");
		return null;
	}

	private void generateAndSendM3(String phoneNumber, Context ctx) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, IllegalStateException, InvalidCipherTextException, NotECKeyException, NoSignatureDoneException, NoSuchProviderException {
		String commandKey = phoneNumber + MyPrefFiles.TEMP_COMMAND;
		String passwordKey = phoneNumber + MyPrefFiles.OTHER_PASSWORD;
		String sessionKeyKey = phoneNumber + MyPrefFiles.SESSION_KEY;
		String ivKey = phoneNumber + MyPrefFiles.IV;
		
		String command = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, commandKey, ctx);
		String password = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, passwordKey, ctx);
		byte[] aesKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, sessionKeyKey, ctx), Base64.DEFAULT);
		Key encryptionKey = new SecretKeySpec(aesKey, CryptoUtility.AES_256);
		byte[] iv = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, ivKey, ctx), Base64.DEFAULT);
		
		byte[] myPriv = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, ctx), Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		PrivateKey mPriv = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(myPriv));
		
		CommandSMS sms = new CommandSMS(command.getBytes(), password, mPriv, encryptionKey, phoneNumber, iv);
		sms.construct();
		//cancello le preferenze ormai inutili
		MyPrefFiles.deleteUselessCommandSessionPreferences(phoneNumber, ctx);
		sms.send();
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