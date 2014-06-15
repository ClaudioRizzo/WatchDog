package it.polimi.dima.watchdog.gps.fragment;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.factory.FeatureEnum;
import it.polimi.dima.watchdog.utilities.FragmentAdapterLifecycle;
import it.polimi.dima.watchdog.utilities.TabsAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 
 * @author claudio
 * 
 */
public class GpsMainFragment extends Fragment {

	public static final String TAG = "Gps";
	private ActionBarActivity myContext;
	private ViewPager mViewPager;
	private TabsAdapter mAdapter;

	public interface MapContextInterface {
		void getMapContextChanges();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = (View) inflater.inflate(R.layout.fragment_gps_main, container,
				false);
		this.mAdapter = new TabsAdapter(getChildFragmentManager(), 3,
				FeatureEnum.GPS);
		this.mViewPager = (ViewPager) v.findViewById(R.id.gps_pager);
		this.mViewPager.setAdapter(this.mAdapter);
		mViewPager.setOnPageChangeListener(pageChangeListener);
		return v;
	}

	private OnPageChangeListener pageChangeListener = new OnPageChangeListener() {

		int currentPosition = 0;

		@Override
		public void onPageSelected(int newPosition) {

			FragmentAdapterLifecycle fragmentToShow = (FragmentAdapterLifecycle) mAdapter.instantiateItem(mViewPager, newPosition);
			fragmentToShow.onResumeFragment();

			FragmentAdapterLifecycle fragmentToHide = (FragmentAdapterLifecycle) mAdapter.instantiateItem(mViewPager, currentPosition);
			fragmentToHide.onPauseFragment();

			currentPosition = newPosition;

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}
	};

	@Override
	public void onAttach(Activity activity) {
		this.setMyContext((ActionBarActivity) activity);

		super.onAttach(activity);

	}

	@Override
	public void onResume() {
		super.onResume();
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(
				GpsMainFragment.TAG);
	}

	// TODO utile???
	public ActionBarActivity getMyContext() {
		return this.myContext;
	}

	// TODO utile???
	public void setMyContext(ActionBarActivity myContext) {
		this.myContext = myContext;
	}
}