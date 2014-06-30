package it.polimi.dima.watchdog.fragments.actionBar;

public class SocialistRequestWrapper {
	
	private String number;
	private String question;
	
	public SocialistRequestWrapper(String number, String question) {
		this.number = number;
		this.question = question;
	}

	public String getQuestion() {
		return this.question;
	}
	
	public String getNumber() {
		return this.number;
	}
}