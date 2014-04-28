package it.polimi.dima.watchdog.crypto;

import org.spongycastle.crypto.InvalidCipherTextException;

/**
 * Interfaccia che verrà implementata da tutte le classi che si occupano di algoritmi crittografici.
 * @author emanuele
 *
 */
public interface Crypto {
	
	public String encrypt() throws IllegalStateException, InvalidCipherTextException;
	public byte[] decrypt() throws IllegalStateException, InvalidCipherTextException;

}
