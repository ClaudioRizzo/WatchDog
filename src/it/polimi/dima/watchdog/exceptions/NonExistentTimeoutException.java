package it.polimi.dima.watchdog.exceptions;

/**
 * 
 * @author emanuele
 *
 */
public class NonExistentTimeoutException extends Exception {

	private static final long serialVersionUID = -7714269442256190215L;
	private String message;
	
	public NonExistentTimeoutException(){}
	
	public NonExistentTimeoutException(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
}