package it.polimi.dima.watchdog.deassociate;

import it.polimi.dima.watchdog.R;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DeassociationAdapter extends BaseAdapter {

	
	private Context context;
	private List<String> numbers;
	private static LayoutInflater inflater = null;

	public DeassociationAdapter(Context context, List<String> numbers) {
		Log.i("DEBUG", "DEBUG: il deassociation adapter è stato creato");
		this.context = context;
		this.numbers = numbers;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//TODO per qualche motivo qui non entra... perchè?
		Log.i("DEBUG", "DEBUG: sono in getView del deassociation adapter");
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_associated_for_deletion, null);
		}
		RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_associated_list_for_deletion);
		
		TextView numberTextView = (TextView) mRelativeLayout.findViewById(R.id.text_view_number);
		String num = this.numbers.get(position);
		Log.i("[DEBUG gps-adapter]", "[DEBUG - GPS-Adapter] " + num);
		numberTextView.setText(num);
		
		Button localizeButton = (Button) mRelativeLayout.findViewById(R.id.button_delete_association);
		localizeButton.setOnClickListener(new DeassociationClickHandler(this.context, num, this));
		return view;	
	}
}