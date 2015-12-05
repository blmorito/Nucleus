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





import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class WsShowPendingMembers extends Activity {
	
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	Integer ws_id, ws_user_level_id;
	SharedPreferences prefs;
	
	LinearLayout noConnection;
	LinearLayout noPending;
	LinearLayout showPending;
	LinearLayout loader;
	
	LayoutInflater inflater;
	ListView listView;
	
	public List<String> email = new ArrayList<String>();
	public List<String> date_invited = new ArrayList<String>();
	
	
	JSONArray pending;
	
	String cancelEmail;
	String resendEmail;
	Boolean toCancel;
	Boolean toResend;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ws_show_pending_members);
		
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		cancelEmail = null;
		resendEmail = null;
		toCancel = false;
		toResend = false;
		ws_id = prefs.getInt("workspace_id", 0);
		ws_user_level_id = prefs.getInt("ws_user_level_id", 0);
		noConnection = (LinearLayout) findViewById(R.id.no_connection_layout_wspending);
		noPending = (LinearLayout) findViewById(R.id.no_invites_pending);
		showPending = (LinearLayout) findViewById(R.id.ws_showpendingmembers);
		loader = (LinearLayout) findViewById(R.id.ws_showmembersloader2);
		
		listView = (ListView) findViewById(R.id.lv_ws_showPendingMembers);
		
		loadPending();
		
	}
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		loadPending();
		super.onRestart();
	}

	public void loadPending() {
		// TODO Auto-generated method stub
		new NetCheck().execute();
	}
	
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					loader.setVisibility(LinearLayout.VISIBLE);
				}
			});
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
						noConnection.setVisibility(LinearLayout.GONE);
						
					}
				});
                
               if (toCancel==true){
            	   toCancel=false;
            	   new cancelInvite().execute();
               }else if(toResend==true){
            	   toResend=false;
            	   new resendInvite().execute();
               }else{
            	   new getWsPendingMembers().execute();
               }
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	toCancel=false;
            	toResend=false;
            	runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						noConnection.setVisibility(LinearLayout.VISIBLE);
						showPending.setVisibility(LinearLayout.GONE);
						loader.setVisibility(LinearLayout.GONE);
					}
				});
            	
                
                
            }
		}
		
	}
	
	public class getWsPendingMembers extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("wsid",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getWsPendingMembers(ws_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					pending = json.getJSONArray("pending");
					
					WsShowPendingMembers.this.email.clear();
					WsShowPendingMembers.this.date_invited.clear();
					
					for(int i=0; i< pending.length(); i++){
						JSONObject z = pending.getJSONObject(i);
						
						
						String email = z.getString("email");
						String date = z.getString("date_invited");
						
						
						
						WsShowPendingMembers.this.email.add(email);
						WsShowPendingMembers.this.date_invited.add(date);
						
					
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							
							showPending.setVisibility(LinearLayout.VISIBLE);
							noPending.setVisibility(LinearLayout.GONE);
							loader.setVisibility(LinearLayout.GONE);
							wsPendingMembersAdapter adapter = new wsPendingMembersAdapter(getApplicationContext(), email, date_invited);
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
							
							showPending.setVisibility(LinearLayout.GONE);
							loader.setVisibility(LinearLayout.GONE);
							noPending.setVisibility(LinearLayout.VISIBLE);
							
							
						}
					});
					
//					if (json.getInt("errorCode")==2){
//						
//						Toast.makeText(WsShowPendingMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//					}else{
//						
//						Toast.makeText(WsShowPendingMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
//					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class cancelInvite extends AsyncTask<Void, Void, JSONObject>{
		
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			pDialog = new ProgressDialog(WsShowPendingMembers.this);
            
            pDialog.setMessage("Cancelling Invites..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("wsid",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.cancelWSinvitation(ws_id, cancelEmail);
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
					Toast.makeText(WsShowPendingMembers.this, "Successfully cancelled an invite", Toast.LENGTH_SHORT).show();
					new NetCheck().execute();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					
					if (json.getInt("errorCode")==2){
						
						Toast.makeText(WsShowPendingMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(WsShowPendingMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	public class resendInvite extends AsyncTask<Void, Void, JSONObject>{
		
		ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			pDialog = new ProgressDialog(WsShowPendingMembers.this);
            
            pDialog.setMessage("Resending email invite..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("wsid",""+ws_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.resendWSinvitation(ws_id, resendEmail);
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
					Toast.makeText(WsShowPendingMembers.this, "Successfully sent another invite", Toast.LENGTH_SHORT).show();
					//new NetCheck().execute();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					pDialog.dismiss();
					
					if (json.getInt("errorCode")==2){
						
						Toast.makeText(WsShowPendingMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(WsShowPendingMembers.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	class wsPendingMembersAdapter extends ArrayAdapter<String>{
		Context context;
		
		List<String> emailX;
		List<String> date_invitedX;
		
		
		wsPendingMembersAdapter(Context c, List<String> email, List<String> date_invited){
			super(c,R.layout.wspendingmembers_row,R.id.txt_pendingEmail, email);
			this.context = c;
			
			this.emailX = email;
			this.date_invitedX = date_invited;
			
			
		}
		
		class myViewHolder{
			TextView text_email, text_dateInvited;
			Button btnResend, btnCancel;
			ImageView image;
			
			public myViewHolder(View view) {
				
				text_email = (TextView) view.findViewById(R.id.txt_pendingEmail);
				text_dateInvited = (TextView) view.findViewById(R.id.txt_pendingAgo);
				
				
				btnResend = (Button) view.findViewById(R.id.btn_pendingResend);
				btnCancel = (Button) view.findViewById(R.id.btn_pendingCancel);
				
				
				image = (ImageView) view.findViewById(R.id.img_pendingPic);
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.wspendingmembers_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
				if(ws_user_level_id==3){
					holder.btnCancel.setVisibility(View.GONE);
					holder.btnResend.setVisibility(View.GONE);
				}
				
				holder.text_email.setText(emailX.get(position));
				holder.text_dateInvited.setText(date_invitedX.get(position));
				
				holder.btnCancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AlertDialog.Builder adg = new AlertDialog.Builder(WsShowPendingMembers.this);
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
						AlertDialog.Builder adg = new AlertDialog.Builder(WsShowPendingMembers.this);
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
}
