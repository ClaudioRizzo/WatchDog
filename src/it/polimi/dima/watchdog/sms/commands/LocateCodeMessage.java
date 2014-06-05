package it.polimi.dima.watchdog.sms.commands;

import java.nio.ByteBuffer;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import android.util.Base64;
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
		int subBodyPosition = SMSUtility.M4_LENGTH_BYTES_SIZE;
		byte[] lengthBytes = new byte[SMSUtility.M4_LENGTH_BYTES_SIZE];
		byte[] fullBody = Base64.decode(body, Base64.DEFAULT);
		System.arraycopy(fullBody, 0, lengthBytes, 0, SMSUtility.M4_LENGTH_BYTES_SIZE);
		int subBodyLength = ByteBuffer.wrap(lengthBytes).getInt();
		byte[] subBody = new byte[subBodyLength];
		System.arraycopy(fullBody, subBodyPosition, subBody, 0, subBodyLength);
		return convertSubBody(subBody);
	}
	
	private List<Double> convertSubBody(byte[] subBody) {
		String body = new String(subBody);
		if(!body.matches("lat=" + ".+" + "lon=" + ".+" + "end")){
			throw new IllegalArgumentException("Il body non è ciò che mi aspetto!!!");
		}
		String temp = body.replace("lat=", "").replace("lon", "$").replace("end", "#");
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
			if(tempArray[i]=='$'){
				dollarIndex = i;
			}
			else if(tempArray[i] == '#'){
				sharpIndex = i;
			}
		}
		if(dollarIndex == -1 || sharpIndex == -1){
			throw new IllegalArgumentException();
		}
		String longitudeString = temp.substring(dollarIndex + 1, sharpIndex - 1);
		return new Double(longitudeString);
	}

	private Double getLatitude(char[] tempArray, String temp) {
		int dollarIndex = -1;
		for(int i=0; i<tempArray.length; i++){
			if(tempArray[i]=='$'){
				dollarIndex = i;
			}
		}
		if(dollarIndex == -1){
			throw new IllegalArgumentException();
		}
		String latitudeString = temp.substring(0, dollarIndex - 1);
		return new Double(latitudeString);
	}

	@Override
	public void handle(SMSCommandVisitorInterface visitor) throws IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException {
		visitor.visit(this);
	}
}
