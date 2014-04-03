package it.polimi.dima.watchdog.fragments;

import it.polimi.dima.watchdog.R;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

/**
 * Fragment handling the main geatures of our app (whould be in the main page of the app)
 * @author claudio
 *
 */
public class MainFeaturesFragment extends Fragment implements OnClickListener {
	
	OnFeatureSelectedListener mCallBack;
	Button gpsButton;
	Button statusButton;
	Button remoteButton;
	
	/**
	 * Every Activity containing this fragment should implement this interface	
	 * @author claudio
	 *
	 */
	public interface OnFeatureSelectedListener {
		public void onGpsFeatureSelected();
		public void onPhoneStatusFeatureSelected();
		public void onSmsRemoteFeatureSelected();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		
		//di fatto verifica che l'attività si sia registra come listener 
		//	(una cosa simile almeno)
		try {
			mCallBack = (OnFeatureSelectedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement OnHeadlineSelectedListener");
		}
	}

	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
		// Inflate the layout for this fragment
        final View fragView = inflater.inflate(R.layout.fragment_main_features, container, false);
		gpsButton = (Button) fragView.findViewById(R.id.gps_button1);
		gpsButton.setOnClickListener(this);
		
		statusButton = (Button) fragView.findViewById(R.id.phone_status);
		statusButton.setOnClickListener(this);
		
		remoteButton = (Button) fragView.findViewById(R.id.sms_remote);
		remoteButton.setOnClickListener(this);
		
		return fragView;
	}


	@Override
	public void onClick(View v) {	
		switch (v.getId()) {
	        case R.id.gps_button1:
	        	mCallBack.onGpsFeatureSelected();
	        	break;
	        case R.id.phone_status:
	        	mCallBack.onPhoneStatusFeatureSelected();
	        	break;
	        case R.id.sms_remote:
	        	mCallBack.onSmsRemoteFeatureSelected();
	        	break;
	        	
	        default: break;
	        		
		 }
		
	}
}
