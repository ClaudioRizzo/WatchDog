package it.polimi.dima.watchdog.sms.commands;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

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

	public LocateCodeMessage(String header, String body) {
		super(header, body);
	}
	
	public String getBody(){
		return super.getBody();
	}
	
	public List<Double> extractSubBody(String body){
		byte[] fullBody = Base64.decode(body, Base64.DEFAULT); //coordinate + padding
		return convertSubBody(fullBody);
	}
	
	private List<Double> convertSubBody(byte[] subBody) {
		String body = new String(subBody);
		if(!body.matches(".+" + "i" + ".+" + "e" + ".*") || subBody.length != 30){
			throw new IllegalArgumentException("Il body non è ciò che mi aspetto!!!");
		}
		String temp = body;
		char[] tempArray = temp.toCharArray();
		
		Double latitude = getLatitude(tempArray, temp);
		Double longitude = getLongitude(tempArray, temp);
		
		List<Double> coordinates = new ArrayList<Double>();
		coordinates.add(latitude);
		coordinates.add(longitude);
		return coordinates;
	}

	private Double getLongitude(char[] tempArray, String temp) {
		int dollarIndex = -1;
		int sharpIndex = -1;
		for(int i=dollarIndex+1; i<tempArray.length; i++){
			if(tempArray[i]=='i'){
				dollarIndex = i;
			}
			else if(tempArray[i] == 'e'){
				sharpIndex = i;
			}
		}
		if(dollarIndex == -1 || sharpIndex == -1){
			throw new IllegalArgumentException();
		}
		String longitudeString = temp.substring(dollarIndex + 1, sharpIndex - 1);
		Log.i("DEBUG", "DEBUG longitude string = " + longitudeString);
		return new Double(longitudeString);
	}

	private Double getLatitude(char[] tempArray, String temp) {
		int dollarIndex = -1;
		for(int i=0; i<tempArray.length; i++){
			if(tempArray[i]=='i'){
				dollarIndex = i;
			}
		}
		if(dollarIndex == -1){
			throw new IllegalArgumentException();
		}
		String latitudeString = temp.substring(0, dollarIndex - 1);
		Log.i("DEBUG", "DEBUG latitude string = " + latitudeString);
		return new Double(latitudeString);
	}

	@Override
	public void handle(SMSCommandVisitorInterface visitor) throws IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException {
		visitor.visit(this);
	}
}
