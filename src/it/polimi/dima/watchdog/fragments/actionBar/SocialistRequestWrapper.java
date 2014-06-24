package it.polimi.dima.watchdog.fragments.actionBar;

public class SocialistRequestWrapper {
	
	private String number;
	private String question;
	private boolean socialistVisible = false;
	
	public SocialistRequestWrapper(String number, String question) {
		this.number = number;
		this.question = question;
	}

	public String getQuestion() {
		return question;
	}
	
	public String getNumber() {
		return number;
	}

	public boolean isSocialistVisible() {
		return socialistVisible;
	}

	public void setSocialistVisible(boolean socialistVisible) {
		this.socialistVisible = socialistVisible;
	}
}
