package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.actionBar.settingsAction.AssociateNumberFragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class AssociateNumberActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.title_associate_number);
		setContentView(R.layout.activity_associate_number);

		if (findViewById(R.id.associate_number_container) != null) {

			if (savedInstanceState != null) {
				return;
			}

			AssociateNumberFragment mAssNum = new AssociateNumberFragment();

			getSupportFragmentManager().beginTransaction()
					.add(R.id.associate_number_container, mAssNum)
					.commit();
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
}
