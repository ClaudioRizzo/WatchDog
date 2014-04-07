package it.polimi.dima.watchdog.exceptions;

public class ArbitraryMessageReceivedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1124578953332366528L;
	private String message;
	
	public String getErrorMessage(){
		return this.message;
	}
	
	public ArbitraryMessageReceivedException(){}
	public ArbitraryMessageReceivedException(String message){
		this.message = message;
	}

}
