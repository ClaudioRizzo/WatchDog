package it.polimi.dima.watchdog.fragments.wizard;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.activities.InitializationWizardActivity;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class WelcomeScreenFragment extends Fragment implements OnClickListener {

	private Context context;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		this.context = getActivity();
		View v = (View) inflater.inflate(R.layout.fragment_welcome_screen_first_time, container, false);
		Button mButton = (Button) v.findViewById(R.id.button_welcome_screen);
		mButton.setOnClickListener(this);
		return v;
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent(this.context, InitializationWizardActivity.class);
		startActivity(intent);
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
}
