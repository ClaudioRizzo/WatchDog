package it.polimi.dima.watchdog.sms.socialistMillionare.factory;


import android.util.Log;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.sms.socialistMillionare.IDontWantToAssociateMessage;
import it.polimi.dima.watchdog.sms.socialistMillionare.KeyValidatedCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionare.PublicKeyRequestCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionare.PublicKeySentCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionare.SMSProtocol;
import it.polimi.dima.watchdog.sms.socialistMillionare.SecretAnswerAndPublicKeyHashSentCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionare.SecretQuestionSentCodeMessage;

public class SocialistMillionaireFactory implements SMSProtocolInterface {

	@Override
	public SMSProtocol getMessage(String header) throws ArbitraryMessageReceivedException {
		
		Log.i("[DEBUG] in factory ho ricevuto: ", header);
		if (header.equals(SMSUtility.CODE1)) {
			return new PublicKeyRequestCodeMessage(SMSUtility.CODE1, null);
		} else if (header.equals(SMSUtility.CODE2)) {
			return new PublicKeySentCodeMessage(SMSUtility.CODE2, null);

		} else if (header.equals(SMSUtility.CODE3)) {
			return new SecretQuestionSentCodeMessage (SMSUtility.CODE3, null);

		} else if (header.equals(SMSUtility.CODE4)) {
			return new SecretAnswerAndPublicKeyHashSentCodeMessage(SMSUtility.CODE4, null);

		} else if (header.equals(SMSUtility.CODE5)) {
			return new KeyValidatedCodeMessage(SMSUtility.CODE5, null);

		} else if (header.equals(SMSUtility.CODE6)) {
			return new IDontWantToAssociateMessage(
					SMSUtility.CODE6, null);
		}
		else {
			throw new ArbitraryMessageReceivedException("Messaggio con un header sconosciuto!!!");
		}

	}
}
