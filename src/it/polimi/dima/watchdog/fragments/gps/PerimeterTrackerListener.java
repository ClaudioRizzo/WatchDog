package it.polimi.dima.watchdog.fragments.gps;

public interface PerimeterTrackerListener {

	void onPerimeterViolated();

	void onLocationAcquired(double lat, double lon);
}
