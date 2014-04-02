package it.polimi.dima.watchdog.crypto;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * In questa classe verranno generate le chiavi da dare in pasto agli algoritmi a partire da una "password";
 * l'algoritmo usato è PBKDF2 con HmacSHA256.
 * 
 * JAVADOC WILL COME (TODO)
 *  
 * @author emanuele
 *
 */
public class KeyGenerator {
	
	private String password;
	private byte[] pwd;
	private String salt;
	private byte[] slt;
	private final int dkLen = 32;
	private int counter = 0x1000;
	private byte[] key;
	
	public byte[] getKey(){
		return this.key;
	}
	
	public KeyGenerator(String password, String salt){
		this.password = password;
		this.pwd = this.password.getBytes();
		this.salt = salt;
		this.slt = this.salt.getBytes();
		this.counter = 0;
	}
	
	public KeyGenerator(byte[] password, byte[] salt){
		this.pwd = password;
		this.password = new String(this.pwd);
		this.slt = salt;
		this.salt = new String(this.slt);
		this.counter = 0;
	}
	
	public byte[] generateKey() throws NoSuchAlgorithmException, InvalidKeyException{
		
		SecretKeySpec keyspec = new SecretKeySpec(this.pwd, "HmacSHA256");
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
            this.key = dk;
            return this.key;
        }
		
        this.key = result;
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
