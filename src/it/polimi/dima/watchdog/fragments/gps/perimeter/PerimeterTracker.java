package it.polimi.dima.watchdog.fragments.gps.perimeter;

import it.polimi.dima.watchdog.fragments.gps.perimeter.listener.PerimeterTrackerListener;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

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

		Log.i("[DEBUG]", "perimeter-tracker location " + lat);

		if (beta == 0 && alpha == 0) {
			// first localization
			this.alpha = lat;
			this.beta = lon;
			Log.i("[DEBUG]", "[DEBUG - first location]");
			mCallBack.onLocationAcquired(lat, lon);

		} else {

			Log.i("[DEBUG]","[VIOLATE] sono nell'else r="+r+"nuovo: "+getDistance(lat, lon));
			// mobile is moving with a settled perimeter
			Log.i("[DEBUG]","[VIOLATE] accuracy: "+location.getAccuracy());
			
			if (this.r <= getDistance(lat, lon)) {
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
	 * get distance from the new point passed as latitude and longitude and the center in alpa beta
	 * @param lat
	 * @param lon
	 * @return
	 */
	private double getDistance(double lat, double lon) {
		
		double distance;
		Location center = new Location("");
		Location newLoc = new Location("");
		
		newLoc.setLatitude(lat);
		newLoc.setLongitude(lon);
		
		Log.i("[DEBUG]", "[VIOLATE] a= "+alpha+", b= "+beta);
		Log.i("[DEBUG]", "[VIOLATE] new_lat= "+lat+", new_lon= "+lon);
		
		center.setLatitude(alpha);
		center.setLongitude(beta);
		
		distance = center.distanceTo(newLoc);
	
		
		return distance;
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

	/**
	 * register the gpsTracker to get location updates
	 */
	public void getLocationUpdates() {
		Criteria criteria = new Criteria();
		String provider = mLocationManager.getBestProvider(criteria, false);
		mLocationManager.requestLocationUpdates(provider, 200, 1, this);
	}

	public Location getLastLocation() {

		Criteria criteria = new Criteria();
		String provider = mLocationManager.getBestProvider(criteria, false);
		return mLocationManager.getLastKnownLocation(provider);

	}
	
	public double getRadius() {
		return this.r;
	}
	
	public void setRadius(double r) {
		this.r = r;
	}


}
