package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.gps.fragments.localization.interfaces.MessageActionListener;
import java.util.ArrayList;
import java.util.List;
import android.util.Log;


public class ListenerUtility {

	private List<MessageActionListener> listeners;
	private static ListenerUtility listUtil = null;
	
	private ListenerUtility() {
		this.listeners = new ArrayList<MessageActionListener>();
	}
	
	public static ListenerUtility getInstance() {
		if(listUtil == null) {
			listUtil = new ListenerUtility();
			
		}
		return listUtil;
	}
	
	public void addListener(MessageActionListener listener) {
		
		Log.i("[DEBUG]", "[DEBUG] prima di aggiungere il listener");
		if(!this.listeners.contains(listener)){
			Log.i("[DEBUG]", "[DEBUG] in listenrUtil aggiungo il listener");
			this.listeners.add(listener);
			Log.i("[DEBUG]", "[DEBUG] in listenrUtil aggiungo il listener " + this.listeners.size());
		}
	}
	
	public void notifyLocationAcquired(double lat, double lon) {
		
		Log.i("[DEBUG]", "[DEBUG] nella notify " + this.listeners.size());
		for(MessageActionListener l: this.listeners) {
			l.onLocationMessageReceived(lat, lon);
		}
	}
	
	public void notifyLocationAcquired(String errorCode) {
		
		Log.i("[DEBUG]", "[DEBUG] nella notify ho ricevuto correttamente il messaggio di errore della locate");
		//TODO fare qualcosa con l'errore
	}
}