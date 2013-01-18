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

package com.achep.FifteenPuzzle.stats;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class GraphsView extends View {

	private ArrayList<Integer[]> mPointsList;
	private ArrayList<Integer> mColorsList;
	private int mMaxValue;

	private Paint mPaint;

	public GraphsView(Context context) {
		this(context, null);
	}

	public GraphsView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GraphsView(Context context, AttributeSet attrs, int styles) {
		super(context, attrs, styles);
		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		mPointsList = new ArrayList<Integer[]>();
		mColorsList = new ArrayList<Integer>();
	}

	public void addPoints(int[] points, int color) {
		if (points == null || points.length == 0)
			return;

		Integer[] points2 = new Integer[points.length];
		for (int i = 0; i < points.length; i++) {
			points2[i] = points[i];
			if (points[i] > mMaxValue)
				mMaxValue = points[i];
		}

		mPointsList.add(points2);
		mColorsList.add(color);
	}

	@Override
	public void onDraw(Canvas canvas) {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		if (width == 0 || height == 0)
			return;

		// Build background
		mPaint.setStrokeWidth(1f);
		mPaint.setColor(0x90909090);
		float step = (float) height / mMaxValue * 10;
		for (int i = 0; i <= width; i += step)
			canvas.drawLine(i, 0, i, height, mPaint);		
		for (int i = height; i >= 0; i -= step)
			canvas.drawLine(0, i, width, i, mPaint);

		// Build graph
		mPaint.setStrokeWidth(4f);
		int length = mPointsList.size();
		for (int i = 0; i < length; i++) {
			mPaint.setColor(mColorsList.get(i));

			final Integer[] values = mPointsList.get(i);
			for (int j = 1; j < values.length; j++) {
				final float startX = (float) (j - 1) / (values.length - 1)
						* width;
				final float stopX = (float) j / (values.length - 1) * width;

				final float startY = (float) (mMaxValue - values[j - 1])
						/ mMaxValue * height;
				final float stopY = (float) (mMaxValue - values[j]) / mMaxValue
						* height;

				canvas.drawLine(startX, startY, stopX, stopY, mPaint);
			}
		}
	}
}