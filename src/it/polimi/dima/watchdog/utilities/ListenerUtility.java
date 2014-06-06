package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.fragments.gps.map.MessageActionListener;

import java.util.ArrayList;
import java.util.List;


public class ListenerUtility {

	private List<MessageActionListener> listeners;
	
	private static ListenerUtility listUtil = null;
	
	private ListenerUtility() {
		this.listeners = new ArrayList<MessageActionListener>();
	}
	
	public static ListenerUtility getInstance() {
		if(listUtil == null) {
			return new ListenerUtility();
		}
		else
		{
			return listUtil;
		}
	}
	
	public void addListener(MessageActionListener listener) {
		
		if(!listeners.contains(listener))
			this.listeners.add(listener);
	}
	
	public void notifyLocationAcquired(double lat, double lon) {
		for(MessageActionListener l: listeners) {
			l.onLocationMessageReceived(lat, lon);
		}
	}
	
	
}
