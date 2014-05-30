package it.polimi.dima.watchdog.crypto;

import it.polimi.dima.watchdog.utilities.CryptoUtility;
import java.security.Key;
import javax.crypto.spec.SecretKeySpec;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

/**
 * In questa classe viene generata di volta in volta una chiave per l'AES a partire da un "segreto condiviso";
 * l'algoritmo usato è PBKDF2 con HmacSHA256. Il segreto condiviso si suppone essere stato già concordato con
 * l'altro utente mediante ECDH.
 *  
 * @author emanuele
 *
 */
public class AESKeyGenerator {
	
	private byte[] secret;
	private byte[] salt;
	private byte[] keyValue;
	private Key key;
	
	public byte[] getKeyValue(){
		return this.keyValue;
	}
	
	public Key getKey(){
		return this.key;
	}
	
	public AESKeyGenerator(String secret, String salt){
		this.secret = secret.getBytes();
		this.salt = salt.getBytes();
	}
	
	public AESKeyGenerator(byte[] secret, byte[] salt){
		this.secret = secret;
		this.salt = salt;
	}
	
    /**
     * Genera e ritorna una chiave per AES-256-GCM a partire da secret e salt con "PBKDF2 con HmacSHA256".
     * 
     * @return una chiave valida per AES-256-GCM
     */
    public Key generateKey(){
    	PKCS5S2ParametersGenerator gen = new PKCS5S2ParametersGenerator(new SHA256Digest());
    	gen.init(this.secret, this.salt, 4096);
    	this.keyValue = ((KeyParameter) gen.generateDerivedParameters(256)).getKey();
    	this.key = new SecretKeySpec(this.keyValue, CryptoUtility.AES_256);
    	return this.key;
    }  
}