package it.polimi.dima.watchdog.fragments.gps.map;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import it.polimi.dima.watchdog.R;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;




public class MyMapFragment extends Fragment {

	public static String TAG = "MAP_FRAGMENT";
	private GoogleMap mMap;
	private Location location;
	
	
	public MyMapFragment(Location location) {
		this.location = location;
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		
		
        View v = inflater.inflate(R.layout.fragment_mymap, container, false);
        GpsTracker gps;
        mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        
        
        if(location == null) {
        	
        	try {
    			
    			Context ctx = getActivity().getApplicationContext();
    			gps = new GpsTracker(ctx, (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE));
    			
    			Location lastLoc = gps.getLastKnownLocation();
    			
    			Log.i("DEBUG", "DEBUG last position: "+lastLoc);
    			if(lastLoc != null) {
    				
    				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude()), 12.0f));
    				
    				
    			}
    			
    			
    		} catch (LocationException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        	
        }
        else {
        	Log.i("[DEBUG]", "latitudine "+location.getLatitude()+" longitudine: "+location.getLongitude()); 
        	mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 12.0f));
			mMap.addMarker(new MarkerOptions()
	        .position(new LatLng(location.getLatitude(), location.getLongitude()))
	        .title("Your Phone"));
        }
        
        
        return v;
    }
	


}
