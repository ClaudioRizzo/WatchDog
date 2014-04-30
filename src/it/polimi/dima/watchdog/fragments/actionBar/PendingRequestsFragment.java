package it.polimi.dima.watchdog.fragments.actionBar;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.R;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class PendingRequestsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_pending_requests,
				container, false);

		ListView mListView = (ListView) v
				.findViewById(R.id.list_pending_requests);
		PendingRequestsAdapter mAdapter = new PendingRequestsAdapter(
				getActivity(), getData());
		mListView.setAdapter(mAdapter);

		return v;
	}

	private List<SocialistRequestWrapper> getData() {
		Map<String, ?> pendingReqMap = getActivity().getSharedPreferences(
				MyPrefFiles.PENDENT, Context.MODE_PRIVATE).getAll();
		
		List<SocialistRequestWrapper> data = new LinkedList<SocialistRequestWrapper>();
		
		for(String num: pendingReqMap.keySet()) {
			data.add(new SocialistRequestWrapper(num, (String) pendingReqMap.get(num))); 
		}
		
		return data;
		
	}
}
