package it.polimi.dima.watchdog.exceptions;

public class WrongPasswordException extends Exception {

	private static final long serialVersionUID = 4999925941996051985L;
	private String message;
	
	public WrongPasswordException(){}
	
	public WrongPasswordException(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}

}