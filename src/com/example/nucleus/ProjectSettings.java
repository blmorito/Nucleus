package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.WorkspaceSettings.updateWorkspace;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

public class ProjectSettings extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	
	Integer user_id, workspace_id, ws_user_level_id, project_id;
	EditText edt_pName, edt_pDesc;
	Button btn_save, btn_cancel, btn_changeLeader, btn_deadline;
	Boolean toUpdate;
	
	JSONArray pmembers;
	String deadlinez;
	String deadline;
	DatePickerDialog datepicker_dueDate;
	SimpleDateFormat dateFormatter,dateFinalFormatter;
	Calendar cal;
	
	public List<String> projectMemberId = new ArrayList<String>();
	public List<String> projectMemberName = new ArrayList<String>();
	Integer project_leader_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.project_settings);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs= getSharedPreferences("MyPref", MODE_PRIVATE);
		toUpdate = false;
		user_id = prefs.getInt("user_id", 0);
		project_id = prefs.getInt("project_id", 0);
		
		
		
		cal = Calendar.getInstance();
		dateFormatter = new SimpleDateFormat("EEE, d MMMM yyyy");
		dateFinalFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		edt_pName = (EditText) findViewById(R.id.edt_projSetName);
		edt_pDesc = (EditText) findViewById(R.id.edt_projSetDesc);
		
		btn_save = (Button) findViewById(R.id.btn_projSetSave);
		btn_cancel = (Button) findViewById(R.id.btn_projSetCancel);
		btn_changeLeader = (Button) findViewById(R.id.btn_changeLeader);
		btn_deadline = (Button) findViewById(R.id.btn_projectSetDeadline);
		
		new NetCheck().execute();
		
		edt_pName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_pName.setError(null);
			}
		});
		
		datepicker_dueDate = new DatePickerDialog(ProjectSettings.this, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				Calendar newCal = Calendar.getInstance();
				newCal.set(year, monthOfYear, dayOfMonth);
				deadline = dateFinalFormatter.format(newCal.getTime());
				btn_deadline.setText(dateFormatter.format(newCal.getTime()));
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
		btn_deadline.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Calendar x = Calendar.getInstance();
				try {
					Log.e("ni sud sa try","yes");
					x.setTime(dateFinalFormatter.parse(deadlinez));
					datepicker_dueDate.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()+( 1000 * 60 * 60 * 24 ));
					datepicker_dueDate.show();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					Log.e("Asss",deadline);
					Log.e("ni sud sa try","yes adn cath");
					e.printStackTrace();
				}
				
				Toast.makeText(ProjectSettings.this, deadline, Toast.LENGTH_SHORT).show();
				
			}
		});
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edt_pName.getText().toString().trim().equals("")){
					edt_pName.setError("Field cannot be blank");
					
				}else{
					toUpdate = true;
					new NetCheck().execute();
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
		
		btn_changeLeader.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder adb = new AlertDialog.Builder(ProjectSettings.this);
				adb.setTitle("Select new project leader");
				ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ProjectSettings.this, android.R.layout.select_dialog_item);
				for(int i = 0; i< projectMemberId.size(); i++){
					arrayAdapter.add(projectMemberName.get(i));
				}
				
				adb.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						String name = projectMemberName.get(which);
						project_leader_id = Integer.parseInt(projectMemberId.get(which));
						btn_changeLeader.setText("Leader: "+name);
						
					}
				});
				adb.show();
			}
		});
		
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
						//noConnection.setVisibility(LinearLayout.GONE);
						
					}
				});
                
                if(toUpdate==true){
                	toUpdate =false;
                	new updateProjectSetting().execute();
                }else{
                	new getProjectSetting().execute();
                }
               // new updateWorkspace().execute();
               //new getWorkspaceInfo().execute();
                
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	setProgressBarIndeterminateVisibility(false);
            	
            	Toast.makeText(getBaseContext(), "Unable to connect to the server", Toast.LENGTH_SHORT);
            	
                
                
            }
		}
		
	}
	
	public class getProjectSetting extends AsyncTask<Void, Void, JSONObject>{
		
		
		
		
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
			JSONObject json = uf.getProjectSetting(user_id, project_id);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					
					project_leader_id = json.getInt("project_leader");
					pmembers = json.getJSONArray("project_members");
					
					projectMemberId.clear();
					projectMemberName.clear();
					
					for(int i=0; i<pmembers.length();i++){
						JSONObject z = pmembers.getJSONObject(i);
						int pid = z.getInt("pmember_id");
						String pname = z.getString("pmember_name");
						
						projectMemberId.add(""+pid);
						projectMemberName.add(pname);
						
					}
					
					btn_changeLeader.setText("Leader: "+projectMemberName.get(projectMemberId.indexOf(""+project_leader_id)));
					
					
					final String pname = json.getString("project_name");
					final String pdesc = json.getString("project_desc");
					runOnUiThread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							edt_pName.setText(pname);
							edt_pDesc.setText(pdesc);
						}
					});
					
					try {
						btn_deadline.setText(dateFormatter.format(dateFinalFormatter.parse(json.getString("project_deadline"))));
						deadline = dateFinalFormatter.format(dateFinalFormatter.parse(json.getString("project_deadline")));
						deadlinez = deadline;
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
//					Toast.makeText(getBaseContext(), "Successfully Updated Workspace Info", Toast.LENGTH_SHORT).show();
//					finish();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	public class updateProjectSetting extends AsyncTask<Void, Void, JSONObject>{
		String newName, newDesc;
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			newName = edt_pName.getText().toString().trim();
			newDesc = edt_pDesc.getText().toString().trim();
			
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			userFunctions uf = new userFunctions();
			JSONObject json = uf.updateProjectSetting(project_id, newName, newDesc, project_leader_id, deadline);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
					finish();
					
//					Toast.makeText(getBaseContext(), "Successfully Updated Workspace Info", Toast.LENGTH_SHORT).show();
//					finish();
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), json.getString("message"), Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
}
