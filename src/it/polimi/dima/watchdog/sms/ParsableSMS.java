package it.polimi.dima.watchdog.sms;

import android.content.Context;
import it.polimi.dima.watchdog.sms.commands.CommandMessageInterface;
import it.polimi.dima.watchdog.sms.commands.SMSCommandVisitorInterface;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSPublicKeyVisitorInterface;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SocialistMillionaireMessageInterface;

/**
 * A protocol message should extends this class
 * @author claudio, emanuele
 *
 */
public class ParsableSMS implements SocialistMillionaireMessageInterface, CommandMessageInterface {

	private String header; //MUST be base64
	private String body; //MUST be Base64
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
	public void handle(SMSCommandVisitorInterface visitor) {}

	
}
