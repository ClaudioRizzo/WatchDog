package it.polimi.dima.watchdog.fragments.siren;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.factory.FeatureEnum;
import it.polimi.dima.watchdog.utilities.tabsAdapters.SirenTabsAdapter;
import it.polimi.dima.watchdog.utilities.tabsAdapters.TabsAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author claudio
 *
 */
public class SirenMainFragment extends Fragment {

	public static final String TAG = "Siren";
	private ViewPager mViewPager;
	private TabsAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = (View) inflater.inflate(R.layout.fragment_sms_siren_main, container, false);
		this.mAdapter = new SirenTabsAdapter(getChildFragmentManager(), 3, FeatureEnum.SIREN, getActivity());
		this.mViewPager = (ViewPager) v.findViewById(R.id.sms_siren_pager);
		this.mViewPager.setAdapter(this.mAdapter);
		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(SirenMainFragment.TAG);
	}
}