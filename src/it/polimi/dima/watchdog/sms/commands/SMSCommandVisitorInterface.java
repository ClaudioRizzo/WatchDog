package it.polimi.dima.watchdog.sms.commands;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import org.spongycastle.crypto.InvalidCipherTextException;

import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;

/**
 * 
 * @author emanuele
 *
 */
public interface SMSCommandVisitorInterface {
	public void visit(SirenOnCodeMessage sirenOnCodeMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalStateException, InvalidCipherTextException, IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException;
	public void visit(SirenOffCodeMessage sirenOffCodeMessage) throws NoSuchAlgorithmException, InvalidKeySpecException, IllegalStateException, InvalidCipherTextException, IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException;
	public void visit(MarkLostCodeMessage markLostCodeMessage);
	public void visit(MarkStolenCodeMessage markStolenCodeMessage);
	public void visit(MarkLostOrStolenCodeMessage markLostOrStolenCodeMessage);
	public void visit(MarkFoundCodeMessage markFoundCodeMessage);
	public void visit(LocateCodeMessage locateCodeMessage) throws IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException, IllegalStateException, InvalidCipherTextException;
}