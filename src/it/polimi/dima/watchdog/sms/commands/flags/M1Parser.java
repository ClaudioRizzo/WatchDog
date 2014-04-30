package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;

public class M1Parser {
	private byte[] header;
	private byte[] salt;
	private byte[] iv;
	private byte[] signature;
	private byte[] rawMessage;
	public static final int SALT_LENGTH = 32;
	public static final int IV_LENGTH = 12;
	
	public M1Parser(byte[] rawMessage){
		this.rawMessage = rawMessage;
	}
	
	public void parse() throws ArbitraryMessageReceivedException {
		try{
			System.arraycopy(this.rawMessage, 0, this.header, 0, SMSProtocol.HEADER_LENGTH);
			System.arraycopy(this.rawMessage, SMSProtocol.HEADER_LENGTH, this.iv, 0, M1Parser.IV_LENGTH);
			System.arraycopy(this.rawMessage, SMSProtocol.HEADER_LENGTH + M1Parser.IV_LENGTH, this.salt, 0, M1Parser.SALT_LENGTH);
			System.arraycopy(this.rawMessage, SMSProtocol.HEADER_LENGTH + M1Parser.IV_LENGTH + M1Parser.SALT_LENGTH, this.signature, 0, this.rawMessage.length - (SMSProtocol.HEADER_LENGTH + M1Parser.IV_LENGTH + M1Parser.SALT_LENGTH));
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			
		}
	}
}
