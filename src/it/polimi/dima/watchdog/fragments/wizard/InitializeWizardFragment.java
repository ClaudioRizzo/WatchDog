package it.polimi.dima.watchdog.fragments.wizard;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.utilities.CryptoUtility;
import it.polimi.dima.watchdog.utilities.PasswordUtils;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

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
		public void getWizardChanges(boolean wizardDone, byte[] hashToSave, byte[] salt) throws NoSuchAlgorithmException, NoSuchProviderException;
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
			/*while(!Pattern.matches(PasswordUtils.PASSWORD_REGEX, cleanPassword)){
				//TODO notificare che la password può essere composta solo da lettere maiuscole e minuscole e numeri
				//e che deve contenere almeno un numero e almeno una lettera (maiuscola o minuscola non ha importanza)
				//e che deve essere lunga almeno 8 caratteri e non più di 20. Quindi chiedere di immetterla di nuovo
				//e salvarla di nuovo in cleanPassword
			}*/
			byte[] hashToSave = this.getHash(cleanPassword.getBytes());
			this.mCallBack.getWizardChanges(true, hashToSave, this.salt);
		}
		catch (NoSuchAlgorithmException e)
		{
			e.printStackTrace();
			//TODO notificare l'errore
			System.exit(-1);
		}
		catch (NoSuchProviderException e)
		{
			e.printStackTrace();
			//TODO notificare l'errore
			System.exit(-1);
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
			throw new ClassCastException(activity.toString() + "must implement OnPasswordInizializedListener");
		}
	}

	/**
	 * Prende la password, la sala, ne fa l'hashe ritorna quest'ultimo.
	 * 
	 * @param pswd : la password
	 * @return l'hash salato della password
	 * @throws NoSuchAlgorithmException 
	 */
	private byte[] getHash(byte[] pswd) throws NoSuchAlgorithmException {
		byte[] saltedPswd = new byte[pswd.length + this.salt.length];
		System.arraycopy(pswd, 0, saltedPswd, 0, pswd.length);
		System.arraycopy(this.salt, 0, saltedPswd, pswd.length, this.salt.length);
		return PasswordUtils.getByteHash(saltedPswd, CryptoUtility.SHA_256);
	}
}
