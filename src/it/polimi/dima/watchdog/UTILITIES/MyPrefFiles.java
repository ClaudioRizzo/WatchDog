package it.polimi.dima.watchdog.UTILITIES;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Classe che raccoglie tutte le preferenze che si usano in questa applicazione
 * @author claudio, emanuele
 *
 */
public class MyPrefFiles {

	private MyPrefFiles() {
		throw new IllegalAccessError("Costruttore privato!!!");
	}	
	
	//QUI I NOMI DEI FILE
	
	/**
	 * File che contiene il mio numero di telefono
	 */
	public static final String MY_NUMBER_FILE = "my_number_file";
	
	/**
	 * File delle richieste in attesa di accettazione da parte mia
	 */
	public static final String PENDENT =  "pendent";
	
	/**
	 * File che contiene l'indicazione dello stato del wizard (fatto/non fatto)
	 */
	public static final String PREF_INIT = "my_init_file";
	
	/**
	 * File che contiene l'hash della propria password e il sale corrispondente.
	 */
	public static final String PASSWORD_AND_SALT = "password_and_salt";
	
	/**
	 * File che contiene le chiavi pubbliche già validate.
	 */
	public static final String KEYRING = "keyring";
	
	/**
	 * File che contiene i sali delle password di tutti i telefoni abbinati (in modo che quando bisogna digitare
	 * su questo telefono una password di un altro telefono da "salvare" si possa fare l'hash correttamente).
	 */
	public static final String HASHRING = "hashring"; //OVVIO PUN xD
	
	/**
	 * File che contiene le proprie chiavi.
	 */
	public static final String MY_KEYS = "mykeys";
	
	/**
	 * File che, per ogni telefono asociato, contiene domanda e risposta segrete a cui lui dovrà rispondere
	 * perchè io possa validare la sua chiave pubblica nel SMP.
	 */
	public static final String SECRET_Q_A = "secret_question_and_answer";
	
	/**
	 * File che contiene le chiavi pubbliche non ancora validate.
	 */
	public static final String KEYSQUARE = "keysquare"; //OVVIO PUN xD
	
	/**
	 * File che contiene tutti i dati di una "sessione di comando"
	 */
	public static final String COMMAND_SESSION = "command_session";
	
	/**
	 * File che contiene per ogni telefono a me associato con SMP il segreto condiviso tra me e lui.
	 */
	public static final String SHARED_SECRETS = "shared_secrets";
	
	/**
	 * File che per ogni numero di telefono tiene traccia di cosa è stato già fatto e di cosa manca da fare
	 * nel SMP. I riferimenti possono essere cancellati se incompleti alla ricezione di IDontWantToAssociate
	 * e se completi alla ricezione di un apposito messaggio di reset.
	 */
	public static final String SMP_STATUS = "smp_status";
	
	
	
	
	
	
	
	//DA QUI IN POI CHIAVI
	
	//Chiavi generiche
	/**
	 * Valore che simboleggia il mio numero di telefono
	 */
	public static final String MY_PHONE_NUMBER = "my_phone_number";
	
	/**
	 * valore chiave per sapere se il wizard è finito o no.
	 */
	public static final String WIZARD_DONE = "wizardDone";
	
	/**
	 * valore "chiave" per la propria chiave pubblica.
	 */
	public static final String MY_PUB = "my_public_key";
	
	/**
	 * valore "chiave" per la propria chiave privata. 
	 */
	public static final String MY_PRIV = "my_private_key";
	
	/**
	 * valore chiave per l'hash della propria password.
	 */
	public static final String MY_PASSWORD_HASH = "my_password_hash";
	
	/**
	 * valore chiave per il sale con cui è stato costruito l'hash della propria password.
	 */
	public static final String MY_PASSWORD_SALT = "my_password_salt";
	
	/**
	 * valore chiave parziale per la risposta segreta a cui sarà premesso il numero dell'altro.  
	 */
	public static final String SECRET_ANSWER = "secret_answer";
	
	/**
	 * valore chiave parziale per la domanda segreta a cui sarà premesso il numero dell'altro.  
	 */
	public static final String SECRET_QUESTION = "secret_question";
	
	
	
	
	
	
	//Qui i nomi di chiave parziali per il file SMP_STATUS
	
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che gli ho mandato la richiesta
	 * di avere la sua chiave pubblica.
	 */
	public static final String PUB_KEY_REQUEST_FORWARDED = "pkrf";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che ho ricevuto la sua chiave pubblica.
	 */
	public static final String PUB_KEY_RECEIVED = "pkr";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che gli ho mandato la domanda segreta.
	 */
	public static final String SECRET_QUESTION_FORWARDED = "sqf";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che ho ricevuto da lui un hash.
	 */
	public static final String HASH_RECEIVED = "hr";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che ho mandato all'altro il sale con cui
	 * è stato generato l'hash della mia password salvato nel mio file PASSWORD_AND_SALT.
	 */
	public static final String ACK_AND_SALT_FORWARDED = "asf";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che ho ricevuto da lui una richiesta di
	 * mandargli la mia chiave pubblica.
	 */
	public static final String PUB_KEY_REQUEST_RECEIVED = "pkrr";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che gli ho mandato la mia chiave
	 * pubblica.
	 */
	public static final String PUB_KEY_FORWARDED = "pkf";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che ho ricevuto da lui la domanda segreta.
	 */
	public static final String SECRET_QUESTION_RECEIVED = "sqr";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che gli ho mandato l'hash di mia chiave
	 * pubblica || risposta alla domanda segreta.
	 */
	public static final String HASH_FORWARDED = "hf";
	/**
	 * Chiave parziale postfissa al numero dell'altro che indica che ho ricevuto da lui il sale della sua
	 * password e la conferma che tutto è andato bene.
	 */
	public static final String ACK_AND_SALT_RECEIVED = "asr";
	
	
	
	
	
	
	
	//Qui i nomi di chiave parziali per il file COMMAND_SESSION
	
	/**
	 * Chiave paziale a cui verrà preposto il numero di telefono dell'altro utente; indica la chiave
	 * di crittazione/decrittazione del prossimo messaggio in codifica Base64
	 */
	public static final String SESSION_KEY = "session_key"; //il valore sarà Base64
	
	/**
	 * Chiave paziale a cui verrà preposto il numero di telefono dell'altro utente; indica il vettore
	 * di inizializzazione che verrà usato nella crittazione/decrittazione del prossimo messaggio in codifica
	 * Base64.
	 */
	public static final String IV = "iv"; //il valore sarà Base64
	
	/**
	 * Chiave paziale a cui verrà concatenato il numero di telefono dell'altro utente; indica un valore
	 * stringa che simboleggia lo stato della sessione di comando; il valore puntato può assumere solo i
	 * seguenti 8 valori: FREE, FLAG_M1_SENT, FLAG_M1_RECEIVED, FLAG_M2_SENT, FLAG_M2_RECEIVED,
	 * FLAG_M3_SENT, FLAG_M3_RECEIVED, FLAG_M4_RECEIVED.
	 */
	public static final String COMMUNICATION_STATUS_WITH = "me_with_";
	
	/**
	 * Chiave parziale a cui verrà preposto il numero di telefono dell'altro utente; indica il comando che
	 * si vuole mandare nella sessione. Tale comando deve essere contenuto in SMSUtility e NON è in Base64.
	 */
	public static final String TEMP_COMMAND = "temp_command";//TODO ricordarsi di inizializzare
	
	/**
	 * Chiave parziale a cui verrà preposto il numero di telefono dell'altro utente; indica la password che
	 * si vuole allegare al messaggio da mandare. NON è in Base64.
	 */
	public static final String OTHER_PASSWORD = "other_password";//TODO ricordarsi di inizializzare
	
	
	
	
	
	
	
	//DA QUI IN POI CI SONO I METODI
	
	/**
	 * Ottiene la perferenza puntata da key in fileName nel contesto ctx.
	 * 
	 * @param fileName : il file in cui prendere la preferenza
	 * @param key : la chiave che punta alla preferenza
	 * @param ctx : il contesto
	 * @return la preferenza trovata
	 * @throws NoSuchPreferenceFoundException se la preferenza non esiste
	 */
	public static String getMyPreference(String fileName, String key, Context ctx) throws NoSuchPreferenceFoundException {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String preference = sp.getString(key, null);
		
		if(preference == null){
			throw new NoSuchPreferenceFoundException("I/O Error");
		}
		return preference;
	}
	
	/**
	 * Setta in fileName nel contesto ctx una preferenza che ha key come chiave e value come valore.
	 * 
	 * @param fileName : il file in cui inserire la preferenza
	 * @param key : la chiave della preferenza
	 * @param value : il valore della preferenza
	 * @param ctx : il contesto
	 */
	//se value è una chiave deve essere Base64
	public static void setMyPreference(String fileName, String key, String value, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * Cancella da fileName la preferenza indicata da key nel contesto ctx.
	 * 
	 * @param fileName : il file da cui cancellare la preferenza
	 * @param key : la chiave per individuare la preferenza da cancellare
	 * @param ctx : il contesto
	 */
	public static void deleteMyPreference(String fileName, String key, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
	
	/**
	 * Cerca se esiste una preferenza in fileName con la chiave key e il contesto ctx.
	 * 
	 * @param fileName : il file in cui cercare la preferenza
	 * @param key : la chiave per cercare la preferenza
	 * @param ctx : il contesto
	 * @return true se la preferenza esiste, false altrimenti
	 */
	public static boolean existsPreference(String fileName, String key, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String value = sp.getString(key, null);
		if(value == null){
			return false;
		}
		return true;
	}
	
	public static void replacePreference(String fileName, String key, String newValue, Context ctx){
		MyPrefFiles.deleteMyPreference(fileName, key, ctx);
		MyPrefFiles.setMyPreference(fileName, key, newValue, ctx);
	}
	
	/**
	 * Se qualcosa va storto nel SMP tutte le preferenze relative all'altro utente vanno cancellate.
	 */
	public static void eraseSmpPreferences(String phoneNumber, Context ctx){
		//cancellazione della chiave già validata dell'altro utente, se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYRING, phoneNumber, ctx);
		}
		//cancellazione della chiave dell'altro utente in attesa di validazione, se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYSQUARE, phoneNumber, ctx);
		}
		//cancellazione del segreto comune (quello scambiato tramite ECDH), se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, ctx);
		}
		//cancellazione del sale delle password dell'altro utente, se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.HASHRING, phoneNumber, ctx);
		}
		
		List<String> keys = MyPrefFiles.createKeysForSmpStatus(phoneNumber);
		//resetta il file che tiene lo stato del SMP
		for(String s : keys){
			if(MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, s, ctx)){
				MyPrefFiles.deleteMyPreference(MyPrefFiles.SMP_STATUS, s, ctx);
			}
		}
		//se sto aspettando dall'altro la prova che validi la sua chiave pubblica, non devo più aspettarla
		if(MyPrefFiles.existsPreference(MyPrefFiles.PENDENT, phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.PENDENT, phoneNumber, ctx);
		}
		
		//dato che non ha più senso avere uno stato della sessione di comando, lo cancello se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + phoneNumber, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + phoneNumber, ctx);
		}
	}
	
	/**
	 * Metodo che cerca nelle preferenze se esistono riferimenti ad un determinato numero di telefono.
	 * @param phoneNumber : il numero di telefono dell'altro
	 * @param ctx : il contesto
	 * @return true se esiste almeno un riferimento, false altrimenti
	 */
	public static boolean iHaveSomeReferencesToThisUser(String phoneNumber, Context ctx){
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, phoneNumber, ctx)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, phoneNumber, ctx)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, ctx)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, phoneNumber, ctx)){
			return true;
		}
		
		List<String> keys = MyPrefFiles.createKeysForSmpStatus(phoneNumber);
		for(String s : keys){
			if(MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, s, ctx)){
				return true;
			}
		}
		
		if(MyPrefFiles.existsPreference(MyPrefFiles.PENDENT, phoneNumber, ctx)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Stabilisce se entrambe le parti hanno terminato con successo il SMP.
	 * 
	 * @param phoneNumber : il numero di telefono dell'altro
	 * @param ctx : il contesto
	 * @return true se entrambe le parti hanno terminato con successo il SMP, false altrimenti
	 */
	public static boolean isSmpSuccessfullyFinishedByBoth(String phoneNumber, Context ctx){
		List<String> keys = MyPrefFiles.createKeysForSmpStatus(phoneNumber);
		
		//L'idea è che, a SMP concluso da entrambe le parti, devono esistere valori per tutte le chiavi
		for(String s : keys){
			if(!existsPreference(MyPrefFiles.SMP_STATUS, s, ctx)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Ritorna tutti i valori delle preferenze contenuti in fileName.
	 * @param fileName : il nome del file di cui si vogliono tutte le preferenze
	 * @param ctx : il contesto
	 * @return tutti i valori delle preferenze contenuti in fileName
	 */
	public static Map<String, ?> getPrefMap(String fileName, Context ctx) {
		SharedPreferences sp = ctx.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		return sp.getAll();
	}
	
	/**
	 * Crea tutte le chiavi del file SMP_STATUS a partire dal numero di telefono dell'altro.
	 * 
	 * @param phoneNumber : il numero di telefono dell'altro
	 * @return la lista delle chiavi da usare nel file SMP_STATUS
	 */
	private static List<String> createKeysForSmpStatus(String phoneNumber){
		List<String> keys = new ArrayList<String>();
		keys.add(phoneNumber + MyPrefFiles.PUB_KEY_REQUEST_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.PUB_KEY_RECEIVED);
		keys.add(phoneNumber + MyPrefFiles.SECRET_QUESTION_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.HASH_RECEIVED);
		keys.add(phoneNumber + MyPrefFiles.ACK_AND_SALT_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.PUB_KEY_REQUEST_RECEIVED);
		keys.add(phoneNumber + MyPrefFiles.PUB_KEY_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.SECRET_QUESTION_RECEIVED);
		keys.add(phoneNumber + MyPrefFiles.HASH_FORWARDED);
		keys.add(phoneNumber + MyPrefFiles.ACK_AND_SALT_RECEIVED);
		return keys;
	}
	
	
	public static void eraseCommandSession(String other, Context ctx){
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.SESSION_KEY, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.SESSION_KEY, ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV, ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.TEMP_COMMAND, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.TEMP_COMMAND, ctx);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.OTHER_PASSWORD, ctx)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.OTHER_PASSWORD, ctx);
		}
	}
	
}