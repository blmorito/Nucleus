package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.Dashboard.getWorkspaceName;
import com.example.nucleus.WsShowPendingMembers.NetCheck;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class DashboardInvites extends Activity {
	
	Integer user_id;
	Integer workspace_id;
	Integer switch_id;
	LinearLayout noConnect;
	LinearLayout noInvites;
	LinearLayout showInvites;
	
	ArrayList<HashMap<String, String>> invitesList;
	JSONArray invites;
	 int accepted_ws_id;
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	
	public List<String> invite_id = new ArrayList<String>();
	public List<String> full_name = new ArrayList<String>();
	public List<String> workspace_name = new ArrayList<String>();
	public List<String> project_name = new ArrayList<String>();
	public List<String> date_invited = new ArrayList<String>();
	public List<String> type = new ArrayList<String>();
	public List<Bitmap> avatars = new ArrayList<Bitmap>();
	
	public ListView listView;
	public LayoutInflater inflater;
	public Bitmap bitmap;
	
	
	public Boolean toAcceptOrDecline;
	public Boolean toChangeWs;
	
	public Integer inviteId;
	
	public String inviteAction;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.dashboard_invites);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		//flag for sending accept/decline task
		toAcceptOrDecline 	= false;
		toChangeWs = false;
		
		inviteId = 0;
		inviteAction = "";
		
		invitesList = new ArrayList<HashMap<String,String>>();
		
		SharedPreferences pref = getSharedPreferences("MyPref", MODE_PRIVATE) ;
		user_id = pref.getInt("user_id", 0);
		workspace_id = pref.getInt("workspace_id", 0);
		
		noConnect = (LinearLayout) findViewById(R.id.no_connection_layout_Invite);
		noInvites = (LinearLayout) findViewById(R.id.no_invites);
		showInvites = (LinearLayout) findViewById(R.id.show_invites);
		
		listView = (ListView) findViewById(R.id.lv_invites);
		
		new NetCheck().execute();
		
		TextView tryAgainX = (TextView) findViewById(R.id.tryAgainInvite);
		tryAgainX.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				new NetCheck().execute();
			}
		});
		
	}
	public Bitmap getRoundedShape(Bitmap scaleBitmapImage) {
	    int targetWidth = 250;
	    int targetHeight = 250;
	    Bitmap targetBitmap = Bitmap.createBitmap(targetWidth, 
	                        targetHeight,Bitmap.Config.ARGB_8888);

	    Canvas canvas = new Canvas(targetBitmap);
	    Path path = new Path();
	    path.addCircle(((float) targetWidth - 1) / 2,
	        ((float) targetHeight - 1) / 2,
	        (Math.min(((float) targetWidth), 
	        ((float) targetHeight)) / 2),
	        Path.Direction.CCW);

	    canvas.clipPath(path);
	    Bitmap sourceBitmap = scaleBitmapImage;
	    canvas.drawBitmap(sourceBitmap, 
	        new Rect(0, 0, sourceBitmap.getWidth(),
	        sourceBitmap.getHeight()), 
	        new Rect(0, 0, targetWidth, targetHeight), null);
	    return targetBitmap;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.invite_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch(item.getItemId()){
		
		
		case R.id.action_refresh_invite:
			new NetCheck().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
				
		}
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
                
                
                runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						noConnect.setVisibility(LinearLayout.GONE);
						
					}
				});
                
                if(toAcceptOrDecline==true){
                	toAcceptOrDecline=false;
                	new acceptOrDeclineInvite().execute();
                }else if (toChangeWs==true) {
					toChangeWs = false;
					new ChangeWs().execute();
				}else{
                	new getMyInvites().execute();
                }
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	toAcceptOrDecline = false;
            	runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						noConnect.setVisibility(LinearLayout.VISIBLE);
						noInvites.setVisibility(LinearLayout.GONE);
						showInvites.setVisibility(LinearLayout.GONE);
						
					}
				});
            	
                
                
            }
		}
		
	}
	public class ChangeWs extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.switchWorkspace(user_id, accepted_ws_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
	                
	                editor.putInt("workspace_id",accepted_ws_id );
	                editor.putInt("ws_user_level_id", 3);
	                editor.commit();
	                Intent intent = new Intent(DashboardInvites.this, Dashboard.class);
	                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
	                startActivity(intent);
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
					
					
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	public class getMyInvites extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.dashboardGetInvites(user_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					invites = json.getJSONArray("invites");
					DashboardInvites.this.invite_id.clear();
					DashboardInvites.this.full_name.clear();
					DashboardInvites.this.workspace_name.clear();
					DashboardInvites.this.project_name.clear();
					DashboardInvites.this.date_invited.clear();
					DashboardInvites.this.avatars.clear();
					DashboardInvites.this.type.clear();
					
					for(int i=0; i< invites.length(); i++){
						JSONObject x = invites.getJSONObject(i);
						
						String invite_id = ""+x.getInt("invite_id");
						String full_name = x.getString("full_name");
						String workspace_name = x.getString("workspace_name");
						String project_name = x.getString("project_name");
						String date_invited = x.getString("date_invited");
						String avatar_path = x.getString("avatar_path");
						String type = x.getString("type");
						
						DashboardInvites.this.invite_id.add(invite_id);
						DashboardInvites.this.full_name.add(full_name);
						DashboardInvites.this.workspace_name.add(workspace_name);
						DashboardInvites.this.project_name.add(project_name);
						DashboardInvites.this.date_invited.add(date_invited);
						DashboardInvites.this.type.add(type);
						
						byte[] blobimg = Base64.decode(avatar_path.getBytes(), 1);
				    	bitmap = BitmapFactory.decodeByteArray(blobimg, 0, blobimg.length);
						
				    	DashboardInvites.this.avatars.add(bitmap);
						
					}
					
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							noInvites.setVisibility(LinearLayout.GONE);
							showInvites.setVisibility(LinearLayout.VISIBLE);
							
							invitesAdapter adapter = new invitesAdapter(getApplicationContext(), invite_id, full_name, project_name, workspace_name, date_invited, avatars, type);
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
							noInvites.setVisibility(LinearLayout.VISIBLE);
							showInvites.setVisibility(LinearLayout.GONE);
							
							
						}
					});
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(DashboardInvites.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(DashboardInvites.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	public class acceptOrDeclineInvite extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.acceptOrDeclineInvite(user_id, inviteId, inviteAction);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					if(inviteAction.equalsIgnoreCase("Accept")){
						
						 accepted_ws_id = json.getInt("workspace_id");
						//Toast.makeText(getBaseContext(), "You have successfully accepted the invitation. You will now be redirected to that workspace.", Toast.LENGTH_SHORT).show();
						if(workspace_id == accepted_ws_id){
							
						}else{
							AlertDialog.Builder adb = new AlertDialog.Builder(DashboardInvites.this);
							adb.setTitle("Change Workspace");
							adb.setMessage("You have successfully accepted the invite. Do you want to switch to the other workspace that you just accepted?");
							adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									toChangeWs = true;
									new NetCheck().execute();
									
									
								}
							});
							adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									
								}
							});
							adb.show();
							
						}
					}else{
						Toast.makeText(getBaseContext(), "You have successfully declined the invitation", Toast.LENGTH_SHORT).show();
						
					}
					
					new NetCheck().execute();
					
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					
					if (json.getInt("errorCode")==1){
						
						Toast.makeText(DashboardInvites.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}else{
						
						Toast.makeText(DashboardInvites.this, json.getString("message"), Toast.LENGTH_SHORT).show();
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + json);
			}
		}
		
	}
	
	class invitesAdapter extends ArrayAdapter<String>{
		Context context;
		List<String> invite_idX;
		List<String> full_nameX;
		List<String> project_nameX;
		List<String> workspace_nameX;
		List<String> date_invitedX;
		List<Bitmap> avatarsX;
		List<String> typeX;
		
		invitesAdapter(Context c, List<String> invite_id, List<String> full_name, List<String> project_name, List<String> workspace_name, List<String> date_invited, List<Bitmap> avatars, List<String> type){
			super(c,R.layout.invite_row,R.id.txt_inviteId, invite_id);
			this.context = c;
			this.invite_idX = invite_id;
			this.full_nameX = full_name;
			this.project_nameX = project_name;
			this.workspace_nameX = workspace_name;
			this.date_invitedX = date_invited;
			this.avatarsX = avatars;
			this.typeX = type;
		}
		
		class myViewHolder{
			TextView text_inviteId, text_fullName, text_projectName, text_workspaceName, text_action, text_under,text_date;
			Button btnAccept, btnDecline;
			ImageView image;
			
			public myViewHolder(View view) {
				text_inviteId = (TextView) view.findViewById(R.id.txt_inviteId);
				text_fullName = (TextView) view.findViewById(R.id.txt_inviteUser);
				text_projectName = (TextView) view.findViewById(R.id.txt_inviteProject);
				text_workspaceName = (TextView) view.findViewById(R.id.txt_inviteWorkspace);
				
				text_action = (TextView) view.findViewById(R.id.txt_inviteAction);
				text_under = (TextView) view.findViewById(R.id.txt_inviteUnder);
				text_date = (TextView) view.findViewById(R.id.txt_inviteDate);
				
				btnAccept = (Button) view.findViewById(R.id.btn_inviteAccept);
				btnDecline = (Button) view.findViewById(R.id.btn_inviteDecline);
				
				image = (ImageView) view.findViewById(R.id.imgv_inviteUserPic);
				
			}
			
			
		}
		
		@Override
		public View getView(final int position, View convertView, ViewGroup parent){
			View row = convertView;
			myViewHolder holder = null;
			
			if (row == null){
				inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				row = inflater.inflate(R.layout.invite_row, parent, false);
				holder = new myViewHolder(row);
				row.setTag(holder);
			}else{
				holder = (myViewHolder) row.getTag();
			}
			
			
				if(type.get(position).toString().equalsIgnoreCase("Workspace")){
					holder.text_inviteId.setText(invite_idX.get(position));
					holder.text_fullName.setText(full_nameX.get(position));
					holder.text_projectName.setVisibility(View.GONE);
					holder.text_under.setVisibility(View.GONE);
					holder.text_action.setText("has invited you to the workspace");
					holder.text_workspaceName.setText(workspace_nameX.get(position));
					holder.text_date.setText(date_invitedX.get(position));
					holder.image.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				}else{
					holder.text_projectName.setVisibility(View.VISIBLE);
					holder.text_under.setVisibility(View.VISIBLE);
					holder.text_action.setText("has invited you to the project");
					holder.text_inviteId.setText(invite_idX.get(position));
					holder.text_fullName.setText(full_nameX.get(position));
					holder.text_projectName.setText(project_nameX.get(position));
					holder.text_workspaceName.setText(workspace_nameX.get(position));
					holder.text_date.setText(date_invitedX.get(position));
					holder.image.setImageBitmap(getRoundedShape(avatarsX.get(position)));
				}
				holder.btnAccept.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						//Toast.makeText(getBaseContext(), ""+project_nameX.get(position), Toast.LENGTH_SHORT).show();
						toAcceptOrDecline = true;
						inviteAction = "Accept";
						inviteId = Integer.parseInt(invite_idX.get(position));
						new NetCheck().execute();
					}
				});
				
				holder.btnDecline.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
						AlertDialog.Builder adg = new AlertDialog.Builder(DashboardInvites.this);
		                adg.setTitle("Confirmatiion");
		                if(typeX.get(position).equalsIgnoreCase("Workspace")){
		                	adg.setMessage("Are you sure you want to decline this invitation? This will also decline project invitations with the same workspace as this.");
		                }else{
		                	adg.setMessage("Are you sure you want to decline this invitation?");
		                }
		                adg.setCancelable(false);
		                adg.setPositiveButton("Yes",
		                	    new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int which) {
		                      //dismiss the dialog  
		                    	toAcceptOrDecline = true;
								inviteAction = "Decline";
								inviteId = Integer.parseInt(invite_idX.get(position));
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
