package it.polimi.dima.watchdog;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;

import java.util.regex.Pattern;

import android.content.Context;
import android.telephony.SmsManager;
import android.util.Base64;
import android.widget.Toast;

/**
 * 
 * @author claudio, emanuele
 *
 */
public class SMSUtility {

	/**
	 * Da usare nel metodo onReceive delle classi che ricevono sms.
	 */
	public static final String SMS_EXTRA_NAME ="pdus";
	/**
	 * PublicKeyRequestCode (for SMP only) : header del messaggio che chiede all'altro la chiave pubblica.
	 */
	public static String CODE1 = "C0DE1FFF";
	/**
	 * PublicKeySentCode (for SMP only) : header del messaggio che manda la chiave pubblica a chi l'ha chiesta.
	 */
	public static String CODE2 = "C0DE2FFF";
	/**
	 * SecretQuestionSentCode (for SMP only) : header del messaggio che manda la domanda segreta all'altro.
	 */
	public static String CODE3 = "C0DE3FFF";
	/**
	 * SecretAnswerAndPublicKeyHashSentCode (for SMP only) : header del messaggio che manda l'hash di
	 * propria chiave pubblica || risposta segreta all'altro.
	 */
	public static String CODE4 = "C0DE4FFF";
	/**
	 * KeyValidatedCode (for SMP only) : header del messaggio che conferma l'avvenuta validazione della chiave
	 * pubblica.
	 */
	public static String CODE5 = "C0DE5FFF";
	/**
	 * IDontWantToAssociateCode (for SMP only) : header del messaggio che informa dell'abort del processo di
	 * validazione della chiave (vale per tutti i possibili errori, non solo per la mancata uguaglianza degli
	 * hash).
	 */
	public static String CODE6 = "C0DE6FFF";
	/**
	 * HereIsMyPublicKeyCode (for ECDH only) : header del messaggio di colui che invia per primo all'altro la
	 * propria chiave pubblica.
	 */
	public static String CODE7 = "C0DE7FFF";
	/**
	 * HereIsMyPublicKeyTooCode (for ECDH only) : header del messaggio di colui che invia all'altro la propria
	 * chiave pubblica solo dopo aver ricevuto quella dell'altro.
	 */
	public static String CODE8 = "C0DE8FFF";
	
	/**
	 * SirenOnCode (for commands only)
	 */
	public static String SIREN_ON = "C0DE01FF";
	
	/**
	 * SirenOffCode (for commands only)
	 */
	public static String SIREN_OFF = "C0DE01FF";
	
	/**
	 * MarkStolenCode (for commands only)
	 */
	public static String MARK_STOLEN = "C0DE01FF";
	
	/**
	 * MarkLostCode (for commands only)
	 */
	public static String MARK_LOST = "C0DE01FF";
	
	/**
	 * MarkLostOrStolenCode (for commands only)
	 */
	public static String MARK_LOST_OR_STOLEN = "C0DE01FF";
	
	/**
	 * MarkFoundCode (for commands only)
	 */
	public static String MARK_FOUND = "C0DE01FF";
	
	/**
	 * LocateCode (for commands only)
	 */
	public static String LOCATE = "C0DE01FF";
	
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	/**
	 * Porta su cui vengono ricevuti solo i messaggi del SMP.
	 */
	public static final short SMP_PORT = (short) 999;
	/**
	 * Porta su cui vengono ricevuti solo i messaggi di ECDH. (Probabilmente inutile TODO)
	 */
	public static final short ECDH_PORT = (short) 111;
	/**
	 * Porta su cui viene ricevuto solo il sale da dare in pasto al keygen di AES. TODO
	 */
	public static final short AES_KEYGEN_SALT_PORT = (short) 7777;
	/**
	 * Porta su cui vengono ricevuti solo i messaggi di controllo remoto.
	 */
	public static final short COMMAND_PORT = (short) 9999;
	/**
	 * Porta usata esclusivamente per i test (TODO aggiungere al manifest le classi che testano)
	 */
	public static final short TEST_PORT = (short) 777;
	
	
	/**
	 * Converte un array byte[] nella corrispondente stringa esadecimale.
	 * @param bytes : l'array di byte da convertire
	 * @return la stringa esadecimale corrispondente
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	/**
	 * Converte la stringa da esadecimale a byte[], oppure lancia un'eccezione.
	 * 
	 * @param s : la stringa in input
	 * @return la conversione in byte[] di s
	 * @throws IllegalArgumentException : se s non è una stringa esadecimale
	 */
	public static byte[] hexStringToByteArray(String s) throws IllegalArgumentException {
		if(!Pattern.compile("^[0-9A-Fa-f]+$").matcher(s).matches()){
			throw new IllegalArgumentException("La stringa in input non è esadecimale!!!");
		}
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
	
	/**
	 * Manda il messaggio dopo aver concatenato header e body.
	 * 
	 * @param number : il numero di telefono del destinatario
	 * @param port : la porta su cui il destinatario riceverà il messaggio
	 * @param header : l'header del messaggio
	 * @param body : il corpo del messaggio
	 */
	public static void sendMessage(String number, short port, byte[] header, byte[] body) {
		SmsManager man = SmsManager.getDefault();
		int dataLength = body == null ? 0 : body.length;
		byte[] message = new byte[header.length + dataLength];
		System.arraycopy(header, 0, message, 0, header.length);
		if(body != null) {
			System.arraycopy(body, 0, message, header.length, body.length);
		}
		man.sendDataMessage(number, null, port, message, null, null);
	}
	
	/**
	 * Ritorna l'header del messaggio
	 * @param msg
	 * @return
	 * @throws ArbitraryMessageReceivedException
	 */
	public static String getHeader(byte[] msg) throws ArbitraryMessageReceivedException {
		
		if(msg.length < SMSProtocol.HEADER_LENGTH){
			throw new ArbitraryMessageReceivedException("Messaggio inaspettato: troppo corto!!!");
		}
		byte[] header = new byte[SMSProtocol.HEADER_LENGTH];
		System.arraycopy(msg, 0, header, 0, SMSProtocol.HEADER_LENGTH);		
		
		
		return SMSUtility.bytesToHex(header);	
	}
	
	public static String getBody(byte[] msg) throws ArbitraryMessageReceivedException {
		
		if(msg.length < SMSProtocol.HEADER_LENGTH){
			throw new ArbitraryMessageReceivedException("Messaggio inaspettato: troppo corto!!!");
		}
		//android pare (dalla javadoc) non aggiungere un terminatore all'array di byte quando si usa getUserData(). In caso contrario la lunghezza
		//va decurtata di 1.
		int bodyLength = msg.length - SMSProtocol.HEADER_LENGTH;
		
		if(bodyLength == 0){
			return null;
		}
		
		byte[] body = new byte[bodyLength];
		System.arraycopy(msg, SMSProtocol.HEADER_LENGTH, body, 0, bodyLength);
		String bodyStr = Base64.encodeToString(body, Base64.DEFAULT);
		return bodyStr;
	}
	
	public static void showShortToastMessage(String message, Context ctx) {
		Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
		toast.show();
	}
}
