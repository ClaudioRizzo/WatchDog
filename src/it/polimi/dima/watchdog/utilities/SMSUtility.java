package it.polimi.dima.watchdog.utilities;

import it.polimi.dima.watchdog.exceptions.ArbitraryMessageReceivedException;
import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import it.polimi.dima.watchdog.sms.ParsableSMS;
import it.polimi.dima.watchdog.sms.commands.flags.StatusFree;
import java.util.regex.Pattern;
import android.content.Context;
import android.telephony.SmsManager;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

/**
 * Raccolta di stringhe e metodi utili per gli sms di SMP e Command Protocol.
 * 
 * @author claudio, emanuele
 *
 */
public class SMSUtility {
	/**
	 * Durata del timeout: 120 secondi
	 */
	public static final int TIMEOUT_LENGTH = 120;
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
	public static String SIREN_OFF = "C0DE02FF";
	
	/**
	 * MarkStolenCode (for commands only)
	 */
	public static String MARK_STOLEN = "C0DE03FF";
	
	/**
	 * MarkLostCode (for commands only)
	 */
	public static String MARK_LOST = "C0DE04FF";
	
	/**
	 * MarkLostOrStolenCode (for commands only)
	 */
	public static String MARK_LOST_OR_STOLEN = "C0DE05FF";
	
	/**
	 * MarkFoundCode (for commands only)
	 */
	public static String MARK_FOUND = "C0DE06FF";
	
	/**
	 * LocateCode (for commands only)
	 */
	public static String LOCATE = "C0DE01FF";
	
	/**
	 * Header del primo messaggio della sessione di comando.
	 */
	public static String M1_HEADER = "C0DE001F";
	/**
	 * Header del secondo messaggio della sessione di comando.
	 */
	public static String M2_HEADER = "C0DE002F";
	/**
	 * Header del quarto messaggio della sessione di comando.
	 */
	public static String M4_HEADER = "C0DE004F";
	
	private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	/**
	 * Porta su cui vengono ricevuti solo i messaggi del SMP.
	 */
	public static final short SMP_PORT = (short) 999;
	/**
	 * Porta su cui vengono ricevuti solo i messaggi dela sessione di controllo.
	 */
	public static final short COMMAND_PORT = (short) 9999;
	/**
	 * Porta usata esclusivamente per i test (TODO aggiungere al manifest le classi che testano)
	 */
	public static final short TEST_PORT = (short) 777;
	/**
	 * Indicatore del mio telefono per il timeout
	 */
	public static final String MY_PHONE = "my_phone";
	/**
	 * Indicatore di un altro telefono per il timeout
	 */
	public static final String OTHER_PHONE = "other_phone";
	
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
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] lunghezza del messaggio da inviare appena prima di inviarlo: " + message.length);
		Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] messaggio da inviare appena prima di inviarlo: " + Base64.encodeToString(message, Base64.DEFAULT));
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
			throw new ArbitraryMessageReceivedException("Messaggio inaspettato: troppo corto!!!");
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
			throw new ArbitraryMessageReceivedException("Messaggio inaspettato: troppo corto!!!");
		}
		//android pare (dalla javadoc) non aggiungere un terminatore all'array di byte quando si usa getUserData(). In caso contrario la lunghezza
		//va decurtata di 1.
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
	 * Visualizza un popup di errore.
	 * 
	 * @param message : il messaggio da mostrare
	 * @param ctx : il contesto corrente
	 */
	private static void showShortToastMessage(String message, Context ctx) {
		Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	/**
	 * Se qualcosa va storto nel SMP vengono cancellate tutte le preferenze relative
	 * all'altro utente e quest'ultimo viene esortato a fare lo stesso.
	 * 
	 * @param e : l'eccezione da gestire
	 * @param other : il numero di telefono dell'altro
	 * @param ctx : il contesto corrente 
	 */
	public static void handleErrorOrExceptionInSmp(Exception e, String other, Context ctx) {
		//In caso di MessageWillBeIgnoredException non si fa proprio nulla
		if(!(e instanceof MessageWillBeIgnoredException)){
			Log.i("[DEBUG_SMP]", "CAUGHT ERROR OR EXCEPTION");
			
			//notifico...
			if(e != null){
				SMSUtility.showShortToastMessage(e.getMessage(), ctx);
				e.printStackTrace();
			}
			
			//... e cancello i riferimenti all'altro utente...
			MyPrefFiles.eraseSmpPreferences(other, ctx);
			// TODO notificare il fragment di quello che è successo
			
			//... e lo notifico, esortandolo a fare lo stesso
			SMSUtility.sendMessage(other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
		}
	}
	
	/**
	 * Se qualcosa va storto nella sessione di comando, viene gestita l'eccezione e vengono cancellate le
	 * preferenze della command session.
	 * 
	 * @param e : l'eccezione da gestire
	 * @param other : il numero di telefono dell'altro
	 * @param ctx : il contesto corrente
	 */
	public static void handleErrorOrExceptionInCommandSession(Exception e, String other, Context ctx){
		if(!(e instanceof MessageWillBeIgnoredException)){
			Log.i("[DEBUG_SMP]", "CAUGHT ERROR OR EXCEPTION");
			
			//notifico...
			if(e != null){
				SMSUtility.showShortToastMessage(e.getMessage(), ctx);
				e.printStackTrace();
			}
			
			//... cancello i riferimenti all'altro utente nella sessione di comando...
			MyPrefFiles.eraseCommandSession(other, ctx);
			
			//... e torno in status free.
			MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusFree.CURRENT_STATUS, ctx);
			// TODO notificare il fragment di quello che è successo
		}
		else{
			Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] messaggio ignorato!!!");
		}
	}
	
	
}
