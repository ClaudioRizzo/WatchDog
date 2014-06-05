package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;

/**
 * Questa interfaccia sarà implementata dalle factory di SMP e command message.
 * 
 * @author claudio
 *
 */
public interface ParsebleSMSInterface {
	ParsableSMS getMessage(String header, String body) throws ArbitraryMessageReceivedException;
}
