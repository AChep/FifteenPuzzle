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

package com.achep.FifteenPuzzle.updater;

import com.achep.FifteenPuzzle.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class AutoUpdater extends Activity implements OnClickListener {

	private ImageView mDownloadButton;
	private RelativeLayout mBackButton;
	private TextView mChangelog;
	private ProgressBar mProgressBar;

	private String mVersionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mVersionName = getIntent().getAction();
		if (mVersionName != null) {
			setContentView(R.layout.activity_auto_updater);

			final TextView newVersion = (TextView) findViewById(R.id.version);
			newVersion.setText(mVersionName);

			mDownloadButton = (ImageView) findViewById(R.id.download);
			mDownloadButton.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					makeToast(R.string.action_bar_download_new_version);
					return true;
				}
			});
			mBackButton = (RelativeLayout) findViewById(R.id.back);
			mChangelog = (TextView) findViewById(R.id.content);
			mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

			// OnClickListeners
			mDownloadButton.setOnClickListener(this);
			mBackButton.setOnClickListener(this);

			// Time cap
			mChangelog.setText("Function isn't availabel yet.");
			mProgressBar.setVisibility(View.GONE);
			mDownloadButton.setVisibility(View.VISIBLE);
		} else {
			finish();
		}
	}

	private void makeToast(int stringId) {
		Toast.makeText(this, getResources().getString(stringId),
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onClick(View v) {
		if (v == mDownloadButton) {
			startService(new Intent(this, DownloadService.class));
		} else if (v == mBackButton) {
			finish();
		}
	}

}