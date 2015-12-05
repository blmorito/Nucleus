package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.InviteWorkspace.getProjectsForInvite;
import com.example.nucleus.InviteWorkspace.getWsMemberEmails;
import com.example.nucleus.InviteWorkspace.sendWorkspaceInvite;
import com.example.nucleus.MeEdit.NetCheck;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MeEditCred extends Activity {
	
	SharedPreferences pref;
	Integer user_id;
	String current_email;
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	TextView currentEmail;
	EditText edt_newEmail, edt_currentPw, edt_newPw, edt_newPw2;
	Button btn_submit, btn_cancel;
	
	//to be passed
	Boolean toUpdate;
	String newEmail,newPassword;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.me_profile_credentials);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		toUpdate = false;
		
		pref = getSharedPreferences("MyPref", MODE_PRIVATE);
		user_id = pref.getInt("user_id", 0);
		current_email = pref.getString("user_email", null);
		Log.e("cemail", current_email);
		
		currentEmail = (TextView) findViewById(R.id.txt_credEmail);
		edt_newEmail = (EditText) findViewById(R.id.edt_credNewEmail);
		edt_currentPw = (EditText) findViewById(R.id.edt_credCurrentPassword);
		edt_newPw = (EditText) findViewById(R.id.edt_credNewPassword);
		edt_newPw2 = (EditText) findViewById(R.id.edt_credConfirmPassword);
		btn_submit = (Button) findViewById(R.id.btn_credSubmit);
		btn_cancel = (Button) findViewById(R.id.btn_credCancel);
		currentEmail.setText(current_email);
		
		edt_newEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_newEmail.setError(null);
			}
		});
		edt_currentPw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_currentPw.setError(null);
			}
		});
		edt_newPw.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_newPw.setError(null);
			}
		});
		edt_newPw2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_newPw2.setError(null);
			}
		});
		
		btn_submit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_newEmail.getText().toString().equalsIgnoreCase("") && edt_currentPw.getText().toString().equalsIgnoreCase("")){
					edt_newEmail.setError("This field cannot be blank");
					edt_currentPw.setError("This field cannot be blank");
				}else if (edt_newEmail.getText().toString().equalsIgnoreCase("")) {
					edt_newEmail.setError("This field cannot be blank");
				}else if (edt_currentPw.getText().toString().equalsIgnoreCase("")) {
					edt_currentPw.setError("This field cannot be blank");
				}else{
					 if (isValidEmail(edt_newEmail.getText().toString())) {
						 if(edt_newPw.getText().toString().equals(edt_newPw2.getText().toString())){
							 if(edt_newPw.getText().toString().trim().equalsIgnoreCase("")){
								 //Toast.makeText(getBaseContext(), "will not update pw", Toast.LENGTH_SHORT).show();
								 toUpdate = true;
								 newEmail = edt_newEmail.getText().toString();
								 newPassword = "";
								 new NetCheck().execute();
							 }else{
								if(edt_newPw.length()<6){
									edt_newPw.setError("Password too short");
								}else{
									toUpdate = true;
									 newEmail = edt_newEmail.getText().toString();
									 newPassword = edt_newPw.getText().toString();
									 new NetCheck().execute();
								}
							 }
						 }else{
							 edt_newPw2.setError("Passwords do not match ");
						 }
						 
					 }else{
						 edt_newEmail.setError("Invalid email");
					 }
					
				}
			}
		});
		
		btn_cancel.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
	}
	
	public final static boolean isValidEmail(CharSequence target) {
	  return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}
	
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		
		
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
                
                
                
                
               
				if(toUpdate==true){
					toUpdate=false;
					new updateCredentials().execute();
				}else{
					
				}
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	
            	Toast.makeText(getBaseContext(), "Couldn't connect to the network", Toast.LENGTH_SHORT).show();
                
                
            }
		}
		
	}
	public class updateCredentials extends AsyncTask<Void, Void, JSONObject>{
		
		//Integer u_id;
		String pw;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			pw = edt_currentPw.getText().toString();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.updateCredentials(user_id, pw, newEmail, newPassword);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
	                
	                editor.putString("user_email", newEmail);
	                editor.commit();
	                
					AlertDialog.Builder adg = new AlertDialog.Builder(MeEditCred.this);
	                adg.setTitle("Success!");
	                adg.setMessage("You successfully changed your user credentials");
	                adg.setCancelable(true);
	                adg.setPositiveButton("Ok",
	                	    new DialogInterface.OnClickListener() {
	                    public void onClick(DialogInterface dialog, int which) {
	                      //dismiss the dialog  
	                    	
	                    	finish();
	                    }
	                });
	                adg.create().show();
					
					
					
					
			    	
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if(json.getInt("errorCode")==1){
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								edt_currentPw.setError("Invalid Password");
							}
						});
						//Toast.makeText(getBaseContext(), "Invalid Password", Toast.LENGTH_SHORT).show();
					}else if (json.getInt("errorCode")==2) {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								
							}
						});
						//Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
					}else if (json.getInt("errorCode")==4) {
						edt_newEmail.setError(json.getString("message"));
					}else{
						Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
}
