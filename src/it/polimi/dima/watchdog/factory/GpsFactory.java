package it.polimi.dima.watchdog.factory;

import it.polimi.dima.watchdog.gps.fragment.perimeter.PerimeterFragment;
import it.polimi.dima.watchdog.gps.fragments.localization.LocalizationFragment;
import android.support.v4.app.Fragment;

/**
 * 
 * @author claudio
 *
 */
public class GpsFactory extends FeatureFactory {

	@Override
	public Fragment getFragment(int pos) {
		switch (pos) {
		case 0:
			return new LocalizationFragment();//GpsFragmentsContainer();
		case 1:
			return new PerimeterFragment();
		}
		throw new IllegalStateException("Per il GPS questa posizione non esiste!");
	}
}