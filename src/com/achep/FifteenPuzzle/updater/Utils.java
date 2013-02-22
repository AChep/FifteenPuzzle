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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;

import com.achep.FifteenPuzzle.utils.Project;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;

/**
 * Internet and I/O utilities.
 * 
 * @author achep
 * 
 */
public class Utils {

	/**
	 * Returns file with project's folder (Path from Project.DATA_FOLDER).
	 */
	public static File getProjectFolder() {
		String cardStatus = Environment.getExternalStorageState();
		if (cardStatus.equals(Environment.MEDIA_MOUNTED)) {
			final File rootDirectory = new File(Environment
					.getExternalStorageDirectory().getPath()
					+ Project.DATA_FOLDER);
			if (!rootDirectory.exists())
				rootDirectory.mkdirs();
			return rootDirectory;
		} else
			return null;
	}

	/**
	 * Installs / updates application
	 */
	public static void installApplication(Context context, File app) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(app),
				"application/vnd.android.package-archive");
		context.startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
	}

	/**
	 * Returns TRUE of you're connected to Internet and FALSE else.
	 */
	public static boolean isConnectedToInternet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting() ? true
				: false;
	}

	/**
	 * Downloads text by providing URL address
	 * 
	 * @return String text or Null if failed.
	 */
	public static String internetDownloadText(Context context, String url) {
		if (!isConnectedToInternet(context))
			return null;
		try {
			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(new URL(url).openStream()));

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			for (int i; (i = bufferReader.read()) != -1;)
				byteArrayOutputStream.write(i);

			bufferReader.close();
			return byteArrayOutputStream.toString();
		} catch (Exception e) {
			return null;
		}
	}

}