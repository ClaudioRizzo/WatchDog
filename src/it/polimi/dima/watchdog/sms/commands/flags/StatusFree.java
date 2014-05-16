package it.polimi.dima.watchdog.sms.commands.flags;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Base64;
import it.polimi.dima.watchdog.UTILITIES.CryptoUtility;
import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.UTILITIES.SMSUtility;
import it.polimi.dima.watchdog.crypto.AESKeyGenerator;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.NoECDSAKeyPairGeneratedException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.timeout.Timeout;

/**
 * 
 * @author emanuele
 *
 */
//sono libero, ricevo m1, lo parso, mando m2 e passo a m2_sent
public class StatusFree implements CommandProtocolFlagsReactionInterface{
	
	private M1Parser parser;
	public static String CURRENT_STATUS = "free";
	private static String STATUS_RECEIVED = "m1_received";
	public static String NEXT_SENT_STATUS = StatusM2Sent.CURRENT_STATUS;//TODO da correggere
	
	@Override
	public ParsableSMS parse(Context context, SmsMessage message, String other) throws Exception {//voglio poterle catchare tutte
		Timeout.getInstance(context).removeTimeout(MyPrefFiles.getMyPreference(MyPrefFiles.MY_NUMBER_FILE, MyPrefFiles.MY_PHONE_NUMBER, context) /*TODO inizializzarlo nel wizard*/, other);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusFree.STATUS_RECEIVED, context);
		
		byte[] publicKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, context),Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance(CryptoUtility.EC);
		PublicKey oPub = kf.generatePublic(new X509EncodedKeySpec(publicKey));
		
		this.parser = new M1Parser(message.getUserData(), oPub);
		this.parser.parse();
		generateAndSaveAESKey(other, context);
		saveIV(other, context);
		generateAndSendM2(other, context);
		return null;
	}

	@Override
	public String getCurrentStatus() {
		return StatusFree.CURRENT_STATUS;
	}
	
	@Override
	public String getNextSentStatus() {
		return StatusFree.NEXT_SENT_STATUS;
	}
	
	private void generateAndSaveAESKey(String phoneNumber, Context ctx) throws NoSuchPreferenceFoundException, InvalidKeyException, NoSuchAlgorithmException{
		byte[] salt = this.parser.getSalt();
		byte[] sharedSecret = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, ctx),Base64.DEFAULT);
		AESKeyGenerator generator = new AESKeyGenerator(sharedSecret, salt);
		String aesKey = Base64.encodeToString(generator.generateKey().getEncoded(), Base64.DEFAULT);
		String identifier = phoneNumber + MyPrefFiles.SESSION_KEY;
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, identifier, aesKey, ctx);
	}
	
	private void saveIV(String phoneNumber, Context ctx){
		String identifier = phoneNumber + MyPrefFiles.IV;
		String ivBase64 = Base64.encodeToString(this.parser.getIV(), Base64.DEFAULT);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, identifier, ivBase64, ctx);
	}
	
	private void generateAndSendM2(String phoneNumber, Context ctx) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoECDSAKeyPairGeneratedException, NoSignatureDoneException{
		byte[] header = SMSUtility.M2_HEADER.getBytes();
		byte[] myPrivateKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, ctx), Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance(CryptoUtility.EC);
		PrivateKey mPriv = kf.generatePrivate(new X509EncodedKeySpec(myPrivateKey));
		ECDSA_Signature signer = new ECDSA_Signature(header, mPriv);
		signer.sign();
		byte[] body = signer.getSignature();
		SMSUtility.sendMessage(phoneNumber, SMSUtility.COMMAND_PORT, header, body);
	}
	
	
}
