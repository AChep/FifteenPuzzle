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

	private static final int MOTION_DATA_ORIENTATION = 0;
	private static final int MOTION_DATA_CHIP = 1;
	private static final int MOTION_DATA_DIRECTION = 2;
	private static final int MOTION_DATA_MIN = 3;
	private static final int MOTION_DATA_MAX = 4;

	private static final int MOTION_DATA_ORIENTATION_VERTICAL = 1;
	private static final int MOTION_DATA_ORIENTATION_HORIZONTAL = 0;
	private static final int MOTION_DATA_DIRECTION_RIGHT = 1;
	private static final int MOTION_DATA_DIRECTION_LEFT = -1;
	private static final int MOTION_DATA_NOTHING = -1;

	private int mLength;
	private int mChipSize;

	private int[] mTrack;
	private int mTrackSpace;
	private int mSpaceIndex;

	private Chip[] mChips;

	private Bitmap[] mChipsBitmaps;
	private Paint mChipsPaint;
	private float mChipsTextPaddingY;
	private int[] mChipsPadding;

	private boolean mTouchable = true;
	private int[] mTouchCoordsReal = new int[2];

	private long mTouchDownTime;
	private int[] mTouchDownCoordsReal = new int[2];
	private boolean mQuickSwipe;

	private int[] mMotionData;

	private int mGameSteps;
	private long mGameStartTime;
	private long mGameOverTime;
	private boolean mGameOver;

	private ActivityInterface mActivityInterface;

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
			invalidate();
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
		mMotionData = new int[MOTION_DATA_MAX + mLength];

		final int[] coordsStartPosition = new int[2];
		for (int i = 0; i < mChips.length; i++) {
			Coords.convertLineToReal(i, mLength, mChipSize, coordsStartPosition);

			mTrack[i] = i;
			mChips[i] = new Chip(coordsStartPosition, i);
		}

		// Space
		mSpaceIndex = mChips.length;
		mTrack[mSpaceIndex] = mSpaceIndex;
		mTrackSpace = mSpaceIndex;

		mGameOver = true;
	}

	public void recycleBitmaps() {
		mChipsBitmaps[0].recycle();
		mChipsBitmaps[1].recycle();
	}

	@Override
	public void onDraw(Canvas canvas) {
		final int halfChipSize = mChipSize / 2;
		for (int i = 0; i < mChips.length; i++)
			mChips[i].draw(canvas, halfChipSize);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!mTouchable)
			return false;
		mTouchCoordsReal[0] = Math.round(event.getX()) - mChipsPadding[0];
		mTouchCoordsReal[1] = Math.round(event.getY()) - mChipsPadding[1];

		final int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			// DOWN DOWN DOWN DOWN
			// DOWN DOWN DOWN DOWN
			// DOWN DOWN DOWN DOWN
			// DOWN DOWN DOWN DOWN

			if (mTouchCoordsReal[0] < 0 || mTouchCoordsReal[1] < 0)
				return false;
			final int[] chip2dPos = new int[2];
			Coords.roundRealTo2d(mTouchCoordsReal, mChipSize, chip2dPos);
			if (chip2dPos[0] >= mLength || chip2dPos[1] >= mLength)
				return false;

			final int[] space2dPos = new int[2];
			final int chipLinePos = Coords.convert2dToLine(chip2dPos, mLength);
			Coords.convertLineTo2d(mTrackSpace, mLength, space2dPos);

			// BEGIN
			int xyconst = chip2dPos[0];
			if (xyconst == space2dPos[0]) {
				if (chip2dPos[1] != space2dPos[1])
					mMotionData[MOTION_DATA_ORIENTATION] = MOTION_DATA_ORIENTATION_VERTICAL;
				else
					return false;
			} else {
				xyconst = chip2dPos[1];
				if (xyconst == space2dPos[1])
					mMotionData[MOTION_DATA_ORIENTATION] = MOTION_DATA_ORIENTATION_HORIZONTAL;
				else
					return false;
			}

			mMotionData[MOTION_DATA_CHIP] = mTrack[chipLinePos];

			final int[] chipsCoordsRange = new int[2];
			if (space2dPos[mMotionData[MOTION_DATA_ORIENTATION]]
					- chip2dPos[mMotionData[MOTION_DATA_ORIENTATION]] > 0) {
				chipsCoordsRange[0] = chip2dPos[mMotionData[MOTION_DATA_ORIENTATION]];
				chipsCoordsRange[1] = chipsCoordsRange[0] + 1;

				mMotionData[MOTION_DATA_DIRECTION] = MOTION_DATA_DIRECTION_RIGHT;
			} else { // We can move it to -1
				chipsCoordsRange[1] = chip2dPos[mMotionData[MOTION_DATA_ORIENTATION]];
				chipsCoordsRange[0] = chipsCoordsRange[1] - 1;

				mMotionData[MOTION_DATA_DIRECTION] = MOTION_DATA_DIRECTION_LEFT;
			}
			Coords.convert2dToReal(chipsCoordsRange, mChipSize,
					chipsCoordsRange);
			mMotionData[MOTION_DATA_MIN] = chipsCoordsRange[0];
			mMotionData[MOTION_DATA_MAX] = chipsCoordsRange[1];

			int length = 0;
			final int[] chipsCoords2d = new int[2];
			final int[] chipsCoords2dMode;
			if (mMotionData[MOTION_DATA_ORIENTATION] == MOTION_DATA_ORIENTATION_VERTICAL)
				chipsCoords2dMode = new int[] { 0, 1 };
			else
				chipsCoords2dMode = new int[] { 1, 0 };
			chipsCoords2d[chipsCoords2dMode[0]] = xyconst;
			for (int i = 0; i < mLength; i++) {
				chipsCoords2d[chipsCoords2dMode[1]] = i;
				final int coordsLine = Coords.convert2dToLine(chipsCoords2d,
						mLength);

				if (mTrack[coordsLine] != mSpaceIndex) {
					length++;
					mMotionData[MOTION_DATA_MAX + length] = mTrack[coordsLine];
				}
			}

			mTouchDownTime = System.currentTimeMillis();
			mTouchDownCoordsReal[0] = mTouchCoordsReal[0];
			mTouchDownCoordsReal[1] = mTouchCoordsReal[1];
			mQuickSwipe = true;

			// Push it to drawing
			event.setAction(MotionEvent.ACTION_MOVE);
			onTouchEvent(event);
		} else if (action == MotionEvent.ACTION_MOVE) {
			// MOVE MOVE MOVE MOVE
			// MOVE MOVE MOVE MOVE
			// MOVE MOVE MOVE MOVE
			// MOVE MOVE MOVE MOVE

			// If nothing is selected or not possible to move it
			if (mMotionData[MOTION_DATA_ORIENTATION] == MOTION_DATA_NOTHING)
				return false; // Just stop it

			if (Math.abs(mTouchDownCoordsReal[0] - mTouchCoordsReal[0]) > 10
					|| Math.abs(mTouchDownCoordsReal[1] - mTouchCoordsReal[1]) > 10)
				mQuickSwipe = false;

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

		} else if (action == MotionEvent.ACTION_UP) {
			// UP UP UP UP UP UP UP
			// UP UP UP UP UP UP UP
			// UP UP UP UP UP UP UP
			// UP UP UP UP UP UP UP

			// If nothing is selected or not possible to move it
			if (mMotionData[MOTION_DATA_ORIENTATION] == MOTION_DATA_NOTHING)
				return false; // Just stop it

			boolean theSame = true;

			// We're should detect space chip by false here
			boolean[] changed = new boolean[mLength];

			// Get const coord id from line type
			final int orientationInversed = mMotionData[MOTION_DATA_ORIENTATION] == 1 ? 0
					: 1;
			int xyconst = 0;

			// Quick click as swipe
			boolean quickSwipe = mQuickSwipe
					&& System.currentTimeMillis() - mTouchDownTime <= 200;

			final int[] chipCoords2 = new int[2];
			final int[] trackStable = mTrack.clone();
			for (int i = 1; i < mLength; i++) {
				// Get chips id from moves data
				int chipId = mMotionData[i + MOTION_DATA_MAX];

				Coords.roundRealTo2d(mChips[chipId].getCoordsLink(), mChipSize,
						chipCoords2);
				int chipCoordsLine = Coords.convert2dToLine(chipCoords2,
						mLength);

				if (trackStable[chipCoordsLine] != chipId)
					theSame = false;
				changed[chipCoords2[mMotionData[MOTION_DATA_ORIENTATION]]] = true;

				mTrack[chipCoordsLine] = chipId; // track it
				xyconst = chipCoords2[orientationInversed]; // set const xy

				Coords.convert2dToReal(chipCoords2, mChipSize, chipCoords2);
				continueScroll(chipId, chipCoords2.clone());
			}
			for (int i = 0; i < mLength; i++) {
				if (changed[i])
					continue;

				if (mMotionData[MOTION_DATA_ORIENTATION] == MOTION_DATA_ORIENTATION_VERTICAL) {
					chipCoords2[0] = xyconst;
					chipCoords2[1] = i;
				} else {
					chipCoords2[0] = i;
					chipCoords2[1] = xyconst;
				}

				mTrackSpace = Coords.convert2dToLine(chipCoords2, mLength);
				mTrack[mTrackSpace] = mSpaceIndex;
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
		}
		invalidate();
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
				postInvalidate();
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

	public long getGameTimeMillis() {
		return mGameOver ? mGameOverTime : SystemClock.uptimeMillis()
				- mGameStartTime;
	}

	public boolean getGameStatus() {
		return mGameOver;
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

		final int[] switchChip2d = new int[2];
		for (int i = 0; i < shuffling; i++) {
			// Get random coords
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
			final int spaceLine = Coords.convert2dToLine(space2d, length);
			final int switchChip = Coords.convert2dToLine(switchChip2d, length);

			// Space chip now knows as selected one
			space2d[0] = switchChip2d[0];
			space2d[1] = switchChip2d[1];

			// Swap tracking array
			int k = mTrack[spaceLine];
			mTrack[spaceLine] = mTrack[switchChip];
			mTrack[switchChip] = k;
		}

		// Restore chip's positions by track data
		final int[] chipsCoordsReal = new int[2];
		for (int i = 0; i < trackLength; i++) {
			// Filter space chip
			if (mTrack[i] == mSpaceIndex) {
				mTrackSpace = i;
				continue;
			}
			// Set chip's coords
			Coords.convertLineToReal(i, length, mChipSize, chipsCoordsReal);
			mChips[mTrack[i]].setCoords(chipsCoordsReal);
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
			int halfChipSize = mChipSize / 2;
			solved = Math.abs(coords[0] - perfectCoords[0]) <= halfChipSize
					&& Math.abs(coords[1] - perfectCoords[1]) <= halfChipSize;
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