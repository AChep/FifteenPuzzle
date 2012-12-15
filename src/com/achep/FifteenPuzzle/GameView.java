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

import com.achep.FifteenPuzzle.preferences.PrefPuzzleLengthPicker;
import com.achep.FifteenPuzzle.preferences.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View

{

	private int mPaddingX;
	private int mPaddingY;

	private GameLogic mGameLogic;
	private int[] mMovesData;
	private boolean mTouchAvailable = true;
	private boolean mNeedUpdate = false;

	public GameView(Context context) {
		super(context);
	}

	public GameView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public GameView(Context context, AttributeSet attrs, int styles) {
		super(context, attrs, styles);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		if (mGameLogic != null) {
			for (int i = 0; i < mGameLogic.getChipsLength(); i++)
				mGameLogic.getChip(i).getBitmap().recycle();
			mGameLogic = null;
		}

		final SharedPreferences prefs = getContext().getSharedPreferences(
				"preferences2", 0);

		final int length = prefs.getInt(Settings.Keys.SPREF_PUZZLE_LENGTH,
				PrefPuzzleLengthPicker.DEFAULT);
		final int matrixSize = (w > h ? h : w) - length + 1;
		final int chipSize = matrixSize / length;

		mPaddingX = (w - matrixSize) / 2;
		mPaddingY = (h - matrixSize) / 2;

		mGameLogic = new GameLogic(chipSize, length);
		if (mOnInitializedListener != null) {
			mOnInitializedListener.OnInitialized(mGameLogic);
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		for (int i = 0; i < mGameLogic.getChipsLength(); i++)
			mGameLogic.getChip(i).draw(canvas, mGameLogic.getChipSize() / 2,
					mPaddingX, mPaddingY);
	}

	public boolean needsUpdate() {
		mNeedUpdate = !mNeedUpdate;
		return !mNeedUpdate;
	}

	private OnInitializedListener mOnInitializedListener;

	public void setOnInitializedListener(OnInitializedListener l) {
		mOnInitializedListener = l;
	}

	public interface OnInitializedListener {
		public void OnInitialized(GameLogic gameLogic);
	}

	public void setTouchAvailabel(boolean availabel) {
		mTouchAvailable = availabel;
	}

	public GameLogic getGameLogic() {
		mNeedUpdate = true;
		return mGameLogic;
	}

	public long getTime() {
		return SystemClock.currentThreadTimeMillis();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mTouchAvailable)
			return false;
		mNeedUpdate = true;
		final int[] touchCoords = new int[] {
				Math.round(event.getX() - mPaddingX),
				Math.round(event.getY() - mPaddingY) };
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			final int[] coordsRealRounded = Coords.roundReal(touchCoords,
					mGameLogic.getLength(), mGameLogic.getChipSize());
			if (coordsRealRounded == null)
				// No one of the chips are clicked
				return false;
			final int coordsLine = Coords.convertRealToLine(coordsRealRounded,
					mGameLogic.getLength(), mGameLogic.getChipSize())[0];
			mMovesData = mGameLogic.getAvailableMoves(coordsLine);

			// Push it to drawing
			event.setAction(MotionEvent.ACTION_MOVE);
			onTouchEvent(event);
			break;
		case MotionEvent.ACTION_MOVE: // On moving
			// If nothing is selected or not possible to move it
			if (mMovesData == null)
				return false; // Just stop it

			// Fix swapping puzzle on slowly phones
			int xy = Utils.alignToRange(
					touchCoords[mMovesData[GameLogic.ORIENTATION]],
					mMovesData[GameLogic.MIN], mMovesData[GameLogic.MAX]);

			// Calculate delta of two points
			int delta = xy
					- mGameLogic.getChip(mMovesData[GameLogic.CHIP])
							.getCoords()[mMovesData[GameLogic.ORIENTATION]];
			int chipSize = mGameLogic.getChipSize();
			int lagFixer = delta >= chipSize ? -2 : delta <= -chipSize ? 2 : 0; // LAAAAGSS

			// Set coords
			mGameLogic.getChip(mMovesData[GameLogic.CHIP]).setCoords(
					xy + lagFixer, mMovesData[GameLogic.ORIENTATION]);
			fixPuzzleOverlay(mMovesData[GameLogic.CHIP],
					mMovesData[GameLogic.ORIENTATION]); // Fix mixed puzzles
			break;
		case MotionEvent.ACTION_UP: // On up
			// If nothing is selected or not possible to move it
			if (mMovesData == null)
				return false; // Just stop it

			mGameLogic.completeMoves(mMovesData);
			break;
		}
		return true;
	}

	private void fixPuzzleOverlay(int item, int mode) {
		// Get chips size + divider size
		int size = mGameLogic.getChipSize() + 1;
		// Moving on the selected line
		for (int i = GameLogic.MAX + 1; i < mMovesData.length; i++) {
			int item2 = mMovesData[i]; // Get chip's id number
			if (item2 == item)
				continue; // Afraid of looooooooping
			int[] coords = mGameLogic.getChip(item).getCoords(); // Coords of
																	// selected
																	// chip
			int[] coords2 = mGameLogic.getChip(item2).getCoords();
			// Calculate the delta of two chips coords
			int delta = coords[mode] - coords2[mode];
			if (Math.abs(delta) < size) {
				// Time to fix it!
				delta += delta < 0 ? size : -size;
				// Fix coords
				mGameLogic.getChip(item2)
						.setCoords(coords2[mode] + delta, mode);
				fixPuzzleOverlay(item2, mode); // Maybe fixed chip should be
												// fixed again?
				// Stop it!
				break;
			}
		}
	}

}