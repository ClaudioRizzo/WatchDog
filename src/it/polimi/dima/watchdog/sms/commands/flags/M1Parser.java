package it.polimi.dima.watchdog.sms.commands.flags;

import java.security.PublicKey;

import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;

/**
 * 
 * @author emanuele
 *
 */
public class M1Parser {
	private byte[] header;
	private byte[] salt;
	private byte[] iv;
	private byte[] signature;
	private byte[] rawMessage;
	public static final int SALT_LENGTH = 32;
	public static final int IV_LENGTH = 12;
	private PublicKey oPub;
	
	public M1Parser(byte[] rawMessage, PublicKey oPub){
		this.rawMessage = rawMessage;
		this.header = new byte[SMSProtocol.HEADER_LENGTH];
		this.salt = new byte[M1Parser.SALT_LENGTH];
		this.iv = new byte[M1Parser.IV_LENGTH];
		this.oPub = oPub;
	}
	
	public byte[] getIV(){
		return this.iv;
	}
	
	public byte[] getSalt(){
		return this.salt;
	}
	
	public void parse() throws ArbitraryMessageReceivedException, ErrorInSignatureCheckingException {
		try{
			int ivStartPosition = SMSProtocol.HEADER_LENGTH;
			int saltStartPosition = ivStartPosition + M1Parser.IV_LENGTH;
			int signatureStartPosition = saltStartPosition + M1Parser.SALT_LENGTH;
			
			int messageWithoutSignatureLength = SMSProtocol.HEADER_LENGTH + M1Parser.IV_LENGTH + M1Parser.SALT_LENGTH;
			int signatureLength = this.rawMessage.length - messageWithoutSignatureLength;
			
			if(signatureLength < 1){
				throw new ArbitraryMessageReceivedException();
			}
			
			separateMessageParts(ivStartPosition, saltStartPosition, signatureStartPosition, signatureLength);
			verifySignature(messageWithoutSignatureLength);
			verifyHeader(SMSUtility.M1_HEADER.getBytes());
					}
		catch (ArrayIndexOutOfBoundsException e)
		{
			throw new ArbitraryMessageReceivedException();
		}
	}

	private void verifyHeader(byte[] header) throws ArbitraryMessageReceivedException {
		if(!this.header.equals(header)){
			throw new ArbitraryMessageReceivedException();
		}
	}

	private void verifySignature(int messageWithoutSignatureLength) throws ArbitraryMessageReceivedException, ErrorInSignatureCheckingException {
		byte[] messageWithoutSignature = new byte[messageWithoutSignatureLength];
		System.arraycopy(this.rawMessage, 0, messageWithoutSignature, 0, messageWithoutSignatureLength);
		
		ECDSA_Signature verifier = new ECDSA_Signature(messageWithoutSignature, this.oPub, this.signature);
		if(!verifier.verifySignature()){
			throw new ArbitraryMessageReceivedException("Firma non valida/non corrispondente!!!");
		}
	}

	private void separateMessageParts(int ivStartPosition, int saltStartPosition, int signatureStartPosition, int signatureLength) {
		this.signature = new byte[signatureLength];
		System.arraycopy(this.rawMessage, 0, this.header, 0, SMSProtocol.HEADER_LENGTH);
		System.arraycopy(this.rawMessage, ivStartPosition, this.iv, 0, M1Parser.IV_LENGTH);
		System.arraycopy(this.rawMessage, saltStartPosition, this.salt, 0, M1Parser.SALT_LENGTH);
		System.arraycopy(this.rawMessage, signatureStartPosition, this.signature, 0, signatureLength);
	}
}
