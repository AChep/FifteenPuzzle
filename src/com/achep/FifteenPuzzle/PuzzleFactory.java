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

package com.achep.FifteenPuzzle;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.RadialGradient;
import android.graphics.Shader.TileMode;

public class PuzzleFactory {

	public static Bitmap createChipBaseBitmap(int size) {
		Bitmap bitmap = Bitmap
				.createBitmap(size, size, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		final int halfSize = size / 2;
		final int radius = size / 5;
		final int color1 = 0xFFeeeeee;
		final int color2 = 0xFFbbbbbb;

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setDither(true);

		LinearGradient linearGradient = new LinearGradient(halfSize / 1.5f, 0,
				halfSize * 1.8f, size, color1, color2, TileMode.CLAMP);
		paint.setShader(linearGradient);

		canvas.drawCircle(radius, radius, radius, paint);
		canvas.drawCircle(size - radius, radius, radius, paint);
		canvas.drawCircle(radius, size - radius, radius, paint);
		canvas.drawCircle(size - radius, size - radius, radius, paint);
		canvas.drawRect(0, radius, size, size - radius, paint);
		canvas.drawRect(radius, 0, size - radius, radius, paint);
		canvas.drawRect(radius, size, size - radius, size - radius, paint);

		RadialGradient radialGradient = new RadialGradient(halfSize * 1.5f,
				halfSize * 1.5f, size, color1, color2, TileMode.CLAMP);
		paint.setShader(radialGradient);

		canvas.drawCircle(halfSize, halfSize, halfSize / 1.2f, paint);
		return bitmap;
	}

	// TODO: Delete it
	// TEST COMMIT FROM LINUX =)

	public static Bitmap createChipBitmap(int number, Bitmap bitmapBase) {
		Bitmap bitmap = Bitmap.createBitmap(bitmapBase);
		Canvas canvas = new Canvas(bitmap);
		final int size = bitmap.getHeight();
		final int halfSize = size / 2;

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Align.CENTER);
		paint.setTextSize(halfSize);
		paint.setColor(0xFFE0E0E0);
		canvas.drawText(number + "", halfSize + 1,
				halfSize + paint.getTextSize() / 2.8f + 1, paint);
		paint.setColor(0xFF202020);
		canvas.drawText(number + "", halfSize, halfSize + paint.getTextSize()
				/ 2.8f, paint);
		return bitmap;
	}
}
