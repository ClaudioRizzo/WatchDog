package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.TabsAdapter;
import it.polimi.dima.watchdog.factory.FeatureEnum;
import it.polimi.dima.watchdog.fragments.smsRemote.SmsRemoteMainFragment;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GpsMainFragment extends Fragment {

	public static final String TAG = "Gps";
	
	private ActionBarActivity myContext;
	private ViewPager mViewPager;
	private TabsAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = (View) inflater.inflate(R.layout.fragment_gps_main, container,
				false);

		mAdapter = new TabsAdapter(getChildFragmentManager(), 3,
				FeatureEnum.GPS);
		mViewPager = (ViewPager) v.findViewById(R.id.gps_pager);

		mViewPager.setAdapter(mAdapter);
		// Inflate the layout for this fragment
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		this.myContext = (ActionBarActivity) activity;
		super.onAttach(activity);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(GpsMainFragment.TAG);
	}

}