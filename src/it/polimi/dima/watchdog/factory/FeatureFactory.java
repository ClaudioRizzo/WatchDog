package it.polimi.dima.watchdog.factory;

import android.support.v4.app.Fragment;

public abstract class FeatureFactory {

	public static FeatureFactory getFactory(FeatureEnum e) {
		switch(e) {
		case GPS:
			return new GpsFactory();
		case REMOTE:
			return new SmsRemoteFactory();
		}
		return null;
	}
	/**
	 * In base alla posizione nel tab genera il fragment corrispondente:
	 * Ad esepio se in Tab0 abbiamo un fragment locazione, questo verrà generato
	 * @param pos
	 * @return
	 */
	public abstract Fragment getFragment(int pos);
}