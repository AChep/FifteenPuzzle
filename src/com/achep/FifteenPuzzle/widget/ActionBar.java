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

import com.achep.FifteenPuzzle.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ActionBar extends LinearLayout {

	private RelativeLayout mNavigateLayout;
	private TextView mTitleText;
	private LinearLayout mActionsBar;

	private int mHeight;

	public ActionBar(Context context) {
		this(context, null);
	}

	public ActionBar(Context context, AttributeSet attrs) {
		super(context, attrs);

		initialize(context);
	}

	@SuppressLint("NewApi")
	public ActionBar(Context context, AttributeSet attrs, int styles) {
		super(context, attrs, styles);

		initialize(context);
	}

	private void initialize(Context context) {
		mHeight = (int) getResources().getDimension(R.dimen.action_bar_height);

		int horizontalPadding = density(4);
		int verticalPadding = horizontalPadding * 2;

		// Initialize navigating part
		mNavigateLayout = new RelativeLayout(context);
		ImageView appIcon = new ImageView(context);
		appIcon.setImageResource(R.drawable.ic_launcher);
		RelativeLayout.LayoutParams appIconLp = new RelativeLayout.LayoutParams(
				mHeight, LayoutParams.WRAP_CONTENT);
		appIconLp.leftMargin = horizontalPadding;
		appIconLp.topMargin = verticalPadding;
		appIconLp.bottomMargin = verticalPadding;
		appIconLp.addRule(RelativeLayout.CENTER_VERTICAL);
		mNavigateLayout.addView(appIcon, appIconLp);

		// Title text
		mTitleText = new TextView(context);
		mTitleText.setTextAppearance(context,
				android.R.style.TextAppearance_Medium);
		mTitleText.setPadding(0, 0, verticalPadding, 0);
		RelativeLayout.LayoutParams titleTextLp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		titleTextLp.addRule(RelativeLayout.CENTER_VERTICAL);
		titleTextLp.leftMargin = mHeight + horizontalPadding;
		mNavigateLayout.addView(mTitleText, titleTextLp);

		// Add navigating part to main view
		addView(mNavigateLayout, newLayoutParams());

		// Icons and actions
		mActionsBar = new LinearLayout(context);
		mActionsBar.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		mActionsBar.setPadding(0, 0, appIconLp.leftMargin, 0);
		addView(mActionsBar, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
	}

	/**
	 * Sets pop-up android pattern.
	 */
	public void actionBarSetPopUpPattern(final Activity activity) {
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (activity != null)
					activity.finish();
			}
		};

		RelativeLayout.LayoutParams backArrowLp = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		backArrowLp.addRule(RelativeLayout.CENTER_VERTICAL);

		ImageView backArrow = new ImageView(getContext());
		backArrow.setImageResource(R.drawable.ic_actionbar_back);
		backArrow.setLayoutParams(backArrowLp);
		mNavigateLayout.addView(backArrow, 0);
		mNavigateLayout.setOnClickListener(listener);
		mNavigateLayout.setBackgroundResource(R.drawable.image_view_selector);
		mNavigateLayout.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, mHeight));
	}

	/**
	 * Sets title label
	 */
	public void actionBarSetTitle(CharSequence title) {
		mTitleText.setText(title);
	}

	/**
	 * Returns and adds to action bar new ProgressBar. Warning: Don't set
	 * LayoutParams to output view!
	 */
	public ProgressBar actionBarInitAndAddProgressBar() {
		ProgressBar pb = new ProgressBar(getContext());
		mActionsBar.addView(prepareView(pb));
		return pb;
	}

	/**
	 * Returns and adds to action bar new ImageView with toast info and icon.
	 * Warning: Don't set OnLongClick listener and LayoutParams to output view!
	 */
	public ImageView actionBarInitAndAddImageButton(final int stringRes,
			final int imageRes) {
		ImageView imageView = new ImageView(getContext());

		imageView.setImageResource(imageRes);
		imageView.setScaleType(ScaleType.CENTER);
		imageView.setBackgroundResource(R.drawable.image_view_selector);

		prepareView(imageView).setOnLongClickListener(
				new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						Toast.show(getContext(), stringRes);
						return true;
					}
				});

		mActionsBar.addView(imageView);
		return imageView;
	}

	private View prepareView(View view) {
		int padding = density(8);
		view.setPadding(0, padding, 0, padding);
		view.setLayoutParams(new LayoutParams(mHeight, mHeight));

		return view;
	}

	private LayoutParams newLayoutParams() {
		LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		return lp;
	}

	private int density(int a) {
		return (int) (a * getResources().getDisplayMetrics().density);
	}
}