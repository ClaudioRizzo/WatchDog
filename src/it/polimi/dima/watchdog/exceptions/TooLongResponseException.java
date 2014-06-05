package it.polimi.dima.watchdog.exceptions;

public class TooLongResponseException extends Exception {

	private static final long serialVersionUID = 5739437383541363835L;
	private String message;
	
	public TooLongResponseException(){}
	public TooLongResponseException(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
}