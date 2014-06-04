package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import java.security.Security;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * 
 * @author claudio, emanuele
 *
 */
public class LocalizationFragment extends Fragment {
	

	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());
		Context ctx = getActivity().getApplicationContext();
        View v = inflater.inflate(R.layout.fragment_localization, container, false);
        Button mButton = (Button) v.findViewById(R.id.button_localization);
        Button mSwitchButton = (Button) v.findViewById(R.id.button_switch_map);
        mSwitchButton.setOnClickListener(new GpsSwitchClickHandler(getFragmentManager()));
        mButton.setOnClickListener(new GpsLocalizeClickHandler(getPassword(v), getPhoneNumber(v), ctx));
        return v;
    }
	
	
	
	
	private String getPhoneNumber(View view) {
		EditText mEditText = (EditText) view.findViewById(R.id.phone_number1);
		String phoneNum = mEditText.getText().toString();
		return phoneNum;
	}
	
	private String getPassword(View view) {
		EditText mEditText = (EditText) view.findViewById(R.id.password_localize_1);
		String cleanPassword = mEditText.getText().toString();
		return cleanPassword;
	}
}