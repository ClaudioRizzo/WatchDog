package it.polimi.dima.watchdog.fragments.actionBar;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.password.PasswordResetter;
import it.polimi.dima.watchdog.password.PasswordUtils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_change_password, container, false);
		
		handleChange(v);
		
		return v;
	}
	
	
	private void handleChange(View v) {
		Button changeButton = (Button) v.findViewById(R.id.button_change_password);
		final EditText oldEdit = (EditText) v.findViewById(R.id.edit_text_old_password);
		final EditText newEdit = (EditText) v.findViewById(R.id.edit_text_new_pass);
		final EditText checkEdit = (EditText) v.findViewById(R.id.edit_text_confirm_new_pass);
		
		changeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String newPass = newEdit.getText().toString();
				String oldPass = oldEdit.getText().toString();
				String check = checkEdit.getText().toString();
				
				Log.i("DEBUG", "DEBUG: OLD = " + oldPass);
				Log.i("DEBUG", "DEBUG: NEW = " + newPass);
				Log.i("DEBUG", "DEBUG: CONF = " + check);
				
				if(PasswordUtils.isEmpty(oldPass) || PasswordUtils.isEmpty(newPass) || PasswordUtils.isEmpty(check)){
					Log.i("DEBUG", "DEBUG: QUALCHE CAMPO è VUOTO");
					ErrorManager.handleNonFatalError(ErrorFactory.BLANK_FIELD);
				}
				else if(!newPass.matches(PasswordUtils.PASSWORD_REGEX)){
					Log.i("DEBUG", "DEBUG: BAD PASSWORD: NON MATCHA LA REGEX");
					ErrorManager.handleNonFatalError(ErrorFactory.BAD_PASSWORD);
				}
				else if(newPass.equals(check)) {
					Log.i("DEBUG", "DEBUG: NEW == CHECK");
						
					try {
						PasswordResetter resetter = new PasswordResetter(oldPass, newPass, getActivity());
						resetter.changePassword();
						resetter.storePasswordHashAndSalt();
						resetter.notifyAllContacts();
					} catch (Exception e) {
						ErrorManager.handleNonFatalError(e.getMessage());
					}
				} else {
					Log.i("DEBUG", "DEBUG: NEW != CHECK");
					ErrorManager.handleNonFatalError(ErrorFactory.WRONG_PASSWORD);
				}
				
			}
		});
	}
	
}