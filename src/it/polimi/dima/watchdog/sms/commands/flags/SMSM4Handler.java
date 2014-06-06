package it.polimi.dima.watchdog.sms.commands.flags;

import android.content.Context;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.map.LocationReceivedListener;
import it.polimi.dima.watchdog.fragments.gps.map.MyMapFragment;
import it.polimi.dima.watchdog.sms.commands.LocateCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkFoundCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkLostCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkLostOrStolenCodeMessage;
import it.polimi.dima.watchdog.sms.commands.MarkStolenCodeMessage;
import it.polimi.dima.watchdog.sms.commands.SMSCommandVisitorInterface;
import it.polimi.dima.watchdog.sms.commands.SirenOffCodeMessage;
import it.polimi.dima.watchdog.sms.commands.SirenOnCodeMessage;

/**
 * 
 * @author emanuele
 *
 */
public class SMSM4Handler implements SMSCommandVisitorInterface {
	
	private String other; //TODO utile?
	private Context ctx; //TODO utile?
	
	
	public SMSM4Handler(String other, Context context){ //TODO utile?
		this.other = other;
		this.ctx = context;
	}
	
	@Override
	public void visit(SirenOnCodeMessage sirenOnCodeMessage) {
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN ON RECEIVED");
		
	}

	@Override
	public void visit(SirenOffCodeMessage sirenOffCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] SIREN OFF RECEIVED");
	}

	@Override
	public void visit(MarkLostCodeMessage markLostCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK LOST RECEIVED");
	}

	@Override
	public void visit(MarkStolenCodeMessage markStolenCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK STOLEN RECEIVED");
	}

	@Override
	public void visit(MarkLostOrStolenCodeMessage markLostOrStolenCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK LOST OR STOLEN RECEIVED");
	}

	@Override
	public void visit(MarkFoundCodeMessage markFoundCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] MARK FOUND RICEVUTO");
	}

	@Override
	public void visit(LocateCodeMessage locateCodeMessage) {
		// TODO Auto-generated method stub
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] LOCATE RESPONSE RICEVUTO");
		locateCodeMessage.extractSubBody(locateCodeMessage.getBody());
		
		double latitude = locateCodeMessage.getlatitude();
		double longitude = locateCodeMessage.getLongitude();
		
		Location l = new Location("");
		l.setLatitude(latitude);
		l.setLongitude(longitude);
		
		MyMapFragment mapFrag = new MyMapFragment(l);
		
		FragmentActivity myFrag = (FragmentActivity) ctx;
		FragmentTransaction trans = myFrag.getSupportFragmentManager().beginTransaction();
		trans.replace(R.id.gps_fragment_container, mapFrag);
		trans.addToBackStack(null);
		trans.commit();
			
	}
	
	
}
