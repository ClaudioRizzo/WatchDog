package it.polimi.dima.watchdog.factory;

import it.polimi.dima.watchdog.FeaturesEnum;
import android.support.v4.app.Fragment;

/**
 * Questa classe è utile alla gestione delle tabs dell'app, dispaccia a seconda
 * della feature la factory corrispondente
 * @author claudio
 *
 */
public abstract class FeaturesFactory {

	/**
	 * ritorna una factory di feature corrispondenta alla FeatureEnum
	 * @param feature: tipo di factory desiderata
	 * @return
	 */
	public static FeaturesFactory getFeature(FeaturesEnum feature) {
		switch (feature) {
		case GPS:
			return new GpsFactory();
		case SMS_REMOTE:
			return new SmsRemoteFactory();
		default:
			return null;
		}
	}
	
	public abstract Fragment getFragment(int id);
	
}
