package it.polimi.dima.watchdog.sms.commands.flags;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import it.polimi.dima.watchdog.crypto.AESKeyGenerator;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.timeout.TimeoutWrapper;
import it.polimi.dima.watchdog.utilities.CryptoUtility;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;

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
	
	
	public StatusFree(){
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
	}
	
	
	@Override
	public ParsableSMS parse(Context context, SmsMessage message, String other) throws NoSuchPreferenceFoundException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NotECKeyException, InvalidKeyException, NoSignatureDoneException  {//voglio poterle catchare tutte
		//TimeoutWrapper.removeTimeout(SMSUtility.MY_PHONE, other, context);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusFree.STATUS_RECEIVED, context);
		
		byte[] publicKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, context),Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		PublicKey oPub = keyFactory.generatePublic(new X509EncodedKeySpec(publicKey));
		
		//cancellare dopo che funziona
		byte[] mess = message.getUserData();
		Log.i("[DEBUG]", "[DEBUG] la chiave pubblica dell'altro: " + MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, context));
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] lunghezza del messaggio ricevuto: " + mess.length);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] messaggio ricevuto: " + Base64.encodeToString(mess, Base64.DEFAULT));
		//fine roba da cancellare
		
		this.parser = new M1Parser(message.getUserData(), oPub);
		this.parser.parse();
		generateAndSaveAESKey(other, context);
		saveIV(other, context);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m1 received and parsed");
		generateAndSendM2(other, context);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] m2 sent");
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusFree.NEXT_SENT_STATUS, context);
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
	
	private void generateAndSendM2(String phoneNumber, Context ctx) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NotECKeyException, NoSignatureDoneException, NoSuchProviderException{
		byte[] header = SMSUtility.hexStringToByteArray(SMSUtility.M2_HEADER);
		byte[] myPrivateKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, ctx), Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC, CryptoUtility.SC);
		PrivateKey mPriv = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(myPrivateKey));
		ECDSA_Signature signer = new ECDSA_Signature(header, mPriv);
		signer.sign();
		byte[] body = signer.getSignature();
		SMSUtility.sendMessage(phoneNumber, SMSUtility.COMMAND_PORT, header, body);
	}	
}