package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.sms.ParsableSMS;

import java.security.PublicKey;

public class M4Parser {
	private byte[] rawMessage;
	private byte[] header;
	private byte[] data;
	private byte[] signature;
	private PublicKey oPub;
	
	public M4Parser(byte[] rawMessage, PublicKey oPub){
		this.rawMessage = rawMessage;
		this.oPub = oPub;
		this.header = new byte[ParsableSMS.HEADER_LENGTH];
	}
	
	public void parse(){
		/* TODO rawMessage = header || data || ' ' || firma
		 * scompattare il messaggio
		 * verificare la firma
		 * verificare l'header
		 */
	}
}
