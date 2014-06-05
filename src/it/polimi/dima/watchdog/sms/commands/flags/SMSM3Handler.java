package it.polimi.dima.watchdog.sms.commands.flags;

import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import it.polimi.dima.watchdog.crypto.ECDSA_Signature;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import it.polimi.dima.watchdog.sms.commands.LocateCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkFoundCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkLostCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkLostOrStolenCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkStolenCodeMessage;
import it.polimi.dima.watchdog.sms.commands.SMSCommandVisitorInterface;
import it.polimi.dima.watchdog.sms.commands.SirenOffCodeMessage;
import it.polimi.dima.watchdog.sms.commands.SirenOnCodeMessage;
import it.polimi.dima.watchdog.utilities.CryptoUtility;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;

public class SMSM3Handler implements SMSCommandVisitorInterface {
<<<<<<< HEAD
=======

>>>>>>> b742b3ad4ad4c5e13c682dc45dac0286092f2e92

	private String other;
	private Context ctx;
	
	public SMSM3Handler(String other, Context context){
		this.other = other;
		this.ctx = context;
	}
	
	@Override
	public void visit(SirenOnCodeMessage sirenOnCodeMessage) {
		// TODO fare quello che serve e creare m4 cosi' fatto:
		// aes256gcm(key fetched from pref (number + KEY_FOR_M4), iv fetched (number + IV_FOR_M4), m4header + specific command header + data + signature)
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN ON RECEIVED");
		
	}

	@Override
	public void visit(SirenOffCodeMessage sirenOffCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN OFF RECEIVED");
	}

	@Override
	public void visit(MarkLostCodeMessage markLostCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK LOST RECEIVED");
	}

	@Override
	public void visit(MarkStolenCodeMessage markStolenCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK STOLEN RECEIVED");
	}

	@Override
	public void visit(MarkLostOrStolenCodeMessage markLostOrStolenCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK LOST OR STOLEN RECEIVED");
	}

	@Override
	public void visit(MarkFoundCodeMessage markFoundCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK FOUND RICEVUTO");
	}

	@Override
	public void visit(LocateCodeMessage locateCodeMessage) throws IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] LOCATE RICEVUTO");
		String location = null; //TODO inizializzare coi dati del gps
		constructLocateResponse(SMSUtility.hexStringToByteArray(SMSUtility.LOCATE), location);
	}
	
	private void constructLocateResponse(byte[] specificHeader, String location) throws TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException{
		
		//creo l'header (prima parte, poi c'è anche specificHeader)
		byte[] header = SMSUtility.hexStringToByteArray(SMSUtility.M4_HEADER);
		
		//alloco spazio per il body
		byte[] body = new byte[SMSUtility.M4_BODY_LENGTH];
		
		//creo il sottobody
		byte[] locationBytes = location.getBytes();
		
		//lunghezza dei byte che indicano la lunghezza del sottobody
		final int lengthBytesSize = 4;
		
		//lunghezza del sottobody
		int locationLength = locationBytes.length;
		
		//lunghezza del padding
		int paddingLength = SMSUtility.getM4BodyPaddingLength(SMSUtility.M4_BODY_LENGTH, lengthBytesSize, locationLength);
		
		//creo i 4 byte che simbloeggiano la lunghezza del sottobody
		byte[] lengthByte = ByteBuffer.allocate(lengthBytesSize).putInt(locationLength).array();
		
		//creo il padding e lo inizializzo a tutti zeri
		byte[] padding = ByteBuffer.allocate(paddingLength).array();
		for(int i=0; i<paddingLength; i++){
			padding[i] = '\0';
		}
		
		//creo il body
		System.arraycopy(lengthByte, 0, body, 0, lengthBytesSize);
		System.arraycopy(locationBytes, 0, body, lengthBytesSize, locationLength);
		System.arraycopy(padding, 0, body, lengthBytesSize + locationLength, paddingLength);
		
		//creo il messaggio senza firma
		byte[] messageWithoutSignature = new byte[header.length + specificHeader.length + body.length];
		System.arraycopy(header, 0, messageWithoutSignature, 0, header.length);
		System.arraycopy(specificHeader, 0, messageWithoutSignature, header.length, specificHeader.length);
		System.arraycopy(body, 0, messageWithoutSignature, header.length + specificHeader.length, body.length);
		
		String mPrivBase64 = MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, this.ctx);
		byte[] mPrivEncoded = Base64.decode(mPrivBase64, Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC);
		PrivateKey mPriv = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(mPrivEncoded));
		
		ECDSA_Signature signer = new ECDSA_Signature(messageWithoutSignature, mPriv);
		signer.sign();
		byte[] signature = signer.getSignature();
		
		byte[] message = new byte[messageWithoutSignature.length + signature.length];
		System.arraycopy(messageWithoutSignature, 0, message, 0, messageWithoutSignature.length);
		System.arraycopy(signature, 0, message, messageWithoutSignature.length, signature.length);
	}
}
