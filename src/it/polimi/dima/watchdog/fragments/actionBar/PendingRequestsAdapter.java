package it.polimi.dima.watchdog.fragments.actionBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * 
 * @author claudio, emanuele
 * 
 */
public class PendingRequestsAdapter extends BaseAdapter {

	private Context context;
	private LinkedList<SocialistRequestWrapper> data;
	private static LayoutInflater inflater = null;
	private Button acceptButton;
	private Button refuseButton;
	private Button stopButton;
	private ProgressBar smpProgressBar;
	private List<EditText> editTextList;
	
	public Button getStopButton(){
		return this.stopButton;
	}
	
	public ProgressBar getProgressBar(){
		return this.smpProgressBar;
	}

	public PendingRequestsAdapter(Context context, List<SocialistRequestWrapper> data) {
		this.context = context;
		this.data = (LinkedList<SocialistRequestWrapper>) data;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return this.data.size();
	}

	@Override
	public Object getItem(int position) {
		return this.data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null) {
			vi = inflater.inflate(R.layout.list_item_pending_requests, null);
		}
		LinearLayout mLinearLayout = (LinearLayout) vi.findViewById(R.id.linear_layout_pending_list);
		TextView mTextView = (TextView) mLinearLayout.findViewById(R.id.text_view_pending1);
		SocialistRequestWrapper current = data.get(position);
		setStringToShow(current, mTextView);

		EditText secAnswerEditText = (EditText) mLinearLayout.findViewById(R.id.edit_text_secret_answer);
		EditText mySecQuestionEditText = (EditText) mLinearLayout.findViewById(R.id.edit_text_my_secret_question);
		EditText mySecAnswerEditText = (EditText) mLinearLayout.findViewById(R.id.edit_text_my_secret_answer);
		
		noNeedForSecrets(mLinearLayout, current.getNumber());
		
		this.smpProgressBar = (ProgressBar) mLinearLayout.findViewById(R.id.progress_bar_smp);
		this.smpProgressBar.setVisibility(View.GONE);
		this.editTextList = new ArrayList<EditText>(Arrays.asList(secAnswerEditText, mySecQuestionEditText, mySecAnswerEditText));
		this.refuseButton = (Button) mLinearLayout.findViewById(R.id.button_refuse_smp);
		this.acceptButton = (Button) mLinearLayout.findViewById(R.id.button_accept_smp);
		this.stopButton = (Button) mLinearLayout.findViewById(R.id.button_stop_smp);
		
		handleRefuse(current.getNumber());
		handleSend(current.getNumber());
		handleStop(current.getNumber());
		
		return vi;
	}
	
	
	private void handleStop(final String number){
		modifyStopButtonVisibility();
		modifyProgressBarVisibility();
		
		this.stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("DEBUG","DEBUG: ho cliccato su stop per interrompere il socialist in corso.");
				if(MyPrefFiles.iHaveSomeReferencesToThisUser(number, context)){
					ErrorManager.handleErrorOrExceptionInSmp(null, number, context);
				}
				hideElementsAfterClick(false);
			}
		});
	}
	

	/**
	 * Chiamato se l'utente rifiuta di associare i telefoni
	 * 
	 * @param refuseButton : il bottone di rifiuto
	 * @param number : il numero dell'altro
	 */
	private void handleRefuse(final String number) {
		this.refuseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("[DEBUG]", "[DEBUG_SMP] Ho cliccato il bottone rifiuto");

				// Cancello tutte le preferenze relative all'altro (compresa la richiesta pendente)...
				MyPrefFiles.eraseSmpPreferences(number, context);

				// ... lo notifico...
				if(MyPrefFiles.iHaveSomeReferencesToThisUser(number, context)){
					SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
				}
				hideElementsAfterClick(false);
			}
		});
	}

	private void handleSend(final String number) {
		this.acceptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideElementsAfterClick(true);
				// creo un pka...
				PublicKeyAutenticator pka = new PublicKeyAutenticator();
				try {
					// ... poi ottengo ciò che ho digitato...
					String secAnsw = editTextList.get(0).getText().toString();
					String mySecQuestion = editTextList.get(1).getText().toString();
					String mySecAnswer = editTextList.get(2).getText().toString();

					Log.i("[DEBUG]", "[DEBUG_SMP] Secret answer: " + secAnsw);
					Log.i("[DEBUG]", "[DEBUG_SMP] My Secret question: " + mySecQuestion);
					Log.i("[DEBUG]", "[DEBUG_SMP] My Secret answer: " + mySecAnswer);
					Log.i("[DEBUG]", "[DEBUG_SMP] Ho cliccato send");
					
					editTextList.get(0).setText("");
					editTextList.get(1).setText("");
					editTextList.get(2).setText("");
					
					// ...poi setto nelle mie preferenze domanda e risposta segrete che ho scelto (saranno utilizzate dopo il giro di  boa)...
					MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, number + MyPrefFiles.SECRET_QUESTION, mySecQuestion, context);
					MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, number + MyPrefFiles.SECRET_ANSWER, mySecAnswer, context);

					// ... poi recupero la mia chiave pubblica...
					pka.setMyPublicKey(MyPrefFiles.getMyPreference( MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, context));

					// ... e setto nel pka anche la risposta da me digitata...
					pka.setSecretAnswer(secAnsw);

					// ... poi computo l'hash di mia chiave pubblica || risposta...
					pka.doHashToSend();

					// ... lo mando all'altro...
					SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE4), pka.getHashToSend());

					// ... segno in SMP_STATUS di aver inviato l'hash
					String preferenceKey = number + MyPrefFiles.HASH_FORWARDED;
					MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, number, context);

					// ... e tolgo dal file PENDENT la richiesta
					MyPrefFiles.deleteMyPreference(MyPrefFiles.PENDENT, number, context);				

				} catch (Exception e) {
					// notifico e invio richiesta di stop forzato, oltre alla cancellazione delle preferenze
					ErrorManager.handleErrorOrExceptionInSmp(e, number, context);
					cancelView();
				}
			}
		});
	}

	private void setStringToShow(SocialistRequestWrapper socReqWrapper,TextView textView) {
		String toShow = socReqWrapper.getNumber() + ": " + socReqWrapper.getQuestion();
		textView.setText(toShow);
	}

	private boolean noNeedForSecrets(View v, String other) {
		
		String question = MyPrefFiles.getSecQuestionIfExists(context, other);
		if (question != null) {
			EditText mySecQuestionEditText = (EditText) v.findViewById(R.id.edit_text_my_secret_question);
			EditText mySecAnswerEditText = (EditText) v.findViewById(R.id.edit_text_my_secret_answer);
			mySecQuestionEditText.setVisibility(View.GONE);
			mySecAnswerEditText.setVisibility(View.GONE);
			return true;
		} else {
			return false;
		}
	}
	
	private void hideElementsAfterClick(boolean accept_or_refuse){
		editTextList.get(0).setVisibility(View.GONE);
		editTextList.get(1).setVisibility(View.GONE);
		editTextList.get(2).setVisibility(View.GONE);
		acceptButton.setVisibility(View.GONE);
		refuseButton.setVisibility(View.GONE);
		
		if(accept_or_refuse){
			smpProgressBar.setVisibility(View.VISIBLE);
			stopButton.setVisibility(View.VISIBLE);
		}
		else{
			smpProgressBar.setVisibility(View.GONE);
			stopButton.setVisibility(View.GONE);
		}
		notifyDataSetChanged();
	}
	
	private void cancelView(){
		editTextList.get(0).setVisibility(View.GONE);
		editTextList.get(1).setVisibility(View.GONE);
		editTextList.get(2).setVisibility(View.GONE);
		acceptButton.setVisibility(View.GONE);
		refuseButton.setVisibility(View.GONE);
		smpProgressBar.setVisibility(View.GONE);
		stopButton.setVisibility(View.GONE);
	}
	
	private void modifyStopButtonVisibility(){
		if(this.acceptButton.getVisibility() == View.VISIBLE){
			this.stopButton.setVisibility(View.GONE);
		}
		else if(this.acceptButton.getVisibility() == View.GONE){
			this.stopButton.setVisibility(View.VISIBLE);
		}
	}
	
	private void modifyProgressBarVisibility(){
		if(this.acceptButton.getVisibility() == View.VISIBLE){
			this.smpProgressBar.setVisibility(View.GONE);
		}
		else if(this.acceptButton.getVisibility() == View.GONE){
			this.smpProgressBar.setVisibility(View.VISIBLE);
		}
	}
}