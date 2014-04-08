package it.polimi.dima.watchdog.factory;

import it.polimi.dima.watchdog.fragments.smsRemote.SirenOfFragment;
import it.polimi.dima.watchdog.fragments.smsRemote.SirenOnFragment;
import android.support.v4.app.Fragment;

public class SmsRemoteFactory extends FeatureFactory {

	@Override
	public Fragment getFragment(int pos) {
		switch(pos) {
		case 0:
			return new SirenOnFragment();
		case 1: 
			return new SirenOfFragment();
		
		}
		throw new IllegalStateException("Per il Remote Control questa posizione non esiste!");
	}

	
}
