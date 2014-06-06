package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.map.MessageActionListener;
import it.polimi.dima.watchdog.fragments.gps.map.MyMapFragment;
import it.polimi.dima.watchdog.utilities.ListenerUtility;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GpsFragmentsContainer extends Fragment implements
		MessageActionListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = (View) inflater.inflate(R.layout.fragment_gps_container,
				container, false);
		
		ListenerUtility.getInstance().addListener(this);
		Log.i("[DEBUG]", "[DEBUG] aggiunto il fragment come listener");
		
		if (savedInstanceState == null) {
			FragmentTransaction trans = getChildFragmentManager()
					.beginTransaction();
			trans.replace(R.id.gps_fragment_container,
					new LocalizationFragment());
			trans.addToBackStack(null);
			trans.commit();
		}

		return v;
	}

	@Override
	public void onLocationMessageReceived(double lat, double lon) {

		Log.i("[DEBUG]", "[DEBUG] ricevuta la locazione tento di cambiare");
		
		Location l = new Location("");
		l.setLatitude(lat);
		l.setLongitude(lon);

		FragmentTransaction trans = getChildFragmentManager()
				.beginTransaction();
		MyMapFragment mapFrag = new MyMapFragment(l);
		
		trans.replace(R.id.gps_fragment_container, mapFrag);
		trans.addToBackStack(null);
		trans.commitAllowingStateLoss();

	}

}
