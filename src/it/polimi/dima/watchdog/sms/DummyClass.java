package it.polimi.dima.watchdog.sms;

import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;

import java.security.NoSuchAlgorithmException;

public class DummyClass {
	
	private static DummyClass instance;
	
	private DummyInterface mListener;
	
	private DummyClass() {
		
		DummyClass.instance = new DummyClass();
	}
	
	public void setListener(DummyInterface mLisInterface) {
		this.mListener = mLisInterface;
	}
	
	public static DummyClass getDumyInstance() {
		if(instance == null) {
			return new DummyClass();
		} else {
			return instance;
		}
	}
	
	public void utenteHaScelto(boolean choice) throws NoSuchAlgorithmException, NoSuchPreferenceFoundException {
		mListener.notifyChoice(choice);
	}
	
	public interface DummyInterface {
		public void notifyChoice(boolean choice) throws NoSuchAlgorithmException, NoSuchPreferenceFoundException;
	}

}
