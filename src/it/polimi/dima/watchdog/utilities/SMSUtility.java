package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.TooLongResponseException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import java.util.regex.Pattern;
import android.telephony.SmsManager;
import android.util.Base64;

/**
 * Raccolta di stringhe e metodi utili per gli sms di SMP e Command Protocol.
 * 
 * @author claudio, emanuele
 *
 */
public class SMSUtility {
	
	//QUI UTILITIES VARIE
	
	/**
	 * Regex per i numeri di telefono.
	 */
	public static final String PHONE_REGEX = "\\+[0-9]+";
	
	/**
	 * Durata del timeout: 120 secondi.
	 */
	public static final int TIMEOUT_LENGTH = 120;
	
	/**
	 * Da usare nel metodo onReceive delle classi che ricevono sms.
	 */
	public static final String SMS_EXTRA_NAME ="pdus";
	
	/**
	 * Lunghezza fissata del body di m4 (subBody + padding).
	 */
	public static final int M4_BODY_LENGTH = 30;
	
	/**
	 * Usato solo nei metodi byte[] --> Hex e Hex --> byte[].
	 */
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	/**
	 * Indicatore del mio telefono per il timeout
	 */
	public static final String MY_PHONE = "my_phone";
	
	/**
	 * Indicatore di un altro telefono per il timeout
	 */
	public static final String OTHER_PHONE = "other_phone";
	
	/**
	 * Indicatore generico di un comando (serve al service di siren on e siren off)
	 */
	public static final String COMMAND = "command";
	
	
	
	//QUI I CODICI PER I MESSAGGI DEL SMP
	
	/**
	 * PublicKeyRequestCode (for SMP only) : header del messaggio che chiede all'altro la chiave pubblica.
	 */
	public static final String CODE1 = "C0DE1FFF";
	
	/**
	 * PublicKeySentCode (for SMP only) : header del messaggio che manda la chiave pubblica a chi l'ha chiesta.
	 */
	public static final String CODE2 = "C0DE2FFF";
	
	/**
	 * SecretQuestionSentCode (for SMP only) : header del messaggio che manda la domanda segreta all'altro.
	 */
	public static final String CODE3 = "C0DE3FFF";
	
	/**
	 * SecretAnswerAndPublicKeyHashSentCode (for SMP only) : header del messaggio che manda l'hash di
	 * propria chiave pubblica || risposta segreta all'altro.
	 */
	public static final String CODE4 = "C0DE4FFF";
	
	/**
	 * KeyValidatedCode (for SMP only) : header del messaggio che conferma l'avvenuta validazione della chiave
	 * pubblica.
	 */
	public static final String CODE5 = "C0DE5FFF";
	
	/**
	 * IDontWantToAssociateCode (for SMP only) : header del messaggio che informa dell'abort del processo di
	 * validazione della chiave (vale per tutti i possibili errori, non solo per la mancata uguaglianza degli
	 * hash).
	 */
	public static final String CODE6 = "C0DE6FFF";
	
	
	
	//QUI I CODICI PER I COMANDI
	
	/**
	 * SirenOnCode (for commands only)
	 */
	public static final String SIREN_ON = "C0DE01FF";
	
	/**
	 * SirenOffCode (for commands only)
	 */
	public static final String SIREN_OFF = "C0DE02FF";
	
	/**
	 * MarkStolenCode (for commands only)
	 */
	public static final String MARK_STOLEN = "C0DE03FF";
	
	/**
	 * MarkLostCode (for commands only)
	 */
	public static final String MARK_LOST = "C0DE04FF";
	
	/**
	 * MarkLostOrStolenCode (for commands only)
	 */
	public static final String MARK_LOST_OR_STOLEN = "C0DE05FF";
	
	/**
	 * MarkFoundCode (for commands only)
	 */
	public static final String MARK_FOUND = "C0DE06FF";
	
	/**
	 * LocateCode (for commands only)
	 */
	public static final String LOCATE = "C0DE07FF";
	
	
	
	//QUI IL CODICE DEL PASSWORD RESET
	/**
	 * PasswordChangeCode (for password reset only)
	 */
	public static final String PASSWORD_CHANGE = "C0DEBEEF";
	
	
	
	
	//QUI GLI HEADER DEI MESSAGGI DI COMANDO (M3_HEADER NON ESISTE)
	
	/**
	 * Header del primo messaggio della sessione di comando.
	 */
	public static final String M1_HEADER = "C0DE001F";
	
	/**
	 * Header del secondo messaggio della sessione di comando.
	 */
	public static final String M2_HEADER = "C0DE002F";
	
	/**
	 * Header del quarto messaggio della sessione di comando.
	 */
	public static final String M4_HEADER = "C0DE004F";
	
	
	
	
	//QUI I MESSAGGI DI RITORNO DI SIREN ON, SIREN OFF E ...
	
	/**
	 * Messaggio che indica il successo di siren on
	 */
	public static final String SIREN_ON_RESPONSE_OK = "siren_activated";
	
	/**
	 * Messaggio che indica il fallimento di siren on
	 */
	public static final String SIREN_ON_RESPONSE_KO = "siren_activated";
	
	/**
	 * Messaggio che indica il successo di siren off
	 */
	public static final String SIREN_OFF_RESPONSE_OK = "siren_activated";
	
	/**
	 * Messaggio che indica il fallimento di siren off
	 */
	public static final String SIREN_OFF_RESPONSE_KO = "siren_activated";
	
	/**
	 * Messaggio che indica il fallimento di locate
	 */
	public static final String LOCATE_RESPONSE_KO = "locate_failed";
	
	
	
	
	//QUI LE PORTE PER LA RICEZIONE SMS
	
	/**
	 * Porta su cui vengono ricevuti solo i messaggi del SMP.
	 */
	public static final short SMP_PORT = (short) 999;
	
	/**
	 * Porta su cui vengono ricevuti solo i messaggi dela sessione di controllo.
	 */
	public static final short COMMAND_PORT = (short) 9999;
	
	/**
	 * Porta usata esclusivamente per il cambio password.
	 */
	public static final short PASSWORD_RESET_PORT = (short) 7777;
	
	
	
	
	
	//QUI I METODI
	
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
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
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
	 * Manda il messaggio (comando o password reset).
	 * 
	 * @param number : il destinatario
	 * @param port : la porta
	 * @param message : il messaggio
	 */
	public static void sendSingleMessage(String number, short port, byte[] message){
		SmsManager man = SmsManager.getDefault();
		man.sendDataMessage(number, null, port, message, null, null);
	}
	
	/**
	 * Ritorna l'header di un messaggio.
	 * 
	 * @param msg : il messaggio
	 * @return l'header del messaggio
	 * @throws ArbitraryMessageReceivedException se il messaggio è troppo corto
	 */
	public static String getHeader(byte[] msg) throws ArbitraryMessageReceivedException {
		
		if(msg.length < ParsableSMS.HEADER_LENGTH){
			throw new ArbitraryMessageReceivedException();
		}
		byte[] header = new byte[ParsableSMS.HEADER_LENGTH];
		System.arraycopy(msg, 0, header, 0, ParsableSMS.HEADER_LENGTH);		
		
		
		return SMSUtility.bytesToHex(header);	
	}
	
	/**
	 * Ottiene il body di un messaggio.
	 * 
	 * @param msg : il messaggio
	 * @return il body del messaggio
	 * @throws ArbitraryMessageReceivedException se il messaggio è troppo corto
	 */
	public static String getBody(byte[] msg) throws ArbitraryMessageReceivedException {
		
		if(msg.length < ParsableSMS.HEADER_LENGTH){
			throw new ArbitraryMessageReceivedException();
		}
		int bodyLength = msg.length - ParsableSMS.HEADER_LENGTH;
		
		if(bodyLength == 0){
			return null;
		}
		
		byte[] body = new byte[bodyLength];
		System.arraycopy(msg, ParsableSMS.HEADER_LENGTH, body, 0, bodyLength);
		String bodyStr = Base64.encodeToString(body, Base64.DEFAULT);
		return bodyStr;
	}
	
	
	/**
	 * Ottiene la lunghezza del padding di m4 a partire dalla lunghezza target e quella effettiva.
	 * 
	 * @param m4BodyLength : la lunghezza target di m4
	 * @param dataLength : la lunghezza effettiva di m4
	 * @return la lunghezza del padding di m4
	 * @throws TooLongResponseException se la lunghezza effettiva è maggiore della lunghezza target
	 */
	public static int getM4BodyPaddingLength(int m4BodyLength, int dataLength) throws TooLongResponseException {
		
		if(dataLength > m4BodyLength){
			throw new TooLongResponseException();
		}
		return m4BodyLength - dataLength;
	}
}