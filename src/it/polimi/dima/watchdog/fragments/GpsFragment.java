package it.polimi.dima.watchdog.fragments;

import it.polimi.dima.watchdog.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

public class GpsFragment extends Fragment {

	public static String GPS_MESSAGE = "message";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		setHasOptionsMenu(false);// true se vogliamo cambiare dinamicamente la
									// action bar
		return inflater.inflate(R.layout.fragment_gps, container, false);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Add your menu entries here

		super.onCreateOptionsMenu(menu, inflater);
		

	}

	@Override
	public void onResume() {
		super.onResume();
		int title = R.string.gps_title;
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(
				title);
	}
}
