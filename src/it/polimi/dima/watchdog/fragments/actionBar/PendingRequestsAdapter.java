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
import android.widget.RelativeLayout;
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
		RelativeLayout myRelativeLayout = (RelativeLayout) vi.findViewById(R.id.linear_layout_pending_list);
		SocialistRequestWrapper current = this.data.get(position);
		List<EditText> editTextList = getEditTexts(myRelativeLayout);
		
		constructStringToShow(myRelativeLayout, current);
		hideElementsIfInSmpSecondHalf(myRelativeLayout, editTextList,current.getNumber());
		handleRefuse(vi, current.getNumber(), editTextList);
		handleSend(vi, current.getNumber(), editTextList);
		
		return vi;
	}
	
	private void constructStringToShow(RelativeLayout myRelativeLayout, SocialistRequestWrapper current) {
		TextView numberTextView = (TextView) myRelativeLayout.findViewById(R.id.text_view_pending_number);
		TextView questionTextView = (TextView) myRelativeLayout.findViewById(R.id.text_view_pending_question);
		setStringToShow(current, numberTextView, questionTextView);
	}

	private List<EditText> getEditTexts(RelativeLayout myRelativeLayout) {
		EditText secAnswerEditText = (EditText) myRelativeLayout.findViewById(R.id.edit_text_secret_answer);
		EditText mySecQuestionEditText = (EditText) myRelativeLayout.findViewById(R.id.edit_text_my_secret_question);
		EditText mySecAnswerEditText = (EditText) myRelativeLayout.findViewById(R.id.edit_text_my_secret_answer);
		
		return new ArrayList<EditText>(Arrays.asList(secAnswerEditText, mySecQuestionEditText, mySecAnswerEditText));
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
				Log.i("DEBUG", "DEBUG: rifiuto: parte il messaggio code6");
				SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
				hideElementsAfterClick(rawView, editTextList);
			}
		});
	}

	private void handleSend(final View rawView, final String number, final List<EditText> editTextList) {
		
		Button acceptButton = (Button) rawView.findViewById(R.id.button_accept_smp);
		acceptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				// creo un pka...
				PublicKeyAutenticator pka = new PublicKeyAutenticator();
				try {
					// ... poi ottengo ciò che ho digitato...
					String secAnsw = editTextList.get(0).getText().toString();
					String mySecQuestion = editTextList.get(1).getText().toString();
					String mySecAnswer = editTextList.get(2).getText().toString();

					Log.i("[DEBUG]", "[DEBUG_SMP] Ho cliccato send");
					
					// ...poi setto nelle mie preferenze domanda e risposta segrete che ho scelto (saranno utilizzate dopo il giro di  boa)...
					MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, number + MyPrefFiles.SECRET_QUESTION, mySecQuestion, context);
					MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, number + MyPrefFiles.SECRET_ANSWER, mySecAnswer, context);

					// ...poi tolgo il numero dalla lista delle richieste pendenti...
					MyPrefFiles.deleteMyPreference(MyPrefFiles.PENDENT, number, context);
					
					// ... poi recupero la mia chiave pubblica...
					pka.setMyPublicKey(MyPrefFiles.getMyPreference( MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, context));

					// ... e setto nel pka anche la risposta da me digitata...
					pka.setSecretAnswer(secAnsw);

					// ... poi computo l'hash di mia chiave pubblica || risposta...
					pka.doHashToSend();
					
					hideElementsAfterClick(rawView, editTextList);
					// ... lo mando all'altro...
					SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE4), pka.getHashToSend());

					// ... segno in SMP_STATUS di aver inviato l'hash
					String preferenceKey = number + MyPrefFiles.HASH_FORWARDED;
					MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, number, context);
					
				} catch (Exception e) {
					// notifico e invio richiesta di stop forzato, oltre alla cancellazione delle preferenze
					ErrorManager.handleErrorOrExceptionInSmp(e, number, context);
					hideElementsAfterClick(rawView, editTextList);
				}
			}
		});
	}

	private void setStringToShow(SocialistRequestWrapper socReqWrapper, TextView numberTextView, TextView questionTextView) {
		String number = "Request from " + socReqWrapper.getNumber();
		String question = "Secret question: " + socReqWrapper.getQuestion();
		numberTextView.setText(number);
		questionTextView.setText(question);
		numberTextView.setTextSize(20);
		questionTextView.setTextSize(20);
	}

	private void hideElementsIfInSmpSecondHalf(View v, List<EditText> editTextList, String other) {
		String question = MyPrefFiles.getSecQuestionIfExists(this.context, other);
		
		if (question != null) {
			EditText mySecQuestionEditText = (EditText) v.findViewById(R.id.edit_text_my_secret_question);
			EditText mySecAnswerEditText = (EditText) v.findViewById(R.id.edit_text_my_secret_answer);
			mySecQuestionEditText.setVisibility(View.GONE);
			mySecAnswerEditText.setVisibility(View.GONE);
		}
	}
	
	private void hideElementsAfterClick(View rawView, List<EditText> editTextList){
		for(int i=0; i<=2; i++){
			editTextList.get(i).setVisibility(View.GONE);
		}
		
		rawView.findViewById(R.id.button_accept_smp).setVisibility(View.GONE);
		rawView.findViewById(R.id.button_refuse_smp).setVisibility(View.GONE);
		rawView.findViewById(R.id.text_view_pending_number).setVisibility(View.GONE);
		rawView.findViewById(R.id.text_view_pending_question).setVisibility(View.GONE);
		rawView.findViewById(R.id.text_view_message_waiting).setVisibility(View.GONE);
		
		notifyDataSetChanged();
	}
}