package it.polimi.dima.watchdog.factory;

import it.polimi.dima.watchdog.fragments.smsRemote.SirenOffFragment;
import it.polimi.dima.watchdog.fragments.smsRemote.SirenOffLocalFragment;
import it.polimi.dima.watchdog.fragments.smsRemote.SirenOnFragment;
import android.support.v4.app.Fragment;

/**
 * 
 * @author claudio
 *
 */
public class SmsRemoteFactory extends FeatureFactory {

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