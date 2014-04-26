package it.polimi.dima.watchdog.fragments.actionBar.settingsAction;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class AssociateNumberFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_associate_number,
				container, false);

		Button mButton = (Button) v
				.findViewById(R.id.button_associate);
		mButton.setOnClickListener(this);
		
		return v;

	}

	@Override
	public void onClick(View v) {
		
		//TODO: Effettuare validazione degli input ! 
		this.getAndSaveQuestion();
		try {
			MyPrefFiles.getMyPreference(MyPrefFiles.SECRET_Q_A, MyPrefFiles.SECRET_QUESTION, getActivity().getApplicationContext());
		} catch (NoSuchPreferenceFoundException e) {
			Log.i("[DEBUG]", "L'errore è qui");
			e.printStackTrace();
			return;
		}
		this.getAndSaveAnswer();
		this.getNumberAndStart();
	}
	
	private void getAndSaveQuestion() {
		View fragView = getView();
		EditText mQuestionEditText = (EditText) fragView
				.findViewById(R.id.edit_text_associate_question);
		String mQuestion = mQuestionEditText.getText().toString();
		Log.i("[DEBUG]", mQuestion);
		MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A,
				MyPrefFiles.SECRET_QUESTION, mQuestion, getActivity()
						.getApplicationContext());
	}
	
	private void getAndSaveAnswer() {
		View fragView = getView();
		EditText mQuestionEditText = (EditText) fragView
				.findViewById(R.id.edit_text_associate_answer);
		String mQuestion = mQuestionEditText.getText().toString();
		MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A,
				MyPrefFiles.SECRET_ANSWER, mQuestion, getActivity()
						.getApplicationContext());
	}
	
	private void getNumberAndStart() {
		View fragView = getView();
		EditText mPhoneToAssociateEditText = (EditText) fragView
				.findViewById(R.id.edit_text_associate_number);
		String phoneNumberToAssociate = mPhoneToAssociateEditText.getText()
				.toString();
		byte[] byteMessage = SMSUtility.hexStringToByteArray(SMSUtility.CODE1);
		SMSUtility.sendMessage(phoneNumberToAssociate, SMSUtility.SMP_PORT, byteMessage, null);
	}
}
