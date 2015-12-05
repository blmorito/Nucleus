package com.example.nucleus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class CreateWorkspaceSuccess extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_ws_success);
		
		Button btn_continue = (Button) findViewById(R.id.btn_wsSuccess);
		
		btn_continue.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(CreateWorkspaceSuccess.this, Dashboard.class));
				finish();
			}
		});
	}

}
