package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.map.MyMapFragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.View.OnClickListener;

public class GpsSwitchClickHandler implements OnClickListener {

	private FragmentManager mFragMan;
	
	public GpsSwitchClickHandler(FragmentManager mFragMan) {
		this.mFragMan = mFragMan;
	}
	
	@Override
	public void onClick(View v) {
		
		mFragMan.beginTransaction().replace(R.id.gps_fragment_container, new MyMapFragment()).commit();
	}

}
