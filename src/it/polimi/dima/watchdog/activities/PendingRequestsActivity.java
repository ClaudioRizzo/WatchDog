package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.actionBar.PendingRequestsFragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

/**
 * 
 * @author claudio
 *
 */
public class PendingRequestsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pending_requests);

		if (findViewById(R.id.pending_requests_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
		}
		
		
		PendingRequestsFragment mPendReqFrag = new PendingRequestsFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.pending_requests_container, mPendReqFrag).commit();
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