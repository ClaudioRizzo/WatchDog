package it.polimi.dima.watchdog.exceptions;

/**
 * 
 * @author emanuele
 *
 */
public class AlreadyExistentTimeoutException extends Exception {

	private static final long serialVersionUID = 4750942614762992651L;
	private String message;
	
	public AlreadyExistentTimeoutException(){}
	
	public AlreadyExistentTimeoutException(String message){
		this.message = message;
	}
	
	public String getMessage(){
		return this.message;
	}
}