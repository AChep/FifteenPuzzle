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

package com.achep.FifteenPuzzle.preferences;

import com.achep.FifteenPuzzle.R;

import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class Settings extends PreferenceActivity implements
		OnPreferenceChangeListener, OnPreferenceClickListener {

	public static final String SHARED_PREFERENCES_FILE = "preferences2";

	public static class Keys {
		public final static String SPREF_PUZZLE_LENGTH = "spref_puzzle_length";

		public final static String PREF_USER_NAME = "pref_user_name";

		public final static String PREF_ABOUT_CURRENT_VERSION = "pref_about_current_version";
		public final static String PREF_ABOUT_FEEDBACK = "pref_about_feedback";
	}

	private EditTextPreference mPrefUserName;
	private Preference mPrefAboutCurrentVersion;
	private Preference mPrefAboutFeedback;

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		addPreferencesFromResource(R.xml.settings);

		mPrefUserName = (EditTextPreference) findPreference(Keys.PREF_USER_NAME);
		mPrefUserName.setOnPreferenceChangeListener(this);
		updateUserNamePreference(mPrefUserName.getText());

		mPrefAboutCurrentVersion = (Preference) findPreference(Keys.PREF_ABOUT_CURRENT_VERSION);
		String versionName = null;
		try {
			versionName = getPackageManager().getPackageInfo(getPackageName(),
					0).versionName;
		} catch (NameNotFoundException e) {
			versionName = "Something went wrong";
		}
		mPrefAboutCurrentVersion.setSummary(getResources().getString(
				R.string.app_name)
				+ " " + versionName);

		mPrefAboutFeedback = (Preference) findPreference(Keys.PREF_ABOUT_FEEDBACK);
		mPrefAboutFeedback.setOnPreferenceClickListener(this);
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

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (preference.equals(mPrefAboutFeedback)) {
			Intent i = new Intent(Intent.ACTION_SEND);
			i.setType("message/rfc822");
			i.putExtra(Intent.EXTRA_EMAIL,
					new String[] { "artemchep@gmail.com" });
			i.putExtra(Intent.EXTRA_SUBJECT, "[APP FEEDBACK] Fifteen Puzzles");
			@SuppressWarnings("deprecation")
			String body = "Device model: " + android.os.Build.MANUFACTURER
					+ " " + android.os.Build.PRODUCT + "\nAndroid version: "
					+ android.os.Build.VERSION.SDK + "\nCPU: "
					+ android.os.Build.CPU_ABI + "\n\nDear developer, ";
			i.putExtra(Intent.EXTRA_TEXT, body);
			try {
				startActivity(Intent.createChooser(i, "Send mail..."));
			} catch (android.content.ActivityNotFoundException ex) {
				Toast.makeText(
						this,
						getResources().getString(
								R.string.settings_other_about_feedback_failed),
						Toast.LENGTH_SHORT).show();
			}
			return true;
		}
		return false;
	}

}