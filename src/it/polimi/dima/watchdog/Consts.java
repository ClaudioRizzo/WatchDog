package it.polimi.dima.watchdog;

public class Consts {

	public static String TITLE = "title";

	
	/**
	 * All the applications tabs for the functionality are saved in this array
	 */
	public static int[] tabs_frame_id = {R.id.tab1_root_frame, R.id.tab2_root_frame};
	
	/**
	 * From now on variables indicating the number of tabs for each feature
	 */
	public static int GPS_TABS = 2;
	public static int SMS_TABS = 2;
	
	private Consts() {
		throw new AssertionError();
	}
}
