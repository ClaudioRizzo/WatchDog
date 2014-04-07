package it.polimi.dima.watchdog.factory;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.LocalizatoionFragment;
import it.polimi.dima.watchdog.fragments.PerimeterFragment;
import android.support.v4.app.Fragment;

/**
 * dispaccia i fragment relativi alle funzionalità del gps
 * @author claudio
 *
 */
public class GpsFactory extends FeaturesFactory {

	@Override
	public Fragment getFragment(int id) {
		switch (id) {
		case R.id.tab1_root_frame:
			return new LocalizatoionFragment();
		case R.id.tab2_root_frame:
			return new PerimeterFragment();
		default:
			return null;
		}
	}

}
