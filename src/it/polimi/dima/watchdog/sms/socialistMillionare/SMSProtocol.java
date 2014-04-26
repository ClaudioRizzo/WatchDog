package it.polimi.dima.watchdog.sms.socialistMillionare;

import it.polimi.dima.watchdog.sms.commands.CommandMessageInterface;
import it.polimi.dima.watchdog.sms.commands.SMSCommandVisitorInterface;
import it.polimi.dima.watchdog.sms.ecdh.EllipticCurvesDiffieHellmanMessageInterface;
import it.polimi.dima.watchdog.sms.ecdh.SMSKeyExchangeVisitorInterface;
import android.telephony.SmsManager;

/**
 * A protocol message should extends this class
 * @author claudio, emanuele
 *
 */
public class SMSProtocol implements SocialistMillionareMessageInterface, EllipticCurvesDiffieHellmanMessageInterface, CommandMessageInterface {

	private String header; //MUST be base64
	private String body; //MUST be Base64
	private SmsManager smsMan;
	public static int HEADER_LENGTH = 4; //ovvero 4 byte: NON si tiene conto del terminatore nullo.
	
	
	public SMSProtocol(String header, String body) {
		this.header = header;
		this.body = body;
		this.smsMan = SmsManager.getDefault();
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

	public SmsManager getSmsMan() {
		return smsMan;
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		/**/
		
	}

	@Override
	public void handle(SMSKeyExchangeVisitorInterface visitor) {
		/**/
		
	}

	@Override
	public void handle(SMSCommandVisitorInterface visitor) {
		/**/
		
	}

	
}
