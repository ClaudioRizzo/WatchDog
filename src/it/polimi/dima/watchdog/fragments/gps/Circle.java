package it.polimi.dima.watchdog.fragments.gps;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;


public class Circle implements LocationListener {

		
	private double alpha; //La latitudine al momento del setting del perimetro
	private double beta;  //La longitudine "	"		"		"		"
	private double r; //il raggio della circonferenza
	
	private LocationManager mLocationManager;
	
	/**
	 * The r param by default is settled to 10 meters.
	 * 
	 * 
	 * @param alpha: The latitude when the perimeter has been settled
	 * @param beta: The longitude when the perimeter has been settled
	 * @param r: The radius of this circle in meters
	 * @param mLocationManager: the location manager to use gps features
	 */
	public Circle(double alpha, double beta, double r, LocationManager mLocationManager) {
		this.alpha = alpha;
		this.beta = beta;
		
		if(r != 0) {
			this.r = r;
		} else {
			this.r = 10;
		}
		
		this.mLocationManager = mLocationManager;
		initializeGps();
	}

	private void initializeGps() {
		
		
	}

	@Override
	public void onLocationChanged(Location location) {
		double lat = location.getLatitude();
		double lon = location.getLongitude();
		
		if(this.r <= computeRadius(lat, lon)) {
			//TODO: il perimetro è stato violato!!!
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
	 * @param x: la latitudine corrente
	 * @param y: la longitudine corrente
	 * @return il raggio computato
	 */
	private double computeRadius(double x, double y) {
		double newSquareRadius = (x-alpha)*(x-alpha) + (y-beta)*(y-beta);
		
		return Math.sqrt(newSquareRadius);
	}
	
	public boolean isProviderEnabled() {
		
		boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		return (isGpsEnabled || isNetworkEnabled);
	}
	
	/**
	 * remove the gpsTracker from listening to location cahnges
	 */
	public void removeLocationUpdates() {
		mLocationManager.removeUpdates(this);
	}
	
}
