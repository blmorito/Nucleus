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

import com.example.nucleus.WsShowPendingMembers.NetCheck;
import com.example.nucleus.WsShowPendingMembers.cancelInvite;
import com.example.nucleus.WsShowPendingMembers.getWsPendingMembers;
import com.example.nucleus.WsShowPendingMembers.resendInvite;
import com.example.nucleus.WsShowPendingMembers.wsPendingMembersAdapter;
import com.example.nucleus.WsShowPendingMembers.wsPendingMembersAdapter.myViewHolder;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class ProjectMembersPending extends Activity {
	
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	Integer user_id, workspace_id, project_id, p_user_level_id;
	SharedPreferences prefs;
	
	LinearLayout ll_noConnect, ll_noPending, ll_pendingMem;
	ListView listView;
	LayoutInflater inflater;
	JSONArray pending;
	
	String cancelEmail;
	String resendEmail;
	
	Boolean toCancel, toResend;
	public List<String> email = new ArrayList<String>();
	public List<String> date_invited = new ArrayList<String>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.project_pending_members);
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		toResend = false;
		toCancel = false;
		ll_noConnect = (LinearLayout) findViewById(R.id.no_connection_layout_ppending);
		ll_noPending = (LinearLayout) findViewById(R.id.no_invites_pending);
		ll_pendingMem = (LinearLayout) findViewById(R.id.showpendingproj);
		
		listView = (ListView) findViewById(R.id.lvShowProjPending);
		listView.setEmptyView(findViewById(R.id.no_invites_pending));
		user_id = prefs.getInt("user_id", 0);
		project_id = prefs.getInt("project_id", 0);
		p_user_level_id = prefs.getInt("p_user_level_id", 0);
		
		new NetCheck().execute();
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
						ll_noConnect.setVisibility(LinearLayout.GONE);
						
					}
				});
                
             
                if(toResend==true){
                	toResend = false;
                	new resendInviteProject().execute();
                }else if(toCancel==true){
                	toCancel=false;
                	new cancelInviteProject().execute();
                }else{
                	
                	new getProjectPendingMembers().execute();
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
						ll_noConnect.setVisibility(LinearLayout.VISIBLE);
						ll_pendingMem.setVisibility(LinearLayout.GONE);
						
					}
				});
            	
                
                
            }
		}
		
	}
		
	public class getProjectPendingMembers extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//Log.e("wsid",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProjectPendingMembers(project_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					pending = json.getJSONArray("pending");
					
					ProjectMembersPending.this.email.clear();
					ProjectMembersPending.this.date_invited.clear();
					
					for(int i=0; i< pending.length(); i++){
						JSONObject z = pending.getJSONObject(i);
						
						
						String email = z.getString("email");
						String date = z.getString("date_invited");
						
						
						
						ProjectMembersPending.this.email.add(email);
						ProjectMembersPending.this.date_invited.add(date);
						
					
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							ll_pendingMem.setVisibility(LinearLayout.VISIBLE);
							
							ProjectPendingAdapter adapter = new ProjectPendingAdapter(getApplicationContext(), email, date_invited);
							listView.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							
						}
					});
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							
							//ll_pendingMem.setVisibility(LinearLayout.GONE);
							ProjectMembersPending.this.email.clear();
							ProjectMembersPending.this.date_invited.clear();
							ProjectPendingAdapter adapter = new ProjectPendingAdapter(getApplicationContext(), email, date_invited);
							listView.setAdapter(adapter);
							adapter.notifyDataSetChanged();
							
						}
					});
					
//						if (json.getInt("errorCode")==2){
//							
//							Toast.makeText(WsShowPendingMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//						}else{
//							
//							Toast.makeText(WsShowPendingMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//						}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	class ProjectPendingAdapter extends ArrayAdapter<String>{
		Context context;
		
		List<String> emailX;
		List<String> date_invitedX;
		
		
		ProjectPendingAdapter(Context c, List<String> email, List<String> date_invited){
			super(c,R.layout.project_pending_row,R.id.txt_ppendingEmail, email);
			this.context = c;
			
			this.emailX = email;
			this.date_invitedX = date_invited;
			
			
		}
		
		class myViewHolder{
			TextView text_email, text_dateInvited;
			Button btnResend, btnCancel;
			ImageView image;
			
			public myViewHolder(View view) {
				
				text_email = (TextView) view.findViewById(R.id.txt_ppendingEmail);
				text_dateInvited = (TextView) view.findViewById(R.id.txt_ppendingAgo);
				
				
				btnResend = (Button) view.findViewById(R.id.btn_ppendingResend);
				btnCancel = (Button) view.findViewById(R.id.btn_ppendingCancel);
				
				
				image = (ImageView) view.findViewById(R.id.img_ppendingPic);
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.project_pending_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				
				holder.text_email.setText(emailX.get(position));
				holder.text_dateInvited.setText(date_invitedX.get(position));
				
				holder.btnCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog.Builder adg = new AlertDialog.Builder(ProjectMembersPending.this);
		                //adg.setTitle("Connection Error");
		                adg.setMessage("This will cancel all the project invitations of this person in this workspace. Proceed?");
		                adg.setCancelable(true);
		                adg.setPositiveButton("Yes",
		                	    new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                      //dismiss the dialog  
		                    	cancelEmail = emailX.get(position);
		                    	cancelInvitation();
		                    }

							private void cancelInvitation() {
								// TODO Auto-generated method stub
								toCancel = true;
								new NetCheck().execute();
							}
		                });
		                adg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						});
		                adg.create().show();
					}
				});
				
				holder.btnResend.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog.Builder adg = new AlertDialog.Builder(ProjectMembersPending.this);
		                //adg.setTitle("Connection Error");
		                adg.setMessage("Resend email invitation to "+emailX.get(position)+"?");
		                adg.setCancelable(true);
		                adg.setPositiveButton("Ok",
		                	    new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                      //dismiss the dialog  
		                    	resendEmail = emailX.get(position);
		                    	resendInvitation();
		                    }

							private void resendInvitation() {
								// TODO Auto-generated method stub
								toResend = true;
								new NetCheck().execute();
							}
		                });
		                adg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}
						});
		                adg.create().show();
					}
				});
				
				
			
			
			return row;
		}
	}
	
	public class resendInviteProject extends AsyncTask<Void, Void, JSONObject>{
		
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			pDialog = new ProgressDialog(ProjectMembersPending.this);
            
            pDialog.setMessage("Resending email invite..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			//Log.e("wsid",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.resendProjectInvite(user_id, project_id, resendEmail);
			resendEmail=null;
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					pDialog.dismiss();
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(ProjectMembersPending.this, "Successfully sent another invite", Toast.LENGTH_SHORT).show();
					//new NetCheck().execute();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					
					if (json.getInt("errorCode")==2){
						
						Toast.makeText(ProjectMembersPending.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(ProjectMembersPending.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	public class cancelInviteProject extends AsyncTask<Void, Void, JSONObject>{
		
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			pDialog = new ProgressDialog(ProjectMembersPending.this);
            
            pDialog.setMessage("Cancelling Invite..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.cancelProjectInvite(user_id, project_id, cancelEmail);
			cancelEmail=null;
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					pDialog.dismiss();
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(ProjectMembersPending.this, "Successfully cancelled an invite", Toast.LENGTH_SHORT).show();
					new NetCheck().execute();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					
					if (json.getInt("errorCode")==2){
						
						Toast.makeText(ProjectMembersPending.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(ProjectMembersPending.this, json.getString("message"), Toast.LENGTH_SHORT).show();
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
