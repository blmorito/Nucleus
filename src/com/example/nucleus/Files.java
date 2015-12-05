package com.example.nucleus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Files extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences prefs;
	Integer user_id, workspace_id, ws_user_level_id, p_user_level_id;
	Integer project_id;
	String project_name;
	String project_desc;
	WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.files);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		project_id = prefs.getInt("project_id", 0);
		user_id = prefs.getInt("user_id", 0);
		webView = (WebView) findViewById(R.id.webView1);
		
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://192.168.8.120/nucleus/api/files.php?project_id="+project_id+"&user_id="+user_id);
		
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		webView.loadUrl("http://192.168.8.120/nucleus/api/files.php?project_id="+project_id+"&user_id="+user_id);
		super.onRestart();
	}

}
