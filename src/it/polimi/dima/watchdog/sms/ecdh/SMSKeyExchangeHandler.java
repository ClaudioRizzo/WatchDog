package it.polimi.dima.watchdog.sms.ecdh;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class SMSKeyExchangeHandler extends BroadcastReceiver implements SMSKeyExchangeVisitorInterface {

	@Override
	public void visit(HereIsMyPublicKeyCodeMessage pubKeySentMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void visit(HereIsMyPublicKeyTooCodeMessage pubKeySentTooMessage) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub

	}

}
