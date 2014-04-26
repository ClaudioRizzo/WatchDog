package it.polimi.dima.watchdog.fragments.wizard;

import it.polimi.dima.watchdog.PasswordUtils;
import it.polimi.dima.watchdog.R;

import java.security.NoSuchAlgorithmException;
//TODO scommentare alla fine
//import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class InitializeWizardFragment extends Fragment implements
		OnClickListener {

	private OnPasswordInizializedListener mCallBack;
	private final String salt = Base64.encodeToString(PasswordUtils.nextSalt(), Base64.DEFAULT);

	public interface OnPasswordInizializedListener {
		public void getWizardChanges(boolean wizardDone, String hashToSave, String salt);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = (View) inflater.inflate(R.layout.fragment_initialize_wizard,
				container, false);

		Button mButton = (Button) v
				.findViewById(R.id.button_initialize_password);
		mButton.setOnClickListener(this);

		return v;

	}

	@Override
	public void onClick(View v) {
		View fragView = getView();
		TextView mTextView = (TextView) fragView
				.findViewById(R.id.user_password);
		String cleanPassword = mTextView.getText().toString();
		
		//TODO scommentare alla fine
		/*while(!Pattern.matches(PasswordUtils.PASSWORD_REGEX, cleanPassword)){
			//TODO notificare che la password può essere composta solo da lettere maiuscole e minuscole e numeri
			//e che deve contenere almeno un numero e almeno una lettera (maiuscola o minuscola non ha importanza)
			//e che deve essere lunga almeno 8 caratteri e non più di 20. Quindi chiedere di immetterla di nuovo
			//e salvarla di nuovo in cleanPassword
		}*/
		String hashToSave = this.getHash(cleanPassword);
		mCallBack.getWizardChanges(true, hashToSave, this.salt);

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		try {
			this.mCallBack = (OnPasswordInizializedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ "must implement OnPasswordInizializedListener");
		}
	}

	/**
	 * prende la password la sala e ne fa l'hash, ritorna la stringa
	 * rappresentant el'hash
	 * 
	 * @param pswd
	 * @return
	 */
	private String getHash(String pswd) {
		String saltedPswd = pswd + this.salt;
		String hashString = null;

		try {
			byte[] hash = PasswordUtils.getByteHash(saltedPswd, "SHA-256");
			hashString = Base64.encodeToString(hash, Base64.DEFAULT);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return hashString;

	}

}
