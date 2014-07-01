package it.polimi.dima.watchdog.fragments.actionBar;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.exceptions.BadPhoneNumberException;
import it.polimi.dima.watchdog.password.PasswordUtils;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.NotificationUtilities;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author claudio, emanuele
 *
 */
public class AssociateNumberFragment extends Fragment implements OnClickListener {
	private String otherNumber;
	private Context context;
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.context = getActivity();
		View v = inflater.inflate(R.layout.fragment_associate_number, container, false);
		Button mButton = (Button) v.findViewById(R.id.button_associate);
		mButton.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		this.context = getActivity();
		try{
			
			try{
				this.getNumber();
				this.getAndSaveQuestion();
				this.getAndSaveAnswer();
				clearScreen();
				this.startSMP();
			}
			catch (BadPhoneNumberException e){
				ErrorManager.handleNonFatalError(e.getMessage(), this.context);
			}
			catch (IllegalArgumentException e){
				ErrorManager.handleNonFatalError(e.getMessage(), this.context);
			}
		}
		catch (Exception e){
			if(this.otherNumber != null){
				ErrorManager.handleErrorOrExceptionInSmp(e, this.otherNumber, this.context);
			}
			else{
				ErrorManager.handleNonFatalError(e.getMessage(), this.context);
			}
		}
	}

	/**
	 * Salva in this.otherNumber il numero dell'altro.
	 * @throws BadPhoneNumberException 
	 */
	private void getNumber() throws BadPhoneNumberException {
		View fragView = getView();
		EditText mPhoneToAssociateEditText = (EditText) fragView.findViewById(R.id.edit_text_associate_number);
		this.otherNumber = mPhoneToAssociateEditText.getText().toString();
		if(!this.otherNumber.matches(SMSUtility.PHONE_REGEX)){
			throw new BadPhoneNumberException(ErrorFactory.BAD_PHONE_NUMBER);
		}
		mPhoneToAssociateEditText.setText("");
	}
	
	/**
	 * Ottiene la domanda segreta che sarà inviata all'altro più avanti e la salva nelle preferenze.
	 */
	private void getAndSaveQuestion() {
		View fragView = getView();
		EditText mQuestionEditText = (EditText) fragView.findViewById(R.id.edit_text_associate_question);
		String mQuestion = mQuestionEditText.getText().toString();
		Log.i("[DEBUG]", mQuestion);
		
		if(PasswordUtils.isEmpty(mQuestion)){
			throw new IllegalArgumentException(ErrorFactory.BLANK_FIELD);
		}
		
		//salvo nelle preferenze la domanda segreta che sarà inviata all'altro più avanti
		MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, this.otherNumber + MyPrefFiles.SECRET_QUESTION, mQuestion, this.context);
		mQuestionEditText.setText("");
	}

	/**
	 * Ottiene la risposta alla domanda segreta che sarà inviata all'altro più avanti e la salva nelle preferenze.
	 */
	private void getAndSaveAnswer() {
		View fragView = getView();
		EditText mAnswerEditText = (EditText) fragView.findViewById(R.id.edit_text_associate_answer);
		String mAnswer = mAnswerEditText.getText().toString();
		
		if(PasswordUtils.isEmpty(mAnswer)){
			throw new IllegalArgumentException(ErrorFactory.BLANK_FIELD);
		}
		
		//salvo nelle preferenze la risposta alla domanda segreta che sarà inviata all'altro più avanti
		MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, this.otherNumber + MyPrefFiles.SECRET_ANSWER, mAnswer, this.context);
		mAnswerEditText.setText("");
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
		MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, this.otherNumber, this.context);
	}
	
	public void clearScreen(){
		View view = getView();
		view.findViewById(R.id.text_view_hint_associate).setVisibility(View.GONE);
		view.findViewById(R.id.edit_text_associate_number).setVisibility(View.GONE);
		view.findViewById(R.id.edit_text_associate_question).setVisibility(View.GONE);
		view.findViewById(R.id.edit_text_associate_answer).setVisibility(View.GONE);
		view.findViewById(R.id.button_associate).setVisibility(View.GONE);
		
		NotificationUtilities.CreatePopup("Message from the system", "The association has started, wait for a notification", "ASSOCIATION_STARTED", this.context, false);
	}
}