package it.polimi.dima.watchdog.sms.socialistMillionare.factory;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.sms.socialistMillionare.SMSProtocol;

public interface SMSProtocolInterface {

	SMSProtocol getMessage(byte[] header) throws ArbitraryMessageReceivedException;
}
