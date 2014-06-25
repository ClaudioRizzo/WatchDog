package it.polimi.dima.watchdog.fragments.gps.perimeter.listener;

public interface PerimeterTrackerListener {

	void onPerimeterViolated();

	void onLocationAcquired(double lat, double lon);
}