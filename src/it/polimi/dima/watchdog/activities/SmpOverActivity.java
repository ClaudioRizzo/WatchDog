package it.polimi.dima.watchdog.activities;

import it.polimi.dima.watchdog.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SmpOverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_smp_over);
		setFinishOnTouchOutside(false);
		
		TextView body = (TextView) findViewById(R.id.smp_over_body);
		String number = getIntent().getStringExtra("other");
		body.setText("The association with " + number + " is successfully over. Refresh the page to start sending commands to it.");
		
		Button button = (Button) findViewById(R.id.button_smp_over);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
