package it.polimi.dima.watchdog.gps.fragment.perimeter.listener;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.gps.fragment.perimeter.PerimeterTracker;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class TrackButtonListener implements OnClickListener {

	private PerimeterTracker tracker;
	private Context context;

	public TrackButtonListener(PerimeterTracker tracker, Context context) {
		this.tracker = tracker;
		this.context = context;
	}

	@Override
	public void onClick(View v) {

		LocationManager locationMan = (LocationManager) this.context.getSystemService(Context.LOCATION_SERVICE);

		if (!this.tracker.isProviderEnabled(locationMan)) {
			askForLocationEnabled();
		}

		this.tracker.getLocationUpdates();
		((ActionBarActivity) this.context).findViewById(R.id.seek_bar_radius).setVisibility(View.GONE);
		((ActionBarActivity) this.context).findViewById(R.id.linear_layout_seek_bar_values).setVisibility(View.GONE);
		

	}

	private void askForLocationEnabled() {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this.context);

		TextView title = new TextView(this.context);
        title.setText("GPS ALERT!!!");
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        alertDialogBuilder.setCustomTitle(title);
        
        TextView msg = new TextView(this.context);
        msg.setText("GPS is not enabled. Do you want to go to settings menu?");
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);
        alertDialogBuilder.setView(msg);

		alertDialogBuilder.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						context.startActivity(intent);
					}
				});

		alertDialogBuilder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});

		Dialog alertDialog = alertDialogBuilder.create();
		alertDialog.setCanceledOnTouchOutside(false);
		alertDialog.setCancelable(false);
		alertDialog.show();
	}
}