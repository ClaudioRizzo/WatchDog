package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class InitializationWizardActivity extends ActionBarActivity {

	public final static String PREFS_NAME = "MyPrefsFile";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (findViewById(R.id.initialize_wizard_container) != null) {

			if (savedInstanceState != null) {
				return;
			}
		}
		
	}
}
