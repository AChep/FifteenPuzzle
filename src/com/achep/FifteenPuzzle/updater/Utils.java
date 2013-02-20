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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

public class Utils {

	public static boolean connectedToInternet(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		return netInfo != null && netInfo.isConnectedOrConnecting() ? true
				: false;
	}

	public static File getPathToFolder(String folder) {
		final String cardStatus = Environment.getExternalStorageState();
		if (cardStatus.equals(Environment.MEDIA_MOUNTED)) {
			final File rootDirectory = new File(Environment
					.getExternalStorageDirectory().getPath() + folder);
			if (!rootDirectory.exists())
				rootDirectory.mkdirs();
			return rootDirectory;
		} else
			return null;
	}

	public static String downloadText(Context context, String url) {
		if (!connectedToInternet(context))
			return null;
		try {
			BufferedReader bufferReader = new BufferedReader(
					new InputStreamReader(new URL(url).openStream()));

			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			int i;
			while ((i = bufferReader.read()) != -1) {
				byteArrayOutputStream.write(i);
			}

			bufferReader.close();
			return byteArrayOutputStream.toString();
		} catch (Exception e) {
			return null;
		}
	}

}