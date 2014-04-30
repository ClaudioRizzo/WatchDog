package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.actionBar.PendingRequestsFragment;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

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
		
		//TODO: creare il fragment da inserire nell'activity e cercare di capire che fare una lista dinamica usando listview e un adapter
		PendingRequestsFragment mPendReqFrag = new PendingRequestsFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.pending_requests_container, mPendReqFrag).commit();

	}
	
}
