package it.polimi.dima.watchdog.gps.fragments.localization.interfaces;

public interface MessageActionListener {

	void onLocationMessageReceived(double lat, double lon);
	void onSmpOver(String other);
}
