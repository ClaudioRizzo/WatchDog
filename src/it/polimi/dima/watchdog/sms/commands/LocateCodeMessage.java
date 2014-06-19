package it.polimi.dima.watchdog.sms.commands;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.spongycastle.crypto.InvalidCipherTextException;

import android.util.Base64;
import android.util.Log;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.utilities.SMSUtility;

/**
 * 
 * @author emanuele
 *
 */
public class LocateCodeMessage extends ParsableSMS {

	private double latitude;
	private double longitude;
	private String errorCode = null;
	
	public LocateCodeMessage(String header, String body) {
		super(header, body);
	}
	
	public String getBody(){
		return super.getBody();
	}
	
	public double getlatitude(){
		return this.latitude;
	}
	
	public double getLongitude(){
		return this.longitude;
	}
	
	public String getErrorCode(){
		return this.errorCode;
	}
	
	public void extractSubBody(String body) {
		byte[] fullBody = Base64.decode(body, Base64.DEFAULT); //coordinate + padding
		convertSubBody(fullBody);
	}
	
	private void convertSubBody(byte[] subBody) {
		String temp = new String(subBody);
		
		if(subBody.length != SMSUtility.M4_BODY_LENGTH){
			throw new IllegalArgumentException("Il body non è ciò che mi aspetto!!!");
		}
		else if(temp.matches(SMSUtility.LOCATE_RESPONSE_KO + ".*")){
			this.errorCode = SMSUtility.LOCATE_RESPONSE_KO;
		}
		else if(!temp.matches(".+" + "i" + ".+" + "e" + ".*")){
			throw new IllegalArgumentException("Il body non è ciò che mi aspetto!!!");
		}
		else{
			char[] tempArray = temp.toCharArray();
			this.latitude = getLatitude(tempArray, temp).doubleValue();
			this.longitude = getLongitude(tempArray, temp).doubleValue();
		}
	}

	private Double getLongitude(char[] tempArray, String temp) {
		int separatorIndex = -1;
		int endIndex = -1;
		for(int i=separatorIndex+1; i<tempArray.length; i++){
			if(tempArray[i]=='i'){
				separatorIndex = i;
			}
			else if(tempArray[i] == 'e'){
				endIndex = i;
			}
		}
		if(separatorIndex == -1 || endIndex == -1){
			throw new IllegalArgumentException();
		}
		String longitudeString = temp.substring(separatorIndex + 1, endIndex - 1);
		Log.i("DEBUG", "DEBUG longitude string = " + longitudeString);
		return new Double(longitudeString);
	}

	private Double getLatitude(char[] tempArray, String temp) {
		int separatorIndex = -1;
		for(int i=0; i<tempArray.length; i++){
			if(tempArray[i]=='i'){
				separatorIndex = i;
			}
		}
		if(separatorIndex == -1){
			throw new IllegalArgumentException();
		}
		String latitudeString = temp.substring(0, separatorIndex - 1);
		Log.i("DEBUG", "DEBUG latitude string = " + latitudeString);
		return new Double(latitudeString);
	}

	@Override
	public void handle(SMSCommandVisitorInterface visitor) throws IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException, IllegalStateException, InvalidCipherTextException {
		visitor.visit(this);
	}
}