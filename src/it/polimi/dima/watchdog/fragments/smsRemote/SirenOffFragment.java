package it.polimi.dima.watchdog.fragments.smsRemote;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.utilities.FragmentAdapterLifecycle;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

/**
 * 
 * @author claudio
 *
 */
public class SirenOffFragment extends Fragment implements FragmentAdapterLifecycle {

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_siren_off, container, false);
        
        ListView mListView = (ListView) v.findViewById(R.id.list_siren_off);

		SirenOffAdapter adapter = new SirenOffAdapter(getActivity(), getData());
		mListView.setAdapter(adapter);

		return v;
    }
	
	private List<String> getData() {
		Map<String, ?> pendingReqMap = getActivity().getSharedPreferences(
				MyPrefFiles.ASSOCIATED, Context.MODE_PRIVATE).getAll();
		List<String> data = new ArrayList<String>();
		// data.add("+393488941694");
		Log.i("[DEBUG]", "[DEBUG] prima del for");
		for (String num : pendingReqMap.keySet()) {
			Log.i("[DEBUG]", "[DEBUG] ci sono numeri associati " + num);
			data.add(num);
		}
		return data;
	}

	@Override
	public void onResumeFragment() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPauseFragment() {
		// TODO Auto-generated method stub
		
	}
}