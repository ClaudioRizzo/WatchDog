package it.polimi.dima.watchdog.gps.exceptions;

public class LocationException extends Exception {

	private static final long serialVersionUID = 2355600250379105960L;

	public LocationException(String error) {
		super(error);
	}
}
