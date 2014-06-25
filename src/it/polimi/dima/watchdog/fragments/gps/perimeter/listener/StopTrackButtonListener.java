package it.polimi.dima.watchdog.fragments.gps.perimeter.listener;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.perimeter.PerimeterTracker;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class StopTrackButtonListener implements OnClickListener {

	private PerimeterTracker tracker;
	private Context context;

	public StopTrackButtonListener(PerimeterTracker tracker, Context context) {
		this.tracker = tracker;
		this.context = context;
	}

	@Override
	public void onClick(View v) {

		this.tracker.removeLocationUpdates();
		Log.i("[STOP]", "[STOP]");
		((ActionBarActivity) this.context).findViewById(R.id.seek_bar_radius).setVisibility(View.VISIBLE);
		((ActionBarActivity) this.context).findViewById(R.id.linear_layout_seek_bar_values).setVisibility(View.VISIBLE);

	}

}
