package it.polimi.dima.watchdog.utilities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

public class PopupFragment extends DialogFragment {

	private String title;
	private String message;
	private Context context;
	
	public PopupFragment(String title, String message, Context context){
		this.title = title;
		this.message = message;
		this.context = context;
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
                   }
               });
        
        Dialog errorPopup = errorPopupBuilder.create();
        errorPopup.setCanceledOnTouchOutside(false);
        errorPopup.setCancelable(false);
        return errorPopup;
    }
}