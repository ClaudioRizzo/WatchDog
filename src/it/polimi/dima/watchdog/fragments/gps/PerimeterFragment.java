package it.polimi.dima.watchdog.fragments.gps;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.map.GpsLocalizer;
import it.polimi.dima.watchdog.fragments.gps.map.LocationChangeListenerInterface;
import it.polimi.dima.watchdog.fragments.gps.map.LocationException;
import it.polimi.dima.watchdog.utilities.FragmentAdapterLifecycle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;

public class PerimeterFragment extends Fragment implements
		PerimeterTrackerListener, FragmentAdapterLifecycle {

	private GoogleMap mMap;
	private MapView mMapView;
	private Bundle mBundle;
	private PerimeterTracker perimeterTracker;
	private Activity mActivity;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater
				.inflate(R.layout.fragment_perimeter, container, false);

		mMapView = (MapView) v.findViewById(R.id.perimeter_map);
		mMapView.onCreate(mBundle);
		setUpMapIfNeed(v);
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

		perimeterTracker = new PerimeterTracker(1,
				(LocationManager) getActivity().getSystemService(
						Context.LOCATION_SERVICE));
		perimeterTracker.setListener(this);

	}

	private void askForLocationEnabled() {
		/**
		 * Function to show settings alert dialog
		 * */

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

		// Setting Dialog Title
		alertDialog.setTitle("GPS is settings");

		// Setting Dialog Message
		alertDialog
				.setMessage("GPS is not enabled. Do you want to go to settings menu?");

		// Setting Icon to Dialog
		// alertDialog.setIcon(R.drawable.delete);

		// On pressing Settings button
		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						startActivity(intent);
					}
				});

		// on pressing cancel button
		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		// Showing Alert Message
		alertDialog.show();

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

	}

	@Override
	public void onResumeFragment() {
		Log.i("[DEBuG]", "onResumeFragment() perimeter "+getActivity());
		
		LocationManager locationMan = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);
		
		if(!perimeterTracker.isProviderEnabled(locationMan)) {
			askForLocationEnabled();
		}

	}
	
	

	@Override
	public void onPauseFragment() {
		Log.i("[DEBUG]", "onPauseFragment() perimeter "+getActivity());
		// Toast.makeText(getActivity(), "onResumeFragment():" + "perimeter",
		// Toast.LENGTH_SHORT).show();

	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.i("DEBUG", "perimeter onAttach()");
	}
	


}