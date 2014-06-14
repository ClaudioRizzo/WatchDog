package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.transition.Visibility;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TrackButtonListener implements OnClickListener {

	private PerimeterTracker tracker;
	private Context ctx;

	public TrackButtonListener(PerimeterTracker tracker, Context ctx) {
		this.tracker = tracker;
		this.ctx = ctx;
	}

	@Override
	public void onClick(View v) {

		LocationManager locationMan = (LocationManager) ctx
				.getSystemService(Context.LOCATION_SERVICE);

		if (!tracker.isProviderEnabled(locationMan)) {
			askForLocationEnabled();
		}

		tracker.getLocationUpdates();
		((ActionBarActivity) ctx).findViewById(R.id.seek_bar_radius).setVisibility(View.INVISIBLE);
		((ActionBarActivity) ctx).findViewById(R.id.linear_layout_seek_bar_values).setVisibility(View.INVISIBLE);
		

	}

	private void askForLocationEnabled() {
		/**
		 * Function to show settings alert dialog
		 * */

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(ctx);

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
						ctx.startActivity(intent);
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
