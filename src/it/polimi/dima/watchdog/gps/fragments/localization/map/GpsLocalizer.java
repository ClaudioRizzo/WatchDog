package it.polimi.dima.watchdog.gps.fragments.localization.map;

import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.exceptions.LocationDisabledExeption;
import it.polimi.dima.watchdog.exceptions.LocationException;
import it.polimi.dima.watchdog.gps.fragments.localization.interfaces.LocationChangeListenerInterface;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GpsLocalizer implements LocationListener {
	
	private LocationChangeListenerInterface mCallback;
	private LocationManager mLocationManager;

	
	public GpsLocalizer(Context ctx, LocationManager mLocationManager) throws LocationException {
		
		this.mLocationManager = mLocationManager;
		
		boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if(!isGpsEnabled && !isNetworkEnabled) {
			throw new LocationDisabledExeption(ErrorFactory.LOCALIZATION_IMPOSSIBLE);
		}
	}

	
	public void addListener(LocationChangeListenerInterface mCallback) {
		this.mCallback = mCallback;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.i("[NOTIFICA]", "[NOTIFICA] ho notificato");
		this.mCallback.onlocationChange(location);

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

	public Location getLastKnownLocation() {
		return mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	}

	/**
	 * registers the gpsTracker to get location updates
	 */
	public void getLocationUpdates() {
		Criteria criteria = new Criteria();
	    String provider = this.mLocationManager.getBestProvider(criteria, false);
		this.mLocationManager.requestLocationUpdates(provider, 400, 1, this);
	}

	/**
	 * removes the gpsTracker from listening to location changes
	 */
	public void removeLocationUpdates() {
		this.mLocationManager.removeUpdates(this);
	}
}