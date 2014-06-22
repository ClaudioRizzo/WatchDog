package it.polimi.dima.watchdog.fragments.smsRemote;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.errors.ErrorManager;
import it.polimi.dima.watchdog.siren.LocalSirenStop;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


public class SirenOffLocalFragment extends Fragment {

	private Context context;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
		this.context = getActivity();
		
        // Inflate the layout for this fragment
		
        View v =  inflater.inflate(R.layout.fragment_local_siren_off, container, false);
    
        handleStopSiren(v);
        
		return v;
    }
	
	
	private void handleStopSiren(View v) {
		
		Button stopButton = (Button)  v.findViewById(R.id.button_stop);
		final EditText passwordField = (EditText) v.findViewById(R.id.edit_text_password_siren_off);
		
		stopButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String password = passwordField.getText().toString();
				LocalSirenStop sirenStopLocal = new LocalSirenStop(password, getActivity());
				
				try {
					sirenStopLocal.turnOffSiren();
				} catch (Exception e) {
					ErrorManager.handleNonFatalError(e.getMessage(), context);
				}
			}
		});
		
		
	}
	
}
