package it.polimi.dima.watchdog.factory;

import android.support.v4.app.Fragment;

/**
 * 
 * @author claudio
 *
 */
public abstract class FeatureFactory {

	public static FeatureFactory getFactory(FeatureEnum e) {
		switch(e) {
		case GPS:
			return new GpsFactory();
		case SIREN:
			return new SmsSirenFactory();
		}
		return null;
	}
	
	/**
	 * In base alla posizione nel tab genera il fragment corrispondente: ad esepio se in Tab0 abbiamo un
	 * fragment locazione, questo verrà generato.
	 * 
	 * @param pos
	 * @return il fragment corrispondente
	 */
	public abstract Fragment getFragment(int pos);
}