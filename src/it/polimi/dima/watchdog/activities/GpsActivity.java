package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.MyDrawerUtility;
import it.polimi.dima.watchdog.R;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class GpsActivity extends ActionBarActivity {

	private MyDrawerUtility mDrawerUtils;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);
		setContentView(R.layout.activity_gps_layout);
		this.setTitle("GPS");
		System.out.println("ON CREATE");
		mDrawerUtils = new MyDrawerUtility();
		mDrawerUtils.InitializeDrawerList(this, R.id.gps_drawer_layout,
				R.id.gps_left_drawer);

		mDrawerUtils.handleOpenCloseDrawer(this, R.id.gps_drawer_layout);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * use to handle menu item selected on the action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerUtils.getOnOptionsItemSelected(item)) {
	          return true;
	        }
		
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerUtils.syncDrawerToggle();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerUtils.onConfigurationChangedNeeded(newConfig);
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		System.out.println("ON START");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		System.out.println("ON RESUME");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		System.out.println("ON PAUSE");
	}
	
	@Override
	public void	onStop(){
		super.onStop();
		System.out.println("ON STOP");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		System.out.println("ON DESTROY");
	}
}
