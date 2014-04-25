package it.polimi.dima.watchdog;

import java.util.regex.Pattern;

import android.telephony.SmsManager;

/**
 * 
 * @author claudio, emanuele
 *
 */
public class SMSUtility {

	/**
	 * PublicKeyRequestCode
	 */
	public static String CODE1 = "C0DE1FFF";
	/**
	 * PublicKeySentCode
	 */
	public static String CODE2 = "C0DE2FFF";
	/**
	 * SecretQuestionSentCode
	 */
	public static String CODE3 = "C0DE3FFF";
	/**
	 * SecretAnswerAndPublicKeyHashSentCode
	 */
	public static String CODE4 = "C0DE4FFF";
	/**
	 * KeyValidatedCode
	 */
	public static String CODE5 = "C0DE5FFF";
	/**
	 * IDontWantToAssociate
	 */
	public static String CODE6 = "C0DE6FFF";
	public static String CODE7 = "C0DE7FFF";
	public static String CODE8 = "C0DE8FFF";
	
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	public static final short SMP_PORT = (short) 999;
	public static final short ECDH_PORT = (short) 7777;
	public static final short COMMAND_PORT = (short) 9999;
	
	
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
}
