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

import com.example.nucleus.TaskView.NetCheck;
import com.example.nucleus.TaskView.deleteTask;
import com.example.nucleus.TaskView.getProjectMembersForAssign;
import com.example.nucleus.TaskView.getTaskInfo;
import com.example.nucleus.TaskView.saveTask;
import com.example.nucleus.TaskView.toStartTask;
import com.example.nucleus.TaskView.toUpdateTask;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
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
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TaskEdit extends Activity {
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	SharedPreferences prefs;
	int task_id, user_id, project_id, p_user_level_id;
	
	
	
	
	
	Button btn_assign, btn_deadline, btn_save;
	EditText edt_taskName;
	
	Integer assignId;
	String due_date;
	
	//bools
	Boolean toSave;
	
	
	JSONArray members;
	public List<String> membersId = new ArrayList<String>();
	public List<String> membersName = new ArrayList<String>();
	
	//getextras
	String task_name, task_date, task_status, task_isLate, task_assignee;
	
	String project_deadline;
	DatePickerDialog datepicker_dueDate;
	SimpleDateFormat dateFormatter,dateFinalFormatter;
	Calendar cal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		setContentView(R.layout.task_edit);
		
		toSave=false;
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		user_id = prefs.getInt("user_id", 0);
		project_id = prefs.getInt("project_id", 0);
		task_id = getIntent().getIntExtra("task_id", 0);
		task_status = getIntent().getStringExtra("task_status");
		
		cal = Calendar.getInstance();
		dateFormatter = new SimpleDateFormat("EEE, d MMMM yyyy");
		dateFinalFormatter = new SimpleDateFormat("yyyy-MM-dd");
		
		btn_assign = (Button) findViewById(R.id.btn_taskviewAssign);
		btn_deadline = (Button) findViewById(R.id.btn_taskviewDue);
		btn_save = (Button) findViewById(R.id.btn_taskviewSave);
		edt_taskName = (EditText) findViewById(R.id.edt_taskviewName);
		
		datepicker_dueDate = new DatePickerDialog(TaskEdit.this, new OnDateSetListener() {
			int noOfTimesCalled = 0;
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
						AlertDialog.Builder adb = new AlertDialog.Builder(TaskEdit.this);
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
								btn_deadline.setText(dateFormatter.format(newCal.getTime()));
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
		
		
		btn_deadline.setOnClickListener(new OnClickListener() {
			
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
					AlertDialog.Builder adb = new AlertDialog.Builder(TaskEdit.this);
					adb.setTitle("Deadline Error");
					adb.setMessage("Task due date cannot be extended. Project deadline already reached");
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
		
		btn_assign.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AlertDialog.Builder builderSingle = new AlertDialog.Builder(TaskEdit.this);
	            builderSingle.setIcon(R.drawable.ic_launcher);
	            //builderSingle.setTitle("Select One Name:-");
	            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(TaskEdit.this,
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
		
		edt_taskName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_taskName.setError(null);
			}
		});
		
		btn_save.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(edt_taskName.getText().toString().trim().equals("")){
					edt_taskName.setError("You must name this task!");
				}else{
					toSave=true;
					new NetCheck().execute();
				}
			}
		});
		
		if (task_status.equalsIgnoreCase("Done")){
			//cb_task.setChecked(true);
			btn_assign.setEnabled(false);
			btn_deadline.setEnabled(false);
		}else{
			//cb_task.setChecked(false);
			btn_assign.setEnabled(true);
			btn_deadline.setEnabled(true);
		}
		new NetCheck().execute();
		
	}
	
	public class NetCheck extends AsyncTask<Void, Void, Boolean>{
		ProgressDialog pdialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//setProgressBarIndeterminateVisibility(true);
			pdialog = new ProgressDialog(TaskEdit.this);
			pdialog.setMessage("Loading task info");
			pdialog.setIndeterminate(false);
			pdialog.setCancelable(false);
			pdialog.show();
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
			pdialog.dismiss();
			if(result == true){
				//setProgressBarIndeterminateVisibility(false);
                
                
				
				
				if (toSave == true){
					toSave = false;
					new saveTask().execute();
				}else{
					new getProjectMembersForAssign().execute();
				}
				
                //new loginAccount().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                
            }
            else{
            	//setProgressBarIndeterminateVisibility(false);


            	
            	Toast.makeText(getBaseContext(), "Couldn't connect to the network", Toast.LENGTH_SHORT).show();
                
                
            }
		}
		
	}
	
	public class saveTask extends AsyncTask<Void, Void, JSONObject>{
		String newTaskName;
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
			
			newTaskName = edt_taskName.getText().toString().trim();
            
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.saveTask(user_id, task_id, newTaskName, assignId, due_date );
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					setProgressBarIndeterminateVisibility(false);
					 
					
					Toast.makeText(getBaseContext(), "You have successfully updated the task", Toast.LENGTH_SHORT).show();
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

	public class getProjectMembersForAssign extends AsyncTask<Void, Void, JSONObject>{
	
		ProgressDialog pd;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			//setProgressBarIndeterminateVisibility(true);
			pd = new ProgressDialog(TaskEdit.this);
			pd.setMessage("Loading task info");
			pd.setIndeterminate(false);
			pd.setCancelable(false);
			pd.show();
	        
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			Log.e("huh",""+project_id);
			userFunctions uf = new userFunctions();
			JSONObject json = uf.getProjectMembersForAssignAndInfo(project_id, task_id);
			
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			// TODO Auto-generated method stub
			try {
				
				if (json.getInt("success")==1){
					//setProgressBarIndeterminateVisibility(false);
					 pd.dismiss();
					members= json.getJSONArray("membersName");
					membersId.clear();
					membersName.clear();
					
						for(int i=0; i<members.length();i++){
							JSONObject x = members.getJSONObject(i);
							
							String temp_id = x.getString("user_id");
							String temp_name = x.getString("full_name");
							TaskEdit.this.membersId.add(temp_id);
							TaskEdit.this.membersName.add(temp_name);
						}
						
						Toast.makeText(getBaseContext(), membersId.toString(), Toast.LENGTH_SHORT);
						
						assignId = json.getInt("assignee_id");
						
						try {
							due_date = dateFinalFormatter.format(dateFinalFormatter.parse(json.getString("date_due")));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						if (assignId==0){
							btn_assign.setText("Anyone");
						}else{
							Log.e("iddd",""+assignId+" | "+membersId.indexOf(assignId));
							Log.e("arrr",membersId.toString());
							btn_assign.setText(membersName.get(membersId.indexOf(""+assignId)));
						}
						
						try {
							btn_deadline.setText(dateFormatter.format(dateFinalFormatter.parse(due_date)));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						try {
							project_deadline = dateFinalFormatter.format(dateFinalFormatter.parse(json.getString("project_deadline")));
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						edt_taskName.setText(json.getString("task_name"));
					
						
					
			
				}else{
					//setProgressBarIndeterminateVisibility(false);
					
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
