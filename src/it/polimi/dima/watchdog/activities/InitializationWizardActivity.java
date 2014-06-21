package it.polimi.dima.watchdog.activities;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.crypto.ECKeyPairGeneratorWrapper;
import it.polimi.dima.watchdog.fragments.wizard.InitializeWizardFragment;
import it.polimi.dima.watchdog.fragments.wizard.InitializeWizardFragment.OnPasswordInizializedListener;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;

//TODO: verificare che la password inserita rispetti i nostri standard di sicurezza
/**
 * Classe che rappresenta l'attività del wizard iniziale dell'applicazione.
 * 
 * @author claudio, emanuele
 *
 */
public class InitializationWizardActivity extends ActionBarActivity implements OnPasswordInizializedListener {

	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getApplicationContext();
		setContentView(R.layout.activity_initialize_wizard);
		
		setTheme(R.style.Theme_Hacker);

		if (findViewById(R.id.initialize_wizard_container) != null) {
			if (savedInstanceState != null) {
				return;
			}
		}
		InitializeWizardFragment mIniWizFrag = new InitializeWizardFragment();
		getSupportFragmentManager().beginTransaction().add(R.id.initialize_wizard_container, mIniWizFrag).commit();
	}

	@Override
	public void saveWizardResults(boolean wizardDone, byte[] hashToSave, byte[] salt) throws NoSuchAlgorithmException, NoSuchProviderException {
		ECKeyPairGeneratorWrapper mkeyGen = new ECKeyPairGeneratorWrapper();
		mkeyGen.generateKeyPair();
		byte[] pubKeyBytes = mkeyGen.getPublicKey().getEncoded();
		byte[] privateKeyBytes = mkeyGen.getPrivateKey().getEncoded();
		String pubKey = Base64.encodeToString(pubKeyBytes, Base64.DEFAULT);
		String privateKey = Base64.encodeToString(privateKeyBytes, Base64.DEFAULT);
		
		//saving preferences
		MyPrefFiles.setMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, pubKey, this.context);
		MyPrefFiles.setMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, privateKey, this.context);
		MyPrefFiles.setMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_HASH, Base64.encodeToString(hashToSave, Base64.DEFAULT), this.context);
		MyPrefFiles.setMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_SALT, Base64.encodeToString(salt, Base64.DEFAULT), this.context);
		MyPrefFiles.setWizardDone(this.context);
		
		//start MainActivity
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}	
}