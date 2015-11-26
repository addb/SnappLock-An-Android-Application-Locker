package com.addb.snapplock;

import java.util.Hashtable;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

public class ActivityStartingHandler implements ActivityStartingListener{
	private Context mContext;
	private ActivityManager mAm;
	private String lastRunningPackage;
	private Hashtable<String, Runnable> tempAllowedPackages = new Hashtable<String, Runnable>();
	private Handler handler;
	private String lockScreenActivityName;
	public String appname;
	
	public ActivityStartingHandler(Context context){
		mContext = context;
		handler = new Handler();
		mAm = (ActivityManager)mContext.getSystemService(Context.ACTIVITY_SERVICE);
		lastRunningPackage = getRunningPackage();
		context.registerReceiver(new BroadcastReceiver(){
			@Override
			public void onReceive(Context context, Intent intent) {
				String packagename = intent.getStringExtra(LockScreenActivity.EXTRA_PACKAGE_NAME);
				if (AppLockerPreference.getInstance(mContext).getRelockTimeout() > 0){
					if (tempAllowedPackages.containsKey(packagename)){
						// Extend the time
						Log.d("Detector", "Extending timeout for: " + packagename);
						handler.removeCallbacks(tempAllowedPackages.get(packagename));
					}
					Runnable runnable = new RemoveFromTempRunnable(packagename);
					tempAllowedPackages.put(packagename, runnable);
					handler.postDelayed(runnable, 
							AppLockerPreference.getInstance(mContext).getRelockTimeout() * 1000 * 60);
					log();
				}
				lastRunningPackage = packagename;
			}
		}, new IntentFilter(LockScreenActivity.ACTION_APPLICATION_PASSED));
		
		lockScreenActivityName = ".LockScreenActivity" ;
	}

	private void log(){
		String output = "temp allowed: ";
		for(String p: tempAllowedPackages.keySet()){
			output += p + ", ";
		}
		Log.d("Detector", output);
	}
	
	private class RemoveFromTempRunnable implements Runnable{
		private String mPackageName;
		public RemoveFromTempRunnable(String pname){
			mPackageName = pname;
		}
		public void run() {
			Log.d("Detector", "Lock timeout Expires: " + mPackageName);
			tempAllowedPackages.remove(mPackageName);
		}
	}
	
	private String getRunningPackage(){
		List<RunningTaskInfo> infos = mAm.getRunningTasks(1);
		if (infos.size()<1) return null; 
		RunningTaskInfo info = infos.get(0);
		return info.topActivity.getPackageName();
	}
	
	
	 public void onActivityStarting(String packageName, String activityName) {
	 
		//debug: //debug: log.i("Detector","onActivityStarting");
		synchronized(this){
			
			//Putting the lastRunningPackage up makes applocker's preferences activties
			//not getting locked all the time!
			if (packageName.equals(lastRunningPackage)) return;
			
			
			if (packageName.equals(mContext.getPackageName())){
				// Of course cannot block lock screen
				//debug: //debug: log.i("Detector",lockScreenActivityName);
				if (activityName.equals(lockScreenActivityName)) return;
				// But we need to block preferences
				if(activityName.equals(".ImagePagerActivity")||activityName.equals(".ApplicationListActivity")||activityName.equals(".ImageGridActivity")) return;
				blockActivity(packageName, activityName);
			}
			
			
			
			String[] list = AppLockerPreference.getInstance(mContext).getApplicationList();
			
			if ((AppLockerPreference.getInstance(mContext).getRelockTimeout() > 0))
				if (tempAllowedPackages.containsKey(packageName)) return;
			
			for(int i=0; i<list.length; i++){
				if (list[i].equals(packageName)){
					appname=packageName;
					System.out.println("***"+packageName+"****");
					blockActivity(packageName, activityName);
					return;
				}
			}
			
			lastRunningPackage = packageName;
			System.out.println("----->"+packageName);
		}
	}

	private void blockActivity(String packageName, String activityName) {
		//debug: log.i("Detector", "Blocking: " + packageName);
		// Block!
		
		Intent lockIntent = new Intent(mContext, LockScreenActivity.class);
		lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		lockIntent.putExtra(LockScreenActivity.BlockedActivityName, activityName)
			.putExtra(LockScreenActivity.BlockedPackageName, packageName);
		
		mContext.startActivity(lockIntent);
	}
	
}