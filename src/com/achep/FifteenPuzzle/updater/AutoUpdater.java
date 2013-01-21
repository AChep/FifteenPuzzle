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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.achep.FifteenPuzzle.R;
import com.achep.FifteenPuzzle.Utils;
import com.achep.FifteenPuzzle.preferences.Settings;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
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
	private ProgressDialog mProgressDialog;

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
		} else
			finish();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case 0:
			mProgressDialog = new ProgressDialog(this);
			mProgressDialog.setMessage(getResources().getString(
					R.string.auto_updater_downloading_dialog));
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
			return mProgressDialog;
		default:
			return null;
		}
	}

	@Override
	public void onClick(View v) {
		if (v == mDownloadButton) {
			new AsyncDownloadAndInstall().execute();
		} else if (v == mBackButton) {
			finish();
		}
	}

	private class AsyncDownloadAndInstall extends
			AsyncTask<Void, String, String> {

		private static final String URL_MASTER_APPLICATION = "https://github.com/AChep/FifteenPuzzle/raw/master/bin/FifteenPuzzle.apk";

		@SuppressWarnings("deprecation")
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showDialog(0);

			mProgressDialog.setProgress(0);
		}

		@Override
		protected String doInBackground(Void... void_) {
			Resources res = getResources();
			if (Utils.connectedToInternet(AutoUpdater.this)) {
				int errorType = 0;
				try {
					URL url = new URL(URL_MASTER_APPLICATION);
					URLConnection conexion = url.openConnection();
					conexion.connect();

					errorType++;
					int lenghtOfFile = conexion.getContentLength();
					InputStream input = new BufferedInputStream(
							url.openStream());

					errorType++;
					FileOutputStream output = new FileOutputStream(new File(
							Utils.getPathToFolder(Settings.SDCARD_FOLDER),
							mVersionName + ".apk"));

					errorType++;
					byte data[] = new byte[1024];

					long total = 0;

					int count;
					while ((count = input.read(data)) != -1) {
						total += count;
						publishProgress(""
								+ (int) ((total * 100) / lenghtOfFile));
						output.write(data, 0, count);
					}

					output.flush();
					output.close();
					input.close();

					return null;
				} catch (Exception e) {
					e.printStackTrace();
					return res
							.getString(R.string.auto_updater_downloading_error1
									+ errorType);
				}
			} else
				return res
						.getString(R.string.auto_updater_downloading_error_connection);

		}

		@SuppressWarnings("deprecation")
		@Override
		protected void onPostExecute(String error) {
			dismissDialog(0);
			if (error != null) {
				Toast.makeText(AutoUpdater.this, error, Toast.LENGTH_LONG)
						.show();
			} else {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(Uri.fromFile(new File(Utils
						.getPathToFolder(Settings.SDCARD_FOLDER), mVersionName
						+ ".apk")), "application/vnd.android.package-archive");
				startActivity(intent);
			}
		}
	}

}