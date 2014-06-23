package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.wizard.WelcomeScreenFragment;
import android.app.Activity;
import android.os.Bundle;

public class WelcomeScreenActivity extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialize_wizard);
		
		setTheme(R.style.Theme_Hacker);

		if (findViewById(R.id.initialize_wizard_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
		}
		WelcomeScreenFragment mIniWizFrag = new WelcomeScreenFragment();
		getFragmentManager().beginTransaction().add(R.id.initialize_wizard_container, mIniWizFrag).commit();
	}
}