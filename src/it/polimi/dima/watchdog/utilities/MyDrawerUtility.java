package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.activities.MainActivity;
import it.polimi.dima.watchdog.fragments.gps.GpsMainFragment;
import it.polimi.dima.watchdog.fragments.smsRemote.SmsRemoteMainFragment;
import android.content.res.Configuration;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 
 * @author claudio
 *
 */
public class MyDrawerUtility {

	private String[] mGenFeatures;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle = GpsMainFragment.TAG; //default value

	/**
	 * Questo metodo permette di inizializzare un drawer nella sua visualizzazione.
	 * 
	 * @param mActivity: L'attività nella quale si vuole implementare il drawer
	 * @param drawerLayout: L'id del Drawer layout per l'attività considerata
	 * @param drawerList: la list view implementata nel layout dell'attività in questione
	 */
	public void InitializeDrawerList(ActionBarActivity mActivity, int drawerLayout, int drawerList) {
		
		mGenFeatures = mActivity.getResources().getStringArray(R.array.gen_features_array);
		mDrawerLayout = (DrawerLayout) mActivity.findViewById(drawerLayout);
		mDrawerList = (ListView) mActivity.findViewById(drawerList);

		mDrawerList.setAdapter(new ArrayAdapter<String>(mActivity, R.layout.drawer_list_item, mGenFeatures));

		mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		mActivity.getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener(mActivity));
	}

	/**
	 * Metodo da utilizzare qualora si vuole gestire l'apertura e la chiusura del drawer.
	 * 
	 * @param mActivity : l'attività in questione
	 * @param mDrawerlayout : l'id del drawer layout implementato nell'activity layout
	 */
	public void handleOpenCloseDrawer(final ActionBarActivity mActivity, int mDrawerlayout) {
		mDrawerLayout = (DrawerLayout) mActivity.findViewById(mDrawerlayout);
		mDrawerToggle = new ActionBarDrawerToggle(mActivity, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				mActivity.getSupportActionBar().setTitle(mTitle);
				mActivity.supportInvalidateOptionsMenu();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				mActivity.setTitle(R.string.app_name);
				mDrawerTitle = mActivity.getTitle();
				mActivity.getSupportActionBar().setTitle(mDrawerTitle);
				mActivity.supportInvalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void syncDrawerToggle() {
		mDrawerToggle.syncState();
	}

	public void onConfigurationChangedNeeded(Configuration newConfig) {
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public boolean getOnOptionsItemSelected(MenuItem item) {
		return mDrawerToggle.onOptionsItemSelected(item);
	}

	/* The click listner for ListView in the navigation drawer */
	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		private ActionBarActivity activity;
		private DrawerItemClickListener(ActionBarActivity activity) {
			this.activity = activity;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			/*
			 * In questo metodo si gestiscono i click nel menu del drawer
			 */
			MainActivity mainActivity;
			if (activity instanceof MainActivity) {
				mainActivity = (MainActivity) activity;
			} else {
				throw new IllegalArgumentException("Non è una MainActivity");
			}

			switch (position) {
			case 0:
				mainActivity.replaceFragment(new GpsMainFragment(), GpsMainFragment.TAG);
				mTitle = GpsMainFragment.TAG;
				mDrawerLayout.closeDrawers();
				break;
			case 1:
				mainActivity.replaceFragment(new SmsRemoteMainFragment(), SmsRemoteMainFragment.TAG);
				mTitle = SmsRemoteMainFragment.TAG;
				mDrawerLayout.closeDrawers();
				break;
			default:
				break;
			}
		}
	}
}
