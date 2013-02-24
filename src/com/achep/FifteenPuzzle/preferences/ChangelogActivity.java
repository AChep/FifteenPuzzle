/*
 * Copyright (C) 2012-2013 AChep@xda
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

package com.achep.FifteenPuzzle.preferences;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.achep.FifteenPuzzle.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ChangelogActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_changelog);

		// Load changelog from raw resource
		InputStream inputStream = getResources().openRawResource(
				R.raw.changelog);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		int i;
		try {
			i = inputStream.read();
			while (i != -1) {
				byteArrayOutputStream.write(i);
				i = inputStream.read();
			}
			inputStream.close();
		} catch (IOException e) {
			finish();
			return;
		}

		// Set to textview
		TextView changelog = (TextView) findViewById(R.id.changelog);
		changelog.setText(byteArrayOutputStream.toString());
	}
}