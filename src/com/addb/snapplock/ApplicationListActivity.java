package com.addb.snapplock;

import gueei.binding.Binder;
import gueei.binding.Command;
import gueei.binding.Observable;
import gueei.binding.collections.ArrayListObservable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;



import com.addb.snapplock.R;
import com.addb.snapplock.R.layout;
//import com.google.android.gms.drive.internal.v;

import android.app.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ToggleButton;

public class ApplicationListActivity extends Activity {
	ProgressDialog loading;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loading = ProgressDialog.show(this, "Please wait", "Listing applications installed in your phone... ");
		//loading.show(this, "Please wait", "Listing applications installed in your phone... ");
		  (new LoadApplicationTask()).execute();
		
		  SharedPreferences sharedPrefs = getSharedPreferences("com.addb.snapplock", MODE_PRIVATE);
		   
		 
		
	}

	int n1;

	
	
	private ProgressDialog ProgressDialog(
			ApplicationListActivity applicationListActivity, int customdialog) {
		// TODO Auto-generated method stub
		return null;
	}





	private class LoadApplicationTask extends AsyncTask<Integer, Integer, Integer>{
		private ArrayList<AppItem> items = new ArrayList<AppItem>();
		
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			Collections.sort(items);
			
			Applications.addAll(items);
			
			
			Binder.setAndBindContentView(ApplicationListActivity.this, 
			R.layout.applicationlist, ApplicationListActivity.this);			
			loading.dismiss();
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
	        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);


	
	        List<ResolveInfo> mApps = getPackageManager().queryIntentActivities(mainIntent, 0);
	 
	        String[] list = AppLockerPreference.getInstance(ApplicationListActivity.this).getApplicationList();
	        int len = list.length;
	        
	        int length = mApps.size();
	        for(int i=0; i<length; i++){
	        	
	        	ResolveInfo info = mApps.get(i);
	        	Drawable image = info.loadIcon(getPackageManager());
	        	
	        	boolean included = false;
	        	
	        	for (int j=0; j<len; j++){
	        		if (info.activityInfo.packageName.equals(list[j])){
	        			included = true;
	        			
	        			break;
	        		}
	        	}
	        	
	        	items.add(new AppItem(
	        			info.activityInfo.loadLabel(getPackageManager()).toString(), 
	        			info.activityInfo.name,
	        			info.activityInfo.packageName,
	        			image, included, checkImportance(info.activityInfo.packageName)));
	        }
	        
	        boolean included = false;
	        	
			
        	for (int j=0; j<len; j++){
        		if ("com.android.packageinstaller".equals(list[j])){
        			included = true;
        			

    				break;
        		}
        	}
	        
	        // Add default components 
	        ApplicationInfo info;
			try {
				info = getPackageManager().getApplicationInfo("com.android.packageinstaller", 0);
		        items.add(new AppItem(
		        		info.loadLabel(getPackageManager()).toString(),
		        		info.name,
		        		info.packageName,
		        		info.loadIcon(getPackageManager()),
		        		included, true
		        ));
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        return 0;
		}

		private boolean checkImportance(String packageName) {
			if ("com.android.vending".equals(packageName)||
				"com.android.settings".equals(packageName)||"com.facebook.katana".equals(packageName)
				||"com.facebook.orca".equals(packageName)||"com.whatsapp".equals(packageName)
				||"com.google.android.gallery3d".equals(packageName)||"com.android.gallery3d".equals(packageName)
					){
				return true;
			}
			return false;
		}
	}
	
	public final Command SelectAll = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			for(AppItem app: Applications){
				
				app.Included.set(true);
				
				
			}
			saveToPreference();
		}
	};
	
	public final Command DeselectAll = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			for(AppItem app: Applications){
				app.Included.set(false);
			}
			saveToPreference();
		}
	};
	
	public final Command SelectAllImportant = new Command(){
		@Override
		public void Invoke(View view, Object... args) {
			for(AppItem app: Applications){
				if (app.Important.get())
					app.Included.set(true);
					
				else
					app.Included.set(false);
			}
			saveToPreference();
		}
	};
	
	
	
	
	
	//toggle
	public void onToggleClicked(View view) {
		
	    // Is the toggle on?
	    boolean on = ((ToggleButton) view).isChecked();
	   
	    
	    if (on) {
	    	for(AppItem app: Applications){

				app.Included.set(true);
				
				
				
			}
			saveToPreference();
			 
	    } else {
	    	for(AppItem app: Applications){
				app.Included.set(false);
				
			}
			saveToPreference();
			 
	    }
	}
	//toggle
	public final ArrayListObservable<AppItem> Applications = 
			new ArrayListObservable<AppItem>(AppItem.class);
	
	public class AppItem implements Comparable<AppItem>{
		//protected Object ;
		
		public final StringObservable Label = new StringObservable();
		public final StringObservable Name = new StringObservable();
		public final StringObservable PackageName = new StringObservable();
		public final Observable<Drawable> Icon = new Observable<Drawable>(Drawable.class);
		public final BooleanObservable Included = new BooleanObservable(false);
		public final BooleanObservable Checked = new BooleanObservable(false);
		
		public final BooleanObservable Important = new BooleanObservable(false);
		public AppItem(String label, String name, String packageName, Drawable icon, boolean included, boolean important){
			Label.set(label);
			Name.set(name);
			PackageName.set(packageName);
			Icon.set(icon);
			Included.set(included);
			Important.set(important);
		}
		public final Command ToggleIncluded = new Command(){
			@Override
			
			public void Invoke(View view, Object... args) {
				Included.toggle();
				Checked.toggle();
				
	    	
				saveToPreference();
			}
		};
		public int compareTo(AppItem another) {
			if (Important.get() && !another.Important.get()) return -1;
			if (!Important.get() && another.Important.get()) return 1;
			return Label.get().compareTo(another.Label.get());
		}
	}
	
	private void saveToPreference(){
		ArrayList<String> allowed = new ArrayList<String>();
		for(AppItem app: Applications){
			// Only save included applications
			if (app.Included.get()){
				allowed.add(app.PackageName.get());
			}
		}
		AppLockerPreference.getInstance(this).saveApplicationList(allowed.toArray(new String[0]));
	}
}
