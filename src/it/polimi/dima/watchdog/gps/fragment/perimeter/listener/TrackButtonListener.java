package it.polimi.dima.watchdog.gps.fragment.perimeter.listener;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.gps.fragment.perimeter.PerimeterTracker;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class TrackButtonListener implements OnClickListener {

	private PerimeterTracker tracker;
	private Context context;

	public TrackButtonListener(PerimeterTracker tracker, Context context) {
		this.tracker = tracker;
		this.context = context;
	}

	@Override
	public void onClick(View v) {

		LocationManager locationMan = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		if (!this.tracker.isProviderEnabled(locationMan)) {
			askForLocationEnabled();
		}

		this.tracker.getLocationUpdates();
		((ActionBarActivity) this.context).findViewById(R.id.seek_bar_radius).setVisibility(View.INVISIBLE);
		((ActionBarActivity) this.context).findViewById(R.id.linear_layout_seek_bar_values).setVisibility(View.INVISIBLE);
		

	}

	private void askForLocationEnabled() {
		/**
		 * Function to show settings alert dialog
		 * */

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this.context);

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
						context.startActivity(intent);
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

}
