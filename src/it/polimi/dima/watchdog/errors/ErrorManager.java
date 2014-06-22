package it.polimi.dima.watchdog.errors;

import it.polimi.dima.watchdog.exceptions.MessageWillBeIgnoredException;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class ErrorManager {
	
	
	/**
	 * Visualizza un popup di errore.
	 * 
	 * @param message : il messaggio da mostrare
	 * @param ctx : il contesto corrente
	 */
	public static void showShortToastMessage(String message, Context ctx) {
		Toast toast = Toast.makeText(ctx, message, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	
	public static void handleFatalError(String message, Context context){
		String popupMessage = getErrorMessage(message);
		CreatePopup(popupMessage, context);
		
		System.exit(-1);
	}
	
	public static void handleNonFatalError(String message, Context context){
		String popupMessage = getErrorMessage(message);
		CreatePopup(popupMessage, context);
	}
	
	
	/**
	 * Se qualcosa va storto nel SMP vengono cancellate tutte le preferenze relative
	 * all'altro utente e quest'ultimo viene esortato a fare lo stesso.
	 * 
	 * @param e : l'eccezione da gestire
	 * @param other : il numero di telefono dell'altro
	 * @param ctx : il contesto corrente 
	 */
	public static void handleErrorOrExceptionInSmp(Exception e, String other, Context context) {
		//In caso di MessageWillBeIgnoredException non si fa proprio nulla
		if(!(e instanceof MessageWillBeIgnoredException)){
			Log.i("[DEBUG_SMP]", "CAUGHT ERROR OR EXCEPTION");
			
			//notifico...
			if(e != null){
				String popupMessage = getErrorMessage(e);
				CreatePopup(popupMessage, context);
				e.printStackTrace();
			}
			
			//... e cancello i riferimenti all'altro utente...
			MyPrefFiles.eraseSmpPreferences(other, context);
			
			//... e lo notifico, esortandolo a fare lo stesso
			SMSUtility.sendMessage(other, SMSUtility.SMP_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE6), null);
		}
	}
	
	/**
	 * Se qualcosa va storto nella sessione di comando, viene gestita l'eccezione e vengono cancellate le
	 * preferenze della command session; lo stato della comunicazione con l'altro viene resettato a free.
	 * 
	 * @param e : l'eccezione da gestire
	 * @param other : il numero di telefono dell'altro
	 * @param ctx : il contesto corrente
	 * @param hidden : indica se il messaggio di errore dovrà essere nascosto o visualizzato
	 */
	public static void handleErrorOrExceptionInCommandSession(Exception e, String other, Context context, boolean hidden){
		if(!(e instanceof MessageWillBeIgnoredException)){
			Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] CAUGHT ERROR OR EXCEPTION");
			
			//notifico...
			if(e != null){
				if(!hidden){
					String popupMessage = getErrorMessage(e);
					CreatePopup(popupMessage, context);
				}
				e.printStackTrace();
			}
			
			//... cancello i riferimenti all'altro utente nella sessione di comando (fa anche tornare immediatamente in status free). 
			MyPrefFiles.eraseCommandSession(other, context);
		}
		else{
			Log.i("[DEBUG_COMMAND]", "[DEBUG_COMMAND] messaggio ignorato!!!");
		}
	}
	
	
	private static String getErrorMessage(Exception e){
		String message = e.getMessage();
		
		if(message == null || message.equals("")){
			return ErrorFactory.GENERIC_ERROR;
		}
		else if(ErrorFactory.isMessageInFactory(message)){
			return message;
		}
		else{
			return ErrorFactory.GENERIC_ERROR;
		}
	}
	
	private static String getErrorMessage(String message){		
		if(message == null || message.equals("")){
			return ErrorFactory.GENERIC_ERROR;
		}
		else if(ErrorFactory.isMessageInFactory(message)){
			return message;
		}
		else{
			return ErrorFactory.GENERIC_ERROR;
		}
	}
	
	private static void CreatePopup(String message, Context context){
		FragmentManager manager = ((Activity) context).getFragmentManager();
		DialogFragment newFragment = new ErrorPopupFragment(message);
	    newFragment.show(manager, "missiles");
	}
}