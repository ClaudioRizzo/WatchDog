package it.polimi.dima.watchdog.fragments.smsRemote;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class SirenOnClickHandler extends GenericSmsClickHandler implements OnClickListener {

	
	private View fragView;

	public SirenOnClickHandler(View fragView, String otherNumber, Context context) {
		super(fragView, otherNumber, context);
		this.fragView = fragView;
		
	}

	@Override
	public void onClick(View v) {
		Log.i("[DEBUG]", "[SIREN] cliccato");
		
		super.handleClick(v, SMSUtility.SIREN_ON);
		EditText et = (EditText) fragView.findViewById(R.id.edit_text_associated_password);
		et.setText("");
		
		
		
	}

}
