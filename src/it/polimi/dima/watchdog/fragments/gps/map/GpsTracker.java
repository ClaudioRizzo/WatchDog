package it.polimi.dima.watchdog.fragments.gps.map;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GpsTracker implements LocationListener {
	
	private LocationChangeListenerInterface mCallback;
	private LocationManager mLocationManager;

	
	public GpsTracker(Context ctx, LocationManager mLocationManager) throws LocationException {
		
		this.mLocationManager = mLocationManager;
		
		boolean isGpsEnabled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		boolean isNetworkEnabled = mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		
		if(!isGpsEnabled && !isNetworkEnabled) {
			throw new LocationDisabledExeption("Gps e rete dati disattivi");
		}
			
		
	}
	
	public void addListener(LocationChangeListenerInterface mCallback) {
		this.mCallback = mCallback;
	}
	
	@Override
	public void onLocationChanged(Location location) {
		Log.i("[NOTIFICA]", "[NOTIFICA] ho notificato");
		mCallback.onlocationChange(location);
		
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
	 * register the gpsTracker to get location updates
	 */
	public void getLocationUpdates() {
		Criteria criteria = new Criteria();
	    String provider = mLocationManager.getBestProvider(criteria, false);
		mLocationManager.requestLocationUpdates(provider, 400, 1, this);
	}
	
	/**
	 * remove the gpsTracker from listening to location cahnges
	 */
	public void removeLocationUpdates() {
		mLocationManager.removeUpdates(this);
	}
	

}
