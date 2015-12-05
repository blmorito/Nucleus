package com.example.nucleus;

import android.app.ActionBar;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class GoalHome extends TabActivity {
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		
		TabHost tabHost = getTabHost();
		
		TabSpec openGoals = tabHost.newTabSpec("Open");
		
		openGoals.setIndicator("Open");
		Intent openGoalsIntent = new Intent(this, GoalOpen.class);
		openGoals.setContent(openGoalsIntent);
		
		TabSpec inProgressGoals = tabHost.newTabSpec("In Progress");
		inProgressGoals.setIndicator("In Progress");
		Intent inProgressGoalsIntent = new Intent(this, GoalInProgress.class);
		inProgressGoals.setContent(inProgressGoalsIntent);
		
		TabSpec doneGoals = tabHost.newTabSpec("Done");
		doneGoals.setIndicator("Done");
		Intent doneGoalsIntent = new Intent(this, GoalDone.class);
		doneGoals.setContent(doneGoalsIntent);
		
		tabHost.addTab(openGoals);
		tabHost.addTab(inProgressGoals);
		tabHost.addTab(doneGoals);
		int x = getIntent().getIntExtra("goToTab", 9);
		if (x==9){
			
		}else{
			tabHost.setCurrentTab(x);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.goalhome_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		
		Activity currentActivity = getCurrentActivity();
		
		switch (item.getItemId()) {
		case R.id.action_goalhome_addgoal:
			startActivity(new Intent(GoalHome.this, GoalNew.class));
			return true;
		case R.id.action_goalhome_refresh:
			if (currentActivity instanceof GoalOpen){
				((GoalOpen)currentActivity).loadOpenGoals();
			}else if (currentActivity instanceof GoalInProgress) {
				((GoalInProgress)currentActivity).loadInProgressGoals();
			}else{
				((GoalDone)currentActivity).loadDoneGoals();
			}
			
			return true;

		default:
			return super.onOptionsItemSelected(item);
		}
		
	}
}
