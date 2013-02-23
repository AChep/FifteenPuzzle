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

		String[] apkNames = new String[mApps.length];
		String errorIsntApp = getResources().getString(
				R.string.rollback_error_damaged_file);
		for (int i = 0; i < apkNames.length; i++) {
			if (mApps[i].isDirectory()) {
				apkNames[i] = errorIsntApp;
				mDamagedApps[i] = true;
				continue;
			}
			apkNames[i] = mApps[i].getName();
			int length = apkNames[i].length();
			if (!apkNames[i].substring(length - 4, length).equals(".apk")) {
				apkNames[i] = errorIsntApp;
				mDamagedApps[i] = true;
				continue;
			}
			apkNames[i] = apkNames[i].substring(0, length - 4);

		}
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, apkNames);
		setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		if (!mDamagedApps[position])
			Utils.installApplication(this, mApps[position]);
	}
}