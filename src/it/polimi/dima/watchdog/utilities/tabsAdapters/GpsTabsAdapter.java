package it.polimi.dima.watchdog.utilities.tabsAdapters;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.factory.FeatureEnum;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

public class GpsTabsAdapter extends TabsAdapter {

	private int[] gpsIcons = {R.drawable.gps1, R.drawable.gps2};
	
	public GpsTabsAdapter(FragmentManager fm, int tabsNum,
			FeatureEnum mFeatureEnum, Context context) {
		super(fm, tabsNum, mFeatureEnum, context);
		
	}
	
	@Override
    public CharSequence getPageTitle(int position) {
		Resources res = super.context.getResources();
		Drawable myDrawable = res.getDrawable(gpsIcons[position]);
		
		SpannableStringBuilder sb; // space added before text for convenience

		switch (position) {
		case 0:
			sb = new SpannableStringBuilder("  Localize");
			break;
		case 1:
			sb = new SpannableStringBuilder("  Perimeter");
			break;
		default:
			sb = new SpannableStringBuilder("  Gps");
			break;
		}
		
	    myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight()); 
	    ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE); 
	    sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 
	    
	    return sb;
	}
	
	

}
