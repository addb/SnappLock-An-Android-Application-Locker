/*******************************************************************************
* Copyright 2011-2013 Sergey Tarasevich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.addb.snapplock;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseActivity extends Activity {
	File imageFile;
	String filepath[];
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}


	String[] getImages() {

		File file = null;
		File[] listFile;
		String[] FilePathStrings = null;

		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			Toast.makeText(this, "Error! No SDCARD Found!", Toast.LENGTH_LONG)
					.show();
		} else {
			// Locate the image folder in your SD Card
				file = new File(
					Environment
							.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
					"SnAppLock");
			// Create a new folder if no folder named SnApplock exist
			file.mkdirs();
		}

		if (file.isDirectory() && !file.equals(".nomedia")) {
			listFile = file.listFiles();

			// Create a String array for FilePathStrings
			FilePathStrings = new String[listFile.length];
			
			// Create a String array for FileNameStrings
			// FileNameStrings = new String[listFile.length];

			for (int i = 0; i < listFile.length; i++) {
				// Get the path of the image file

				FilePathStrings[i] = "file://" + listFile[i].getAbsolutePath();
				
				
				// Get the name image file
				// FileNameStrings[i] = listFile[i].getName();
			}
		}
		return FilePathStrings;

	}
	
}
