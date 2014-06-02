package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.crypto.AES256GCM;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
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
	private byte[] header;
	private byte[] body; //corpo del messaggio
	
	
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
		decrypt();
		int headerLength = ParsableSMS.HEADER_LENGTH;
		int bodyLength = SMSUtility.M4_BODY_LENGTH;
		int signatureLength = this.decryptedSMS.length - headerLength - bodyLength;
		separateMessageParts(headerLength, bodyLength, signatureLength);
		verifySignature();
		verifyHeader(SMSUtility.hexStringToByteArray(SMSUtility.M4_HEADER));
	}

	private void verifySignature() throws NotECKeyException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException {
		ECDSA_Signature ver = new ECDSA_Signature(this.messageWithoutSignature, this.oPub, this.signature);
		if(!ver.verifySignature()){
			throw new ArbitraryMessageReceivedException();
		}
	}
	
	private void verifyHeader(byte[] header) throws ArbitraryMessageReceivedException{
		if(!Arrays.equals(this.header, header)){
			throw new ArbitraryMessageReceivedException();
		}
	}

	private void separateMessageParts(int headerLength, int bodyLength, int signatureLength) {
		this.header = new byte[headerLength];
		this.body = new byte[bodyLength];
		this.messageWithoutSignature = new byte[headerLength + bodyLength];
		this.signature = new byte[signatureLength];
		System.arraycopy(this.decryptedSMS, 0, this.header, 0, headerLength);
		System.arraycopy(this.decryptedSMS, headerLength, this.body, 0, bodyLength);
		System.arraycopy(this.decryptedSMS, 0, this.messageWithoutSignature, 0, headerLength + bodyLength);
		System.arraycopy(this.decryptedSMS, headerLength + bodyLength, this.signature, 0, signatureLength);
	}

	private void decrypt() throws IllegalStateException, InvalidCipherTextException {
		AES256GCM dec = new AES256GCM(this.decryptionKey, this.smsEncrypted, this.iv);
		this.decryptedSMS = dec.decrypt();
	}
}