package it.polimi.dima.watchdog.fragments.gps.map;

public interface LocationReceivedListener {

	void onLocationMessageReceived(double lat, double lon);
}
