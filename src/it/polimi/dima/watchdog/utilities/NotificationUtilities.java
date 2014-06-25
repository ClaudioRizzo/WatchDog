package it.polimi.dima.watchdog.utilities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;

public class NotificationUtilities {

	public static void CreatePopup(String title, String message, String tag, Context context){
		if(context instanceof Activity){
			FragmentManager manager = ((Activity) context).getFragmentManager();
			DialogFragment newFragment = new PopupFragment(title, message, context);
		    newFragment.show(manager, tag);
		}
	}
}