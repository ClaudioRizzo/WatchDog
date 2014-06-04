package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GpsFragmentsContainer extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = (View) inflater.inflate(R.layout.fragment_gps_container,
				container, false);
		
		LocalizationFragment mLocFrag = new LocalizationFragment();

		getChildFragmentManager().beginTransaction().add(
				R.id.gps_fragment_container, mLocFrag).commit();

		return v;
	}

}
