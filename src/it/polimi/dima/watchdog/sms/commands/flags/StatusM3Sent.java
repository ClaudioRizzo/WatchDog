package it.polimi.dima.watchdog.sms.commands.flags;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.InvalidCipherTextException;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.commands.CommandFactory;
import it.polimi.dima.watchdog.sms.timeout.TimeoutWrapper;
import it.polimi.dima.watchdog.utilities.CryptoUtility;
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
//Ho mandato m3, mi aspetto m4 e dopo averlo parsato passo a free
public class StatusM3Sent implements CommandProtocolFlagsReactionInterface {

	private M4Parser parser;
	public static String CURRENT_STATUS = "m3_sent";
	private static String STATUS_RECEIVED = "m4_received";
	public static String NEXT_SENT_STATUS = StatusFree.CURRENT_STATUS;

	
	public StatusM3Sent(){
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}
	
	
	@Override
	public void parse(Context context, SmsMessage message, String other) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IllegalArgumentException, IllegalStateException, InvalidCipherTextException, ArbitraryMessageReceivedException, NotECKeyException, ErrorInSignatureCheckingException  {
		TimeoutWrapper.removeTimeout(SMSUtility.MY_PHONE, other, context);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM3Sent.STATUS_RECEIVED, context);
		
		PublicKey oPub = fetchOtherPublicKey(other, context);
		Key decryptionKey = fetchDecryptionKey(other, context);
		byte[] iv = fetchIv(other, context);
		
		this.parser = new M4Parser(message.getUserData(), oPub, decryptionKey, iv);
		this.parser.parse();
		byte[] header = this.parser.getSpecificHeader();
		byte[] body = this.parser.getBody();
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m4 received and parsed");
		handleReturnedData(header, body);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM3Sent.NEXT_SENT_STATUS, context);
	}
	
	private void handleReturnedData(byte[] header, byte[] body) throws ArbitraryMessageReceivedException {
		CommandFactory factory = new CommandFactory();
		String factoryHeader = SMSUtility.bytesToHex(body);
		String factorybody = Base64.encodeToString(body, Base64.DEFAULT);
		SMSM4Handler handler = new SMSM4Handler();
		ParsableSMS smsToParse = factory.getMessage(factoryHeader, factorybody);
		smsToParse.handle(handler);
	}


	private byte[] fetchIv(String other, Context ctx) throws NoSuchPreferenceFoundException {
		String iv = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV_FOR_M4, ctx);
		return Base64.decode(iv, Base64.DEFAULT);
	}


	private Key fetchDecryptionKey(String other, Context ctx) throws NoSuchPreferenceFoundException {
		String decKey = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.KEY_FOR_M4, ctx);
		byte[] decKeyValue = Base64.decode(decKey, Base64.DEFAULT);
		return new SecretKeySpec(decKeyValue, CryptoUtility.AES_256);
	}


	private PublicKey fetchOtherPublicKey(String other, Context ctx) throws InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, NoSuchPreferenceFoundException{
		byte[] publicKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, ctx),Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		return keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
	}

	@Override
	public String getCurrentStatus() {
		return StatusM3Sent.CURRENT_STATUS;
	}

	@Override
	public String getNextSentStatus() {
		return StatusM3Sent.NEXT_SENT_STATUS;
	}
}