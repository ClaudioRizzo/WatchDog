package it.polimi.dima.watchdog.fragments.gps;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.actionBar.PendingRequestsAdapter;
import it.polimi.dima.watchdog.fragments.actionBar.SocialistRequestWrapper;
import it.polimi.dima.watchdog.fragments.gps.map.MessageActionListener;
import it.polimi.dima.watchdog.utilities.ListenerUtility;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;

import java.security.Security;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

/**
 * 
 * @author claudio, emanuele
 * 
 */
public class LocalizationFragment extends Fragment {

	public static String TAG = "LOCALIZATION_FRAGMENT";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Security.addProvider(new org.spongycastle.jce.provider.BouncyCastleProvider());

		View v = inflater.inflate(R.layout.fragment_localization, container,
				false);

		ListView mListView = (ListView) v.findViewById(R.id.list_localize);

		GpsAdapter adapter = new GpsAdapter(getActivity(), getData());
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

	

}