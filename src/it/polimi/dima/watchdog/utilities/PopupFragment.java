package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.activities.MainActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class PopupFragment extends DialogFragment {

	private String title;
	private String message;
	private Context context;
	private boolean initialization;
	
	public PopupFragment(String title, String message, Context context, boolean initialization){
		this.title = title;
		this.message = message;
		this.context = context;
		this.initialization = initialization;
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        
        AlertDialog.Builder errorPopupBuilder = new AlertDialog.Builder(getActivity());
        
        TextView title = new TextView(this.context);
        title.setText(this.title);
        title.setBackgroundColor(Color.DKGRAY);
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.WHITE);
        title.setTextSize(20);
        errorPopupBuilder.setCustomTitle(title);
        
        TextView msg = new TextView(this.context);
        msg.setText(this.message);
        msg.setPadding(10, 10, 10, 10);
        msg.setGravity(Gravity.CENTER);
        msg.setTextSize(18);
        errorPopupBuilder.setView(msg);
        
        errorPopupBuilder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                	   dialog.dismiss();
                	   if(initialization){
							Intent intent = new Intent(context, MainActivity.class);
							startActivity(intent);
							((Activity)context).finish();
                       } 
                   }
               });
        
        Dialog errorPopup = errorPopupBuilder.create();
        errorPopup.setCanceledOnTouchOutside(false);
        errorPopup.setCancelable(false);
        return errorPopup;
    }
}