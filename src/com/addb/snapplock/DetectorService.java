

package com.addb.snapplock;
import java.io.BufferedReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.regex.Pattern;

import com.addb.snapplock.R;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.IBinder;

public class DetectorService extends Service {
	public static final String ACTION_DETECTOR_SERVICE = "com.addb.detector.service";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	private static final Class<?>[] mStartForegroundSignature = new Class[] {
        int.class, Notification.class};
    private static final Class<?>[] mStopForegroundSignature = new Class[] {
        boolean.class};
    
    private NotificationManager mNM;
    private Method mStartForeground;
    private Method mStopForeground;
    private Object[] mStartForegroundArgs = new Object[2];
    private Object[] mStopForegroundArgs = new Object[1];
    
    /**
     * This is a wrapper around the new startForeground method, using the older
     * APIs if it is not available.
     */
    void startForegroundCompat(int id, Notification notification) {
        // If we have the new startForeground API, then use it.
        if (mStartForeground != null) {
            mStartForegroundArgs[0] = Integer.valueOf(id);
            mStartForegroundArgs[1] = notification;
            try {
                mStartForeground.invoke(this, mStartForegroundArgs);
            } catch (InvocationTargetException e) {
                // Should not happen.
                //debug: log.w("Detector", "Unable to invoke startForeground", e);
            } catch (IllegalAccessException e) {
                // Should not happen.
                //debug: log.w("Detector", "Unable to invoke startForeground", e);
            }
            return;
        }
        
        // Fall back on the old API.
        stopForeground(true);
        mNM.notify(id, notification);
    }
    
    /**
     * This is a wrapper around the new stopForeground method, using the older
     * APIs if it is not available.
     */
    void stopForegroundCompat(int id) {
        // If we have the new stopForeground API, then use it.
        if (mStopForeground != null) {
            mStopForegroundArgs[0] = Boolean.TRUE;
            try {
                mStopForeground.invoke(this, mStopForegroundArgs);
            } catch (InvocationTargetException e) {
                // Should not happen.
                //debug: log.w("Detector", "Unable to invoke stopForeground", e);
            } catch (IllegalAccessException e) {
                // Should not happen.
                //debug: log.w("Detector", "Unable to invoke stopForeground", e);
            }
            return;
        }
        
        // Fall back on the old API.  Note to cancel BEFORE changing the
        // foreground state, since we could be killed at that point.
        mNM.cancel(id);
        stopForeground(false);
    }
	
    @Override
    public void onCreate() {
    	//debug: log.i("Detector","Service.Oncreate");
    	initConstant();
        mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        try {
            mStartForeground = getClass().getMethod("startForeground",
                    mStartForegroundSignature);
            mStopForeground = getClass().getMethod("stopForeground",
                    mStopForegroundSignature);
        } catch (NoSuchMethodException e) {
            // Running on an older platform.
            mStartForeground = mStopForeground = null;
        }
    }
    
	@Override
    public void onDestroy() {
		//debug: log.i("Detector","Service.Ondestroy");
    	mThread.interrupt();
    	
        // Make sure our notification is gone.
        stopForegroundCompat(R.string.service_running);
    }
    
 // This is the old onStart method that will be called on the pre-2.0
    // platform.  On 2.0 or later we override onStartCommand() so this
    // method will not be called.
    @Override
    public void onStart(Intent intent, int startId) {
    	//debug: log.i("Detector","Service.Onstart");
        handleCommand(intent);
        
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
    	//debug: log.i("Detector","Service.OnStartCommand");
        handleCommand(intent);
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return Service.START_STICKY;
    }
    
    private void handleCommand(Intent intent){
    	// In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.service_running_2);
        CharSequence text1 = getText(R.string.service_running);
        // Set the icon, scrolling text and timestamp
        @SuppressWarnings("deprecation")
		Notification notification = new Notification(R.drawable.notify, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, AppLockerActivity.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this,text ,
                       text1, contentIntent);
        
        startForegroundCompat(R.string.service_running, notification);
        
        startMonitorThread((ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE));
    }
    
    private void startMonitorThread(final ActivityManager am){
    	if (mThread!=null)
    		mThread.interrupt();
    	
    	mThread = new MonitorlogThread(new ActivityStartingHandler(this));
    	mThread.start();
    }
    
    private static Thread mThread;
    private static boolean constantInited = false;
    private static Pattern ActivityNamePattern; 
    private static String logCatCommand;
    private static String ClearlogCatCommand;
    
    private void initConstant() {
    	//debug: log.i("Detector","Service.OninitConstant");
    	if (constantInited) return;
    	String pattern = getResources().getString(R.string.activity_name_pattern);
    	//debug: log.d("Detector", "pattern: " + pattern);
    	ActivityNamePattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
    	logCatCommand = getResources().getString(R.string.logcat_command);
    	ClearlogCatCommand = getResources().getString(R.string.logcat_clear_command);
    }


    
    private class MonitorlogThread extends Thread{
    	
    	ActivityStartingListener mListener;
    	public MonitorlogThread(ActivityStartingListener listener){
    		//debug: log.i("Detector","Monitor//debug: logThread");
    		mListener = listener;
    	}
    	
    	BufferedReader br;
    	
		@Override
		public void run() {
			
			//debug: log.i("Detector","RUN!");
			
			while(!this.isInterrupted() ){
				
				try {
		            Thread.sleep(100);
		            ////debug: log.i("Detector","try!");
		            //This is the code I use in my service to identify the current foreground application, its really easy:

		      		ActivityManager am = (ActivityManager) getBaseContext().getSystemService(ACTIVITY_SERVICE);
		      		// The first in the list of RunningTasks is always the foreground task.
		      		RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
		      		
		      		//Thats it, then you can easily access details of the foreground app/activity:
		      		String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
		      		PackageManager pm = getBaseContext().getPackageManager();
		      		PackageInfo foregroundAppPackageInfo = null;
		  			String foregroundTaskAppName = null;
		  			String foregroundTaskActivityName = foregroundTaskInfo.topActivity.getShortClassName().toString();
		      		try {
		  				foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
		  				foregroundTaskAppName = foregroundAppPackageInfo.applicationInfo.loadLabel(pm).toString();
		  				
		  				//debug: log.i("Detector",foregroundTaskAppName);
		  			} catch (NameNotFoundException e) {
		  				// TODO Auto-generated catch block
		  				e.printStackTrace();
		  			}
		  			
		  			if (mListener!=null){
		  				//mListener.onActivityStarting(foregroundAppPackageInfo.packageName,foregroundTaskAppName);
		  				mListener.onActivityStarting(foregroundAppPackageInfo.packageName,foregroundTaskActivityName);
		  			}
		              
		              
		          } catch (InterruptedException e) {
		              // good practice
		              Thread.currentThread().interrupt();
		              return;
		          }
			}
			
		}
    }  
}
