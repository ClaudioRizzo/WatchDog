package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.MyDrawerUtility;
import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.GpsMainFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	MyDrawerUtility mDrawerUtil;
	static final String ACTION = "android.intent.action.DATA_SMS_RECEIVED";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Restore preferences false if doesn't exist
		SharedPreferences settings = getSharedPreferences(
				MyPrefFiles.PREF_INIT, Context.MODE_PRIVATE);
		boolean wizardDone = settings.getBoolean(MyPrefFiles.WIZARD_DONE, false);
		getSupportActionBar().setTitle(R.string.default_tab);
		setContentView(R.layout.activity_main_layout);

		mDrawerUtil = new MyDrawerUtility();

		mDrawerUtil.InitializeDrawerList(this, R.id.drawer_layout,
				R.id.left_drawer);
		mDrawerUtil.handleOpenCloseDrawer(this, R.id.drawer_layout);
		

		if (wizardDone) {
			if (findViewById(R.id.main_fragment_container) != null) {

				if (savedInstanceState != null) {
					return;
				}

				// la view di default sarà per noi quella con le gps features
				GpsMainFragment mGpsMainFrag = new GpsMainFragment();

				getSupportFragmentManager().beginTransaction()
						.add(R.id.main_fragment_container, mGpsMainFrag)
						.commit();
			}
		} else {
			Intent intent = new Intent(this, InitializationWizardActivity.class);
			startActivity(intent);
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
	public boolean onOptionsItemSelected(MenuItem item) {

		mDrawerUtil.getOnOptionsItemSelected(item);
		// Handle presses on the action bar items
		/*
		 * switch (item.getItemId()) { case R.id.gps_icon: replaceFragment(new
		 * GpsMainFragment()); return true; case R.id.sms_icon:
		 * replaceFragment(new SmsRemoteMainFragment()); return true; default:
		 * return super.onOptionsItemSelected(item); }
		 */

		/*if (item.getItemId() == R.id.prova_sms) {
			SmsManager man = SmsManager.getDefault();
			man.sendDataMessage("+393466342499", null, (short) 7777,
					"ciao".getBytes(), null, null);

		}*/
		
		switch (item.getItemId()) {
		case R.id.settings_panel:
			Intent intent = new Intent(this, SettingsActivity.class);
			startActivity(intent);
		default:
			return super.onOptionsItemSelected(item);

		}
		
		
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerUtil.syncDrawerToggle();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerUtil.onConfigurationChangedNeeded(newConfig);
	}

	public void replaceFragment(Fragment fragment, String tag) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.main_fragment_container, fragment, tag);
		transaction.addToBackStack(tag);
		transaction.commit();
	}

}
