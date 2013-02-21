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

	public static int alignToRange(int value, int min, int max) {
		return value < min ? min : value > max ? max : value;
	}

	public static long div(long a, int b) {
		return (a - a % b) / b;
	};

	public static double pifagor(float x, float y, float x2, float y2) {
		return Math.sqrt(Math.pow(x - x2, 2) + Math.pow(y - y2, 2));
	}

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

	public static String getFormatedTimeFromMillis(long s) {
		return getFormatedTime(div(s, 1000));
	}

}