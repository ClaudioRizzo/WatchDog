package it.polimi.dima.watchdog.factory;

import it.polimi.dima.watchdog.fragments.gps.DummyGps;
import it.polimi.dima.watchdog.fragments.gps.LocalizationFragment;
import it.polimi.dima.watchdog.fragments.gps.PerimeterFragment;
import android.support.v4.app.Fragment;

public class GpsFactory extends FeatureFactory {

	@Override
	public Fragment getFragment(int pos) {
		switch (pos) {
		case 0:
			return new LocalizationFragment();
		case 1:
			return new PerimeterFragment();
		case 2:
			return new DummyGps();
		}
		throw new IllegalStateException("Per il GPS questa posizione non esiste!");
	}

}
