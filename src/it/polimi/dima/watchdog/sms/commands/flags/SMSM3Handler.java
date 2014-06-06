package it.polimi.dima.watchdog.sms.commands.flags;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.spec.SecretKeySpec;

import org.spongycastle.crypto.InvalidCipherTextException;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Base64;
import android.util.Log;
import it.polimi.dima.watchdog.crypto.AES256GCM;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import it.polimi.dima.watchdog.fragments.gps.map.GpsTracker;
import it.polimi.dima.watchdog.fragments.gps.map.LocationChangeListenerInterface;
import it.polimi.dima.watchdog.fragments.gps.map.LocationException;
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

public class SMSM3Handler implements SMSCommandVisitorInterface, LocationChangeListenerInterface {

	private String other;
	private Context ctx;
	private String locationString = null;
	private GpsTracker gps;
	
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
		
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] LOCATE RICEVUTO");
		
		try {
			gps = new GpsTracker(ctx, (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE));
			gps.addListener(this);
			gps.getLocationUpdates();
			
			
		} catch (LocationException e) {
			// TODO gestire errore device spenti
			e.printStackTrace();
		}
		
		
		
	}
	
	private void constructResponse(byte[] specificHeader, String location) throws TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException, IllegalStateException, InvalidCipherTextException{
		byte[] header = SMSUtility.hexStringToByteArray(SMSUtility.M4_HEADER);
		byte[] subBody = location.getBytes();
		//byte[] subBody = "ciao".getBytes();
		int subBodyLength = subBody.length;
		int paddingLength = SMSUtility.getM4BodyPaddingLength(SMSUtility.M4_BODY_LENGTH, subBodyLength);
		
		byte[] padding = generatePadding(paddingLength);
		byte[] body = fillBody(subBody, padding, subBodyLength, paddingLength);
		byte[] messageWithoutSignature = generatePlaintext(header, specificHeader, body);
		
		PrivateKey mPriv = MyPrefFiles.getMyPrivateKey(this.ctx);
		byte[] signature = CryptoUtility.doSignature(messageWithoutSignature, mPriv);
		byte[] message = packMessage(messageWithoutSignature, signature);
		Key encKey = fetchEncryptionKey();
		byte[] iv = fetchIv();
		AES256GCM encryptor = new AES256GCM(encKey, message, iv, CryptoUtility.ENC);
		encryptor.encrypt();
		//Log.i("DEBUG", "DEBUG invio le a: "+"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes().length);
		//SMSUtility.sendCommandMessage(this.other, SMSUtility.COMMAND_PORT, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa".getBytes());
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] ciphertext = " + Base64.encodeToString(encryptor.getCiphertext(), Base64.DEFAULT));
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] ciphertext length = " + encryptor.getCiphertext().length);
		//SMSUtility.sendCommandMessage(this.other, SMSUtility.COMMAND_PORT, encryptor.getCiphertext());
		Log.i("gps","gps m4 inviato");
		MyPrefFiles.deleteUselessCommandSessionPreferencesAfterM4IsSent(this.other, this.ctx);
	}
	
	private byte[] fetchIv() throws NoSuchPreferenceFoundException {
		String iv = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, this.other + MyPrefFiles.IV_FOR_M4, this.ctx);
		return Base64.decode(iv, Base64.DEFAULT);
	}

	private Key fetchEncryptionKey() throws NoSuchPreferenceFoundException, NoSuchAlgorithmException {
		String key = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, this.other + MyPrefFiles.KEY_FOR_M4, this.ctx);
		Log.i("DEBUG", "DEBUG chiave appena prima di usarla per criptare: " + key);
		byte[] keyBytes = Base64.decode(key, Base64.DEFAULT);
		return new SecretKeySpec(keyBytes, CryptoUtility.AES_256);
	}

	private byte[] packMessage(byte[] messageWithoutSignature, byte[] signature) {
		byte[] message = new byte[messageWithoutSignature.length + signature.length];
		System.arraycopy(messageWithoutSignature, 0, message, 0, messageWithoutSignature.length);
		System.arraycopy(signature, 0, message, messageWithoutSignature.length, signature.length);
		return message;
	}

	private byte[] fillBody(byte[] subBody, byte[] padding, int subBodyLength, int paddingLength) {
		byte[] body = new byte[SMSUtility.M4_BODY_LENGTH];
		System.arraycopy(subBody, 0, body, 0, subBodyLength);
		System.arraycopy(padding, 0, body, subBodyLength, paddingLength);
		return body;
	}

	private byte[] generatePadding(int paddingLength){
		byte[] padding = ByteBuffer.allocate(paddingLength).array();
		for(int i=0; i<paddingLength; i++){
			padding[i] = '\0';
		}
		return padding;
	}
	
	private byte[] generatePlaintext(byte[] header, byte[] specificHeader, byte[] body){
		byte[] messageWithoutSignature = new byte[header.length + specificHeader.length + body.length];
		System.arraycopy(header, 0, messageWithoutSignature, 0, header.length);
		System.arraycopy(specificHeader, 0, messageWithoutSignature, header.length, specificHeader.length);
		System.arraycopy(body, 0, messageWithoutSignature, header.length + specificHeader.length, body.length);
		return messageWithoutSignature;
	}

	@Override
	public void onlocationChange(Location location) {
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		
		this.locationString = lat+"i"+lon+"e";
		System.out.println(this.locationString);
		System.out.println(this.locationString.length());
		gps.removeLocationUpdates();
		Log.i("[GPS]", "[GPS] posizione acquisita");
		Log.i("[GPS]", "[GPS] lat: "+lat+" lon: "+lon);
		try {
			constructResponse(SMSUtility.hexStringToByteArray(SMSUtility.LOCATE), this.locationString);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TooLongResponseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchPreferenceFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSignatureDoneException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NotECKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidCipherTextException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
