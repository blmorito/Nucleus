package com.example.nucleus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PostSignUp extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome_page);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		Button btn_welcomeC = (Button) findViewById(R.id.btn_welcomeC);
		
		btn_welcomeC.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent i = new Intent(PostSignUp.this, CreateProject.class);
				i.putExtra("fromFreshSignup", true);
				startActivity(i);
				finish();
			}
		});
	}
	
	@Override
	public void onBackPressed()
	{
	    super.onBackPressed(); 
	    startActivity(new Intent(PostSignUp.this, MainActivity.class));
	    finish();

	}
}
