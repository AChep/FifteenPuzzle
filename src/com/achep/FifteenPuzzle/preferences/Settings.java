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

import com.achep.FifteenPuzzle.R;

import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;

public class Settings extends PreferenceActivity implements
		OnPreferenceChangeListener {

	public static final String SDCARD_FOLDER = "/Android/data/com.achep.FifteenPuzzles/";
	public static final String SHARED_PREFERENCES_FILE = "preferences2";

	public static class Keys {
		public final static String SPREF_PUZZLE_LENGTH = "spref_puzzle_length";
		public final static String PREF_USER_NAME = "pref_user_name";
	}

	private EditTextPreference mPrefUserName;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.settings);

		mPrefUserName = (EditTextPreference) findPreference(Keys.PREF_USER_NAME);
		mPrefUserName.setOnPreferenceChangeListener(this);
		updateUserNamePreference(mPrefUserName.getText());
	}

	@Override
	public boolean onPreferenceChange(Preference pref, Object value) {
		if (pref.equals(mPrefUserName)) {
			updateUserNamePreference((String) value);
			return true;
		}
		return false;
	}

	private void updateUserNamePreference(String text) {
		mPrefUserName.setSummary(getResources().getString(
				R.string.settings_nickname2)
				+ " " + text);
	}

}