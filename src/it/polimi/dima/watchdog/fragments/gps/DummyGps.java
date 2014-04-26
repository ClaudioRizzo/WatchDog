package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.SMSUtility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Un Dummy Fragment che serve solo per prova
 * @author claudio
 *
 */
public class DummyGps extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_dummy_test, container,
				false);
		
		Button mButton = (Button) v.findViewById(R.id.button_sent_tryal);
		mButton.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		Log.i("[DEBUG]", "Ho cliccato la prova");
		SmsManager man = SmsManager.getDefault();
		byte[] message = SMSUtility.hexStringToByteArray(SMSUtility.CODE2); //provo solo l'header
		man.sendDataMessage("+393466342499", null, SMSUtility.SMP_PORT,
				message, null, null);
		
	}
}
