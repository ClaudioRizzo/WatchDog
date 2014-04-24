package it.polimi.dima.watchdog.sms.socialistMillionare;

import android.telephony.SmsManager;

/**
 * A protocol message should extends this class
 * @author claudio
 *
 */
public class SMSProtocol implements SocialistMillionareMessageInterface {

	private byte[] header;
	private byte[] body;
	private SmsManager smsMan;
	public static int HEADER_LENGTH = 4; //ovvero 4 byte: NON si tiene conto del terminatore nullo.
	
	
	public SMSProtocol(byte[] header, byte[] body) {
		this.header = header;
		this.body = body;
		this.smsMan = SmsManager.getDefault();
	}
	
	public byte[] getHeader() {
		return header;
	}
	public void setHeader(byte[] header) {
		this.header = header;
	}
	public byte[] getBody() {
		return body;
	}
	public void setBody(byte[] body) {
		this.body = body;
	}

	public SmsManager getSmsMan() {
		return smsMan;
	}

	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		/**/
		
	}

	
}
