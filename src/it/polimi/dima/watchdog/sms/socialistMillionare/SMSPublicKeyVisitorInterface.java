package it.polimi.dima.watchdog.sms.socialistMillionare;

/**
 * Visitors Interface for the message exchanging in the SMP
 * @author claudio
 *
 */
public interface SMSPublicKeyVisitorInterface {

	public void visit(PublicKeyRequestCodeMessage pubKeyReqMsg);
	public void visit(PublicKeySentCodeMessage pubKeySentMsg);
	public void visit(SecretAnswerAndPublicKeyHashSentCodeMessage secAnswMsg);
	public void visit(SecretQuestionSentCodeMessage secQuestMsg);
	public void visit(KeyValidatedCodeMessage keyValMsg);
	public void visit(IDontWantToAssociateMessage noAssMsg);
	public void visitCollection(SocialistMillionareMessageCollection collection);
}
