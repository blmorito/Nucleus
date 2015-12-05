package com.example.nucleus;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.nucleus.SignUpForm.RegisterMe;

import android.animation.LayoutTransition;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class CreateProject extends Activity {
	EditText edt_addEmail;
	Button btn_addEmail;
	LinearLayout container;
	ArrayList<String> invites;
	Button btn_startProject;
	static Globals g = Globals.getInstance();
	static String ip = g.getMyIp();
	public EditText edt_projectName, edt_projectDesc;
	Boolean fromFreshSignUp;
	Button btn_deadLine;
	String workspace_id,user_id,user_email;
	
	String due_date;
	//date
	DatePickerDialog datepicker_dueDate;
	int day,month,year;
	SimpleDateFormat dateFormatter,dateFinalFormatter;
	Calendar cal;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_project);
		
		dateFormatter = new SimpleDateFormat("EEE, d MMMM yyyy");
		dateFinalFormatter = new SimpleDateFormat("d MMMM yyyy");
		SharedPreferences prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		Calendar dd = Calendar.getInstance();
		dd.add(Calendar.DAY_OF_MONTH, 1);
		due_date = dateFinalFormatter.format(dd.getTime());
		workspace_id= ""+prefs.getInt("workspace_id", 0);
		user_id = ""+prefs.getInt("user_id", 0);
		user_email = prefs.getString("user_email", null);
		//dates
		cal = Calendar.getInstance();
		
		datepicker_dueDate = new DatePickerDialog(CreateProject.this, new OnDateSetListener() {
			
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				Calendar newCal = Calendar.getInstance();
				newCal.set(year, monthOfYear, dayOfMonth);
				due_date = dateFinalFormatter.format(newCal.getTime());
				btn_deadLine.setText(dateFormatter.format(newCal.getTime()));
			}
		}, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
		btn_deadLine = (Button) findViewById(R.id.btn_deadline);
		
		btn_deadLine.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				datepicker_dueDate.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis()+( 1000 * 60 * 60 * 24 ));
				datepicker_dueDate.show();
			}
		});
		//emailadds
		Button btn_skipProject = (Button) findViewById(R.id.btn_skipProject);
		fromFreshSignUp = getIntent().getBooleanExtra("fromFreshSignup", false);
		if (fromFreshSignUp){
			btn_skipProject.setVisibility(View.VISIBLE);
		}
		btn_skipProject.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startActivity(new Intent(CreateProject.this, Dashboard.class));
				finish();
			}
		});
		
		edt_addEmail = (EditText) findViewById(R.id.edt_addEmail);
		btn_addEmail = (Button) findViewById(R.id.btn_addEmail);
		edt_projectName = (EditText) findViewById(R.id.edt_projectName);
	    edt_projectDesc = (EditText) findViewById(R.id.edt_projectDesc);
		container = (LinearLayout) findViewById(R.id.container);
		
		edt_projectName.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_projectName.setError(null);
			}
		});
		
		
		//action bar change color
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		edt_addEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				edt_addEmail.setError(null);
			}
		});
		btn_addEmail.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				edt_addEmail.setError(null);
				// TODO Auto-generated method stub
				if (edt_addEmail.getText().length()==0){
					
			            edt_addEmail.setError("Field cannot be left blank.");
			        
				}else {
					if (isValidEmail(edt_addEmail.getText().toString())){
						
						if (edt_addEmail.getText().toString().equalsIgnoreCase(user_email)){
							edt_addEmail.setError("You are automatically a member of this project");
						}else{
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
						}
					}else{
						edt_addEmail.setError("Invalid Email");
					}
					
				}
			}
		});
		
		LayoutTransition transition = new LayoutTransition();
		container.setLayoutTransition(transition);
		
		btn_startProject = (Button) findViewById(R.id.btn_startProject);
		btn_startProject.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (edt_projectName.getText().toString().trim().equals("")){
					edt_projectName.setError("This field cannot be blank");
				}else{
					String showallPrompt = "";

				    int childCount = container.getChildCount();
				    showallPrompt += "childCount: " + childCount + "\n\n";
				    invites = new ArrayList<String>();
				    
				    for(int c=0; c<childCount; c++){
				     View childView = container.getChildAt(c);
				     TextView childTextView = (TextView)(childView.findViewById(R.id.edt_emailRow));
				     String childTextViewText = (String)(childTextView.getText());
				     
				     //showallPrompt += c + ": " + childTextViewText + "\n";
				     invites.add(childTextViewText);
				     
				    }
				    
				    for (String temp : invites){
				    	showallPrompt += temp+"\n";
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
		private ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(CreateProject.this);
			pDialog.setMessage("Loading..");
			pDialog.setTitle("Checking Connection");
			pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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
                pDialog.dismiss();
                
                new CreateNewProject().execute();
                //Toast.makeText(getBaseContext(), "Connection Active", Toast.LENGTH_SHORT).show();
                //new ProcessRegister().execute();
            }
            else{
                pDialog.dismiss();
                //Toast.makeText(getBaseContext(), "No Connection", Toast.LENGTH_SHORT).show();
                
                //registerErrorMsg.setText("Error in Network Connection");
                AlertDialog.Builder adg = new AlertDialog.Builder(CreateProject.this);
                adg.setTitle("Connection Error");
                adg.setMessage("You dont seem to be connected to the internet. Please connect and try again.");
                adg.setCancelable(true);
                adg.setPositiveButton("Ok",
                	    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                      //dismiss the dialog  
                    }
                });
                adg.create().show();
            }
		}
		
	}
	
	public class CreateNewProject extends AsyncTask<Void, Void, JSONObject>{
		ProgressDialog pDialog;
		String projectName = edt_projectName.getText().toString();
		String projectDesc = edt_projectDesc.getText().toString();
		
		
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			
			pDialog = new ProgressDialog(CreateProject.this);
            
            pDialog.setMessage("Creating Project..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
		}
		@Override
		protected JSONObject doInBackground(Void... params) {
			// TODO Auto-generated method stub
			userFunctions uf = new userFunctions();
			JSONObject json = uf.createProject(projectName, projectDesc, invites, workspace_id, user_id, due_date);
			return json;
		}
		
		@Override
		protected void onPostExecute(JSONObject json) {
			
			
			// TODO Auto-generated method stub
			try {
				if (json.getInt("success")==1){
					pDialog.dismiss();
					
					
					final int p_id = json.getInt("project_id");
					final String p_name = json.getString("project_name");
					
					AlertDialog.Builder adg = new AlertDialog.Builder(CreateProject.this);
	               
                
	                adg.setMessage("You have successfully created a project.");
	                adg.setCancelable(false);
	                
	                adg.setPositiveButton("Ok",
	                	    new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                     //dismiss the dialog
                    	SharedPreferences.Editor editor = getSharedPreferences("MyPref", MODE_PRIVATE).edit();
                        
                        editor.putInt("p_user_level_id", 1);
                        editor.putInt("project_id", p_id);
                        editor.commit();
                        
                        Intent i = new Intent(CreateProject.this, Project.class);
                        i.putExtra("project_id", p_id);
                        i.putExtra("project_name", p_name);
                        
                        if(fromFreshSignUp==true){
                        	i.putExtra("fromFreshSignUp", true);
                        }
                        
                        startActivity(i);
                        finish();
                    	
                    }
	                });
	                
	                
              
	                adg.show();
				}else{
					if (json.getInt("errorCode")==1){
						Toast.makeText(CreateProject.this, json.getString("message"), Toast.LENGTH_LONG).show();
						pDialog.dismiss();
					}else{
						Toast.makeText(CreateProject.this, json.getString("message"), Toast.LENGTH_LONG).show();
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
