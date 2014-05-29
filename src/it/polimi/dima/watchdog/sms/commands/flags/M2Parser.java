package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.security.PublicKey;

/**
 * 
 * @author emanuele
 *
 */
public class M2Parser {

	private byte[] rawMessage;
	private byte[] header;
	private byte[] signature;
	private PublicKey oPub;
	
	public M2Parser(byte[] rawMessage, PublicKey oPub){
		this.rawMessage = rawMessage;
		this.oPub = oPub;
		this.header = new byte[ParsableSMS.HEADER_LENGTH];
	}
	
	public void parse() throws ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NotECKeyException{
		int signatureLength = this.rawMessage.length - this.header.length;
		int signatureStartPosition = this.header.length;
			
		if(signatureLength < 1){
			throw new ArbitraryMessageReceivedException();
		}
		separateMessageParts(signatureStartPosition, signatureLength);
		verifySignature();
		verifyHeader(SMSUtility.M2_HEADER.getBytes());
	}

	private void verifyHeader(byte[] header) throws ArbitraryMessageReceivedException {
		if(!this.header.equals(header)){
			throw new ArbitraryMessageReceivedException();
		}
	}

	private void verifySignature() throws ErrorInSignatureCheckingException, ArbitraryMessageReceivedException, NotECKeyException {
		ECDSA_Signature verifier = new ECDSA_Signature(this.header, this.oPub, this.signature);
		if(!verifier.verifySignature()){
			throw new ArbitraryMessageReceivedException("Firma non valida/non corrispondente!!!");
		}
	}

	private void separateMessageParts(int signatureStartPosition, int signatureLength) {
		this.signature = new byte[signatureLength];
		System.arraycopy(this.rawMessage, 0, this.header, 0, this.header.length);
		System.arraycopy(this.rawMessage, signatureStartPosition, this.signature, 0, signatureLength);
	}
}