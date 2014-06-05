package it.polimi.dima.watchdog.sms.commands;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

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
	public void visit(SirenOnCodeMessage sirenOnCodeMessage);
	public void visit(SirenOffCodeMessage sirenOffCodeMessage);
	public void visit(MarkLostCodeMessage markLostCodeMessage);
	public void visit(MarkStolenCodeMessage markStolenCodeMessage);
	public void visit(MarkLostOrStolenCodeMessage markLostOrStolenCodeMessage);
	public void visit(MarkFoundCodeMessage markFoundCodeMessage);
	public void visit(LocateCodeMessage locateCodeMessage) throws IllegalArgumentException, TooLongResponseException, NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException, NoSignatureDoneException, NotECKeyException;
}
