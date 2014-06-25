package it.polimi.dima.watchdog.fragments.gps.localization.interfaces;

public interface MessageActionListener {

	void onLocationMessageReceived(double lat, double lon);
	void onSmpOver(String other);
}
