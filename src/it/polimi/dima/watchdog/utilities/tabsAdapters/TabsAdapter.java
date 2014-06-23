package it.polimi.dima.watchdog.utilities.tabsAdapters;

import it.polimi.dima.watchdog.factory.FeatureEnum;
import it.polimi.dima.watchdog.factory.FeatureFactory;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Classe usata per gestire le tabs nei fragment che ne hanno bisogno.
 * 
 * @author claudio
 *
 */
public class TabsAdapter extends FragmentPagerAdapter {
	
	private int tabsNum;
	private FeatureEnum mFeatureEnum;
	protected Context context;
	
	
	
	
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
        
		return "OBJECT " + (position + 1);
			
    }

	
	public int getTABS_NUM() {
		return this.tabsNum;
	}
}