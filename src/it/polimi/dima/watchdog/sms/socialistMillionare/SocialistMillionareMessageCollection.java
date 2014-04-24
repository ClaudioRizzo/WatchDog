package it.polimi.dima.watchdog.sms.socialistMillionare;

import java.math.BigInteger;

/**
 * Collezione di header di messaggi dalla quale parte il pattern visitor
 * @author claudio
 *
 */
public class SocialistMillionareMessageCollection implements SocialistMillionareMessageInterface {

	
	private SocialistMillionareMessageInterface[] messages;
	
	public SocialistMillionareMessageCollection() {

		this.messages = new SocialistMillionareMessageInterface[] {
				new PublicKeyRequestCodeMessage(
						new BigInteger("0xC0DE1FFF").toByteArray(), null),
				new PublicKeySentCodeMessage(
						new BigInteger("0xC0DE2FFF").toByteArray(), null),
				new SecretQuestionSentCodeMessage(
						new BigInteger("0xC0DE3FFF").toByteArray(), null),
				new SecretAnswerAndPublicKeyHashSentCodeMessage(
						new BigInteger("0xC0DE4FFF").toByteArray(), null),
				new KeyValidatedCodeMessage(
						new BigInteger("0xC0DE5FFF").toByteArray(), null),
				new IDontWantToAssociateMessage(
						new BigInteger("0xC0DE6FFF").toByteArray(), null) };
	}
	
	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		for(SocialistMillionareMessageInterface msg : this.messages) {
			msg.handle(visitor);
		}
		
	}

	
}
