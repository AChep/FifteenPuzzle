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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class PrefIcon extends Preference {

	private Drawable mIcon;

	public PrefIcon(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public PrefIcon(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setLayoutResource(R.layout.preference_icon);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.PrefIcon, defStyle, 0);
		mIcon = a.getDrawable(R.styleable.PrefIcon_icon);
		a.recycle();
	}

	@Override
	public void onBindView(View view) {
		super.onBindView(view);
		ImageView imageView = (ImageView) view.findViewById(R.id.icon);
		if (imageView != null && mIcon != null) {
			imageView.setImageDrawable(mIcon);
		}
	}

	public void setIcon(Drawable icon) {
		if ((icon == null && mIcon != null)
				|| (icon != null && !icon.equals(mIcon))) {
			mIcon = icon;
			notifyChanged();
		}
	}

	public Drawable getIcon() {
		return mIcon;
	}
}