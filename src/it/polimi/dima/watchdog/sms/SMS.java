package it.polimi.dima.watchdog.sms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Classe che gestisce la costruzione degli SMS.
 * @author emanuele
 *
 */
public class SMS {
	private String text;
	private String password;
	private byte[] passwordHash;
	private byte[] finalMessage;
	
	/**
	 * Costruttore con solo testo.
	 * @param text : il testo in chiaro del messaggio
	 */
	public SMS(String text){
		this.text = text;
	}
	
	/**
	 * Costruttore con testo e password che richiama il metodo di incapsulamento di text e password nel messaggio
	 * finale.
	 * 
	 * @param text : il testo in chiaro del messaggio
	 * @param password : la password che varrà "allegata" al messaggio
	 * @throws NoSuchAlgorithmException
	 * @throws IOException
	 */
	public SMS(String text, String password) throws NoSuchAlgorithmException, IOException{
		this.text = text;
		this.password = password;
		construct();
	}
	
	/**
	 * Setta la password utilizzando quella passata.
	 * @param password : la password che in un secondo momento verrà "allegata" al messaggio
	 */
	public void setPassword(String password){
		if(this.password == null){
			this.password = password;
		}
	}
	
	/**
	 * Public perchè può essere chiamato dall'esterno nel caso si costruisca esplicitamente passo a passo l'SMS.
	 * Crea un hash della password con sha-256 e crea la struttura hash(password) || text .
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
