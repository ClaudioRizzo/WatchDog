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
import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
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

/**
 * 
 * @author emanuele
 *
 */
//Ho mandato m2, mi aspetto m3, lo parso e lo ritorno
public class StatusM2Sent implements CommandProtocolFlagsReactionInterface {

	
	private ParsableSMS recMsg;
	private CommandFactory commandFactory;
	private M3Parser parser;
	public static String CURRENT_STATUS = "m2_sent";
	private static String STATUS_RECEIVED = "m3_received";
	public static String NEXT_SENT_STATUS = StatusFree.CURRENT_STATUS;

	
	public StatusM2Sent(){
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
		this.commandFactory = new CommandFactory();
	}
	
	
	@Override
	public ParsableSMS parse(Context context, SmsMessage message, String other) throws IllegalStateException, InvalidCipherTextException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException, NotECKeyException, NoSuchProviderException {
		TimeoutWrapper.removeTimeout(SMSUtility.MY_PHONE, other, context);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM2Sent.STATUS_RECEIVED, context);
					
		this.parser = popolateParser(message, context, other);
		this.parser.decrypt(); //decritta, verifica firma e password e mette in "plaintext" il codice
		this.recMsg = this.commandFactory.getMessage(SMSUtility.bytesToHex(this.parser.getPlaintext()));
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m3 received and parsed");	
		return this.recMsg;	
	}
	
	private M3Parser popolateParser(SmsMessage sms, Context ctx, String other) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		byte[] encryptedMessage = sms.getUserData();
		
		String decKey = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, other, ctx);
		byte[] decKeyValue = Base64.decode(decKey, Base64.DEFAULT);
		Key decryptionKey = new SecretKeySpec(decKeyValue, CryptoUtility.AES_256);
		
		String otherPub = MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, ctx);
		byte[] otherPubValue = Base64.decode(otherPub, Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		PublicKey oPub = keyFactory.generatePublic(new X509EncodedKeySpec(otherPubValue));
		
		String storedHash = MyPrefFiles.getMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_HASH, ctx);
		byte[] storedPasswordHash = Base64.decode(storedHash, Base64.DEFAULT);
		
		String iv = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV, ctx);
		byte[] ivValue = Base64.decode(iv, Base64.DEFAULT);
		
		return new M3Parser(encryptedMessage, decryptionKey, oPub, storedPasswordHash, ivValue);
	}

	@Override
	public String getCurrentStatus() {
		return StatusM2Sent.CURRENT_STATUS;
	}
	
	@Override
	public String getNextSentStatus() {
		return StatusM2Sent.NEXT_SENT_STATUS;
	}
}