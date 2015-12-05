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

import com.example.nucleus.Discussion.NetCheck;
import com.example.nucleus.ProjectInvite.getProjectMembersEmails;
import com.example.nucleus.ProjectInvite.sendProjectInvite;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TaskNew extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	Integer user_id, workspace_id, project_id;
	
	EditText edt_taskName;
	Button btn_assign, btn_duedate, btn_addtask;
	JSONArray members;
	public List<String> membersId = new ArrayList<String>();
	public List<String> membersName = new ArrayList<String>();
	int noOfTimesCalled =0;
	int assignId;
	
	
	String due_date;
	
	DatePickerDialog datepicker_dueDate;
	int day,month,year;
	SimpleDateFormat dateFormatter,dateFinalFormatter;
	Calendar cal;
	
	Boolean toAdd;
	String project_deadline;
	
	String goal_name;
	int goal_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.task_new);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		toAdd = false;
		assignId = 0;
		user_id = prefs.getInt("user_id", 0);
		project_id = prefs.getInt("project_id", 0);
		
		edt_taskName = (EditText) findViewById(R.id.edt_addtaskName);
		btn_assign = (Button) findViewById(R.id.btn_addtaskAssign);
		btn_duedate = (Button) findViewById(R.id.btn_addtaskDue);
		btn_addtask = (Button) findViewById(R.id.btn_addtask);
		TextView txt_top = (TextView) findViewById(R.id.textView5top);
		
		goal_name = getIntent().getStringExtra("goal_name");
		goal_id = getIntent().getIntExtra("goal_id", 0);
		txt_top.setText("Task Creation for the goal : "+goal_name);
		
		//datesss
		dateFormatter = new SimpleDateFormat("EEE, d MMMM yyyy");
		dateFinalFormatter = new SimpleDateFormat("yyyy-MM-dd");
		Calendar dd = Calendar.getInstance();
		dd.add(Calendar.DAY_OF_MONTH, 1);
		due_date = dateFinalFormatter.format(dd.getTime());
		
		btn_duedate.setText("Default: "+dateFormatter.format(dd.getTime()));
		
		cal = Calendar.getInstance();
		
		datepicker_dueDate = new DatePickerDialog(TaskNew.this, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				if(noOfTimesCalled%2==0){
					final Calendar newCal = Calendar.getInstance();
					newCal.set(year, monthOfYear, dayOfMonth);
					
					Calendar pDeadline = Calendar.getInstance();
					try {
						pDeadline.setTime(dateFinalFormatter.parse(project_deadline));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (newCal.compareTo(pDeadline)==1) {
						AlertDialog.Builder adb = new AlertDialog.Builder(TaskNew.this);
						adb.setTitle("Deadline");
						String pd="";
						try {
							pd = dateFormatter.format(dateFinalFormatter.parse(project_deadline));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							Toast.makeText(getBaseContext(), ""+e, Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
						adb.setMessage("This date exceeds the project deadline ("+pd+"). Are you sure you want to set this as the task due date?");
						adb.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								due_date = dateFinalFormatter.format(newCal.getTime());
								btn_duedate.setText(dateFormatter.format(newCal.getTime()));
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
				}
				noOfTimesCalled++;
				
				
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		
		btn_duedate.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				datepicker_dueDate.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()+( 1000 * 60 * 60 * 24 ));
				
				Calendar x = Calendar.getInstance();
				try {
					x.setTime(dateFinalFormatter.parse(project_deadline));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				try {
					//datepicker_dueDate.getDatePicker().setMaxDate(x.getTimeInMillis());
					datepicker_dueDate.show();
				} catch (Exception e) {
					AlertDialog.Builder adb = new AlertDialog.Builder(TaskNew.this);
					adb.setTitle("Deadline Error");
					adb.setMessage("Task due date cannot be extended. Day before project deadline already reached ");
					adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					adb.show();
				}
			}
		});
		//aaaend
		
		new NetCheck().execute();
		
		edt_taskName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_taskName.setError(null);
			}
		});
		
		btn_addtask.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (edt_taskName.getText().toString().trim().equals("")){
					edt_taskName.setError("Task name is required in adding a task.");
				}else{
					//Toast.makeText(getBaseContext(), due_date+" | "+assignId, Toast.LENGTH_SHORT).show();
					toAdd = true;
					new NetCheck().execute();
				
				}
			}
		});
		
		btn_assign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(TaskNew.this);
	            builderSingle.setIcon(R.drawable.ic_launcher);
	            //builderSingle.setTitle("Select One Name:-");
	            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TaskNew.this,
	                    android.R.layout.select_dialog_item);
	            arrayAdapter.add("Anyone");
	            for(int i = 0; i < membersId.size(); i++){
	            	arrayAdapter.add(membersName.get(i));
	            }
	            
	            


	            builderSingle.setAdapter(arrayAdapter,
	                    new DialogInterface.OnClickListener() {

	                        @Override
	                        public void onClick(DialogInterface dialog, int which) {
	                            String strName = arrayAdapter.getItem(which);
	                            if (which == 0){
	                            	assignId = 0;
	                            }else{
	                            	assignId = Integer.parseInt(membersId.get(which-1));
	                            }
	                            btn_assign.setText(strName);
	                            //Toast.makeText(getBaseContext(), ""+assignId, Toast.LENGTH_SHORT).show();
	                        }
	                    });
	            builderSingle.show();
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
                
                
				
				
				if(toAdd==true){
					toAdd=false;
					new addTask().execute();
				}else{
					new getProjectMembersForAssign().execute();
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
	
	public class getProjectMembersForAssign extends AsyncTask<Void, Void, JSONObject>{
		
		
		
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
			JSONObject json = uf.getProjectMembersForAssign(project_id);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					try {
						project_deadline = dateFinalFormatter.format(dateFinalFormatter.parse(json.getString("project_deadline")));
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					members= json.getJSONArray("membersName");
					membersId.clear();
					membersName.clear();
					
						for(int i=0; i<members.length();i++){
							JSONObject x = members.getJSONObject(i);
							
							String temp_id = x.getString("user_id");
							String temp_name = x.getString("full_name");
							TaskNew.this.membersId.add(temp_id);
							TaskNew.this.membersName.add(temp_name);
						}
						
						Toast.makeText(getBaseContext(), membersId.toString(), Toast.LENGTH_SHORT);
					
					
					
					
			
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
	
	public class addTask extends AsyncTask<Void, Void, JSONObject>{
		
		String task_name;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			task_name = edt_taskName.getText().toString().trim();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.addTask(user_id, goal_id, task_name, assignId, due_date);
			
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
			
				}else{
					setProgressBarIndeterminateVisibility(false);
					
					Toast.makeText(getBaseContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.e("log_tag", "Failed data was:\n" + e);
			}
		}
		
	}
	
	
}
