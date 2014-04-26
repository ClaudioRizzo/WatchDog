package it.polimi.dima.watchdog.sms.aesKey;

import it.polimi.dima.watchdog.crypto.AESKeyGenerator;

/**
 * Forse è utile un wrapper per la classe AESKeyGenerator (e che gestisca lo scambio del sale) TODO
 * @author emanuele
 *
 */
public class SMSAESKeyGeneration {
	
	public AESKeyGenerator keygen;
	
	public SMSAESKeyGeneration(){
		this.keygen = null;
	}

}
