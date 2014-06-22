package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.factory.FeatureEnum;
import it.polimi.dima.watchdog.factory.FeatureFactory;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;

/**
 * Classe usata per gestire le tabs nei fragment che ne hanno bisogno.
 * 
 * @author claudio
 *
 */
public class TabsAdapter extends FragmentPagerAdapter {
	
	private int tabsNum;
	private FeatureEnum mFeatureEnum;
	private Context context;
	
	public TabsAdapter(FragmentManager fm, int tabsNum, FeatureEnum mFeatureEnum, Context context) {
		super(fm);
		
		this.context = context;
		this.tabsNum = tabsNum;
		this.mFeatureEnum = mFeatureEnum;
	}

	@Override
	public Fragment getItem(int position) {
		
		FeatureFactory mFactory = FeatureFactory.getFactory(this.mFeatureEnum);
		return mFactory.getFragment(position);
	}

	@Override
	public int getCount() {
		return this.tabsNum;
	}
	
	@Override
    public CharSequence getPageTitle(int position) {
        //return "OBJECT " + (position + 1);
		Resources res = context.getResources();
		Drawable myDrawable = res.getDrawable(R.drawable.green_clock);
		
		SpannableStringBuilder sb = new SpannableStringBuilder(" " + "Page #"+ position); // space added before text for convenience

	    myDrawable.setBounds(0, 0, myDrawable.getIntrinsicWidth(), myDrawable.getIntrinsicHeight()); 
	    ImageSpan span = new ImageSpan(myDrawable, ImageSpan.ALIGN_BASELINE); 
	    sb.setSpan(span, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE); 

	    return sb;
		
    }

	
	public int getTABS_NUM() {
		return this.tabsNum;
	}
}