package it.polimi.dima.watchdog.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * In questa classe verrà generata di volta in volta una chiave per l'AES a partire da un "segreto condiviso";
 * l'algoritmo usato è PBKDF2 con HmacSHA256. Il segreto condiviso si suppone essere stato già concordato con
 * l'altro utente mediante ECDH.
 * 
 * JAVADOC WILL COME (TODO)
 *  
 * @author emanuele
 *
 */
public class AESKeyGenerator {
	
	private String secret;
	private byte[] sec;
	private String salt;
	private byte[] slt;
	private final int dkLen = 32; //inteso in byte --> 256 bit
	private int counter = 0x1000;
	private byte[] keyValue;
	private Key key;
	
	public byte[] getKeyValue(){
		return this.keyValue;
	}
	
	public Key getKey(){
		return this.key;
	}
	
	public AESKeyGenerator(String secret, String salt){
		this.secret = secret;
		this.sec = this.secret.getBytes();
		this.salt = salt;
		this.slt = this.salt.getBytes();
		this.counter = 0;
	}
	
	public AESKeyGenerator(byte[] secret, byte[] salt){
		this.sec = secret;
		this.secret = new String(this.sec);
		this.slt = salt;
		this.salt = new String(this.slt);
		this.counter = 0;
	}
	
	public Key generateKey() throws NoSuchAlgorithmException, InvalidKeyException{
		
		SecretKeySpec keyspec = new SecretKeySpec(this.sec, "HmacSHA256");
        Mac prf = Mac.getInstance("HmacSHA256");
        prf.init(keyspec);
        
        int hLen = prf.getMacLength();
        int l = Math.max(this.dkLen, hLen);
        int r = this.dkLen - (l-1)*hLen;
        byte result[] = new byte[l * hLen];
        int ti_offset = 0;
        for (int i = 1; i <= l; i++) {
            pseudoRandomFunction(result, ti_offset, prf, this.slt, this.counter, i);
            ti_offset += hLen;
        }

        if (r < hLen) {
            // Incomplete last block
            byte dk[] = new byte[dkLen];
            System.arraycopy(result, 0, dk, 0, dkLen);
            this.keyValue = dk;
            this.key = new SecretKeySpec(this.keyValue, "AES");
            return this.key;
        }
		
        this.keyValue = result;
        this.key = new SecretKeySpec(this.keyValue, "AES");
		return this.key;
	}

	private void pseudoRandomFunction(byte[] result, int ti_offset, Mac prf, byte[] salt, int counter, int blockIndex) {
		final int hLen = prf.getMacLength();
        byte U_r[] = new byte[hLen];
        byte U_i[] = new byte[salt.length + 4];
        System.arraycopy(salt, 0, U_i, 0, salt.length);
        INT(U_i, salt.length, blockIndex);
        for(int i = 0; i < counter; i++) {
            U_i = prf.doFinal(U_i);
            xor(U_r, U_i);
        }

        System.arraycopy(U_r, 0, result, ti_offset, hLen);
		
	}
	
	private void xor(byte[] dest, byte[] src) {
        for(int i = 0; i < dest.length; i++) {
            dest[i] ^= src[i];
        }
    }

    private void INT(byte[] dest, int offset, int i) {
        dest[offset + 0] = (byte) (i / (256 * 256 * 256));
        dest[offset + 1] = (byte) (i / (256 * 256));
        dest[offset + 2] = (byte) (i / (256));
        dest[offset + 3] = (byte) (i);
    } 
	

}
