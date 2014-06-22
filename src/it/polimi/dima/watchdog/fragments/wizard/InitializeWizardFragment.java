package it.polimi.dima.watchdog.fragments.wizard;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.password.PasswordUtils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class InitializeWizardFragment extends Fragment implements OnClickListener {

	private OnPasswordInizializedListener mCallBack;
	private final byte[] salt = PasswordUtils.nextSalt();

	public interface OnPasswordInizializedListener {
		public void saveWizardResults(boolean wizardDone, byte[] hashToSave, byte[] salt) throws NoSuchAlgorithmException, NoSuchProviderException;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = (View) inflater.inflate(R.layout.fragment_initialize_wizard, container, false);
		Button mButton = (Button) v.findViewById(R.id.button_initialize_password);
		mButton.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		try{
			View fragView = getView();
			TextView mTextView = (TextView) fragView.findViewById(R.id.user_password);
			String cleanPassword = mTextView.getText().toString();
			
			//TODO scommentare alla fine
			if(!Pattern.matches(/*PasswordUtils.PASSWORD_REGEX*/ ".+", cleanPassword)){
				ErrorManager.handleNonFatalError(ErrorFactory.BAD_PASSWORD);
			}
			else{
				byte[] hashToSave = PasswordUtils.computeHash(cleanPassword.getBytes(), this.salt, CryptoUtility.SHA_256);
				this.mCallBack.saveWizardResults(true, hashToSave, this.salt);
			}
		}
		catch (NoSuchAlgorithmException e)
		{
			ErrorManager.handleFatalError(ErrorFactory.WIZARD_ERROR);
		}
		catch (NoSuchProviderException e)
		{
			ErrorManager.handleFatalError(ErrorFactory.WIZARD_ERROR);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			this.mCallBack = (OnPasswordInizializedListener) activity;
		} 
		catch (ClassCastException e) 
		{
			ErrorManager.handleFatalError(e.getMessage());
		}
	}
}