package it.polimi.dima.watchdog.utilities.drawer;

import it.polimi.dima.watchdog.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DrawerItemAdapter extends ArrayAdapter<ObjectDrawerItem>  {

	
	private Context context;
    int layoutResourceId;
    private ObjectDrawerItem data[] = null;
 
    public DrawerItemAdapter(Context mContext, int layoutResourceId, ObjectDrawerItem[] data) {
 
        super(mContext, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = mContext;
        this.data = data;
    }
 
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
 
        View listItem = convertView;
 
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        listItem = inflater.inflate(layoutResourceId, parent, false);
 
        ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.image_view_item);
        TextView textViewName = (TextView) listItem.findViewById(R.id.text_view_item);
        
        ObjectDrawerItem folder = data[position];
 
        
        imageViewIcon.setImageResource(folder.icon);
        textViewName.setText(folder.name);
        
        return listItem;
    }
	
}