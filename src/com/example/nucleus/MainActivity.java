package com.example.nucleus;



import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;













import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	EditText loginEmail, loginPassword;
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences.Editor editor;
	SharedPreferences prefs;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE); 
		
		Boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		 loginEmail = (EditText) findViewById(R.id.loginEmail);
		 loginPassword = (EditText) findViewById(R.id.loginPassword);
		 Button btn_logIn = (Button) findViewById(R.id.btn_logIn);
		
		
		 if (isLoggedIn==true){
				//redirect to main page
				startActivity(new Intent(MainActivity.this,Dashboard.class));
				finish();
			}
		 
		 
		 if (loginEmail.getText().toString().equals("")){
			 
		 }else {
				loginEmail.requestFocus();
		}
		 
		 
			 
		 
		TextView txt_Signup = (TextView) findViewById(R.id.txtSignUp);
		String sourceString = "Don't have an account?<br><b><u>Click here to Sign up!</u></b> "; 
		txt_Signup.setText(Html.fromHtml(sourceString));
		
		TextView txt_forgotPW = (TextView) findViewById(R.id.txt_forgotPW);
		String sourceString2 = "<u>Forgot your password?</u>"; 
		txt_forgotPW.setText(Html.fromHtml(sourceString2));
		txt_forgotPW.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
				intent.putExtra("email", loginEmail.getText().toString());
				startActivity(intent);
			}
		});
		
		txt_Signup.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(getBaseContext(),SignUpForm.class));
			}
		});
		
		btn_logIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String emailTrap, passTrap;
				emailTrap = loginEmail.getText().toString();
				passTrap = loginPassword.getText().toString();
				
				if (emailTrap.trim().equalsIgnoreCase("") || passTrap.trim().equalsIgnoreCase("") ){
					Toast.makeText(getBaseContext(), "Please fill in the required fields.", Toast.LENGTH_SHORT).show();
				}else{
					new NetCheck().execute();
				}
				
			}
		});
	}
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		private ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Loading..");
			pDialog.setTitle("Checking Connection");
			pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            
            
            if (netInfo != null && netInfo.isConnected()) {
            	
                try {
                	
                    URL url = new URL("http://"+ip+"/nucleus/api/check_net.php");
                    HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                    urlc.setConnectTimeout(3000);
                    urlc.connect();
                    
                    if (urlc.getResponseCode() == 200) {
                        return true;
                    }
                } catch (MalformedURLException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                    Log.d("wrongurl",e1.toString());
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.d("wrongurl",e.toString());
                }
            }
            
            return false;
		}
		
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(result == true){
                pDialog.dismiss();
                
                new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                //new ProcessRegister().execute();
            }
            else{
                pDialog.dismiss();
                //Toast.makeText(getBaseContext(), "No Connection", Toast.LENGTH_SHORT).show();
                
                //registerErrorMsg.setText("Error in Network Connection");
                AlertDialog.Builder adg = new AlertDialog.Builder(MainActivity.this);
                adg.setTitle("Connection Error");
                adg.setMessage("You dont seem to be connected to the internet. Please connect and try again.");
                adg.setCancelable(true);
                adg.setPositiveButton("Ok",
                	    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      //dismiss the dialog  
                    }
                });
                adg.create().show();
            }
		}
		
	}
	
	public class loginAccount extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pDialog;
		String Xemail, Xpassword;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			
			
			
			Xemail = loginEmail.getText().toString();
			
			Xpassword = loginPassword.getText().toString();
			
			pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setTitle("Log in");
            pDialog.setMessage("Logging in ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.loginUser(Xemail, Xpassword);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					pDialog.dismiss();
					
					
					
	               
	                SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
	                editor.putBoolean("isLoggedIn", true);
	                editor.putInt("user_id", json.getInt("user_id"));
	                editor.putInt("workspace_id", json.getInt("workspace_id"));
	                editor.putString("user_email", json.getString("user_email"));
	                editor.putInt("ws_user_level_id", json.getInt("ws_user_level"));
	                editor.commit();
	                
	                startActivity(new Intent(MainActivity.this, Dashboard.class));
	                finish();
				}else{
					pDialog.dismiss();
					Toast.makeText(MainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(MainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(MainActivity.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}

	
	
	
}
