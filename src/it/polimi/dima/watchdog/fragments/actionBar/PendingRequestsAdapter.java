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
	private boolean clicked = false;

	


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
		
		
		List<EditText> editTextList = new ArrayList<EditText>(Arrays.asList(secAnswerEditText, mySecQuestionEditText, mySecAnswerEditText));
		
		noNeedForSecrets(mLinearLayout, editTextList,current.getNumber());
		
		
		handleRefuse(vi, current.getNumber(), editTextList);
		handleSend(vi, current.getNumber(), editTextList);
		handleStop(vi, editTextList,current.getNumber());
		
		return vi;
	}
	
	
	private void handleStop(final View rawView, final List<EditText> editTextList, final String number){
		modifyStopButtonVisibility(rawView);
		modifyProgressBarVisibility(rawView);
		
		
		Button stopButton = (Button) rawView.findViewById(R.id.button_stop_smp);
		
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Log.i("DEBUG","DEBUG: ho cliccato su stop per interrompere il socialist in corso.");
				if(MyPrefFiles.iHaveSomeReferencesToThisUser(number, context)){
					ErrorManager.handleErrorOrExceptionInSmp(null, number, context);
				}
				hideElementsAfterClick(rawView, editTextList, false);
			}
		});
	}
	

	/**
	 * Chiamato se l'utente rifiuta di associare i telefoni
	 * 
	 * @param refuseButton : il bottone di rifiuto
	 * @param number : il numero dell'altro
	 */
	private void handleRefuse(final View rawView, final String number, final List<EditText> editTextList) {
		

		Button refuseButton = (Button) rawView.findViewById(R.id.button_refuse_smp);
		
		
		refuseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("[DEBUG]", "[DEBUG_SMP] Ho cliccato il bottone rifiuto");

				// Cancello tutte le preferenze relative all'altro (compresa la richiesta pendente)...
				MyPrefFiles.eraseSmpPreferences(number, context);

				// ... lo notifico...
				if(MyPrefFiles.iHaveSomeReferencesToThisUser(number, context)){
					SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
				}
				hideElementsAfterClick(rawView, editTextList, false);
			}
		});
	}

	private void handleSend(final View rawView, final String number, final List<EditText> editTextList) {
		
		Button acceptButton = (Button) rawView.findViewById(R.id.button_accept_smp);
		
		acceptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				clicked = true;
				
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
					
					
					// ...poi setto nelle mie preferenze domanda e risposta segrete che ho scelto (saranno utilizzate dopo il giro di  boa)...
					MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, number + MyPrefFiles.SECRET_QUESTION, mySecQuestion, context);
					MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, number + MyPrefFiles.SECRET_ANSWER, mySecAnswer, context);

					// ... poi recupero la mia chiave pubblica...
					pka.setMyPublicKey(MyPrefFiles.getMyPreference( MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, context));

					// ... e setto nel pka anche la risposta da me digitata...
					pka.setSecretAnswer(secAnsw);

					// ... poi computo l'hash di mia chiave pubblica || risposta...
					pka.doHashToSend();

					hideElementsAfterClick(rawView, editTextList, true);
					// ... lo mando all'altro...
					SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE4), pka.getHashToSend());

					// ... segno in SMP_STATUS di aver inviato l'hash
					String preferenceKey = number + MyPrefFiles.HASH_FORWARDED;
					MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, number, context);

				
									

				} catch (Exception e) {
					clicked = false;
					// notifico e invio richiesta di stop forzato, oltre alla cancellazione delle preferenze
					ErrorManager.handleErrorOrExceptionInSmp(e, number, context);
					cancelView(rawView, editTextList);
				}
			}
		});
	}

	private void setStringToShow(SocialistRequestWrapper socReqWrapper,TextView textView) {
		String toShow = socReqWrapper.getNumber() + ": " + socReqWrapper.getQuestion();
		textView.setText(toShow);
	}

	private void noNeedForSecrets(View v, List<EditText> editTextList, String other) {
		
		String question = MyPrefFiles.getSecQuestionIfExists(context, other);
		if (question != null) {
			EditText mySecQuestionEditText = (EditText) v.findViewById(R.id.edit_text_my_secret_question);
			EditText mySecAnswerEditText = (EditText) v.findViewById(R.id.edit_text_my_secret_answer);
			mySecQuestionEditText.setVisibility(View.GONE);
			mySecAnswerEditText.setVisibility(View.GONE);
			
		}
		
		if(clicked) {
			Log.i("[DEBUG]", "[SMP_DEBUG] sono dentro");
			hideElementsAfterClick(v, editTextList, true);
		}
		
		

	}
	
	private void hideElementsAfterClick(View rawView, List<EditText> editTextList, boolean accept_or_refuse){
		editTextList.get(0).setVisibility(View.GONE);
		editTextList.get(1).setVisibility(View.GONE);
		editTextList.get(2).setVisibility(View.GONE);
		
		Button acceptButton = (Button) rawView.findViewById(R.id.button_accept_smp);
		Button refuseButton = (Button) rawView.findViewById(R.id.button_refuse_smp);
		Button stopButton = (Button) rawView.findViewById(R.id.button_stop_smp);
		
		ProgressBar smpProgressBar = (ProgressBar) rawView.findViewById(R.id.progress_bar_smp);
		
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
	
	private void cancelView(View rawView, List<EditText> editTextList){
		editTextList.get(0).setVisibility(View.GONE);
		editTextList.get(1).setVisibility(View.GONE);
		editTextList.get(2).setVisibility(View.GONE);
		
		Button acceptButton = (Button) rawView.findViewById(R.id.button_accept_smp);
		Button refuseButton = (Button) rawView.findViewById(R.id.button_refuse_smp);
		Button stopButton = (Button) rawView.findViewById(R.id.button_stop_smp);
		
		ProgressBar smpProgressBar = (ProgressBar) rawView.findViewById(R.id.progress_bar_smp);
		
		acceptButton.setVisibility(View.GONE);
		refuseButton.setVisibility(View.GONE);
		smpProgressBar.setVisibility(View.GONE);
		stopButton.setVisibility(View.GONE);
	}
	
	private void modifyStopButtonVisibility(View rawView){
		
		Button acceptButton = (Button) rawView.findViewById(R.id.button_accept_smp);
		Button stopButton = (Button) rawView.findViewById(R.id.button_stop_smp);
		
		
		if(acceptButton.getVisibility() == View.VISIBLE){
			stopButton.setVisibility(View.GONE);
		}
		else if(acceptButton.getVisibility() == View.GONE){
			stopButton.setVisibility(View.VISIBLE);
		}
	}
	
	private void modifyProgressBarVisibility(View rawView){
		
		Button acceptButton = (Button) rawView.findViewById(R.id.button_accept_smp);		
		ProgressBar smpProgressBar = (ProgressBar) rawView.findViewById(R.id.progress_bar_smp);
		
		if(acceptButton.getVisibility() == View.VISIBLE){
			smpProgressBar.setVisibility(View.GONE);
		}
		else if(acceptButton.getVisibility() == View.GONE){
			smpProgressBar.setVisibility(View.VISIBLE);
		}
	}
}