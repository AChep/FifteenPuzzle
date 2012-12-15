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

import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.content.Context;
import android.content.SharedPreferences;

public class PrefNumberPickerBase extends DialogPreference implements
		OnSeekBarChangeListener {

	private int mValue;
	private SeekBar mSeekBar;
	private TextView mTextView;

	private final SharedPreferences sharedPrefs;
	private final String valueKey;
	private final int max;
	private final int min;
	private final int defaultValue;

	public PrefNumberPickerBase(Context context, AttributeSet attrs,
			String valueKey, int max, int min, int defaultValue) {
		super(context, attrs);
		sharedPrefs = context.getSharedPreferences("preferences2", 0);
		this.valueKey = valueKey;
		this.max = max;
		this.min = min;
		this.defaultValue = defaultValue - min;
		mValue = sharedPrefs.getInt(valueKey, defaultValue) - min;
	}

	@Override
	protected View onCreateDialogView() {
		View view = ((LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.preference_number_picker_base, null);

		mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
		mSeekBar.setMax(max - min);
		mSeekBar.setProgress(mValue);
		mSeekBar.setOnSeekBarChangeListener(this);

		mTextView = (TextView) view.findViewById(R.id.value);
		mTextView.setText(mValue + min + "");

		TextView resetButton = (TextView) view.findViewById(R.id.reset);
		resetButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onProgressChanged(mSeekBar, defaultValue, false);
				mSeekBar.setProgress(defaultValue);
			}
		});

		TextView textMaxLabel = (TextView) view.findViewById(R.id.max);
		textMaxLabel.setText(max + "");
		TextView textMinLabel = (TextView) view.findViewById(R.id.min);
		textMinLabel.setText(min + "");
		return view;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (positiveResult) {
			sharedPrefs.edit().putInt(valueKey, mValue + min).commit();
		}
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		mValue = value;
		mTextView.setText(Integer.toString(mValue + min));
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
	}

}