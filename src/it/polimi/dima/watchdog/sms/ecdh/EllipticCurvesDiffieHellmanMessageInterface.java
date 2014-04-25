package it.polimi.dima.watchdog.sms.ecdh;

/**
 * Richiesta per il pattern visitor in ECDH.
 * @author emanuele
 *
 */
public interface EllipticCurvesDiffieHellmanMessageInterface {
	
	public void handle(SMSKeyExchangeVisitorInterface visitor);

}
