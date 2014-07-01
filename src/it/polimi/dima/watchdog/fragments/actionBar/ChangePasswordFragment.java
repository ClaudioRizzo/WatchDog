package it.polimi.dima.watchdog.fragments.actionBar;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.exceptions.NoSignatureDoneException;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.exceptions.NotECKeyException;
import it.polimi.dima.watchdog.exceptions.WrongPasswordException;
import it.polimi.dima.watchdog.password.PasswordResetter;
import it.polimi.dima.watchdog.password.PasswordUtils;
import it.polimi.dima.watchdog.utilities.NotificationUtilities;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ChangePasswordFragment extends Fragment {

	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.context = getActivity();
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
				String newPassword = newEdit.getText().toString();
				String oldPassword = oldEdit.getText().toString();
				String newPasswordConfirmation = checkEdit.getText().toString();
				
				tryChangingPassword(oldPassword, newPassword, newPasswordConfirmation, oldEdit, newEdit, checkEdit);
			}
		});
	}
	
	protected void tryChangingPassword(String oldPassword, String newPassword, String newPasswordConfirmation, EditText oldEdit, EditText newEdit, EditText checkEdit) {
		if(PasswordUtils.isEmpty(oldPassword) || PasswordUtils.isEmpty(newPassword) || PasswordUtils.isEmpty(newPasswordConfirmation)){
			ErrorManager.handleNonFatalError(ErrorFactory.BLANK_FIELD, this.context);
		}
		else if(!newPassword.matches(PasswordUtils.PASSWORD_REGEX)){
			ErrorManager.handleNonFatalError(ErrorFactory.BAD_PASSWORD, this.context);
		}
		else if(newPassword.equals(newPasswordConfirmation)) {
			tryToResetAndNotify(oldPassword, newPassword, oldEdit, newEdit, checkEdit);
		}
		else {
			ErrorManager.handleNonFatalError(ErrorFactory.WRONG_PASSWORD, this.context);
		}
	}

	private void tryToResetAndNotify(String oldPassword, String newPassword, EditText oldEdit, EditText newEdit, EditText checkEdit) {
		try {
			resetAndNotify(oldPassword, newPassword, oldEdit, newEdit, checkEdit);
		} 
		catch (Exception e) {
			ErrorManager.handleNonFatalError(e.getMessage(), this.context);
		}
	}

	private void resetAndNotify(String oldPassword, String newPassword, EditText oldEdit, EditText newEdit, EditText checkEdit) throws NoSuchAlgorithmException, WrongPasswordException, InvalidKeySpecException, NoSuchPreferenceFoundException, NoSignatureDoneException, NotECKeyException {
		PasswordResetter resetter = new PasswordResetter(oldPassword, newPassword, getActivity());
		resetter.changePassword();
		resetter.storePasswordHashAndSalt();
		clearScreen(oldEdit, newEdit, checkEdit);
		resetter.notifyAllContacts();
	}

	public void clearScreen(EditText old, EditText newP, EditText confirm){
		old.setText("");
		newP.setText("");
		confirm.setText("");
		NotificationUtilities.CreatePopup("Notification from the system", "The password was successfully updated!", "PWD_UPDATE_SUCCESS", this.context, false);
	}
}