package it.polimi.dima.watchdog.exceptions;

public class BadPhoneNumberException extends Exception {

	private static final long serialVersionUID = -615228309007343274L;
	private String message;
	
	public BadPhoneNumberException(){}
	
	public BadPhoneNumberException(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
}