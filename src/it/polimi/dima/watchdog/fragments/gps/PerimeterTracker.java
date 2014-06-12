package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.fragments.gps.map.LocationException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class PerimeterTracker implements LocationListener {

	private double alpha = 0; // La latitudine al momento del setting del
								// perimetro
	private double beta = 0; // La longitudine "	" "		" "
	private double r; // il raggio della circonferenza

	private PerimeterTrackerListener mCallBack = null;

	private LocationManager mLocationManager;

	/**
	 * The r param by default is settled to 10 meters.
	 * 
	 * @param r
	 *            : The radius of this circle in meters
	 * @param mLocationManager
	 *            : the location manager to use gps features
	 */
	public PerimeterTracker(double r, LocationManager mLocationManager) {

		if (r != 0) {
			this.r = r;
		} else {
			this.r = 10;
		}

		this.mLocationManager = mLocationManager;

	}

	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lon = location.getLongitude();

		if (beta == 0 && alpha == 0) {
			// first localization
			this.alpha = lat;
			this.beta = lon;

		} else {

			// mobile is moving with a settled perimeter
			if (this.r <= computeRadius(lat, lon)) {
				mCallBack.onPerimeterViolated();
			}
		}

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}

	@Override
	public void onProviderEnabled(String provider) {

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param x
	 *            : la latitudine corrente
	 * @param y
	 *            : la longitudine corrente
	 * @return il raggio computato
	 */
	private double computeRadius(double x, double y) {
		double newSquareRadius = (x - alpha) * (x - alpha) + (y - beta)
				* (y - beta);

		return Math.sqrt(newSquareRadius);
	}

	public boolean isProviderEnabled(LocationManager mLocationManager) {

		boolean isGpsEnabled = mLocationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		return (isGpsEnabled || isNetworkEnabled);
	}

	/**
	 * remove the gpsTracker from listening to location changes
	 */
	public void removeLocationUpdates() {
		mLocationManager.removeUpdates(this);
	}

	public void setListener(PerimeterTrackerListener listener) {
		this.mCallBack = listener;
	}

}
