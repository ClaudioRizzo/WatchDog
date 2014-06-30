package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.actionBar.DeassociationFragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

public class DeassociationActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle("Deassociation");
		setContentView(R.layout.activity_deassociate);
		
		setTheme(R.style.Theme_Hacker);
		
		if (findViewById(R.id.deassociate_number_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
			DeassociationFragment deassFragment = new DeassociationFragment();
			getSupportFragmentManager().beginTransaction().add(R.id.deassociate_number_container, deassFragment).commit();
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	} 
}