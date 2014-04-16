package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class LocalizationFragment extends Fragment implements OnClickListener {

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_localization, container, false);
        
        Button mButton = (Button) v.findViewById(R.id.button_localization);
        mButton.setOnClickListener(this);
        
        return v;
    }

	@Override
	public void onClick(View v) {
		
		//TODO: implementare l'invio dell'sms al numero ottenuto nella text view
		
	}
	
	private String getPhoneNumber(View view) {
		TextView mTextView = (TextView) view.findViewById(R.id.phone_number1);
		String phoneNum = mTextView.getText().toString();
		return phoneNum;
	}
	
	private String getPassword(View view) {
		TextView mTextView = (TextView) view.findViewById(R.id.password_localize_1);
		String cleanPassword = mTextView.getText().toString();
		return cleanPassword;
	}
	
	
}
