/*
 * Copyright (C) 2013 AChep@xda <artemchep@gmail.com>
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

package com.achep.FifteenPuzzle.widget;

import android.content.Context;

/**
 * Makes work with Toast messages a bit faster
 * 
 * @author achep
 */
public class Toast {

	/**
	 * Make and show a standard toast that just contains a text view from string
	 * resource.
	 */
	public static void show(Context context, int res) {
		makeRes(context, res, android.widget.Toast.LENGTH_SHORT).show();
	}

	/**
	 * Make and show a standard toast that just contains a text view from string
	 * resource.
	 */
	public static void showLong(Context context, int res) {
		makeRes(context, res, android.widget.Toast.LENGTH_LONG).show();
	}

	/**
	 * Make a standard toast that just contains a text view from string
	 * resource.
	 */
	public static android.widget.Toast makeRes(Context context, int res,
			int length) {
		return android.widget.Toast.makeText(context, context.getResources()
				.getString(res), length);
	}
}