package it.polimi.dima.watchdog.deassociate;

import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class DeassociationClickHandler implements OnClickListener {

	private Context context;
	private String number;
	private DeassociationAdapter mCallBack;
	
	
	public DeassociationClickHandler(Context context, String number, DeassociationAdapter mCallBack){
		this.context = context;
		this.number = number;
		this.mCallBack = mCallBack;
	}
	
	@Override
	public void onClick(View v) {
		Log.i("DEBUG", "DEBUG: ho cancellato le preferenze");
		//TODO chiaramente scommentare queste due righe e commentare il toast.
		//MyPrefFiles.eraseSmpPreferences(this.number, this.context);
		//this.mCallBack.notifyDataSetChanged();
		ErrorManager.showShortToastMessage("preferenze cancellate", this.context);
	}
}
