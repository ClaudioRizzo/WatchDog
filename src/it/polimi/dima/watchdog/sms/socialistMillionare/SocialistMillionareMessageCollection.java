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
						new BigInteger("C0DE1FFF", 16).toByteArray(), null),
				new PublicKeySentCodeMessage(
						new BigInteger("C0DE2FFF", 16).toByteArray(), null),
				new SecretQuestionSentCodeMessage(
						new BigInteger("C0DE3FFF", 16).toByteArray(), null),
				new SecretAnswerAndPublicKeyHashSentCodeMessage(
						new BigInteger("C0DE4FFF", 16).toByteArray(), null),
				new KeyValidatedCodeMessage(
						new BigInteger("C0DE5FFF", 16).toByteArray(), null),
				new IDontWantToAssociateMessage(
						new BigInteger("C0DE6FFF", 16).toByteArray(), null) };
	}
	
	@Override
	public void handle(SMSPublicKeyVisitorInterface visitor) {
		for(SocialistMillionareMessageInterface msg : this.messages) {
			msg.handle(visitor);
		}
		
	}

	
}
