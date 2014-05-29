package it.polimi.dima.watchdog.exceptions;

public class SmpHashesMismatchException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8553235245243422535L;
	private String message;
	
	public SmpHashesMismatchException(){}
	
	public SmpHashesMismatchException(String message){
		this.message = message;
	}
	
	
	public String getMessage(){
		return this.message;
	}

}
