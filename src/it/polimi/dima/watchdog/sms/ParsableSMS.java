package it.polimi.dima.watchdog.sms;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.spongycastle.crypto.InvalidCipherTextException;

import android.content.Context;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import it.polimi.dima.watchdog.sms.commands.CommandMessageInterface;
import it.polimi.dima.watchdog.sms.commands.SMSCommandVisitorInterface;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSPublicKeyVisitorInterface;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SocialistMillionaireMessageInterface;

/**
 * A protocol message should extends this class.
 * 
 * @author claudio, emanuele
 *
 */
public class ParsableSMS implements SocialistMillionaireMessageInterface, CommandMessageInterface {

	private String header;
	private String body;
	public static int HEADER_LENGTH = 4; //ovvero 4 byte
	
	
	public ParsableSMS(String header, String body) {
		this.header = header;
		this.body = body;
	}
	
	public String getHeader() {
		return header;
	}
	public void setHeader(String header) {
		this.header = header;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	
	@Override
	public void validate(String otherNumber, Context ctx) throws Exception {}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {}

	@Override
	public void handle(SMSCommandVisitorInterface visitor) throws IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException, IllegalStateException, InvalidCipherTextException {}

	
}
