package it.polimi.dima.watchdog.sms.commands;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.spec.SecretKeySpec;

import it.polimi.dima.watchdog.CryptoUtility;
import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.SMSParser;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;

/**
 * 
 * @author emanuele
 *
 */
public class SMSCommandHandler extends BroadcastReceiver implements SMSCommandVisitorInterface{

	private Context ctx;
	private SMSProtocol recMsg;
	private String other;
	private CommandFactory comFac;
	private SMSParser parser;
	
	
	public SMSCommandHandler(){
		this.comFac = new CommandFactory();
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		this.ctx = context;
		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get(SMSUtility.SMS_EXTRA_NAME);
				SmsMessage message = null;
				
				for (int i = 0; i < pdusObj.length; i++) {
					message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}
				this.other = message.getDisplayOriginatingAddress();
				String myContext = getMyContext(context);
				
				if(myContext == "free"){//TODO mettere la stringa da qualche parte
					
				}
				else if(myContext == "flag_m1_sent"){//TODO mettere la stringa da qualche parte
					
				}
				else if(myContext == "flag_m2_sent"){//TODO mettere la stringa da qualche parte
					//TODO: se arriva un timeout smettere immediatamente quello che si stava facendo e
					//chiamare manageTimeout()
					try{
						//TODO stoppare il timeout
						MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context);
						MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, "flag_m3_received", context);
						
						this.parser = popolateParser(message);	
						this.recMsg = this.comFac.getMessage(SMSUtility.bytesToHex(this.parser.getPlaintext()));
						this.recMsg.setBody(SMSUtility.getBody(message.getUserData()));
						this.parser.decrypt();
						this.recMsg.handle(this); //qui verrà fatto ciò che il messaggio chiede
												  //compreso l'invio di un nuovo messaggio
						
						MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context);
						MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, "flag_m4_sent", context);
						
					}
					catch(Exception e)
					{
						//TODO: se arriva un timeout smettere immediatamente quello che si stava facendo e
						//chiamare manageTimeout()
						MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context);
						MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, "flag_m2_sent", context);
						
						
					}
					
				}
				else if(myContext == "flag_m3_sent"){//TODO mettere la stringa da qualche parte
	
				}
				else{
					//il messaggio viene semplicemente ignorato senza fare altro
				}
				
				
				
			}
			
			
		} 
		catch (NoSuchPreferenceFoundException e)
		{
			//TODO ignorare il messaggio (ovvero fare in modo che non venga "visitato")
		}
		catch (Exception e) 
		{
			//TODO ignorare il messaggio (ovvero fare in modo che non venga "visitato")
			e.printStackTrace();
		}
		
	}
	
	private SMSParser popolateParser(SmsMessage sms) throws NoSuchPreferenceFoundException, NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] encryptedMessage = sms.getUserData();
		
		String decKey = MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, this.other, this.ctx);
		byte[] decKeyValue = Base64.decode(decKey, Base64.DEFAULT);
		Key decryptionKey = new SecretKeySpec(decKeyValue, CryptoUtility.AES_256);
		
		String otherPub = MyPrefFiles.getMyPreference(MyPrefFiles.KEYRING, this.other, this.ctx);
		byte[] otherPubValue = Base64.decode(otherPub, Base64.DEFAULT);
		KeyFactory kf = KeyFactory.getInstance(CryptoUtility.EC);
		PublicKey oPub = kf.generatePublic(new X509EncodedKeySpec(otherPubValue));
		
		String storedHash = MyPrefFiles.getMyPreference(MyPrefFiles.PASSWORD_AND_SALT, MyPrefFiles.MY_PASSWORD_HASH, this.ctx);
		byte[] storedPasswordHash = Base64.decode(storedHash, Base64.DEFAULT);
		
		return new SMSParser(encryptedMessage, decryptionKey, oPub, storedPasswordHash);
	}
	
	private String getMyContext(Context context) throws NoSuchPreferenceFoundException{
		if(MyPrefFiles.existsPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context)){
			return MyPrefFiles.getMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, context);
		}
		return null;
	}

	@Override
	public void visit(SirenOnCodeMessage sirenOnCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SirenOffCodeMessage sirenOffCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkLostCodeMessage markLostCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkStolenCodeMessage markStolenCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkLostOrStolenCodeMessage markLostOrStolenCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkFoundCodeMessage markFoundCodeMessage) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(LocateCodeMessage locateCodeMessage) {
		// TODO Auto-generated method stub
		
	}
	
	private void manageTimeout(Context ctx){
		MyPrefFiles.deleteMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, ctx);
		MyPrefFiles.setMyPreference(MyPrefFiles.COMMAND_SESSION, MyPrefFiles.COMMUNICATION_STATUS_WITH + this.other, "free", ctx);
	}

}
