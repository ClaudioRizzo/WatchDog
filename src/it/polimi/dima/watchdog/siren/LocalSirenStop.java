package it.polimi.dima.watchdog.siren;

import it.polimi.dima.watchdog.crypto.CryptoUtility;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.password.PasswordUtils;
import it.polimi.dima.watchdog.utilities.MyPrefFiles;
import it.polimi.dima.watchdog.utilities.SMSUtility;
import it.polimi.dima.watchdog.utilities.ServicesUtilities;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import android.content.Context;
import android.content.Intent;

/**
 * Classe che permette di spegnere un'eventuale sirena locale previa inserimento della propria password. Uso:
 * prima costruire la classe con la password digitata dall'utente e poi chiamare turnOffSiren()
 * 
 * @author emanuele
 *
 */
public class LocalSirenStop {
	
	private byte[] myPasswordHash;
	private byte[] myPasswordSalt;
	private byte[] insertedPassword;
	private Context context;
	
	/**
	 * Costruisce l'oggetto con la password digitata dall'utente e il contesto corrente.
	 * 
	 * @param password : la password digitata dall'utente
	 * @param context : il contesto corrente
	 */
	public LocalSirenStop(String password, Context context){
		this.insertedPassword = password.getBytes();
		this.context = context;
	}
	
	private boolean isPasswordCorrect() throws NoSuchPreferenceFoundException, NoSuchAlgorithmException{
		this.myPasswordHash = MyPrefFiles.getMyPasswordHash(this.context);
		this.myPasswordSalt = MyPrefFiles.getMyPasswordSalt(this.context);
		return Arrays.equals(this.myPasswordHash, PasswordUtils.computeHash(this.insertedPassword, this.myPasswordSalt, CryptoUtility.SHA_256));
	}
	
	/**
	 * Spegne la sirena se è attiva e se la password inserita è corretta.
	 * 
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPreferenceFoundException
	 */
	public void turnOffSiren() throws NoSuchAlgorithmException, NoSuchPreferenceFoundException{
		if(isPasswordCorrect()){
			if(ServicesUtilities.isMyServiceRunning(this.context, SirenService.class)){
				Intent intent = new Intent(this.context,SirenService.class);
				intent.putExtra(SMSUtility.COMMAND, SMSUtility.SIREN_OFF);
				this.context.startService(intent);
			}
			else{
				SMSUtility.showShortToastMessage("La sirena non è attiva: non posso spegnerla", this.context);
			}
		}
		else{
			SMSUtility.showShortToastMessage("Password sbagliata: non ho potuto interrompere la sirena.", this.context);
		}
	}
}