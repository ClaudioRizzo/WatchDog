package it.polimi.dima.watchdog.sms.commands.flags;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import it.polimi.dima.watchdog.UTILITIES.CryptoUtility;
import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.timeout.Timeout;
import android.content.Context;
import android.telephony.SmsMessage;
import android.util.Base64;

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

	@Override
	public ParsableSMS parse(Context context, SmsMessage message, String other) throws Exception {
		Timeout.getInstance(context).removeTimeout(MyPrefFiles.getMyPreference(MyPrefFiles.MY_NUMBER_FILE, MyPrefFiles.MY_PHONE_NUMBER, context) /*TODO inizializzarlo nel wizard*/, other);
		MyPrefFiles.replacePreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusM3Sent.STATUS_RECEIVED, context);
		
		byte[] publicKey = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, context),Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance(CryptoUtility.EC);
		PublicKey oPub = kf.generatePublic(new X509EncodedKeySpec(publicKey));
		
		this.parser = new M4Parser(message.getUserData(), oPub);
		this.parser.parse();
		//TODO fare qualcosa con il body del messaggio
		return null;
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