package it.polimi.dima.watchdog.activities;

import java.util.List;

import it.polimi.dima.watchdog.Consts;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.GpsFragment;
import it.polimi.dima.watchdog.fragments.MainFeaturesFragment;
import it.polimi.dima.watchdog.fragments.MainFeaturesFragment.OnFeatureSelectedListener;
import it.polimi.dima.watchdog.fragments.PhoneStatusFragment;
import it.polimi.dima.watchdog.fragments.SmsFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class MainActivity extends ActionBarActivity implements
		OnFeatureSelectedListener {

	private String[] mGeneralFeaturesTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;

	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mDrawerTitle = getTitle();
		mGeneralFeaturesTitles = getResources().getStringArray(
				R.array.gen_features_array);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// Set the adapter for the list view
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mGeneralFeaturesTitles));

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		// enable ActionBar app icon to behave as action to toggle nav drawer
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			/* Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
				int cTitle = getCurrentFragmentTitle();
				if(cTitle != R.string.app_name)
					getSupportActionBar().setTitle(cTitle);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}

			/* Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				getSupportActionBar().setTitle(mDrawerTitle);
				supportInvalidateOptionsMenu(); // creates call to
												// onPrepareOptionsMenu()
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);
		MainFeaturesFragment fragment = new MainFeaturesFragment();
		Bundle args = new Bundle();
		args.putInt(Consts.TITLE, R.string.app_name);
		fragment.setArguments(args);

		if (savedInstanceState == null) {

			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_features_container, fragment, "").commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
		// If the nav drawer is open, hide action items related to the content
		// view
		/*
		 * boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		 * menu.findItem(R.id.action_websearch).setVisible(!drawerOpen); return
		 * super.onPrepareOptionsMenu(menu);
		 */

		// per nascondere gli elementi nella barra applicativa qualora il drawer
		// venga
		// aperto
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		if (id == R.id.settings_panel) {
			System.out.println("Ho cliccato il setting panel");
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	

	/* Drawer List listener */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			switch (position) {
			case 0:
				mDrawerLayout.closeDrawers();
				replaceFragment(new MainFeaturesFragment(), R.string.app_name);
				break;
			case 1:
				mDrawerLayout.closeDrawers();
				replaceFragment(new GpsFragment(), R.string.gps_title);
				break;
			case 2:
				mDrawerLayout.closeDrawers();
				replaceFragment(new SmsFragment(), R.string.remote_title);
				break;
			case 3:
				mDrawerLayout.closeDrawers();
				replaceFragment(new PhoneStatusFragment(), R.string.status_title);
				break;
			default:
				mDrawerLayout.closeDrawers();
				break;
			}
			
			// qua gestiamo i click sugli elementi del menu' che appare a
			// sinistra NB a seconda di avere un drawer in ogni attività
			// possiamo pensare di avere una classe che gestisce il drawer

		}

	}

	@Override
	public void onGpsFeatureSelected() {
		replaceFragment(new GpsFragment(), R.string.gps_title);
	}

	@Override
	public void onPhoneStatusFeatureSelected() {
		replaceFragment(new PhoneStatusFragment(), R.string.status_title);

	}

	@Override
	public void onSmsRemoteFeatureSelected() {
		replaceFragment(new SmsFragment(),R.string.remote_title);

	}

	
	private void replaceFragment(Fragment frag, int title) {
		
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		Bundle args = new Bundle();
		args.putInt(Consts.TITLE, title);
		frag.setArguments(args);
		//Replace whatever is in the fragment_container view with this fragment,
		// and add the transaction to the back stack so the user can navigate back
		transaction.replace(R.id.main_features_container, frag);
		transaction.addToBackStack(null);

		// Commit the transaction
		transaction.commit();
	}

	private int getCurrentFragmentTitle() {
		FragmentManager fMan = getSupportFragmentManager();
		List<Fragment> fragments = fMan.getFragments();
		
		for(Fragment f : fragments) {
			if(f != null && f.isVisible()) {
				return f.getArguments().getInt(Consts.TITLE);
			} 
		}
		return -1;
	}
}
