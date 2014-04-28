package it.polimi.dima.watchdog.fragments.actionBar;

import it.polimi.dima.watchdog.R;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PendingRequestsAdapter extends BaseAdapter {

	private Context ctx;
	private String data[];
	private static LayoutInflater inflater = null;
	
	public PendingRequestsAdapter (Context ctx, String data[]) {
		this.ctx = ctx;
		this.data = data;
		inflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getCount() {
		
		return data.length;
	}

	@Override
	public Object getItem(int position) {
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
        if (vi == null)
            vi = inflater.inflate(R.layout.list_item_pending_recuests, null);
        //TODO: CORREGGERE ERRORE!!!!
        TextView text = (TextView) vi.findViewById(R.id.linear_layout_pending_list);
        text.setText(data[position]);
        return vi;
	}

}
