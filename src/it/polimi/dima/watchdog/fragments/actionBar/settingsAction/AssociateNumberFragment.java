package it.polimi.dima.watchdog.fragments.actionBar.settingsAction;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.UTILITIES.MyPrefFiles;
import it.polimi.dima.watchdog.UTILITIES.SMSUtility;
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
	
	private String otherNumber;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_associate_number,
				container, false);

		Button mButton = (Button) v.findViewById(R.id.button_associate);
		mButton.setOnClickListener(this);

		return v;

	}

	@Override
	public void onClick(View v) {

		Log.i("[DEBUG]", "Ho cliccato per inviare");
		
		//salvo in this.otherNumber il numero dell'altro
		this.getNumber();
		
		// TODO: Effettuare validazione degli input !
		// prendo la domanda digitata e la salva nelle preferenze
		this.getAndSaveQuestion();
		try {
			MyPrefFiles.getMyPreference(MyPrefFiles.SECRET_Q_A, MyPrefFiles.SECRET_QUESTION, getActivity().getApplicationContext());
		} catch (NoSuchPreferenceFoundException e) {
			e.printStackTrace();
			return;
		}
		this.getAndSaveAnswer();
		this.startSMP();
	}

	/**
	 * Salva in this.otherNumber il numero dell'altro.
	 */
	private void getNumber() {
		View fragView = getView();
		EditText mPhoneToAssociateEditText = (EditText) fragView.findViewById(R.id.edit_text_associate_number);
		this.otherNumber = mPhoneToAssociateEditText.getText().toString();
	}
	
	/**
	 * Ottiene la domanda segreta che sarà inviata all'altro più avanti e la salva nelle preferenze.
	 */
	private void getAndSaveQuestion() {
		View fragView = getView();
		EditText mQuestionEditText = (EditText) fragView.findViewById(R.id.edit_text_associate_question);
		String mQuestion = mQuestionEditText.getText().toString();
		Log.i("[DEBUG]", mQuestion);
		
		//salvo nelle preferenze la domanda segreta che sarà inviata all'altro più avanti
		MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, this.otherNumber + MyPrefFiles.SECRET_QUESTION, mQuestion, getActivity().getApplicationContext());
	}

	/**
	 * Ottiene la risposta alla domanda segreta che sarà inviata all'altro più avanti e la salva nelle preferenze.
	 */
	private void getAndSaveAnswer() {
		View fragView = getView();
		EditText mQuestionEditText = (EditText) fragView.findViewById(R.id.edit_text_associate_answer);
		String mQuestion = mQuestionEditText.getText().toString();
		
		//salvo nelle preferenze la risposta alla domanda segreta che sarà inviata all'altro più avanti
		MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, MyPrefFiles.SECRET_ANSWER, mQuestion, getActivity().getApplicationContext());
	}

	/**
	 * Fa partire il SMP mandando all'altro la richiesta di invio della chiave pubblica.
	 */
	private void startSMP() {
		//preparo il messaggio di richiesta della chiave pubblica...
		byte[] byteMessage = SMSUtility.hexStringToByteArray(SMSUtility.CODE1);
		
		//... lo invio all'altro...
		SMSUtility.sendMessage(this.otherNumber, SMSUtility.SMP_PORT, byteMessage, null);
		
		//... e segno di aver mandato la richiesta in SMP_STATUS
		String preferenceKey = this.otherNumber + MyPrefFiles.PUB_KEY_REQUEST_FORWARDED;
		MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, this.otherNumber, getActivity().getApplicationContext());
	}
}
