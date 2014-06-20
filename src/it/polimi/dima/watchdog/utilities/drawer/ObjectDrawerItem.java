package it.polimi.dima.watchdog.utilities.drawer;

public class ObjectDrawerItem {

	private int icon;
    private String name;
 
    // Constructor.
    public ObjectDrawerItem(int icon, String name) {
 
        this.icon = icon;
        this.name = name;
    }

	public int getIcon() {
		return icon;
	}

	public String getName() {
		return name;
	}
    
   
}