package it.polimi.dima.watchdog.factory;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.SirenOnFragment;
import android.support.v4.app.Fragment;

/**
 * dispaccia i fragment relativi alla funzionalità del controllo in remoto via sms
 * @author claudio
 *
 */
public class SmsRemoteFactory extends FeaturesFactory {

	@Override
	public Fragment getFragment(int id) {
		switch (id) {
		case R.id.tab1_root_frame:
			return new SirenOnFragment();
		case R.id.tab2_root_frame:
			return new SirenOnFragment();
		default:
			return null;
		}
	}

}
