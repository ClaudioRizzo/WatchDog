package it.polimi.dima.watchdog.sms.commands.flags;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.ErrorInSignatureCheckingException;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;

import org.spongycastle.crypto.InvalidCipherTextException;

import android.content.Context;
import android.telephony.SmsMessage;

/**
 * 
 * @author emanuele
 *
 */
public interface CommandProtocolFlagsReactionInterface {
	public abstract String getCurrentStatus();
	public abstract String getNextSentStatus();
	public void parse(Context context, SmsMessage message, String other) throws NoSuchPreferenceFoundException, InvalidKeySpecException, NoSuchAlgorithmException, NoSuchProviderException, ArbitraryMessageReceivedException, ErrorInSignatureCheckingException, NotECKeyException, InvalidKeyException, NoSignatureDoneException, UnsupportedEncodingException, IllegalStateException, InvalidCipherTextException, IllegalArgumentException, TooLongResponseException;
}
