package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;









import com.example.nucleus.MainActivity.loginAccount;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpForm extends Activity {
	
	EditText  edt_fullName, edt_email, edt_comp_org,  edt_password;
	private Pattern pattern;
	private Matcher matcher;
	private static final String PASSWORD_PATTERN = 
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";
	private static final String PASSWORD_PATTERN2 = 
            "((?=.*\\d)(?=.*[a-z]).{6,20})";
	
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	SharedPreferences.Editor editor;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_form);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		pattern = Pattern.compile(PASSWORD_PATTERN);
		
		
		edt_fullName = (EditText) findViewById(R.id.edt_fullName);
		edt_email = (EditText) findViewById(R.id.edt_email);
		edt_comp_org = (EditText) findViewById(R.id.edt_comp_org);
		
		edt_password = (EditText) findViewById(R.id.edt_password);
		Button btn_signUp = (Button) findViewById(R.id.btn_signUp);
		
		
		btn_signUp.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edt_fullName.getText().toString().equals("") || edt_email.getText().toString().equals("") || edt_comp_org.getText().toString().equals("") || edt_password.getText().toString().equals("")){
					Toast.makeText(SignUpForm.this, "Please fill up all the fields", Toast.LENGTH_SHORT).show();
				}else {
					if (edt_password.getText().length()< 6){
						Toast.makeText(SignUpForm.this, "Password must at least be 6 characters long",Toast.LENGTH_SHORT).show();
					}else{
						if (edt_password.getText().toString().matches(PASSWORD_PATTERN)){
							new NetCheck().execute();
							Toast.makeText(SignUpForm.this, "3", Toast.LENGTH_SHORT).show();
						}else if(edt_password.getText().toString().matches(PASSWORD_PATTERN2)){
							new NetCheck().execute();
							
						}else{
							AlertDialog.Builder adg = new AlertDialog.Builder(SignUpForm.this);
			                adg.setTitle("Weak Password");
			                
			                adg.setMessage("You're password strength is weak. You can proceed to your registration or you can improve your password. Do you want to change your password?");
			                adg.setCancelable(false);
			                
			                adg.setPositiveButton("No, Im good.",
			                	    new DialogInterface.OnClickListener() {
			                    public void onClick(DialogInterface dialog, int which) {
			                      //dismiss the dialog
			                    	new NetCheck().execute();
			                    	
			                    }
			                });
			                adg.setNegativeButton("Yes, I'll change it", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							});
			                adg.create().show();
						}
						//new NetCheck().execute();
					}
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
			pDialog = new ProgressDialog(SignUpForm.this);
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
                
                new RegisterMe().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                //new ProcessRegister().execute();
            }
            else{
                pDialog.dismiss();
                //Toast.makeText(getBaseContext(), "No Connection", Toast.LENGTH_SHORT).show();
                
                //registerErrorMsg.setText("Error in Network Connection");
                AlertDialog.Builder adg = new AlertDialog.Builder(SignUpForm.this);
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
	
	public class RegisterMe extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pDialog;
		String fullName, comp_org, email, password;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			fullName = edt_fullName.getText().toString();
			
			comp_org = edt_comp_org.getText().toString();
			
			
			email = edt_email.getText().toString();
			password = edt_password.getText().toString();
			pDialog = new ProgressDialog(SignUpForm.this);
            pDialog.setTitle("Contacting Servers");
            pDialog.setMessage("Registering ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			userFunctions uf = new userFunctions();
			JSONObject json = uf.registerUser(fullName, comp_org, email, password);
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
	                editor.putString("user_email", json.getString("user_email"));
	                editor.putInt("user_id", json.getInt("user_id"));
	                editor.putInt("workspace_id", json.getInt("workspace_id"));
	                editor.putInt("ws_user_level_id", json.getInt("ws_user_level_id"));
	                editor.commit();
					AlertDialog.Builder adg = new AlertDialog.Builder(SignUpForm.this);
	                adg.setTitle("Registration Successful");
	                
	                adg.setMessage("You have successfully registered to Nucleus.");
	                adg.setCancelable(false);
	                
	                adg.setPositiveButton("Ok",
	                	    new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                      //dismiss the dialog
	                    	
	                    	Intent i = new Intent(SignUpForm.this, PostSignUp.class);
	                    	i.putExtra("fromFreshSignup", true);
	                    	startActivity(i);
	                    	finish();
	                    }
	                });
	                
	                adg.show();
				}else{
					if (json.getInt("errorCode")==1){
						Toast.makeText(SignUpForm.this, json.getString("message"), Toast.LENGTH_SHORT).show();
						pDialog.dismiss();
					}else{
						Toast.makeText(SignUpForm.this, json.getString("message"), Toast.LENGTH_SHORT).show();
						pDialog.dismiss();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
}
