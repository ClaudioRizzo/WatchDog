package it.polimi.dima.watchdog;

public class SMSUtility {

	
	public static String CODE1 = "C0DE1FFF";
	public static String CODE2 = "C0DE2FFF";
	public static String CODE3 = "C0DE3FFF";
	public static String CODE4 = "C0DE4FFF";
	public static String CODE5 = "C0DE5FFF";
	public static String CODE6 = "C0DE6FFF";
	public static String CODE7 = "C0DE7FFF";
	public static String CODE8 = "C0DE8FFF";
	
	final private static char[] hexArray = "0123456789ABCDEF".toCharArray();
	
	
	/**
	 * converte array di byt in stringa esadecimale
	 * @param bytes
	 * @return
	 */
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	/**
	 * converte stringa esadecimale in array di byte
	 * @param s
	 * @return
	 */
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
}
