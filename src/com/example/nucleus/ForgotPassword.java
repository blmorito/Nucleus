package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ForgotPassword extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	String forgotEmail;
	EditText edt_forgotEmail;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.forgot_pw);
		EditText emailForgot = (EditText) findViewById(R.id.edt_forgotEmail);
		
		Intent intent = getIntent();
		emailForgot.setText(intent.getStringExtra("email"));
		
		TextView txt_goBackToSignIn = (TextView) findViewById(R.id.txt_goBackToSignIn);
		String sourceString = "<b><u>Go back to Sign In</u></b> "; 
		txt_goBackToSignIn.setText(Html.fromHtml(sourceString));
		
		txt_goBackToSignIn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(ForgotPassword.this, MainActivity.class));
				finish();
			}
		});
		
		edt_forgotEmail = (EditText) findViewById(R.id.edt_forgotEmail);
		Button btn_forgotEmail = (Button) findViewById(R.id.btn_forgotEmail);
		forgotEmail = edt_forgotEmail.getText().toString();
		
		btn_forgotEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new NetCheck().execute();
			}
		});
		
	}
	
	public final static boolean isValidEmail(CharSequence target) {
	  return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}
	
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		private ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
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
				setProgressBarIndeterminateVisibility(false);
                new forgotPassword().execute();
				
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
                //Toast.makeText(getBaseContext(), "No Connection", Toast.LENGTH_SHORT).show();
                
                //registerErrorMsg.setText("Error in Network Connection");
                AlertDialog.Builder adg = new AlertDialog.Builder(ForgotPassword.this);
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
	public class forgotPassword extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pDialog;
		String email;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			email = edt_forgotEmail.getText().toString() ;
			
			pDialog = new ProgressDialog(ForgotPassword.this);
            
            pDialog.setMessage("Checking email..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			userFunctions uf = new userFunctions();
			JSONObject json = uf.forgotPassword(email);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				if (json.getInt("success")==1){
					pDialog.dismiss();
					
					startActivity(new Intent(ForgotPassword.this, ForgotPWemail.class));
					finish();
	                
	                
				}else{
					if (json.getInt("errorCode")==1){
						Toast.makeText(ForgotPassword.this, json.getString("message"), Toast.LENGTH_SHORT).show();
						pDialog.dismiss();
					}else{
						Toast.makeText(ForgotPassword.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
