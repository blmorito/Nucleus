package com.example.nucleus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class ForgotPWemail extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgot_pw_email);
		
		TextView txt_goBackSignIn = (TextView) findViewById(R.id.txt_goBackSignIn);
		txt_goBackSignIn.setText(Html.fromHtml("<u>Click here to Sign in</u>"));
		
		txt_goBackSignIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ForgotPWemail.this,MainActivity.class));
				finish();
			}
		});
	}
}
