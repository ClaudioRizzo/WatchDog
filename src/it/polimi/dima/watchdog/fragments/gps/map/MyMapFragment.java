package it.polimi.dima.watchdog.fragments.gps.map;

import it.polimi.dima.watchdog.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




public class MyMapFragment extends Fragment {

	public static String TAG = "MAP_FRAGMENT";
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		
        View v = inflater.inflate(R.layout.fragment_mymap, container, false);
        return v;
    }
}
