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
public class SirenOnCodeMessage extends ParsableSMS {

	private String message;
	
	public String getMessage(){
		return this.message;
	}
	
	public SirenOnCodeMessage(String header, String body) {
		super(header, body);
	}
	
	@Override
	public void handle(SMSCommandVisitorInterface visitor) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalStateException, InvalidCipherTextException, IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException {
		visitor.visit(this);
	}
	
	public void extractSubBody(String body){
		byte[] fullBody = Base64.decode(body, Base64.DEFAULT); //messaggio + padding
		String fullBodyMessage = new String(fullBody);
		Log.i("DEBUG", "DEBUG: full body = " + fullBodyMessage);
		getMessage(fullBodyMessage);
	}

	private void getMessage(String fullBodyMessage) {
		if(fullBodyMessage.matches(SMSUtility.SIREN_ON_RESPONSE_OK + ".*")){
			this.message = fullBodyMessage.substring(0, SMSUtility.SIREN_ON_RESPONSE_OK.length() - 1);
		}
		else if(fullBodyMessage.matches(SMSUtility.SIREN_ON_RESPONSE_KO + ".*")){
			this.message = fullBodyMessage.substring(0, SMSUtility.SIREN_ON_RESPONSE_KO.length() - 1);
		}
		else{
			throw new IllegalArgumentException("Il body non è ciò che mi aspetto!!!");
		}
	}
}