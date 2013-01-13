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

import java.util.Random;

import com.achep.FifteenPuzzle.preferences.PrefPuzzleLengthPicker;
import com.achep.FifteenPuzzle.preferences.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View implements
		SharedPreferences.OnSharedPreferenceChangeListener {

	private int mLength;
	private int mChipSize;

	private int[] mTrack;
	private int mSpaceIndex;

	private Chip[] mChips;

	private Bitmap[] mChipsBitmaps;
	private Paint mChipsPaint;
	private float mChipsTextPaddingY;
	private int[] mChipsPadding;

	private boolean mTouchable = true;
	private int[] mTouchCoordsReal = new int[2];

	private int[] mMotionData;

	private int mGameSteps;
	private long mGameStartTime;
	private long mGameOverTime;
	private boolean mGameOver = true;

	private ActivityInterface mActivityInterface;
	private boolean mIsChanged;

	public interface ActivityInterface {
		public void onGameOver(int steps, long timeMillis, int length);
	}

	public void setActivityInterface(ActivityInterface activityInterface) {
		mActivityInterface = activityInterface;
	}

	public GameView(Context context) {
		this(context, null);
	}

	public GameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameView(Context context, AttributeSet attrs, int styles) {
		super(context, attrs, styles);
		context.getSharedPreferences(Settings.SHARED_PREFERENCES_FILE, 0)
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSizeChanged(int w, int h, int oldw, int oldh) {
		updateParams(w, h);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		if (key.equals(Settings.Keys.SPREF_PUZZLE_LENGTH)) {
			updateParams(getWidth(), getHeight());
		}
	}

	private void updateParams(int width, int height) {
		// Init shared preferences var
		SharedPreferences sp = getContext().getSharedPreferences(
				Settings.SHARED_PREFERENCES_FILE, 0);

		// Get main params
		int length = sp.getInt(Settings.Keys.SPREF_PUZZLE_LENGTH,
				PrefPuzzleLengthPicker.DEFAULT);
		int matrixSize = (width > height ? height : width) - length + 1;
		int chipSize = matrixSize / length;

		// Setup them
		mLength = length;
		mChipSize = chipSize;

		// Create chip's bitmaps: solved & normal
		if (mChipsBitmaps != null)
			recycleBitmaps();
		mChipsBitmaps = PuzzleFactory.createChipBitmaps(chipSize);

		if (mChipsPaint == null) {
			mChipsPaint = new Paint();
			mChipsPaint.setAntiAlias(true);
			mChipsPaint.setTextAlign(Align.CENTER);
			mChipsPaint.setColor(0xFF202020);
		}
		mChipsPaint.setTextSize(chipSize / 2);
		mChipsTextPaddingY = mChipsPaint.getTextSize() / 2.8f;
		mChipsPadding = new int[] { (width - matrixSize) / 2,
				(height - matrixSize) / 2 };

		mTrack = new int[mLength * mLength];
		mChips = new Chip[mTrack.length - 1];

		int[] coordsLine = new int[1];
		for (int i = 0; i < mChips.length; i++) {
			mTrack[i] = i;
			coordsLine[0] = i;

			final int[] coordsPerfect = Coords.convertLineToReal(coordsLine,
					mLength, mChipSize);

			mChips[i] = new Chip(coordsPerfect, i);
		}

		// Space
		mSpaceIndex = mChips.length;
		mTrack[mSpaceIndex] = mSpaceIndex;
	}

	public void recycleBitmaps() {
		for (int i = 0; i < mChipsBitmaps.length; i++) {
			mChipsBitmaps[i].recycle();
		}
	}

	@Override
	public void onDraw(Canvas canvas) {
		int halfChipSize = mChipSize / 2;
		for (int i = 0; i < mChips.length; i++) {
			mChips[i].draw(canvas, halfChipSize);
		}
		mIsChanged = false;
	}

	private static final int MOTION_DATA_ORIENTATION = 0;
	private static final int MOTION_DATA_CHIP = 1;
	private static final int MOTION_DATA_MIN = 2;
	private static final int MOTION_DATA_MAX = 3;

	private static final int MOTION_DATA_ORIENTATION_VERTICAL = 1;
	private static final int MOTION_DATA_ORIENTATION_HORIZONTAL = 0;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mTouchable)
			return false;
		mTouchCoordsReal[0] = Math.round(event.getX() - mChipsPadding[0]);
		mTouchCoordsReal[1] = Math.round(event.getY() - mChipsPadding[1]);
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			final int[] coordsRealRounded = Coords.roundRealTo2d(
					mTouchCoordsReal, mLength, mChipSize);
			if (coordsRealRounded == null)
				// No one of the chips are clicked
				return false;

			// Moves data
			int[] data = new int[MOTION_DATA_MAX + mLength];

			int chipLinePos = Coords
					.convert2dToLine(coordsRealRounded, mLength)[0];
			int[] space2dPos = Coords.convertLineTo2d(
					Utils.findElement(mTrack, new int[] { mSpaceIndex }),
					mLength);
			int[] chip2dPos = Coords.convertLineTo2d(new int[] { chipLinePos },
					mLength);

			// BEGIN
			int xyconst = chip2dPos[0];
			if (xyconst == space2dPos[0]) {
				if (chip2dPos[1] != space2dPos[1])
					data[MOTION_DATA_ORIENTATION] = MOTION_DATA_ORIENTATION_VERTICAL;
				else
					return false;
			} else {
				xyconst = chip2dPos[1];
				if (xyconst == space2dPos[1])
					data[MOTION_DATA_ORIENTATION] = MOTION_DATA_ORIENTATION_HORIZONTAL;
				else
					return false;

			}

			data[MOTION_DATA_CHIP] = mTrack[chipLinePos];

			int[] dataRange = new int[2];
			if (space2dPos[data[MOTION_DATA_ORIENTATION]]
					- chip2dPos[data[MOTION_DATA_ORIENTATION]] > 0) {
				dataRange[0] = chip2dPos[data[MOTION_DATA_ORIENTATION]];
				dataRange[1] = dataRange[0] + 1;
			} else { // We can move it to -1
				dataRange[1] = chip2dPos[data[MOTION_DATA_ORIENTATION]];
				dataRange[0] = dataRange[1] - 1;
			}
			int[] dataRangeReal = Coords.convert2dToReal(dataRange, mChipSize);
			data[MOTION_DATA_MIN] = dataRangeReal[0];
			data[MOTION_DATA_MAX] = dataRangeReal[1];

			int length = 0;
			for (int i = 0; i < mLength; i++) {
				int linePos;
				if (data[MOTION_DATA_ORIENTATION] == MOTION_DATA_ORIENTATION_VERTICAL) {
					linePos = Coords.convert2dToLine(new int[] { xyconst, i },
							mLength)[0];
				} else {
					linePos = Coords.convert2dToLine(new int[] { i, xyconst },
							mLength)[0];
				}

				if (mTrack[linePos] != mSpaceIndex) {
					length++;
					data[MOTION_DATA_MAX + length] = mTrack[linePos];
				}
			}
			mMotionData = data;

			// Push it to drawing
			event.setAction(MotionEvent.ACTION_MOVE);
			onTouchEvent(event);
			break;
		case MotionEvent.ACTION_MOVE: // On moving
			// If nothing is selected or not possible to move it
			if (mMotionData == null)
				return false; // Just stop it

			// Fix of the swapping puzzle on slowly phones
			int xy = Utils.alignToRange(
					mTouchCoordsReal[mMotionData[MOTION_DATA_ORIENTATION]],
					mMotionData[MOTION_DATA_MIN], mMotionData[MOTION_DATA_MAX]);

			// Calculate delta of two points
			int delta = xy
					- mChips[mMotionData[MOTION_DATA_CHIP]].getCoordsLink()[mMotionData[MOTION_DATA_ORIENTATION]];
			int lagFixer = delta >= mChipSize ? -2 : delta <= -mChipSize ? 2
					: 0; // LAAAAGSS

			// Set coords
			mChips[mMotionData[MOTION_DATA_CHIP]].setCoords(xy + lagFixer,
					mMotionData[MOTION_DATA_ORIENTATION]);
			fixChipsOverlay(mMotionData[MOTION_DATA_CHIP],
					mMotionData[MOTION_DATA_ORIENTATION]); // Fix mixed puzzles
			break;
		case MotionEvent.ACTION_UP: // On up
			// If nothing is selected or not possible to move it
			if (mMotionData == null)
				return false; // Just stop it

			boolean theSame = true;

			// We're should detect space chip by false here
			boolean[] changed = new boolean[mLength];
			// Get const coord id from line type
			int orientationInversed = mMotionData[MOTION_DATA_ORIENTATION] == 1 ? 0
					: 1;
			// Const coordinate of line
			xyconst = -1;
			// Space isn't just chip
			for (int i = 1; i < mLength; i++) {
				// Get chips id from moves data
				int chipId = mMotionData[i + MOTION_DATA_MAX];
				// Get chip real coords
				final int[] coords2d = Coords.roundRealTo2d(
						mChips[chipId].getCoordsLink(), mLength, mChipSize);
				final int[] coordsRealRounded2 = Coords.convert2dToReal(
						coords2d, mChipSize);
				final int coordsLine = Coords
						.convert2dToLine(coords2d, mLength)[0];

				changed[coords2d[mMotionData[MOTION_DATA_ORIENTATION]]] = true; // not
																				// space
				if (mTrack[coordsLine] != chipId)
					theSame = false;
				mTrack[coordsLine] = chipId; // track it
				continueScroll(chipId, coordsRealRounded2);
				// getChip(chipId).setCoords(coordsRealRounded); // set coords
				xyconst = coords2d[orientationInversed]; // set const xy
			}
			for (int i = 0; i < mLength; i++) {
				// Stop if it's not space chip
				if (changed[i])
					continue;
				// It's space just here!

				int[] coords2d;
				if (mMotionData[MOTION_DATA_ORIENTATION] == MOTION_DATA_ORIENTATION_VERTICAL) {
					coords2d = new int[] { xyconst, i };
				} else {
					coords2d = new int[] { i, xyconst };
				}
				final int coordsLine = Coords
						.convert2dToLine(coords2d, mLength)[0];
				mTrack[coordsLine] = mSpaceIndex;
				break;
			}
			if (!theSame && !mGameOver) {
				mGameSteps++;
				if (isGameOver()) {
					mGameOverTime = getGameTimeMillis();
					mGameOver = true;
					if (mActivityInterface != null) {
						mActivityInterface.onGameOver(mGameSteps,
								mGameOverTime, mLength);
					}
				}
			}
			break;
		}
		return true;
	}

	private void continueScroll(final int id, final int[] coordsEnd) {
		final int[] coordsStart = mChips[id].getCoordsLink().clone();
		final int[] deltaCoords = new int[] { coordsEnd[0] - coordsStart[0],
				coordsEnd[1] - coordsStart[1] };

		float duration0 = 100;
		int mode0;
		if (deltaCoords[0] != 0 && deltaCoords[1] == 0) {
			// HORIZONTAL LINE
			mode0 = 0;
		} else if (deltaCoords[0] == 0 && deltaCoords[1] != 0) {
			// VERTICAL LINE
			mode0 = 1;
		} else
			return;
		duration0 *= (float) Math.abs(deltaCoords[mode0]) * 2 / mChipSize;

		final int duration = Math.round(duration0);
		final int mode = mode0;
		final long endTime = SystemClock.uptimeMillis() + duration;

		final Handler handler = new Handler();
		Runnable runnable = new Runnable() {

			private int mOldCoord = coordsStart[mode];

			@Override
			public void run() {
				if (mChips[id].getCoordsLink()[mode] != mOldCoord) {
					return; // Stop animation if you're pressed this chip
				}

				long deltaTime = endTime - SystemClock.uptimeMillis();
				if (deltaTime > 0) {
					float progress = (float) (duration - deltaTime) / duration;
					mOldCoord = coordsStart[mode]
							+ Math.round(deltaCoords[mode] * progress);
					mChips[id].setCoords(mOldCoord, mode);

					handler.postDelayed(this, 10);
				} else
					mChips[id].setCoords(coordsEnd);
			}
		};
		handler.post(runnable);
	}

	private void fixChipsOverlay(int item, int mode) {
		// Get chips size + divider size
		int size = mChipSize + 1;
		// Moving on the selected line
		for (int i = MOTION_DATA_MAX + 1; i < mMotionData.length; i++) {
			int item2 = mMotionData[i]; // Get chip's id number
			if (item2 == item)
				continue; // Afraid of looooooooping
			int[] coords = mChips[item].getCoordsLink();
			int[] coords2 = mChips[item2].getCoordsLink();

			// Calculate the delta of two chips coords
			int delta = coords[mode] - coords2[mode];
			if (Math.abs(delta) < size) {
				// Time to fix it!
				delta += delta < 0 ? size : -size;
				// Fix coords
				mChips[item2].setCoords(coords2[mode] + delta, mode);
				fixChipsOverlay(item2, mode); // Maybe fixed chip should be
												// fixed again?
				// Stop it!
				break;
			}
		}
	}

	public boolean isChanged() {
		return mIsChanged;
	}

	public long getGameTimeMillis() {
		return mGameOver ? mGameOverTime : SystemClock.uptimeMillis()
				- mGameStartTime;
	}

	public void newGame() {
		// Lock user's actions to protect bugs
		mTouchable = false;

		mGameOver = false;
		mGameSteps = 0;
		shuffleChips();
		mGameStartTime = SystemClock.uptimeMillis();

		// Unlock user's actions
		mTouchable = true;
	}

	private void shuffleChips() {
		final Random random = new Random();
		final int trackLength = mTrack.length;
		final int length = mLength;

		// Inverse track!
		for (int i = 0; i < trackLength; i++) {
			mTrack[i] = trackLength - 1 - i;
		}

		// Space coordinates
		int[] space2d = new int[] { 0, 0 };

		int reback = -1;
		int shuffling = trackLength * trackLength;
		for (int i = 0; i < shuffling; i++) {
			// Get random coords
			final int[] switchChip2d = new int[2];
			switch (random.nextInt(4)) {
			case 0: // TO RIGHT
				if (space2d[0] != length - 1 && reback != 0) {
					switchChip2d[0] = space2d[0] + 1;
					switchChip2d[1] = space2d[1];

					reback = 2;
					break;
				}
			case 1: // TO TOP
				if (space2d[1] != 0 && reback != 1) {
					switchChip2d[0] = space2d[0];
					switchChip2d[1] = space2d[1] - 1;

					reback = 3;
					break;
				}
			case 2: // TO LEFT
				if (space2d[0] != 0 && reback != 2) {
					switchChip2d[0] = space2d[0] - 1;
					switchChip2d[1] = space2d[1];

					reback = 0;
					break;
				}
			case 3: // TO BOTTOM
				if (space2d[1] != length - 1 && reback != 3) {
					switchChip2d[0] = space2d[0];
					switchChip2d[1] = space2d[1] + 1;

					reback = 1;
					break;
				}
			default:
				continue;
			}
			final int space = Coords.convert2dToLine(space2d, length)[0];
			final int switchChip = Coords.convert2dToLine(switchChip2d, length)[0];

			// Space chip now knows as selected one
			space2d[0] = switchChip2d[0];
			space2d[1] = switchChip2d[1];

			// Swap tracking array
			int k = mTrack[space];
			mTrack[space] = mTrack[switchChip];
			mTrack[switchChip] = k;
		}

		// Restore chip's positions by track data
		int[] coordsLine = new int[1];
		for (int i = 0; i < trackLength; i++) {
			// Filter space chip
			if (mTrack[i] == mSpaceIndex)
				continue;
			// Set chip's coords
			coordsLine[0] = i;
			mChips[mTrack[i]].setCoords(Coords.convertLineToReal(coordsLine,
					length, mChipSize));
		}
	}

	private boolean isGameOver() {
		for (int i = 0; i < mTrack.length; i++)
			if (mTrack[i] != i)
				return false;
		return true;
	}

	private class Chip {

		private int[] coords;
		private final String text;
		private final int[] perfectCoords;

		private boolean solved;

		public Chip(int[] coords, int index) {
			this.coords = coords.clone();
			this.perfectCoords = coords.clone();
			this.text = index + 1 + "";

			onSetCoords();
		}

		private void onSetCoords() {
			mIsChanged = true;
			int halfChipSize = mChipSize / 2;
			solved = Math.abs(coords[0] - perfectCoords[0]) < halfChipSize
					&& Math.abs(coords[1] - perfectCoords[1]) < halfChipSize;
		}

		public void draw(Canvas canvas, int halfChipSize) {
			final float x = mChipsPadding[0] + coords[0];
			final float y = mChipsPadding[1] + coords[1];

			canvas.drawBitmap(solved ? mChipsBitmaps[1] : mChipsBitmaps[0], x
					- halfChipSize, y - halfChipSize, mChipsPaint);
			canvas.drawText(text, x, y + mChipsTextPaddingY, mChipsPaint);
		}

		public void setCoords(int coords, int mode) {
			this.coords[mode] = coords;

			onSetCoords();
		}

		public void setCoords(int[] coords) {
			this.coords[0] = coords[0];
			this.coords[1] = coords[1];

			onSetCoords();
		}

		public int[] getCoordsLink() {
			return coords;
		}
	}
}