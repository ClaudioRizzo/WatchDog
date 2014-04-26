package it.polimi.dima.watchdog.sms.ecdh;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import it.polimi.dima.watchdog.MyPrefFiles;
import it.polimi.dima.watchdog.SMSUtility;
import it.polimi.dima.watchdog.crypto.SharedSecretAgreement;
import it.polimi.dima.watchdog.exceptions.NoSuchPreferenceFoundException;
import it.polimi.dima.watchdog.sms.socialistMillionaire.SMSProtocol;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Base64;

/**
 * Classe che reagisce adeguatamente ai vari messaggi di ECDH.
 * @author emanuele
 *
 */
public class SMSKeyExchangeHandler extends BroadcastReceiver implements SMSKeyExchangeVisitorInterface {

	private SharedSecretAgreement ssa;
	private String other;
	private EllipticCurvesDiffieHellmanFactory ecdhFactory;
	private Context ctx;
	private SMSProtocol recMsg;
	
	public SMSKeyExchangeHandler(){
		this.ecdhFactory = new EllipticCurvesDiffieHellmanFactory();
		this.ssa = new SharedSecretAgreement();
	}
	
	@Override
	public void visit(HereIsMyPublicKeyCodeMessage pubKeySentMessage) {
		try 
		{
			this.ssa.setMyPrivateKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, this.ctx));
			this.ssa.setTokenReceived(pubKeySentMessage.getBody());
			this.ssa.generateSharedSecret();
			byte[] myPub = Base64.decode(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PUB, this.ctx), Base64.DEFAULT);
			SMSUtility.sendMessage(this.other, SMSUtility.ECDH_PORT, SMSUtility.hexStringToByteArray(SMSUtility.CODE8), myPub);
		}
		catch (NoSuchAlgorithmException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
		} 
		catch (InvalidKeySpecException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
		} 
		catch (NoSuchPreferenceFoundException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
		}
		catch (InvalidKeyException e)
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
		}

	}

	@Override
	public void visit(HereIsMyPublicKeyTooCodeMessage pubKeySentTooMessage) {
		try 
		{
			this.ssa.setMyPrivateKey(MyPrefFiles.getMyPreference(MyPrefFiles.MY_KEYS, MyPrefFiles.MY_PRIV, this.ctx));
			this.ssa.setTokenReceived(pubKeySentTooMessage.getBody());
			this.ssa.generateSharedSecret();
		} 
		catch (NoSuchAlgorithmException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
		} 
		catch (InvalidKeySpecException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
		} 
		catch (NoSuchPreferenceFoundException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
		}
		catch (InvalidKeyException e) 
		{
			SMSUtility.showShortToastMessage(e.getMessage(), this.ctx);
			e.printStackTrace();
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		this.ctx = context;
		final Bundle bundle = intent.getExtras();

		try {
			if (bundle != null) {
				final Object[] pdusObj = (Object[]) bundle.get("pdus");
				SmsMessage message = null;
				
				for (int i = 0; i < pdusObj.length; i++) {
					message = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
				}
				
				this.other = message.getDisplayOriginatingAddress();
				this.recMsg = this.ecdhFactory.getMessage(SMSUtility.getHeader(message.getUserData()));
				this.recMsg.setBody(SMSUtility.getBody(message.getUserData()));
				this.recMsg.handle(this);
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
