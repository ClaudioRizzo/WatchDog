package it.polimi.dima.watchdog.gps.exceptions;

public class LocationDisabledExeption extends LocationException {

	private static final long serialVersionUID = -1215960943175580425L;

	public LocationDisabledExeption(String error) {
		super(error);
	}

}
