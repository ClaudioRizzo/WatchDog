package it.polimi.dima.watchdog.sms.commands.flags;

import android.util.Log;
import it.polimi.dima.watchdog.sms.commands.LocateCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkFoundCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkLostCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkLostOrStolenCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkStolenCodeMessage;
import it.polimi.dima.watchdog.sms.commands.SMSCommandVisitorInterface;
import it.polimi.dima.watchdog.sms.commands.SirenOffCodeMessage;
import it.polimi.dima.watchdog.sms.commands.SirenOnCodeMessage;
import it.polimi.dima.watchdog.utilities.ListenerUtility;
import it.polimi.dima.watchdog.utilities.SMSUtility;

/**
 * Classe che gestisce i dati ritornati da m4.
 * 
 * @author emanuele
 *
 */
public class SMSM4Handler implements SMSCommandVisitorInterface {	
	
	@Override
	public void visit(SirenOnCodeMessage sirenOnCodeMessage) {
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN ON RESPONSE RECEIVED");
		sirenOnCodeMessage.extractSubBody(sirenOnCodeMessage.getBody());
		
		if(sirenOnCodeMessage.getMessage().equals(SMSUtility.SIREN_ON_RESPONSE_OK)){
			//TODO fare qualcosa
		}
		else if(sirenOnCodeMessage.getMessage().equals(SMSUtility.SIREN_ON_RESPONSE_KO)){
			//TODO fare qualcosa
		}
		else{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void visit(SirenOffCodeMessage sirenOffCodeMessage) {
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN OFF RESPONSE RECEIVED");
		sirenOffCodeMessage.extractSubBody(sirenOffCodeMessage.getBody());
		
		if(sirenOffCodeMessage.getMessage().equals(SMSUtility.SIREN_OFF_RESPONSE_OK)){
			//TODO fare qualcosa
		}
		else if(sirenOffCodeMessage.getMessage().equals(SMSUtility.SIREN_OFF_RESPONSE_KO)){
			//TODO fare qualcosa
		}
		else{
			throw new IllegalArgumentException();
		}
	}

	@Override
	public void visit(MarkLostCodeMessage markLostCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK LOST RESPONSE RECEIVED");
	}

	@Override
	public void visit(MarkStolenCodeMessage markStolenCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK STOLEN RESPONSE RECEIVED");
	}

	@Override
	public void visit(MarkLostOrStolenCodeMessage markLostOrStolenCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK LOST OR STOLEN RESPONSE RECEIVED");
	}

	@Override
	public void visit(MarkFoundCodeMessage markFoundCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK FOUND RESPONSE RICEVUTO");
	}

	@Override
	public void visit(LocateCodeMessage locateCodeMessage) {
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] LOCATE RESPONSE RICEVUTO");
		locateCodeMessage.extractSubBody(locateCodeMessage.getBody());
		
		if(locateCodeMessage.getErrorCode() != null){
			double latitude = locateCodeMessage.getlatitude();
			double longitude = locateCodeMessage.getLongitude();
			
			ListenerUtility.getInstance().notifyLocationAcquired(latitude, longitude);
		}
		else{
			ListenerUtility.getInstance().notifyLocationAcquired(locateCodeMessage.getErrorCode());
		}	
	}
}
