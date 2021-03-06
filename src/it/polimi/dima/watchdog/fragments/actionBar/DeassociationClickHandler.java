package it.polimi.dima.watchdog.fragments.actionBar;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.NotificationUtilities;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;

public class DeassociationClickHandler implements OnClickListener {

	private Context context;
	private String number;
	private DeassociationAdapter mCallBack;
	private View view;
	
	
	public DeassociationClickHandler(View view, Context context, String number, DeassociationAdapter mCallBack){
		this.context = context;
		this.number = number;
		this.mCallBack = mCallBack;
		this.view = view;
	}
	
	@Override
	public void onClick(View v) {
		MyPrefFiles.eraseSmpPreferences(this.number, this.context);
		this.view.findViewById(R.id.text_view_number).setVisibility(View.GONE);
		this.view.findViewById(R.id.button_delete_association).setVisibility(View.GONE);
		NotificationUtilities.CreatePopup("Notification from the system", "You succesfully removed " + this.number + " from your associated numbers list! Refresh the page if you still see it.", "DEASSOCIATION_SUCCESS", this.context, false);
		this.mCallBack.notifyDataSetChanged();
	}
}
