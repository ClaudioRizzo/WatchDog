package it.polimi.dima.watchdog.sms.commands.flags;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.InvalidCipherTextException;

import it.polimi.dima.watchdog.UTILITIES.CryptoUtility;
import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.exceptions.NoECDSAKeyPairGeneratedException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.commands.CommandSMS;
import it.polimi.dima.watchdog.sms.timeout.Timeout;
import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Base64;

//ho mandato m1, mi aspetto m2, lo parso, invio m3 e passo a m3_sent
public class StatusM1Sent implements CommandProtocolFlagsReactionInterface {

	private M2Parser parser;
	public static String CURRENT_STATUS = "m1_sent";
	private static String STATUS_RECEIVED = "m2_received";
	public static String NEXT_SENT_STATUS = StatusM3Sent.CURRENT_STATUS;

	@Override
	public ParsableSMS parse(Context context, SmsMessage message, String other) throws Exception {
		Timeout.getInstance(context).removeTimeout(MyPrefFiles.getMyPreference(MyPrefFiles.MY_NUMBER_FILE, MyPrefFiles.MY_PHONE_NUMBER, context) /*TODO inizializzarlo nel wizard*/, other);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM1Sent.STATUS_RECEIVED, context);
		
		byte[] publicKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, context),Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance(CryptoUtility.EC);
		PublicKey oPub = kf.generatePublic(new X509EncodedKeySpec(publicKey));
		
		this.parser = new M2Parser(message.getUserData(), oPub);
		this.parser.parse();
		generateAndSendM3(other, context);
		return null;
	}

	private void generateAndSendM3(String phoneNumber, Context ctx) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, UnsupportedEncodingException, IllegalStateException, InvalidCipherTextException, NoECDSAKeyPairGeneratedException, NoSignatureDoneException {
		String key = phoneNumber + MyPrefFiles.TEMP_COMMAND;
		String key2 = phoneNumber + MyPrefFiles.OTHER_PASSWORD;
		String key3 = phoneNumber + MyPrefFiles.SESSION_KEY;
		String key4 = phoneNumber + MyPrefFiles.IV;
		
		String command = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, key, ctx);
		String password = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, key2, ctx);
		byte[] aesKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, key3, ctx), Base64.DEFAULT);
		Key encryptionKey = new SecretKeySpec(aesKey, CryptoUtility.AES_256);
		byte[] iv = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, key4, ctx), Base64.DEFAULT);
		
		byte[] myPriv = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, ctx), Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance(CryptoUtility.EC);
		PrivateKey mPriv = kf.generatePrivate(new X509EncodedKeySpec(myPriv));
		
		CommandSMS sms = new CommandSMS(command.getBytes(), password, mPriv, encryptionKey, phoneNumber, iv);
		sms.construct();
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
