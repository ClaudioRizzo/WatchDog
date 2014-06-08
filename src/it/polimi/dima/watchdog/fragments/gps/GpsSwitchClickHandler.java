package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.map.MyMapFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class GpsSwitchClickHandler implements OnClickListener {

	private FragmentManager mFragMan;

	public GpsSwitchClickHandler(FragmentManager mFragMan) {
		this.mFragMan = mFragMan;
	}

	@Override
	public void onClick(View v) {

		FragmentTransaction transaction = mFragMan.beginTransaction();

		transaction.replace(R.id.gps_fragment_container,
				new MyMapFragment(null));
		transaction.addToBackStack(null);
		transaction.commit();

	}

}
