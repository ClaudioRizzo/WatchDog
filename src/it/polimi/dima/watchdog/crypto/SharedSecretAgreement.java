package it.polimi.dima.watchdog.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Questa classe creerà un segreto condiviso tra due utenti, a partire dalle chiavi rsa di entrambi, senza
 * che il segreto stesso sia trasmesso su alcun canale. Il segreto potrà poi essere utilizzato dalla classe
 * KeyGenerator per derivare le chiavi dell'AES. Il protocollo utilizzato è il ECMQV (oppure il FHMQV se ne
 * esiste una versione in java). Le chiavi pubbliche si suppongono già autenticate.
 * 
 * @author emanuele
 *
 */
public class SharedSecretAgreement {
	private PublicKey myPublicKey;
	private PrivateKey myPrivateKey;
	private PublicKey otherPublicKey;
	private byte[] otherS; //detta (X,x) la coppia calcolata dall'altro con x random e X = [x]P su una EC nota e condivisa, e detti Xs i primi L bit
						   //di X dove L = ceil((floor(log2(n))+1)/2) e n è l'ordine del punto P della EC, e detto innfine oPr la chiave privata
						   //dell'altro, --> otherS = x + Xs*oPr
						   //Importante è che io conosca solo otherS e non i valori usati per calcolarlo.
	private byte[] sharedSecret;
	 
	
	
	public SharedSecretAgreement(PublicKey mPu, PrivateKey mPr, PublicKey oPu, byte[] oS){
		this.myPublicKey = mPu;
		this.myPrivateKey = mPr;
		this.otherPublicKey = oPu;
		this.otherS = oS;
	}
	
	/**
	 * Metodo che genera lo shared secret con l'algoritmo ECMQV
	 * @return
	 */
	public byte[] generateSharedSecret(){
		//TODO
		return this.sharedSecret;
	}

}
