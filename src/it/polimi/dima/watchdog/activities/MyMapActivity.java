package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import it.polimi.dima.watchdog.gps.fragments.localization.map.MyMapFragment;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class MyMapActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		
		if(findViewById(R.id.map_container) != null) {
			
			if(savedInstanceState != null) {
				return;
			}
			
			Location location = getLocation();
			MyMapFragment mapFrag = new MyMapFragment(location);
			
			getSupportFragmentManager().beginTransaction().add(R.id.map_container, mapFrag).commit();
		}
	}

	
	private Location getLocation() {
		Intent intent = getIntent();
		

		Location l = new Location("");
		l.setLatitude(intent.getDoubleExtra("latitude", 0));
		l.setLongitude(intent.getDoubleExtra("longitude", 0));
		
		return l;
	}
	
	
}
