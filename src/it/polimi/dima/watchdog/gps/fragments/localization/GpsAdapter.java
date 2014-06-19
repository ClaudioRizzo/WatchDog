package it.polimi.dima.watchdog.gps.fragments.localization;

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

public class GpsAdapter extends BaseAdapter {

	private Context context;
	private List<String> numbers;
	private static LayoutInflater inflater = null;
	
	public GpsAdapter(Context context, List<String> numbers) {
		this.context = context;
		this.numbers = numbers;
		GpsAdapter.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		
		return numbers.size();
	}

	@Override
	public Object getItem(int position) {
		return numbers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		
		if (view == null){
			view = inflater.inflate(R.layout.list_item_asscociated, null);
		}
		
		RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_localize_list);
		
		TextView numberTextView = (TextView) mRelativeLayout.findViewById(R.id.text_view_number);
		String num = this.numbers.get(position);
		Log.i("[DEBUG gps-adapter]", "[DEBUG - GPS-Adapter] "+num);
		numberTextView.setText(num);
		
		Button localizeButton = (Button) mRelativeLayout.findViewById(R.id.button_send);
		localizeButton.setOnClickListener(new GpsLocalizeClickHandler(view, num, this.context));
		
		
		return view;
	}
}
