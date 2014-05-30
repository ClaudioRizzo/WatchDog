package it.polimi.dima.watchdog.exceptions;

/**
 * 
 * @author emanuele
 *
 */
public class NoSuchPreferenceFoundException extends Exception {

	private static final long serialVersionUID = 2894235738005940595L;
	private String message;
	
	public String getErrorMessage(){
		return this.message;
	}
	
	public NoSuchPreferenceFoundException(){}
	
	public NoSuchPreferenceFoundException(String message){
		this.message = message;
	}
}