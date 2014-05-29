package it.polimi.dima.watchdog.sms.commands;

import android.util.Log;
import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.ParsebleSMSInterface;
import it.polimi.dima.watchdog.utilities.SMSUtility;

/**
 * 
 * @author emanuele
 *
 */
public class CommandFactory implements ParsebleSMSInterface {

	@Override
	public ParsableSMS getMessage(String header) throws ArbitraryMessageReceivedException {
		Log.i("[DEBUG] in factory ho ricevuto: ", header); //per vedere l'header ricevuto
		if(header.equals(SMSUtility.SIREN_ON)){
			return new SirenOnCodeMessage(SMSUtility.SIREN_ON, null);
		}
		else if(header.equals(SMSUtility.SIREN_OFF)){
			return new SirenOffCodeMessage(SMSUtility.SIREN_OFF, null);
		}
		else if(header.equals(SMSUtility.MARK_LOST)){
			return new MarkLostCodeMessage(SMSUtility.MARK_LOST, null);
		}
		else if(header.equals(SMSUtility.MARK_STOLEN)){
			return new MarkStolenCodeMessage(SMSUtility.MARK_STOLEN, null);
		}
		else if(header.equals(SMSUtility.MARK_LOST_OR_STOLEN)){
			return new MarkLostOrStolenCodeMessage(SMSUtility.MARK_LOST_OR_STOLEN, null);
		}
		else if(header.equals(SMSUtility.MARK_FOUND)){
			return new MarkFoundCodeMessage(SMSUtility.MARK_FOUND, null);
		}
		else if(header.equals(SMSUtility.LOCATE)){
			return new LocateCodeMessage(SMSUtility.LOCATE, null);
		}
		else throw new ArbitraryMessageReceivedException("Messaggio con un header sconosciuto!!!");
	}
}