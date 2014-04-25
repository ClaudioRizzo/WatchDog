package it.polimi.dima.watchdog.sms.ecdh;

/**
 * Interfaccia per il visitor nello scambio di messaggi in ECDH.
 * @author emanuele
 *
 */
public interface SMSKeyExchangeVisitorInterface {

	public void visit(HereIsMyPublicKeyCodeMessage pubKeySentMessage);
	public void visit(HereIsMyPublicKeyTooCodeMessage pubKeySentTooMessage);

}
