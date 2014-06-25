package it.polimi.dima.watchdog.factory;

import it.polimi.dima.watchdog.fragments.siren.SirenOffFragment;
import it.polimi.dima.watchdog.fragments.siren.SirenOffLocalFragment;
import it.polimi.dima.watchdog.fragments.siren.SirenOnFragment;
import android.support.v4.app.Fragment;

/**
 * 
 * @author claudio
 *
 */
public class SmsSirenFactory extends FeatureFactory {

	@Override
	public Fragment getFragment(int pos) {
		switch(pos) {
		case 0:
			return new SirenOnFragment();
		case 1: 
			return new SirenOffFragment();
		case 2:
			return new SirenOffLocalFragment();
		}
		throw new IllegalStateException("Per il Remote Control questa posizione non esiste!");
	}
}