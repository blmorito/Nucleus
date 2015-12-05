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











import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class InviteWorkspace extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	Integer user_id, workspace_id;
	
	LayoutInflater inflater;
	
	LinearLayout container;
	LinearLayout inviteProjects;
	
	//views
	EditText edt_emailinvite;
	Button btn_addemailinvite;
	Button btn_sendemailinvite;
	ListView lv_invitews;
	JSONArray members, projects;
	
	public List<String> checkEmails = new ArrayList<String>();
	
	//for adapter
	public List<String> projectId = new ArrayList<String>();
	public List<String> projectName = new ArrayList<String>();
	
	public ArrayList<String> selectedProjects = new ArrayList<String>();
	public ArrayList<String> selectedProjectsName = new ArrayList<String>();
	public ArrayList<String> invites = new ArrayList<String>();
	
	public static Boolean sendInvite;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.invite_to_ws);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		sendInvite = false;
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		checkEmails = new ArrayList<String>();
		user_id = prefs.getInt("user_id", 0);
		workspace_id = prefs.getInt("workspace_id", 0);
		
		new NetCheck().execute();
		
		container = (LinearLayout) findViewById(R.id.container_invitews);
		inviteProjects = (LinearLayout) findViewById(R.id.layout_wsinviteprojects);
		
		lv_invitews = (ListView) findViewById(R.id.lv_wsinvite);
		
		btn_addemailinvite = (Button) findViewById(R.id.btn_addEmailInviteWs);
		btn_sendemailinvite = (Button) findViewById(R.id.btn_wsinvitesend);
		
		edt_emailinvite = (EditText) findViewById(R.id.edt_addEmailInviteWs);
		
		edt_emailinvite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_emailinvite.setError(null);
			}
		});
		
		btn_addemailinvite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_emailinvite.setError(null);
				
				if (edt_emailinvite.getText().toString().length()==0){
					edt_emailinvite.setError("Please input an email here");
				}else{
					if(isValidEmail(edt_emailinvite.getText().toString())){
						if(checkEmails.contains(edt_emailinvite.getText().toString())){
							edt_emailinvite.setError("The person you are trying to add is already a member of the workspace");
						}else{
							LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							final View addView = layoutInflater.inflate(R.layout.emailrow, null);
							TextView textOut = (TextView) addView.findViewById(R.id.edt_emailRow);
							textOut.setText(edt_emailinvite.getText().toString());
							Button buttonRemove = (Button) addView.findViewById(R.id.btn_removeRow);
							
							edt_emailinvite.setText("");
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
						edt_emailinvite.setError("Invalid email address");
					}
				}
			}

			
		});
		
		LayoutTransition transition = new LayoutTransition();
		container.setLayoutTransition(transition);
		
		btn_sendemailinvite.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				int childCount = container.getChildCount();
				
				if(childCount==0){
					edt_emailinvite.setError("Add an email to invite a person");
				}else{
					
					invites = new ArrayList<String>();
					for(int c=0; c<childCount; c++){
					     View childView = container.getChildAt(c);
					     TextView childTextView = (TextView)(childView.findViewById(R.id.edt_emailRow));
					     String childTextViewText = (String)(childTextView.getText());
					     
					     //showallPrompt += c + ": " + childTextViewText + "\n";
					     invites.add(childTextViewText);
					     
				     
				    }
					
					sendInvite=true;
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
                
                
                
                
               if (sendInvite==false){
            	   new getWsMemberEmails().execute();
                   new getProjectsForInvite().execute();
               }else{
            	   sendInvite=false;
            	   new sendWorkspaceInvite().execute();
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
	
	public class getWsMemberEmails extends AsyncTask<Void, Void, JSONObject>{
		
		Integer ws_id;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			ws_id =prefs.getInt("workspace_id", 0);
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getWsMemberEmails(ws_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					members= json.getJSONArray("members");
					
					
						for(int i=0; i<members.length();i++){
							JSONObject x = members.getJSONObject(i);
							
							String temp_email = x.getString("email");
							InviteWorkspace.this.checkEmails.add(temp_email);
						}
					
					
					
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(InviteWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(InviteWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	public class getProjectsForInvite extends AsyncTask<Void, Void, JSONObject>{
		
		Integer ws_id;
		Integer u_id;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			ws_id =prefs.getInt("workspace_id", 0);
			u_id = prefs.getInt("user_id", 0);
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProjectsForInvite(ws_id, u_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					projects= json.getJSONArray("projects");
					
					
					
					for(int i=0; i<projects.length();i++){
						JSONObject x = projects.getJSONObject(i);
						
						String project_id = x.getString("project_id");
						String project_name = x.getString("project_name");
						
						InviteWorkspace.this.projectId.add(project_id);
						InviteWorkspace.this.projectName.add(project_name);
					}
					
					if (projects.length()==0){
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								inviteProjects.setVisibility(LinearLayout.GONE);
							}
						});
					}else{
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								
								
								inviteProjects.setVisibility(LinearLayout.VISIBLE);
								wsInviteProjects adapter = new wsInviteProjects(getApplicationContext(), projectId, projectName);
								lv_invitews.setAdapter(adapter);
								adapter.notifyDataSetChanged();
								
							}
						});
					}
					
					
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(InviteWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(InviteWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class wsInviteProjects extends ArrayAdapter<String>{
		Context context;
		List<String> projectIdX;
		List<String> projectNameX;
	
		
		wsInviteProjects(Context c, List<String> projectId, List<String> projectName){
			super(c,R.layout.ws_projectbox,R.id.txt_projectidcheckbox, projectId);
			this.context = c;
			this.projectIdX = projectId;
			this.projectNameX = projectName;
		
		}
		
		class myViewHolder{
			TextView text_projectId;
			
			CheckBox cb;
			
			public myViewHolder(View view) {
				text_projectId = (TextView) view.findViewById(R.id.txt_projectidcheckbox);
				cb = (CheckBox) view.findViewById(R.id.cb_project);
	
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.ws_projectbox, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
				
				
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				holder.text_projectId.setText(projectIdX.get(position));
				holder.cb.setText(projectNameX.get(position));
				
				
				final boolean isChecked = holder.cb.isChecked();
				final String checkedProject = holder.text_projectId.getText().toString();
				
				
				holder.cb.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						
						if (selectedProjects.contains(projectIdX.get(position))){
							selectedProjects.remove(selectedProjects.indexOf(projectIdX.get(position)));
						}else{
							selectedProjects.add(projectIdX.get(position));
						}
						
//						String tust="";
//						for(int i=0;i<selectedProjects.size();i++){
//							tust+=selectedProjects.get(i)+"\n";
//						}
//						
//						Toast.makeText(getBaseContext(), tust, Toast.LENGTH_SHORT).show();
						
						
						
						
						
					
					}
				});
				
				
				
			
			
			return row;
		}
	}
	
	public class sendWorkspaceInvite extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pDialog;
		Integer ws_id;
		Integer u_id;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(InviteWorkspace.this);
            
            pDialog.setMessage("Sending Invites..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
			
			ws_id =prefs.getInt("workspace_id", 0);
			u_id = prefs.getInt("user_id", 0);
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.sendWorkspaceInvites(ws_id, u_id, invites, selectedProjects);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					pDialog.dismiss();
					 
					startActivity(new Intent(InviteWorkspace.this, InviteWorkspaceSuccess.class));
					finish();
					
					
					
					
					
					
					
					
					
			
				}else{
					pDialog.dismiss();
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(InviteWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(InviteWorkspace.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}

}
