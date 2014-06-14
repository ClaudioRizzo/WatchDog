package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPerimeterListener implements OnSeekBarChangeListener {

	
	private Context ctx;
	private PerimeterTracker tracker;

	public SeekBarPerimeterListener(Context ctx, PerimeterTracker tracker) {
		this.ctx = ctx;
		this.tracker = tracker;
		
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		TextView radiusView = (TextView) ((ActionBarActivity) ctx).findViewById(R.id.text_view_settled_radius);
		radiusView.setText("Hai settato un raggio di: "+progress+" metri");
		
		tracker.setRadius(progress);

		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

}
