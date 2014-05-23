package it.polimi.dima.watchdog.sms.socialistMillionaire.factory;


import android.util.Log;
import it.polimi.dima.watchdog.UTILITIES.SMSUtility;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.ParsebleSMSInterface;
import it.polimi.dima.watchdog.sms.socialistMillionaire.IDontWantToAssociateCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionaire.KeyValidatedCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionaire.PublicKeyRequestCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionaire.PublicKeySentCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SecretAnswerAndPublicKeyHashSentCodeMessage;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SecretQuestionSentCodeMessage;

public class SocialistMillionaireFactory implements ParsebleSMSInterface {

	@Override
	public ParsableSMS getMessage(String header) throws ArbitraryMessageReceivedException {
		
		Log.i("[DEBUG_SMP]", "[DEBUG_SMP] in factory ho ricevuto: " +  header);
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
			return new IDontWantToAssociateCodeMessage(
					SMSUtility.CODE6, null);
		}
		else {
			throw new ArbitraryMessageReceivedException("Messaggio con un header sconosciuto!!!");
		}

	}
}
