package it.polimi.dima.watchdog.sms.commands.flags;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import org.spongycastle.crypto.InvalidCipherTextException;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.commands.CommandFactory;
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
	public void parse(Context context, SmsMessage message, String other) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeySpecException, IllegalArgumentException, IllegalStateException, InvalidCipherTextException, ArbitraryMessageReceivedException, NotECKeyException, ErrorInSignatureCheckingException, TooLongResponseException, NoSignatureDoneException  {
		//TimeoutWrapper.removeTimeout(SMSUtility.MY_PHONE, other, context);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM3Sent.STATUS_RECEIVED, context);
		
		PublicKey oPub = MyPrefFiles.getOtherPublicKey(context, other);
		Key decryptionKey = MyPrefFiles.getSymmetricCryptoKey(context, other, false);
		byte[] iv = MyPrefFiles.getIV(context, other, false);
		
		this.parser = new M4Parser(message.getUserData(), oPub, decryptionKey, iv);
		this.parser.parse();
		byte[] header = this.parser.getSpecificHeader();
		byte[] body = this.parser.getBody();
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m4 received and parsed");
		handleReturnedData(header, body, other, context);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM3Sent.NEXT_SENT_STATUS, context);
	}
	
	private void handleReturnedData(byte[] header, byte[] body, String other, Context context) throws ArbitraryMessageReceivedException, IllegalArgumentException, TooLongResponseException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException {
		CommandFactory factory = new CommandFactory();
		String factoryHeader = SMSUtility.bytesToHex(header);
		String factorybody = Base64.encodeToString(body, Base64.DEFAULT);
		SMSM4Handler handler = new SMSM4Handler(other, context);
		ParsableSMS smsToParse = factory.getMessage(factoryHeader, factorybody);
		smsToParse.handle(handler);
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