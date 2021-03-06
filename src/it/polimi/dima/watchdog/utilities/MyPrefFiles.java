package it.polimi.dima.watchdog.utilities;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.errors.ErrorFactory;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.commands.session.StatusFree;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

/**
 * Classe che raccoglie tutte le preferenze che si usano in questa applicazione.
 * 
 * @author claudio, emanuele
 *
 */
public class MyPrefFiles {

	private MyPrefFiles() {
		throw new IllegalAccessError("Costruttore privato!!!");
	}	
	
	//QUI I NOMI DEI FILE
	
	/**
	 * File con i numeri di telefono associati
	 */
	public static final String ASSOCIATED = "associated";
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
	
	/**
	 * File in cui vengono salvati i riferimenti ai timeout attivi.
	 */
	public static final String TIMEOUTS_IDS = "timeouts_ids";
	
	/**
	 * File che contiene l'ultimo comando ricevuto tra siren_on e siren_off
	 */
	public static final String SIREN_FILE = "siren_file";
	
	
	
	
	
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
	
	/**
	 * valore chiave parziale per un timeout (verranno messi in coda a questa chiave i numeri di aspettante
	 * e aspettato.
	 */
	public static final String TIMEOUT_ID = "timeout_id";
	
	/**
	 * Chiave per il siren file
	 */
	public static final String SIREN_FILE_KEY = "siren_file_key";
	
	
	
	
	
	
	
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
	 * di crittazione/decrittazione di m3 in codifica Base64
	 */
	public static final String SESSION_KEY = "session_key"; //il valore sarà Base64
	
	/**
	 * Chiave paziale a cui verrà preposto il numero di telefono dell'altro utente; indica il vettore
	 * di inizializzazione che verrà usato nella crittazione/decrittazione di m3 in codifica Base64.
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
	public static final String TEMP_COMMAND = "temp_command";
	
	/**
	 * Chiave parziale a cui verrà preposto il numero di telefono dell'altro utente; indica la password che
	 * si vuole allegare al messaggio da mandare. NON è in Base64.
	 */
	public static final String OTHER_PASSWORD = "other_password";
	
	/**
	 * Chiave paziale a cui verrà preposto il numero di telefono dell'altro utente; indica il vettore
	 * di inizializzazione che verrà usato nella crittazione/decrittazione di m4 in codifica Base64. 
	 */
	public static final String IV_FOR_M4 = "m4_iv";
	
	/**
	 * Chiave paziale a cui verrà preposto il numero di telefono dell'altro utente; indica la chiave
	 * di crittazione/decrittazione di m4 in codifica Base64
	 */
	public static final String KEY_FOR_M4 = "m4_key";
	
	
	
	
	
	
	//DA QUI IN POI CI SONO I METODI
	
	/**
	 * Ottiene la perferenza puntata da key in fileName nel contesto ctx.
	 * 
	 * @param fileName : il file in cui prendere la preferenza
	 * @param key : la chiave che punta alla preferenza
	 * @param context : il contesto corrente
	 * @return la preferenza trovata
	 * @throws NoSuchPreferenceFoundException se la preferenza non esiste
	 */
	public static String getMyPreference(String fileName, String key, Context context) throws NoSuchPreferenceFoundException {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String preference = sp.getString(key, null);
		
		if(preference == null){
			throw new NoSuchPreferenceFoundException(ErrorFactory.MISSING_PREFERENCE);
		}
		return preference;
	}
	
	/**
	 * Ritorna lo stato del wizard iniziale.
	 * 
	 * @param context : il contesto corrente
	 * @return true se il wizard è stato completato, false altrimenti
	 */
	public static boolean isWizardDone(Context context) {
		SharedPreferences sp = context.getSharedPreferences(MyPrefFiles.PREF_INIT, Context.MODE_PRIVATE);
		return sp.getBoolean(MyPrefFiles.WIZARD_DONE, false);
	}
	
	
	/**
	 * Setta in fileName nel contesto ctx una preferenza che ha key come chiave e value come valore.
	 * 
	 * @param fileName : il file in cui inserire la preferenza
	 * @param key : la chiave della preferenza
	 * @param value : il valore della preferenza
	 * @param context : il contesto corrente
	 */
	//se value è una chiave deve essere Base64
	public static void setMyPreference(String fileName, String key, String value, Context context) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}
	
	/**
	 * Salva nelle preferenze il fatto che il wizard è stato completato.
	 * 
	 * @param context : il contesto corrente
	 */
	public static void setWizardDone(Context context) {
		SharedPreferences sp = context.getSharedPreferences(MyPrefFiles.PREF_INIT, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.putBoolean(MyPrefFiles.WIZARD_DONE, true);
		editor.commit();
	}
	
	/**
	 * Cancella da fileName la preferenza indicata da key nel contesto ctx.
	 * 
	 * @param fileName : il file da cui cancellare la preferenza
	 * @param key : la chiave per individuare la preferenza da cancellare
	 * @param context : il contesto corrente
	 */
	public static void deleteMyPreference(String fileName, String key, Context context) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(key);
		editor.commit();
	}
	
	/**
	 * Cerca se esiste una preferenza in fileName con la chiave key e il contesto ctx.
	 * 
	 * @param fileName : il file in cui cercare la preferenza
	 * @param key : la chiave per cercare la preferenza
	 * @param context : il contesto corrente
	 * @return true se la preferenza esiste, false altrimenti
	 */
	public static boolean existsPreference(String fileName, String key, Context context) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
		String value = sp.getString(key, null);
		if(value == null){
			return false;
		}
		return true;
	}
	
	/**
	 * Sostituisce una vecchia preferenza con una nuova.
	 * 
	 * @param fileName: il file in cui sostituire la preferenza
	 * @param key : la chiave per cercare la preferenza
	 * @param newValue : il nuovo valore per la preferenza
	 * @param context : il contesto corrente
	 */
	public static void replacePreference(String fileName, String key, String newValue, Context context){
		MyPrefFiles.deleteMyPreference(fileName, key, context);
		MyPrefFiles.setMyPreference(fileName, key, newValue, context);
	}
	
	/**
	 * Se qualcosa va storto nel SMP tutte le preferenze relative all'altro utente vanno cancellate.
	 * 
	 * @param phoneNumber : il numero di telefono dell'altro
	 * @param context : il contesto corrente
	 */
	public static void eraseSmpPreferences(String phoneNumber, Context context){
		//cancellazione della chiave già validata dell'altro utente, se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, phoneNumber, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYRING, phoneNumber, context);
		}
		//cancellazione della chiave dell'altro utente in attesa di validazione, se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, phoneNumber, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.KEYSQUARE, phoneNumber, context);
		}
		//cancellazione del segreto comune (quello scambiato tramite ECDH), se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, context);
		}
		//cancellazione del sale delle password dell'altro utente, se esiste
		if(MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, phoneNumber, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.HASHRING, phoneNumber, context);
		}
		
		//cancellazione della secret question e delle secret answer inviate all'altro utente
		if(MyPrefFiles.existsPreference(MyPrefFiles.SECRET_Q_A, phoneNumber + MyPrefFiles.SECRET_QUESTION, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.SECRET_Q_A, phoneNumber + MyPrefFiles.SECRET_QUESTION, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.SECRET_Q_A, phoneNumber + MyPrefFiles.SECRET_ANSWER, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.SECRET_Q_A, phoneNumber + MyPrefFiles.SECRET_ANSWER, context);
		}
		
		List<String> keys = MyPrefFiles.createKeysForSmpStatus(phoneNumber);
		//resetta il file che tiene lo stato del SMP
		for(String s : keys){
			if(MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, s, context)){
				MyPrefFiles.deleteMyPreference(MyPrefFiles.SMP_STATUS, s, context);
			}
		}
		//se sto aspettando dall'altro la prova che validi la sua chiave pubblica, non devo più aspettarla
		if(MyPrefFiles.existsPreference(MyPrefFiles.PENDENT, phoneNumber, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.PENDENT, phoneNumber, context);
		}
		
		//dato che non ha più senso avere uno stato della sessione di comando, lo cancello se esiste
		eraseCommandSession(phoneNumber, context);
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + phoneNumber, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + phoneNumber, context);
		}
		
		if(MyPrefFiles.existsPreference(MyPrefFiles.ASSOCIATED, phoneNumber, context)) {
			MyPrefFiles.deleteMyPreference(MyPrefFiles.ASSOCIATED, phoneNumber, context);
		}
	}
	
	/**
	 * Metodo che cerca nelle preferenze se esistono riferimenti ad un determinato numero di telefono.
	 * @param phoneNumber : il numero di telefono dell'altro
	 * @param context : il contesto corrente
	 * @return true se esiste almeno un riferimento, false altrimenti
	 */
	public static boolean iHaveSomeReferencesToThisUser(String phoneNumber, Context context){
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYRING, phoneNumber, context)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.KEYSQUARE, phoneNumber, context)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.SHARED_SECRETS, phoneNumber, context)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.HASHRING, phoneNumber, context)){
			return true;
		}
		
		List<String> keys = MyPrefFiles.createKeysForSmpStatus(phoneNumber);
		for(String s : keys){
			if(MyPrefFiles.existsPreference(MyPrefFiles.SMP_STATUS, s, context)){
				return true;
			}
		}
		
		if(MyPrefFiles.existsPreference(MyPrefFiles.PENDENT, phoneNumber, context)){
			return true;
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.ASSOCIATED, phoneNumber, context)){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Stabilisce se entrambe le parti hanno terminato con successo il SMP.
	 * 
	 * @param phoneNumber : il numero di telefono dell'altro
	 * @param context : il contesto corrente
	 * @return true se entrambe le parti hanno terminato con successo il SMP, false altrimenti
	 */
	public static boolean isSmpSuccessfullyFinishedByBoth(String phoneNumber, Context context){
		List<String> keys = MyPrefFiles.createKeysForSmpStatus(phoneNumber);
		
		//L'idea è che, a SMP concluso da entrambe le parti, devono esistere valori per tutte le chiavi
		for(String s : keys){
			if(!existsPreference(MyPrefFiles.SMP_STATUS, s, context)){
				return false;
			}
		}
		return true;
	}
	
	/**
	 * Ritorna tutti i valori delle preferenze contenuti in fileName.
	 * 
	 * @param fileName : il nome del file di cui si vogliono tutte le preferenze
	 * @param context : il contesto corrente
	 * @return tutti i valori delle preferenze contenuti in fileName
	 */
	public static Map<String, ?> getPrefMap(String fileName, Context context) {
		SharedPreferences sp = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
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
	
	/**
	 * Cancella l'intera sessione di comando e resetta a status free lo stato della comunicazione.
	 * 
	 * @param other : il numero di telefono dell'altro
	 * @param context : il contesto corrente
	 */
	public static void eraseCommandSession(String other, Context context){
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.SESSION_KEY, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.SESSION_KEY, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.TEMP_COMMAND, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.TEMP_COMMAND, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.OTHER_PASSWORD, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.OTHER_PASSWORD, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV_FOR_M4, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV_FOR_M4, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.KEY_FOR_M4, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.KEY_FOR_M4, context);
		}
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + other, StatusFree.CURRENT_STATUS, context);
	}
	
	/**
	 * Metodo che cancella iv e chiave dopo il loro utilizzo per creare/crittare/decrittare m3.
	 * 
	 * @param other : il numero di telefono dell'altro
	 * @param context : il contesto corrente
	 */
	public static void deleteUselessCommandSessionPreferencesForM3(String other, Context context){
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.SESSION_KEY, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.SESSION_KEY, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.TEMP_COMMAND, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.TEMP_COMMAND, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.OTHER_PASSWORD, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.OTHER_PASSWORD, context);
		}
	}
	
	/**
	 * Metodo che cancella iv e chiave dopo il loro utilizzo per crittare/decrittare m4.
	 * 
	 * @param other : il numero di telefono dell'altro
	 * @param context : il contesto corrente
	 */
	public static void deleteUselessCommandSessionPreferencesForM4(String other, Context context){
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.KEY_FOR_M4, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.KEY_FOR_M4, context);
		}
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV_FOR_M4, context)){
			MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, other + MyPrefFiles.IV_FOR_M4, context);
		}
	}
	
	/**
	 * Ritorna la mia chiave privata.
	 * 
	 * @param context : il contesto corrente
	 * @return la mia chiave privata
	 * @throws NoSuchPreferenceFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PrivateKey getMyPrivateKey(Context context) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException{
		String mPrivBase64 = MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, context);
		byte[] mPrivEncoded = Base64.decode(mPrivBase64, Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC);
		return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(mPrivEncoded));
	}
	
	/**
	 * Ritorna la mia chiave pubblica.
	 * 
	 * @param context : il contesto corrente
	 * @return la mia chiave pubblica
	 * @throws NoSuchPreferenceFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getMyPublicKey(Context context) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException{
		String mPubBase64 = MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, context);
		byte[] mPubEncoded = Base64.decode(mPubBase64, Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC);
		return keyFactory.generatePublic(new X509EncodedKeySpec(mPubEncoded));
	}
	
	/**
	 * Ritorna la chiave pubblica dell'altro, prelevandola dal keyring
	 * 
	 * @param context : il contesto corrente
	 * @param other : il numero di telefono dell'altro
	 * @return la chiave pubblica dell'altro
	 * @throws NoSuchPreferenceFoundException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public static PublicKey getOtherPublicKey(Context context, String other) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException{
		String oPubBase64 = MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, other, context);
		byte[] oPubEncoded = Base64.decode(oPubBase64, Base64.DEFAULT);
		KeyFactory keyFactory = KeyFactory.getInstance(CryptoUtility.EC);
		return keyFactory.generatePublic(new X509EncodedKeySpec(oPubEncoded));
	}
	
	/**
	 * Ritorna la chiave dell'aes relativa a m3 o m4.
	 * 
	 * @param context
	 * @param other
	 * @param m3_or_m4 : se è true si riferisce a m3, altrimenti a m4
	 * @return
	 * @throws NoSuchPreferenceFoundException
	 */
	public static Key getSymmetricCryptoKey(Context context, String other, boolean m3_or_m4) throws NoSuchPreferenceFoundException{
		String key = null;
		if(m3_or_m4){
			key = MyPrefFiles.SESSION_KEY;
		}
		else{
			key = MyPrefFiles.KEY_FOR_M4;
		}
		String decKey = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, other + key, context);
		byte[] decKeyValue = Base64.decode(decKey, Base64.DEFAULT);
		return new SecretKeySpec(decKeyValue, CryptoUtility.AES_256);
	}
	
	/**
	 * Ritorna l'initialization vector per m3 o m4.
	 * 
	 * @param context
	 * @param other
	 * @param m3_or_m4 : se è true si riferisce a m3, altrimenti a m4
	 * @return
	 * @throws NoSuchPreferenceFoundException
	 */
	public static byte[] getIV(Context context, String other, boolean m3_or_m4) throws NoSuchPreferenceFoundException{
		String key = null;
		if(m3_or_m4){
			key = MyPrefFiles.IV;
		}
		else{
			key = MyPrefFiles.IV_FOR_M4;
		}
		String iv = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, other + key, context);
		return Base64.decode(iv, Base64.DEFAULT);
	}
	
	/**
	 * Ritorna il sale della password di un'altro utente.
	 * 
	 * @param context : il contesto corrente
	 * @param other : il numero di telefono dell'altro
	 * @return il sale cercato
	 * @throws NoSuchPreferenceFoundException
	 */
	public static byte[] getOtherPasswordSalt(Context context, String other) throws NoSuchPreferenceFoundException{
		String salt = MyPrefFiles.getMyPreference(MyPrefFiles.HASHRING, other, context);
		return Base64.decode(salt, Base64.DEFAULT);
	}
	
	/**
	 * Ritorna il comando della sessione.
	 * 
	 * @param context : il contesto corrente
	 * @param other : il numero di telefono dell'altro
	 * @return il comando della sessione
	 * @throws NoSuchPreferenceFoundException
	 */
	public static byte[] getSessionCommand(Context context, String other) throws NoSuchPreferenceFoundException{
		String key = other + MyPrefFiles.TEMP_COMMAND;
		String command = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, key, context);
		return Base64.decode(command, Base64.DEFAULT);
	}
	
	/**
	 * Ritorna la password (dell'altro) digitata e storata prima di mandare m1.
	 * 
	 * @param context : il contesto corrente
	 * @param other : il numero di telefono dell'altro
	 * @return la password
	 * @throws NoSuchPreferenceFoundException
	 */
	public static byte[] getPreviouslyStoredPassword(Context context, String other) throws NoSuchPreferenceFoundException{
		String passwordKey = other + MyPrefFiles.OTHER_PASSWORD;
		return MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, passwordKey, context).getBytes();
	}
	
	/**
	 * Ritorna l'hash della mia password.
	 * 
	 * @param context : il contesto corrente
	 * @return l'hash della mia password
	 * @throws NoSuchPreferenceFoundException
	 */
	public static byte[] getMyPasswordHash(Context context) throws NoSuchPreferenceFoundException{
		String storedHash = MyPrefFiles.getMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_HASH, context);
		return Base64.decode(storedHash, Base64.DEFAULT);
	}
	
	/**
	 * Ritorna il sale della mia password.
	 * 
	 * @param context : il conststo corrente
	 * @return il sale della mia password
	 * @throws NoSuchPreferenceFoundException
	 */
	public static byte[] getMyPasswordSalt(Context context) throws NoSuchPreferenceFoundException{
		String storedHash = MyPrefFiles.getMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_SALT, context);
		return Base64.decode(storedHash, Base64.DEFAULT);
	}
	
	public static String getSecQuestionIfExists(Context context, String other) {
		if(existsPreference(MyPrefFiles.SECRET_Q_A, other + MyPrefFiles.SECRET_QUESTION, context)) {
			try {
				return getMyPreference(MyPrefFiles.SECRET_Q_A, other + MyPrefFiles.SECRET_QUESTION, context);
			} catch (NoSuchPreferenceFoundException e) {
				return null;
			}
		}
		return null;
	}
}