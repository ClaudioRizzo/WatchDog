package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.gps.GpsMainFragment;
import it.polimi.dima.watchdog.fragments.gps.localization.interfaces.MessageActionListener;
import it.polimi.dima.watchdog.utilities.ListenerUtility;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.drawer.MyDrawerUtility;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;

/**
 * 
 * @author claudio
 * 
 */
public class MainActivity extends ActionBarActivity implements
		MessageActionListener {

	MyDrawerUtility mDrawerUtil;
	static final String ACTION = "android.intent.action.DATA_SMS_RECEIVED";
	private boolean wizardDone;
	private String TAG;

	public String getTag() {
		return this.TAG;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.wizardDone = MyPrefFiles.isWizardDone(this);
		TAG = "main";

		if (this.wizardDone) {

			ListenerUtility.getInstance(this).addListener(this);
			getSupportActionBar().setTitle(R.string.default_tab);
			setContentView(R.layout.activity_main_layout);

			setTheme(R.style.Theme_Hacker);
			this.mDrawerUtil = new MyDrawerUtility();
			this.mDrawerUtil.InitializeDrawerList(this, R.id.drawer_layout,
					R.id.left_drawer);
			this.mDrawerUtil.handleOpenCloseDrawer(this, R.id.drawer_layout);

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
			getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
			getActionBar().hide();
			Intent intent = new Intent(this, WelcomeScreenActivity.class);
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
			intent = new Intent(this, AssociateNumberActivity.class);
			startActivity(intent);
			break;
		case R.id.deassociate_panel:
			intent = new Intent(this, DeassociationActivity.class);
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
		if (this.wizardDone)
			this.mDrawerUtil.syncDrawerToggle();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		if (this.wizardDone)
			this.mDrawerUtil.onConfigurationChangedNeeded(newConfig);
	}

	public void replaceFragment(Fragment fragment, String tag) {
		FragmentTransaction transaction = getSupportFragmentManager()
				.beginTransaction();
		transaction.replace(R.id.main_fragment_container, fragment, tag);
		transaction.addToBackStack(tag);
		transaction.commit();
	}

	@Override
	public void onLocationMessageReceived(double lat, double lon) {

		Log.i("[DEBUG]", "[DEBUG] ricevuta la locazione: tento di cambiare");

		/*
		 * Intent intent = new Intent(this, MyMapActivity.class);
		 * intent.putExtra("latitude", lat); intent.putExtra("longitude", lon);
		 * startActivity(intent);
		 */
		// TODO: start new activity map
		notifyUser(lat, lon);
	}

	private void notifyUser(double lat, double lon) {
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				this).setSmallIcon(R.drawable.ic_launcher)
				.setContentTitle("Localized")
				.setContentText("Your phone has been localized")
				.setAutoCancel(true)
				.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
		Intent resultIntent = new Intent(this, MyMapActivity.class);
		resultIntent.putExtra("latitude", lat);
		resultIntent.putExtra("longitude", lon);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		stackBuilder.addParentStack(PendingRequestsActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager = (NotificationManager) this
				.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.notify(0, mBuilder.build());
	}

	@Override
	public void onSmpOver(String other) {
		Log.i("DEBUG", "DEBUG: dovrei creare il popup");
		Intent intent = new Intent(this, SmpOverActivity.class);
		intent.putExtra("other", other);
		startActivity(intent);
	}

}