package it.polimi.dima.watchdog.gps.fragments.localization;

import it.polimi.dima.watchdog.fragments.smsRemote.GenericSmsClickHandler;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class GpsLocalizeClickHandler extends GenericSmsClickHandler implements OnClickListener {

	
	
	public GpsLocalizeClickHandler(View fragView, String otherNumber,
			Context context) {
		super(fragView, otherNumber, context);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onClick(View v) {
		super.handleClick(v, SMSUtility.LOCATE);
	}
	
	


}
