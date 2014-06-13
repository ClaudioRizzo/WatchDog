package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.fragments.gps.map.MessageActionListener;

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
		if(!listeners.contains(listener)){
			Log.i("[DEBUG]", "[DEBUG] in listenrUtil aggiungo il listenr");
			this.listeners.add(listener);
			Log.i("[DEBUG]", "[DEBUG] in listenrUtil aggiungo il listenr "+listeners.size());
		}
	}
	
	public void notifyLocationAcquired(double lat, double lon) {
		
		Log.i("[DEBUG]", "[DEBUG] nella notify "+listeners.size());
		for(MessageActionListener l: listeners) {
			l.onLocationMessageReceived(lat, lon);
		}
	}
	
	public void notifyLocationAcquired(String errorCode) {
		
		Log.i("[DEBUG]", "[DEBUG] nella notify ho ricevuto correttamente il messaggio di errore della locate");
		//TODO fare qualcosa con l'errore
	}
	
	
}
