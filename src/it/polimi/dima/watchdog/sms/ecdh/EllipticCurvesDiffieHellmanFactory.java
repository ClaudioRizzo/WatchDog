package it.polimi.dima.watchdog.sms.ecdh;

import android.util.Log;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.sms.socialistMillionare.SMSProtocol;
import it.polimi.dima.watchdog.sms.socialistMillionare.factory.SMSProtocolInterface;

public class EllipticCurvesDiffieHellmanFactory implements SMSProtocolInterface {

	@Override
	public SMSProtocol getMessage(String header) throws ArbitraryMessageReceivedException {
		Log.i("[DEBUG] in factory ho ricevuto: ", header); //per vedere l'header ricevuto
		if(header.equals(SMSUtility.CODE7)){
			return new HereIsMyPublicKeyCodeMessage(SMSUtility.CODE7, null);
		}
		else if(header.equals(SMSUtility.CODE8)){
			return new HereIsMyPublicKeyTooCodeMessage(SMSUtility.CODE8, null);
		}
		else throw new ArbitraryMessageReceivedException("Messaggio con un header sconosciuto!!!");
	}

}
