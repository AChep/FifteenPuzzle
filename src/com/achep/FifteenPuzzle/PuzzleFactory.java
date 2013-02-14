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

package com.achep.FifteenPuzzle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;

public class PuzzleFactory {

	private static final int[] COLOR_POINTS = new int[] { 0xFFeeeeee,
			0xFFbbbbbb };
	private static final int[] COLOR_SELECTED_POINTS = new int[] { 0xFF00de6d,
			0xFF00ab5c };

	public static Bitmap[] createChipBitmaps(int size) {
		final int halfSize = size / 2;
		final int radius = size / 5;

		Bitmap[] bitmaps = new Bitmap[2];
		bitmaps[0] = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);
		bitmaps[1] = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_4444);

		Canvas[] canvass = new Canvas[2];
		canvass[0] = new Canvas(bitmaps[0]);
		canvass[1] = new Canvas(bitmaps[1]);

		final float lgX0 = halfSize / 1.5f;
		final float lgY0 = 0;
		final float lgX1 = halfSize * 1.8f;
		final float lgY1 = size;

		LinearGradient[] linearGradients = new LinearGradient[2];
		linearGradients[0] = new LinearGradient(lgX0, lgY0, lgX1, lgY1,
				COLOR_POINTS[0], COLOR_POINTS[1], TileMode.CLAMP);
		linearGradients[1] = new LinearGradient(lgX0, lgY0, lgX1, lgY1,
				COLOR_SELECTED_POINTS[0], COLOR_SELECTED_POINTS[1],
				TileMode.CLAMP);

		final float rgX = halfSize * 1.5f;
		final float rgY = rgX;
		final float rgRadius = size;

		RadialGradient[] radialGradients = new RadialGradient[2];
		radialGradients[0] = new RadialGradient(rgX, rgY, rgRadius,
				COLOR_POINTS[0], COLOR_POINTS[1], TileMode.CLAMP);
		radialGradients[1] = new RadialGradient(rgX, rgY, rgRadius,
				COLOR_SELECTED_POINTS[0], COLOR_SELECTED_POINTS[1],
				TileMode.CLAMP);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);

		final float circleRadius = halfSize / 1.2f;

		paint.setShader(linearGradients[0]);
		drawSquare(canvass[0], paint, radius, size);
		paint.setShader(radialGradients[0]);
		drawCircle(canvass[0], paint, circleRadius, halfSize);

		paint.setShader(linearGradients[1]);
		drawSquare(canvass[1], paint, radius, size);
		paint.setShader(radialGradients[1]);
		drawCircle(canvass[1], paint, circleRadius, halfSize);
		return bitmaps;
	}

	private static void drawSquare(Canvas canvas, Paint paint, int radius,
			int size) {
		canvas.drawCircle(radius, radius, radius, paint);
		canvas.drawCircle(size - radius, radius, radius, paint);
		canvas.drawCircle(radius, size - radius, radius, paint);
		canvas.drawCircle(size - radius, size - radius, radius, paint);
		canvas.drawRect(0, radius, size, size - radius, paint);
		canvas.drawRect(radius, 0, size - radius, radius, paint);
		canvas.drawRect(radius, size, size - radius, size - radius, paint);
	}

	private static void drawCircle(Canvas canvas, Paint paint, float radius,
			int xy) {
		canvas.drawCircle(xy, xy, radius, paint);
	}

}