package it.polimi.dima.watchdog.sms.commands.flags;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import org.spongycastle.crypto.InvalidCipherTextException;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Log;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.commands.CommandFactory;
import it.polimi.dima.watchdog.sms.timeout.TimeoutWrapper;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;

/**
 * 
 * @author emanuele
 *
 */
//Ho mandato m2, mi aspetto m3, lo parso e lo ritorno
public class StatusM2Sent implements CommandProtocolFlagsReactionInterface {

	private M3Parser parser;
	public static String CURRENT_STATUS = "m2_sent";
	private static String STATUS_RECEIVED = "m3_received";
	public static String NEXT_SENT_STATUS = StatusFree.CURRENT_STATUS;

	
	public StatusM2Sent(){
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}
	
	
	@Override
	public void parse(Context context, SmsMessage message, String other) throws IllegalStateException, InvalidCipherTextException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException, NotECKeyException, NoSuchProviderException, IllegalArgumentException, TooLongResponseException, NoSignatureDoneException {
		TimeoutWrapper.removeTimeout(SMSUtility.MY_PHONE, other, context);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM2Sent.STATUS_RECEIVED, context);
					
		this.parser = popolateParser(message, context, other);
		this.parser.decrypt(); //decritta, verifica firma e password e mette in "plaintext" il codice
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m3 received and parsed");
		handleReturnedData(other, context);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM2Sent.NEXT_SENT_STATUS, context);
	}
	
	private void handleReturnedData(String other, Context context) throws ArbitraryMessageReceivedException, IllegalArgumentException, TooLongResponseException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException, IllegalStateException, InvalidCipherTextException {
		CommandFactory factory = new CommandFactory();
		SMSM3Handler handler = new SMSM3Handler(other, context);
		ParsableSMS smsToParse = factory.getMessage(SMSUtility.bytesToHex(this.parser.getPlaintext()), null);
		smsToParse.handle(handler);
	}
	
	private M3Parser popolateParser(SmsMessage sms, Context context, String other) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchProviderException {
		byte[] encryptedMessage = sms.getUserData();
		Key decryptionKey = MyPrefFiles.getSymmetricCryptoKey(context, other, true);
		PublicKey oPub = MyPrefFiles.getOtherPublicKey(context, other);
		byte[] storedPasswordHash = MyPrefFiles.getMyPasswordHash(context);
		byte[] iv = MyPrefFiles.getIV(context, other, true);
		return new M3Parser(encryptedMessage, decryptionKey, oPub, storedPasswordHash, iv);
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