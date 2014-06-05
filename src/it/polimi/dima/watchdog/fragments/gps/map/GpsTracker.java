package it.polimi.dima.watchdog.fragments.gps.map;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GpsTracker extends Service implements LocationListener {

	/* variabili utili al gps */

	private final Context ctx;

	boolean isGpsEnabled = false;
	boolean isNetworkEnabled = false;

	boolean canGetLocation = false;

	private Location location;
	double latitude, longitude;

	// The minimum distance to change Updates in meters
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

	// The minimum time between updates in milliseconds
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute

	// Declaring a Location Manager
	protected LocationManager locationManager;

	public GpsTracker(Context ctx) {
		this.ctx = ctx;
		this.location = getLocation();
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d("[DEBUG] - change location", "ho cambiato location");
		Log.d("[DEBUG] - change location", "latitudine: "+location.getLatitude());
		Log.d("[DEBUG] - change location", "longitudine: "+location.getLongitude());

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	public Location getLocation() {

		locationManager = (LocationManager) ctx
				.getSystemService(LOCATION_SERVICE);

		isGpsEnabled = locationManager
				.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = locationManager
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

		if (!isGpsEnabled && !isNetworkEnabled) {
			// non posso connettermi
		} else {
			this.canGetLocation = true;
			if (isNetworkEnabled) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				Log.d("Network", "Network Position Connection");
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				}

			}

			if (isGpsEnabled) {
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES,
						MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
				Log.d("GPS", "GPS Position Connection");
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);

				if (location != null) {
					latitude = location.getLatitude();
					longitude = location.getLongitude();
				}
			}
		}
		
		return location;

	}
	
	public double getLatitude() {
		return this.latitude;
	}
	
	public double getLongitude() {
		return this.longitude;
	}

}
