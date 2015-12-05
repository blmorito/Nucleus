package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;







import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.InviteWorkspace.NetCheck;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateWorkspace extends Activity {
	
	SharedPreferences pref;
	Integer user_id, workspace_id;
	
	EditText edt_workspaceName, edt_workspaceDesc, edt_addEmail;
	Button btn_addEmail, btn_createWorkspace;
	public ArrayList<String> invites = new ArrayList<String>();
	
	LinearLayout container;
	
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.create_ws);
		
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		pref = getSharedPreferences("MyPref", MODE_PRIVATE);
		user_id = pref.getInt("user_id", 0);
		workspace_id = pref.getInt("workspace_id", 0);
		
		edt_workspaceName = (EditText) findViewById(R.id.edt_workspaceName);
		edt_workspaceDesc = (EditText) findViewById(R.id.edt_workspaceDesc);
		edt_addEmail = (EditText) findViewById(R.id.edt_createWsInviteEmail);
		container = (LinearLayout) findViewById(R.id.containerws);
		edt_workspaceName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_workspaceName.setError(null);
			}
		});
		
		edt_addEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_addEmail.setError(null);
			}
		});
		
		btn_addEmail = (Button) findViewById(R.id.btn_createWsAddEmail);
		btn_createWorkspace = (Button) findViewById(R.id.btn_createWorkspace);
		
		btn_addEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_addEmail.getText().toString().trim().equals("")){
					edt_addEmail.setError("An email is needed to send an invite.");
				}else if (isValidEmail(edt_addEmail.getText().toString().trim())) {
					LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					final View addView = layoutInflater.inflate(R.layout.emailrow, null);
					TextView textOut = (TextView) addView.findViewById(R.id.edt_emailRow);
					textOut.setText(edt_addEmail.getText().toString());
					Button buttonRemove = (Button) addView.findViewById(R.id.btn_removeRow);
					
					edt_addEmail.setText("");
					InputMethodManager inputManager = (InputMethodManager)
	                getSystemService(Context.INPUT_METHOD_SERVICE); 
					inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
	                           InputMethodManager.HIDE_NOT_ALWAYS);
					buttonRemove.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							((LinearLayout)addView.getParent()).removeView(addView);
						}
					});
					container.addView(addView);
				}else{
					edt_addEmail.setError("Invalid email address");
				}
			}
		});
		
		btn_createWorkspace.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_workspaceName.getText().toString().trim().equals("")){
					edt_workspaceName.setError("This field cannot be blank");
				}else{
					int childCount = container.getChildCount();
					
					invites = new ArrayList<String>();
					for(int c=0; c<childCount; c++){
					     View childView = container.getChildAt(c);
					     TextView childTextView = (TextView)(childView.findViewById(R.id.edt_emailRow));
					     String childTextViewText = (String)(childTextView.getText());
					     
					     //showallPrompt += c + ": " + childTextViewText + "\n";
					     invites.add(childTextViewText);
					     
				     
				    }
					
					
				    new NetCheck().execute();
						
					
				}
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
                
                
                
                
             
               
                new CreateNewWorkspace().execute();
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	
            	Toast.makeText(getBaseContext(), "Couldn't connect to the network", Toast.LENGTH_SHORT).show();
                
                
            }
		}
		
	}
	
	public class CreateNewWorkspace extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pDialog;
		
		String ws_name = edt_workspaceName.getText().toString().trim();
		String ws_desc = edt_workspaceDesc.getText().toString().trim();
		
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			
			pDialog = new ProgressDialog(CreateWorkspace.this);
            
            pDialog.setMessage("Creating Workspace..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			userFunctions uf = new userFunctions();
			JSONObject json = uf.createWorkspace(user_id, ws_name,ws_desc, invites);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			
			
			// TODO Auto-generated method stub
			try {
				if (json.getInt("success")==1){
					pDialog.dismiss();
					

	                SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
	                
	                editor.putInt("workspace_id", json.getInt("workspace_id"));
	                editor.putInt("ws_user_leve_id", 1);
	                editor.commit();
	                Intent intent = new Intent(CreateWorkspace.this, CreateWorkspaceSuccess.class);
	                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	                startActivity(intent);
					//Toast.makeText(getBaseContext(), "Done!", Toast.LENGTH_SHORT).show();
	                
					
				}else{
					if (json.getInt("errorCode")==1){
						Toast.makeText(CreateWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
						pDialog.dismiss();
					}else{
						Toast.makeText(CreateWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
