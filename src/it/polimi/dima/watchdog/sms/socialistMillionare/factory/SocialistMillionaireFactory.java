package it.polimi.dima.watchdog.sms.socialistMillionare.factory;

import java.math.BigInteger;

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
	public SMSProtocol getMessage(byte[] header) throws ArbitraryMessageReceivedException {

		if (header.equals(new BigInteger(SMSUtility.CODE1, 16).toByteArray())) {
			return new PublicKeyRequestCodeMessage(new BigInteger(SMSUtility.CODE1, 16)
			.toByteArray(), null);
		} else if (header.equals(new BigInteger(SMSUtility.CODE2, 16)
				.toByteArray())) {
			return new PublicKeySentCodeMessage(new BigInteger(SMSUtility.CODE2, 16)
			.toByteArray(), null);

		} else if (header.equals(new BigInteger(SMSUtility.CODE3, 16)
				.toByteArray())) {
			return new SecretQuestionSentCodeMessage (new BigInteger(SMSUtility.CODE3, 16)
			.toByteArray(), null);

		} else if (header.equals(new BigInteger(SMSUtility.CODE4, 16)
				.toByteArray())) {
			return new SecretAnswerAndPublicKeyHashSentCodeMessage(new BigInteger(SMSUtility.CODE4, 16)
			.toByteArray(), null);

		} else if (header.equals(new BigInteger(SMSUtility.CODE5, 16)
				.toByteArray())) {
			return new KeyValidatedCodeMessage(new BigInteger(SMSUtility.CODE5, 16)
			.toByteArray(), null);

		} else if (header.equals(new BigInteger(SMSUtility.CODE6, 16)
				.toByteArray())) {
			return new IDontWantToAssociateMessage(
					new BigInteger(SMSUtility.CODE6, 16).toByteArray(), null);
		}
		else {
			throw new ArbitraryMessageReceivedException("Messaggio con un header sconosciuto!!!");
		}

	}
}
