package it.polimi.dima.watchdog.fragments.actionBar;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class DeassociationFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_associated_for_deletion, container, false);
		
		TextView message = (TextView) v.findViewById(R.id.message_associated_for_deletion_no_contacts);
		message.setVisibility(View.GONE);
		
		ListView mListView = (ListView) v.findViewById(R.id.list_item_associated_listview);
		
		List<String> contacts = getAssociatedNumbers();
		
		if(contacts.size() == 0){
			message.setVisibility(View.VISIBLE);
		}
		else{
			DeassociationAdapter mAdapter = new DeassociationAdapter(getActivity(), getAssociatedNumbers());
			mListView.setAdapter(mAdapter);
		}
		
		return v;
	}
	
	private List<String> getAssociatedNumbers(){
		Map<String, ?> numbersMap = getActivity().getSharedPreferences(MyPrefFiles.ASSOCIATED, Context.MODE_PRIVATE).getAll();
		List<String> numbers = new ArrayList<String>();
		
		for(String num : numbersMap.keySet()){
			numbers.add(num);
		}
		return numbers;
	}
}