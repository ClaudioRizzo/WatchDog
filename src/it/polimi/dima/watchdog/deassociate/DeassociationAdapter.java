package it.polimi.dima.watchdog.deassociate;

import it.polimi.dima.watchdog.R;
import java.util.List;
import android.content.Context;
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
		this.context = context;
		this.numbers = numbers;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	
	@Override
	public int getCount() {
		return this.numbers.size();
	}

	@Override
	public Object getItem(int position) {
		return this.numbers.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			view = inflater.inflate(R.layout.list_item_associated_for_deletion, null);
		}
		RelativeLayout mRelativeLayout = (RelativeLayout) view.findViewById(R.id.relative_layout_associated_list_for_deletion);
		
		TextView numberTextView = (TextView) mRelativeLayout.findViewById(R.id.text_view_number);
		String num = this.numbers.get(position);
		numberTextView.setText(num);
		
		Button localizeButton = (Button) mRelativeLayout.findViewById(R.id.button_delete_association);
		localizeButton.setOnClickListener(new DeassociationClickHandler(view, this.context, num, this));
		return view;	
	}
}