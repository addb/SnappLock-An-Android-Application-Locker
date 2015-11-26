package com.addb.snapplock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.widget.Switch;

public class AppLockerPreference implements OnSharedPreferenceChangeListener {

	
	public boolean isAutoStart() {
		return mAutoStart;
	}
	
	public boolean isServiceEnabled() {
	
		return mServiceEnabled;
	}
	public boolean isSmsServiceEnabled() {
		return mSmsServiceEnabled;
	}
	public boolean isRandomEnabled() {
		return mRandomEnabled;
	}
	public boolean isAlertEnabled() {
		return mAlertEnabled;
	}
	public boolean isAutoVerifyEnabled() {
		return mAlertEnabled;
	}
	public void saveServiceEnabled(boolean serviceEnabled) {
		mServiceEnabled = serviceEnabled;
		mPref.edit().putBoolean(PREF_SERVICE_ENABLED, mServiceEnabled);
	}
	public void saveSmsServiceEnabled(boolean smsservice) {
		mSmsServiceEnabled = smsservice;
		mPref.edit().putBoolean(PREF_SMS_SERVICE, mSmsServiceEnabled);
	}
	public void saveRandomEnabled(boolean random) {
		mRandomEnabled = random;
		mPref.edit().putBoolean(PREF_RANDOM, mRandomEnabled);
	}
	public void saveAlertEnabled(boolean alert) {
		mAlertEnabled = alert;
		mPref.edit().putBoolean(PREF_ALERT, mAlertEnabled);
	}
	public void saveAutoVerifyEnabled(boolean autoverify) {
		
		mAutoVerifyEnabled = autoverify;
		mPref.edit().putBoolean(PREF_ALERT, mAutoVerifyEnabled);
	}
	public String[] getApplicationList() {
		return mApplicationList;
	}
	public void saveApplicationList(String[] applicationList) {
		mApplicationList = applicationList;
		String combined = "";
		for (int i=0; i<mApplicationList.length; i++){
			combined = combined + mApplicationList[i] + ";";
		}
		mPref.edit().putString(PREF_APPLICATION_LIST, combined).commit();
	}

	private static final String PREF_SERVICE_ENABLED = "service_enabled";
	private static final String PREF_APPLICATION_LIST = "application_list";
	private static final String PREF_AUTO_START = "start_service_after_boot";
	private static final String PREF_PASSWORD = "password";
	private static final String PREF_PHONENO = "phoneNo";
	private static final String PREF_MESSAGE = "message";
	private static final String PREF_SMS_SERVICE = "sms_service";
	private static final String PREF_RANDOM = "random";
	private static final String PREF_ALERT = "alert";
	private static final String PREF_AUTO_VERIFY = "autoverify";
	
	/**
	 * Section for singleton pattern
	 */
	private SharedPreferences mPref;
	private AppLockerPreference(Context context) {
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
		mPref.registerOnSharedPreferenceChangeListener(this);
		reloadPreferences();
	}
	private void reloadPreferences() {
		mServiceEnabled = mPref.getBoolean(PREF_SERVICE_ENABLED, false);
		mApplicationList = mPref.getString(PREF_APPLICATION_LIST, "").split(";");
		mAutoStart = mPref.getBoolean(PREF_AUTO_START, false);
		mPassword = mPref.getString(PREF_PASSWORD, "1234");
		mPhoneNo = mPref.getString(PREF_PHONENO,"");
		mMessage = mPref.getString(PREF_MESSAGE,"Intruder Alert ! wrong password typed more than two times!");
		mSmsServiceEnabled = mPref.getBoolean(PREF_SMS_SERVICE, false);
		mRandomEnabled = mPref.getBoolean(PREF_RANDOM, false);
		mAlertEnabled = mPref.getBoolean(PREF_ALERT, false);
		mAutoVerifyEnabled = mPref.getBoolean(PREF_AUTO_VERIFY, false);
		
		
		if (mPref.getBoolean("relock_policy", true)){
			try{
				mRelockTimeout = Integer.parseInt(mPref.getString("relock_timeout", "-1"));
			}catch(Exception e){
				mRelockTimeout = -1;
			}
		}else{
			mRelockTimeout = -1;
		}
	}

	private static AppLockerPreference mInstance;
	public static AppLockerPreference getInstance(Context context){
		return mInstance == null ?
				(mInstance = new AppLockerPreference(context)) :
					mInstance;
	}

	public boolean mServiceEnabled, mAutoStart;public boolean mRandomEnabled, mSmsServiceEnabled,mAlertEnabled,mAutoVerifyEnabled;
	private String[] mApplicationList;
	private String mPassword,mPhoneNo,mMessage;
	private int mRelockTimeout;
	
	
	
	public int getRelockTimeout(){
		return mRelockTimeout;
	}
	
	public String getPassword() {
		return mPassword;
	}
	public String getPhoneNo() {
		return mPhoneNo;
	}
	public String getMessage() {
		return mMessage;
	}
	public void savePassword(String password) {
		mPassword = password;
		mPref.edit().putString(PREF_PASSWORD, password);
	}
	public void savePhoneNo(String phoneNo) {
		mPhoneNo = phoneNo;
		mPref.edit().putString(PREF_PHONENO, phoneNo);
	}
	
	public void saveMessage(String message) {
		mMessage = message;
		mPref.edit().putString(PREF_MESSAGE, message);
	}
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		reloadPreferences();
	}
}
