package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.PasswordUtils;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.crypto.ECKeyPairGenerator;
import it.polimi.dima.watchdog.fragments.wizard.InitializeWizardFragment;
import it.polimi.dima.watchdog.fragments.wizard.InitializeWizardFragment.OnPasswordInizializedListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;

//TODO: verificare che la password inserita rispetti i nostri standard di sicurezza
//da verificare

public class InitializationWizardActivity extends ActionBarActivity implements
		OnPasswordInizializedListener {


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_initialize_wizard);

		if (findViewById(R.id.initialize_wizard_container) != null) {

			if (savedInstanceState != null) {
				return;
			}
		}

		InitializeWizardFragment mIniWizFrag = new InitializeWizardFragment();
		getSupportFragmentManager().beginTransaction()
				.add(R.id.initialize_wizard_container, mIniWizFrag).commit();

	}


	@Override
	public void getWizardChanges(boolean wizardDone, String hashToSave, String salt) {
		SharedPreferences settings = getSharedPreferences(MyPrefFiles.PREF_INIT, 0);
		SharedPreferences.Editor editor = settings.edit();
		ECKeyPairGenerator mkeyGen = new ECKeyPairGenerator();
		mkeyGen.generateKeyPair();
		
		byte[] pubKeyBytes = mkeyGen.getPublicKey().getEncoded();
		byte[] privateKeyBytes = mkeyGen.getPrivateKey().getEncoded();
		String pubKey = Base64.encodeToString(pubKeyBytes, Base64.DEFAULT);
		String privateKey = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);
		
		//saving preferences
		editor.putBoolean(MyPrefFiles.WIZARD_DONE, wizardDone);
		editor.putString(MyPrefFiles.PSSWD_HASH_SALTED, hashToSave);
		editor.putString(MyPrefFiles.SALT, salt);
		
		
		editor.putString(MyPrefFiles.PUB_KEY, pubKey);
		editor.putString(MyPrefFiles.PRI_KEY, privateKey);
		
		editor.commit();
		
		//start MainActivity
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	
}