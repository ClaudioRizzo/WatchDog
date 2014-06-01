package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.security.PublicKey;
import java.util.Arrays;
import android.util.Base64;
import android.util.Log;

/**
 * 
 * @author emanuele
 *
 */
public class M2Parser {

	private byte[] header;
	private byte[] salt;
	private byte[] iv;
	private byte[] signature;
	private byte[] rawMessage;
	public static final int SALT_LENGTH = 32;
	public static final int IV_LENGTH = 12;
	private PublicKey oPub;
	
	public M2Parser(byte[] rawMessage, PublicKey oPub){
		this.rawMessage = rawMessage;
		this.header = new byte[ParsableSMS.HEADER_LENGTH];
		this.salt = new byte[M1Parser.SALT_LENGTH];
		this.iv = new byte[M1Parser.IV_LENGTH];
		this.oPub = oPub;
	}
	
	public void parse() throws ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NotECKeyException{
		int ivStartPosition = ParsableSMS.HEADER_LENGTH;
		Log.i("[DEBUG]", "[DEBUG] iv start position = " + ivStartPosition);
		int saltStartPosition = ivStartPosition + M2Parser.IV_LENGTH;
		Log.i("[DEBUG]", "[DEBUG] salt start position = " + saltStartPosition);
		int signatureStartPosition = saltStartPosition + M2Parser.SALT_LENGTH;
		Log.i("[DEBUG]", "[DEBUG] signature start position = " + signatureStartPosition);
		int messageWithoutSignatureLength = ParsableSMS.HEADER_LENGTH + M2Parser.IV_LENGTH + M2Parser.SALT_LENGTH;
		Log.i("[DEBUG]", "message without signature length = " + messageWithoutSignatureLength);
		int signatureLength = this.rawMessage.length - messageWithoutSignatureLength;
		Log.i("[DEBUG]", "[DEBUG] signature length = " + signatureLength);
			
		if(signatureLength < 1){
			throw new ArbitraryMessageReceivedException();
		}
		separateMessageParts(ivStartPosition, saltStartPosition, signatureStartPosition, signatureLength);
		verifySignature(messageWithoutSignatureLength);
		verifyHeader(SMSUtility.hexStringToByteArray(SMSUtility.M2_HEADER));
	}

	private void verifyHeader(byte[] header) throws ArbitraryMessageReceivedException {
		if(!Arrays.equals(this.header, header)){
			throw new ArbitraryMessageReceivedException();
		}
	}

	private void verifySignature(int messageWithoutSignatureLength) throws ErrorInSignatureCheckingException, ArbitraryMessageReceivedException, NotECKeyException {
		byte[] messageWithoutSignature = new byte[messageWithoutSignatureLength];
		System.arraycopy(this.rawMessage, 0, messageWithoutSignature, 0, messageWithoutSignatureLength);
		Log.i("[DEBUG]", "[DEBUG] messaggio senza firma: " + Base64.encodeToString(messageWithoutSignature, Base64.DEFAULT));
		Log.i("[DEBUG]", "[DEBUG] lunghezza della firma: " + this.signature.length);
		Log.i("[DEBUG]", "[DEBUG] firma: " + Base64.encodeToString(this.signature, Base64.DEFAULT));
		ECDSA_Signature verifier = new ECDSA_Signature(messageWithoutSignature, this.oPub, this.signature);
		if(!verifier.verifySignature()){
			throw new ArbitraryMessageReceivedException("Firma non valida/non corrispondente!!!");
		}
	}

	private void separateMessageParts(int ivStartPosition, int saltStartPosition, int signatureStartPosition, int signatureLength) {
		this.signature = new byte[signatureLength];
		System.arraycopy(this.rawMessage, 0, this.header, 0, ParsableSMS.HEADER_LENGTH);
		System.arraycopy(this.rawMessage, ivStartPosition, this.iv, 0, M2Parser.IV_LENGTH);
		System.arraycopy(this.rawMessage, saltStartPosition, this.salt, 0, M2Parser.SALT_LENGTH);
		System.arraycopy(this.rawMessage, signatureStartPosition, this.signature, 0, signatureLength);
	}
}