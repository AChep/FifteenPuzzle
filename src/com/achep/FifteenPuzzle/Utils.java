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
}

class Coords {

	/**
	 * Output array requires length >= 2x
	 */
	public static void convertLineTo2d(int[] coordsLine, int length,
			int[] coords2d) {
		for (int i = 0; i < coordsLine.length; i++) {
			coords2d[i * 2] = coordsLine[i] % length;
			coords2d[i * 2 + 1] = (int) Utils.div(coordsLine[i], length);
		}
	}

	/**
	 * Output array requires length >= 0.5x
	 */
	public static void convert2dToLine(int[] coords2d, int length,
			int[] coordsLine) {
		for (int i = 0; i < coordsLine.length; i++) {
			coordsLine[i] = coords2d[i * 2 + 1] * length + coords2d[i * 2];
		}
	}

	/**
	 * Output array requires length >= 1x
	 */
	public static void convertRealTo2d(int[] coordsReal, int chipSize,
			int[] coords2d) {
		for (int i = 0; i < coordsReal.length; i++) {
			coords2d[i] = (coordsReal[i] - chipSize / 2) / (chipSize + 1);
		}
	}

	/**
	 * Output array requires length >= 1x
	 */
	public static void convert2dToReal(int[] coords2d, int chipSize,
			int[] coordsReal) {
		for (int i = 0; i < coords2d.length; i++) {
			coordsReal[i] = chipSize / 2 + coords2d[i] * chipSize + coords2d[i];
		}
	}

	/**
	 * Output array requires length >= 2x
	 */
	public static void convertLineToReal(int[] coordsLine, int length,
			int chipSize, int[] coordsReal) {
		convertLineTo2d(coordsLine, length, coordsReal);
		convert2dToReal(coordsReal, chipSize, coordsReal);
	}

	/**
	 * Output array requires length >= 0.5x
	 */
	public static void convertRealToLine(int[] coordsReal, int length,
			int chipSize, int[] coordsLine) {
		convertRealTo2d(coordsReal, chipSize, coordsReal);
		convert2dToLine(coordsReal, length, coordsLine);
	}

	/**
	 * Output array requires length >= 1x
	 */
	public static void roundRealTo2d(int[] coords, int chipSize,
			int[] coords2d) {
		for (int i = 0; i < coords.length; i++) {
			coords2d[i] = coords[i] + chipSize / 2;
		}
		convertRealTo2d(coords2d, chipSize, coords2d);
	}
}