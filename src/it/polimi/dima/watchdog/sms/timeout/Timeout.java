package it.polimi.dima.watchdog.sms.timeout;

import it.polimi.dima.watchdog.exceptions.AlreadyExistentTimeoutException;
import it.polimi.dima.watchdog.exceptions.MyTimeoutException;
import it.polimi.dima.watchdog.exceptions.NonExistentTimeoutException;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import java.util.HashMap;
import java.util.Map;
import android.app.Activity;
import android.content.Context;
import android.database.Observable;

/**
 * Classe che gestisce i timeout del command protocol.
 * 
 * @author emanuele
 *
 */
public class Timeout extends Observable<Activity> {
	
	private static Timeout timeout;
	private Context ctx;
	private Map<String, Integer> timeouts;
	
	private Timeout(Context ctx){
		this.ctx = ctx;
		this.timeouts = new HashMap<String, Integer>();
	}
	
	public static Timeout getInstance(Context context){
		if(timeout == null){
			return new Timeout(context);
		}
		return timeout;
	}
	
	/**
	 * Setta il timeout.
	 * 
	 * @param waiting : il numero di telefono di colui che aspetta un messaggio.
	 * @param other : il numero di telefono di colui di cui si aspetta un messaggio.
	 * @param time : la durata del timeout
	 * @throws AlreadyExistentTimeoutException se esiste già un timeout dello stesso tipo di quello che si vuole creare
	 * @throws InterruptedException se viene chiamato un interrupt mentre il thread è in sleep
	 * @throws NonExistentTimeoutException se non esiste il timeout cercato
	 */
	public void addTimeout(String waiting, String other, int time) throws AlreadyExistentTimeoutException, InterruptedException, NonExistentTimeoutException {
		if(this.timeouts.containsKey(waiting + other)){
			throw new AlreadyExistentTimeoutException("Timeout già esistente!!!");
		}
		else{
			this.timeouts.put(waiting + other, Integer.valueOf(time));
		}
		while(true){
			try{
				Thread.sleep(1000);
				decreaseTimeout(waiting, other);
			}
			catch (MyTimeoutException e){
				SMSUtility.handleErrorOrExceptionInCommandSession(e, other, this.ctx);
				break;
			}	
		}
	}
	
	/**
	 * Rimuove il timeout.
	 * 
	 * @param waiting : il numero di telefono di colui che aspetta un messaggio.
	 * @param other : il numero di telefono di colui di cui si aspetta un messaggio.
	 * @throws NonExistentTimeoutException se non esiste il timeout cercato
	 */
	public void removeTimeout(String waiting, String other) throws NonExistentTimeoutException{
		if(!this.timeouts.containsKey(waiting + other)){
			throw new NonExistentTimeoutException("Non esiste tale timeout!!!");
		}
		else{
			this.timeouts.remove(waiting + other);
		}
	}
	
	/**
	 * Diminuisce il timeout di un secondo.
	 * 
	 * @param waiting : il numero di telefono di colui che aspetta un messaggio.
	 * @param other : il numero di telefono di colui di cui si aspetta un messaggio.
	 * @throws NonExistentTimeoutException se non esiste il timeout cercato
	 * @throws MyTimeoutException se il timeout scade
	 */
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
}