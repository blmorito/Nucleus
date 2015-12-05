package com.example.nucleus;

import com.example.nucleus.WsShowMembers.NetCheck;

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
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.TabHost.TabSpec;

public class WsShowMembersTLayout extends TabActivity {
	
	Integer ws_user_level_id;
	SharedPreferences prefs;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ws_members_tabs);
		ActionBar bar = getActionBar();
		bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00253a")));
		prefs = getSharedPreferences("MyPref", MODE_PRIVATE);
		
		ws_user_level_id = prefs.getInt("ws_user_level_id", 0);
		
		invalidateOptionsMenu();
		TabHost tabHost = getTabHost();
		
		TabSpec currentMembers = tabHost.newTabSpec("Members");
		
		currentMembers.setIndicator("Current Members");
		Intent currentMembersIntent = new Intent(this, WsShowMembers.class);
		currentMembers.setContent(currentMembersIntent);
		
		TabSpec pendingMembers = tabHost.newTabSpec("Pending");
		pendingMembers.setIndicator("Pending Members");
		Intent pendingMembersIntent = new Intent(this, WsShowPendingMembers.class);
		pendingMembers.setContent(pendingMembersIntent);
		
		tabHost.addTab(currentMembers);
		tabHost.addTab(pendingMembers);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.ws_members_menu, menu);
		return super.onCreateOptionsMenu(menu);
		
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		Activity currentActivity = getCurrentActivity();
		
		switch(item.getItemId()){
		
		case R.id.action_invite_member:
			startActivity(new Intent(WsShowMembersTLayout.this,InviteWorkspace.class));
			return true;
		case R.id.action_refresh_members:
			if (currentActivity instanceof WsShowMembers){
				((WsShowMembers)currentActivity).loadMem();
			}else{
				((WsShowPendingMembers)currentActivity).loadPending();
			}
			
			return true;
		default:
			return super.onOptionsItemSelected(item);
				
		}
		
		
		
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		
//		if(ws_user_level_id==3){
//			menu.findItem(R.id.action_invite_member).setVisible(false);
//			menu.findItem(R.id.action_member_settings).setVisible(false);
//		}else if (ws_user_level_id==2) {
//			menu.findItem(R.id.action_invite_member).setVisible(true);
//			menu.findItem(R.id.action_member_settings).setVisible(false);
//		}else{
//			menu.findItem(R.id.action_invite_member).setVisible(true);
//			menu.findItem(R.id.action_member_settings).setVisible(true);
//		}
		return super.onPrepareOptionsMenu(menu);
	}

}
