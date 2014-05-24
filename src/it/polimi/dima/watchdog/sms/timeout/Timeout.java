package it.polimi.dima.watchdog.sms.timeout;

import it.polimi.dima.watchdog.exceptions.AlreadyExistentTimeoutException;
import it.polimi.dima.watchdog.exceptions.MyTimeoutException;
import it.polimi.dima.watchdog.exceptions.NonExistentTimeoutException;
import it.polimi.dima.watchdog.sms.commands.flags.StatusFree;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.database.Observable;

public class Timeout extends Observable<Activity> {
	
	private static Timeout timeout;
	private Context ctx;
	private Map<String, Integer> timeouts;
	//private Map<String, Boolean> timeoutSide;
	
	private Timeout(Context ctx){
		this.ctx = ctx;
		this.timeouts = new HashMap<String, Integer>();
		//this.timeoutSide = new HashMap<String, Boolean>();
	}
	
	public static Timeout getInstance(Context context){
		if(timeout == null){
			return new Timeout(context);
		}
		return timeout;
	}
	
	
	public void addTimeout(String waiting, String other, int time) throws AlreadyExistentTimeoutException, InterruptedException, NonExistentTimeoutException {
		if(this.timeouts.containsKey(waiting + other)){
			throw new AlreadyExistentTimeoutException("Timeout già esistente!!!");
		}
		else{
			this.timeouts.put(waiting + other, Integer.valueOf(time));
			//this.timeoutSide.put(waiting + other, Boolean.valueOf(sideA));
		}
		while(true){
			try{
				Thread.sleep(1000);
				decreaseTimeout(waiting, other);
			}
			catch (MyTimeoutException e){
				manageTimeout(waiting, other);
				break;
			}
			
		}
	}
	
	
	public void removeTimeout(String waiting, String other) throws NonExistentTimeoutException{
		if(!this.timeouts.containsKey(waiting + other)){
			throw new NonExistentTimeoutException("Non esiste tale timeout!!!");
		}
		else{
			this.timeouts.remove(waiting + other);
			//this.timeoutSide.remove(waiting + other);
		}
	}
	
	
	private void decreaseTimeout(String waiting, String other) throws NonExistentTimeoutException, MyTimeoutException{
		if(!timeouts.containsKey(waiting + other)){
			throw new NonExistentTimeoutException("Non esiste tale timeout!!!");
		}
		else{
			Integer newValue = this.timeouts.get(waiting + other) - 1;
			
			if(newValue <= 0){
				throw new MyTimeoutException();
			}
			else{
				this.timeouts.put(waiting + other, newValue);
			}
			
			
		}
	}
	
	private void manageTimeout(String waiting, String other){
		MyPrefFiles.eraseCommandSession(other, this.ctx);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusFree.CURRENT_STATUS, this.ctx);
	}

}
