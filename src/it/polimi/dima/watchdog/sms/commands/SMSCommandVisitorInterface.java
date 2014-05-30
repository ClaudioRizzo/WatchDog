package it.polimi.dima.watchdog.sms.commands;

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
	public void visit(LocateCodeMessage locateCodeMessage);
}
