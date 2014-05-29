package it.polimi.dima.watchdog.fragments.actionBar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
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
import android.widget.TextView;

/**
 * 
 * @author claudio, emanuele
 *
 */
public class PendingRequestsAdapter extends BaseAdapter {

	private Context ctx;
	private LinkedList<SocialistRequestWrapper> data;
	private static LayoutInflater inflater = null;

	public PendingRequestsAdapter(Context ctx, List<SocialistRequestWrapper> data) {
		this.ctx = ctx;
		this.data = (LinkedList<SocialistRequestWrapper>) data;
		inflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
		if (vi == null){
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
		Button refuseButton = (Button) mLinearLayout.findViewById(R.id.button_refuse_smp);
		Button sendButton = (Button) mLinearLayout.findViewById(R.id.button_accept_smp);
		handleRefuse(refuseButton, current.getNumber());
		handleSend(sendButton, current.getNumber(),editTextList);
		return vi;
	}

	/**
	 * Chiamato se l'utente rifiuta di associare i telefoni
	 * @param refuseButton : il bottone di rifiuto
	 * @param number : il numero dell'altro
	 */
	private void handleRefuse(Button refuseButton, final String number) {
		refuseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("[DEBUG]", "[DEBUG_SMP] Ho cliccato il bottone rifiuto");
				
				//Cancello tutte le preferenze relative all'altro (compresa la richiesta pendente)...
				MyPrefFiles.eraseSmpPreferences(number, ctx);
				
				//... lo notifico...
				SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
			}
		});
	}

	private void handleSend(Button sendButton, final String number, final List<EditText> editTextList) {
		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				//creo un pka...
				PublicKeyAutenticator pka = new PublicKeyAutenticator();
				try {
					//... poi ottengo ciò che ho digitato...
					String secAnsw = editTextList.get(0).getText().toString();
					String mySecQuestion = editTextList.get(1).getText().toString();
					String mySecAnswer = editTextList.get(2).getText().toString();
					
					Log.i("[DEBUG]", "[DEBUG_SMP] Ho cliccato send: " + secAnsw);
					
					//...poi setto nelle mie preferenze domanda e risposta segrete che ho scelto (saranno utilizzate dopo il giro di boa)...
					MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, number + MyPrefFiles.SECRET_QUESTION, mySecQuestion, ctx);
					MyPrefFiles.setMyPreference(MyPrefFiles.SECRET_Q_A, number + MyPrefFiles.SECRET_ANSWER, mySecAnswer, ctx);
					
					//... poi recupero la mia chiave pubblica...
					pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, ctx));
					
					//... e setto nel pka anche la risposta da me digitata...
					pka.setSecretAnswer(secAnsw);
					
					//... poi computo l'hash di mia chiave pubblica || risposta ...
					pka.doHashToSend();

					//... lo mando all'altro...
					SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE4), pka.getHashToSend());
					
					//... segno in SMP_STATUS di aver inviato l'hash
					String preferenceKey = number + MyPrefFiles.HASH_FORWARDED;
					MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, number, ctx);
					
					//... e tolgo dal file PENDENT la richiesta
					MyPrefFiles.deleteMyPreference(MyPrefFiles.PENDENT, number, ctx);

				} 
				catch (Exception e)
				{
					//notifico e invio richiesta di stop forzato, oltre alla cancellazione delle preferenze
					SMSUtility.handleErrorOrExceptionInSmp(e, number, ctx);
				}
			}
		});
	}

	private void setStringToShow(SocialistRequestWrapper socReqWrapper, TextView textView) {
		String toShow = socReqWrapper.getNumber() + ": " + socReqWrapper.getQuestion();
		textView.setText(toShow);
	}
}
