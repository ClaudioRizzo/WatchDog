package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.GpsMainFragment;
import it.polimi.dima.watchdog.fragments.smsRemote.SmsRemoteMainFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		if (findViewById(R.id.main_fragment_container) != null) {

			if (savedInstanceState != null) {
				return;
			}

			// la view di default sarà per noi quella con le gps features
			GpsMainFragment mGpsMainFrag = new GpsMainFragment();

			getSupportFragmentManager().beginTransaction()
					.add(R.id.main_fragment_container, mGpsMainFrag).commit();
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
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.gps_icon:
	            replaceFragment(new GpsMainFragment());
	            return true;
	        case R.id.sms_icon:
	            replaceFragment(new SmsRemoteMainFragment());
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void replaceFragment(Fragment fragment) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.main_fragment_container, fragment);
		transaction.addToBackStack(null);
		transaction.commit();
	}

}
