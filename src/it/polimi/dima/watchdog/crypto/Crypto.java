package it.polimi.dima.watchdog.crypto;

import org.spongycastle.crypto.InvalidCipherTextException;

/**
 * Interfaccia che verrà implementata dalle classi che si occupano della crittografia simmetrica.
 * 
 * @author emanuele
 * 
 */
public interface Crypto {
	
	public String encrypt() throws IllegalStateException, InvalidCipherTextException;
	public byte[] decrypt() throws IllegalStateException, InvalidCipherTextException;
}