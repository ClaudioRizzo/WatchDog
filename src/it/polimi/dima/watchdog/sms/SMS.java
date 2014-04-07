package it.polimi.dima.watchdog.sms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SMS {
	private String text;
	private String password;
	private byte[] passwordHash;
	private byte[] finalMessage;
	
	
	public SMS(String text){
		this.text = text;
	}
	
	public SMS(String text, String password) throws NoSuchAlgorithmException, IOException{
		this.text = text;
		this.password = password;
		construct();
	}
	
	public void setPassword(String password){
		if(this.password == null){
			this.password = password;
		}
	}
	
	/**
	 * Public perchè può essere chiamato dall'esterno nel caso si costruisca esplicitamente passo a passo l'SMS
	 * @throws NoSuchAlgorithmException 
	 * @throws IOException 
	 */
	public void construct() throws NoSuchAlgorithmException, IOException{
		if(this.finalMessage == null && this.text != null){
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			//l'hash sarà lungo 256 bit
			this.passwordHash = digest.digest(this.password.getBytes("UTF-8"));
			
			//in pratica si crea una struttura hash(password) || text
			//Sapendo che l'hash è lungo 256 bit, è comodo alla ricezione separare le due parti se l'hash...
			//...è in testa
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
			outputStream.write(this.passwordHash);
			outputStream.write(this.text.getBytes());
			this.finalMessage = outputStream.toByteArray();
		}
	}
	
}
