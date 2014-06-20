package it.polimi.dima.watchdog.fragments.smsRemote;

import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SirenOnClickHandler extends GenericSmsClickHandler implements OnClickListener {

	
	public SirenOnClickHandler(View fragView, String otherNumber, Context context) {
		super(fragView, otherNumber, context);
		
	}

	@Override
	public void onClick(View v) {
		Log.i("[DEBUG]", "[SIREN] cliccato");
		
		super.handleClick(v, SMSUtility.SIREN_ON);
		
		
		
	}

}
