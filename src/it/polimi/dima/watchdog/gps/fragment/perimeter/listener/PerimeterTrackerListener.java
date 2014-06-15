package it.polimi.dima.watchdog.gps.fragment.perimeter.listener;

public interface PerimeterTrackerListener {

	void onPerimeterViolated();

	void onLocationAcquired(double lat, double lon);
}
