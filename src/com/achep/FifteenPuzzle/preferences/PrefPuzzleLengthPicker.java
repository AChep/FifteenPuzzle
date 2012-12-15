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

package com.achep.FifteenPuzzle.preferences;

import android.util.AttributeSet;
import android.content.Context;

public class PrefPuzzleLengthPicker extends PrefNumberPickerBase {
	private static final int MAX = 15;
	private static final int MIN = 2;
	public static final int DEFAULT = 4;

	public PrefPuzzleLengthPicker(Context context, AttributeSet attrs) {
		super(context, attrs, Settings.Keys.SPREF_PUZZLE_LENGTH, MAX, MIN,
				DEFAULT);
	}
}