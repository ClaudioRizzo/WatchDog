package it.polimi.dima.watchdog.sms.commands;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.spongycastle.crypto.InvalidCipherTextException;

import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import android.content.Context;

/**
 * 
 * @author emanuele
 *
 */
public interface CommandMessageInterface {
	public void handle(SMSCommandVisitorInterface visitor) throws IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException, IllegalStateException, InvalidCipherTextException;
	public  void validate(String otherNumber, Context ctx) throws Exception;
}
