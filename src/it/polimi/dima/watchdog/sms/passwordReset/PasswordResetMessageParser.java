package it.polimi.dima.watchdog.sms.passwordReset;

import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.password.PasswordUtils;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;

/**
 * Separa le parti del messaggio e verifica firma e header.
 * 
 * @author emanuele
 *
 */
public class PasswordResetMessageParser {

	private byte[] sms;
	private Context context;
	private String other;
	private byte[] header;
	private byte[] salt;
	private byte[] signature;
	private byte[] messageWithoutSignature;
	
	/**
	 * Crea l'oggetto a partire da sms, numero del mittente e contesto corrente.
	 * 
	 * @param sms : il messaggio ricevuto
	 * @param other : il numero del mittente
	 * @param context : il contesto corrente
	 */
	public PasswordResetMessageParser(byte[] sms, String other, Context context){
		this.sms = sms;
		this.context = context;
		this.other = other;
	}
	
	/**
	 * Ritorna il sale.
	 * 
	 * @return il sale.
	 */
	public byte[] getSalt(){
		return this.salt;
	}
	
	/**
	 * Verifica header e firma del messaggio dopo aver separato le sue parti.
	 * 
	 * @throws ArbitraryMessageReceivedException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 * @throws NoSuchPreferenceFoundException
	 * @throws ErrorInSignatureCheckingException
	 * @throws NotECKeyException
	 */
	public void parse() throws ArbitraryMessageReceivedException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException, ErrorInSignatureCheckingException, NotECKeyException{
		separateMessageParts();
		verifySignature();
		verifyHeader();
	}

	private void verifySignature() throws NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPreferenceFoundException, ErrorInSignatureCheckingException, NotECKeyException, ArbitraryMessageReceivedException {
		PublicKey oPub = MyPrefFiles.getOtherPublicKey(this.context, this.other);
		if(!CryptoUtility.verifySignature(this.messageWithoutSignature, this.signature, oPub)){
			throw new ArbitraryMessageReceivedException(ErrorFactory.SIGNATURE_EXCEPTION);
		}
	}

	private void verifyHeader() throws ArbitraryMessageReceivedException {
		byte[] header = SMSUtility.hexStringToByteArray(SMSUtility.PASSWORD_CHANGE);
		if(!Arrays.equals(this.header, header)){
			throw new ArbitraryMessageReceivedException(ErrorFactory.INVALID_HEADER);
		}
	}

	private void separateMessageParts() {
		this.header = new byte[ParsableSMS.HEADER_LENGTH];
		this.salt = new byte[PasswordUtils.SALT_LENGTH];
		int signatureLength = this.sms.length - ParsableSMS.HEADER_LENGTH - PasswordUtils.SALT_LENGTH;
		this.signature = new byte[signatureLength];
		this.messageWithoutSignature = new byte[this.sms.length - signatureLength];
		
		System.arraycopy(this.sms, 0, this.header, 0, this.header.length);
		System.arraycopy(this.sms, 0, this.salt, this.header.length, this.salt.length);
		System.arraycopy(this.sms, 0, this.signature, this.header.length + this.salt.length, this.signature.length);
		System.arraycopy(this.sms, 0, this.messageWithoutSignature, 0, this.messageWithoutSignature.length);
	}
}
