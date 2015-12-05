package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;













import com.example.nucleus.InviteWorkspace.NetCheck;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectInvite extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	Integer user_id, workspace_id, project_id;
	
	LayoutInflater inflater;
	
	LinearLayout container;
	
	LinearLayout noConnect, inviteUI;
	
	//views
	//EditText edt_addEmail;
	Button btn_addEmail, btn_invite;
	
	JSONArray members;
	JSONArray wsmembers;
	public List<String> checkEmails = new ArrayList<String>();
	public List<String> wsMembers = new ArrayList<String>();
	public List<String> emailsToInvite = new ArrayList<String>();
	
	AutoCompleteTextView act;
	Boolean toInvite;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.project_invite);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		toInvite = false;
		
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		project_id = prefs.getInt("project_id", 0);
		
		container = (LinearLayout) findViewById(R.id.container_invitep);
		//edt_addEmail = (EditText) findViewById(R.id.edt_pInvEmail);
		btn_addEmail = (Button) findViewById(R.id.btn_pInvAdd);
		btn_invite = (Button) findViewById(R.id.btn_pInvInvite);
		
		noConnect = (LinearLayout) findViewById(R.id.no_connection_layout_pinv);
		inviteUI = (LinearLayout) findViewById(R.id.layoutProjectInvite);
		
		act = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,wsMembers);
		act.setAdapter(adapter);
		
		LayoutTransition transition = new LayoutTransition();
		container.setLayoutTransition(transition);
		
		new NetCheck().execute();
		
		act.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				act.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						act.setError(null);
					}
				});
			}
		});
		
		btn_addEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (act.getText().toString().trim().equals("")){
					act.setError("Field cannot be blank");
				}else{
					if(isValidEmail(act.getText().toString())){
						if (checkEmails.contains(act.getText().toString().trim())){
							act.setError("This person is already a member of the project");
						}else{
							LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							final View addView = layoutInflater.inflate(R.layout.emailrow, null);
							TextView textOut = (TextView) addView.findViewById(R.id.edt_emailRow);
							textOut.setText(act.getText().toString());
							Button buttonRemove = (Button) addView.findViewById(R.id.btn_removeRow);
							
							act.setText("");
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
						}
					}else{
						act.setError("This is an invalid email");
					}
					
				}
			}
		});
		
		btn_invite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int childCount = container.getChildCount();
				
				if(childCount==0){
					act.setError("Add an email to invite a person");
				}else{
					emailsToInvite = new ArrayList<String>();
					for(int c=0; c<childCount; c++){
					     View childView = container.getChildAt(c);
					     TextView childTextView = (TextView)(childView.findViewById(R.id.edt_emailRow));
					     String childTextViewText = (String)(childTextView.getText());
					     
					     //showallPrompt += c + ": " + childTextViewText + "\n";
					     emailsToInvite.add(childTextViewText);
					     
				     
				    }
					
					toInvite=true;
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
                
                
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						noConnect.setVisibility(LinearLayout.GONE);
						inviteUI.setVisibility(LinearLayout.VISIBLE);
					}
				});
                
                if (toInvite==true){
                	toInvite = false;
                	new sendProjectInvite().execute();
                }else{
                	
                	new getProjectMembersEmails().execute();
                }
              
               
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);


            	runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						noConnect.setVisibility(LinearLayout.VISIBLE);
						inviteUI.setVisibility(LinearLayout.GONE);
					}
				});
            	
            	Toast.makeText(getBaseContext(), "Couldn't connect to the network", Toast.LENGTH_SHORT).show();
                
                
            }
		}
		
	}
	public class getProjectMembersEmails extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProjectMembersEmails(project_id);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					members= json.getJSONArray("emails");
					wsmembers = json.getJSONArray("wsemails");
						ProjectInvite.this.wsMembers.clear();
						for(int i=0; i<members.length();i++){
							JSONObject x = members.getJSONObject(i);
							
							String temp_email = x.getString("email");
							ProjectInvite.this.checkEmails.add(temp_email);
						}
						
						for(int i=0;i<wsmembers.length();i++){
							JSONObject x = wsmembers.getJSONObject(i);
							
							String temp_email = x.getString("email");
							ProjectInvite.this.wsMembers.add(temp_email);
						}
					
					
					
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class sendProjectInvite extends AsyncTask<Void, Void, JSONObject>{
		
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			pDialog = new ProgressDialog(ProjectInvite.this);
            
            pDialog.setMessage("Sending Invites..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.sendProjectInvite(user_id, workspace_id, project_id, emailsToInvite);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			pDialog.dismiss();
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					startActivity(new Intent(ProjectInvite.this, ProjectInviteSuccess.class));
					finish();
					
					
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), "Unable to connect to the network", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}

}
