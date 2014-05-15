package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.UTILITIES.TabsAdapter;
import it.polimi.dima.watchdog.factory.FeatureEnum;
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

		this.mAdapter = new TabsAdapter(getChildFragmentManager(), 3,
				FeatureEnum.GPS);
		this.mViewPager = (ViewPager) v.findViewById(R.id.gps_pager);

		this.mViewPager.setAdapter(this.mAdapter);
		// Inflate the layout for this fragment
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		this.setMyContext((ActionBarActivity) activity);
		super.onAttach(activity);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(GpsMainFragment.TAG);
	}

	//TODO utile???
	public ActionBarActivity getMyContext() {
		return this.myContext;
	}
	
	//TODO utile???
	public void setMyContext(ActionBarActivity myContext) {
		this.myContext = myContext;
	}

}