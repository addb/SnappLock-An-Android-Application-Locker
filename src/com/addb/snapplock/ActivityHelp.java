package com.addb.snapplock;


import com.addb.snapplock.R;


import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.bdajeprbeb.pkovhhlszx212366.AdListener;  //Add import statements
import com.bdajeprbeb.pkovhhlszx212366.AdListener.AdType;
import com.bdajeprbeb.pkovhhlszx212366.AdListener.MraidAdListener;
import com.bdajeprbeb.pkovhhlszx212366.AdView;
import com.bdajeprbeb.pkovhhlszx212366.MA;

public class ActivityHelp extends Activity{
	

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help_activity);
		WebView webView = (WebView)findViewById(R.id.help);
		webView.loadUrl("file:///android_asset/help.html");
		webView.getSettings().setBuiltInZoomControls(false);
		
		 
	    

	}




}
