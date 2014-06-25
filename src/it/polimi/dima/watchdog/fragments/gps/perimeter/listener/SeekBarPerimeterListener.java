package it.polimi.dima.watchdog.fragments.gps.perimeter.listener;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.perimeter.PerimeterTracker;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class SeekBarPerimeterListener implements OnSeekBarChangeListener {

	
	private Context context;
	private PerimeterTracker tracker;

	public SeekBarPerimeterListener(Context context, PerimeterTracker tracker) {
		this.context = context;
		this.tracker = tracker;
		
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		TextView radiusView = (TextView) ((ActionBarActivity) this.context).findViewById(R.id.text_view_settled_radius);
		radiusView.setText("Radius settled to: "+progress+" meters");
		
		this.tracker.setRadius(progress);

		
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