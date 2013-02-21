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
import com.achep.FifteenPuzzle.widget.ActionBar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

public class AutoUpdater extends Activity implements OnClickListener {

	private ActionBar mActionBar;
	private ImageView mDownloadButton;
	private ProgressBar mProgressBar;

	private TextView mChangelog;
	private ScrollView mChangelogPanel;

	private String mVersionName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mVersionName = getIntent().getAction();
		if (mVersionName != null) {
			setContentView(R.layout.activity_auto_updater);

			final TextView newVersion = (TextView) findViewById(R.id.version);
			newVersion.setText(mVersionName);

			mActionBar = (ActionBar) findViewById(R.id.action_bar);
			mActionBar.setTitle(getTitle().toString());
			mActionBar.setPopUpPattern(this);

			mDownloadButton = mActionBar.newImageButton(
					R.string.action_bar_download_new_version,
					R.drawable.ic_actionbar_download);
			mDownloadButton.setOnClickListener(this);
			mDownloadButton.setVisibility(View.GONE);

			mProgressBar = mActionBar.newProgressBar();

			mChangelog = (TextView) findViewById(R.id.content);
			mChangelogPanel = (ScrollView) findViewById(R.id.content_panel);

			// Time cap
			mChangelog.setText("Function isn't availabel yet.");
			mChangelogPanel.setVisibility(View.VISIBLE);
			mChangelogPanel.startAnimation(AnimationUtils.loadAnimation(this,
					R.anim.widget_list_view_panel_in));

			mProgressBar.setVisibility(View.GONE);
			mDownloadButton.setVisibility(View.VISIBLE);
		} else {
			finish();
		}
	}

	@Override
	public void onClick(View v) {
		if (v.equals(mDownloadButton)) {
			startService(new Intent(this, DownloadService.class)
					.setAction(mVersionName));
		}
	}

}