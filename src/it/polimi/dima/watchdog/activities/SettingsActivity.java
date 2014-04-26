package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.actionBar.SettingsFragment;
import it.polimi.dima.watchdog.fragments.actionBar.SettingsFragment.MyOnListItemClicked;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

/**
 * Setting activity
 * 
 * @author claudio
 * 
 */
public class SettingsActivity extends ActionBarActivity implements MyOnListItemClicked {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.settings_title);
		setContentView(R.layout.activity_settings_layout);

		if (findViewById(R.id.settings_container) != null) {
			if (savedInstanceState != null) {
				return;
			}

			SettingsFragment mSettingsFrag = new SettingsFragment();

			getSupportFragmentManager().beginTransaction()
					.add(R.id.settings_container, mSettingsFrag).commit();
		}

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		// Respond to the action bar's Up/Home button
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void getClickOnAsscociateNumber() {
		Intent intent = new Intent(this, AssociateNumberActivity.class);
		startActivity(intent);
		
	}

}