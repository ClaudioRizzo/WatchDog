package it.polimi.dima.watchdog.activities;

import java.security.InvalidAlgorithmParameterException;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.crypto.ECKeyPairGeneratorWrapper;
import it.polimi.dima.watchdog.fragments.wizard.InitializeWizardFragment;
import it.polimi.dima.watchdog.fragments.wizard.InitializeWizardFragment.OnPasswordInizializedListener;
import android.content.Context;
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
		SharedPreferences keys = getSharedPreferences(MyPrefFiles.MY_KEYS, Context.MODE_PRIVATE);
		SharedPreferences password = getSharedPreferences(MyPrefFiles.PASSWORD_AND_SALT, Context.MODE_PRIVATE);
		SharedPreferences wizard = getSharedPreferences(MyPrefFiles.PREF_INIT, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor1 = keys.edit();
		SharedPreferences.Editor editor2 = password.edit();
		SharedPreferences.Editor editor3 = wizard.edit();
		ECKeyPairGeneratorWrapper mkeyGen = new ECKeyPairGeneratorWrapper();
		try {
			mkeyGen.generateKeyPair();
		} catch (InvalidAlgorithmParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] pubKeyBytes = mkeyGen.getPublicKey().getEncoded();
		byte[] privateKeyBytes = mkeyGen.getPrivateKey().getEncoded();
		String pubKey = Base64.encodeToString(pubKeyBytes, Base64.DEFAULT);
		String privateKey = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);
		
		//saving preferences
		editor1.putString(MyPrefFiles.MY_PUB, pubKey);
		editor1.putString(MyPrefFiles.MY_PRIV, privateKey);
		editor2.putString(MyPrefFiles.MY_PASSWORD_HASH, hashToSave);
		editor2.putString(MyPrefFiles.MY_PASSWORD_SALT, salt);
		editor3.putBoolean(MyPrefFiles.WIZARD_DONE, wizardDone);
		
		editor1.commit();
		editor2.commit();
		editor3.commit();
		
		//start MainActivity
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}
	
	
}
