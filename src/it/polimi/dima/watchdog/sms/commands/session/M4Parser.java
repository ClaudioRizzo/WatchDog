package it.polimi.dima.watchdog.sms.commands.session;

import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.utilities.SMSUtility;

import java.security.Key;
import java.security.PublicKey;
import java.util.Arrays;

import org.spongycastle.crypto.InvalidCipherTextException;

/**
 * 
 * @author emanuele
 *
 */
public class M4Parser {
	private byte[] smsEncrypted; //sms crittato
	private Key decryptionKey; //chiave dell'AES
	private byte[] iv;
	private PublicKey oPub; //chiave pubblica del mittente, usata per verificare la firma
	private byte[] decryptedSMS;
	private byte[] messageWithoutSignature;
	private byte[] signature; //firma scorporata dal messaggio
	private byte[] header; // header di m4
	private byte[] specificHeader; //header specifico
	private byte[] body; //corpo del messaggio = real body || padding
	
	public byte[] getSpecificHeader(){
		return this.specificHeader;
	}
	
	public byte[] getBody(){
		return this.body;
	}
	
	public M4Parser(byte[] rawMessage, PublicKey oPub, Key decryptionKey, byte[] iv){
		this.smsEncrypted = rawMessage;
		this.oPub = oPub;
		this.decryptionKey = decryptionKey;
		this.iv = iv;
	}
	
	public void parse() throws IllegalArgumentException, ArbitraryMessageReceivedException, NotECKeyException, ErrorInSignatureCheckingException, IllegalStateException, InvalidCipherTextException{
		this.decryptedSMS = CryptoUtility.doEncryptionOrDecryption(this.smsEncrypted, this.decryptionKey, this.iv, CryptoUtility.DEC);
		int headerLength = ParsableSMS.HEADER_LENGTH;
		int specificHeaderLength = ParsableSMS.HEADER_LENGTH;
		int bodyLength = SMSUtility.M4_BODY_LENGTH;
		int signatureLength = this.decryptedSMS.length - headerLength - specificHeaderLength - bodyLength;
		separateMessageParts(headerLength, specificHeaderLength, bodyLength, signatureLength);
		verifySignature();
		verifyHeader(SMSUtility.hexStringToByteArray(SMSUtility.M4_HEADER));
		verifySubHeader();
	}

	private void verifySignature() throws NotECKeyException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException {
		if(!CryptoUtility.verifySignature(this.messageWithoutSignature, this.signature, this.oPub)){
			throw new ArbitraryMessageReceivedException(ErrorFactory.SIGNATURE_EXCEPTION);
		}
	}
	
	private void verifyHeader(byte[] header) throws ArbitraryMessageReceivedException{
		if(!Arrays.equals(this.header, header)){
			throw new ArbitraryMessageReceivedException(ErrorFactory.INVALID_HEADER);
		}
	}
	
	private void verifySubHeader() throws ArbitraryMessageReceivedException{
		String header = SMSUtility.bytesToHex(this.specificHeader);
		if(!header.equals(SMSUtility.SIREN_ON)){
			if(!header.equals(SMSUtility.SIREN_OFF)){
				if(!header.equals(SMSUtility.MARK_LOST)){
					if(!header.equals(SMSUtility.MARK_STOLEN)){
						if(!header.equals(SMSUtility.MARK_LOST_OR_STOLEN)){
							if(!header.equals(SMSUtility.MARK_FOUND)){
								if(!header.equals(SMSUtility.LOCATE)){
									throw new ArbitraryMessageReceivedException(ErrorFactory.INVALID_HEADER);
								}
							}
						}
					}
				}
			}
		}
	}

	private void separateMessageParts(int headerLength, int specificHeaderLength, int bodyLength, int signatureLength) {
		this.header = new byte[headerLength];
		this.specificHeader = new byte[specificHeaderLength];
		this.body = new byte[bodyLength];
		this.messageWithoutSignature = new byte[headerLength + specificHeaderLength + bodyLength];
		this.signature = new byte[signatureLength];
		System.arraycopy(this.decryptedSMS, 0, this.header, 0, headerLength);
		System.arraycopy(this.decryptedSMS, headerLength, this.specificHeader, 0, specificHeaderLength);
		System.arraycopy(this.decryptedSMS, headerLength + specificHeaderLength, this.body, 0, bodyLength);
		System.arraycopy(this.decryptedSMS, 0, this.messageWithoutSignature, 0, headerLength + specificHeaderLength + bodyLength);
		System.arraycopy(this.decryptedSMS, headerLength + specificHeaderLength + bodyLength, this.signature, 0, signatureLength);
	}
}