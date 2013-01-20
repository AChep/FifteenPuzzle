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

package com.achep.FifteenPuzzle;

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

	public static int alignToRange(int value, int min, int max) {
		return value < min ? min : value > max ? max : value;
	}

	public static long div(long a, int b) {
		return (a - a % b) / b;
	};

	public static String fixTwoZero(long x) {
		return x < 10 ? "0" + x : Long.toString(x);
	}

	public static String fixThreeZero(long x) {
		return x < 10 ? "00" + x : x < 100 ? "0" + x : Long.toString(x);
	}

	public static String getFormatedTime(long s) {
		return Utils.fixTwoZero(Utils.div(s, 60) % 60) + ":"
				+ Utils.fixTwoZero(s % 60);
	}

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

class Coords {

	/**
	 * Output array requires length >= 2x
	 */
	public static void convertLineTo2d(int coordsLine, int length,
			int[] coords2d) {
		coords2d[0] = coordsLine % length;
		coords2d[1] = (coordsLine - coords2d[0]) / length;
	}

	public static int convert2dToLine(int[] coords2d, int length) {
		return coords2d[1] * length + coords2d[0];
	}

	/**
	 * Output array requires length >= 1x
	 */
	public static void convertRealTo2d(int[] coordsReal, int chipSize,
			int[] coords2d) {
		final int halfChipSize = chipSize / 2;
		chipSize++;
		coords2d[0] = (coordsReal[0] - halfChipSize) / chipSize;
		coords2d[1] = (coordsReal[1] - halfChipSize) / chipSize;
	}

	/**
	 * Output array requires length >= 1x
	 */
	public static void convert2dToReal(int[] coords2d, int chipSize,
			int[] coordsReal) {
		final int halfChipSize = chipSize / 2;
		coordsReal[0] = halfChipSize + coords2d[0] * chipSize + coords2d[0];
		coordsReal[1] = halfChipSize + coords2d[1] * chipSize + coords2d[1];
	}

	/**
	 * Output array requires length >= 2x
	 */
	public static void convertLineToReal(int coordsLine, int length,
			int chipSize, int[] coordsReal) {
		convertLineTo2d(coordsLine, length, coordsReal);
		convert2dToReal(coordsReal, chipSize, coordsReal);
	}

	/**
	 * Output array requires length >= 0.5x
	 */
	public static int convertRealToLine(int[] coordsReal, int length,
			int chipSize) {
		final int[] coords2d = new int[2];
		convertRealTo2d(coordsReal, chipSize, coords2d);
		return convert2dToLine(coords2d, length);
	}

	/**
	 * Output array requires length >= 1x
	 */
	public static void roundRealTo2d(int[] coords, int chipSize, int[] coords2d) {
		final int halfChipSize = chipSize / 2;
		coords2d[0] = coords[0] + halfChipSize;
		coords2d[1] = coords[1] + halfChipSize;
		convertRealTo2d(coords2d, chipSize, coords2d);
	}
}