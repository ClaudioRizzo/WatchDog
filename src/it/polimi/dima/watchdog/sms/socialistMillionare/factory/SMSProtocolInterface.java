package it.polimi.dima.watchdog.sms.socialistMillionare.factory;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.sms.socialistMillionare.SMSProtocol;

/**
 * Questa interfaccia sarà implementata dalle factory di SMP ed ECDH.
 * @author claudio
 *
 */
public interface SMSProtocolInterface {

	SMSProtocol getMessage(String header) throws ArbitraryMessageReceivedException;
}
