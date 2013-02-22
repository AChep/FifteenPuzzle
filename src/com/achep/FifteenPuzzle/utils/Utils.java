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

package com.achep.FifteenPuzzle.utils;

public class Utils {

	/**
	 * Aligns value to provided range
	 */
	public static int mathAlignToRange(int value, int min, int max) {
		return value < min ? min : value > max ? max : value;
	}

	public static long mathDiv(long a, int b) {
		return (a - a % b) / b;
	};

	/**
	 * Returns length between two points
	 */
	public static double mathPifagor(float x, float y, float x2, float y2) {
		return Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2));
	}

	private static String timeFixTwoZero(long x) {
		return x < 10 ? "0" + x : Long.toString(x);
	}

	public static String timeGetFormatedTimeFromSeconds(long s) {
		return Utils.timeFixTwoZero(Utils.mathDiv(s, 60) % 60) + ":"
				+ Utils.timeFixTwoZero(s % 60);
	}

	public static String timeGetFormatedTimeFromMillis(long s) {
		return timeGetFormatedTimeFromSeconds(mathDiv(s, 1000));
	}

}