package com.addb.snapplock;

import gueei.binding.Binder;


import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.addb.snapplock.R;
import com.addb.snapplock.R.id;
import com.addb.snapplock.R.layout;
import com.addb.snapplock.R.menu;

import android.view.View;
import android.widget.CheckBox;
import android.widget.SearchView;
import android.widget.Switch;
import android.widget.TabHost;


@SuppressWarnings("deprecation")
public class AppLockerActivity extends TabActivity {
   // @Override
 
	 private TabHost mTabHost;

	    static final String KEY_IS_FIRST_TIME =  "com.addb.snapplock.first_time";
	    static final String KEY =  "com.addb.snapplock";
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Binder.setAndBindContentView(this, R.layout.tab, this);
      // getActionBar().setDisplayHomeAsUpEnabled(false);
    if(isFirstTime())
    {
    	Intent i=new Intent(this, ActivityHelp.class);
    	this.startActivity(i);
    	getSharedPreferences(KEY, Context.MODE_PRIVATE).edit().putBoolean(KEY_IS_FIRST_TIME, false).commit();
    }
       
       }
   
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.action_menu, menu);
	    return super.onCreateOptionsMenu(menu);
	    
	    
	   	}
	 public boolean isFirstTime(){
	        return getSharedPreferences(KEY, Context.MODE_PRIVATE).getBoolean(KEY_IS_FIRST_TIME, true);
	        
	        
	    }
		
		@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch(item.getItemId()) {
	    case R.id.action_about:
	    { Intent intent = new Intent(this, AboutActivity.class);
	        this.startActivity(intent);
	        break;}
	   case R.id.action_help:
	    	{Intent intent = new Intent(this, ActivityHelp.class);
	        this.startActivity(intent);
	        // another startActivity, this is for item with id "menu_item2"
	        break;
	    	}
	    default:
	        return super.onOptionsItemSelected(item);
	    }

	    return true;
	}
	




}




