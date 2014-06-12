package it.polimi.dima.watchdog.fragments.smsRemote;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.utilities.FragmentAdapterLifecycle;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author claudio
 *
 */
public class SirenOffFragment extends Fragment implements FragmentAdapterLifecycle {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_siren_off, container, false);
    }

	@Override
	public void onResumeFragment() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPauseFragment() {
		// TODO Auto-generated method stub
		
	}
}