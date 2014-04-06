package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.fragments.FirstTabRootFragment;
import it.polimi.dima.watchdog.fragments.LocalizatoionFragment;
import it.polimi.dima.watchdog.fragments.PerimeterFragment;
import it.polimi.dima.watchdog.fragments.SecondTabRootFragment;
import it.polimi.dima.watchdog.fragments.SirenOnFragment;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBar.TabListener;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity implements TabListener {

	private TabsAdapter mTabsAdapter;
	private ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_layout);

		this.setMyAdapter(getSupportFragmentManager(),
				(ViewPager) findViewById(R.id.main_pager));

		this.addTabs();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * use to handle menu item selected on the action bar
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		FragmentTransaction trans = getSupportFragmentManager().beginTransaction();	
		switch (item.getItemId()) {
		case R.id.gps_icon:
			trans.replace(R.id.tab1_root_frame, new LocalizatoionFragment());
			trans.replace(R.id.tab2_root_frame, new PerimeterFragment());
			trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			trans.addToBackStack(null);
			

			trans.commit();
			return true;
		case R.id.sms_icon:
			trans.replace(R.id.tab1_root_frame, new SirenOnFragment());
			trans.replace(R.id.tab2_root_frame, new SirenOnFragment());
			trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			trans.addToBackStack(null);

			trans.commit();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

	}

	private void setMyAdapter(FragmentManager fm, ViewPager v) {
		mTabsAdapter = new TabsAdapter(fm);
		mViewPager = v;
		mViewPager.setAdapter(mTabsAdapter);

		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                // We can also use ActionBar.Tab#select() to do this if we have a reference to the
                // Tab.
                getSupportActionBar().setSelectedNavigationItem(position);
            }
        });
		
		
		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mTabsAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter.
			// Also specify this Activity object, which implements the
			// TabListener interface, as the
			// listener for when this tab is selected.
			ActionBar actionBar = getSupportActionBar();
			actionBar
					.addTab(actionBar.newTab()
							.setText(mTabsAdapter.getPageTitle(i))
							.setTabListener(this));
		}

	}

	private void addTabs() {
		ActionBar mActionBar = getSupportActionBar();

		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

	}

	private class TabsAdapter extends FragmentPagerAdapter {

		private String context = "GPS";

		public TabsAdapter(String context, FragmentManager mFragmentManager) {
			super(mFragmentManager);
			this.setContext(context);
		}

		public TabsAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {

			// handle gps tabs
			switch (i) {
			case 0:
				return new FirstTabRootFragment();
			default:
				return new SecondTabRootFragment();
			}

		}

		@Override
		public int getCount() {

			return 2;
		}

		public void setContext(String context) {
			this.context = context;
		}

	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction arg1) {
		 mViewPager.setCurrentItem(tab.getPosition());
		
	}

	@Override
	public void onTabUnselected(Tab arg0, FragmentTransaction arg1) {
		// TODO Auto-generated method stub
		
	}



}
