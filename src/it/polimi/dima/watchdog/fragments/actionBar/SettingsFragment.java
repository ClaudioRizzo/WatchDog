package it.polimi.dima.watchdog.fragments.actionBar;

import it.polimi.dima.watchdog.R;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class SettingsFragment extends Fragment implements OnItemClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_settings, container, false);
		String[] settings = getResources().getStringArray(
				R.array.array_settings);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(
				inflater.getContext(), R.layout.settings_list_item,
				R.id.text_view_setting_element, settings);

		ListView myListView = (ListView) v
				.findViewById(R.id.list_view_settings);
		myListView.setAdapter(adapter);
		myListView.setOnItemClickListener(this);
		return v;

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		switch (position) {
		case 0:
			Log.i("[DEBUG]", "Ho cliccato position " + position);
			break;
		default:
			break;

		}
	}

}
