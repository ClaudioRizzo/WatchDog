package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;

public class StopTrackButtonListener implements OnClickListener {

	private PerimeterTracker tracker;
	private Context ctx;

	public StopTrackButtonListener(PerimeterTracker tracker, Context ctx) {
		this.tracker = tracker;
		this.ctx = ctx;
	}

	@Override
	public void onClick(View v) {

		tracker.removeLocationUpdates();
		((ActionBarActivity) ctx).findViewById(R.id.seek_bar_radius).setVisibility(View.VISIBLE);
		((ActionBarActivity) ctx).findViewById(R.id.linear_layout_seek_bar_values).setVisibility(View.VISIBLE);

	}

}
