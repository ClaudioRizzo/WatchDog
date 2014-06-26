package it.polimi.dima.watchdog.fragments.siren;

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
import android.widget.TextView;

/**
 * 
 * @author claudio
 *
 */
public class SirenOffFragment extends Fragment implements FragmentAdapterLifecycle {

	private SirenOffAdapter adapter;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
		
        View view =  inflater.inflate(R.layout.fragment_siren_off, container, false);
        
        TextView message = (TextView) view.findViewById(R.id.message_siren_off_no_contacts);
		message.setVisibility(View.GONE);
        
        ListView mListView = (ListView) view.findViewById(R.id.list_siren_off);

		List<String> contacts = getData();
		
		if(contacts.size() == 0){
			message.setVisibility(View.VISIBLE);
		}
		else{
			this.adapter = new SirenOffAdapter(getActivity(), getData());
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
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onResumeFragment() {
		
	}

	@Override
	public void onPauseFragment() {
		// TODO Auto-generated method stub
		
	}
}