package com.addb.snapplock;

import gueei.binding.Binder;

import gueei.binding.Command;
import gueei.binding.Observable;
import gueei.binding.observables.BooleanObservable;
import gueei.binding.observables.StringObservable;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import javax.mail.Multipart;

//import com.addb.snapplock.ApplicationListActivity.LoadApplicationTask;

import com.addb.snapplock.R;
import com.addb.snapplock.R.layout;
import com.addb.snapplock.CameraPreview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.telephony.gsm.*;

import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.location.Location;
import android.media.ExifInterface;
import android.media.MediaPlayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

@SuppressLint({ "InlinedApi", "SdCardPath" })
public class LockScreenActivity extends Activity {
	public static final String BlockedPackageName = "locked package name";
	public static final String BlockedActivityName = "locked activity name";
	public static final String ACTION_APPLICATION_PASSED = "com.addb.snapplock.applicationpassedtest";
	public static final String EXTRA_PACKAGE_NAME = "com.addb.snapplock.extra.package.name";
	protected static final String TAG = "SnappLock";
	private Camera mCamera;
	
	private SurfaceHolder mHolder;
	private CameraPreview mPreview;
	boolean frontCam;
	ProgressDialog loading;
	 MediaPlayer mp ;
	 int trial=0;
	 
	 int notificationID=1;
	 Drawable d;
	 String appname;
	 
		//int last; //to show last accessed time
	// SmsManager smsManager = SmsManager.getDefault();
	// frontCam=pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
	private static File getOutputMediaFile()  {

		File mediaStorageDir = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"SnAppLock");
		
		
		// return new File(sdDir, "CameraAPIDemo");
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d(TAG, "failed to create directory");
				 File noMedia = new File(mediaStorageDir.getAbsolutePath() + "/.nomedia");
			        noMedia.mkdirs();
			        try {
						noMedia.createNewFile();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				return null;
			}
		}

		// Create a media file name
       
    
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
				.format(new Date());
		File mediaFile;
		mediaFile = new File(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		String pic=(mediaStorageDir.getPath() + File.separator
				+ "IMG_" + timeStamp + ".jpg");
		return mediaFile;
	}
		
	final PictureCallback mPicture = new PictureCallback() {
		
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			if(frontCam)
			{
			camera.startPreview();
			File pictureFile = getOutputMediaFile();
			if (pictureFile == null) {

				return;
			}
			

			try {
				
				FileOutputStream fos = new FileOutputStream(pictureFile);
				fos.write(data);
				fos.close();
				//Toast.makeText(getApplicationContext(), "image saved",Toast.LENGTH_SHORT).show();

			} catch (FileNotFoundException e) {
				Log.d(TAG, "File not found: " + e.getMessage());
				Toast.makeText(getApplicationContext(), "Picture not saved",
						Toast.LENGTH_SHORT).show();
			} catch (IOException e) {
				Log.d(TAG, "Error accessing file: " + e.getMessage());
				Toast.makeText(getApplicationContext(), "Picture not saved",
						Toast.LENGTH_SHORT).show();
			} finally {
				
				if(frontCam)
				camera.startPreview();
			}
		}//frontcam if end
		}
	};

	public static Camera getCameraInstance() {
		
		Camera c = null;
		try {
			c = Camera.open(1);
		
			c.setDisplayOrientation(90);
			c.startPreview();
			// attempt to get a Camera instance
		} catch (Exception e) {
			// Camera is not available (in use or does not exist)
		}
		return c; // returns null if camera is unavailable
	}

	// To keep track of activity's window focus
	boolean currentFocus;

	// To keep track of activity's foreground/background status
	boolean isPaused;

	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mPreview = new CameraPreview(this, mCamera);
		
		 StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		 StrictMode.setThreadPolicy(policy); 
		 //immersive mode
		 

		 if(Build.VERSION.SDK_INT >= 11)
		 {
			 System.out.println("inside 11 if ***********");
		 int mUIFlag = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                /* | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                 | View.SYSTEM_UI_FLAG_LOW_PROFILE
                 | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                 | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;*/

     getWindow().getDecorView().setSystemUiVisibility(mUIFlag);
     getActionBar().hide();
		 }
		 //end immersive
     PackageManager pm = this.getPackageManager();
	 frontCam=pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT);
	 
		
		
		 
			//Wallpaper.set(WallpaperManager.getInstance(this).getFastDrawable());
			Binder.setAndBindContentView(this, R.layout.lockscreen, this);
			if(frontCam)
			camInst();
			
			  try
			     {
				  Intent i=getIntent();
			     System.out.println("Itent");
			     appname = i.getStringExtra(BlockedPackageName);
			     System.out.println(appname);
			    
			        d = getPackageManager().getApplicationIcon(appname);
			         System.out.println("set icon");
			         ImageView icon_View=(ImageView) this.findViewById(R.id.icon_View);
			         icon_View.setBackgroundDrawable(d);
			         
			         ApplicationInfo ai = getPackageManager().getApplicationInfo(appname, 0);

			         String n=ai.loadLabel(getPackageManager()).toString();
			        // String name = (String) packageManager.getApplicationLabel(ai);
			         TextView app_name=(TextView) this.findViewById(R.id.app_name);
			         app_name.setText(n);
			  System.out.println(n);
			     }
			     catch (PackageManager.NameNotFoundException e)
			     {
			         return;
			     }
		
			     
		        	   
		
		
		
	
		
		/*if (Build.VERSION.SDK_INT < 16) {
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
					WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}*/
		
		
	}
	public void  camInst()
		{
		
		// Create an instance of Camera
		if(frontCam)
		{
		mCamera = getCameraInstance();
		 
		/*if(mCamera == null)*/{  
		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.addView(mPreview);
		System.out.println("Cam initialized");
		}
		/*else {
            System.out.println("Camera failed");
             }*/
		}
	}

	public final Observable<Drawable> Wallpaper = new Observable<Drawable>(
			Drawable.class);
	public final Command Number = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			if ((args.length < 1) || !(args[0] instanceof Integer))
				return;
			Integer number = (Integer) args[0];
			Password.set(Password.get() + number);
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			 v.vibrate(60);
			 if(AppLockerPreference.getInstance(LockScreenActivity.this).mAutoVerifyEnabled){
			 String one=Password.get(),two=AppLockerPreference.getInstance(getApplicationContext()).getPassword();
			 
			 if(one.length()==two.length())
			 if (verifyPassword()) {
					Passed.set(true);
					test_passed();
					camRelease();
					 
				}
			 }//if autoverifyenabled end
		}
	};
	
	public void camRelease()
	{
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			System.out.println("cam RELEASED!!");
		}
		
	}
	public final Command Clear = new Command() {
		@Override
		public void Invoke(View view, Object... args) {
			String pass=Password.get(),setpass="";
			char c[]=pass.toCharArray();
			int l=c.length;
			
			for(int i=0;i<l-1;i++)
			{
				setpass=setpass+c[i];
			}
			Password.set(setpass);
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			 v.vibrate(60);
			
			 
		}
	};
	public final Command Verify1 = new Command() {

		@Override
		public void Invoke(View view, Object... args) {
			if (verifyPassword()) {
				Passed.set(true);
				if (mCamera != null) {
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				}
				test_passed();
				 
			}}				


			};
	
	public final Command Verify = new Command() {

		@Override
		public void Invoke(View view, Object... args) {
			if (verifyPassword()) {
				Passed.set(true);
				if (mCamera != null) {
					mCamera.stopPreview();
					mCamera.release();
					mCamera = null;
				}
				test_passed();
				 
				 // Vibrate for 500 milliseconds
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				 v.vibrate(200);
				trial=0;
				  
				

			} else {
				// if password wrong more than 2 times 
				trial++;
				//
				if(trial>=1)
				{ 
					Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					 v.vibrate(600);
				Notification notf = new NotificationCompat.Builder(LockScreenActivity.this)
		        .setAutoCancel(false)
		     
		         // .setLights(0xffff00, 1000, 100)        
		        // .setLights(0xff0000, 5000, 100)
		          .setLights(0xff0000, 1000, 1000)
		          .build();

		    NotificationManager mNotificationManager =  (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
		    mNotificationManager.notify(2, notf); 
		    
		    //showNotification();
				}
				//
				if(trial>=3)
					
					{
					if(AppLockerPreference.getInstance(LockScreenActivity.this).mAlertEnabled)
					{
						mp = MediaPlayer.create(LockScreenActivity.this, R.raw.alert);
				mp.start();}
					//--alert
					
					
					
				if(AppLockerPreference.getInstance(LockScreenActivity.this).mSmsServiceEnabled){
				 try {
                     SmsManager smsManager = SmsManager.getDefault();
                   
                     
                     //lng=v.lng1();
                     smsManager.sendTextMessage(AppLockerPreference.getInstance(LockScreenActivity.this).getPhoneNo(), null,AppLockerPreference.getInstance(LockScreenActivity.this).getMessage(), null, null);
                     Toast.makeText(getApplicationContext(), "SMS sent.",
                     Toast.LENGTH_LONG).show();
                  } catch(Exception e) {
                     Toast.makeText(getApplicationContext(),
                     "SMS failed, please try again.",
                     Toast.LENGTH_LONG).show();
                     e.printStackTrace();
                  }
					}
					}//end if
				//----------------------
				
				//reset password field
				Passed.set(false);
				Password.set("");
				//------------------
				
				
				
				try {
					if(frontCam)
					{
					
					mCamera.setDisplayOrientation(0);
					Camera.Parameters params=mCamera.getParameters();
					
					//params.setRotation(-90);
					if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
			        {   
						params.set("orientation", "portrait");
			            params.set("rotation",270);
			        }
					mCamera.setParameters(params);
					mCamera.takePicture(null, null, mPicture);
					System.out.println("CLICKED!!");
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				
				
				Toast.makeText(getApplicationContext(),
						"Wrong Password! Try Again", Toast.LENGTH_SHORT)
						.show();
				
				
			
			}//no of trials for email
		}
	};
	
	
	public final BooleanObservable Passed = new BooleanObservable(false);

	private void test_passed() {
		this.sendBroadcast(new Intent().setAction(ACTION_APPLICATION_PASSED)
				.putExtra(EXTRA_PACKAGE_NAME,
						getIntent().getStringExtra(BlockedPackageName)));
		
		finish();
	}

	public boolean verifyPassword() {
		if (Password.get() == null)
			return false;
		return Password.get().equals(
				AppLockerPreference.getInstance(this).getPassword());
	}

	public final StringObservable Password = new StringObservable("");

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
				.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
		
		onPause();
		finish();
	}

	protected void onPause() {
		super.onPause();
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		}
	//	mp.release();
	}

	/*protected void onResume() {
		super.onResume();
		if (mCamera != null) {
			try {
				mCamera.reconnect();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mCamera.startPreview();

		}

	}*/
	void showNotification()
	{
		Intent i=new Intent(this,LockScreenActivity.class);
		i.putExtra("notificationID", notificationID);
		
		PendingIntent pendingintent = PendingIntent.getActivity(this, 0, i, 0);
		
		NotificationManager nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		Notification notify=new Notification(R.drawable.snapplocklogo,
				"Intrusion Detection",
				System.currentTimeMillis());
		
		CharSequence from="SnappLock";
		CharSequence message="Unauthorized access Detected";
		notify.setLatestEventInfo(this, from, message, pendingintent);
		
		notify.vibrate=new long[] { 100, 250, 100, 500};
		nm.notify(notificationID,notify);
		
		
		
	}
	
	
	
}
