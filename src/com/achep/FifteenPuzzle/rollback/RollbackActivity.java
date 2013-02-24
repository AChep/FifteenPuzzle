/*
 * Copyright (C) 2012-2013 AChep@xda <artemchep@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.achep.FifteenPuzzle.rollback;

import java.io.File;

import com.achep.FifteenPuzzle.R;
import com.achep.FifteenPuzzle.updater.Utils;
import com.achep.FifteenPuzzle.widget.Toast;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RollbackActivity extends ListActivity {

	private File[] mApps;
	private boolean[] mDamagedApps;

	private String mDamagedAppLabel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Get project's folder
		File root = Utils.getProjectFolder();
		if (root == null) {
			Toast.showLong(this, R.string.rollback_error_project_folder);
			finish();
			return;
		}

		// Get all files
		mApps = root.listFiles();
		if (mApps.length == 0) {
			Toast.showLong(this, R.string.rollback_error_project_folder_empty);
			finish();
			return;
		}
		mDamagedApps = new boolean[mApps.length];

		mDamagedAppLabel = getResources().getString(
				R.string.rollback_error_damaged_file);
		String appName = getResources().getString(R.string.app_name);

		String[] appsNames = new String[mApps.length];
		for (int i = 0; i < appsNames.length; i++) {
			if (mApps[i].isDirectory()) {
				// Directory
				setDamagedApp(i, appsNames);
				continue;
			}
			String name = mApps[i].getName();
			int j = 0, length = name.length();
			for (j = length - 1; j >= 0; j--) {
				if (name.charAt(j) == ' ')
					break;
			}
			if (j == 0) {
				// No space
				setDamagedApp(i, appsNames);
				continue;
			}
			name = name.substring(j, length);
			length = name.length();
			if (!name.substring(length - 4, length).equals(".apk")) {
				// Isn't *.apk
				setDamagedApp(i, appsNames);
				continue;
			}
			appsNames[i] = appName + name.substring(0, length - 4);

		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, appsNames);
		setListAdapter(adapter);
	}

	private void setDamagedApp(int i, String[] appsNames) {
		appsNames[i] = mDamagedAppLabel;
		mDamagedApps[i] = true;
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (!mDamagedApps[position])
			Utils.installApplication(this, mApps[position]);
	}
}