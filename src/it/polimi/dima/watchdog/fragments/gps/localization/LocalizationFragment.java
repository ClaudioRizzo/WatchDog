package it.polimi.dima.watchdog.fragments.gps.localization;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.utilities.FragmentAdapterLifecycle;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

/**
 * 
 * @author claudio, emanuele
 * 
 */
public class LocalizationFragment extends Fragment implements FragmentAdapterLifecycle {

	public static String TAG = "LOCALIZATION_FRAGMENT";
	private GpsAdapter adapter;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());

		View view = inflater.inflate(R.layout.fragment_localization, container,
				false);
		
		TextView message = (TextView) view.findViewById(R.id.message_localize_no_contacts);
		message.setVisibility(View.GONE);

		ListView mListView = (ListView) view.findViewById(R.id.list_localize);

		List<String> contacts = getData();
		
		if(contacts.size() == 0){
			message.setVisibility(View.VISIBLE);
		}
		else{
			this.adapter = new GpsAdapter(getActivity(), getData());
			mListView.setAdapter(this.adapter);
		}
		return view;
	}

	private List<String> getData() {
		Map<String, ?> pendingReqMap = getActivity().getSharedPreferences(
				MyPrefFiles.ASSOCIATED, Context.MODE_PRIVATE).getAll();
		List<String> data = new ArrayList<String>();
		for (String num : pendingReqMap.keySet()) {
			Log.i("[DEBUG]", "[DEBUG] ci sono numeri associati " + num);
			data.add(num);
		}
		return data;
	}

	@Override
	public void onResumeFragment() {
		
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}

	@Override
	public void onPauseFragment() {
		
	}
}