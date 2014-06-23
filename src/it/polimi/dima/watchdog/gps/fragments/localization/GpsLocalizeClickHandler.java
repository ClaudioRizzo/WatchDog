package it.polimi.dima.watchdog.gps.fragments.localization;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.smsRemote.GenericSmsClickHandler;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class GpsLocalizeClickHandler extends GenericSmsClickHandler implements OnClickListener {

	private View fragView;
	
	public GpsLocalizeClickHandler(View fragView, String otherNumber,
			Context context) {
		super(fragView, otherNumber, context);
		this.fragView = fragView;
	}

	@Override
	public void onClick(View v) {
		super.handleClick(v, SMSUtility.LOCATE);
		EditText et = (EditText) fragView.findViewById(R.id.edit_text_associated_password);
		et.setText("");
	}
	
	


}
