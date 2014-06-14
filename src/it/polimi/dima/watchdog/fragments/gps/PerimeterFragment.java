package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.utilities.FragmentAdapterLifecycle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

public class PerimeterFragment extends Fragment implements
		PerimeterTrackerListener, FragmentAdapterLifecycle {

	private GoogleMap mMap;
	private MapView mMapView;
	private Bundle mBundle;
	private PerimeterTracker perimeterTracker;
	private Location lastKnown;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.fragment_perimeter, container, false);

		MapsInitializer.initialize(getActivity());
		mMapView = (MapView) v.findViewById(R.id.perimeter_map);
		mMapView.onCreate(mBundle);
		setUpMapIfNeed(v);

		Button trackButton = (Button) v.findViewById(R.id.button_track);
		trackButton.setOnClickListener(new TrackButtonListener(
				perimeterTracker, getActivity()));

		Button stopTrackingButton = (Button) v
				.findViewById(R.id.button_stop_tracking);
		stopTrackingButton.setOnClickListener(new StopTrackButtonListener(
				perimeterTracker, getActivity()));

		SeekBar seekBar = (SeekBar) v.findViewById(R.id.seek_bar_radius);
		seekBar.setOnSeekBarChangeListener(new SeekBarPerimeterListener(
				getActivity(), perimeterTracker));

		return v;
	}

	private void setUpMapIfNeed(View v) {
		if (mMap == null) {
			mMap = ((MapView) v.findViewById(R.id.perimeter_map)).getMap();

			if (mMap != null) {
				setUpMap();
			}
		}

	}

	private void setUpMap() {
		// Context ctx = getActivity().getApplicationContext();

		perimeterTracker = new PerimeterTracker(0,
				(LocationManager) getActivity().getSystemService(
						Context.LOCATION_SERVICE));
		perimeterTracker.setListener(this);
		// perimeterTracker.getLocationUpdates();

		this.lastKnown = perimeterTracker.getLastLocation();

		if (lastKnown != null) {
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
					lastKnown.getLatitude(), lastKnown.getLongitude()), 19f));
		}

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mBundle = savedInstanceState;
		Log.i("DEBUG", "DEBUG-FRAG on create");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i("DEBUG", "DEBUG-FRAG on resume");
		mMapView.onResume();
	}

	@Override
	public void onPause() {
		// Destroy map
		super.onPause();
		mMapView.onPause();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();

	}

	@Override
	public void onPerimeterViolated() {
		Log.i("[DEBUG]", "[VIOLATE] ho violato il perimetro");
		//TODO: la precisione non è troppo scarsa :( 
	}

	@Override
	public void onResumeFragment() {
		Log.i("[DEBuG]", "onResumeFragment() perimeter " + getActivity());

	}

	@Override
	public void onPauseFragment() {
		Log.i("[DEBUG]", "onPauseFragment() perimeter " + getActivity());
		// Toast.makeText(getActivity(), "onResumeFragment():" + "perimeter",
		// Toast.LENGTH_SHORT).show();

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("DEBUG", "perimeter onAttach()");
	}

	@Override
	public void onLocationAcquired(double lat, double lon) {
		// Instantiates a new CircleOptions object and defines the center and
		// radius
		CircleOptions circleOptions = new CircleOptions().center(
				new LatLng(lat, lon)).radius(perimeterTracker.getRadius()); // In
																			// meters

		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lat,
				lon), 19f));

		// Get back the mutable Circle

		Circle circle = mMap.addCircle(circleOptions);
		circle.setStrokeColor(Color.BLUE);
		circle.setFillColor(Color.TRANSPARENT);

	}

}