package it.polimi.dima.watchdog.sms.commands.flags;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;

import org.spongycastle.crypto.InvalidCipherTextException;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import it.polimi.dima.watchdog.fragments.gps.map.GpsLocalizer;
import it.polimi.dima.watchdog.fragments.gps.map.LocationChangeListenerInterface;
import it.polimi.dima.watchdog.fragments.gps.map.LocationException;
import it.polimi.dima.watchdog.siren.SirenService;
import it.polimi.dima.watchdog.sms.commands.LocateCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkFoundCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkLostCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkLostOrStolenCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkStolenCodeMessage;
import it.polimi.dima.watchdog.sms.commands.SMSCommandVisitorInterface;
import it.polimi.dima.watchdog.sms.commands.SirenOffCodeMessage;
import it.polimi.dima.watchdog.sms.commands.SirenOnCodeMessage;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;

/**
 * Classe che gestisce il comando arrivato con m3 e costruisce la risposta (m4)
 * 
 * @author emanuele
 *
 */
public class SMSM3Handler implements SMSCommandVisitorInterface, LocationChangeListenerInterface {

	private String other;
	private Context ctx;
	private String locationString = null;
	private GpsLocalizer gps;
	
	public SMSM3Handler(String other, Context context){
		this.other = other;
		this.ctx = context;
	}
	
	@Override
	public void visit(SirenOnCodeMessage sirenOnCodeMessage) {
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN ON RECEIVED");
		if(!SirenService.isMyServiceRunning(this.ctx)){
			Intent intent = new Intent(this.ctx,SirenService.class);
			intent.putExtra(SMSUtility.COMMAND, SMSUtility.SIREN_ON);
			this.ctx.startService(intent);
		}
	}

	@Override
	public void visit(SirenOffCodeMessage sirenOffCodeMessage) {
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN OFF RECEIVED");
		if(SirenService.isMyServiceRunning(this.ctx)){
			//TODO mandare comandi al service
		}
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
			this.gps = new GpsLocalizer(ctx, (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE));
			this.gps.addListener(this);
			this.gps.getLocationUpdates();
		} catch (LocationException e) {
			// TODO gestire errore device spenti
			e.printStackTrace();
		}
	}
	
	private void constructResponse(byte[] specificHeader, String location) throws TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException, IllegalStateException, InvalidCipherTextException{
		byte[] header = SMSUtility.hexStringToByteArray(SMSUtility.M4_HEADER);
		byte[] subBody = location.getBytes();
		int subBodyLength = subBody.length;
		int paddingLength = SMSUtility.getM4BodyPaddingLength(SMSUtility.M4_BODY_LENGTH, subBodyLength);
		
		byte[] padding = generatePadding(paddingLength);
		byte[] body = fillBody(subBody, padding, subBodyLength, paddingLength);
		byte[] messageWithoutSignature = generatePlaintext(header, specificHeader, body);
		PrivateKey mPriv = MyPrefFiles.getMyPrivateKey(this.ctx);
		byte[] signature = CryptoUtility.doSignature(messageWithoutSignature, mPriv);
		byte[] message = packMessage(messageWithoutSignature, signature);
		Key encKey = MyPrefFiles.getSymmetricCryptoKey(this.ctx, this.other, false);
		byte[] iv = MyPrefFiles.getIV(this.ctx, this.other, false);
		byte[] commandMessage = CryptoUtility.doEncryptionOrDecryption(message, encKey, iv, CryptoUtility.ENC);
		
		SMSUtility.sendCommandMessage(this.other, SMSUtility.COMMAND_PORT, commandMessage);
		MyPrefFiles.deleteUselessCommandSessionPreferencesForM4(this.other, this.ctx);
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
		Log.i("[DEBUG]", "[DEBUG] header length = " + header.length);
		Log.i("[DEBUG]", "[DEBUG] specificHeader length = " + specificHeader.length);
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
		this.gps.removeLocationUpdates();
		Log.i("[GPS]", "[GPS] posizione acquisita");
		Log.i("[GPS]", "[GPS] lat: "+lat+" lon: "+lon);
		try {
			constructResponse(SMSUtility.hexStringToByteArray(SMSUtility.LOCATE), this.locationString);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
