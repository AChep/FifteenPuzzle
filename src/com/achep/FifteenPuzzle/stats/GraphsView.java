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

package com.achep.FifteenPuzzle.stats;

import java.util.ArrayList;

import com.achep.FifteenPuzzle.Project;
import com.achep.FifteenPuzzle.Utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GraphsView extends View {

	private ArrayList<Integer[]> mPointsList;
	private ArrayList<Integer> mColorsList;
	private ArrayList<String> mLabelsList;
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
		mPaint.setTextSize(16 * getResources().getDisplayMetrics().density);

		mPointsList = new ArrayList<Integer[]>();
		mColorsList = new ArrayList<Integer>();
		mLabelsList = new ArrayList<String>();
	}

	public void addPoints(int[] points, int color, String label) {
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
		mLabelsList.add(label);
	}

	private boolean mPointsDowngrade;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int points = event.getPointerCount();
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_POINTER_DOWN:
		case MotionEvent.ACTION_DOWN:
			writeCenterPointAndRadius(event);
			scrolling(true);
			if (points > 1)
				zooming(true);
			break;
		case MotionEvent.ACTION_MOVE:
			writeCenterPointAndRadius(event);
			scrolling(mPointsDowngrade);
			if (points > 1)
				zooming(mPointsDowngrade);
			mPointsDowngrade = false;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			mPointsDowngrade = true;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			break;
		}

		invalidate();
		return true;
	}

	// Scrolling
	private float mScrollStartX;
	private float mScrollStartY;
	private float mScrollX;
	private float mScrollY;

	private void scrolling(boolean touchDown) {
		final float x = mCenterPoint[0], y = mCenterPoint[1];
		if (!touchDown) {
			mScrollX += x - mScrollStartX;
			mScrollY += y - mScrollStartY;
		}
		mScrollStartX = x;
		mScrollStartY = y;
	}

	// Zooming
	private float mZoomStart;
	private float mZoom = 1f;

	private void zooming(boolean touchDown) {
		final float radius = mZoomRadius;
		if (!touchDown) {
			mZoom = radius / mZoomStart;
		} else {
			mZoomStart = radius / mZoom;
		}
	}

	private float[] mCenterPoint = new float[2];
	private float mZoomRadius;

	private void writeCenterPointAndRadius(MotionEvent event) {
		float x = 0f, y = 0f;
		int points = event.getPointerCount();
		for (int i = 0; i < points; i++) {
			x += event.getX(i);
			y += event.getY(i);
		}
		mCenterPoint[0] = x / points;
		mCenterPoint[1] = y / points;

		double radius = 1f;
		for (int i = 0; i < points; i++) {
			double length = Utils.pifagor(event.getX(i), event.getY(i),
					mCenterPoint[0], mCenterPoint[1]);
			if (length > radius)
				radius = length;
		}
		mZoomRadius = (float) radius;
	}

	@Override
	public void onDraw(Canvas canvas) {
		int width = getMeasuredWidth();
		int height = getMeasuredHeight();
		if (width == 0 || height == 0 || mMaxValue == 0)
			return;

		// Build background
		mPaint.setStrokeWidth(1f);
		mPaint.setColor(0x90707070);
		float step = (float) height / mMaxValue * 10;
		for (int i = 0; i <= width; i += step)
			canvasDrawLine(canvas, i, 0, i, height, mPaint);
		canvasDrawLine(canvas, width, 0, width, height, mPaint);
		for (int i = height; i >= 0; i -= step)
			canvasDrawLine(canvas, 0, i, width, i, mPaint);
		canvasDrawLine(canvas, 0, 0, width, 0, mPaint);

		// Build graph
		mPaint.setStrokeWidth(4f);
		int length = mPointsList.size();
		for (int i = 0; i < length; i++) {
			mPaint.setColor(mColorsList.get(i));

			final Integer[] values = mPointsList.get(i);
			for (int j = 1; j < values.length; j++) {
				final float startY = (float) (mMaxValue - values[j - 1])
						/ mMaxValue * height;
				final float stopY = (float) (mMaxValue - values[j]) / mMaxValue
						* height;

				canvasDrawLine(canvas, getPointX(j - 1, values.length, width),
						startY, getPointX(j, values.length, width), stopY,
						mPaint);
			}

			float textSize = mPaint.getTextSize();
			float y = height - (textSize * 1.2f) * (i + 1);
			canvas.drawCircle(10, y - textSize / 2 + 4, textSize / 8, mPaint);
			canvas.drawText(mLabelsList.get(i), 20, y, mPaint);
		}

		if (Project.DEBUG) {
			mPaint.setColor(0x90707070);
			canvas.drawCircle(mCenterPoint[0], mCenterPoint[1], mZoomRadius,
					mPaint);
			mPaint.setColor(0xffffff70);
			canvas.drawText("" + mZoom, mCenterPoint[0] + 30,
					mCenterPoint[1] + 30, mPaint);
		}
	}

	private float getPointX(int i, int length, int width) {
		return (float) i / (length - 1) * width;
	}

	private void canvasDrawLine(Canvas canvas, float x, float y, float x2,
			float y2, Paint paint) {
		float xpadding = mScrollX;
		float ypadding = mScrollY;

		final float cx = mCenterPoint[0], cy = mCenterPoint[1];

		canvas.drawLine(zoomPoint(x, cx) + xpadding, zoomPoint(y, cy)
				+ ypadding, zoomPoint(x2, cx) + xpadding, zoomPoint(y2, cy)
				+ ypadding, paint);
	}

	private float zoomPoint(float a, float b) {
		return a * mZoom;
	}
}