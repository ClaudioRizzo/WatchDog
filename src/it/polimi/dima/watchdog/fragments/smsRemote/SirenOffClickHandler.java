package it.polimi.dima.watchdog.fragments.smsRemote;

import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class SirenOffClickHandler extends GenericSmsClickHandler implements OnClickListener {

	public SirenOffClickHandler(View fragView, String otherNumber,
			Context context) {
		super(fragView, otherNumber, context);
		
	}

	@Override
	public void onClick(View v) {
		super.handleClick(v, SMSUtility.SIREN_OFF);

	}

}
