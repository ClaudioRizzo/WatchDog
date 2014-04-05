package it.polimi.dima.watchdog.fragments;

import it.polimi.dima.watchdog.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class PhoneStatusFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		getActivity().setTitle("Status");
		setHasOptionsMenu(false);// true se vogliamo cambiare dinamicamente la
									// action bar
		return inflater.inflate(R.layout.fragment_phone_status, container,
				false);

	}

	@Override
	public void onResume() {
		super.onResume();
		int title = R.string.status_title;
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(
				title);
	}
}
