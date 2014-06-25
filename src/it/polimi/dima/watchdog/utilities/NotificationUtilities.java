package it.polimi.dima.watchdog.utilities;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.widget.Toast;

public class NotificationUtilities {

	public static void CreatePopup(String title, String message, String tag, Context context, boolean initialization){
		if(context instanceof Activity){
			FragmentManager manager = ((Activity) context).getFragmentManager();
			DialogFragment newFragment = new PopupFragment(title, message, context, initialization);
		    newFragment.show(manager, tag);
		}
	}

	
	/**
	 * Visualizza un popup di errore.
	 * 
	 * @param message : il messaggio da mostrare
	 * @param context : il contesto corrente
	 */
	public static void showShortToastMessage(String message, Context context) {
		Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
		toast.show();
	}
}