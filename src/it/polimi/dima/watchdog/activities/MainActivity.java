package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.gps.fragment.GpsMainFragment;
import it.polimi.dima.watchdog.gps.fragments.localization.interfaces.MessageActionListener;
import it.polimi.dima.watchdog.utilities.ListenerUtility;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.drawer.MyDrawerUtility;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

/**
 * 
 * @author claudio
 *
 */
public class MainActivity extends ActionBarActivity implements MessageActionListener {

	private Context context;
	MyDrawerUtility mDrawerUtil;
	static final String ACTION = "android.intent.action.DATA_SMS_RECEIVED";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getApplicationContext();
		
		ListenerUtility.getInstance().addListener(this);
		boolean wizardDone = MyPrefFiles.isWizardDone(this.context);
		getSupportActionBar().setTitle(R.string.default_tab);
		setContentView(R.layout.activity_main_layout);
		
		setTheme(R.style.Theme_Hacker);
		
		this.mDrawerUtil = new MyDrawerUtility();
		this.mDrawerUtil.InitializeDrawerList(this, R.id.drawer_layout,R.id.left_drawer);
		this.mDrawerUtil.handleOpenCloseDrawer(this, R.id.drawer_layout);
		
		if (wizardDone) {
			if (findViewById(R.id.main_fragment_container) != null) {
				if (savedInstanceState != null) {
					return;
				}
				// la view di default sarà per noi quella con le gps features
				GpsMainFragment mGpsMainFrag = new GpsMainFragment();
				getSupportFragmentManager().beginTransaction().add(R.id.main_fragment_container, mGpsMainFrag).commit();
			}
			//cancellare TODO
			/*Intent intent = new Intent(getApplicationContext(),SirenService.class);
			intent.putExtra(SMSUtility.COMMAND, SMSUtility.SIREN_ON);
			getApplicationContext().startService(intent);*/
		} else {
			Intent intent = new Intent(this, InitializationWizardActivity.class);
			startActivity(intent);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		this.mDrawerUtil.getOnOptionsItemSelected(item);
		
		Intent intent;
		
		switch (item.getItemId()) {
		case R.id.change_password_panel:
			intent = new Intent(this, ChangePasswordActivity.class);
			startActivity(intent);
			break;
		case R.id.pending_requests_panel:
			intent = new Intent(this, PendingRequestsActivity.class);
			startActivity(intent);
			break;
		case R.id.associate_panel:
			intent= new Intent(this, AssociateNumberActivity.class);
			startActivity(intent);
			break;
			
		default:
			break;
		}	
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		this.mDrawerUtil.syncDrawerToggle();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		this.mDrawerUtil.onConfigurationChangedNeeded(newConfig);
	}

	public void replaceFragment(Fragment fragment, String tag) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(R.id.main_fragment_container, fragment, tag);
		transaction.addToBackStack(tag);
		transaction.commit();
	}

	@Override
	public void onLocationMessageReceived(double lat, double lon) {

		Log.i("[DEBUG]", "[DEBUG] ricevuta la locazione: tento di cambiare");

		
		Intent intent = new Intent(this, MyMapActivity.class);
		intent.putExtra("latitude", lat);
		intent.putExtra("longitude", lon);
		startActivity(intent);
		//TODO: start new activity map
	}
	

}