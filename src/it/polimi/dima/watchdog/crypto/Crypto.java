package it.polimi.dima.watchdog.crypto;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Interfaccia che verrà implementata da tutte le classi che si occupano di algoritmi crittografici.
 * @author emanuele
 *
 */
public interface Crypto {
	
	
	public String encrypt() throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, NoSuchProviderException, IllegalBlockSizeException, BadPaddingException;
	public String decrypt();

}
