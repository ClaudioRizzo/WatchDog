package it.polimi.dima.watchdog.fragments.actionBar;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.crypto.PublicKeyAutenticator;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import android.content.Context;
import android.util.Base64;
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

public class PendingRequestsAdapter extends BaseAdapter {

	private Context ctx;
	private LinkedList<SocialistRequestWrapper> data;
	private static LayoutInflater inflater = null;
	private String secretAnswer;

	public PendingRequestsAdapter(Context ctx,
			List<SocialistRequestWrapper> data) {
		this.ctx = ctx;
		this.data = (LinkedList<SocialistRequestWrapper>) data;
		inflater = (LayoutInflater) ctx
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {

		return data.size();
	}

	@Override
	public Object getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (vi == null)
			vi = inflater.inflate(R.layout.list_item_pending_recuests, null);

		LinearLayout mLinearLayout = (LinearLayout) vi
				.findViewById(R.id.linear_layout_pending_list);

		TextView mTextView = (TextView) mLinearLayout
				.findViewById(R.id.text_view_pending1);
		SocialistRequestWrapper current = data.get(position);
		setStringToShow(current, mTextView);

		EditText secAnswerEditText = (EditText) mLinearLayout
				.findViewById(R.id.edit_text_secret_answer);
		this.secretAnswer = secAnswerEditText.getText().toString();

		Button refuseButton = (Button) mLinearLayout
				.findViewById(R.id.button_refuse_smp);
		Button sendButton = (Button) mLinearLayout
				.findViewById(R.id.button_accept_smp);

		handleRefuse(refuseButton, data.get(position).getNumber());
		handleSend(sendButton, data.get(position).getNumber());

		return vi;
	}

	private void handleRefuse(Button refuseButton, final String number) {

		refuseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i("[DEBUG]", "Ho cliccato il bottone rifiuto");
				MyPrefFiles.erasePreferences(number, ctx);
				SMSUtility.sendMessage(number, SMSUtility.SMP_PORT,
						SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);

			}
		});
	}

	private void handleSend(Button sendButton, final String number) {

		sendButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				PublicKeyAutenticator pka = new PublicKeyAutenticator();
				try {
					Log.i("[DEBUG]", "Ho cliccato send");
					pka.setMyPublicKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, ctx));
					pka.setSecretAnswer(secretAnswer);
					pka.doHashToSend();
					SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE4), pka.getHashToSend());
					String preferenceKey = number + MyPrefFiles.HASH_FORWARDED;
					Log.i("[DEBUG-HAS_ANSW", Base64.encodeToString(pka.getHashToSend(), Base64.DEFAULT));
					MyPrefFiles.setMyPreference(MyPrefFiles.SMP_STATUS, preferenceKey, number, ctx);
				
				} catch (NoSuchPreferenceFoundException e) {
					
					SMSUtility.showShortToastMessage(e.getMessage(), ctx);
					e.printStackTrace();
					handleErrorOrException(number);
				} catch (NoSuchAlgorithmException e) {
					SMSUtility.showShortToastMessage(e.getMessage(), ctx);
					e.printStackTrace();
					handleErrorOrException(number);
				}
				

			}
		});
	}

	private void setStringToShow(SocialistRequestWrapper socReqWrapper,
			TextView textView) {
		String toShow = socReqWrapper.getNumber() + ": "
				+ socReqWrapper.getQuestion();
		textView.setText(toShow);
	}
	
	private void handleErrorOrException(String number){
		MyPrefFiles.erasePreferences(number, this.ctx);
		
		SMSUtility.sendMessage(number, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
	}

}
