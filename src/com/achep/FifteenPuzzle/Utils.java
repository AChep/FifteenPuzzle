/*
 * Copyright (C) 2012 AChep@xda <artemchep@gmail.com>
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

import java.util.Arrays;

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

	public static int[] findElement(int[] array, int[] value) {
		int[] id = new int[value.length];
		int length = value.length;
		Arrays.fill(id, -1); // cause exception if missed

		// let's find the elements!
		for (int i = 0; i < array.length; i++) {
			for (int j = 0; j < value.length; j++) {
				if (array[i] == value[j]) {
					id[j] = i;

					// Kinda optimization
					length--;
					if (length == 0)
						break;
				}
			}
			if (length == 0)
				break; // Cheers!
		}

		// Okay, happy end
		return id;
	}
}

class Coords {
	public static int[] convertLineTo2d(int[] coordsLine, int length) {
		int[] coords2d = new int[coordsLine.length * 2];
		for (int i = 0; i < coordsLine.length; i++) {
			coords2d[i * 2] = coordsLine[i] % length;
			coords2d[i * 2 + 1] = (int) Utils.div(coordsLine[i], length);
		}
		return coords2d;
	}

	public static int[] convert2dToLine(int[] coords2d, int length) {
		int[] coordsLine = new int[coords2d.length / 2];
		for (int i = 0; i < coordsLine.length; i++) {
			coordsLine[i] = coords2d[i * 2 + 1] * length + coords2d[i * 2];
		}
		return coordsLine;
	}

	public static int[] convertRealTo2d(int[] coordsReal, int chipSize) {
		int[] coords2d = new int[coordsReal.length];
		for (int i = 0; i < coordsReal.length; i++) {
			coords2d[i] = (coordsReal[i] - chipSize / 2) / (chipSize + 1);
		}
		return coords2d;
	}

	public static int[] convert2dToReal(int[] coords2d, int chipSize) {
		int[] coordsReal = new int[coords2d.length];
		for (int i = 0; i < coords2d.length; i++) {
			coordsReal[i] = chipSize / 2 + coords2d[i] * chipSize + coords2d[i];
		}
		return coordsReal;
	}

	public static int[] convertLineToReal(int[] coordsLine, int length,
			int chipSize) {
		return convert2dToReal(convertLineTo2d(coordsLine, length), chipSize);
	}

	public static int[] convertRealToLine(int[] coordsReal, int length,
			int chipSize) {
		return convert2dToLine(convertRealTo2d(coordsReal, chipSize), length);
	}

	public static int[] roundReal(int[] coords, int length, int chipSize) {
		for (int y0 = 0; y0 < length; y0++) {
			for (int x0 = 0; x0 < length; x0++) {
				// Get perfect position
				int[] real = convert2dToReal(new int[] { x0, y0 }, chipSize);
				if (Math.abs(coords[0] - real[0]) < chipSize / 2 + 2
						&& Math.abs(coords[1] - real[1]) < chipSize / 2 + 2) {
					// If that's looks like real one - return
					return new int[] { real[0], real[1] };
				}
			}
		}
		return null;
	}
}