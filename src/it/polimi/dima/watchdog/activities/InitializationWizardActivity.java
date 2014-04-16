package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class InitializationWizardActivity extends ActionBarActivity {

	public final static String PREFS_NAME = "MyPrefFile";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean("wizardDone", true);
		
		editor.commit();
		
		if (findViewById(R.id.initialize_wizard_container) != null) {

			if (savedInstanceState != null) {
				return;
			}
		}
		
	}
}
